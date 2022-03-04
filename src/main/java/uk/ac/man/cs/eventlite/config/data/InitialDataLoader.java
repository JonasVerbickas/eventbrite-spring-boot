package uk.ac.man.cs.eventlite.config.data;

import java.time.LocalDate;
import java.time.LocalTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Venue;

@Configuration
@Profile("default")
public class InitialDataLoader {

	private final static Logger log = LoggerFactory.getLogger(InitialDataLoader.class);
	

	private final static String[] NAME = { "Engineering Building,%s", "Main Library,%s" };

	//@Autowired
//	private EventService eventService;


	@Autowired
	//private VenueService venueService;

	@Bean
	CommandLineRunner initDatabase(VenueService venueService) {
		return args -> {
			if (venueService.count() > 0) {
				log.info("Database already populated with venues. Skipping venue initialization.");
			} else {

				
					for (String template : NAME) {
						log.info("Preloading: " + venueService.save(new Venue(template)));
					}
				}// Build and save initial venues here.
		};
		}

			
	CommandLineRunner initDatabase(EventService eventService) {
		return args -> {
			if (eventService.count() > 0) {
				log.info("Database already populated with events. Skipping event initialization.");
			} else {
				
				Event eA = new Event();
				eA.setName("Event A");
				eA.setTime(LocalTime.of(12, 0, 0));
				eA.setDate(LocalDate.of(2022, 1, 4));
				eventService.save(eA);
			}
		};
		}
			
	
	
}
