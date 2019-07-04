package br.com.ciandt.dojo.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;

import br.com.ciandt.dojo.model.Cliente;

public class ClienteItemProcessor implements ItemProcessor<Cliente, Cliente> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClienteItemProcessor.class);

    private String regiao;

    @BeforeStep
    public void beforeStep(final StepExecution stepExecution) {
        this.regiao = stepExecution.getJobExecution().getExecutionContext().getString("REGION");
    }

    @Override
    public Cliente process(final Cliente cliente) throws Exception {
        final String nome = cliente.getNome().toUpperCase();
        final String sobrenome = cliente.getSobrenome().toUpperCase();

        final Cliente clienteConvertido = new Cliente(nome, sobrenome, this.regiao);

        LOGGER.info("Convertendo (" + cliente + ") para (" + clienteConvertido + ")");

        return clienteConvertido;
    }
}
