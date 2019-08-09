package com.transformers.exceptions;

@SuppressWarnings("serial")
public class TransformerNotFoundException extends RuntimeException {
    public TransformerNotFoundException(Integer id) {
	super("Could not find transformer with ID " + id);
    }
}