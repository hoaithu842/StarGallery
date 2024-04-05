package vn.edu.hcmus.stargallery.Listener;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public abstract class OnSwipeTouchListener implements ImageView.OnTouchListener {

    private final GestureDetector gestureDetector;
    static final int MIN_DISTANCE = 150; // Minimum distance for a swipe to be recognized

    public OnSwipeTouchListener(android.content.Context ctx){
        gestureDetector = new GestureDetector(ctx, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > MIN_DISTANCE) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                        return true;
                    }
                } else {
                    // Vertical swipe
                    if (Math.abs(diffY) > MIN_DISTANCE) {
                        if (diffY > 0) {
                            onSwipeBottom();
                        } else {
                            onSwipeTop();
                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    public abstract void onSwipeRight();

    public abstract void onSwipeLeft();
    public abstract void onSwipeTop();
    public abstract void onSwipeBottom();
}