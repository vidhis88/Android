package com.mileagetracker.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mileagetracker.R;
import com.mileagetracker.dao.MileageRecord;
import com.mileagetracker.dao.MileageTrackerDB;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by vshah2 on 11/17/14.
 */
public class MileageEntryListActivity extends ActionBarActivity {

	private RecyclerView entryListRecyclerView;
	private RecyclerView.Adapter adapter;
	private RecyclerView.LayoutManager linearLayoutManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mileage_entry_list_activity);

		List<MileageRecord> mileageEntries = MileageTrackerDB.getInstance(this).getAllRecordsByVehicleId(1);

		entryListRecyclerView = (RecyclerView) findViewById(R.id.entry_list_recycler_view);
		if (mileageEntries != null && entryListRecyclerView != null) {
			entryListRecyclerView.setHasFixedSize(true);
			entryListRecyclerView.addItemDecoration(new DividerItemDecoration(this));

			linearLayoutManager = new LinearLayoutManager(this);
			entryListRecyclerView.setLayoutManager(linearLayoutManager);

			adapter = new EntryListAdapter(mileageEntries);
			entryListRecyclerView.setAdapter(adapter);
		}
	}

	public class EntryListAdapter extends RecyclerView.Adapter<EntryListAdapter.ViewHolder> {
		private List<MileageRecord> records;

		public EntryListAdapter(List<MileageRecord> records) {
			this.records = records;
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mileage_entry_item, parent, false);
			ViewHolder viewHolder = new ViewHolder(view);
			return viewHolder;
		}

		@Override
		public void onBindViewHolder(ViewHolder viewHolder, int position) {
			MileageRecord record = records.get(position);

			viewHolder.avgMpgTV.setText(String.valueOf(record.getMpg()));
			viewHolder.brandTV.setText(record.getFuelBrand());
			viewHolder.amountTV.setText(String.valueOf(record.getAmount()));

			Date entryDate = new Date(record.getEntryTimeMillis());
			String date = new SimpleDateFormat("MMM dd", Locale.US).format(entryDate);
			try {
				if (entryDate.before(new SimpleDateFormat("MM dd yyyy", Locale.US).parse("Jan 01, " + Calendar.getInstance().get(Calendar.YEAR)))) {
					date = new SimpleDateFormat("MMM dd, yyyy", Locale.US).format(entryDate);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			viewHolder.dateTV.setText(date);
		}

		@Override
		public int getItemCount() {
			return records.size();
		}

		public class ViewHolder extends RecyclerView.ViewHolder {
			TextView avgMpgTV;
			TextView dateTV;
			TextView brandTV;
			TextView amountTV;

			public ViewHolder(View itemView) {
				super(itemView);

				avgMpgTV = (TextView) itemView.findViewById(R.id.avg_mpg_tv);
				dateTV = (TextView) itemView.findViewById(R.id.date_tv);
				brandTV = (TextView) itemView.findViewById(R.id.fuel_brand_tv);
				amountTV = (TextView) itemView.findViewById(R.id.amount_tv);
			}
		}
	}

	public class DividerItemDecoration extends RecyclerView.ItemDecoration {

		private final int[] ATTRS = new int[]{
				android.R.attr.listDivider
		};

		private Drawable mDivider;

		public DividerItemDecoration(Context context) {
			final TypedArray a = context.obtainStyledAttributes(ATTRS);
			mDivider = a.getDrawable(0);
			a.recycle();
		}

		@Override
		public void onDraw(Canvas c, RecyclerView parent) {
			drawVertical(c, parent);
		}

		public void drawVertical(Canvas c, RecyclerView parent) {
			final int left = parent.getPaddingLeft();
			final int right = parent.getWidth() - parent.getPaddingRight();

			final int childCount = parent.getChildCount();
			for (int i = 0; i < childCount; i++) {
				final View child = parent.getChildAt(i);
				final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
						.getLayoutParams();
				final int top = child.getBottom() + params.bottomMargin +
						Math.round(ViewCompat.getTranslationY(child));
				final int bottom = top + mDivider.getIntrinsicHeight();
				mDivider.setBounds(left, top, right, bottom);
				mDivider.draw(c);
			}
		}

		@Override
		public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
			outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
		}
	}
}
