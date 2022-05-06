package uk.ac.man.cs.eventlite.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Entity
public class Venue {
    @Id
    @GeneratedValue
    private long id;

    @NotEmpty(message="The venue name cannot be empty.")
    @Size(max=50, message="The venue name can not exceed 50 characters.")
    private String name;

    @NotEmpty(message="The venue capacity cannot be empty.")
    @Min(value=5, message="The venue can not have a capacity less than 5 people.")
    private int capacity;

    @NotEmpty(message="The venue address cannot be empty.")
    @Size(max=500, message="The venue address can not exceed 500 characters.")
    private String address = "-";

    @NotEmpty(message="The venue postcode cannot be empty.")
    @Size(max=7, message="The venue postcode must be in the format XXX XXX.")
    private String postcode;

    private double latitude;
    private double longitude;


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
	

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }
	
	
}
