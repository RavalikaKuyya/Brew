package io.github.jamiesanson.brew.util.extension

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import kotlin.reflect.KProperty

/**
 * Extension function for making observation of LiveData simple in a Fragment
 */
fun <T> LiveData<T>.observe(fragment: Fragment, callback: (T?) -> Unit) {
    this.observe(fragment.activity as LifecycleOwner, android.arch.lifecycle.Observer { item -> callback(item) })
}

/**
 * Extension function for making observation of LiveData simple in a Activities
 */
fun <T> LiveData<T>.observe(activity: AppCompatActivity, callback: (T?) -> Unit) {
    this.observe(activity as LifecycleOwner, android.arch.lifecycle.Observer { item -> callback(item) })
}

/**
 * Handy delegates for create mutable live data model fields quickly
 */
fun <T> mutableLiveData() = MutableLiveDataDelegate<T>()

class MutableLiveDataDelegate<T> {
    private var liveData: MutableLiveData<T> = MutableLiveData()

    operator fun getValue(thisRef: Any?, property: KProperty<*>): MutableLiveData<T> = liveData

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: MutableLiveData<T>) {
        liveData = value
    }
}