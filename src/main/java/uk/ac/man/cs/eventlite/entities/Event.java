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
import javax.validation.constraints.Future;
import javax.validation.constraints.Size;
import javax.persistence.GeneratedValue;

@Entity
public class Event {

	@Id
	@GeneratedValue
	private long id;
	

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Future(message = "Date Has To Be In The Future")
	
	private LocalDate date;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@DateTimeFormat(pattern = "HH:mm")
	private LocalTime time;
	@NotBlank(message = "Cannot be empty!")
	@Size(max = 256, message = "the length of the events name should not be more than 256 characters")
	private String name;

	
	@Size(max = 500, message = "Description should not have more than 500 characters")
	@Column(length = 100000)
	private String description;
	
	private String errorMsg;
	
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
	
	public void setErrorMsg(String err) {
		this.errorMsg = err;
	}
	
	public String getErrorMsg() {
		return this.errorMsg;
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
			String name_err_msg = "";
			if(event.getName() == null) {
				name_err_msg = "event name invalid";
				event.setErrorMsg("event name cannot be null!"); 
			}else if(event.getName().length()>255) {
				event.setErrorMsg("event name should within 256 chars!"); 
			}else {
				event.setErrorMsg("your event name invalid for unknow reason. Make sure you fill a name within 256 chars!!!");
				
			}
			
			//return event.getErrorMsg();
			return name_err_msg;
		}
		
		if(!checkVenue(event)){
			String venue_error_msg = "Select a Venue!!!";
			event.setErrorMsg("Select a Venue!!!");
			return event.getErrorMsg();
		}
		
		if(!checkDescription(event)) {
			if(event.getDescription() == null) {
				event.setErrorMsg("event description cannot be null");
			}else if(event.getName().length()>=500) {
				event.setErrorMsg("event description should within 500 chars!");
			}else {
				event.setErrorMsg("your description name invalid for unknow reason. Make sure you fill a name within 500 chars!!!");
			}
			return event.getErrorMsg();
		}
		
		if(!checkDate(event)) {
			event.setErrorMsg("Enter a Date");
			return event.getErrorMsg();
		}
		
		if(!checkTime(event)) {
			event.setErrorMsg("event time is invalid");
			return event.getErrorMsg();
		}
		
		if(!checkisFuture(event)) {
			event.setErrorMsg("event should happen in the future!!");
			return event.getErrorMsg();
		}
		return "All pass";
	}
	
//	public ActionResult myAction(message) {
//		return JavaScript(window.alert(message));
//	}

}
