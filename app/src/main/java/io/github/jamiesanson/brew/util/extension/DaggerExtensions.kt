package io.github.jamiesanson.brew.util.extension

import android.support.v7.app.AppCompatActivity
import io.github.jamiesanson.brew.BrewApp

// Used to simplify injection in Activities
val AppCompatActivity.component
    get() = (this.application as BrewApp).applicationComponent