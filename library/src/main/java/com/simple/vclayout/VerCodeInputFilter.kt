package com.simple.vclayout

import android.text.InputFilter
import android.text.Spanned
import androidx.core.text.isDigitsOnly

class VerCodeInputFilter(private val listener: OnVerInputListener) : InputFilter {
    private val maxLength = 1

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        return if (!source.isDigitsOnly()) {
            ""
        } else {
            if (source.length > maxLength) {
                listener.onInput(source.toString())
            }
            null
        }
    }
}
