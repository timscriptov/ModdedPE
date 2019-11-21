package com.microsoft.onlineid.internal.ui;

import android.animation.AnimatorSet;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class ProgressView extends RelativeLayout {
    public static final int NumberOfDots = 5;
    private static final int ProgressColor = Color.rgb(121, 121, 121);
    private static final float ProgressDotSizeDip = 2.6f;
    private static final float ProgressPaddingDip = 2.6f;
    private int _dotSize;
    private ProgressAnimation _progressAnimation;

    private class ProgressAnimation {
        private int _animationDuration;
        private int[] _dotDelays;
        private int[] _keyframeXTranslations;
        private AnimatorSet _progressAnimator = generateAnimation();

        public ProgressAnimation(int width) {
            setAnimationParams(width);
        }

        private AnimatorSet generateAnimation() {
            PropertyValuesHolder keyframes = createKeyframes();
            ObjectAnimator[] xAnimators = new ObjectAnimator[ProgressView.NumberOfDots];
            for (int i = ProgressView.ProgressColor; i < ProgressView.NumberOfDots; i++) {
                ObjectAnimator xAnimator = ObjectAnimator.ofPropertyValuesHolder(ProgressView.this.getChildAt(i), new PropertyValuesHolder[]{keyframes});
                xAnimator.setDuration((long) this._animationDuration);
                xAnimator.setRepeatCount(-1);
                xAnimator.setStartDelay((long) this._dotDelays[i]);
                xAnimators[i] = xAnimator;
            }
            AnimatorSet set = new AnimatorSet();
            set.playTogether(xAnimators);
            return set;
        }

        private void setAnimationParams(int width) {
            int dotGap = Math.round(((float) width) / 25.0f);
            float linearVelocity = (((float) width) / 10.0f) + 30.0f;
            int distanceInOut = Math.round(((float) width) * 0.416666f);
            int distanceLinear = Math.round(((float) width) * (1.0f - (2.0f * 0.416666f)));
            float timeLinear = ((float) (distanceLinear * 1000)) / linearVelocity;
            float timeInOut = ((timeLinear / 0.3333f) - timeLinear) / 2.0f;
            int dotDelay = Math.round(((float) (dotGap * 1000)) / linearVelocity);
            if (dotDelay > 333) {
                dotDelay = 333;
            }
            int[] iArr = new int[ProgressView.NumberOfDots];
            iArr[ProgressView.ProgressColor] = ProgressView.ProgressColor;
            iArr[1] = dotDelay;
            iArr[2] = dotDelay * 2;
            iArr[3] = dotDelay * 3;
            iArr[4] = dotDelay * 4;
            this._dotDelays = iArr;
            this._animationDuration = Math.round(((timeLinear + (2.0f * timeInOut)) + (((float) dotDelay) * 4.0f)) + 250.0f);
            iArr = new int[ProgressView.NumberOfDots];
            iArr[ProgressView.ProgressColor] = ProgressView.this._dotSize * -1;
            iArr[1] = distanceInOut;
            iArr[2] = distanceInOut + distanceLinear;
            iArr[3] = ProgressView.this._dotSize + width;
            iArr[4] = ProgressView.this._dotSize * -1;
            this._keyframeXTranslations = iArr;
        }

        private PropertyValuesHolder createKeyframes() {
            float[] keyframeFractions = new float[]{0.0f, 0.15f, 0.65f, 0.8f, 1.0f};
            Keyframe[] keyframes = new Keyframe[this._keyframeXTranslations.length];
            for (int i = ProgressView.ProgressColor; i < this._keyframeXTranslations.length; i++) {
                keyframes[i] = Keyframe.ofFloat(keyframeFractions[i], (float) this._keyframeXTranslations[i]);
            }
            keyframes[this._keyframeXTranslations.length - 1].setInterpolator(new OvershootInterpolator(1.0f));
            return PropertyValuesHolder.ofKeyframe(View.TRANSLATION_X, keyframes);
        }

        public boolean startAnimation() {
            if (this._progressAnimator.isRunning()) {
                return false;
            }
            this._progressAnimator.start();
            return true;
        }

        public boolean stopAnimation() {
            if (!this._progressAnimator.isRunning()) {
                return false;
            }
            this._progressAnimator.end();
            return true;
        }

        public boolean isAnimating() {
            return this._progressAnimator.isRunning();
        }
    }

    public ProgressView(Context context, AttributeSet attributes, int defStyle) {
        super(context, attributes, defStyle);
        initialize();
    }

    public ProgressView(Context context, AttributeSet attributes) {
        super(context, attributes);
        initialize();
    }

    public ProgressView(Context context) {
        super(context);
        initialize();
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        boolean isAnimationStopped = stopAnimation();
        this._progressAnimation = new ProgressAnimation(w);
        if (isAnimationStopped) {
            startAnimation();
        }
    }

    public boolean startAnimation() {
        return this._progressAnimation != null && this._progressAnimation.startAnimation();
    }

    public boolean stopAnimation() {
        return this._progressAnimation != null && this._progressAnimation.stopAnimation();
    }

    public boolean isAnimating() {
        return this._progressAnimation != null && this._progressAnimation.isAnimating();
    }

    public void overrideDefaultPadding(float paddingDip) {
        int paddingPixels = Dimensions.convertDipToPixels(ProgressPaddingDip, getContext().getResources().getDisplayMetrics());
        setPadding(ProgressColor, paddingPixels, ProgressColor, paddingPixels);
    }

    private void initialize() {
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        this._dotSize = Dimensions.convertDipToPixels(ProgressPaddingDip, metrics);
        int paddingPixels = Dimensions.convertDipToPixels(ProgressPaddingDip, metrics);
        setPadding(ProgressColor, paddingPixels, ProgressColor, paddingPixels);
        for (int i = ProgressColor; i < NumberOfDots; i++) {
            addDot();
        }
        this._progressAnimation = new ProgressAnimation(getWidth());
    }

    private View addDot() {
        View dot = new View(getContext());
        dot.setLayoutParams(new LayoutParams(this._dotSize, this._dotSize));
        dot.setBackgroundColor(ProgressColor);
        dot.setX((float) (this._dotSize * -1));
        addView(dot);
        return dot;
    }
}
