package com.transformers.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.transformers.models.Transformer;
import com.transformers.repository.TransformerRepository;
import com.transformers.services.TransformerService;

public class ServiceUnitTests {

    // Creating a Mock Repository to speedup the test cases instead of
    // connecting to a database
    private static TransformerRepository repository = Mockito
	    .mock(TransformerRepository.class);
    private static TransformerService service;

    @BeforeClass
    public static void initTransformer() {
	service = new TransformerService(repository);
    }

    private Transformer initialize() {
	Transformer transformer = new Transformer(1, "Optimus Prime", 10, 9, 10, 9,
		1, 9, 9, 10, Transformer.TYPE.AUTOBOT);
	when(repository.save(any(Transformer.class))).thenReturn(transformer);
	return service.createTransformer(transformer);
    }

    @Test
    public void getListOfSavedTransformers() {
	// checking list before adding transformers
	assertTrue(service.getAllTransformers().isEmpty());
	Transformer transformer = initialize();

	List<Transformer> transformers = new ArrayList<>();
	transformers.add(transformer);
	when(repository.findAll()).thenReturn(transformers);

	// checking list after adding transformers
	assertFalse(service.getAllTransformers().isEmpty());
    }

    @Test
    public void checkIfTransformerIsGettingSavedOrNot() {
	Transformer transformer = new Transformer(1, "Optimus Prime", 10, 9, 10, 9,
		1, 9, 9, 10, Transformer.TYPE.AUTOBOT);
	when(repository.save(any(Transformer.class))).thenReturn(transformer);
	Transformer savedTransformer = service.createTransformer(transformer);
	assertEquals("Optimus Prime", savedTransformer.getName());
    }

    @Test
    public void updateAlreadySavedTransformer() {
	Transformer savedTransformer = initialize();
	savedTransformer.setName("New Name");
	savedTransformer.setRank(99);
	savedTransformer = service.updateOrCreateTransformer(savedTransformer,
		savedTransformer.getId());
	assertEquals("New Name", savedTransformer.getName());
	assertEquals(99, savedTransformer.getRank());
    }

    @Test
    public void getDetailsOfSavedTransformer() {
	Transformer savedTransformer = initialize();
	when(repository.findById(any(Integer.class)))
		.thenReturn(Optional.of(savedTransformer));
	Transformer fetchedTransformer = service
		.findById(savedTransformer.getId());
	assertEquals("Optimus Prime", fetchedTransformer.getName());
    }

    @Test
    public void deleteTransformerById() {
	Transformer savedTransformer = initialize();
	service.delete(savedTransformer.getId());
	verify(repository, times(1)).deleteById(eq(savedTransformer.getId()));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getBattleResult() {
	Transformer optimus = new Transformer(1, "Optimus Prime", 10, 9, 10, 9,
		1, 9, 9, 10, Transformer.TYPE.AUTOBOT);
	when(repository.save(any(Transformer.class))).thenReturn(optimus);
	optimus = service.createTransformer(optimus);

	Transformer soundwave = new Transformer(3, "Soundwave", 8, 9, 2, 6, 7,
		5, 6, 10, Transformer.TYPE.DECEPTICON);
	when(repository.save(any(Transformer.class))).thenReturn(soundwave);
	soundwave = service.createTransformer(soundwave);

	Transformer bluestreak = new Transformer(4, "Bluestreak", 6, 6, 7, 9, 5,
		2, 9, 7, Transformer.TYPE.AUTOBOT);
	when(repository.save(any(Transformer.class))).thenReturn(bluestreak);
	bluestreak = service.createTransformer(bluestreak);
	List<Integer> ids = new ArrayList<>();
	ids.add(optimus.getId());
	ids.add(soundwave.getId());
	ids.add(bluestreak.getId());

	List<Transformer> transformers = new ArrayList<>();
	transformers.add(optimus);
	transformers.add(soundwave);
	transformers.add(bluestreak);

	when(repository.findAllById(anyList())).thenReturn(transformers);
	HashMap<String, String> result = service.getBattleResult(ids);
	assertEquals("No survivors", result.get("Survivors"));
	assertEquals("1 battle", result.get("NumberOfBattles"));
	assertEquals("Winning team (AUTOBOTS) : Optimus Prime", result.get("Winner"));
    }

}
