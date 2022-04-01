package uk.ac.man.cs.eventlite.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
public class Venue {
	@Id
	@GeneratedValue
	private long id;
	@NotBlank(message = "Cannot be empty!")
	@Size(max = 256, message = "the length of the venue's name should not be more than 256 characters")
	private String name;

	private String address = "-";
	
	@Min(value = 1, message = "this venue cannot be 0 capacity")
	private int capacity;

	private double longitude;
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
	}

	public double getLatitude() {
		System.out.println("getLatitude:" + this.latitude + " for " + this.getName());
		return this.latitude;
	}

	public void setLatitude(double latitude) {
		System.out.println("setLatitude:"+latitude+" for " + this.getName());
		this.latitude = latitude;
	}

	public double getLongitude() {
		System.out.println("getLongitude:" + this.longitude + " for " + this.getName());
		return this.longitude;
	}

	public void setLongitude(double longitude) {
		System.out.println("setLongitude:"+latitude+" for " + this.getName());
		this.longitude = longitude;
	}
}
