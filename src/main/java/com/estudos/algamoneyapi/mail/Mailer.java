package com.estudos.algamoneyapi.mail;

import com.estudos.algamoneyapi.model.Lancamento;
import com.estudos.algamoneyapi.repository.LancamentoRepository;
import org.hibernate.mapping.UnionSubclass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.*;

import org.thymeleaf.context.Context;


@Component
public class Mailer {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine thymeleaf;

//    @EventListener
//    private void teste(ApplicationReadyEvent event){
//        this.enviarEmail("fernandesmjanayna@gmail.com", Arrays.asList("janaynambr@gmail.com"),
//                "Testando", "Olá! <br/> Teste ok.");
//    }

//    	@Autowired
//    	private LancamentoRepository repo;
//    	@EventListener
//    	private void teste(ApplicationReadyEvent event) {
//            System.out.println("teste");
//    		String template = "mail/aviso-lancamentos-vencidos";
//
//    		List<Lancamento> lista = repo.findAll();
//
//    		Map<String, Object> variaveis = new HashMap<>();
//    		variaveis.put("lancamentos", lista);
//
//    		this.enviarEmail("fernandesmjanayna@gmail.com",
//    				Arrays.asList("janaynambr@gmail.com"),
//    				"Testando", template, variaveis);
//    		System.out.println("Terminado o envio de e-mail...");
//    	}

    public void avisarSobreLancamentosVencidos(List<Lancamento> vencidos) {
        Map<String, Object> variaveis = new HashMap<>();
        variaveis.put("lancamentos", vencidos);
        List<String> emails = Collections.singletonList("janaynambr@gmail.com");
        this.enviarEmail("fernandesmjanayna@gmail.com", emails, "Lançamentos Vencidos", "mail/aviso-lancamentos-vencidos", variaveis);
    }

    public void enviarEmail(String remetente,
                            List<String> destinatarios, String assunto, String template,
                            Map<String, Object> variaveis) {
        Context context = new Context(new Locale("pt", "BR"));

        variaveis.forEach(context::setVariable);

        String mensagem = thymeleaf.process(template, context);

        this.enviarEmail(remetente, destinatarios, assunto, mensagem);
    }

    public void enviarEmail(String remetente, List<String> destinatarios, String assunto, String mensagem){
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
            helper.setFrom(remetente);
            helper.setTo(destinatarios.toArray(new String[destinatarios.size()]));
            helper.setSubject(assunto);
            helper.setText(mensagem, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw  new RuntimeException("Problemas com o envio de e-mail!", e);
        }
    }
}
