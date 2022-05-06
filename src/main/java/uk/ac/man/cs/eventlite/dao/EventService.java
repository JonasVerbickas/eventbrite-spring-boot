package uk.ac.man.cs.eventlite.dao;

import java.util.Optional;


import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

public interface EventService {

	public long count();

	public Iterable<Event> findAll();

	public Iterable<Event> findAllByOrderByDateAscNameAsc();
	
	public Iterable<Event> findAllByVenueOrderByDateAscNameAsc(Venue venue);

	public Optional<Event> findById(long id);
	
	public Event save(Event entity);
	
	public void delete(Event event);

	public void deleteById(long id);

	public Iterable<Event> findByNameContainingIgnoreCaseOrderByDateAscNameAsc(String name);

}
