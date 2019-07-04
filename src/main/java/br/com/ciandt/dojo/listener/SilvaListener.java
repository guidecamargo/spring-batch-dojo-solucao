package br.com.ciandt.dojo.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.stereotype.Component;

import br.com.ciandt.dojo.model.Cliente;

@Component
public class SilvaListener implements ItemReadListener<Cliente> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SilvaListener.class);

    @Override
    public void beforeRead() {
        // Bloco vazio
    }

    @Override
    public void afterRead(final Cliente item) {
        if ("Silva".equalsIgnoreCase(item.getSobrenome())) {
            LOGGER.info("**** Silva localizado: " + item.getNome());
        }
    }

    @Override
    public void onReadError(final Exception ex) {
        // Bloco vazio
    }

}
