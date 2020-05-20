package com.estudos.algamoneyapi.repository.pessoa;

import com.estudos.algamoneyapi.model.Pessoa;
import com.estudos.algamoneyapi.repository.filter.PessoaFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PessoaRepositoryQuery {
    Page<Pessoa> filtrar(PessoaFilter pessoaFilter, Pageable pageable);
}
