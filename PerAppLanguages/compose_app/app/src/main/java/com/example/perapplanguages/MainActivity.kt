/*
 * Copyright (C) 2022 The Android Open Source Project
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

package com.example.perapplanguages

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import com.example.perapplanguages.ui.theme.PerAppLanguagesTheme

// For now, extend from AppCompatActivity.
// Otherwise, setApplicationLocales will do nothing.
//
// Extending from AppCompatActivity requires to use an AppCompat theme for the Activity.
// In the manifest, for the activity, use android:theme="@style/Theme.AppCompat"
// Otherwise, the application will crash.
//
// The alternative is to replace AppCompatDelegate with the Framework APIs.
// The Frameworks APIs are not backwards compatible, like AppCompatDelegate, and so work for T+.
// However, with the Framework APIs, you can use Compose themes and extend from ComponentActivity.
// Framework APIs: https://developer.android.com/about/versions/13/features/app-languages#framework-impl
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PerAppLanguagesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Screen()
                }
            }
        }
    }
}

@Composable
fun Screen() {
    Column (
        modifier = Modifier
            .wrapContentSize(Alignment.TopCenter)
            .padding(top = 48.dp)
    ) {
        Text(
            text = stringResource(id = R.string.greeting),
            fontSize = 30.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        LocaleDropdownMenu()
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LocaleDropdownMenu() {

    val localeOptions = mapOf(
        R.string.en to "en",
        R.string.fr to "fr",
        R.string.hi to "hi",
        R.string.ja to "ja"
    ).mapKeys { stringResource(it.key) }

    // boilerplate: https://developer.android.com/reference/kotlin/androidx/compose/material/package-summary#ExposedDropdownMenuBox(kotlin.Boolean,kotlin.Function1,androidx.compose.ui.Modifier,kotlin.Function1)
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        TextField(
            readOnly = true,
            value = stringResource(R.string.language),
            onValueChange = { },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            localeOptions.keys.forEach { selectionLocale ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        // set app locale given the user's selected locale
                        AppCompatDelegate.setApplicationLocales(
                            LocaleListCompat.forLanguageTags(
                                localeOptions[selectionLocale]
                            )
                        )
                    },
                    content = { Text(selectionLocale) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PerAppLanguagesTheme {
        Screen()
    }
}