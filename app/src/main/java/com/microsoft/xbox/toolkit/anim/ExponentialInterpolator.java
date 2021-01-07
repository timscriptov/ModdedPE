package com.microsoft.xbox.toolkit.anim;

/**
 * 07.01.2021
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class ExponentialInterpolator extends XLEInterpolator {
    private float exponent;

    public ExponentialInterpolator(float f, EasingMode easingMode) {
        super(easingMode);
        this.exponent = f;
    }

    public float getInterpolationCore(float f) {
        return (float) ((Math.pow(2.718281828459045d, (double) (this.exponent * f)) - 1.0d) / (Math.pow(2.718281828459045d, (double) this.exponent) - 1.0d));
    }
}
