package com.example.android.text.demo.hyphenation

import android.annotation.SuppressLint
import android.graphics.text.LineBreaker
import android.os.Build
import android.os.Bundle
import android.text.Layout
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ScrollView
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import com.example.android.text.R
import com.example.android.text.databinding.HyphenationFragmentBinding
import com.example.android.text.ui.utils.doOnItemSelected
import com.example.android.text.ui.viewBindings

/**
 * Demonstrates different options for the `android:hyphenationFrequency` attribute, including
 * 'fullFast' and 'normalFast' introduced in Android 13 (API level 33). Their behaviors are the same
 * as 'full' and 'normal', but uses new algorithm with improved performance. The attribute itself
 * has been available since Android Marshmallow (API level 23).
 */
@RequiresApi(23)
class HyphenationFragment : Fragment(R.layout.hyphenation_fragment) {

    private val binding by viewBindings(HyphenationFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Set up edge-to-edge display.
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.controlLayout.updatePadding(bottom = systemBars.bottom)
            insets
        }
        // Set up the control.
        binding.hyphenationFrequency.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            android.R.id.text1,
            HyphenationFrequencyOption.values().map { it.label }
        )
        binding.hyphenationFrequency.doOnItemSelected { _, position, _ ->
            binding.paragraphs.hyphenationFrequency =
                HyphenationFrequencyOption.values()[position].value
        }
    }
}

@SuppressLint("InlinedApi")
private enum class HyphenationFrequencyOption(
    val value: Int,
    val label: String
) {
    FullFast(Layout.HYPHENATION_FREQUENCY_FULL_FAST, "fullFast (API 33+)"),
    Full(Layout.HYPHENATION_FREQUENCY_FULL, "full"),
    None(Layout.HYPHENATION_FREQUENCY_NONE, "none"),
    NormalFast(Layout.HYPHENATION_FREQUENCY_NORMAL_FAST, "normalFast (API 33+)"),
    Normal(Layout.HYPHENATION_FREQUENCY_NORMAL, "normal"),
}
