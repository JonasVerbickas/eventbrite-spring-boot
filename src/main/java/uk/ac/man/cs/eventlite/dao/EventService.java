package uk.ac.man.cs.eventlite.dao;

import uk.ac.man.cs.eventlite.entities.Event;

public interface EventService {

	public long count();

	public Iterable<Event> findAll();

	public Iterable<Event> findAllByOrderByDateAscTimeAsc();
	
	public void save(Event entity);

	public void deleteById(long id);
}
