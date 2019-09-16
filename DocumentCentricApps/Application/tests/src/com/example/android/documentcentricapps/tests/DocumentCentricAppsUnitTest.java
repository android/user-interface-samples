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

import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.Button;
import android.widget.CheckBox;

/**
 * Unit tests for DocumentCentricApps sample.
 */
@MediumTest
public class DocumentCentricAppsUnitTest extends ActivityUnitTestCase<DocumentCentricActivity> {

    private DocumentCentricActivity mDocumentCentricActivity;

    public DocumentCentricAppsUnitTest() {
        super(DocumentCentricActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        final Intent launchIntent = new Intent(getInstrumentation()
                .getTargetContext(), DocumentCentricActivity.class);
        mDocumentCentricActivity = startActivity(launchIntent, null, null);
    }

    public void testNewDocumentButton_IntentIsSentOnClick() {
        // Given a initialized Activity
        assertNotNull("mDocumentCentricActivity is null", mDocumentCentricActivity);
        final Button createNewDocumentButton = (Button) mDocumentCentricActivity
                .findViewById(R.id.new_document_button);
        assertNotNull(createNewDocumentButton);

        // When "Create new Document" Button is clicked
        createNewDocumentButton.performClick();

        // Then NewDocumentActivity is started with the correct flags
        final Intent newDocumentIntent = getStartedActivityIntent();
        assertNotNull("newDocumentIntent is null", newDocumentIntent);
        assertEquals("intent is missing flag FLAG_ACTIVITY_NEW_DOCUMENT", Intent.FLAG_ACTIVITY_NEW_DOCUMENT,
                newDocumentIntent.getFlags() & Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
    }

    public void testNewDocumentButton_FlagMultipleSetWhenCheckboxIsChecked() {
        // Given a initialized Activity and ticked "Create new task" checkbox
        assertNotNull("mDocumentCentricActivity is null", mDocumentCentricActivity);
        final Button createNewDocumentButton = (Button) mDocumentCentricActivity
                .findViewById(R.id.new_document_button);
        assertNotNull(createNewDocumentButton);
        final CheckBox newTaskCheckbox = (CheckBox) mDocumentCentricActivity
                .findViewById(R.id.multiple_task_checkbox);
        assertNotNull(newTaskCheckbox);
        newTaskCheckbox.setChecked(true);

        // When "Create new Document" Button is clicked
        createNewDocumentButton.performClick();

        // Then NewDocumentActivity is started with the new document and multiple task flags
        final Intent newDocumentIntent = getStartedActivityIntent();
        assertNotNull("newDocumentIntent is null", newDocumentIntent);
        assertEquals("intent is missing flag FLAG_ACTIVITY_NEW_DOCUMENT", Intent.FLAG_ACTIVITY_NEW_DOCUMENT,
                newDocumentIntent.getFlags() & Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        assertEquals("intent is missing flag FLAG_ACTIVITY_MULTIPLE_TASK", Intent.FLAG_ACTIVITY_MULTIPLE_TASK,
                newDocumentIntent.getFlags() & Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
    }

}