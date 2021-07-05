/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.windowmanagersample;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;
import androidx.window.WindowInfoRepo;
import androidx.window.WindowLayoutInfo;
import androidx.window.java.WindowInfoRepoJavaAdapter;
import com.example.windowmanagersample.databinding.ActivitySplitLayoutBinding;

public class SplitLayoutActivity extends AppCompatActivity {

    private WindowInfoRepoJavaAdapter windowInfoRepo;
    private ActivitySplitLayoutBinding binding;
    private final LayoutStateChangeCallback layoutStateChangeCallback =
            new LayoutStateChangeCallback();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySplitLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        windowInfoRepo = new WindowInfoRepoJavaAdapter(WindowInfoRepo.create(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        windowInfoRepo.addWindowLayoutInfoListener(Runnable::run, layoutStateChangeCallback);
    }

    @Override
    protected void onStop() {
        super.onStop();
        windowInfoRepo.removeWindowLayoutInfoListener(layoutStateChangeCallback);
    }

    class LayoutStateChangeCallback implements Consumer<WindowLayoutInfo> {
        @Override
        public void accept(WindowLayoutInfo windowLayoutInfo) {
            binding.splitLayout.updateWindowLayout(windowLayoutInfo);
        }
    }
}
