package com.transformers.api;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transformers.models.Transformer;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ApiTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetAllTransformers() throws Exception {
	mockMvc.perform(MockMvcRequestBuilders.get("/transformers")
		.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers
			.jsonPath("$._embedded.transformerList").isArray());
    }

    @Test
    public void checkIfTransformerIsGettingSavedOrNot() throws Exception {
	mockMvc.perform(MockMvcRequestBuilders.post("/transformers")
		.content(asJsonString(new Transformer(6, "Test transformer", 4,
			6, 7, 9, 5, 2, 9, 7, Transformer.TYPE.AUTOBOT)))
		.contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.name")
			.value("Test transformer"));
    }

    @Test
    public void getDetailsOfTransformerWithValidId() throws Exception {
	mockMvc.perform(MockMvcRequestBuilders.get("/transformers/1")
		.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.name")
			.value("Optimus Prime"));
    }

    @Test
    public void getDetailsOfTransformerWithInvalidId() throws Exception {
	mockMvc.perform(MockMvcRequestBuilders.get("/transformers/10")
		.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound());
    }

    @DirtiesContext
    @Test
    public void updateAlreadyExistingTransformerWithValidId() throws Exception {
	mockMvc.perform(MockMvcRequestBuilders.put("/transformers/1")
		.content(asJsonString(new Transformer(1, "Evil Optimus Prime",
			10, 16, 7, 9, 5, 2, 9, 7, Transformer.TYPE.DECEPTICON)))
		.contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.name")
			.value("Evil Optimus Prime"))
		.andExpect(MockMvcResultMatchers.jsonPath("$.intelligence")
			.value(16))
		.andExpect(MockMvcResultMatchers.jsonPath("$.type")
			.value(Transformer.TYPE.DECEPTICON.toString()));
    }

    @DirtiesContext
    @Test
    public void deleteTransformerWithValidId() throws Exception {
	mockMvc.perform(MockMvcRequestBuilders.delete("/transformers/1")
		.accept(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers.content()
			.string("Transformer deleted"));
    }

    @Test
    public void deleteTransformerWithInvalidId() throws Exception {
	mockMvc.perform(MockMvcRequestBuilders.delete("/transformers/10")
		.accept(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers.content()
			.string("No Transformer with this ID exists"));
    }

    @Test
    public void findWinnerFromTransformersBattleWhenOptimusPrimeAndPredakingFight()
	    throws Exception {
	String[] ids = new String[] { "1", "2", "3", "4", "5" };
	mockMvc.perform(MockMvcRequestBuilders.post("/getBattleResult")
		.content(asJsonString(ids)).contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers.jsonPath("$.content.Survivors")
			.value("No survivors"))
		.andExpect(MockMvcResultMatchers
			.jsonPath("$.content.NumberOfBattles")
			.value("1 battle"))
		.andExpect(MockMvcResultMatchers.jsonPath("$.content.Winner")
			.value("Everyone was destroyed"));

    }

    @Test
    public void findWinnerFromTransformersBattleWithValidIds()
	    throws Exception {
	String[] ids = new String[] { "5", "3", "4" };
	mockMvc.perform(MockMvcRequestBuilders.post("/getBattleResult")
		.content(asJsonString(ids)).contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers.jsonPath("$.content.Survivors")
			.value("Survivors from losing team (AUTOBOTS): Bluestreak"))
		.andExpect(MockMvcResultMatchers
			.jsonPath("$.content.NumberOfBattles")
			.value("1 battle"))
		.andExpect(MockMvcResultMatchers.jsonPath("$.content.Winner")
			.value("Winning team (DECEPTICONS) : Soundwave"));

    }

    @Test
    public void findWinnerFromTransformersBattleWithInvalidIds()
	    throws Exception {
	// An invalid id will not return an instance and hence in this case
	// only 2 transformers will fight i.e. 3 & 4
	String[] ids = new String[] { "15", "3", "4" };
	mockMvc.perform(MockMvcRequestBuilders.post("/getBattleResult")
		.content(asJsonString(ids)).contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers.jsonPath("$.content.Survivors")
			.value("No survivors"))
		.andExpect(MockMvcResultMatchers
			.jsonPath("$.content.NumberOfBattles")
			.value("1 battle"))
		.andExpect(MockMvcResultMatchers.jsonPath("$.content.Winner")
			.value("Winning team (DECEPTICONS) : Soundwave"));

    }

    @Test
    public void findWinnerFromTransformersBattleWithLessThan2Ids()
	    throws Exception {
	String[] ids = new String[] { "12","4" };
	mockMvc.perform(MockMvcRequestBuilders.post("/getBattleResult")
		.content(asJsonString(ids)).contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers.jsonPath("$.content.Error")
			.value("2 or more valid ids required"));

    }

    public static String asJsonString(final Object obj) {
	try {
	    return new ObjectMapper().writeValueAsString(obj);
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }
}
