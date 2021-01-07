package com.microsoft.xbox.toolkit.anim;

/**
 * 07.01.2021
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class BackEaseInterpolator extends XLEInterpolator {
    private float amplitude;

    public BackEaseInterpolator(float f, EasingMode easingMode) {
        super(easingMode);
        this.amplitude = f;
    }

    public float getInterpolationCore(float f) {
        float max = (float) Math.max((double) f, 0.0d);
        return (float) (((double) ((max * max) * max)) - (((double) (this.amplitude * max)) * Math.sin(((double) max) * 3.141592653589793d)));
    }
}
