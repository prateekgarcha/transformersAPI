package com.transformers;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.transformers.models.Transformer;
import com.transformers.repository.TransformerRepository;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
class LoadDatabase {
    @Bean
    CommandLineRunner initDatabase(TransformerRepository repository) {
	return args -> {
	    log.info("Preloading "
		    + repository.save(new Transformer(1, "Optimus Prime", 10, 9,
			    10, 9, 1, 9, 9, 10, Transformer.TYPE.AUTOBOT)));
	    log.info("Preloading "
		    + repository.save(new Transformer(2, "Predaking", 10, 9, 10,
			    9, 2, 9, 9, 10, Transformer.TYPE.DECEPTICON)));
	    log.info("Preloading "
		    + repository.save(new Transformer(3, "Soundwave", 8, 9, 2,
			    6, 7, 5, 6, 10, Transformer.TYPE.DECEPTICON)));
	    log.info("Preloading "
		    + repository.save(new Transformer(4, "Bluestreak", 6, 6, 7,
			    9, 5, 2, 9, 7, Transformer.TYPE.AUTOBOT)));
	    log.info(
		    "Preloading " + repository.save(new Transformer(5, "Hubcap",
			    4, 4, 4, 4, 4, 4, 4, 4, Transformer.TYPE.AUTOBOT)));
	};
    }
}
