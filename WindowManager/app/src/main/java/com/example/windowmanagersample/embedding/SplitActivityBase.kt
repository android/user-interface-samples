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

package com.example.windowmanagersample.embedding

import android.app.Activity
import android.app.PendingIntent
import android.app.PendingIntent.CanceledException
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.LayoutDirection
import android.util.Log
import android.util.TypedValue
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Consumer
import androidx.window.core.ExperimentalWindowApi
import androidx.window.embedding.ActivityFilter
import androidx.window.embedding.ActivityRule
import androidx.window.embedding.SplitController
import androidx.window.embedding.SplitInfo
import androidx.window.embedding.SplitPairFilter
import androidx.window.embedding.SplitPairRule
import androidx.window.embedding.SplitPlaceholderRule
import androidx.window.embedding.SplitRule
import com.example.windowmanagersample.databinding.ActivitySplitActivityLayoutBinding

/**
 * Sample showcase of split activity rules. Allows the user to select some split configuration
 * options with checkboxes and launch activities with those options applied.
 */
@OptIn(ExperimentalWindowApi::class)
open class SplitActivityBase : AppCompatActivity(), CompoundButton.OnCheckedChangeListener {
    private lateinit var splitController: SplitController
    private var callback: Consumer<List<SplitInfo>>? = null
    private lateinit var binding: ActivitySplitActivityLayoutBinding

