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
import uk.ac.man.cs.eventlite.dao.EventServiceImpl;
import uk.ac.man.cs.eventlite.dao.VenueServiceImpl;
import uk.ac.man.cs.eventlite.entities.Venue;

@Configuration
@Profile("default")
public class InitialDataLoader {

	private final static Logger log = LoggerFactory.getLogger(InitialDataLoader.class);

	private final static String[] NAME = { "Engineering Building,%s", "Main Library,%s" };

	@Autowired
	private EventServiceImpl eventService;

	@Autowired
	private VenueServiceImpl venueService;

	@Bean
	CommandLineRunner initDatabase() {
		return args -> {
			Venue v = new Venue();
			if (venueService.count() > 0) {
				log.info("Database already populated with venues. Skipping venue initialization.");
			} else {
				for (String template : NAME) {
					v = new Venue(template);
					log.info("Preloading: " + venueService.save(v));

				}
			} // Build and save initial venues here.
			if (eventService.count() > 0) {
				log.info("Database already populated with events. Skipping event initialization.");
			} else {

				Event eA = new Event();
				eA.setName("Event A");
				eA.setTime(LocalTime.of(12, 0, 0));
				eA.setDate(LocalDate.of(2022, 2, 1));
				eA.setVenue(v);
				eventService.save(eA);
				Event eD = new Event();
				eD.setName("Event D");
				eD.setTime(LocalTime.of(8, 2, 52));
				eD.setDate(LocalDate.of(2002, 8, 16));
				eD.setVenue(v);
				eventService.save(eD);
				Event eB = new Event();
				eB.setName("Event B");
				eB.setTime(LocalTime.of(15, 1, 15));
				eB.setDate(LocalDate.of(2012, 1, 4));
				eB.setVenue(v);
				eventService.save(eB);
				Event eC = new Event();
				eC.setName("Event C");
				eC.setTime(LocalTime.of(22, 2, 52));
				eC.setDate(LocalDate.of(2002, 8, 16));
				eC.setVenue(v);
				eventService.save(eC);
				Event eF = new Event();
				eF.setName("Event F");
				eF.setTime(LocalTime.of(2, 2, 52));
				eF.setDate(LocalDate.of(2002, 8, 16));
				eF.setVenue(v);
				eventService.save(eF);
			}
		};
	}

}
