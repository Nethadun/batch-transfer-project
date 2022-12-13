package com.example.batch.config;

import com.example.batch.model.Batch;
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
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
    @Autowired
    public JobBuilderFactory jobBuilderFactory;
    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public FlatFileItemReader<Batch> reader() {
        return new FlatFileItemReaderBuilder<Batch>()
                .name("voltItemReader")
                .resource(new ClassPathResource("batch.csv"))
                .delimited()
                .names(new String[]{"name", "telephone"})
                .lineMapper(lineMapper())
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Batch>() {{
                    setTargetType(Batch.class);
                }})
                .build();
    }

    @Bean
    public LineMapper<Batch> lineMapper() {
        final DefaultLineMapper<Batch> defaultLineMapper = new DefaultLineMapper<>();
        final DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(";");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames(new String[] {"name","telephone"});
        final BatchFieldSetMapper fieldSetMapper = new BatchFieldSetMapper();
        defaultLineMapper.setLineTokenizer(lineTokenizer);
        defaultLineMapper.setFieldSetMapper(fieldSetMapper);
        return defaultLineMapper;
    }

    @Bean
    public BatchProcessor processor() {
        return new BatchProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Batch> writer(final DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Batch>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO batch (name, telephone) VALUES (:name, :telephone)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Job importVoltageJob(NotificationListener listener, Step step1) {
        return jobBuilderFactory.get("importVoltageJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step step1(JdbcBatchItemWriter<Batch> writer) {
        return stepBuilderFactory.get("step1")
                .<Batch, Batch> chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer)
                .build();
    }

}
