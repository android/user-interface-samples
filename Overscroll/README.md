Overscroll Sample
=====================

## Overscroll

Overscroll is an effect when the user hits the content boundary and continues to scroll by either explicit dragging or by a fling. The current default Android Overscroll is a “glow” effect that appears on the content edge and reflects the x/y coordinate changes. For Android 12, Overscroll will provide a new, system-wide “stretch all” effect to the overshoot behavior.
The new design will have a more natural scroll stop indicator, moving away from a pervasive, blue glow.

## Enabling Stretch Over Scroll

On Android 12, stretch over scroll is enabled by default. The "glow" edge effect is no longer supported and all applications that use the existing EdgeEffect will now stretch.

## Views with Over Scroll

Framework Views that use EdgeEffect have been modified to work with the stretch over scroll:
* AbsListView
* ScrollView
* HorizonalScrollView

Internal Views have been adjusted to support stretch over scroll:
* RecyclerView
* ViewPager

Jetpack Views have also be adjusted to support stretch over scroll:
* RecyclerView
* ViewPager
* NestedScrollView

### Stretch EdgeEffect Usage

There are a few behaviors that need to be modified to have the best user experience with stretch over scroll.
When the user releases and touches the contents during the release animation, the touch should register as a "catch." The user stops the animation and begins manipulating the stretch again.
When the user moves the finger in the opposite direction of the stretch, it should release the stretch until it is fully gone and then begin scrolling.
When the user "flings" during a stretch, that fling should fling the EdgeEffect to enhance the stretch effect.

[EdgeEffect](https://developer.android.com/reference/android/widget/EdgeEffect)

```
    float getDistance()
    float onPullDistance(float deltaDistance, float displacement)
```

There are a few behaviors that need to be modified to have the best user experience with stretch over scroll.
* When the user releases and touches the contents during the release animation, the touch should register as a "catch." The user stops the animation and begins manipulating the stretch again.
* When the user moves the finger in the opposite direction of the stretch, it should release the stretch until it is fully gone and then begin scrolling.
* When the user "flings" during a stretch, that fling should fling the EdgeEffect to enhance the stretch effect.

## Catching the Animation

When a user catches an active stretch animation, the EdgeEffect.isFinished() will return false. This will indicate that the stretch should be manipulated by the touch motion. In most containers, this will be detected in onInterceptTouchEvent():

```
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
    ...
      switch (action & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:
          ...
          mIsBeingDragged = !mEdgeEffectBottom.isFinished()
                || !mEdgeEffectTop.isFinished();
          ...
```

In this example, onInterceptTouchEvent() will return true when mIsBeingDragged is true, so it is sufficient for consuming the event before the child has an opportunity to consume it.

Examples:
* [ScrollView]()
* [RecyclerView]()

## Releasing the Over Scroll Effect

It is important to release the stretch effect prior to scrolling or else the stretch will be applied to the scrolling content.

```
    @Override
    public boolean onTouchEvent(MotionEvent ev) {

      final int actionMasked = ev.getActionMasked();

      switch (actionMasked) {
        case MotionEvent.ACTION_MOVE:
          final float x = ev.getX(activePointerIndex);
          final float y = ev.getY(activePointerIndex);
          float deltaY = y - mLastMotionY;
          float pullDistance = deltaY / getHeight();
          float displacement = x / getWidth();

          if (deltaY < 0 && mEdgeEffectTop.getDistance() > 0) {
            deltaY -= getHeight() * mEdgeEffectTop
                .onPullDistance(pullDistance, displacement);
          }
          if (deltaY > 0 && mEdgeEffectBottom.getDistance() > 0) {
            deltaY += getHeight() * mEdgeEffectBottom
                .onPullDistance(-pullDistance, 1 - displacement);
          }
          ...
```

Examples:
* [ScrollView]()
* [RecyclerView]()

When dragging, before passing the touch event to nested scrolling or dragging the scroll, the 
EdgeEffect's pull distance must be consumed. Above, getDistance() will return a positive value when an edge effect is being displayed and can be released with motion. When the touch event releases the stretch, it is first consumed by the EdgeEffect so that it will be completely released before other effects, such as nested scrolling, are displayed. You can use getDistance() to learn how much pull distance is required to release the current effect.

The onPullDistance() differs from onPull() by returning the consumed amount of the passed delta. onPull() allows negative values for the total distance for glow effects (not stretch effects), while onPullDistance() limits the value to a minimum of zero for all effect types.

## Fling During Over Scroll

The stretch over scroll UX is better when the user sees an exaggerated stretch when flinging while stretching – and it makes it fun to play with. This is simply a matter of detecting the fling and calling onAbsorb().

```
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
    //…
      switch (actionMasked) {
        case MotionEvent.ACTION_UP:
          if (mIsBeingDragged) {
            final VelocityTracker velocityTracker = mVelocityTracker;
            velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
            int initialVelocity = (int) velocityTracker.getYVelocity(mActivePointerId);

          if ((Math.abs(initialVelocity) > mMinimumVelocity)) {
            if (!mEdgeGlowTop.isFinished()) {
              mEdgeGlowTop.onAbsorb(-initialVelocity);
            } else if (!mEdgeGlowBottom.isFinished()) {
              mEdgeGlowBottom.onAbsorb(velocityY);
            }
     //...
```

Examples:
* [ScrollView]()
* [RecyclerView]()

### Layering Views

We must still support a stretch effect when applied to Views that don't have a parent/child relationship, such as a WebView with Android Views layered on top. When this is done, the EdgeEffect should have the same effects applied to all affected Views. The Views layered on top of the WebView should belong to one ViewGroup and have the stretch EdgeEffect applied to it. The WebView should have a stretch EdgeEffect applied to it as well.

The easiest way to accomplish this is to move the EdgeEffect usage to a ViewGroup parent that contains both the WebView and the contained parents. The stretch will then be applied to all children of the ViewGroup.

When this isn't possible, two different EdgeEffects will have to synchronize their pull distance. The controlling EdgeEffect, where [EdgeEffect.onRelease()](https://developer.android.com/reference/android/widget/EdgeEffect#onRelease()) and [EdgeEffect.onAbsorb()](https://developer.android.com/reference/android/widget/EdgeEffect#onAbsorb(int)) is called, provides the pull distance to the controlled EdgeEffect. Whenever EdgeEffect.draw() is called, EdgeEffect.getDistance() can be used to feed the pull distance to the controlled EdgeEffect.

[WebView](https://developer.android.com/reference/android/webkit/WebView) does not currently support modifying its [EdgeEffect](https://developer.android.com/reference/android/widget/EdgeEffect) or retrieving it, so apps must place the content as children of the WebView.


