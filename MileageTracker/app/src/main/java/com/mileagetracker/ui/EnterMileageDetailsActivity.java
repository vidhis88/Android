package com.mileagetracker.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.mileagetracker.R;
import com.mileagetracker.dao.MileageRecord;
import com.mileagetracker.dao.MileageTrackerDB;

import java.util.Calendar;

/**
 * Created by vshah2 on 10/27/14.
 */
public class EnterMileageDetailsActivity extends ActionBarActivity implements View.OnClickListener {

	private EditText odometerReadingEd;
	private EditText milesDrivenEd;
	private EditText gallonsEd;
	private EditText amountEd;
	private EditText fuelBrandEd;
	private TextView dateTV;
	private TextView milesTV;
	private TextView mpgTV;

	private int numRecords;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.enter_mileage_details_activity);

		odometerReadingEd = (EditText) findViewById(R.id.odometer_reading);
		milesDrivenEd = (EditText) findViewById(R.id.miles_driven_ed);
		gallonsEd = (EditText) findViewById(R.id.gallons);
		amountEd = (EditText) findViewById(R.id.amount);
		fuelBrandEd = (EditText) findViewById(R.id.fuel_brand);

		dateTV = (TextView) findViewById(R.id.date);
		milesTV = (TextView) findViewById(R.id.miles_driven);
		mpgTV = (TextView) findViewById(R.id.mpg);

		Calendar calendar = Calendar.getInstance();
		dateTV.setText(String.valueOf(calendar.getTime()));

		findViewById(R.id.save_btn).setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		numRecords = MileageTrackerDB.getInstance(this).getNumRecords();
		if (numRecords == 1) {
			findViewById(R.id.miles_driven_layout).setVisibility(View.GONE);
			milesDrivenEd.setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.miles_driven_layout).setVisibility(View.VISIBLE);
			milesDrivenEd.setVisibility(View.GONE);
		}

		super.onResume();
	}

	@Override
	public void onClick(View v) {
		int odoReading = Integer.parseInt(odometerReadingEd.getText().toString());
		float gallonsFilled = Float.parseFloat(gallonsEd.getText().toString());

		if (v.getId() == R.id.save_btn) {
			MileageRecord record = new MileageRecord();
			record.setOdometerReading(odoReading);
			record.setGallonsFilled(gallonsFilled);
			record.setAmount(Float.parseFloat(amountEd.getText().toString()));
			record.setFuelBrand(fuelBrandEd.getText().toString());
			record.setVehicleId(1);

			MileageRecord lastRecord = MileageTrackerDB.getInstance(this).getLastRecord();
			if (numRecords > 1 && lastRecord != null) {
				int milesDriven = odoReading - lastRecord.getOdometerReading();
				milesTV.setText(String.valueOf(milesDriven));

				//TODO: check for gallonsFilled to be > 0 before allowing them to save data
				float mpgRatio = milesDriven / gallonsFilled;
				mpgTV.setText(String.valueOf(mpgRatio));

				record.setMpg(mpgRatio);
			} else {
				String milesDrivenStr = String.valueOf(milesDrivenEd.getText());
				if (!TextUtils.isEmpty(milesDrivenStr)) {
					int milesDriven = Integer.parseInt(milesDrivenStr);
					float mpgRatio = milesDriven / gallonsFilled;
					mpgTV.setText(String.valueOf(mpgRatio));

					record.setMpg(mpgRatio);
				} else {
					float initialMPG = MileageTrackerDB.getInstance(this.getApplicationContext()).getVehicleInitialMPG(1);
					mpgTV.setText(String.valueOf(initialMPG));
					record.setMpg(initialMPG);
				}
			}

			MileageTrackerDB.getInstance(this.getApplicationContext()).addRecord(record);
		}
	}
}
