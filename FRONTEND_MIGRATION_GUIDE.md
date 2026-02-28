# Guia de Migração Frontend: Multi-Tenant → Single Buffet

## Resumo
O backend foi convertido de multi-tenant para single-buffet. O buffet ID agora é configurado
no backend via `application.properties` (`ebuffet.buffet-id`). O frontend **NÃO precisa mais
enviar o header `X-Buffet-Id`** em nenhuma request.

---

## 1. REMOVER HEADER `X-Buffet-Id` DE TODAS AS REQUESTS

### O que mudar:
Remover o header `X-Buffet-Id` de **TODOS** os interceptors/services HTTP.

### Exemplo (Angular HttpInterceptor):
```typescript
// ANTES:
intercept(req: HttpRequest<any>, next: HttpHandler) {
  const buffetId = this.authService.getBuffetId();
  const cloned = req.clone({
    setHeaders: {
      'Authorization': `Bearer ${token}`,
      'X-Buffet-Id': buffetId.toString()  // ❌ REMOVER ESTA LINHA
    }
  });
  return next.handle(cloned);
}

// DEPOIS:
intercept(req: HttpRequest<any>, next: HttpHandler) {
  const cloned = req.clone({
    setHeaders: {
      'Authorization': `Bearer ${token}`
    }
  });
  return next.handle(cloned);
}
```

---

## 2. ATUALIZAR FLUXO DE LOGIN

### Request (sem mudança):
```json
POST /api/auth/login
{
  "username": "email@example.com",
  "password": "senha123"
}
```

### Response (MUDOU):
```typescript
// ANTES:
interface AuthResponse {
  token: string;
  buffetId: number;   // ❌ REMOVIDO
  role: string;
}

// DEPOIS:
interface AuthResponse {
  token: string;
  role: string;       // "CLIENTE" | "BUFFET"
}
```

### O que fazer:
- Remover `buffetId` do modelo `AuthResponse`
- Remover qualquer armazenamento de `buffetId` no localStorage/sessionStorage
- Remover qualquer lógica que use `authResponse.buffetId`

---

## 3. ATUALIZAR FLUXO DE REGISTRO

### Request (MUDOU):
```typescript
// ANTES:
POST /api/auth/register
{
  "nome": "João",
  "email": "joao@email.com",
  "senha": "123456",
  "telefone": "11999999999",
  "buffetId": 1        // ❌ REMOVER
}

// DEPOIS:
POST /api/auth/register
{
  "nome": "João",
  "email": "joao@email.com",
  "senha": "123456",
  "telefone": "11999999999"
}
```

### O que fazer:
- Remover campo `buffetId` do formulário de registro
- Remover campo `buffetId` do DTO `RegisterRequest`

---

## 4. ATUALIZAR CRIAÇÃO DE RESERVA

### Request (MUDOU):
```typescript
// ANTES:
POST /api/clientes/reservas?clienteId=1
Headers: X-Buffet-Id: 1
{
  "buffetId": 1,          // ❌ REMOVER
  "qtdPessoas": 50,
  "dataDesejada": "2026-03-15",
  "horarioDesejado": "18:00",
  "endereco": { ... },
  "servicoIds": [1, 2],
  "comidaIds": [1, 2, 3]
}

// DEPOIS:
POST /api/clientes/reservas?clienteId=1
{
  "qtdPessoas": 50,
  "dataDesejada": "2026-03-15",
  "horarioDesejado": "18:00",
  "endereco": { ... },
  "servicoIds": [1, 2],
  "comidaIds": [1, 2, 3]
}
```

---

## 5. ATUALIZAR TODOS OS ENDPOINTS - REMOVER HEADER

Todos estes endpoints **NÃO** recebem mais o header `X-Buffet-Id`:

### Comidas (`/api/comidas`)
| Método | Endpoint | Mudança |
|--------|----------|---------|
| POST | `/api/comidas` | Remover header X-Buffet-Id |
| GET | `/api/comidas` | Remover header X-Buffet-Id |
| PUT | `/api/comidas/{id}` | Remover header X-Buffet-Id |
| DELETE | `/api/comidas/{id}` | Remover header X-Buffet-Id |

### Serviços (`/api/servicos`)
| Método | Endpoint | Mudança |
|--------|----------|---------|
| POST | `/api/servicos` | Remover header X-Buffet-Id |
| GET | `/api/servicos` | Remover header X-Buffet-Id |
| PUT | `/api/servicos/{id}` | Remover header X-Buffet-Id |
| DELETE | `/api/servicos/{id}` | Remover header X-Buffet-Id |

