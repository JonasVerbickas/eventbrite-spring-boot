package uk.ac.man.cs.eventlite.entities;

import javax.persistence.Entity; 
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
public class Venue {
    @Id
    @GeneratedValue
    private long id;

    @NotBlank(message = "The venue name cannot be empty!")
    @Size(max=50, message="The venue name can not exceed 50 characters.")
    private String name;

   @NotNull(message = "The venue capacity cannot be empty!")
    @Min(value=1, message="The venue can not have a capacity less than 1 person.")
    private int capacity;

    @NotBlank(message = "The venue address cannot be empty!")
    @Size(max=500, message="The venue address can not exceed 500 characters.")
    private String address = "-";

    @NotBlank(message = "The venue postcode cannot be empty!")
//    @Size(max=7, message="The venue postcode must be in the format XXX XXX.")
    @Pattern(regexp ="^([Gg][Ii][Rr] 0[Aa]{2})|((([A-Za-z][0-9]{1,2})|(([A-Za-z][A-Ha-hJ-Yj-y][0-9]{1,2})|(([AZa-z][0-9][A-Za-z])|([A-Za-z][A-Ha-hJ-Yj-y][0-9]?[A-Za-z])))) [0-9][A-Za-z]{2})$", message="The venue postcode must be in the format XXX XXX.")
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

	public String getAddresswithPostcode() {
		String addrWithZip = address + this.getPostcode();
		return addrWithZip;
	}
	
	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public double getLatitude() {
		System.out.println("getLatitude:" + this.latitude + " for " + this.getName());
		return this.latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		System.out.println("getLongitude:" + this.longitude + " for " + this.getName());
		return this.longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }
    
	public static boolean checkName(Venue venue) {
		//name cannot longer than 256, cannot be null, the length of name cannot be 0
		if (venue.name.length() > 255 || venue.name == null || venue.name.length() == 0) {
			return false;
		}
		return true; 
		
	}
	
	public static boolean checkAddr(Venue venue) {
		//Same as name
		if (venue.address == null|| venue.address.length() == 0|| venue.address.length() >= 300) {
			return false;
		} 
		return true;
	}
	
	public static boolean checkCapacity(Venue venue) {
		//should be a positive integer
		if (venue.capacity <= 0 || !(Math.round(venue.capacity) == venue.capacity)) {
			return false;
		} 
		return true;
	}
	
	
	public static String checkAll(Venue venue) {
		if(!checkName(venue)) {
			String name_error_msg = "event name is invalid";
			return name_error_msg;
		}
		
		if(!checkAddr(venue)){
			String addr_error_msg = "event venue's address is invalid";
			return addr_error_msg;
		}
		
		if(!checkCapacity(venue)) {
			String cap_error_msg = "capacity's is invalid";
			return cap_error_msg;
		}
		
		return "All pass";
	}

	
	
}
