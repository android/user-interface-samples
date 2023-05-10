package com.example.android.splashscreen

import android.app.UiModeManager
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.android.splashscreen.databinding.MainActivityBinding

/**
 * Shows the app content that is commonly used in all of [DefaultActivity], [AnimatedActivity], and
 * [CustomActivity]. This also handles the custom dark mode on API level 31 and above.
 */
abstract class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    protected lateinit var splashScreen: SplashScreen

    override fun onCreate(savedInstanceState: Bundle?) {
        // Set up 'core-splashscreen' to handle the splash screen in a backward compatible manner.
        splashScreen = installSplashScreen()

        // The splash screen remains on the screen as long as this condition is true.
        splashScreen.setKeepOnScreenCondition { !viewModel.isReady }

        super.onCreate(savedInstanceState)

        val binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        // Configure edge-to-edge display.
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            binding.appBar.updatePadding(
                top = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            )
            insets
        }

        // Show the in-app dark theme settings. This is available on API level 31 and above.
        if (Build.VERSION.SDK_INT >= 31) {
            viewModel.nightMode.observe(this) { nightMode ->
                val radioButtonId = radioButtonId(nightMode)
                if (binding.theme.checkedRadioButtonId != radioButtonId) {
                    binding.theme.check(radioButtonId)
                }
            }
            binding.theme.setOnCheckedChangeListener { _, checkedId ->
                viewModel.updateNightMode(nightMode(checkedId))
            }
        } else {
            binding.content.visibility = View.INVISIBLE
        }

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // For the sake of this demo app, we kill the app process when the back button is
                // pressed so we see a cold start animation for each launch.
                finishAndRemoveTask()
                Process.killProcess(Process.myPid())
            }
        })
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
}
