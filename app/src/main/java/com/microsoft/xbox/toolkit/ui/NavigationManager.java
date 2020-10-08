package com.microsoft.xbox.toolkit.ui;

import android.view.KeyEvent;
import android.view.View;

import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.anim.XLEAnimationPackage;
import com.microsoft.xboxtcui.XboxTcuiSdk;

import java.util.Iterator;
import java.util.Stack;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class NavigationManager implements View.OnKeyListener {
    private static final String TAG = "NavigationManager";
    public final Stack<ActivityParameters> navigationParameters;
    public final Stack<ScreenLayout> navigationStack;
    final Runnable callAfterAnimation;
    public boolean cannotNavigateTripwire;
    public OnNavigatedListener navigationListener;
    private NavigationManagerAnimationState animationState;
    private XLEAnimationPackage currentAnimation;
    private boolean goingBack;
    private NavigationCallbacks navigationCallbacks;
    private boolean transitionAnimate;
    private Runnable transitionLambda;

    private NavigationManager() {
        boolean z = true;
        navigationParameters = new Stack<>();
        navigationStack = new Stack<>();
        currentAnimation = null;
        animationState = NavigationManagerAnimationState.NONE;
        transitionLambda = null;
        goingBack = false;
        transitionAnimate = true;
        cannotNavigateTripwire = false;
        callAfterAnimation = () -> OnAnimationEnd();
        XLEAssert.assertTrue("You must access navigation manager on UI thread.", Thread.currentThread() != ThreadManager.UIThread ? false : z);
    }

    public static NavigationManager getInstance() {
        return NavigationManagerHolder.instance;
    }

    public ScreenLayout getCurrentActivity() {
        if (navigationStack.empty()) {
            return null;
        }
        return navigationStack.peek();
    }

    public String getCurrentActivityName() {
        ScreenLayout ativity = getCurrentActivity();
        if (ativity != null) {
            return ativity.getName();
        }
        return null;
    }

    public ScreenLayout getPreviousActivity() {
        if (navigationStack.empty() || navigationStack.size() <= 1) {
            return null;
        }
        return (ScreenLayout) navigationStack.get(navigationStack.size() - 2);
    }

    public void setOnNavigatedListener(OnNavigatedListener listener) {
        navigationListener = listener;
    }

    public void removeNaviationListener() {
        navigationListener = null;
    }

    public void setNavigationCallbacks(NavigationCallbacks callbacks) {
        navigationCallbacks = callbacks;
    }

    public void removeNavigationCallbacks() {
        navigationCallbacks = null;
    }

    public ActivityParameters getActivityParameters() {
        return getActivityParameters(0);
    }

    public ActivityParameters getActivityParameters(int depth) {
        XLEAssert.assertTrue(depth >= 0 && depth < navigationParameters.size());
        return (ActivityParameters) navigationParameters.get((navigationParameters.size() - depth) - 1);
    }

    public void NavigateTo(Class<? extends ScreenLayout> screenClass, boolean addToStack, ActivityParameters activityParameters) {
        if (addToStack) {
            try {
                PushScreen(screenClass, activityParameters);
            } catch (XLEException e) {
                e.printStackTrace();
            }
        } else {
            try {
                PopScreensAndReplace(1, screenClass, activityParameters);
            } catch (XLEException e) {
                e.printStackTrace();
            }
        }
    }

    public void NavigateTo(Class<? extends ScreenLayout> screenClass, boolean addToStack) {
        NavigateTo(screenClass, addToStack, (ActivityParameters) null);
    }

    public boolean OnBackButtonPressed() {
        boolean shouldFinishActivity = ShouldBackCloseApp();
        if (getCurrentActivity() != null && !getCurrentActivity().onBackButtonPressed()) {
            if (shouldFinishActivity) {
                try {
                    PopScreensAndReplace(1, (Class<? extends ScreenLayout>) null, false, false, false);
                } catch (XLEException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    PopScreen();
                } catch (XLEException e) {
                    e.printStackTrace();
                }
            }
        }
        return shouldFinishActivity;
    }

    public boolean TEST_isAnimatingIn() {
        return false;
    }

    public boolean TEST_isAnimatingOut() {
        return false;
    }

    public boolean isAnimating() {
        return this.animationState != NavigationManagerAnimationState.NONE;
    }

    public boolean ShouldBackCloseApp() {
        return Size() <= 1 && animationState == NavigationManagerAnimationState.NONE;
    }

    public boolean IsScreenOnStack(Class<? extends ScreenLayout> screenClass) {
        Iterator it = navigationStack.iterator();
        while (it.hasNext()) {
            if (((ScreenLayout) it.next()).getClass().equals(screenClass)) {
                return true;
            }
        }
        return false;
    }

    public int CountPopsToScreen(Class<? extends ScreenLayout> screenClass) {
        int TOP_ELEM = navigationStack.size() - 1;
        for (int i = TOP_ELEM; i >= 0; i--) {
            if (((ScreenLayout) navigationStack.get(i)).getClass().equals(screenClass)) {
                return TOP_ELEM - i;
            }
        }
        return -1;
    }

    private int Size() {
        return this.navigationStack.size();
    }

    public void RestartCurrentScreen(boolean animate) throws XLEException {
        RestartCurrentScreen((ActivityParameters) null, animate);
    }

    public void RestartCurrentScreen(ActivityParameters params, boolean animate) throws XLEException {
        if (animationState == NavigationManagerAnimationState.ANIMATING_OUT) {
            OnAnimationEnd();
        } else if (animationState == NavigationManagerAnimationState.ANIMATING_IN) {
            OnAnimationEnd();
            PopScreensAndReplace(1, getCurrentActivity().getClass(), animate, true, true, params);
        } else {
            PopScreensAndReplace(1, getCurrentActivity().getClass(), animate, true, true, params);
        }
    }

    public void PopScreen() throws XLEException {
        PopScreens(1);
    }

    public void PopScreens(int popCount) throws XLEException {
        PopScreensAndReplace(popCount, (Class<? extends ScreenLayout>) null);
    }

    public void PopAllScreens() throws XLEException {
        if (Size() > 0) {
            PopScreensAndReplace(Size(), (Class<? extends ScreenLayout>) null, false, false, false);
        }
    }

    public void PopTillScreenThenPush(Class<? extends ScreenLayout> target, Class<? extends ScreenLayout> newScreen, ActivityParameters params) throws XLEException {
        int toPop = CountPopsToScreen(target);
        if (toPop > 0) {
            PopScreensAndReplace(toPop, newScreen, true, true, false, params);
        } else if (toPop < 0) {
            PopScreensAndReplace(0, newScreen, true, false, false, params);
        } else {
            PopScreensAndReplace(0, newScreen, true, false, false, params);
        }
    }

    public void PopTillScreenThenPush(Class<? extends ScreenLayout> target, Class<? extends ScreenLayout> newScreen) throws XLEException {
        PopTillScreenThenPush(target, newScreen, (ActivityParameters) null);
    }

    public void GotoScreenWithPop(Class<? extends ScreenLayout> screenClass) throws XLEException {
        int toPop = CountPopsToScreen(screenClass);
        if (toPop > 0) {
            PopScreensAndReplace(toPop, (Class<? extends ScreenLayout>) null, true, false, false);
        } else if (toPop < 0) {
            PopScreensAndReplace(Size(), screenClass, true, false, false);
        } else {
            RestartCurrentScreen(true);
        }
    }

    public void GotoScreenWithPop(ActivityParameters activityParameters, Class<? extends ScreenLayout> newTop, Class<? extends ScreenLayout>... until) throws XLEException {
        Class<? extends ScreenLayout> clsUntil = null;
        int idxTop = navigationStack.size() - 1;
        int pos = idxTop;
        loop0:
        while (true) {
            if (pos < 0) {
                break;
            }
            Class<?> cls = ((ScreenLayout) navigationStack.get(pos)).getClass();
            for (Class<? extends ScreenLayout> cls2 : until) {
                if (cls2 == cls) {
                    clsUntil = cls2;
                    break loop0;
                }
            }
            pos--;
        }
        if (clsUntil == null) {
            PopScreensAndReplace(Size(), newTop, true, true, false, activityParameters);
        } else if (clsUntil != newTop) {
            PopScreensAndReplace(idxTop - pos, newTop, true, true, false, activityParameters);
        } else if (pos == idxTop) {
            RestartCurrentScreen(activityParameters, false);
        } else {
            PopScreensAndReplace(idxTop - pos, (Class<? extends ScreenLayout>) null, true, true, false, activityParameters);
        }
    }

    public void GotoScreenWithPush(Class<? extends ScreenLayout> screenClass) throws XLEException {
        int toPop = CountPopsToScreen(screenClass);
        if (toPop > 0) {
            PopScreensAndReplace(toPop, (Class<? extends ScreenLayout>) null, true, false, false);
        } else if (toPop < 0) {
            PopScreensAndReplace(0, screenClass, true, false, false);
        } else {
            RestartCurrentScreen(true);
        }
    }

    public void GotoScreenWithPush(Class<? extends ScreenLayout> screenClass, ActivityParameters activityParameters) throws XLEException {
        int toPop = CountPopsToScreen(screenClass);
        if (toPop > 0) {
            PopScreensAndReplace(toPop, (Class<? extends ScreenLayout>) null, true, false, false, activityParameters);
        } else if (toPop < 0) {
            PopScreensAndReplace(0, screenClass, true, false, false, activityParameters);
        } else {
            RestartCurrentScreen(true);
        }
    }

    public void PushScreen(Class<? extends ScreenLayout> screenClass, ActivityParameters activityParameters) throws XLEException {
        PopScreensAndReplace(0, screenClass, true, false, false, activityParameters);
    }

    public void PushScreen(Class<? extends ScreenLayout> screenClass) throws XLEException {
        PushScreen(screenClass, (ActivityParameters) null);
    }

    public void PopScreensAndReplace(int popCount, Class<? extends ScreenLayout> newScreenClass, ActivityParameters activityParameters) throws XLEException {
        PopScreensAndReplace(popCount, newScreenClass, true, true, false, activityParameters);
    }

    public void PopScreensAndReplace(int popCount, Class<? extends ScreenLayout> newScreenClass) throws XLEException {
        PopScreensAndReplace(popCount, newScreenClass, (ActivityParameters) null);
    }

    public void PopScreensAndReplace(int popCount, Class<? extends ScreenLayout> newScreenClass, boolean animate, boolean isRestart) throws XLEException {
        PopScreensAndReplace(popCount, newScreenClass, animate, true, isRestart);
    }

    public void PopScreensAndReplace(int popCount, Class<? extends ScreenLayout> newScreenClass, boolean animate, boolean goingBack2, boolean isRestart, ActivityParameters activityParameters) throws XLEException {
        final ScreenLayout newScreen;
        final ActivityParameters screenParameters;
        Runnable popAndReplaceRunnable;
        XLEAssert.assertTrue("You must access navigation manager on UI thread.", Thread.currentThread() == ThreadManager.UIThread);
        if (cannotNavigateTripwire) {
            throw new UnsupportedOperationException("NavigationManager: attempted to execute a recursive navigation in the OnStop/OnStart method.  This is forbidden.");
        }
        if (newScreenClass == null || isRestart) {
            newScreen = null;
        } else {
            try {
                newScreen = (ScreenLayout) newScreenClass.getConstructor(new Class[0]).newInstance(new Object[0]);
                animate = animate && newScreen.isAnimateOnPush();
            } catch (Exception e) {
                throw new XLEException(19, "FIXME: Failed to create a screen of type " + newScreenClass.getName(), e);
            }
        }
        if (getCurrentActivity() != null) {
            animate = animate && getCurrentActivity().isAnimateOnPop();
        }
        if (activityParameters == null) {
            screenParameters = new ActivityParameters();
        } else {
            screenParameters = activityParameters;
        }
        final NavigationCallbacks callbacks = navigationCallbacks;
        XLEAssert.assertNotNull(callbacks);
        if (isRestart) {
            popAndReplaceRunnable = new RestartRunner(screenParameters);
        } else {
            final int i = popCount;
            popAndReplaceRunnable = () -> {
                boolean unused = cannotNavigateTripwire = true;
                ScreenLayout from = getCurrentActivity();
                screenParameters.putFromScreen(from);
                screenParameters.putSourcePage(getCurrentActivityName());
                if (getCurrentActivity() != null) {
                    getCurrentActivity().onSetInactive();
                    getCurrentActivity().onPause();
                    getCurrentActivity().onStop();
                }
                for (int i1 = 0; i1 < i1; i1++) {
                    getCurrentActivity().onDestroy();
                    callbacks.removeContentViewXLE((ScreenLayout) navigationStack.pop());
                    navigationParameters.pop();
                }
                TextureManager.Instance().purgeResourceBitmapCache();
                ScreenLayout to = null;
                if (newScreen != null) {
                    if (getCurrentActivity() != null && !newScreen.isKeepPreviousScreen()) {
                        getCurrentActivity().onTombstone();
                    }
                    callbacks.addContentViewXLE((ScreenLayout) navigationStack.push(newScreen));
                    navigationParameters.push(screenParameters);
                    getCurrentActivity().onCreate();
                } else if (getCurrentActivity() != null) {
                    callbacks.addContentViewXLE(getCurrentActivity());
                    if (getCurrentActivity().getIsTombstoned()) {
                        getCurrentActivity().onRehydrate();
                    }
                }
                if (getCurrentActivity() != null) {
                    getCurrentActivity().onStart();
                    getCurrentActivity().onResume();
                    getCurrentActivity().onSetActive();
                    getCurrentActivity().onAnimateInStarted();
                    XboxTcuiSdk.getActivity().invalidateOptionsMenu();
                    to = getCurrentActivity();
                }
                if (navigationListener != null) {
                    navigationListener.onPageNavigated(from, to);
                }
                boolean unused2 = cannotNavigateTripwire = false;
            };
        }
        switch (this.animationState) {
            case NONE:
                Transition(goingBack2, popAndReplaceRunnable, animate);
                return;
            default:
                ReplaceOnAnimationEnd(goingBack2, popAndReplaceRunnable, animate);
                return;
        }
    }

    public void PopScreensAndReplace(int popCount, Class<? extends ScreenLayout> newScreenClass, boolean animate, boolean goingBack2, boolean isRestart) throws XLEException {
        PopScreensAndReplace(popCount, newScreenClass, animate, goingBack2, isRestart, (ActivityParameters) null);
    }

    public void onApplicationPause() {
        for (int i = 0; i < navigationStack.size(); i++) {
            ((ScreenLayout) navigationStack.get(i)).onApplicationPause();
        }
    }

    public void onApplicationResume() {
        for (int i = 0; i < navigationStack.size(); i++) {
            ((ScreenLayout) navigationStack.get(i)).onApplicationResume();
        }
    }

    public void setAnimationBlocking(boolean animationBlocking) {
        if (navigationCallbacks != null) {
            navigationCallbacks.setAnimationBlocking(animationBlocking);
        }
    }

    private void Transition(boolean goingBack2, Runnable lambda, boolean animate) {
        transitionLambda = lambda;
        transitionAnimate = animate;
        goingBack = goingBack2;
        currentAnimation = getCurrentActivity() == null ? null : getCurrentActivity().getAnimateOut(goingBack2);
        startAnimation(currentAnimation, NavigationManagerAnimationState.ANIMATING_OUT);
    }

    private void ReplaceOnAnimationEnd(boolean goingBack2, Runnable lambda, boolean animate) {
        XLEAssert.assertTrue(animationState == NavigationManagerAnimationState.ANIMATING_OUT || animationState == NavigationManagerAnimationState.ANIMATING_IN);
        animationState = NavigationManagerAnimationState.ANIMATING_OUT;
        transitionLambda = lambda;
        transitionAnimate = animate;
        goingBack = goingBack2;
    }

    public void OnAnimationEnd() {
        switch (animationState) {
            case ANIMATING_IN:
                if (navigationCallbacks != null) {
                    navigationCallbacks.setAnimationBlocking(false);
                }
                animationState = NavigationManagerAnimationState.NONE;
                if (getCurrentActivity() != null) {
                    getCurrentActivity().onAnimateInCompleted();
                    return;
                }
                return;
            case ANIMATING_OUT:
                transitionLambda.run();
                XLEAnimationPackage anim = null;
                if (getCurrentActivity() != null) {
                    anim = getCurrentActivity().getAnimateIn(goingBack);
                }
                if (navigationCallbacks != null) {
                    navigationCallbacks.onBeforeNavigatingIn();
                }
                startAnimation(anim, NavigationManagerAnimationState.ANIMATING_IN);
                return;
            default:
                return;
        }
    }

    private void startAnimation(XLEAnimationPackage anim, NavigationManagerAnimationState state) {
        animationState = state;
        currentAnimation = anim;
        if (navigationCallbacks != null) {
            navigationCallbacks.setAnimationBlocking(true);
        }
        if (!transitionAnimate || anim == null) {
            callAfterAnimation.run();
            return;
        }
        anim.setOnAnimationEndRunnable(callAfterAnimation);
        anim.startAnimation();
    }

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode != 4 || event.getAction() != 1) {
            return false;
        }
        if (!OnBackButtonPressed()) {
            return true;
        }
        removeNavigationCallbacks();
        removeNaviationListener();
        return false;
    }

    private enum NavigationManagerAnimationState {
        NONE,
        ANIMATING_IN,
        ANIMATING_OUT,
        COUNT
    }

    public interface NavigationCallbacks {
        void addContentViewXLE(ScreenLayout screenLayout);

        void onBeforeNavigatingIn();

        void removeContentViewXLE(ScreenLayout screenLayout);

        void setAnimationBlocking(boolean z);
    }

    public interface OnNavigatedListener {
        void onPageNavigated(ScreenLayout screenLayout, ScreenLayout screenLayout2);

        void onPageRestarted(ScreenLayout screenLayout);
    }

    private static class NavigationManagerHolder {
        public static final NavigationManager instance = new NavigationManager();

        private NavigationManagerHolder() {
        }
    }

    private class RestartRunner implements Runnable {
        private final ActivityParameters params;

        public RestartRunner(ActivityParameters params2) {
            params = params2;
        }

        public void run() {
            boolean z = true;
            boolean unused = cannotNavigateTripwire = true;
            ScreenLayout from = getCurrentActivity();
            XLEAssert.assertNotNull(from);
            getCurrentActivity().onSetInactive();
            getCurrentActivity().onPause();
            getCurrentActivity().onStop();
            if (navigationParameters.isEmpty()) {
                z = false;
            }
            XLEAssert.assertTrue("navigationParameters cannot be empty!", z);
            navigationParameters.pop();
            navigationParameters.push(params);
            getCurrentActivity().onStart();
            getCurrentActivity().onResume();
            getCurrentActivity().onSetActive();
            getCurrentActivity().onAnimateInStarted();
            XboxTcuiSdk.getActivity().invalidateOptionsMenu();
            if (navigationListener != null) {
                navigationListener.onPageRestarted(from);
            }
            boolean unused2 = cannotNavigateTripwire = false;
        }
    }
}
