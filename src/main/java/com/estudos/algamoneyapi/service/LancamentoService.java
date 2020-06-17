package com.estudos.algamoneyapi.service;


import com.estudos.algamoneyapi.model.Lancamento;
import com.estudos.algamoneyapi.model.Pessoa;
import com.estudos.algamoneyapi.repository.LancamentoRepository;
import com.estudos.algamoneyapi.repository.PessoaRepository;
import com.estudos.algamoneyapi.service.exception.PessoaInexistenteOuInativaException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class LancamentoService {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private LancamentoRepository lancamentoRepository;

    public Lancamento salvar(Lancamento lancamento) {
        Optional<Pessoa> pessoaSalva = pessoaRepository.findById(lancamento.getPessoa().getCodigo());
        if(!pessoaSalva.isPresent() || pessoaSalva.get().isInativo()){
            throw new PessoaInexistenteOuInativaException();
        }
        return lancamentoRepository.save(lancamento);
    }

    public Lancamento atualizar(Long codigo, Lancamento lancamento) {
        Lancamento lancamentoSalvo = buscarLancamentoPeloCodigo(codigo);

        BeanUtils.copyProperties(lancamento, lancamentoSalvo, "codigo");
        return lancamentoRepository.save(lancamentoSalvo);
    }

    private Lancamento buscarLancamentoPeloCodigo(Long codigo) {
        Optional<Lancamento> lancamentoSalvo = lancamentoRepository.findById(codigo);
        if(!lancamentoSalvo.isPresent()){
            throw new EmptyResultDataAccessException(1);
        }
        return lancamentoSalvo.get();
    }
}
