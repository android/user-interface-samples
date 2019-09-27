/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.wearable.wear.wearnotifications;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

/**
 * Shrinks items (children) farther away from the center in a {@link WearableRecyclerView}. The UX
 * makes scrolling more readable.
 */
public class ScalingScrollLayoutCallback extends WearableLinearLayoutManager.LayoutCallback {

    // Max we scale the child View.
    private static final float MAX_CHILD_SCALE = 0.65f;

    private float mProgressToCenter;

    /*
     * Scales the item's icons and text the farther away they are from center allowing the main
     * item to be more readable to the user on small devices like Wear.
     */
    @Override
    public void onLayoutFinished(View child, RecyclerView parent) {

        // Figure out % progress from top to bottom.
        float centerOffset = ((float) child.getHeight() / 2.0f) /  (float) parent.getHeight();
        float yRelativeToCenterOffset = (child.getY() / parent.getHeight()) + centerOffset;

        // Normalizes for center.
        mProgressToCenter = Math.abs(0.5f - yRelativeToCenterOffset);

        // Adjusts to the maximum scale.
        mProgressToCenter = Math.min(mProgressToCenter, MAX_CHILD_SCALE);

        child.setScaleX(1 - mProgressToCenter);
        child.setScaleY(1 - mProgressToCenter);
    }
}
