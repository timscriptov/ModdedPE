package com.microsoft.xbox.toolkit.anim;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class SineInterpolator extends XLEInterpolator {
    public SineInterpolator(EasingMode easingMode) {
        super(easingMode);
    }

    public float getInterpolationCore(float normalizedTime) {
        return (float) (1.0d - Math.sin((1.0d - ((double) normalizedTime)) * 1.5707963267948966d));
    }
}
