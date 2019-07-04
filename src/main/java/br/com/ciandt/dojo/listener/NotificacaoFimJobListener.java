package br.com.ciandt.dojo.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import br.com.ciandt.dojo.model.Cliente;

@Component
public class NotificacaoFimJobListener extends JobExecutionListenerSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificacaoFimJobListener.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public NotificacaoFimJobListener(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void afterJob(final JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            LOGGER.info("!!! JOB FINISHED! Time to verify the results");

            this.jdbcTemplate.query("SELECT nome, sobrenome, regiao FROM clientes",
                            (rs, row) -> new Cliente(
                                            rs.getString(1),
                                            rs.getString(2),
                                            rs.getString(3)))
                            .forEach(person -> LOGGER.info("Encontrado <" + person + "> na base de dados."));
        }
    }
}
