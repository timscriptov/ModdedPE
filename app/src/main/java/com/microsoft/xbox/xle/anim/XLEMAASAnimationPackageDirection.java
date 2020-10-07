package com.microsoft.xbox.xle.anim;

import android.view.View;

import com.microsoft.xbox.toolkit.anim.MAAS;
import com.microsoft.xbox.toolkit.anim.MAASAnimation;
import com.microsoft.xbox.toolkit.anim.XLEAnimation;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

@Root
public class XLEMAASAnimationPackageDirection extends MAASAnimation {
    @Element(required = false)
    public XLEMAASAnimation inAnimation;
    @Element(required = false)
    public XLEMAASAnimation outAnimation;

    public XLEAnimation compile(MAAS.MAASAnimationType type, View targetView) {
        XLEMAASAnimation anim = type == MAAS.MAASAnimationType.ANIMATE_IN ? inAnimation : outAnimation;
        if (anim == null) {
            return null;
        }
        return anim.compile(targetView);
    }
}
