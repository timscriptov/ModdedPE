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
public class XLEMAASAnimationPackageNavigationManager extends MAASAnimation {
    @Element(required = false)
    public XLEMAASAnimationPackageDirection backward;
    @Element(required = false)
    public XLEMAASAnimationPackageDirection forward;

    public XLEAnimation compile(MAAS.MAASAnimationType type, boolean backward2, View targetView) {
        XLEMAASAnimationPackageDirection direction = backward2 ? backward : forward;
        if (direction == null) {
            return null;
        }
        return direction.compile(type, targetView);
    }
}
