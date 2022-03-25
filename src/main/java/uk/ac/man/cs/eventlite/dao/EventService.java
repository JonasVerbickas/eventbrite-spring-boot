package uk.ac.man.cs.eventlite.dao;

import java.util.Optional;


import uk.ac.man.cs.eventlite.entities.Event;

public interface EventService {

	public long count();

	public Iterable<Event> findAll();

	public Iterable<Event> findAllByOrderByDateAscTimeAsc();
	
	public Optional<Event> findById(long id);
	
	public Event save(Event entity);

	public void deleteById(long id);
	
}
