package com.estudos.algamoneyapi.service;


import com.estudos.algamoneyapi.model.Pessoa;
import com.estudos.algamoneyapi.repository.PessoaRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PessoaService {

    @Autowired
    private PessoaRepository pessoaRepository;

    public Pessoa atualizar(Long codigo, Pessoa pessoa) {
        Pessoa pessoaSalva = buscarPessoaPeloCodigo(codigo);

        pessoaSalva.getContatos().clear();
        pessoaSalva.getContatos().addAll(pessoa.getContatos());
        pessoaSalva.getContatos().forEach(c -> c.setPessoa(pessoaSalva));

        BeanUtils.copyProperties(pessoa, pessoaSalva, "codigo", "contatos");
        return pessoaRepository.save(pessoaSalva);
    }

    public void atualizarPropriedadeAtivo(Long codigo, Boolean ativo) {
        Pessoa pessoaSalva = buscarPessoaPeloCodigo(codigo);

        pessoaSalva.setAtivo(ativo);
        pessoaRepository.save(pessoaSalva);
    }

    private Pessoa buscarPessoaPeloCodigo(Long codigo) {
        Optional<Pessoa> pessoaSalva = pessoaRepository.findById(codigo);
        if(!pessoaSalva.isPresent()){
            throw new EmptyResultDataAccessException(1);
        }
        return pessoaSalva.get();
    }

    public Pessoa save(Pessoa pessoa) {
        pessoa.getContatos().forEach(c -> c.setPessoa(pessoa));
        return pessoaRepository.save(pessoa);
    }
}
