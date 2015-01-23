package com.mileagetracker.dao;

/**
 * Created by vshah2 on 10/28/14.
 */
public class MileageRecord {

	private int odometerReading;
	private float gallonsFilled;
	private float amount;
	private long vehicleId;
	private String fuelBrand;
	private float mpg;
	private long entryTimeMillis;

	public int getOdometerReading() {
		return odometerReading;
	}

	public void setOdometerReading(int odometerReading) {
		this.odometerReading = odometerReading;
	}

	public float getGallonsFilled() {
		return gallonsFilled;
	}

	public void setGallonsFilled(float gallonsFilled) {
		this.gallonsFilled = gallonsFilled;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public String getFuelBrand() {
		return fuelBrand;
	}

	public void setFuelBrand(String fuelBrand) {
		this.fuelBrand = fuelBrand;
	}

	public long getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(long vehicleId) {
		this.vehicleId = vehicleId;
	}

	public float getMpg() {
		return mpg;
	}

	public void setMpg(float mpg) {
		this.mpg = mpg;
	}

	public long getEntryTimeMillis() {
		return entryTimeMillis;
	}

	public void setEntryTimeMillis(long entryTimeMillis) {
		this.entryTimeMillis = entryTimeMillis;
	}
}
