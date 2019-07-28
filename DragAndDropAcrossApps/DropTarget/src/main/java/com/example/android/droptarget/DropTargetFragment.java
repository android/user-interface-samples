/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.droptarget;

import com.example.android.common.logger.Log;

import android.app.Activity;
import android.content.ClipDescription;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v13.app.ActivityCompat;
import android.support.v13.view.DragAndDropPermissionsCompat;
import android.support.v4.app.Fragment;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

/**
 * This sample demonstrates data can be moved between views in different applications via
 * drag and drop.
 * <p>This is the Target app for the drag and drop process. This app uses a
 * {@link android.widget.ImageView} as the drop target. Images onto this
 * view from the DragSource app that is also part of this sample.
 * <p>
 * There is also a {@link android.widget.EditText} widget that can accept dropped text (no
 * extra setup is necessary).
 * To access content URIs requiring permissions, the target app needs to request the
 * {@link android.view.DragAndDropPermissions} from the Activity via
 * {@link ActivityCompat#requestDragAndDropPermissions(Activity, DragEvent)}. This permission will
 * stay either as long as the activity is alive, or until the release() method is called on the
 * {@link android.view.DragAndDropPermissions} object.
 */
public class DropTargetFragment extends Fragment {

    private static final String IMAGE_URI = "IMAGE_URI";

    public static final String EXTRA_IMAGE_INFO = "IMAGE_INFO";

    private static final String TAG = "DropTargetFragment";

    private Uri mImageUri;

    private CheckBox mReleasePermissionCheckBox;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_droptarget, container, false);
        final ImageView imageView = (ImageView) rootView.findViewById(R.id.image_target);

        ImageDragListener imageDragListener = new PermissionAwareImageDragListener();

        imageView.setOnDragListener(imageDragListener);

        // Restore the application state if an image was being displayed.
        if (savedInstanceState != null) {
            final String uriString = savedInstanceState.getString(IMAGE_URI);
            if (uriString != null) {
                mImageUri = Uri.parse(uriString);
                imageView.setImageURI(mImageUri);
            }
        }

        mReleasePermissionCheckBox = (CheckBox) rootView.findViewById(R.id.release_checkbox);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (mImageUri != null) {
            savedInstanceState.putString(IMAGE_URI, mImageUri.toString());
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    private class PermissionAwareImageDragListener extends ImageDragListener {

        @Override
        protected void processLocation(float x, float y) {
            // Callback is received when the dragged image enters the drop area.
        }

        @Override
        protected boolean setImageUri(View view, DragEvent event, Uri uri) {
            // Read the string from the clip description extras.
            Log.d(TAG, "ClipDescription extra: " + getExtra(event));

            Log.d(TAG, "Setting image source to: " + uri.toString());
            mImageUri = uri;

            if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
                // Accessing a "content" scheme Uri requires a permission grant.
                DragAndDropPermissionsCompat dropPermissions = ActivityCompat
                        .requestDragAndDropPermissions(getActivity(), event);
                Log.d(TAG, "Requesting permissions.");

                if (dropPermissions == null) {
                    // Permission could not be obtained.
                    Log.d(TAG, "Drop permission request failed.");
                    return false;
                }

                final boolean result = super.setImageUri(view, event, uri);

                if (mReleasePermissionCheckBox.isChecked()) {
                    /* Release the permissions if you are done with the URI.
                     Note that you may need to hold onto the permission until later if other
                     operations are performed on the content. For instance, releasing the
                     permissions here will prevent onCreateView from properly restoring the
                     ImageView state.
                     If permissions are not explicitly released, the permission grant will be
                     revoked when the activity is destroyed.
                     */
                    dropPermissions.release();
                    Log.d(TAG, "Permissions released.");
                }

                return result;
            } else {
                // Other schemes (such as "android.resource") do not require a permission grant.
                return super.setImageUri(view, event, uri);
            }
        }

        @Override
        public boolean onDrag(View view, DragEvent event) {
            // DragTarget is peeking into the MIME types of the dragged event in order to ignore
            // non-image drags completely.
            // DragSource does not do that but rejects non-image content once a drop has happened.
            ClipDescription clipDescription = event.getClipDescription();
            if (clipDescription != null && !clipDescription.hasMimeType("image/*")) {
                return false;
            }
            // Callback received when image is being dragged.
            return super.onDrag(view, event);
        }
    }

    /**
     * DragEvents can contain additional data packaged in a {@link PersistableBundle}.
     * Extract the extras from the event and return the String stored for the
     * {@link #EXTRA_IMAGE_INFO} entry.
     */
    private String getExtra(DragEvent event) {
        // The extras are contained in the ClipDescription in the DragEvent.
        ClipDescription clipDescription = event.getClipDescription();
        if (clipDescription != null) {
            PersistableBundle extras = clipDescription.getExtras();
            if (extras != null) {
                return extras.getString(EXTRA_IMAGE_INFO);
            }
        }
        return null;
    }
}
