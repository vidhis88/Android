package com.app.searchimages;

import java.util.Set;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_clear_history) {
			Utils.removeSharedPrefsItem(this, Utils.SEARCH_HISTORY_PREF);
			this.recreate();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment implements OnClickListener, OnItemClickListener {

		private EditText searchText;
		private Button searchBtn;
		private ListView searchHistLV;
		private ArrayAdapter<String> searchHistoryAdapter;

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.main_fragment, container, false);

			searchText = (EditText) rootView.findViewById(R.id.search_input);
			searchBtn = (Button) rootView.findViewById(R.id.search_btn);
			searchBtn.setOnClickListener(this);

			searchHistLV = (ListView) rootView.findViewById(android.R.id.list);
			searchHistLV.setOnItemClickListener(this);

			return rootView;
		}

		@Override
		public void onResume() {
			super.onResume();

			Set<String> searchSet = Utils.getSharedPrefsSet(getActivity(), Utils.SEARCH_HISTORY_PREF);
			if (searchHistLV != null && searchSet != null && searchSet.size() > 0) {
				String[] searchArray = searchSet.toArray(new String[searchSet.size()]);
				searchHistoryAdapter = new ArrayAdapter<String>(getActivity(), R.layout.search_hist_list_item, searchArray);
				searchHistLV.setAdapter(searchHistoryAdapter);
			}
		}

		@Override
		public void onClick(View v) {
			Editable ed = searchText.getText();

			if (TextUtils.isEmpty(ed)) {
				Toast toastMsg = Toast.makeText(getActivity(), R.string.invalid_empty_text, Toast.LENGTH_SHORT);
				toastMsg.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toastMsg.show();
			} else {
				Utils.addToSharedPrefsSet(getActivity(), Utils.SEARCH_HISTORY_PREF, ed.toString());
				launchImageListActivity(ed.toString());
			}
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Object o = parent.getAdapter().getItem(position);
			if (o != null) {
				String searchTxt = String.valueOf(o);
				launchImageListActivity(searchTxt);
			}
		}

		private void launchImageListActivity(String searchTxt) {
			Intent intent = new Intent();
			intent.setClass(getActivity(), ImageListActivity.class);
			intent.putExtra(Utils.SEARCH_TEXT, searchTxt);
			startActivity(intent);
		}
	}
}
