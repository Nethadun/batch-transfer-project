package com.example.batch.config;

import com.example.batch.model.Batch;
import org.springframework.batch.item.ItemProcessor;

public class BatchProcessor implements ItemProcessor<Batch, Batch> {
    @Override
    public Batch process(Batch batch) throws Exception {
        final String name = batch.getName();
        final String tel = batch.getTelephone();
        final Batch processedVoltage = new Batch();
        processedVoltage.setName(name);
        processedVoltage.setTelephone(tel);
        return processedVoltage;
    }
}
