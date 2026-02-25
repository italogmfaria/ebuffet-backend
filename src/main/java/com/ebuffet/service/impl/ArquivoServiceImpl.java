package com.ebuffet.service.impl;

import com.ebuffet.models.Arquivo;
import com.ebuffet.models.enums.EnumStatus;
import com.ebuffet.models.enums.EnumTipoArquivo;
import com.ebuffet.repository.ArquivoRepository;
import com.ebuffet.service.ArquivoService;
import com.ebuffet.service.CloudinaryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ArquivoServiceImpl implements ArquivoService {

    private final ArquivoRepository arquivoRepo;
    private final CloudinaryService cloudinaryService;

    public ArquivoServiceImpl(ArquivoRepository arquivoRepo, CloudinaryService cloudinaryService) {
        this.arquivoRepo = arquivoRepo;
        this.cloudinaryService = cloudinaryService;
    }

    @Transactional
    @Override
    public Arquivo uploadAndCreateArquivo(MultipartFile file, EnumTipoArquivo tipo) throws IOException {
        String url = cloudinaryService.uploadFile(file);

        Arquivo arquivo = new Arquivo();
        arquivo.setTipo(tipo);
        arquivo.setUrl(url);
        arquivo.setStatus(EnumStatus.ATIVO);

        return arquivoRepo.save(arquivo);
    }
}
