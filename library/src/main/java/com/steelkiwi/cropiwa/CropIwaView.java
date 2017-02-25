package com.steelkiwi.cropiwa;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.FrameLayout;

import com.steelkiwi.cropiwa.config.CropIwaOverlayConfig;


/**
 * @author Yaroslav Polyakov https://github.com/polyak01
 * 03.02.2017.
 */
public class CropIwaView extends FrameLayout {

    /**
     * TODO:
     * 1. Downscale image, if it is larger than view
     * 2. Add ability to configure using xml
     * 3. Add API:
     *      -Scale image
     *      -Rotate image
     *      -Enable/disable gestures
     * 4. Clean everything, add important logs, double check
     * 5. Add ability to crop...
     * The last one is pretty important!
     */

    private CropIwaImageView imageView;
    private CropIwaOverlayView overlayView;

    private CropIwaOverlayConfig overlayConfig;

    private CropIwaImageView.GestureProcessor gestureDetector;

    public CropIwaView(Context context) {
        super(context);
        init(null);
    }

    public CropIwaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CropIwaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CropIwaView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        imageView = new CropIwaImageView(getContext());
        imageView.setBackgroundColor(Color.BLACK);
        gestureDetector = imageView.getImageTransformGestureDetector();
        addView(imageView);

        overlayConfig = CropIwaOverlayConfig.createDefault(getContext());
        overlayView = overlayConfig.isDynamicCrop() ?
                new CropIwaDynamicOverlayView(getContext(), overlayConfig) :
                new CropIwaOverlayView(getContext(), overlayConfig);
        overlayView.setNewBoundsListener(imageView);
        LayoutParams params = generateDefaultLayoutParams();
        params.gravity = Gravity.CENTER;
        overlayView.setLayoutParams(params);
        addView(overlayView);
    }

    @Override
    @SuppressWarnings("RedundantIfStatement")
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //I think this "redundant" if statements improve code readability
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            gestureDetector.onDown(ev);
            return false;
        }
        if (overlayView.isResizing() || overlayView.isDraggingCropArea()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        imageView.measure(widthMeasureSpec, heightMeasureSpec);
        overlayView.measure(
                imageView.getMeasuredWidthAndState(),
                imageView.getMeasuredHeightAndState());
        setMeasuredDimension(
                imageView.getMeasuredWidthAndState(),
                imageView.getMeasuredHeightAndState());
    }

    public CropIwaOverlayConfig configureOverlay() {
        return overlayConfig;
    }

    private int toExactSpec(int size) {
        return MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
    }
}