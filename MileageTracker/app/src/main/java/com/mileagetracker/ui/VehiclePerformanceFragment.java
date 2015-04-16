package com.mileagetracker.ui;

import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mileagetracker.R;
import com.mileagetracker.dao.MileageRecord;
import com.mileagetracker.dao.MileageTrackerDB;
import com.mileagetracker.ui.components.LineGraph;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

		if (Build.VERSION.SDK_INT >= 21) {
			rootView.findViewById(R.id.fab_button).setBackgroundResource(R.drawable.ripple);
		}

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

		List<MileageRecord> recs = MileageTrackerDB.getInstance(getActivity()).getAllRecordsByVehicleId(1);
		Collections.sort(recs, new Comparator<MileageRecord>() {
			@Override
			public int compare(MileageRecord lhs, MileageRecord rhs) {
				return lhs.getEntryTimeMillis() > rhs.getEntryTimeMillis() ? -1 : 1;
			}
		});

		int i = 0;
		int numMpgValues = Math.min(recs.size(), 6);
		float[] mpgValues = new float[numMpgValues];
		for (MileageRecord rec : recs) {
			if (i > 5) break;
			mpgValues[i++] = rec.getMpg();
		}

		LineGraph lineGraph = (LineGraph) rootView.findViewById(R.id.perf_graph);
		lineGraph.setColor(getResources().getColor(android.R.color.holo_green_dark));
		lineGraph.setPoints(mpgValues);//new float[]{20f, 20f, 50f, 55f, 50f, 55f, 130f, 245f, 130f, 245f, 290f, 170f});
	}

	@Override
	public void onClick(View v) {
		Log.w("Vidhi", "in onClick");

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
