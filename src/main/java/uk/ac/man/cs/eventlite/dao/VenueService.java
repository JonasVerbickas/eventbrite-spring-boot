package uk.ac.man.cs.eventlite.dao;

import java.util.Optional;

import uk.ac.man.cs.eventlite.entities.Venue;


public interface VenueService {

	public long count();

	public Iterable<Venue> findAll();

	public Iterable<Venue> findAllByOrderByNameAsc();
	
	public Venue save(Venue venue);
	
	public Optional<Venue> findById(long i);

	public void deleteById(long id);

	public Iterable<Venue> listVenueByName(String name);
}
