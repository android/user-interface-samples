/*
 * Copyright (C) 2021 The Android Open Source Project
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

package com.example.android.appwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import androidx.core.os.BuildCompat
import com.example.android.appwidget.ItemsCollectionRemoteViewsFactory.Companion.EXTRA_VIEW_ID
import com.example.android.appwidget.ItemsCollectionRemoteViewsFactory.Companion.REQUEST_CODE
import com.example.android.appwidget.ItemsCollectionRemoteViewsFactory.Companion.REQUEST_CODE_FROM_COLLECTION_WIDGET
import com.example.android.appwidget.ItemsCollectionRemoteViewsFactory.Companion.getRemoteCollectionItems

/**
 * Implementation of App Widget functionality that demonstrates the difference of how the list of
 * items are inflated with API level 31 and older API levels.
 * This widget also demonstrates the compound buttons (Checkbox, RadioButton, Switch), that are now
 * supported in widgets starting from API level 31.
 */
class ItemsCollectionAppWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_items_collection)
        if (BuildCompat.isAtLeastS()) {
            val collectionItems = getRemoteCollectionItems(context)
            remoteViews.setRemoteAdapter(R.id.items_list_view, collectionItems)
        } else {
            remoteViews.setRemoteAdapter(
                R.id.items_list_view,
                Intent(context, ItemsCollectionRemoteViewsService::class.java)
            )
        }
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (BuildCompat.isAtLeastS() &&
            intent?.extras?.getInt(REQUEST_CODE) == REQUEST_CODE_FROM_COLLECTION_WIDGET
        ) {
            val checked = intent.extras?.getBoolean(
                RemoteViews.EXTRA_CHECKED,
                false
            )
            Toast.makeText(
                context,
                "ViewId : ${intent.extras?.getInt(EXTRA_VIEW_ID)}'s checked status is now : $checked",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

class ItemsCollectionRemoteViewsService : RemoteViewsService() {

    override fun onGetViewFactory(data: Intent?): RemoteViewsFactory {
        return ItemsCollectionRemoteViewsFactory(applicationContext)
    }
}

class ItemsCollectionRemoteViewsFactory(private val context: Context) :
    RemoteViewsService.RemoteViewsFactory {

    override fun onCreate() {}

    override fun onDataSetChanged() {}

    override fun onDestroy() {}

    override fun getCount(): Int = items.count()

    override fun getViewAt(position: Int): RemoteViews {
        return constructRemoteViews(context, items[position])
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = items.count()

    override fun getItemId(position: Int): Long = position.toLong()

    override fun hasStableIds(): Boolean = true

    companion object {
        val items = listOf(
            R.layout.item_checkboxes,
            R.layout.item_radio_buttons,
            R.layout.item_switches,
        )

        const val REQUEST_CODE_FROM_COLLECTION_WIDGET = 2
        const val EXTRA_VIEW_ID = "extra_view_id"
        const val REQUEST_CODE = "request_code"

        @RequiresApi(31)
        fun getRemoteCollectionItems(context: Context): RemoteViews.RemoteCollectionItems {
            val builder = RemoteViews.RemoteCollectionItems.Builder()
            items.forEachIndexed { index, layoutId ->
                builder.addItem(index.toLong(), constructRemoteViews(context, layoutId))
            }
            return builder.setHasStableIds(true).setViewTypeCount(items.count()).build()
        }

        internal fun constructRemoteViews(
            context: Context,
            @LayoutRes layoutId: Int
        ): RemoteViews {
            val remoteViews = RemoteViews(context.packageName, layoutId)
            if (!BuildCompat.isAtLeastS()) {
                return remoteViews
            }
            // Compound buttons in a widget are stateless. You need to change the state and register for
            // the state change events.
            when (layoutId) {
                R.layout.item_checkboxes -> {
                    // This code will check the Checkbox
                    remoteViews.setCompoundButtonChecked(R.id.item_checkbox, true)
                }
                R.layout.item_radio_buttons -> {
                    // This code will check the item_radio_button2 in the item_radio_group RadioGroup
                    remoteViews.setRadioGroupChecked(
                        R.id.item_radio_group,
                        R.id.item_radio_button2
                    )
                }
                R.layout.item_switches -> {
                    val viewId = R.id.item_switch
                    val onCheckedChangePendingIntent = PendingIntent.getBroadcast(
                        context,
                        REQUEST_CODE_FROM_COLLECTION_WIDGET,
                        Intent(context, ItemsCollectionAppWidget::class.java).apply {
                            putExtra(EXTRA_VIEW_ID, viewId)
                            putExtra(REQUEST_CODE, REQUEST_CODE_FROM_COLLECTION_WIDGET)
                        },
                        // API level 31 requires specifying either of
                        // PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_MUTABLE
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                    )
                    // Listen for change events.
                    // RemoteResponse.fromPendingIntent works on an individual item whereas you can set
                    // a PendingIntent template using RemoteViews.setPendingIntentTemplate and
                    // distinguish individual on-click by calling RemoteResponse.fromFillInIntent.
                    // See
                    // https://developer.android.com/reference/android/widget/RemoteViews.RemoteResponse#fromPendingIntent(android.app.PendingIntent)
                    // https://developer.android.com/reference/android/widget/RemoteViews.RemoteResponse#fromFillInIntent(android.content.Intent)
                    // for more details.
                    remoteViews.setOnCheckedChangeResponse(
                        viewId,
                        RemoteViews.RemoteResponse.fromPendingIntent(
                            onCheckedChangePendingIntent
                        )
                    )
                }
            }
            return remoteViews
        }
    }
}