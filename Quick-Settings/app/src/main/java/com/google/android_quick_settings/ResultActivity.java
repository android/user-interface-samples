// Copyright 2016 Google Inc.
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//      http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.android_quick_settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class ResultActivity extends AppCompatActivity {

    public static final String RESULT_ACTIVITY_INFO_KEY = "resultActivityInfo";
    public static final String RESULT_ACTIVITY_NAME_KEY = "resultActivityName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        if (getIntent() != null) {
            Bundle extras = getIntent().getExtras();

            String tileState = extras.getString(RESULT_ACTIVITY_INFO_KEY);
            String tileName = extras.getString(RESULT_ACTIVITY_NAME_KEY);

            TextView outputText = findViewById(R.id.result_info);
            outputText.setText(getString(R.string.result_output, tileName, tileState));

            TextView returnHome = findViewById(R.id.result_return_main);
            returnHome.setOnClickListener(view -> {
                Intent goHome = new Intent(ResultActivity.this, MainActivity.class);
                startActivity(goHome);
            });
        }
    }
}
