package com.estudos.algamoneyapi.service;


import com.estudos.algamoneyapi.dto.LancamentoEstatisticaPessoa;
import com.estudos.algamoneyapi.mail.Mailer;
import com.estudos.algamoneyapi.model.Lancamento;
import com.estudos.algamoneyapi.model.Pessoa;
import com.estudos.algamoneyapi.repository.LancamentoRepository;
import com.estudos.algamoneyapi.repository.PessoaRepository;
import com.estudos.algamoneyapi.service.exception.PessoaInexistenteOuInativaException;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;


@Service
public class LancamentoService {

    private static final Logger logger = LoggerFactory.getLogger(LancamentoService.class);

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private LancamentoRepository lancamentoRepository;

    @Autowired
    private Mailer mailer;

//    @Scheduled(fixedDelay = 1000 * 60 * 30)
//    executa em 18:32 dia 5 de todos os meses de todos os anos
    @Scheduled(cron = "0 32 18 5 * *")
    public void avisarSobreLancamentosVencidos(){
//        System.out.println(">>>>>>>>>>>>>>Método sendo executado...");
        if(logger.isDebugEnabled())
            logger.debug("Preparando envio de emails de lancamentos vencidos");
        List<Lancamento> vencidos = lancamentoRepository.findByDataVencimentoLessThanEqualAndDataPagamentoIsNull(LocalDate.now());
        if(vencidos.isEmpty()){
            logger.info("Sem lançamentos vencidos para aviso.");
            return;
        }
        logger.info("Existem {} lancamentos vencidos.", vencidos.size());
        mailer.avisarSobreLancamentosVencidos(vencidos);
        logger.info("Envio de email concluido");
    }

    public byte[] relatorioPorPessoa(LocalDate inicio, LocalDate fim) throws JRException {
        List<LancamentoEstatisticaPessoa> dados = lancamentoRepository.porPessoa(inicio, fim);
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("DT_INICIO", Date.valueOf(inicio));
        parametros.put("DT_FIM", Date.valueOf(fim));
        parametros.put("REPORT_LOCALE", new Locale("pt", "BR"));
        InputStream inputStream = this.getClass().getResourceAsStream("/relatorios/LancamentosPorPessoa.jasper");
        JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, parametros, new JRBeanCollectionDataSource(dados));
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

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
