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

package com.example.android.glancewidget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.appwidget.CheckBox
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.ToggleableStateKey
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle

private val groceryStringIds = listOf(
    R.string.grocery_list_milk,
    R.string.grocery_list_eggs,
    R.string.grocery_list_tomatoes,
    R.string.grocery_list_bacon,
    R.string.grocery_list_butter,
    R.string.grocery_list_cheese,
    R.string.grocery_list_potatoes,
    R.string.grocery_list_broccoli,
    R.string.grocery_list_salmon,
    R.string.grocery_list_yogurt
)

/**
 * Glance widget that showcases how to use:
 * - Compound buttons
 * - LazyColumn
 * - State management using GlanceStateDefinition
 */
class TodoListGlanceWidget : GlanceAppWidget() {

    override var stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    @Composable
    override fun Content() {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ImageProvider(R.drawable.app_widget_background))
                .appWidgetBackground()
                .appWidgetBackgroundRadius()
                .padding(16.dp)
        ) {
            val context = LocalContext.current
            val prefs = currentState<Preferences>()
            Text(
                text = context.getString(R.string.glance_todo_list),
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .padding(8.dp),
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp),
            )
            CountChecked()
            LazyColumn {
                items(groceryStringIds) {
                    val idString = it.toString()
                    val checked = prefs[booleanPreferencesKey(idString)] ?: false
                    CheckBox(
                        text = context.getString(it),
                        checked = checked,
                        onCheckedChange = actionRunCallback<CheckboxClickAction>(
                            actionParametersOf(
                                toggledStringIdKey to idString,
                            )
                        ),
                        modifier = GlanceModifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CountChecked() {
    val prefs = currentState<Preferences>()
    val checkedCount = groceryStringIds.filter {
        prefs[booleanPreferencesKey(it.toString())] ?: false
    }.size

    Text(
        text = "$checkedCount checkboxes checked",
        modifier = GlanceModifier.padding(start = 8.dp)
    )
}

private val toggledStringIdKey = ActionParameters.Key<String>("ToggledStringIdKey")

class CheckboxClickAction : ActionCallback {
    override suspend fun onRun(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val toggledStringId = requireNotNull(parameters[toggledStringIdKey]) {
            "Add $toggledStringIdKey parameter in the ActionParameters."
        }

        // The checked state of the clicked checkbox can be added implicitly to the parameters and
        // can be retrieved by using the ToggleableStateKey
        val checked = requireNotNull(parameters[ToggleableStateKey]) {
            "This action should only be called in response to toggleable events"
        }
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) {
            it.toMutablePreferences()
                .apply { this[booleanPreferencesKey(toggledStringId)] = checked }
        }
        TodoListGlanceWidget().update(context, glanceId)
    }
}

class TodoListGlanceWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = TodoListGlanceWidget()
}
