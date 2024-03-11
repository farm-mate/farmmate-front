package com.example.farmmate1

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

fun String?.toEditable(): Editable? {
    return Editable.Factory.getInstance().newEditable(this)
}

fun Editable?.toStringOrNull(): String? {
    return this?.toString()
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Nothing to do here
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Nothing to do here
        }

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
    })
}
