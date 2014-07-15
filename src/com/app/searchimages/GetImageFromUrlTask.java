package com.app.searchimages;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public class GetImageFromUrlTask extends AsyncTask<ArrayList<String>, Void, List<Bitmap>> {

	private Context context;
	private ProgressDialog dialog;
	private AsyncTaskResponseListener listener;

	public GetImageFromUrlTask(Context context, AsyncTaskResponseListener listener) {
		this.context = context;
		this.listener = listener;
	}

	@Override
	protected List<Bitmap> doInBackground(ArrayList<String>... imageUrlArray) {
		ArrayList<String> imageUrls = imageUrlArray[0];

		List<Bitmap> bitmaps = new ArrayList<Bitmap>();
		for (int i = 0; i < imageUrls.size(); i++) {
			try {
				URL url = new URL(imageUrls.get(i));
				InputStream input = url.openStream();

				//Download images
				Bitmap bmp = BitmapFactory.decodeStream(input);
				bitmaps.add(bmp);

				input.close();
			}  catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bitmaps;
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
	protected void onPostExecute(List<Bitmap> bmps) {
		if (dialog != null) {
			dialog.dismiss();
		}

		// Now that we have the list of file path to the downloaded images, pass it to the interface 
		listener.onImagesReceived(bmps);
	}
}