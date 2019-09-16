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

package com.example.android.documentcentricapps.tests;

import com.example.android.documentcentricapps.DocumentCentricActivity;
import com.example.android.documentcentricapps.R;

import android.app.ActivityManager;
import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.suitebuilder.annotation.LargeTest;
import android.widget.Button;

import java.util.List;

/**
 * Instrumentation tests for DocumentCentricApps sample.
 */
@LargeTest
public class DocumentCentricAppsInstrumentationTest extends
        ActivityInstrumentationTestCase2<DocumentCentricActivity> {

    private DocumentCentricActivity mDocumentCentricActivity;

    public DocumentCentricAppsInstrumentationTest() {
        super(DocumentCentricActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mDocumentCentricActivity = getActivity();
    }

    public void testNewDocument_CreatesOverviewEntry() {
        // Given a initialized Activity
        assertNotNull("mDocumentCentricActivity is null", mDocumentCentricActivity);
        final Button createNewDocumentButton = (Button) mDocumentCentricActivity
                .findViewById(R.id.new_document_button);
        assertNotNull(createNewDocumentButton);

        // When "Create new Document" Button is clicked
        TouchUtils.clickView(this, createNewDocumentButton);

        // Then a entry in overview app tasks is created.
        List<ActivityManager.AppTask> recentAppTasks = getRecentAppTasks();
        assertEquals("# of recentAppTasks does not match", 2, recentAppTasks.size());
    }

    private List<ActivityManager.AppTask> getRecentAppTasks() {
        ActivityManager activityManager = (ActivityManager) getInstrumentation().getTargetContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.AppTask> appTasks = activityManager.getAppTasks();
        return appTasks;
    }

}