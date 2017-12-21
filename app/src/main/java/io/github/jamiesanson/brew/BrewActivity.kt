package io.github.jamiesanson.brew

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

/**
 * Launcher activity which acts as a splash screen, as well as intent filter.
 */
class BrewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
