package uk.ac.man.cs.eventlite.entities;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.persistence.GeneratedValue;

@Entity
public class Event {

	@Id
	@GeneratedValue
	private long id;
	

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@DateTimeFormat(pattern = "yyyy-MM-dd")

	private LocalDate date;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@DateTimeFormat(pattern = "HH:mm")
	private LocalTime time;
	@NotBlank(message = "Cannot be empty!")
	@Size(max = 256, message = "the length of the venue's name should not be more than 256 characters")
	private String name;

	
	@Size(max = 500, message = "Description should not have more than 500 characters")
	@Column(length = 100000)
	private String description;
	
	
	@ManyToOne
	private Venue venue;

	public Event() {
	}

	public Event(String name, Venue venue, LocalDate date, LocalTime time,String description) {
		this.name = name;
		this.venue = venue;
		this.date = date;
		this.time = time;
		this.description = description;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalTime getTime() {
		return time;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Venue getVenue() {
		return venue;
	}

	public void setVenue(Venue venue) {
		this.venue = venue;
	}
	
	public String toString(String name) {
		return String.format(this.name, name);
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public static boolean checkName(Event event) {
		if (event.name.length() > 255 || event.name == null || event.name.length() == 0) {
			return false;
		}
		return true; 
		
	}
	
	public static boolean checkVenue(Event event) {
		if (event.venue == null) {
			return false;
		} 
		return true;
	}
	
	public static boolean checkDescription(Event event) {
		if (event.description == null || event.description.length() == 0 || event.description.length() > 500)  {
			return false;
		} 
		return true;
	}
	
	public static boolean checkDate(Event event) {
		if (event.date == null) {
			return false;
		} 
		return true;
	}
	
	public static boolean checkTime(Event event) {
		if (event.time == null) {
			return false;
		} 
		return true;
	}
	
	public static boolean checkisFuture(Event event) {
		if(event.date == null) {
			return false;
		}
		LocalDate enterDate = LocalDate.now();
		boolean before = event.date.isBefore(enterDate);
		
		return !before;
	}
	
	public static String checkAll(Event event) {
		if(!checkName(event)) {
			String name_error_msg = "event name is invalid";
			return name_error_msg;
		}
		
		if(!checkVenue(event)){
			String venue_error_msg = "event venue is invalid";
			return venue_error_msg;
		}
		
		if(!checkDescription(event)) {
			String des_error_msg = "event description is invalid";
			return des_error_msg;
		}
		
		if(!checkDate(event)) {
			String date_error_msg = "event date is invalid";
			return date_error_msg;
		}
		
		if(!checkTime(event)) {
			String time_error_msg = "event time is invalid";
			return time_error_msg;
		}
		
		if(!checkisFuture(event)) {
			String future_error_msg = "event should happen in the future!!";
			return future_error_msg;
		}
		return "";
	}

}
