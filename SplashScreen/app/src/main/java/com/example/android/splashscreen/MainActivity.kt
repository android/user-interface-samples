package com.example.android.splashscreen

import android.app.UiModeManager
import android.os.Bundle
import android.os.Process
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.android.splashscreen.databinding.MainActivityBinding

/**
 * Shows the app content that is commonly used in all of [DefaultActivity], [AnimatedActivity], and
 * [CustomActivity]. This also handles the custom dark mode.
 */
abstract class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        // Suppress drawing the app content until the initial data is ready.
        suppressDraw()

        // Configure edge-to-edge display.
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            binding.appBar.updatePadding(
                top = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            )
            insets
        }

        // Show the Theme setting.
        var previousMode: Int? = null
        viewModel.nightMode.observe(this) { nightMode ->
            val radioButtonId = radioButtonId(nightMode)
            if (binding.theme.checkedRadioButtonId != radioButtonId) {
                binding.theme.check(radioButtonId)
            }
            if (previousMode == null) previousMode = nightMode
            if (previousMode != nightMode) recreate()
        }
        binding.theme.setOnCheckedChangeListener { _, checkedId ->
            viewModel.updateNightMode(nightMode(checkedId))
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // For the sake of this demo app, we kill the app when it is closed so we see a cold start
        // animation for each launch.
        finishAndRemoveTask()
        Process.killProcess(Process.myPid())
    }

    private fun radioButtonId(nightMode: Int) = when (nightMode) {
        UiModeManager.MODE_NIGHT_AUTO -> R.id.theme_system
        UiModeManager.MODE_NIGHT_NO -> R.id.theme_light
        UiModeManager.MODE_NIGHT_YES -> R.id.theme_dark
        else -> R.id.theme_system
    }

    private fun nightMode(radioButtonId: Int) = when (radioButtonId) {
        R.id.theme_system -> UiModeManager.MODE_NIGHT_AUTO
        R.id.theme_light -> UiModeManager.MODE_NIGHT_NO
        R.id.theme_dark -> UiModeManager.MODE_NIGHT_YES
        else -> throw RuntimeException("Unknown view ID: $radioButtonId")
    }

    private fun suppressDraw() {
        val content: View = findViewById(android.R.id.content)
        // The splash screen is dismissed as soon as the app draws its first frame. If you want to
        // load some small data asynchronously during the splash screen, you can use an
        // OnPreDrawListener to suppress drawing the app content before the data is ready.
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return if (viewModel.isReady) {
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        false
                    }
                }
            }
        )
    }
}