    // Flag indicating that the config is being updated from checkboxes changes in a loop.
    private var updatingConfigs = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplitActivityLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup activity launch buttons.
        binding.launchB.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    SplitActivityB::class.java
                )
            )
        }
        binding.launchBAndC.setOnClickListener {
            val bStartIntent = Intent(this, SplitActivityB::class.java)
            bStartIntent.putExtra(EXTRA_LAUNCH_C_TO_SIDE, true)
            startActivity(bStartIntent)
        }
        binding.launchE.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    SplitActivityE::class.java
                )
            )
        }
        binding.launchF.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    SplitActivityF::class.java
                )
            )
        }
        binding.launchFPendingIntent.setOnClickListener {
            try {
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, SplitActivityF::class.java),
                    PendingIntent.FLAG_IMMUTABLE
                )
                    .send()
            } catch (e: CanceledException) {
                Log.e(TAG, e.message!!)
            }
        }

        // Listen for split configuration checkboxes to update the rules before launching
        // activities.
        binding.splitMainCheckBox.setOnCheckedChangeListener(this)
        binding.usePlaceholderCheckBox.setOnCheckedChangeListener(this)
        binding.useStickyPlaceholderCheckBox.setOnCheckedChangeListener(this)
        binding.splitBCCheckBox.setOnCheckedChangeListener(this)
        binding.finishBCCheckBox.setOnCheckedChangeListener(this)
        binding.fullscreenECheckBox.setOnCheckedChangeListener(this)
        binding.splitWithFCheckBox.setOnCheckedChangeListener(this)
        splitController = SplitController.getInstance()
    }

    override fun onStart() {
        super.onStart()
        callback = Consumer<List<SplitInfo>> {
            updateCheckboxesFromCurrentConfig()
        }.also {
            splitController.addSplitListener(
                this@SplitActivityBase,
                { obj: Runnable -> obj.run() },
                it
            )
        }
    }

    override fun onStop() {
        super.onStop()
        callback?.let { splitController.removeSplitListener(it) }
        callback = null
    }

    override fun onCheckedChanged(c: CompoundButton, isChecked: Boolean) {
        if (updatingConfigs) {
            return
        }
        if (c.id == binding.splitBCCheckBox.id) {
            if (isChecked) {
                binding.finishBCCheckBox.isEnabled = true
            } else {
                binding.finishBCCheckBox.isEnabled = false
                binding.finishBCCheckBox.isChecked = false
            }
        } else if (c.id == binding.usePlaceholderCheckBox.id) {
            if (isChecked) {
                binding.useStickyPlaceholderCheckBox.isEnabled = true
            } else {
                binding.useStickyPlaceholderCheckBox.isEnabled = false
                binding.useStickyPlaceholderCheckBox.isChecked = false
            }
        }
        updateRulesFromCheckboxes()
    }

    private fun updateCheckboxesFromCurrentConfig() {
        updatingConfigs = true
        val splitMainConfig = getRuleFor(SplitActivityA::class.java, null)
        binding.splitMainCheckBox.isChecked = splitMainConfig != null
        val placeholderForBConfig = getPlaceholderRule(SplitActivityB::class.java)
        binding.usePlaceholderCheckBox.isChecked = placeholderForBConfig != null
        binding.useStickyPlaceholderCheckBox.isEnabled = placeholderForBConfig != null
        binding.useStickyPlaceholderCheckBox.isChecked = (
            placeholderForBConfig != null &&
                placeholderForBConfig.isSticky
            )
        val bAndCPairConfig = getRuleFor(SplitActivityB::class.java, SplitActivityC::class.java)
        binding.splitBCCheckBox.isChecked = bAndCPairConfig != null
        binding.finishBCCheckBox.isEnabled = bAndCPairConfig != null
        binding.finishBCCheckBox.isChecked = (
            bAndCPairConfig != null &&
                bAndCPairConfig.finishPrimaryWithSecondary == SplitRule.FINISH_ALWAYS &&
                bAndCPairConfig.finishSecondaryWithPrimary == SplitRule.FINISH_ALWAYS
            )
        val fConfig = getRuleFor(null, SplitActivityF::class.java)
        binding.splitWithFCheckBox.isChecked = fConfig != null
        val configE = getRuleFor(SplitActivityE::class.java)
        binding.fullscreenECheckBox.isChecked = configE != null && configE.alwaysExpand
        updatingConfigs = false
    }

    private fun getRuleFor(a: Class<out Activity?>?, b: Class<out Activity?>?): SplitPairRule? {
        val currentRules = splitController.getSplitRules()
        for (rule in currentRules) {
            if (rule is SplitPairRule && isRuleFor(a, b, rule)) {
                return rule
            }
        }
        return null
    }

    private fun getPlaceholderRule(a: Class<out Activity?>): SplitPlaceholderRule? {
        val currentRules = splitController.getSplitRules()
        for (rule in currentRules) {
            if (rule is SplitPlaceholderRule) {
                for (filter in rule.filters) {
                    if (filter.componentName.className == a.name) {
                        return rule
                    }
                }
            }
        }
        return null
    }

    private fun getRuleFor(a: Class<out Activity?>): ActivityRule? {
        val currentRules = splitController.getSplitRules()
        for (rule in currentRules) {
            if (rule is ActivityRule && isRuleFor(a, rule)) {
                return rule
            }
        }
        return null
    }

    private fun isRuleFor(
        a: Class<out Activity?>?,
        b: Class<out Activity?>?,
        pairConfig: SplitPairRule
    ): Boolean {
        return isRuleFor(if (a != null) a.name else "*", if (b != null) b.name else "*", pairConfig)
    }

    private fun isRuleFor(
        primaryActivityName: String,
        secondaryActivityName: String,
        pairConfig: SplitPairRule
    ): Boolean {
        for (filter in pairConfig.filters) {
            if (filter.primaryActivityName.className.contains(primaryActivityName) &&
                filter.secondaryActivityName.className.contains(secondaryActivityName)
            ) {
                return true
            }
        }
        return false
    }

    private fun isRuleFor(a: Class<out Activity?>?, config: ActivityRule): Boolean {
        return isRuleFor(if (a != null) a.name else "*", config)
    }

    private fun isRuleFor(activityName: String, config: ActivityRule): Boolean {
        for (filter in config.filters) {
            if (filter.componentName.className.contains(activityName)) {
                return true
            }
        }
        return false
    }

    private fun updateRulesFromCheckboxes() {
        val minSplitWidth = minSplitWidth()
        splitController.clearRegisteredRules()

        if (binding.splitMainCheckBox.isChecked) {
            splitController.registerRule(
                SplitPairRule(
                    filters = setOf(
                        SplitPairFilter(
                            primaryActivityName = componentName(SplitActivityA::class.java),
                            secondaryActivityName = componentName("*"), null
                        )
                    ),
                    finishPrimaryWithSecondary = SplitRule.FINISH_NEVER,
                    finishSecondaryWithPrimary = SplitRule.FINISH_NEVER,
                    clearTop = true,
                    minWidth = minSplitWidth,
                    minSmallestWidth = 0,
                    splitRatio = SPLIT_RATIO,
                    layoutDir = LayoutDirection.LOCALE
                )
            )
        }

        if (binding.usePlaceholderCheckBox.isChecked) {
            val intent = Intent()
            intent.component = componentName(
                "com.example.windowmanagersample.embedding.SplitActivityPlaceholder"
            )
            splitController.registerRule(
                SplitPlaceholderRule(
                    filters = setOf(
                        ActivityFilter(
                            componentName(SplitActivityB::class.java), null
                        )
                    ),
                    placeholderIntent = intent,
                    isSticky = binding.useStickyPlaceholderCheckBox.isChecked,
                    finishPrimaryWithSecondary = SplitRule.FINISH_ADJACENT,
                    minWidth = minSplitWidth,
                    minSmallestWidth = 0,
                    splitRatio = SPLIT_RATIO,
                    layoutDirection = LayoutDirection.LOCALE
                )
            )
        }

        if (binding.splitBCCheckBox.isChecked) {
            splitController.registerRule(
                SplitPairRule(
                    filters = setOf(
                        SplitPairFilter(
                            primaryActivityName = componentName(SplitActivityB::class.java),
                            secondaryActivityName = componentName(SplitActivityC::class.java),
                            secondaryActivityIntentAction = null
                        )
                    ),
                    finishPrimaryWithSecondary = if (binding.finishBCCheckBox.isChecked)
                        SplitRule.FINISH_ALWAYS else SplitRule.FINISH_NEVER,
                    finishSecondaryWithPrimary = if (binding.finishBCCheckBox.isChecked)
                        SplitRule.FINISH_ALWAYS else SplitRule.FINISH_NEVER,
                    clearTop = true,
                    minWidth = minSplitWidth,
                    minSmallestWidth = minSplitWidth,
                    splitRatio = SPLIT_RATIO,
                    layoutDir = LayoutDirection.LOCALE
                )
            )
        }

        if (binding.splitWithFCheckBox.isChecked) {
            splitController.registerRule(
                SplitPairRule(
                    filters = setOf(
                        SplitPairFilter(
                            primaryActivityName = componentName("androidx.window.*"),
                            secondaryActivityName = componentName(SplitActivityF::class.java),
                            secondaryActivityIntentAction = null
                        )
                    ),
                    finishPrimaryWithSecondary = SplitRule.FINISH_NEVER,
                    finishSecondaryWithPrimary = SplitRule.FINISH_NEVER,
                    clearTop = true,
                    minWidth = minSplitWidth,
                    minSmallestWidth = minSplitWidth,
                    splitRatio = SPLIT_RATIO,
                    layoutDir = LayoutDirection.LOCALE
                )
            )
        }

        if (binding.fullscreenECheckBox.isChecked) {
            splitController.registerRule(
                ActivityRule(
                    filters = setOf(
                        ActivityFilter(
                            componentName(SplitActivityE::class.java),
                            null
                        )
                    ),
                    alwaysExpand = true
                )
            )
        }
    }

    private fun componentName(activityClass: Class<out Activity?>?): ComponentName {
        return ComponentName(
            packageName, if (activityClass != null) activityClass.name else "*"
        )
    }

    fun componentName(className: String?): ComponentName {
        return ComponentName(packageName, className!!)
    }

    fun minSplitWidth(): Int {
        val dm = resources.displayMetrics
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            MIN_SPLIT_WIDTH_DP,
            dm
        ).toInt()
    }

    companion object {
        private const val TAG = "SplitActivityTest"
        private const val MIN_SPLIT_WIDTH_DP = 600f
        const val SPLIT_RATIO = 0.3f
        const val EXTRA_LAUNCH_C_TO_SIDE = "launch_c_to_side"
    }
}
