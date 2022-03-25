package uk.ac.man.cs.eventlite.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;

import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.core.exceptions.ServicesException;
import com.mapbox.geojson.Point;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Entity
public class Venue {
	@Id
	@GeneratedValue
	private long id;

	private String name;

	private String address = "-";

	private int capacity;

	private double latitude;

	public Venue() {
	}

	public Venue(String name_in) {
		this.name = name_in;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {

		this.address = address;
		MapboxGeocoding mapboxGeocoding = MapboxGeocoding.builder()
				.accessToken("pk.eyJ1Ijoiam9uYXMtIiwiYSI6ImNsMTVkMXk1eTB2aWYzYm10Zzg0djFhMHcifQ.dNCKShUv6VxLMt5ZEgW45A")
				.query(address)
				.build();
		mapboxGeocoding.enqueueCall(new Callback<GeocodingResponse>() {
			@Override
			public void onResponse(Call<GeocodingResponse> call,
					Response<GeocodingResponse> response) {
				System.out.println("Geocoding Success: ");
				if (response.body() != null) {
					System.out.println("Response has a body" + response.body());
					List<CarmenFeature> results = response.body().features();

						// Get the first Feature from the successful geocoding response
						double point = results.get(0).center().coordinates().get(1);
						setLatitude(point);
				}
				else
				{
					System.out.println("NO BODY:" +response);
				}
			}
			
			@Override
			public void onFailure(Call<GeocodingResponse> call, Throwable throwable) {
				System.out.println("Geocoding Failure: " + throwable.getMessage());
			}
		});
	}

	public double getLatitude() {
		System.out.println("getLatitude:" + this.latitude + " for " + this.getName());
		return this.latitude;
	}

	public void setLatitude(double latitude) {
		System.out.println("setLatitude:"+latitude+" for " + this.getName());
		this.latitude = latitude;
	}
}
