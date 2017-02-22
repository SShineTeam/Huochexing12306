package com.sshine.huochexing.model;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

public class MaskView extends View {

    private Bitmap mBitmap;
    private List<Point> mPoints = new ArrayList<Point>();
    private Paint mPaint;
    private int mXDown;
    private int mYDown;
    private int mTouchSlop;
    private boolean mShouldDrawIcon = false;

    public MaskView(Context context) {
        super(context);
        setWillNotDraw(false);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);
    }

    public MaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        setWillNotDraw(false);
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(100);
    }

    public void setBackgroundBitmap(Bitmap bitmap) {
        mPoints = new ArrayList<Point>();
        if (bitmap != null) {
            Matrix matrix = new Matrix();
            float scaleW = getWidth() / bitmap.getWidth();
            float scaleH = getHeight() / bitmap.getHeight();
            float scale = scaleW > scaleH ? scaleH : scaleW;
            if (scale <= 0) {
                return;
            }
            matrix.postScale(scale, scale); //长和宽放大缩小的比例
            Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            mBitmap = resizeBmp;
            invalidate();
        } else {
            mBitmap = null;
        }
    }

    public synchronized List<Point> getSelectedPoints() {
        List<Point> points = new ArrayList<Point>();
        for (Point p : mPoints) {
            points.add(getFixedPoint(p));
        }
        return points;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mXDown = x;
                mYDown = y;
                mShouldDrawIcon = true;
                mPoints.add(new Point(x, y));
                invalidate();
                break;
//		case MotionEvent.ACTION_UP:
//			int deltaX = (int)x - mXDown;
//			int deltaY = (int)y - mYDown;
//			if (Math.abs(deltaX) < mTouchSlop && Math.abs(deltaY) < mTouchSlop && mBitmap != null){
//				//没移动则画点
//				mShouldDrawIcon = true;
//				mPoints.add(new Point(x, y));
//				invalidate();
//			}
//			break;
            case MotionEvent.ACTION_MOVE:
                mShouldDrawIcon = false;
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap, 0, 0, mPaint);
            if (mShouldDrawIcon) {
                for (Point p : mPoints) {
                    canvas.drawCircle(p.x, p.y, 10.0f, mPaint);
                }
            }
        } else {
            canvas.drawText("请求图片失败", getWidth() / 2, getHeight() / 2, mPaint);
        }
        super.onDraw(canvas);
    }

    private Point getFixedPoint(Point p) {
        if (mBitmap != null) {
            //12306官方验证码293px
            float scaleX = 293 / (float) mBitmap.getWidth();
            //高156px
            float scaleY = 156 / (float) mBitmap.getHeight();
            Point p1 = new Point((int) (p.x * scaleX), (int) (p.y * scaleY));
            return p1;
        }
        return null;
    }
}
