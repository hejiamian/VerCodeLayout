package com.simple.vclayout

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout

class KotlinVerCodeLayout @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private var mEditTexts: MutableList<EditText> = mutableListOf()
    private var mOnCompleteListener: OnCompleteListener? = null

    //是否自动聚焦，不可点击聚焦
    var autoFocus = true

    //焦点是否默认选中
    var enterFocus = true

    override fun onFinishInflate() {
        super.onFinishInflate()
        mEditTexts.clear()
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child is EditText) {
                setupEditText(child)
            }
        }
    }

    //基本配置
    private fun setupEditText(editText: EditText) {
        mEditTexts.add(editText)
        editText.setOnKeyListener(InnerKeyListener(editText))
        editText.addTextChangedListener(InnerTextWatcher(editText))
    }

    //按键监听
    internal inner class InnerKeyListener(var innerEditText: EditText) : OnKeyListener {
        override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
            val textLength = innerEditText.text.length
            if (keyCode == KeyEvent.KEYCODE_DEL && textLength == 0) {
                focusPrevious(innerEditText)
                return true
            }
            return false
        }
    }

    //输入文本监听
    internal inner class InnerTextWatcher(var innerEditText: EditText) : TextWatcher {
        var maxLength = 1
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable) {
            val count = s.length
            require(maxLength != 0) {
                resources.getString(R.string.exception_no_max_length)
            }
            if (count >= maxLength) {
                focusNext(innerEditText)
            }
        }
    }

    //移动焦点到下一个输入框
    protected fun focusNext(et: EditText) {
        val index = mEditTexts.indexOf(et)
        if (index < mEditTexts.size - 1) {
            val nextEt = mEditTexts[index + 1]
            nextEt.isEnabled = true
            nextEt.requestFocus()
            if (autoFocus) {
                et.isEnabled = false
            }
            return
        }
        //完成监听
        if (mOnCompleteListener != null) {
            val editable = Editable.Factory.getInstance().newEditable("")
            for (editText in mEditTexts) {
                editable.append(editText.text)
            }
            mOnCompleteListener?.onComplete(editable, editable.toString())
        }
    }

    //移动焦点到上一个输入框
    protected fun focusPrevious(focusEditText: EditText) {
        val index = mEditTexts.indexOf(focusEditText)
        if (index != 0) {
            val preEditText = mEditTexts[index - 1]
            preEditText.isEnabled = true
            preEditText.requestFocus()
            preEditText.post {
                preEditText.setText("")
            }
            if (autoFocus) {
                focusEditText.isEnabled = false
            }
        }
    }

    //清除所有文本
    fun clear() {
        if (mEditTexts.isEmpty()) return
        for (editText in mEditTexts) {
            editText.setText("")
            editText.clearFocus()
        }
        val first = mEditTexts[0]
        first.isEnabled = true
        first.requestFocus()
    }

    //所有的EditText
    val editTexts: List<EditText>
        get() = mEditTexts

    //输入完成监听事件
    fun setOnCompleteListener(listener: OnCompleteListener?) {
        mOnCompleteListener = listener
    }

    interface OnCompleteListener {
        fun onComplete(editable: Editable?, code: String?)
    }
}