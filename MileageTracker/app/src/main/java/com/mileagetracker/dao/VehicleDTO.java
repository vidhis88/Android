package com.mileagetracker.dao;

/**
 * Created by vshah2 on 11/14/14.
 */
public class VehicleDTO {

	private String make;
	private String model;
	private int year;
	private String name;
	private float initialMPG;

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getInitialMPG() {
		return initialMPG;
	}

	public void setInitialMPG(float initialMPG) {
		this.initialMPG = initialMPG;
	}
}
