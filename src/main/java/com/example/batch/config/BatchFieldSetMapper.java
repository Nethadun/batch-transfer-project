package com.example.batch.config;

import com.example.batch.model.Batch;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

@Component
public class BatchFieldSetMapper implements FieldSetMapper<Batch> {
    @Override
    public Batch mapFieldSet(FieldSet fieldSet) throws BindException {
        final Batch batch = new Batch();
        batch.setName(fieldSet.readString("name"));
        batch.setTelephone(fieldSet.readString("telephone"));
        return batch;
    }
}
