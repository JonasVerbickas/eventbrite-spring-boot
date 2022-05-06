package uk.ac.man.cs.eventlite.dao;


import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.man.cs.eventlite.entities.Venue;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


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
		if((venue.getAddress() != null) && venue.getPostcode() != null)
		{
			MapboxGeocoding mapboxGeocoding = MapboxGeocoding.builder()
					.accessToken(
							"pk.eyJ1Ijoiam9uYXMtIiwiYSI6ImNsMTVkMXk1eTB2aWYzYm10Zzg0djFhMHcifQ.dNCKShUv6VxLMt5ZEgW45A")
					.query(venue.getAddress())
					.build();
			mapboxGeocoding.enqueueCall(new Callback<GeocodingResponse>() {


				@Override
				public void onResponse(Call<GeocodingResponse> call,
						Response<GeocodingResponse> response) {

					System.out.println("Geocoding Success: ");
					System.out.println("Response has a body" + response.body());
					List<CarmenFeature> results = response.body().features();

					// Get the first Feature from the successful geocoding response
					List<Double> point = results.get(0).center().coordinates();
					venue.setLongitude(point.get(0));
					venue.setLatitude(point.get(1));
				}

				@Override
				public void onFailure(Call<GeocodingResponse> call, Throwable throwable) {
					System.out.println("Geocoding Failure: " + throwable.getMessage());
					venue.setLongitude(0.0);
					venue.setLatitude(0.0);
				}
			});
			try {
				// Using sleep here is inefficient
				// A List of all addresses should be maintained and fetched at the same time
				// Mapbox supports up to 50 queries per request
				Thread.sleep(1000L);
			} catch (InterruptedException e) {
				System.out.println("Sleeping Thread:" + e);
			}
		}
		return venueRepository.save(venue);
	}

	@Override
	public Optional<Venue> findById(long id){
		return venueRepository.findById(id);
	}

	@Override
	public void delete(Venue venue) {
		venueRepository.delete(venue);
	}

	@Override
	public void deleteById(long id) {
		venueRepository.deleteById(id);
		
	}
	@Override
	public Iterable<Venue> listVenueByNameIgnoreCase(String name){
		return venueRepository.findByNameContainingIgnoreCaseOrderByNameAsc(name);
	}
}