### Eventos (`/api/eventos`)
| Método | Endpoint | Mudança |
|--------|----------|---------|
| GET | `/api/eventos/{id}` | Remover header X-Buffet-Id |
| GET | `/api/eventos` | Remover header X-Buffet-Id |
| DELETE | `/api/eventos/{id}` | Remover header X-Buffet-Id |
| GET | `/api/eventos/datas-indisponiveis` | Remover header X-Buffet-Id |
| GET | `/api/eventos/me` | Remover header X-Buffet-Id |
| PUT | `/api/eventos/{id}/valor` | Remover header X-Buffet-Id |
| PUT | `/api/eventos/{id}/concluir` | Remover header X-Buffet-Id |
| PUT | `/api/eventos/{id}/cancelar` | Remover header X-Buffet-Id |
| PUT | `/api/eventos/{id}/reverter-cancelamento` | Remover header X-Buffet-Id |
| PUT | `/api/eventos/{id}/cliente/cancelar` | Remover header X-Buffet-Id |
| PUT | `/api/eventos/{id}/cliente` | Remover header X-Buffet-Id |

### Reservas - Cliente (`/api/clientes/reservas`)
| Método | Endpoint | Mudança |
|--------|----------|---------|
| POST | `/api/clientes/reservas` | Remover header + remover buffetId do body |
| GET | `/api/clientes/reservas/{id}` | Remover header X-Buffet-Id |
| GET | `/api/clientes/reservas/me` | Remover header X-Buffet-Id |
| PUT | `/api/clientes/reservas/{id}` | Remover header X-Buffet-Id |
| PUT | `/api/clientes/reservas/{id}/itens` | Remover header X-Buffet-Id |
| PUT | `/api/clientes/reservas/{id}/cancelar` | Remover header X-Buffet-Id |
| GET | `/api/clientes/reservas/{id}/servicos` | Remover header X-Buffet-Id |
| POST | `/api/clientes/reservas/{id}/servicos/{servicoId}` | Remover header X-Buffet-Id |
| DELETE | `/api/clientes/reservas/{id}/servicos/{servicoId}` | Remover header X-Buffet-Id |

### Reservas - Buffet (`/api/buffets/reservas`)
| Método | Endpoint | Mudança |
|--------|----------|---------|
| GET | `/api/buffets/reservas` | Remover header X-Buffet-Id |
| PUT | `/api/buffets/reservas/aprovar/{id}` | Remover header X-Buffet-Id |
| PUT | `/api/buffets/reservas/recusar/{id}` | Remover header X-Buffet-Id |
| PUT | `/api/buffets/reservas/cancelar/{id}` | Remover header X-Buffet-Id |
| PUT | `/api/buffets/reservas/reverter-cancelamento/{id}` | Remover header X-Buffet-Id |
| PUT | `/api/buffets/reservas/{id}/itens` | Remover header X-Buffet-Id |
| GET | `/api/buffets/reservas/{id}/servicos` | Remover header X-Buffet-Id |
| POST | `/api/buffets/reservas/{id}/servicos/{servicoId}` | Remover header X-Buffet-Id |
| DELETE | `/api/buffets/reservas/{id}/servicos/{servicoId}` | Remover header X-Buffet-Id |

### Clientes (`/api/clientes`)
| Método | Endpoint | Mudança |
|--------|----------|---------|
| PUT | `/api/clientes/me` | Remover header X-Buffet-Id |

### Recuperação de Senha (`/api/auth/password`)
| Método | Endpoint | Mudança |
|--------|----------|---------|
| POST | `/api/auth/password/forgot` | Remover header X-Buffet-Id |
| POST | `/api/auth/password/verify-code` | Remover header X-Buffet-Id |
| POST | `/api/auth/password/reset` | Remover header X-Buffet-Id |

---

## 6. CHECKLIST DE MIGRAÇÃO

- [ ] Remover header `X-Buffet-Id` do HttpInterceptor/service HTTP
- [ ] Remover `buffetId` do modelo `AuthResponse`
- [ ] Remover armazenamento de `buffetId` (localStorage/sessionStorage)
- [ ] Remover `buffetId` do formulário de registro
- [ ] Remover `buffetId` do DTO `RegisterRequest`
- [ ] Remover `buffetId` do body do `ReservaRequest`
- [ ] Remover qualquer seleção de buffet na UI (dropdown, etc.)
- [ ] Remover qualquer rota/página de listagem/seleção de buffets
- [ ] Atualizar testes que enviam `X-Buffet-Id`
- [ ] Testar login (sem buffetId)
- [ ] Testar registro (sem buffetId)
- [ ] Testar CRUD de comidas
- [ ] Testar CRUD de serviços
- [ ] Testar CRUD de eventos
- [ ] Testar fluxo completo de reservas
- [ ] Testar recuperação de senha

---

## 7. SCRIPT DE BUSCA RÁPIDA

Execute no terminal do seu projeto frontend para encontrar todas as referências:

```bash
# Buscar referências ao header X-Buffet-Id
grep -rn "X-Buffet-Id\|x-buffet-id\|BUFFET_ID\|buffetId\|buffet_id\|buffet-id" \
  --include="*.ts" --include="*.tsx" --include="*.js" --include="*.jsx" \
  --include="*.html" --include="*.json" \
  src/

# Buscar em services Angular/React
grep -rn "setHeaders.*Buffet\|headers.*Buffet\|buffetId" \
  --include="*.ts" --include="*.tsx" --include="*.js" \
  src/
```

Cada resultado encontrado deve ser avaliado e removido/atualizado conforme este guia.
