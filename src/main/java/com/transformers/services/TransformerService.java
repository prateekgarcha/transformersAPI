package com.transformers.services;

import static com.transformers.models.Transformer.TYPE.AUTOBOT;
import static com.transformers.models.Transformer.TYPE.DECEPTICON;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.transformers.exceptions.TransformerNotFoundException;
import com.transformers.models.Transformer;
import com.transformers.repository.TransformerRepository;

@Component
public class TransformerService {
    private final TransformerRepository repository;
    private final String decepticonsStr = "DECEPTICONS";
    private final String autobotsStr = "AUTOBOTS";

    public TransformerService(TransformerRepository repository) {
	this.repository = repository;
    }

    public List<Transformer> getAllTransformers() {
	return repository.findAll();
    }

    public Transformer createTransformer(Transformer transformer) {
	return repository.save(transformer);
    }

    public Transformer findById(Integer id) {
	return repository.findById(id)
		.orElseThrow(() -> new TransformerNotFoundException(id));
    }

    public Transformer updateOrCreateTransformer(Transformer newTransformer,
	    Integer id) {
	return repository.findById(id).map((Transformer transformer) -> {
	    transformer.setStrength(newTransformer.getStrength());
	    transformer.setIntelligence(newTransformer.getIntelligence());
	    transformer.setSpeed(newTransformer.getSpeed());
	    transformer.setEndurance(newTransformer.getEndurance());
	    transformer.setRank(newTransformer.getRank());
	    transformer.setCourage(newTransformer.getCourage());
	    transformer.setFirepower(newTransformer.getFirepower());
	    transformer.setSkill(newTransformer.getSkill());
	    transformer.setName(newTransformer.getName());
	    transformer.setType(newTransformer.getType());
	    return repository.save(transformer);
	}).orElseGet(() -> {
	    // a new transformer with an auto generated id will be created.
	    return repository.save(newTransformer);
	});
    }

    public void delete(Integer id) {
	repository.deleteById(id);
    }

    public HashMap<String, String> getBattleResult(List<Integer> ids) {
	HashMap<String, String> res = new HashMap<>();
	List<Transformer> transformers = repository.findAllById(ids);
	if (transformers.size() < 2) {
	    res.put("Error", "2 or more valid ids required");
	    return res;
	}
	List<Transformer> autobots = getTransfromersByTypeSortedByRank(
		transformers, AUTOBOT);
	List<Transformer> decepticons = getTransfromersByTypeSortedByRank(
		transformers, DECEPTICON);

	String survivors = "No survivors";

	res = calculateWinner(autobots, decepticons);
	if (res.get("HasGameBeenDestoryed") == "NO") {
	    if (res.get("Winner").contains(decepticonsStr)
		    && autobots.size() > decepticons.size()) {
		survivors = getSurvivorNames(autobots, decepticons.size(),
			autobotsStr);
	    } else if (res.get("Winner").contains(autobotsStr)
		    && decepticons.size() > autobots.size()) {
		survivors = getSurvivorNames(decepticons, autobots.size(),
			decepticonsStr);
	    }
	} else {
	    res.replace("Winner", "Everyone was destroyed");
	}
	res.put("Survivors", survivors);
	res.remove("HasGameBeenDestoryed");
	return res;
    }

    private List<Transformer> getTransfromersByTypeSortedByRank(
	    List<Transformer> transformers, Transformer.TYPE type) {
	return transformers.stream().filter(t -> t.getType() == type)
		.sorted((t1, t2) -> Integer.compare(t1.getRank(), t2.getRank()))
		.collect(Collectors.toList());
    }

    private String getSurvivorNames(List<Transformer> list, int startingIndex,
	    String teamName) {
	String acc = "Survivors from losing team (" + teamName + "): ";
	// since the list is sorted, skipped transformers will be from the end
	// of the list
	return list.subList(startingIndex, list.size()).stream()
		.map(t -> t.getName() + " ").reduce(acc, String::concat).trim();
    }

    private HashMap<String, String> calculateWinner(List<Transformer> autobots,
	    List<Transformer> decepticons) {
	// using hashmap to keep and increment the count of battles won by a
	// team
	HashMap<String, Integer> battlesResult = new HashMap<>();
	battlesResult.put(decepticonsStr, 0);
	battlesResult.put(autobotsStr, 0);
	battlesResult.put("HasGameBeenDestoryed", 0);
	battlesResult.put("NumberOfBattles", 0);

	return beginFights(battlesResult, autobots, decepticons);
    }

