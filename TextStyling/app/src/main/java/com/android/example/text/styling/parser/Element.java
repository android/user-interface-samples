/*
 * Copyright (C) 2018 The Android Open Source Project
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
package com.android.example.text.styling.parser;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * Markdown like type of element.
 */
public class Element {

    public enum Type {TEXT, QUOTE, BULLET_POINT, CODE_BLOCK}

    @NonNull
    private final Type type;

    @NonNull
    private final String text;

    @NonNull
    private final List<Element> elements;

    public Element(@NonNull final Type type, @NonNull final String text,
            @NonNull final List<Element> elements) {
        this.type = type;
        this.text = text;
        this.elements = elements;
    }

    @NonNull
    public Type getType() {
        return type;
    }

    @NonNull
    public String getText() {
        return text;
    }

    @NonNull
    public List<Element> getElements() {
        return elements;
    }

    @Override
    public String toString() {
        return type + " " + text;
    }
}
