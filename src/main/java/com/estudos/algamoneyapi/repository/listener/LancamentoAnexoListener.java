package com.estudos.algamoneyapi.repository.listener;

import com.estudos.algamoneyapi.Algamoney;
import com.estudos.algamoneyapi.model.Lancamento;
import com.estudos.algamoneyapi.storage.S3;
import org.springframework.util.StringUtils;

import javax.persistence.PostLoad;

public class LancamentoAnexoListener {

    @PostLoad
    public void postLoad(Lancamento lancamento){
        if(StringUtils.hasText(lancamento.getAnexo())){
            S3 s3 = Algamoney.getBean(S3.class);
            lancamento.setUrlAnexo(s3.configurarUrl(lancamento.getAnexo()));
        }
    }
}