    private HashMap<String, String> beginFights(
	    HashMap<String, Integer> battlesResult, List<Transformer> autobots,
	    List<Transformer> decepticons) {
	// zipping both the lists so that the iterations are run according to
	// the list with the minimum size and items in the list with larger size
	// are skipped accordingly
	for (int index = 0; index < Math.min(autobots.size(),
		decepticons.size()); index++) {
	    if (battlesResult.get("HasGameBeenDestoryed") == 0) {
		Transformer autobot = autobots.get(index);
		Transformer decepticon = decepticons.get(index);

		int courageDifference = autobot.getCourage()
			- decepticon.getCourage();
		int strengthDifference = autobot.getStrength()
			- decepticon.getStrength();
		int skillDifference = autobot.getSkill()
			- decepticon.getSkill();

		// we can add another OR condition in this if block to check if
		// there is a clone of either Optimus Prime or Predaking but
		// that depends on the definition of a clone which can be
		// related to the name or other features of the transformer but
		// is not clear in the requirements.
		if (autobot.getName() == "Optimus Prime"
			&& decepticon.getName() == "Predaking") {
		    // incrementing the number of battles by one and breaking
		    // the loop because the game has been destroyed because of
		    // this battle
		    battlesResult.replace("HasGameBeenDestoryed", 1);
		    battlesResult.replace("NumberOfBattles",
			    battlesResult.get("NumberOfBattles") + 1);
		    break;
		} else if (autobot.getName() == "Optimus Prime"
			&& decepticon.getName() != "Predaking") {
		    battlesResult.merge(autobotsStr, 1, Integer::sum);
		} else if (decepticon.getName() == "Predaking"
			&& autobot.getName() != "Optimus Prime") {
		    battlesResult.merge(decepticonsStr, 1, Integer::sum);
		} else if (courageDifference >= 4 || strengthDifference >= 3
			|| skillDifference >= 3) {
		    battlesResult.merge(autobotsStr, 1, Integer::sum);
		} else if (courageDifference <= -4 || strengthDifference <= -3
			|| skillDifference <= -3) {
		    battlesResult.merge(decepticonsStr, 1, Integer::sum);
		} else if (autobot.getOverallRating() > decepticon
			.getOverallRating()) {
		    battlesResult.merge(autobotsStr, 1, Integer::sum);
		} else if (decepticon.getOverallRating() > autobot
			.getOverallRating()) {
		    battlesResult.merge(decepticonsStr, 1, Integer::sum);
		}
		// incrementing the battle count as the game has not been
		// destroyed even though there may be a chance where there was a
		// tie but it still counts as a battle
		battlesResult.replace("NumberOfBattles",
			battlesResult.get("NumberOfBattles") + 1);
	    }
	}
	return getFinalResult(battlesResult, autobots, decepticons);
    }

    private HashMap<String, String> getFinalResult(
	    HashMap<String, Integer> battlesResult, List<Transformer> autobots,
	    List<Transformer> decepticons) {

	HashMap<String, String> result = new HashMap<>();
	result.put("Winner",
		battlesResult.get("NumberOfBattles") == 0 ? "No battles fought"
			: "Equal matches won by both teams");
	result.put("NumberOfBattles",
		battlesResult.get("NumberOfBattles").toString() + " battle");
	result.put("HasGameBeenDestoryed",
		battlesResult.get("HasGameBeenDestoryed") == 0 ? "NO" : "YES");

	Transformer maxAutobot = getMaxTransformer(autobots);
	Transformer maxDecepticon = getMaxTransformer(decepticons);

	// if game has not been destroyed, get the winning team name and the
	// member of the winning team with the highest rating
	if (maxAutobot != null && maxDecepticon != null) {
	    String autobotsWon = "Winning team (" + autobotsStr + ") : "
		    + maxAutobot.getName();
	    String decepticonsWon = "Winning team (" + decepticonsStr + ") : "
		    + maxDecepticon.getName();

	    boolean equalMatchesWon = battlesResult
		    .get(autobotsStr) == battlesResult.get(decepticonsStr);
	    boolean autobotsWonGreaterMatches = battlesResult
		    .get(autobotsStr) > battlesResult.get(decepticonsStr);
	    boolean decepticonsWonGreaterMatches = battlesResult
		    .get(decepticonsStr) > battlesResult.get(autobotsStr);
	    if (equalMatchesWon) {
		if (maxAutobot.getOverallRating() > maxDecepticon
			.getOverallRating()) {
		    result.replace("Winner", autobotsWon);
		} else if (maxDecepticon.getOverallRating() > maxAutobot
			.getOverallRating()) {
		    result.replace("Winner", decepticonsWon);
		}
	    } else if (autobotsWonGreaterMatches) {
		result.replace("Winner", autobotsWon);
	    } else if (decepticonsWonGreaterMatches) {
		result.replace("Winner", decepticonsWon);
	    }
	}

	return result;
    }

    private Transformer getMaxTransformer(List<Transformer> transformers) {
	return transformers.stream()
		.max(Comparator.comparing(Transformer::getOverallRating))
		.orElse(null);
    }

}
