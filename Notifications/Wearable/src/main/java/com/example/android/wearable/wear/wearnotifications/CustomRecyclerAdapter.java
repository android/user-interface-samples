/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.wearable.wear.wearnotifications;

import android.widget.ImageView;

import android.support.wearable.view.WearableRecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;

/**
 * Provides a binding from {@link NotificationCompat.Style} data set to views displayed within the
 * {@link WearableRecyclerView}.
 */
public class CustomRecyclerAdapter extends
        WearableRecyclerView.Adapter<CustomRecyclerAdapter.ViewHolder> {

    private static final String TAG = "CustomRecyclerAdapter";

    private String[] mDataSet;

    // Custom Controller used to instruct main activity to update {@link Notification} and/or
    // UI for item selected.
    private Controller mController;

    /**
     * Provides reference to the views for each data item. We don't maintain a reference to the
     * {@link ImageView} (representing the icon), because it does not change for each item. We
     * wanted to keep the sample simple, but you could add extra code to customize each icon.
     */
    public static class ViewHolder extends WearableRecyclerView.ViewHolder {

        private final TextView mTextView;

        public ViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.textView);
        }

        @Override
        public String toString() { return (String) mTextView.getText(); }
    }

    public CustomRecyclerAdapter(String[] dataSet, Controller controller) {
        mDataSet = dataSet;
        mController = controller;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_row_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");

        viewHolder.mTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mController.itemSelected(mDataSet[position]);
            }
        });

        // Replaces content of view with correct element from data set
        viewHolder.mTextView.setText(mDataSet[position]);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.length;
    }
}
