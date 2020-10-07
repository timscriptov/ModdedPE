package com.microsoft.xbox.toolkit.anim;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class ExponentialInterpolator extends XLEInterpolator {
    private float exponent;

    public ExponentialInterpolator(float exponent2, EasingMode easingMode) {
        super(easingMode);
        exponent = exponent2;
    }

    public float getInterpolationCore(float normalizedTime) {
        return (float) ((Math.pow(2.718281828459045d, (exponent * normalizedTime)) - 1.0d) / (Math.pow(2.718281828459045d, exponent) - 1.0d));
    }
}
