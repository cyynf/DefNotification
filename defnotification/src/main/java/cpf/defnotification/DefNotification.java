package cpf.defnotification;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

import androidx.annotation.IdRes;
import androidx.annotation.IntRange;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

/**
 * Author: cpf
 * Date: 2020/4/10
 * Email: cpf4263@gmail.com
 * <p>
 * App internal banner notifications
 */
public class DefNotification implements Runnable {

    private FrameLayout mRootView;

    private View mView;

    private View.OnClickListener mListener;

    private long mDuration = 3000;

    private Interpolator mInterpolator = new FastOutSlowInInterpolator();

    private ValueAnimator mValueAnimator;

    private int mHeight;

    private Handler handler = new Handler();

    private int offset;

    private int touchSlop;

    private float startX, startY, lastY, maxY;

    private boolean canClick;

    public DefNotification(@NonNull FrameLayout frameLayout) {
        mRootView = frameLayout;
        touchSlop = ViewConfiguration.get(frameLayout.getContext()).getScaledTouchSlop();
    }

    public DefNotification(@NonNull Activity activity) {
        this((FrameLayout) activity.getWindow().getDecorView());
        offset = getStatusBarHeight(activity);
    }

    public DefNotification setContentView(@NonNull View view) {
        mView = view;
        setTouchEvent();
        return this;
    }

    public DefNotification setContentView(@LayoutRes int resId) {
        return this.setContentView(LayoutInflater.from(mRootView.getContext()).inflate(resId, mRootView, false));
    }

    public <T extends View> T findViewById(@IdRes int id) {
        return mView.findViewById(id);
    }

    public DefNotification setOnClickListener(@Nullable View.OnClickListener l) {
        mListener = l;
        return this;
    }

    public DefNotification setDuration(@IntRange(from = 3000, to = 10000) long duration) {
        mDuration = duration;
        return this;
    }

    public DefNotification setInterpolator(@NonNull Interpolator interpolator) {
        mInterpolator = interpolator;
        return this;
    }

    private int measureHeight() {
        ViewGroup.LayoutParams lp = mView.getLayoutParams();
        if (lp == null) {
            lp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        if (lp instanceof ViewGroup.MarginLayoutParams) {
            ((ViewGroup.MarginLayoutParams) lp).topMargin += offset;
        }
        int widthSpec;
        if (lp.width > 0) {
            widthSpec = View.MeasureSpec.makeMeasureSpec(lp.width, View.MeasureSpec.EXACTLY);
        } else {
            widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.AT_MOST);
        }
        int heightSpec;
        if (lp.height > 0) {
            heightSpec = View.MeasureSpec.makeMeasureSpec(lp.height, View.MeasureSpec.EXACTLY);
        } else {
            heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        }
        mView.measure(widthSpec, heightSpec);
        return mView.getMeasuredHeight();
    }

    public void show() {
        if (mHeight == 0) {
            mHeight = measureHeight();
        }
        if (mView.getParent() == null) {
            mRootView.addView(mView);
            mView.setTranslationY(getMaxTranslationY());
        }
        enterAnim(mView.getTranslationY(), 0);
        handler.postDelayed(this, mDuration);
    }

    public void hide() {
        if (mValueAnimator != null) {
            mValueAnimator.cancel();
            mValueAnimator = null;
        }
        handler.removeCallbacks(this);
        exitAnim(mView.getTranslationY(), getMaxTranslationY());
    }

    private float getMaxTranslationY() {
        return -(mHeight + offset);
    }

    @Override
    public void run() {
        hide();
    }

    private void enterAnim(float start, float end) {
        mValueAnimator = ValueAnimator.ofFloat(start, end);
        mValueAnimator.setInterpolator(mInterpolator);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (mView == null || mRootView == null) {
                    mValueAnimator.cancel();
                    mValueAnimator = null;
                    return;
                }
                float value = (Float) animation.getAnimatedValue();
                mView.setTranslationY(value);
                maxY = mView.getY();
            }
        });
        mValueAnimator.start();
    }

    private void exitAnim(float start, float end) {
        mValueAnimator = ValueAnimator.ofFloat(start, end);
        mValueAnimator.setInterpolator(mInterpolator);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (mView == null || mRootView == null) {
                    mValueAnimator.cancel();
                    mValueAnimator = null;
                    return;
                }
                float value = (Float) animation.getAnimatedValue();
                mView.setTranslationY(value);
            }
        });
        mValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mView != null && mRootView != null) {
                    mRootView.removeView(mView);
                }
            }
        });
        mValueAnimator.start();
    }

    private void setTouchEvent() {
        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        canClick = true;
                        startX = event.getRawX();
                        startY = event.getRawY();
                        lastY = startY;
                        handler.removeCallbacks(DefNotification.this);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float x = event.getRawX();
                        float y = event.getRawY();
                        updateY(y);
                        lastY = y;
                        if (!isClick(x, y)) {
                            canClick = false;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mListener != null && canClick) {
                            mListener.onClick(v);
                            hide();
                        } else {
                            if (maxY - v.getY() > offset) {
                                hide();
                            } else {
                                show();
                            }
                        }
                        break;
                }
                return true;
            }
        });
    }


    private void updateY(float y) {
        float newY = mView.getY() + (y - lastY);
        if (newY < maxY) {
            mView.setY(newY);
        }
    }

    private boolean isClick(float endX, float endY) {
        return Math.abs(endX - startX) < touchSlop && Math.abs(endY - startY) < touchSlop;
    }

    /**
     * Gets the height of the status bar
     */
    private int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }
}
