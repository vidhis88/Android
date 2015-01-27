package com.mileagetracker.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.mileagetracker.R;
import com.mileagetracker.dao.MileageRecord;
import com.mileagetracker.dao.MileageTrackerDB;
import com.mileagetracker.dao.VehicleDTO;


public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		requestWindowFeature(WindowCompat.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolBar);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (MileageTrackerDB.getInstance(this).getNumVehicles() > 0) {
			getFragmentManager()
					.beginTransaction()
					.add(R.id.container, new MainFragment())
					.commit();
		} else {
			getFragmentManager()
					.beginTransaction()
					.add(R.id.container, new UserProfileFragment())
					.commit();
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
//		if (id == R.id.action_settings) {
//			return true;
//		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class MainFragment extends Fragment implements View.OnClickListener {

		private View rootView;

		public MainFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			rootView = inflater.inflate(R.layout.main_fragment, container, false);
			rootView.findViewById(R.id.fab_button).setOnClickListener(this);
			rootView.findViewById(R.id.avg_mpg_layout).setOnClickListener(this);

			getActivity().setTitle("MPG");

			return rootView;
		}

		@Override
		public void onResume() {
			super.onResume();

			String vehicleName = MileageTrackerDB.getInstance(getActivity()).getVehicleNameById(1);
			if (TextUtils.isEmpty(vehicleName)) {
				vehicleName = "Your vehicle";
			}
			((TextView) rootView.findViewById(R.id.title_text)).setText(vehicleName + "'s average MPG (Miles per Gallon):");
			float mpg = MileageTrackerDB.getInstance(getActivity()).getAvgMpg();
			mpg = Math.round(mpg);
			((TextView) rootView.findViewById(R.id.average_mpg)).setText(String.valueOf(mpg));
		}

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.fab_button) {
				Intent intent = new Intent();
				intent.setClassName(getActivity(), EnterMileageDetailsActivity.class.getName());
				startActivity(intent);
			} else if (v.getId() == R.id.avg_mpg_layout) {
				Intent intent = new Intent();
				intent.setClassName(getActivity(), MileageEntryListActivity.class.getName());
				startActivity(intent);
			}
		}
	}

	/**
	 * Another placeholder fragment containing a simple view.
	 */
	public static class UserProfileFragment extends Fragment implements View.OnClickListener {

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
					if (!TextUtils.isEmpty(year)){
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
}
