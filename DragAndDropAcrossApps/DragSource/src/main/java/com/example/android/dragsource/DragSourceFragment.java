/*
 * Copyright 2015, The Android Open Source Project
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

package com.example.android.dragsource;

import com.example.android.common.logger.Log;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v13.view.DragStartHelper;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;


/**
 * This sample demonstrates data can be moved between views within the app or between different
 * apps via drag and drop.
 * <p>This is the source app for the drag and drop sample. This app contains several
 * {@link android.widget.ImageView} widgets which can be a drag source. Images can be dropped
 * to a drop target area within the same app or in the DropTarget app (a separate app in this
 * sample).
 * <p>
 * There is also one {@link android.widget.EditText} widget that can be a drag source (no extra
 * setup is necessary).
 * <p/>
 * To enable cross application drag and drop, the {@link android.view.View#DRAG_FLAG_GLOBAL}
 * permission needs to be passed to the {@link android.view.View#startDragAndDrop(ClipData,
 * View.DragShadowBuilder, Object, int)} method. If a Uri
 * requiring permission grants is being sent, then the
 * {@link android.view.View#DRAG_FLAG_GLOBAL_URI_READ} and/or the
 * {@link android.view.View#DRAG_FLAG_GLOBAL_URI_WRITE} flags must be used also.
 */
public class DragSourceFragment extends Fragment {

    /**
     * Name of saved data that stores the dropped image URI on the local ImageView when set.
     */
    private static final String IMAGE_URI = "IMAGE_URI";

    /**
     * Name of the parameter for a {@link ClipData} extra that stores a text describing the dragged
     * image.
     */
    public static final String EXTRA_IMAGE_INFO = "IMAGE_INFO";

    /**
     * Uri of the ImageView source when set.
     */
    private Uri mLocalImageUri;

    private static final String TAG = "DragSourceFragment";

    private static final String CONTENT_AUTHORITY = "com.example.android.dragsource.fileprovider";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dragsource, null);

        // Set up two image views for global drag and drop with a permission grant.
        Uri imageUri = getFileUri(R.drawable.image1, "image1.png");
        ImageView imageView = (ImageView) view.findViewById(R.id.image_one);
        setUpDraggableImage(imageView, imageUri);
        imageView.setImageURI(imageUri);

        imageUri = getFileUri(R.drawable.image2, "image2.png");
        imageView = (ImageView) view.findViewById(R.id.image_two);
        setUpDraggableImage(imageView, imageUri);
        imageView.setImageURI(imageUri);

        // Set up the local drop target area.
        final ImageView localImageTarget = (ImageView) view.findViewById(R.id.local_target);
        localImageTarget.setOnDragListener(new ImageDragListener() {
            @Override
            protected boolean setImageUri(View view, DragEvent event, Uri uri) {
                mLocalImageUri = uri;
                Log.d(TAG, "Setting local image to: " + uri);
                return super.setImageUri(view, event, uri);
            }
        });

        if (savedInstanceState != null) {
            final String uriString = savedInstanceState.getString(IMAGE_URI);
            if (uriString != null) {
                mLocalImageUri = Uri.parse(uriString);
                Log.d(TAG, "Restoring local image to: " + mLocalImageUri);
                localImageTarget.setImageURI(mLocalImageUri);
            }
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (mLocalImageUri != null) {
            savedInstanceState.putString(IMAGE_URI, mLocalImageUri.toString());
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    private void setUpDraggableImage(ImageView imageView, final Uri imageUri) {

        // Set up a listener that starts the drag and drop event with flags and extra data.
        DragStartHelper.OnDragStartListener listener = new DragStartHelper.OnDragStartListener() {
            @Override
            public boolean onDragStart(View view, final DragStartHelper helper) {
                Log.d(TAG, "Drag start event received from helper.");

                // Use a DragShadowBuilder
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view) {
                    @Override
                    public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
                        super.onProvideShadowMetrics(shadowSize, shadowTouchPoint);
                        // Notify the DragStartHelper of point where the view was touched.
                        helper.getTouchPosition(shadowTouchPoint);
                        Log.d(TAG, "View was touched at: " + shadowTouchPoint);
                    }
                };

                // Set up the flags for the drag event.
                // Enable drag and drop across apps (global)
                // and require read permissions for this URI.
                int flags = View.DRAG_FLAG_GLOBAL | View.DRAG_FLAG_GLOBAL_URI_READ;

                // Add an optional clip description that that contains an extra String that is
                // read out by the target app.
                final ClipDescription clipDescription = new ClipDescription("", new String[]{
                        getContext().getContentResolver().getType(imageUri)});
                // Extras are stored within a PersistableBundle.
                PersistableBundle extras = new PersistableBundle(1);
                // Add a String that the target app will display.
                extras.putString(EXTRA_IMAGE_INFO,
                        "Drag Started at " + new Date());
                clipDescription.setExtras(extras);

                // The ClipData object describes the object that is being dragged and dropped.
                final ClipData clipData =
                        new ClipData(clipDescription, new ClipData.Item(imageUri));

                Log.d(TAG, "Created ClipDescription. Starting drag and drop.");
                // Start the drag and drop event.
                return view.startDragAndDrop(clipData, shadowBuilder, null, flags);

            }

        };

        // Use the DragStartHelper to detect drag and drop events and use the OnDragStartListener
        // defined above to start the event when it has been detected.
        DragStartHelper helper = new DragStartHelper(imageView, listener);
        helper.attach();
        Log.d(TAG, "DragStartHelper attached to view.");
    }

    /**
     * Copy a drawable resource into local storage and makes it available via the
     * {@link FileProvider}.
     *
     * @see Context#getFilesDir()
     * @see FileProvider
     * @see FileProvider#getUriForFile(Context, String, File)
     */
    private Uri getFileUri(int sourceResourceId, String targetName) {
        // Create the images/ sub directory if it does not exist yet.
        File filePath = new File(getContext().getFilesDir(), "images");
        if (!filePath.exists() && !filePath.mkdir()) {
            return null;
        }

        // Copy a drawable from resources to the internal directory.
        File newFile = new File(filePath, targetName);
        if (!newFile.exists()) {
            copyImageResourceToFile(sourceResourceId, newFile);
        }

        // Make the file accessible via the FileProvider and retrieve its URI.
        return FileProvider.getUriForFile(getContext(), CONTENT_AUTHORITY, newFile);
    }


    /**
     * Copy a PNG resource drawable to a {@File}.
     */
    private void copyImageResourceToFile(int resourceId, File filePath) {
        Bitmap image = BitmapFactory.decodeResource(getResources(), resourceId);

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filePath);
            image.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
