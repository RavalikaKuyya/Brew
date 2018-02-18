package io.github.koss.brew.util.extension

import android.support.v7.app.AppCompatActivity
import io.github.koss.brew.BrewApp

// Used to simplify injection in Activities
val AppCompatActivity.component
    get() = (this.application as BrewApp).applicationComponent