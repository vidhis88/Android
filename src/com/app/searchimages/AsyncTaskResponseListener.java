package com.app.searchimages;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;

public interface AsyncTaskResponseListener {
	
	void onImageUrlsReceived(ArrayList<String> imageUrls);
	void onImagesReceived(List<Bitmap> imageBitmaps);

}
