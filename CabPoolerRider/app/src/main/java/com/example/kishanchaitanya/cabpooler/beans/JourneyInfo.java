package com.example.kishanchaitanya.cabpooler.beans;

import java.io.Serializable;
import java.sql.Timestamp;

public class JourneyInfo implements Serializable {
	
	private String journeyNumber;
	
	private String rideNumber;
	
	private String driverNumber;
	
	private String userNumber;
	
	private String source;
	
	private String destination;
	
	private String distance;
	
	private String cost;
	
	private String status;
	
	private Timestamp startTime;
	
	private Timestamp endTime;

	public String getJourneyNumber() {
		return journeyNumber;
	}

	public void setJourneyNumber(String journeyNumber) {
		this.journeyNumber = journeyNumber;
	}

	public String getRideNumber() {
		return rideNumber;
	}

	public void setRideNumber(String rideNumber) {
		this.rideNumber = rideNumber;
	}

	public String getDriverNumber() {
		return driverNumber;
	}

	public void setDriverNumber(String driverNumber) {
		this.driverNumber = driverNumber;
	}

	public String getUserNumber() {
		return userNumber;
	}

	public void setUserNumber(String userNumber) {
		this.userNumber = userNumber;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}
	
}
