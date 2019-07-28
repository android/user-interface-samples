/*      Copyright 2018 Google LLC All rights reserved.

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
*/
package com.hadrosaur.draganddropdemo;

import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Color;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static android.view.View.DRAG_FLAG_GLOBAL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView dragText = findViewById(R.id.text_drag);
        FrameLayout targetFrame = findViewById(R.id.frame_target);

        //Set up drop target listener.
        targetFrame.setOnDragListener(new DropTargetListener(this));

        //Set up draggable item listener.
        dragText.setOnLongClickListener(new TextViewLongClickListener());
    }

    protected class DropTargetListener implements View.OnDragListener {
        private AppCompatActivity mActivity;

        public DropTargetListener(AppCompatActivity mActivity) {
            this.mActivity = mActivity;
        }

        @Override
        public boolean onDrag(View v, DragEvent event) {
            final int action = event.getAction();

            switch(action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // Limit the types of items this can receive to plain-text and Chrome OS files
                    if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                            || event.getClipDescription().hasMimeType("application/x-arc-uri-list")) {

                        // Greenify background colour so user knows this is a target.
                        v.setBackgroundColor(Color.argb(55,0,255,0));
                        return true;
                    }

                    //If the dragged item is of an undesired type, report that this is not a valid target
                    return false;

                case DragEvent.ACTION_DRAG_ENTERED:
                    // Increase green background colour when item is over top of target.
                    v.setBackgroundColor(Color.argb(150,0,255,0));
                    return true;

                case DragEvent.ACTION_DRAG_LOCATION:
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    // Less intense green background colour when item not over target.
                    v.setBackgroundColor(Color.argb(55,0,255,0));
                    return true;

                case DragEvent.ACTION_DROP:
                    requestDragAndDropPermissions(event); //Allow items from other applications
                    ClipData.Item item = event.getClipData().getItemAt(0);

                    if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        //If this is a text item, simply display it in a new TextView.
                        FrameLayout frameTarget = (FrameLayout) v;
                        frameTarget.removeAllViews();

                        TextView dropped_text = new TextView(mActivity);
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                        params.gravity = Gravity.CENTER;
                        dropped_text.setLayoutParams(params);

                        dropped_text.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
                        dropped_text.setText(item.getText());

                        frameTarget.addView(dropped_text);

                    } else if (event.getClipDescription().hasMimeType("application/x-arc-uri-list")) {
                        //If a file, read the first 200 characters and output them in a new TextView.

                        //Note the use of ContentResolver to resolve the ChromeOS content URI.
                        Uri contentUri = item.getUri();
                        ParcelFileDescriptor parcelFileDescriptor;
                        try {
                            parcelFileDescriptor = getContentResolver().openFileDescriptor(contentUri, "r");
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            Log.e("DRAGTEST", "File not found.");
                            return false;
                        }

                        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();

                        final int MAX_LENGTH = 5000;
                        byte[] bytes = new byte[MAX_LENGTH];

                        try {
                            FileInputStream in = new FileInputStream(fileDescriptor);
                            try {
                                in.read(bytes,0, MAX_LENGTH);
                            } finally {
                                in.close();
                            }
                        } catch (Exception ex) {}
                        String contents = new String(bytes);

                        final int CHARS_TO_READ = 200;
                        int content_length = (contents.length() > CHARS_TO_READ) ? CHARS_TO_READ : 0;

                        FrameLayout frameTarget = (FrameLayout) v;
                        frameTarget.removeAllViews();

                        TextView dropped_text = new TextView(mActivity);
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                        params.gravity = Gravity.CENTER;
                        dropped_text.setLayoutParams(params);

                        dropped_text.setTextSize(TypedValue.COMPLEX_UNIT_SP,10);
                        dropped_text.setText(contents.substring(0,content_length));

                        frameTarget.addView(dropped_text);

                    } else {
                        return false;
                    }
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    // Restore background colour to transparent.
                    v.setBackgroundColor(Color.argb(0,255,255,255));
                    return true;

                default:
                    Log.e("DragDrop Example", "Unknown action type received by DropTargetListener.");
                    return false;
            }
        }
    }

    protected class TextViewLongClickListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View v) {
            TextView thisTextView = (TextView) v;
            String dragContent = "Dragged Text: " + thisTextView.getText();

            //Set the drag content and type.
            ClipData.Item item = new ClipData.Item(dragContent);
            ClipData dragData = new ClipData(dragContent,
                    new String[] {ClipDescription.MIMETYPE_TEXT_PLAIN}, item);

            //Set the visual look of the dragged object.
            //Can be extended and customized. Default is used here.
            View.DragShadowBuilder dragShadow = new View.DragShadowBuilder(v);

            // Starts the drag, note: global flag allows for cross-application drag.
            v.startDragAndDrop(dragData, dragShadow, null, DRAG_FLAG_GLOBAL);

            return false;
        }
    }

}
