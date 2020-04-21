package com.estudos.algamoneyapi.repository.lancamento;

import com.estudos.algamoneyapi.model.Lancamento;
import com.estudos.algamoneyapi.repository.filter.LancamentoFilter;

import java.util.List;

public interface LancamentoRepositoryQuery {
    List<Lancamento> filtrar(LancamentoFilter lancamentoFilter);
}
