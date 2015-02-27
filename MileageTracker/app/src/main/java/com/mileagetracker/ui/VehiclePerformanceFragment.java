package com.mileagetracker.ui;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mileagetracker.R;
import com.mileagetracker.dao.MileageTrackerDB;

/**
 * Created by vshah2 on 2/26/15.
 */
public class VehiclePerformanceFragment extends Fragment implements View.OnClickListener {
	private View rootView;

	public VehiclePerformanceFragment() {
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
