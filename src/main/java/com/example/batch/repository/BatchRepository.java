package com.example.batch.repository;

import com.example.batch.model.Batch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchRepository extends JpaRepository<Batch,Long> {
}
