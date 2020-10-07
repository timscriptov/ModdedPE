package com.microsoft.xbox.toolkit.anim;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class BackEaseInterpolator extends XLEInterpolator {
    private float amplitude;

    public BackEaseInterpolator(float amplitude2, EasingMode easingMode) {
        super(easingMode);
        amplitude = amplitude2;
    }

    public float getInterpolationCore(float normalizedTime) {
        float normalizedTime2 = (float) Math.max(normalizedTime, 0.0d);
        return (float) (((double) ((normalizedTime2 * normalizedTime2) * normalizedTime2)) - (((double) (amplitude * normalizedTime2)) * Math.sin(((double) normalizedTime2) * 3.141592653589793d)));
    }
}
