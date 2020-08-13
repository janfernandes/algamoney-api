package com.estudos.algamoneyapi.storage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.estudos.algamoneyapi.config.property.AlgamoneyApiProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

@Component
public class S3 {

    private static final Logger logger = LoggerFactory.getLogger(S3.class);

    @Autowired
    private AmazonS3 amazonS3;

    @Autowired
    private AlgamoneyApiProperty property;

    public String salvarTemporariamente(MultipartFile arquivo){
        AccessControlList accessControlList = new AccessControlList();
        accessControlList.grantPermission(GroupGrantee.AllUsers, Permission.Read);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(arquivo.getContentType());
        objectMetadata.setContentLength(arquivo.getSize());

        String nomeUnico = this.gerarNomeUnico(arquivo.getOriginalFilename());

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(property.getS3().getBucket(), nomeUnico, arquivo.getInputStream(), objectMetadata)
                    .withAccessControlList(accessControlList);

            putObjectRequest.setTagging(new ObjectTagging(Arrays.asList(new Tag("expirar", "true"))));

            amazonS3.putObject(putObjectRequest);

            if(logger.isDebugEnabled()){
                logger.debug("Arquivo {} enviado com sucesso para o S3.", arquivo.getOriginalFilename());
            }
            return nomeUnico;
        } catch (IOException e) {
            throw new RuntimeException("Problemas ao tentar enviar o arquivo para o S3.", e);
        }
    }

    public String configurarUrl(String objeto) {
        return "\\\\" + property.getS3().getBucket() +
                ".s3.amazonaws.com/" + objeto;
    }

    public void salvar(String anexo) {
        SetObjectTaggingRequest setObjectTaggingRequest = new SetObjectTaggingRequest(property.getS3().getBucket(), anexo,
                new ObjectTagging(Collections.emptyList()));
        amazonS3.setObjectTagging(setObjectTaggingRequest);
    }

    public void substituir(String anexoAntigo, String anexoNovo) {
        if(StringUtils.hasText(anexoAntigo)){
            this.removerAnexo(anexoAntigo);
        }
        this.salvar(anexoNovo);
    }

    public void removerAnexo(String anexo) {
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(property.getS3().getBucket(), anexo);
        amazonS3.deleteObject(deleteObjectRequest);
    }

    private String gerarNomeUnico(String originalFileName){
        return UUID.randomUUID().toString() + "_" + originalFileName;
    }
}
