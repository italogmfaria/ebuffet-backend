package com.ebuffet.service;

import com.ebuffet.models.Arquivo;
import com.ebuffet.models.enums.EnumTipoArquivo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ArquivoService {

    Arquivo uploadAndCreateArquivo(MultipartFile file, EnumTipoArquivo tipo) throws IOException;
}
