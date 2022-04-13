package uk.ac.man.cs.eventlite.dao;


import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import uk.ac.man.cs.eventlite.entities.Venue;

public interface VenueRepository extends CrudRepository<Venue, Long> {
	public Iterable<Venue> findAllByOrderByNameAsc();
	
	public Iterable<Venue> findAll();

	public Optional<Venue> findById(long id);

	public void deleteById(long id);
	
	public Iterable<Venue> findByNameContainingIgnoreCaseOrderByNameAsc(String name);
}