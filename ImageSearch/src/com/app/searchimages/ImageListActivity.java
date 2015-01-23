package com.app.searchimages;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class ImageListActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_list_activity);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.image_list_container, new ImageListFragment()).commit();
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class ImageListFragment extends Fragment implements OnScrollListener, OnItemClickListener, AsyncTaskResponseListener {

		private String searchText;
		private Activity activity;
		private View rootView;
		private ImageListAdapter adapter;
		private List<Bitmap> imageBitmaps;
		private boolean lastItemShown = false;

		public ImageListFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			rootView = inflater.inflate(R.layout.image_list_fragment, container, false);

			activity = getActivity();

			Bundle extras = activity.getIntent().getExtras();
			if (extras != null) {
				searchText = extras.getString(Utils.SEARCH_TEXT);
				if (Utils.isConnected(getActivity())) {

					String[] searchParams = new String[3];
					searchParams[0] = Utils.BASE_URL;
					searchParams[1] = searchText;
					searchParams[2] = "&start=0";

					GetImagesTask getImage = new GetImagesTask(activity, this);
					getImage.execute(searchParams);
				} else {
					Toast.makeText(activity, getResources().getString(R.string.no_connection_text), Toast.LENGTH_SHORT).show();
				}
			}

			return rootView;
		}

		@SuppressWarnings("unchecked")
		public void onImageUrlsReceived(ArrayList<String> urls) {
			GetImageFromUrlTask getImageFromUrl = new GetImageFromUrlTask(activity, this);
			getImageFromUrl.execute(urls);
		}

		public void onImagesReceived(List<Bitmap> bmps) {
			if (imageBitmaps == null) {
				imageBitmaps = new ArrayList<Bitmap>();

				ListView imageList = (ListView) rootView.findViewById(R.id.image_list);
				imageList.setOnScrollListener(this);
				imageList.setOnItemClickListener(this);

				adapter = new ImageListAdapter(activity, imageBitmaps);
				imageList.setAdapter(adapter);
			}

			imageBitmaps.addAll(bmps);
			adapter.notifyDataSetChanged();

			lastItemShown = false;
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if (view.getId() == R.id.image_list 
					&& !lastItemShown 
					&& totalItemCount > 0 
					&& (firstVisibleItem + visibleItemCount == totalItemCount)) {
				
				//Log.d("SearchImages", "i am scrolled all the way");
				lastItemShown = true;

				String[] searchParams = new String[3];
				searchParams[0] = Utils.BASE_URL;
				searchParams[1] = searchText;
				searchParams[2] = "&start=" + imageBitmaps.size();

				GetImagesTask getMoreImages = new GetImagesTask(activity, this);
				getMoreImages.execute(searchParams);
			}

		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Bitmap imgBmp = (Bitmap) parent.getAdapter().getItem(position);

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			imgBmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			byte[] byteArray = stream.toByteArray();

			Intent intent = new Intent(getActivity(), FullScreenImageActivity.class);
			intent.putExtra(Utils.BITMAP_IN_BYTES, byteArray);
			startActivity(intent);
		}
	}
}
