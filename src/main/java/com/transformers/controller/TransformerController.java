package com.transformers.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.transformers.models.Transformer;
import com.transformers.services.TransformerResourceAssembler;
import com.transformers.services.TransformerService;

@RestController
public class TransformerController {
    private final TransformerService service;
    private final TransformerResourceAssembler assembler;

    public TransformerController(TransformerService service,
	    TransformerResourceAssembler assembler) {
	this.service = service;
	this.assembler = assembler;
    }

    // get all the saved transformers
    @GetMapping(value = "/transformers", produces = {
	    MediaType.APPLICATION_JSON_VALUE })
    public Resources<Resource<Transformer>> getAllTransformers() {
	List<Resource<Transformer>> transformers = service.getAllTransformers()
		.stream().map(assembler::toResource)
		.collect(Collectors.toList());

	return new Resources<>(transformers, linkTo(
		methodOn(TransformerController.class).getAllTransformers())
			.withSelfRel());
    }

    // save a new transformer with all the data in the post request body
    @PostMapping(value = "/transformers", produces = {
	    MediaType.APPLICATION_JSON_VALUE })
    public Resource<Transformer> saveTransformer(
	    @RequestBody Transformer transformer) {
	return assembler.toResource(service.createTransformer(transformer));
    }

    // get info for single transformer based on id
    @GetMapping(value = "/transformers/{id}", produces = {
	    MediaType.APPLICATION_JSON_VALUE })
    public Resource<Transformer> findTransformerById(@PathVariable Integer id) {
	Transformer transformer = service.findById(id);
	return assembler.toResource(transformer);
    }

    // update transformer info based on id
    // creates a new transformer if id does not exist
    @PutMapping(value = "/transformers/{id}", produces = {
	    MediaType.APPLICATION_JSON_VALUE })
    public Resource<Transformer> updateOrCreateTransformer(
	    @RequestBody Transformer newTransformer, @PathVariable Integer id) {
	newTransformer = service.updateOrCreateTransformer(newTransformer, id);
	return assembler.toResource(newTransformer);
    }

    // delete a transformer by id
    @DeleteMapping("/transformers/{id}")
    public String deleteTransformer(@PathVariable Integer id) {
	try {
	    service.delete(id);
	    return "Transformer deleted";
	} catch (Exception e) {
	    return "No Transformer with this ID exists";
	}
    }

    // get battle details of a fight between transformers as per the passed in
    // ids. The response will be in json form containing keys like : Survivors,
    // NumberOfBattles and Winner.
    @PostMapping(value = "/getBattleResult", produces = {
	    MediaType.APPLICATION_JSON_VALUE })
    public Resource<HashMap<String, String>> getBattleResult(
	    @RequestBody List<Integer> paramIds) {
//	ArrayList<Integer> ids = new ArrayList<>();
//	Collections.addAll(ids, paramIds);
	HashMap<String, String> res = service.getBattleResult(paramIds);
	return new Resource<>(res);
    }
}
