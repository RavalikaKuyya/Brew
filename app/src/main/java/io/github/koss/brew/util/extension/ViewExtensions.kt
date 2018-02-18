package io.github.koss.brew.util.extension

import android.text.Editable
import android.text.TextWatcher

class OnTextChanged(private val callback: (CharSequence) -> Unit) : TextWatcher {

    override fun onTextChanged(
            s: CharSequence,
            start: Int,
            before: Int,
            count: Int
    ) {
        callback(s)
    }

    override fun afterTextChanged(s: Editable?) {

    }

    override fun beforeTextChanged(
            s: CharSequence?,
            start: Int,
            count: Int,
            after: Int
    ) {
    }
}
