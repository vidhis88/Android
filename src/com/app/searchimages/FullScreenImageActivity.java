package com.app.searchimages;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

public class FullScreenImageActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.full_screen_image_activity);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			byte[] byteArray = extras.getByteArray(Utils.BITMAP_IN_BYTES);

			Bitmap imageBmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
			ImageView fullImageView = (ImageView) findViewById(R.id.full_image);
			fullImageView.setImageBitmap(imageBmp);
		}
	}
}
