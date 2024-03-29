package com.estudos.algamoneyapi.dto;

import com.estudos.algamoneyapi.model.Pessoa;
import com.estudos.algamoneyapi.model.TipoLancamento;

import java.math.BigDecimal;

public class LancamentoEstatisticaPessoa {
    private TipoLancamento tipo;
    private Pessoa pessoa;
    private BigDecimal total;

    public LancamentoEstatisticaPessoa(TipoLancamento tipoLancamento, Pessoa pessoa, BigDecimal total) {
        this.tipo = tipoLancamento;
        this.pessoa = pessoa;
        this.total = total;
    }

    public TipoLancamento getTipo() {
        return tipo;
    }

    public void setTipo(TipoLancamento tipo) {
        this.tipo = tipo;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
