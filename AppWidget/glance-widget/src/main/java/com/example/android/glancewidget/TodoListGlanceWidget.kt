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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.appwidget.CheckBox
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
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
                .background(color = Color.White)
                .padding(8.dp)
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
            CountToggled()
            LazyColumn {
                items(groceryStringIds) {
                    val toggledString = context.getString(it)
                    val checked = prefs[booleanPreferencesKey(toggledString)] ?: false
                    CheckBox(
                        text = toggledString,
                        checked = prefs[booleanPreferencesKey(toggledString)] ?: false,
                        onCheckedChange = actionRunCallback<CheckboxClickAction>(
                            actionParametersOf(
                                toggledKey to toggledString,
                                toggledValue to !checked
                            )
                        ),
                        modifier = GlanceModifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

private val toggledKey = ActionParameters.Key<String>("ToggledKey")
private val toggledValue = ActionParameters.Key<Boolean>("ToggledValue")

class CheckboxClickAction : ActionCallback {
    override suspend fun onRun(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val toggledString = requireNotNull(parameters[toggledKey]) {
            "Add $toggledKey parameter in the ActionParameters."
        }
        // TODO: Remove this parameter once the checked state can be retrieved through
        // ToggleableStateKey.
        val checked = requireNotNull(parameters[toggledValue]) {
            "Add $toggledValue parameter in the ActionParameters."
        }

        // TODO: Uncomment this once the checked state is retrieved from the action parameters
        // in a lazy list
//        val checked = requireNotNull(parameters[ToggleableStateKey]) {
//            "This action should only be called in response to toggleable events"
//        }
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) {
            it.toMutablePreferences()
                .apply { set(booleanPreferencesKey(toggledString), checked) }
        }
        TodoListGlanceWidget().update(context, glanceId)
    }
}

@Composable
private fun CountToggled() {
    val prefs = currentState<Preferences>()
    val context = LocalContext.current
    val checkedCount = groceryStringIds.filter {
        prefs[booleanPreferencesKey(context.getString(it))] ?: false
    }.size

    Text(
        text = "$checkedCount checkboxes checked",
        modifier = GlanceModifier.padding(start = 8.dp)
    )
}

class TodoListGlanceWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = TodoListGlanceWidget()
}
