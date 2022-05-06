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

	/***private final static String[] NAME = { "Engineering Building,%s", "Main Library,%s", "a", "d", "b" };***/

	@Autowired
	private EventServiceImpl eventService;

	@Autowired
	private VenueServiceImpl venueService;

	@Bean
	CommandLineRunner initDatabase() {
		return args -> {
			/***Venue v = new Venue();***/
			
			Venue vA = new Venue();
			Venue vB = new Venue();
			Venue vC = new Venue();
			Venue vD = new Venue();
			 

			if (venueService.count() > 0) {
				log.info("Database already populated with venues. Skipping venue initialization.");
			} else {
				
				/***for (String template : NAME) {
					v = new Venue(template);
					log.info("Preloading: " + venueService.save(v));
					***/
					
					vA.setName("Engineering Building");
					vA.setCapacity(300);
					vA.setAddress("Booth St E");
					vA.setPostcode("M13")		;			
					venueService.save(vA);		
					
					vB .setName("Main Library");
					vB.setCapacity(100);
					vB.setAddress("Oxford Road");
					vB.setPostcode("M13 9PP");
					venueService.save(vB);

					vC .setName("Manchester Academy");
					vC.setCapacity(200);
					vC.setAddress("Manchester Students Union, OXford Rd");
					vC.setPostcode("M13 9PR");
					venueService.save(vC);
			/***	}***/
			} // Build and save initial venues here.
			if (eventService.count() > 0) {
				log.info("Database already populated with events. Skipping event initialization.");
			} else {
				Event eA = new Event();
				eA.setName("Event A");
				eA.setTime(LocalTime.of(12, 0, 0));
				eA.setDate(LocalDate.of(2022, 8, 1));
				eA.setVenue(vA);
				eventService.save(eA);
				
				Event eB = new Event();
				eB.setName("Event B");
				eB.setTime(LocalTime.of(15, 1, 15));
				eB.setDate(LocalDate.of(2012, 1, 4));
				eB.setVenue(vC);
				eventService.save(eB);
				
				Event eC = new Event();
				eC.setName("Event C");
				eC.setTime(LocalTime.of(22, 2, 52));
				eC.setDate(LocalDate.of(2022, 8, 16));
				eC.setVenue(vB);
				eventService.save(eC);
				
				Event eD = new Event();
				eD.setName("Event D");
				eD.setTime(LocalTime.of(8, 2, 52));
				eD.setDate(LocalDate.of(2002, 8, 16));
				eD.setVenue(vB);
				eventService.save(eD);
				
				Event eF = new Event();
				eF.setName("Event F");
				eF.setTime(LocalTime.of(2, 2, 52));
				eF.setDate(LocalDate.of(2002, 8, 16));
				eF.setVenue(vC);
				eventService.save(eF);
				
				Event eG = new Event();
				eG.setName("Testing");
				eG.setTime(LocalTime.of(2, 2, 52));
				eG.setDate(LocalDate.of(2002, 8, 16));
				eG.setVenue(vC);
				eventService.save(eG);

				Event eH = new Event();
				eH.setName("Event H");
				eH.setTime(LocalTime.of(2, 2, 52));
				eH.setDate(LocalDate.of(2022, 7, 16));
				eH.setVenue(vC);
				eventService.save(eH);

				Event eI = new Event();
				eI.setName("Event I");
				eI.setTime(LocalTime.of(2, 2, 52));
				eI.setDate(LocalDate.of(2022, 6, 16));
				eI.setVenue(vC);
				eventService.save(eI);

			}
		};
	}

}
