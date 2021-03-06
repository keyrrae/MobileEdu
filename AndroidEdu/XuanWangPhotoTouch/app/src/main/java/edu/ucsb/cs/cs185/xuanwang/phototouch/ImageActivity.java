/*
 *  Copyright (c) 2017 - present, Xuan Wang
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree.
 *
 */

package edu.ucsb.cs.cs185.xuanwang.phototouch;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

import com.almeros.android.multitouch.MoveGestureDetector;
import com.almeros.android.multitouch.RotateGestureDetector;

public class ImageActivity extends AppCompatActivity implements View.OnTouchListener {

    ImageView mImageView;
    Bitmap mBitMap;
    Matrix mMatrix;

    private float mScaleFactor = 1.0f;
    private float mRotationDegrees = 0.f;
    private float mFocusX = 0.f;
    private float mFocusY = 0.f;

    private ScaleGestureDetector mScaleDetector;
    private RotateGestureDetector mRotateDetector;
    private MoveGestureDetector mMoveDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        mMatrix = new Matrix();

        mImageView = (ImageView) findViewById(R.id.imageView);
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);
        if (id != -1){

            mBitMap = ImageManager.images.get(id);
            mImageView.setImageBitmap(mBitMap);
            mImageView.setOnTouchListener(this);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Setup Gesture Detectors
        mScaleDetector = new ScaleGestureDetector(getApplicationContext(), new ScaleListener());
        mRotateDetector = new RotateGestureDetector(getApplicationContext(), new RotateListener());
        mMoveDetector = new MoveGestureDetector(getApplicationContext(), new MoveListener());
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        Drawable d = mImageView.getDrawable();

        mScaleDetector.onTouchEvent(motionEvent);
        mRotateDetector.onTouchEvent(motionEvent);
        mMoveDetector.onTouchEvent(motionEvent);

        mImageView.setScaleType(ImageView.ScaleType.MATRIX);

        RectF imageRectF = new RectF(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        RectF viewRectF = new RectF(0, 0, mImageView.getWidth(), mImageView.getHeight());
        mMatrix.setRectToRect(imageRectF, viewRectF, Matrix.ScaleToFit.CENTER);
        mMatrix.preScale(mScaleFactor, mScaleFactor, mBitMap.getWidth()/2, mBitMap.getHeight()/2);

        mMatrix.preRotate(mRotationDegrees, mBitMap.getWidth() / 2, mBitMap.getHeight() / 2);
        mMatrix.postTranslate(mFocusX, mFocusY);

        mImageView.setImageMatrix(mMatrix);
        Log.i("scale", String.valueOf(mScaleFactor));
        Log.i("rotate", String.valueOf(mRotationDegrees));
        Log.i("focusX", String.valueOf(mFocusX));
        Log.i("focusY", String.valueOf(mFocusY));

        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            Log.i("test", "test");
            mScaleFactor *= detector.getScaleFactor(); // average distance between fingers
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            Log.i("test", "scalebegin");

            return super.onScaleBegin(detector);
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            Log.i("test", "scaleend");

            super.onScaleEnd(detector);
        }
    }

    private class RotateListener extends RotateGestureDetector.SimpleOnRotateGestureListener {
        @Override
        public boolean onRotate(RotateGestureDetector detector) {
            mRotationDegrees -= detector.getRotationDegreesDelta();
            return true;
        }
    }

    private class MoveListener extends MoveGestureDetector.SimpleOnMoveGestureListener {
        @Override
        public boolean onMove(MoveGestureDetector detector) {
            PointF d = detector.getFocusDelta();
            mFocusX += d.x;
            mFocusY += d.y;

            // mFocusX = detector.getFocusX();
            // mFocusY = detector.getFocusY();
            return true;
        }
    }
}
