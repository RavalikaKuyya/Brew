package io.github.jamiesanson.brew

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import io.github.jamiesanson.brew.ui.main.MainActivity
import org.jetbrains.anko.startActivity

/**
 * Launcher activity which acts as a splash screen, as well as intent filter.
 */
class BrewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO - Intercept here to do intent filtering

        startActivity<MainActivity>()
        finish()
    }
}
