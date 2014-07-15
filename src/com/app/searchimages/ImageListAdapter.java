package com.app.searchimages;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageListAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<Bitmap> imageBmps;
	
	public ImageListAdapter(Context context, List<Bitmap> imageBmps) {
		this.inflater = LayoutInflater.from(context);
		this.imageBmps = imageBmps;
	}
	
	@Override
	public int getCount() {
		return imageBmps.size();
	}

	@Override
	public Bitmap getItem(int position) {
		return imageBmps.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.image_list_item, null);
		}
		
		ImageView imageView = (ImageView) convertView.findViewById(R.id.image_item);
		
		Bitmap image = imageBmps.get(position);
		imageView.setImageBitmap(ThumbnailUtils.extractThumbnail(image, parent.getWidth(), 400));
		
		return convertView;
	}

}
