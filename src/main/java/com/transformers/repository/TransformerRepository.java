package com.transformers.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.transformers.models.Transformer;

public interface TransformerRepository
	extends JpaRepository<Transformer, Integer> {

}
