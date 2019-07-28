/*
* Copyright 2016, The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.droptarget;

import android.content.ClipData;
import android.net.Uri;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;

public class ImageDragListener implements View.OnDragListener {

    private static final int COLOR_INACTIVE = 0xFF888888;

    private static final int COLOR_ACTIVE = 0xFFCCCCCC;

    private static final int COLOR_HOVER = 0xFFEEEEEE;

    @Override
    public boolean onDrag(View view, DragEvent event) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                setTargetColor(view, COLOR_ACTIVE);
                return true;

            case DragEvent.ACTION_DRAG_ENTERED:
                setTargetColor(view, COLOR_HOVER);
                return true;

            case DragEvent.ACTION_DRAG_LOCATION:
                processLocation(event.getX(), event.getY());
                return true;

            case DragEvent.ACTION_DRAG_EXITED:
                setTargetColor(view, COLOR_ACTIVE);
                return true;

            case DragEvent.ACTION_DROP:
                return processDrop(view, event);

            case DragEvent.ACTION_DRAG_ENDED:
                setTargetColor(view, COLOR_INACTIVE);
                return true;

            default:
                break;
        }

        return false;
    }

    private void setTargetColor(View view, int color) {
        view.setBackgroundColor(color);
    }

    private boolean processDrop(View view, DragEvent event) {
        ClipData clipData = event.getClipData();
        if (clipData == null || clipData.getItemCount() == 0) {
            return false;
        }
        ClipData.Item item = clipData.getItemAt(0);
        if (item == null) {
            return false;
        }
        Uri uri = item.getUri();
        if (uri == null) {
            return false;
        }
        return setImageUri(view, event, uri);
    }

    protected void processLocation(float x, float y) {
    }

    protected boolean setImageUri(View view, DragEvent event, Uri uri) {
        if (!(view instanceof ImageView)) {
            return false;
        }
        ((ImageView) view).setImageURI(uri);
        return true;
    }
}
