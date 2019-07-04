package br.com.ciandt.dojo.configuration;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import br.com.ciandt.dojo.listener.NotificacaoFimJobListener;
import br.com.ciandt.dojo.listener.SilvaListener;
import br.com.ciandt.dojo.model.Cliente;
import br.com.ciandt.dojo.processor.ClienteItemProcessor;
import br.com.ciandt.dojo.tasklet.SetupTasklet;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    private static final String OUTPUT_FILE = "target/output.txt";

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public FlatFileItemReader<Cliente> reader() {
        return new FlatFileItemReaderBuilder<Cliente>()
                        .name("clienteItemReader")
                        .resource(new ClassPathResource("dados-entrada.csv"))
                        .delimited()
                        .names(new String[]{"nome", "sobrenome"})
                        .fieldSetMapper(new BeanWrapperFieldSetMapper<Cliente>() {
                            {
                                this.setTargetType(Cliente.class);
                            }
                        })
                        .build();
    }

    @Bean
    public ClienteItemProcessor processor() {
        return new ClienteItemProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Cliente> databaseWriter(final DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Cliente>()
                        .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                        .sql("INSERT INTO clientes (nome, sobrenome, regiao) VALUES (:nome, :sobrenome, :regiao)")
                        .dataSource(dataSource)
                        .build();
    }

    @Bean
    public FlatFileItemWriter<Cliente> fileWriter() {
        FlatFileItemWriter<Cliente> writer = new FlatFileItemWriter<Cliente>();
        writer.setResource(new FileSystemResource(OUTPUT_FILE));
        writer.setLineAggregator(new DelimitedLineAggregator<Cliente>() {
            {
                this.setDelimiter("|");
                this.setFieldExtractor(new BeanWrapperFieldExtractor<Cliente>() {
                    {
                        this.setNames(new String[]{"nome", "sobrenome", "regiao"});
                    }
                });
            }
        });
        return writer;
    }

    @Bean
    public Job importarClienteJob(final NotificacaoFimJobListener listener, final Step stepSetup,
                    final Step stepChunkCliente) {
        return this.jobBuilderFactory.get("importarClienteJob")
                        .incrementer(new RunIdIncrementer())
                        .listener(listener)
                        .flow(stepSetup)
                        .next(stepChunkCliente)
                        .end()
                        .build();
    }

    @Bean
    public Step stepChunkCliente(final JdbcBatchItemWriter<Cliente> databaseWriter,
                    final FlatFileItemWriter<Cliente> fileWriter, final SilvaListener listener) {
        CompositeItemWriter<Cliente> compositeWriter = new CompositeItemWriter<>();
        compositeWriter.setDelegates(Stream
                        .of(databaseWriter, fileWriter)
                        .collect(Collectors.toCollection(ArrayList::new)));

        return this.stepBuilderFactory.get("stepChunkCliente")
                        .<Cliente, Cliente> chunk(10)
                        .reader(this.reader()).listener(listener)
                        .processor(this.processor())
                        .writer(compositeWriter)
                        .build();
    }

    @Bean
    public Step stepSetup(final SetupTasklet tasklet) {
        return this.stepBuilderFactory.get("stepSetup")
                        .tasklet(tasklet)
                        .build();
    }
}
