/*
 * Copyright (C) 2017 The Android Open Source Project
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

package com.example.android.downloadablefonts;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Builder class for constructing a query for downloading a font.
 */
class QueryBuilder {

    @NonNull
    private String mFamilyName;

    @Nullable
    private Float mWidth = null;

    @Nullable
    private Integer mWeight = null;

    @Nullable
    private Float mItalic = null;

    @Nullable
    private Boolean mBesteffort = null;

    QueryBuilder(@NonNull String familyName) {
        mFamilyName = familyName;
    }

    QueryBuilder withFamilyName(@NonNull String familyName) {
        mFamilyName = familyName;
        return this;
    }

    QueryBuilder withWidth(float width) {
        if (width <= Constants.WIDTH_MIN) {
            throw new IllegalArgumentException("Width must be more than 0");
        }
        mWidth = width;
        return this;
    }

    QueryBuilder withWeight(int weight) {
        if (weight <= Constants.WEIGHT_MIN || weight >= Constants.WEIGHT_MAX) {
            throw new IllegalArgumentException(
                    "Weight must be between 0 and 1000 (exclusive)");
        }
        mWeight = weight;
        return this;
    }

    QueryBuilder withItalic(float italic) {
        if (italic < Constants.ITALIC_MIN || italic > Constants.ITALIC_MAX) {
            throw new IllegalArgumentException("Italic must be between 0 and 1 (inclusive)");
        }
        mItalic = italic;
        return this;
    }

    QueryBuilder withBestEffort(boolean bestEffort) {
        mBesteffort = bestEffort;
        return this;
    }

    String build() {
        if (mWeight == null && mWidth == null && mItalic == null && mBesteffort == null) {
            return mFamilyName;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("name=").append(mFamilyName);
        if (mWeight != null) {
           builder.append("&weight=").append(mWeight);
        }
        if (mWidth != null) {
            builder.append("&width=").append(mWidth);
        }
        if (mItalic != null) {
            builder.append("&italic=").append(mItalic);
        }
        if (mBesteffort != null) {
            builder.append("&besteffort=").append(mBesteffort);
        }
        return builder.toString();
    }
}
