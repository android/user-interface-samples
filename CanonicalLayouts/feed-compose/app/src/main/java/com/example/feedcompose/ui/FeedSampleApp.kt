/*
 * Copyright 2022 The Android Open Source Project
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

package com.example.feedcompose.ui

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.feedcompose.data.DataProvider
import com.example.feedcompose.data.Sweets
import com.example.feedcompose.ui.screen.SweetsDetails
import com.example.feedcompose.ui.screen.SweetsFeed

@Composable
fun FeedSampleApp(windowSizeClass: WindowSizeClass) {
    val navController = rememberNavController()
    val router = Router(navController)
    NavHost(navController = navController, startDestination = "/") {
        composable(Destination.Feed.path) {
            SweetsFeed(windowSizeClass = windowSizeClass) {
                router.showSweets(it)
            }
        }
        composable(
            Destination.Details.path,
            arguments = listOf(navArgument("sweetsId") { type = NavType.IntType })
        ) {
            val selectedSweetsId = it.arguments?.getInt("sweetsId") ?: 0
            SweetsDetails(
                sweets = DataProvider.getSweetsById(selectedSweetsId),
                windowSizeClass = windowSizeClass
            ) {
                navController.popBackStack()
            }
        }
    }
}

private sealed interface Destination {
    val base: String
    val path: String

    object Feed : Destination {
        override val base: String = "/"
        override val path: String = base
    }

    object Details : Destination {
        override val base: String = "/show"
        override val path: String = "$base/{sweetsId}"
    }
}

private class Router(val navController: NavController) {
    fun showSweets(sweets: Sweets) {
        navController.navigate("${Destination.Details.base}/${sweets.id}")
    }
}
