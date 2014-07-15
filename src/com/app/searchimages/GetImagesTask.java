package com.app.searchimages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class GetImagesTask extends AsyncTask<String, Void, JSONObject> {

	private Context context;
	private ProgressDialog dialog;
	private AsyncTaskResponseListener listener;

	@Override
	protected JSONObject doInBackground(String... params) {
		try {
			String urlString = params[0];
			String searchStr = params[1];
			String startIndex = params[2];

			String encodedString = URLEncoder.encode(searchStr, "UTF-8");
			URL url = new URL(urlString + encodedString + startIndex);
			URLConnection connection = url.openConnection();
			connection.addRequestProperty("Referer", "www.slack.com");

			String line;
			StringBuilder builder = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			while((line = reader.readLine()) != null) {
				builder.append(line);
			}

			return new JSONObject(builder.toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	public GetImagesTask(Context context, AsyncTaskResponseListener listener) {
		this.context = context;
		this.listener = listener;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		dialog = new ProgressDialog(context);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setMessage(context.getResources().getString(R.string.wait_text));
		dialog.show();
	}

	@Override
	protected void onPostExecute(JSONObject result) {
		super.onPostExecute(result);
		try {
			if (dialog != null) {
				dialog.dismiss();
			}
			ArrayList<String> imageUrls = new ArrayList<String>();

			//Log.i("SearchImages", "response json is " + result);

			JSONObject responseObject = result.getJSONObject("responseData");
			JSONArray resultArray = responseObject.getJSONArray("results");
			for (int i = 0 ; i <resultArray.length() ; i++) {
				imageUrls.add(resultArray.getJSONObject(i).getString("url"));
			}
			// Now that we have complete list of URL's, pass the list to the interface
			listener.onImageUrlsReceived(imageUrls);				
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
