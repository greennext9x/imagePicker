package dev.hoangcuong.imagepicker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import java.util.ArrayList;
import java.util.List;


public class ZoomImageView extends AppCompatImageView implements ViewTreeObserver.OnGlobalLayoutListener
    , View.OnTouchListener, ScaleGestureDetector.OnScaleGestureListener {

    private static final String TAG = "ZoomImageView";
    private boolean isInit;

    private Matrix mMatrix;

    private float mMinScale;

    private float mMidScale;

    private float mMaxScale;

    private ScaleGestureDetector mScaleGestureDetector;

    private int mLastPointereCount;

    private float mLastX;
    private float mLastY;
    private int mTouchSlop;
    private boolean isCanDrag;
    private boolean isCheckLeftAndRight;
    private boolean isCheckTopAndBottom;

    private GestureDetector mGestureDetector;
    private boolean isScaleing;
    private List<MotionEvent> events;
    private OnClickListener onClickListener;
    private int arae_img_id = -1;


    public ZoomImageView(Context context) {
        this(context, null);
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //setScaleType(ScaleType.MATRIX);
        mMatrix = new Matrix();
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (isScaleing || getScale() >= mMaxScale)
                    return true;
                isScaleing = true;
                float x = e.getX();
                float y = e.getY();

                if (getScale() < mMidScale) {
                    postDelayed(new AutoScaleRunnable(mMidScale, x, y), 16);
                } else {
                    postDelayed(new AutoScaleRunnable(mMinScale, x, y), 16);
                }

                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (onClickListener != null) {
                    onClickListener.onClick(ZoomImageView.this);
                    return true;
                }
                return false;
            }
        });
        setOnTouchListener(this);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        events = new ArrayList<>();
    }


    private class AutoScaleRunnable implements Runnable {

        private float mTargetScale;
        private float x; //缩放的中心点x
        private float y; //缩放的中心点y
        private float tmpScale;

        private final float BIGGER = 1.07f;
        private final float SMALL = 0.93f;

        public AutoScaleRunnable(float mTargetScale, float x, float y) {
            this.mTargetScale = mTargetScale;
            this.x = x;
            this.y = y;

            if (getScale() < mTargetScale) {
                tmpScale = BIGGER;
            } else {
                tmpScale = SMALL;
            }
        }

        @Override
        public void run() {
            mMatrix.postScale(tmpScale, tmpScale, x, y);
            checkBorderAndCenterWhenScale();
            setImageMatrix(mMatrix);


            float currentScale = getScale();
            if ((tmpScale > 1.0f && currentScale < mTargetScale)
                || (tmpScale < 1.0f && currentScale > mTargetScale)) {
                postDelayed(this, 16);
            } else {
                float scale = mTargetScale / currentScale;
                mMatrix.postScale(scale, scale, x, y);
                checkBorderAndCenterWhenScale();
                setImageMatrix(mMatrix);
                isScaleing = false;
            }
        }
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @SuppressLint("NewApi")
    @Override
    @SuppressWarnings("deprecation")
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        if(arae_img_id != -1){
            arae_img_id = -1;
            return;
        }
        setScaleType(ScaleType.MATRIX);
        log("执行了onGlobalLayout| NULL:" + (getDrawable() == null));
        if (getDrawable() == null || getWidth() == 0 || getHeight() == 0) return;

        if (!isInit) {
            int width = getWidth();
            int height = getHeight();
            float screenWeight = height * 1.0f / width;

            int imageH = getDrawable().getIntrinsicHeight();
            int imageW = getDrawable().getIntrinsicWidth();
            float imageWeight = imageH * 1.0f / imageW;

            if (screenWeight >= imageWeight) {
                float scale = 1.0f;

                if (imageW > width && imageH < height) {
                    scale = width * 1.0f / imageW;
                }

                if (imageH > height && imageW < width) {
                    scale = height * 1.0f / imageH;
                }

                if (imageH > height && imageW > width) {
                    scale = Math.min(width * 1.0f / imageW, height * 1.0f / imageH);
                    log("max scale:" + scale);
                }

                if (imageH < height && imageW < width) {
                    scale = Math.min(width * 1.0f / imageW, height * 1.0f / imageH);
                    log("min scale:" + scale);
                }

                mMinScale = scale;
                mMidScale = mMinScale * 2;
                mMaxScale = mMinScale * 4;

                int dx = getWidth() / 2 - imageW / 2;
                int dy = getHeight() / 2 - imageH / 2;


                mMatrix.postTranslate(dx, dy);
                mMatrix.postScale(mMinScale, mMinScale, width / 2, height / 2);
            } else {

                float scale = width * 1.0f / imageW;

                mMaxScale = scale;
                mMidScale = mMaxScale / 2;
                mMinScale = mMaxScale / 4;


                mMatrix.postScale(mMaxScale, mMaxScale, 0, 0);
            }

            setImageMatrix(mMatrix);
            isInit = true;
        }
    }


    private static boolean IS_DEBUG = false;


    public static void log(String value) {
        if (IS_DEBUG)
            Log.w(TAG, value);
    }


    public void reSetState() {
        isInit = false;
        setTag(null);
        mMatrix.reset();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (mGestureDetector.onTouchEvent(motionEvent))
            return true;


        if (motionEvent.getPointerCount() > 1)
            mScaleGestureDetector.onTouchEvent(motionEvent);


        float x = 0;
        float y = 0;

        int pointerCount = motionEvent.getPointerCount();

        for (int i = 0; i < pointerCount; i++) {
            x += motionEvent.getX(i);
            y += motionEvent.getY(i);
        }

        x /= pointerCount;
        y /= pointerCount;

        if (mLastPointereCount != pointerCount) {
            isCanDrag = false;
            mLastX = x;
            mLastY = y;
        }

        mLastPointereCount = pointerCount;

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                RectF rectF = getMatrixRectF();
                if ((rectF.width() > getWidth() + 0.01f || (rectF.height() > getHeight() + 0.01f))) {
                    if ((rectF.right != getWidth()) && (rectF.left != 0)) {
                        try {
                            getParent().requestDisallowInterceptTouchEvent(true);
                        } catch (Exception e) {
                            log(e.toString());
                        }
                    }
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {

                float dx = x - mLastX;
                float dy = y - mLastY;

                if (!isCanDrag) {
                    isCanDrag = isMoveAction(dx, dy);
                }

                if (isCanDrag) {
                    RectF rectF = getMatrixRectF();

                    if (getDrawable() != null) {
                        isCheckLeftAndRight = isCheckTopAndBottom = true;

                        if (rectF.width() <= getWidth()) {
                            isCheckLeftAndRight = false;
                            dx = 0;
                        }

                        if (rectF.height() <= getHeight()) {
                            isCheckTopAndBottom = false;
                            dy = 0;
                        }

                        mMatrix.postTranslate(dx, dy);
                        checkBorderWhenTranslate();
                        setImageMatrix(mMatrix);
                    }
                }
                mLastX = x;
                mLastY = y;

                RectF rect = getMatrixRectF();
                if ((rect.width() > getWidth() + 0.01f || (rect.height() > getHeight() + 0.01f))) {
                    if ((rect.right != getWidth()) && (rect.left != 0)) {
                        try {
                            getParent().requestDisallowInterceptTouchEvent(true);
                        } catch (Exception e) {
                            log(e.toString());
                        }
                    }
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                mLastPointereCount = 0;
                break;
            }
        }
        return true;
    }


    private void checkBorderWhenTranslate() {
        RectF rectF = getMatrixRectF();

        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();

        if (rectF.top > 0 && isCheckTopAndBottom) {
            deltaY = -rectF.top;
        }

        if (rectF.bottom < height && isCheckTopAndBottom) {
            deltaY = height - rectF.bottom;
        }


        if (rectF.left > 0 && isCheckLeftAndRight) {
            deltaX = -rectF.left;
        }

        if (rectF.right < width && isCheckLeftAndRight) {
            deltaX = width - rectF.right;
        }

        mMatrix.postTranslate(deltaX, deltaY);
        setImageMatrix(mMatrix);
    }


    private boolean isMoveAction(float dx, float dy) {
        return Math.sqrt(dx * dx + dy * dy) > mTouchSlop;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {

        float scaleFactor = detector.getScaleFactor();//获取用户手势判断出来的缩放值
        float scale = getScale();


        if (getDrawable() == null) return true;

        //缩放范围控制
        if ((scale < mMaxScale && scaleFactor > 1.0f) || (scale > mMinScale && scaleFactor < 1.0f)) {
            if (scaleFactor * scale < mMinScale) {
                scaleFactor = mMinScale / scale;
            }

            if (scale * scaleFactor > mMaxScale) {
                scaleFactor = mMaxScale / scale;
            }

            mMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
            checkBorderAndCenterWhenScale();
            setImageMatrix(mMatrix);
        }
        return true;
    }


    private void checkBorderAndCenterWhenScale() {
        RectF rectF = getMatrixRectF();

        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();

        if (rectF.width() >= width) {
            if (rectF.left > 0)
                deltaX = -rectF.left;
            if (rectF.right < width)
                deltaX = width - rectF.right;
        }

        if (rectF.height() >= height) {
            if (rectF.top > 0)
                deltaY = 0;
            if (rectF.bottom < height)
                deltaY = height - rectF.bottom;
        }

        if (rectF.width() < width) {
            deltaX = width / 2f - rectF.right + rectF.width() / 2;
        }

        if (rectF.height() < height) {
            deltaY = height / 2f - rectF.bottom + rectF.height() / 2;
        }

        mMatrix.postTranslate(deltaX, deltaY);
        setImageMatrix(mMatrix);
    }


    private RectF getMatrixRectF() {
        RectF rectF = new RectF();
        Drawable drawable = getDrawable();

        if (drawable != null) {
            rectF.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            mMatrix.mapRect(rectF);
        }

        return rectF;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
        return true; //缩放开始,返回true 用于接收后续时间
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {

    }


    private float getScale() {
        float[] values = new float[9];
        mMatrix.getValues(values);
        return values[Matrix.MSCALE_X];
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        reSetState();
        super.setImageBitmap(bm);
    }

    @Override
    public void setImageResource(int resId) {
        reSetState();
        super.setImageResource(resId);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        reSetState();
        super.setImageDrawable(drawable);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        this.onClickListener = l;
    }


    public void placeholder(int resID) {
        this.arae_img_id = resID;
        setScaleType(ScaleType.CENTER);
        setImageResource(resID);
    }

}
