package com.transformers.services;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

import com.transformers.controller.TransformerController;
import com.transformers.models.Transformer;

@Component
public class TransformerResourceAssembler
	implements ResourceAssembler<Transformer, Resource<Transformer>> {

    @Override
    public Resource<Transformer> toResource(Transformer transformer) {
	return new Resource<>(transformer,
		linkTo(methodOn(TransformerController.class)
			.findTransformerById(transformer.getId())).withSelfRel(),
		linkTo(methodOn(TransformerController.class).getAllTransformers())
			.withRel("transformers"));
    }
}
