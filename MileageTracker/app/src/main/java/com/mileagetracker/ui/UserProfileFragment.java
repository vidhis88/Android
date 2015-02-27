package com.mileagetracker.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.mileagetracker.R;
import com.mileagetracker.dao.MileageRecord;
import com.mileagetracker.dao.MileageTrackerDB;
import com.mileagetracker.dao.VehicleDTO;

/**
 * Created by vshah2 on 2/26/15.
 */
public class UserProfileFragment extends Fragment implements View.OnClickListener {
	private View rootView;

	public UserProfileFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.user_profile_fragment, container, false);
		rootView.findViewById(R.id.save_vehicle_info_btn).setOnClickListener(this);

		getActivity().setTitle("Vehicle Profile");

		return rootView;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.save_vehicle_info_btn) {
			VehicleDTO vehicle = new VehicleDTO();

			EditText ed = (EditText) rootView.findViewById(R.id.current_mpg_ed);
			if (ed != null) {
				float initialMPG = Float.parseFloat(String.valueOf(ed.getText()));
				vehicle.setInitialMPG(initialMPG);
			}

			ed = (EditText) rootView.findViewById(R.id.make_ed);
			if (ed != null) {
				String make = String.valueOf(ed.getText());
				vehicle.setMake(make);
			}

			ed = (EditText) rootView.findViewById(R.id.model_ed);
			if (ed != null) {
				String model = String.valueOf(ed.getText());
				vehicle.setModel(model);
			}

			ed = (EditText) rootView.findViewById(R.id.year_ed);
			if (ed != null) {
				String year = String.valueOf(ed.getText());
				if (!TextUtils.isEmpty(year)) {
					vehicle.setYear(Integer.parseInt(year));
				}
			}

			Activity activity = getActivity();

			ed = (EditText) rootView.findViewById(R.id.name_ed);
			if (ed != null) {
				String name = String.valueOf(ed.getText());
				if (TextUtils.isEmpty(name)) {
					name = vehicle.getMake();

					if (TextUtils.isEmpty(name)) {
						int vehicleIndex = MileageTrackerDB.getInstance(activity).getNumVehicles() + 1;
						name = "Vehicle " + String.valueOf(vehicleIndex);
					}
				}
				vehicle.setName(name);
			}

			long vehicleId = MileageTrackerDB.getInstance(activity).addVehicleInfo(vehicle);

			MileageRecord record = new MileageRecord();
			record.setVehicleId(vehicleId);
			record.setMpg(vehicle.getInitialMPG());
			MileageTrackerDB.getInstance(activity).addRecord(record);

			Intent intent = new Intent();
			intent.setClassName(activity, MainActivity.class.getName());
			startActivity(intent);
		}
	}
}
