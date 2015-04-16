package com.mileagetracker.ui.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.mileagetracker.R;

/**
 * Created by vshah2 on 2/26/15.
 */
public class LineGraph extends View {
	private float[] origPoints;
	private float[] scaledPoints;
	private int color;
	private int width;

	private Paint paint;
	private Paint cutOffPaint;

	public LineGraph(Context context) {
		this(context, null);
	}

	public LineGraph(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public LineGraph(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		paint = new Paint();
		paint.setStrokeWidth(5);

//		DashPathEffect dashPathEffect = new DashPathEffect(new float[]{20, 20}, 0);
		cutOffPaint = new Paint();
		cutOffPaint.setColor(context.getResources().getColor(R.color.cut_off_color));
		cutOffPaint.setStrokeWidth(5);
		cutOffPaint.setStyle(Paint.Style.STROKE);
//		cutOffPaint.setPathEffect(dashPathEffect);
	}

	@Override
	public void onDraw(Canvas canvas) {
		if (scaledPoints != null && scaledPoints.length > 0 && paint != null) {
			int cHt = canvas.getHeight();
			int cWt = canvas.getWidth();

			paint.setColor(color);

			for (int i = 0; i < scaledPoints.length; i+=4) {
				canvas.drawLine(scaledPoints[i], (cHt - 200f - scaledPoints[i+1]), scaledPoints[i+2], (cHt - 200 - scaledPoints[i+3]), paint);
				canvas.drawCircle(scaledPoints[i+2], (cHt - 200 - scaledPoints[i+3]), 10, paint);
			}

			int numDivisions = 5;
			float divHt = ((float) cHt) / numDivisions;

			for (int i = numDivisions - 1; i >= 0; i--) {
//				if (i == 0) {
//					cutOffPaint.setARGB(255, 0, 0, 0);
//				}
				float lineYCoOrd = cHt - 200f - (divHt * i);
				canvas.drawLine(0, lineYCoOrd, (float) cWt, lineYCoOrd, cutOffPaint);
			}
		}
	}

	@Override
	public void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
		Log.i("Vidhi", "height is " + height);
		this.width = width;
		processAndScalePoints();

//		if (points != null && points.length > 0) {
//			for (int i = 0; i < points.length; i++) {
//				if (i % 2 == 0) {
//					points[i] = points[i];
//				} else {
//					points[i] = height - ((200f + points[i]));
//				}
//			}
//		}
	}

	public void setPoints(float[] mpgValues) {
		origPoints = mpgValues;
		processAndScalePoints();
	}

	private void processAndScalePoints() {
		if (origPoints != null || origPoints.length > 0) {
			int X_INTERVAL = this.width / origPoints.length;

			scaledPoints = new float[origPoints.length * 4];
			for (int i = 0, j = 0; i < origPoints.length; i++, j += 4) {
				scaledPoints[j] = (i == 0) ? 0 : scaledPoints[j - 2];
				scaledPoints[j + 1] = (i == 0) ? 0 : scaledPoints[j - 1];
				scaledPoints[j + 2] = scaledPoints[j] + X_INTERVAL;
				scaledPoints[j + 3] = 4 * origPoints[i];
			}
			invalidate();
		}
	}

	public void setColor(int color) {
		this.color = color;
		invalidate();
	}

}
