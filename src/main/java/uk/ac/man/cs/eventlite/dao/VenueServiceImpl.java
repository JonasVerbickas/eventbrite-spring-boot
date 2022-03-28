package uk.ac.man.cs.eventlite.dao;


import java.io.InputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.man.cs.eventlite.entities.Venue;

@Service
@Transactional
public class VenueServiceImpl implements VenueService {
	
	@Autowired
	private VenueRepository venueRepository;
	
	private final static Logger log = LoggerFactory.getLogger(VenueServiceImpl.class);

	private final static String DATA = "data/venues.json";
	
	@Override
	public long count() {
		return venueRepository.count();
	}
	
	@Override
	public Iterable<Venue> findAll() {
		return venueRepository.findAll();
	}
	
	@Override
	public Iterable<Venue> findAllByOrderByNameAsc() {
		return venueRepository.findAllByOrderByNameAsc();
	}
	
	@Override
	public Venue save(Venue venue) {
		venueRepository.save(venue);
		return venue;
	}

	
	public Optional<Venue> findById(long id){
		return venueRepository.findById(id);
	}

	@Override
	public void deleteById(long id) {
		// TODO Auto-generated method stub
		
	}
	
	public Iterable<Venue> listVenueByName(String name){
		return venueRepository.findByNameContainingOrderByNameAsc(name);
	}

	
	

}
