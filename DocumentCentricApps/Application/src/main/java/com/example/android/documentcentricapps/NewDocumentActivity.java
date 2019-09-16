/*
 * Copyright (C) 2014 The Android Open Source Project
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

package com.example.android.documentcentricapps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Represents a "document" in the new overview notion. This is just a placeholder.
 * Real world examples of this could be:
 *
 * <ul>
 *     <li>Document Editing</li>
 *     <li>Browser tabs</li>
 *     <li>Message composition</li>
 *     <li>Sharing</li>
 *     <li>Shopping item details</li>
 * </ul>
 */
public class NewDocumentActivity extends Activity {

    private TextView mDocumentCounterTextView;
    private int mDocumentCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_document);
        mDocumentCount = getIntent()
                .getIntExtra(DocumentCentricActivity.KEY_EXTRA_NEW_DOCUMENT_COUNTER, 0);
        mDocumentCounterTextView = (TextView) findViewById(
                R.id.hello_new_document_text_view);
        setDocumentCounterText(R.string.hello_new_document_counter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        /* If {@link Intent#FLAG_ACTIVITY_MULTIPLE_TASK} has not been used this Activity
        will be reused.
         */
        setDocumentCounterText(R.string.reusing_document_counter);
    }

    public void onRemoveFromOverview(View view) {
        // It is good pratice to remove a document from the overview stack if not needed anymore.
        finishAndRemoveTask();
    }

    public void setDocumentCounterText(int resId) {
        mDocumentCounterTextView
                .setText(String.format(getString(resId), String.valueOf(mDocumentCount)));
    }

}
