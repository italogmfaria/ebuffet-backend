package com.ebuffet.models;

import com.ebuffet.models.enums.EnumStatus;
import com.ebuffet.models.enums.EnumUserRole;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name="uk_users_email", columnNames = "email"),
                @UniqueConstraint(name="uk_users_telefone", columnNames = "telefone")
        })
@Getter
@Setter
public class User extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nome;

    @Email
    @NotNull
    private String email;

    private String telefone;

    @NotNull
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String senha;

    @Column(name = "buffet_id")
    private Long buffetId;

    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
    private List<Reserva> reservas = new ArrayList<>();

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private EnumUserRole role;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "foto_id", foreignKey = @ForeignKey(name = "fk_user_foto"))
    private Arquivo foto;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role == null) {
            return Collections.emptyList();
        }
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email != null ? email : telefone;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
