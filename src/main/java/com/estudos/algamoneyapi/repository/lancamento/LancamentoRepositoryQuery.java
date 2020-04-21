package com.estudos.algamoneyapi.repository.lancamento;

import com.estudos.algamoneyapi.model.Lancamento;
import com.estudos.algamoneyapi.repository.filter.LancamentoFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LancamentoRepositoryQuery {
    Page<Lancamento> filtrar(LancamentoFilter lancamentoFilter, Pageable pageable);
}
