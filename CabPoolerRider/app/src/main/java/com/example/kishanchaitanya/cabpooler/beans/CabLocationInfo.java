package com.example.kishanchaitanya.cabpooler.beans;

import java.io.Serializable;

public class CabLocationInfo implements Serializable {
	
	private String driverNumber;
	
	private double cabLatitude;
	
	private double cabLongitude;

	public String getDriverNumber() {
		return driverNumber;
	}

	public void setDriverNumber(String driverNumber) {
		this.driverNumber = driverNumber;
	}

	public double getCabLatitude() {
		return cabLatitude;
	}

	public void setCabLatitude(double cabLatitude) {
		this.cabLatitude = cabLatitude;
	}

	public double getCabLongitude() {
		return cabLongitude;
	}

	public void setCabLongitude(double cabLongitude) {
		this.cabLongitude = cabLongitude;
	}
}
