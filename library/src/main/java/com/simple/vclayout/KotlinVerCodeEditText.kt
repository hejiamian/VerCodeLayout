package com.simple.vclayout

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.InputFilter
import android.text.InputType
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.ColorInt
import androidx.core.view.ViewCompat

class KotlinVerCodeEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : VerCodeLayout(context, attrs, defStyleAttr) {
    //输入框的总数
    private val mCount: Int

    //最大输入长度
    private val mMaxLength: Int

    /**
     *
     */
    private val mNormalBackground: Drawable?
    private val mFocusedBackground: Drawable?

    /**
     *
     */
    private val mWidth: Int
    private val mHeight: Int
    private val mMinWidth: Int
    private val mMinHeight: Int

    /**
     * set
     */
    private val mTextSize: Float

    @ColorInt
    private val mTextColor: Int
    private val mTextCursorDrawable: Int
    private val mGravity: Int
    private val mInputType: Int

    /**
     *
     */
    private val mMargin: Int
    private val mMarginLeft: Int
    private val mMarginTop: Int
    private val mMarginRight: Int
    private val mMarginBottom: Int

    /**
     *
     */
    private val mPadding: Int
    private val mPaddingLeft: Int
    private val mPaddingTop: Int
    private val mPaddingRight: Int
    private val mPaddingBottom: Int

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.VerCodeEditText)
        //
        mCount = ta.getInt(R.styleable.VerCodeEditText_vcCount, 0)
        mMaxLength = ta.getInt(R.styleable.VerCodeEditText_vcMaxLength, 1)
        //
        mNormalBackground = ta.getDrawable(R.styleable.VerCodeEditText_vcNormalBackground)
        mFocusedBackground = ta.getDrawable(R.styleable.VerCodeEditText_vcFocusedBackground)
        //
        mTextSize = ta.getDimensionPixelSize(R.styleable.VerCodeEditText_vcTextSize, 0).toFloat()
        mTextColor = ta.getColor(R.styleable.VerCodeEditText_vcTextColor, Color.BLACK)
        mTextCursorDrawable = ta.getResourceId(R.styleable.VerCodeEditText_vcTextCursorDrawable, -1)
        mGravity = ta.getInt(R.styleable.VerCodeEditText_vcGravity, Gravity.CENTER)
        mInputType = ta.getInt(R.styleable.VerCodeEditText_vcInputType, InputType.TYPE_CLASS_NUMBER)
        //
        mWidth = ta.getDimension(R.styleable.VerCodeEditText_vcWidth, 0f).toInt()
        mHeight = ta.getDimension(R.styleable.VerCodeEditText_vcHeight, 0f).toInt()
        mMinWidth = ta.getDimensionPixelSize(R.styleable.VerCodeEditText_vcMinWidth, 0)
        mMinHeight = ta.getDimensionPixelSize(R.styleable.VerCodeEditText_vcMinHeight, 0)
        //
        mMargin = ta.getDimension(R.styleable.VerCodeEditText_vcMargin, 0f).toInt()
        mMarginLeft = ta.getDimension(R.styleable.VerCodeEditText_vcMarginLeft, 0f).toInt()
        mMarginTop = ta.getDimension(R.styleable.VerCodeEditText_vcMarginTop, 0f).toInt()
        mMarginRight = ta.getDimension(R.styleable.VerCodeEditText_vcMarginRight, 0f).toInt()
        mMarginBottom = ta.getDimension(R.styleable.VerCodeEditText_vcMarginBottom, 0f).toInt()
        //
        mPadding = ta.getDimension(R.styleable.VerCodeEditText_vcPadding, -1f).toInt()
        mPaddingLeft = ta.getDimension(R.styleable.VerCodeEditText_vcPaddingLeft, -1f).toInt()
        mPaddingTop = ta.getDimension(R.styleable.VerCodeEditText_vcPaddingTop, -1f).toInt()
        mPaddingRight = ta.getDimension(R.styleable.VerCodeEditText_vcPaddingRight, -1f).toInt()
        mPaddingBottom = ta.getDimension(R.styleable.VerCodeEditText_vcPaddingBottom, -1f).toInt()
        //
        autoFocus = ta.getBoolean(R.styleable.VerCodeEditText_vcAutoFocus, true)
        enterFocus = ta.getBoolean(R.styleable.VerCodeEditText_vcEnterFocus, true)
        ta.recycle()
        createEditTexts()
    }

    //创建EditText们
    private fun createEditTexts() {
        if (mCount <= 0) return
        for (i in 0 until mCount) {
            val editText = EditText(context)
            //
            if (i == 0 && enterFocus) {
                editText.requestFocus()
            }
            if (i > 0 && autoFocus) {
                editText.isEnabled = false
            }
            //
            setDefault(editText)
            //margin
            val params = marginLayoutParams
            //background
            setBackground(editText)
            //padding
            setPadding(editText)
            //addView
            this.addView(editText, params)
        }
    }

    //设置默认的属性
    private fun setDefault(editText: EditText) {
        editText.maxLines = 1
        if (mTextSize != 0f) {
            editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize)
        }
        editText.setTextColor(mTextColor)
        if (mTextCursorDrawable != -1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                editText.setTextCursorDrawable(mTextCursorDrawable)
            }
        }
        editText.gravity = Gravity.CENTER
        editText.minWidth = mMinWidth
        editText.minHeight = mMinHeight
        editText.gravity = mGravity
        editText.inputType = mInputType
        val inputFilter = VerCodeInputFilter(object : OnVerInputListener {
            override fun onInput(text: String) {
                for (i in text.indices) {
                    val child = getChildAt(i)
                    if (child is EditText) {
                        child.setText(text[i].toString())
                    }
                }
            }
        })
        editText.filters = arrayOf<InputFilter>(inputFilter)
    }

    private fun setBackground(editText: EditText) {
        editText.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            val d = (if (hasFocus) mFocusedBackground else mNormalBackground)
                ?: return@OnFocusChangeListener
            ViewCompat.setBackground(v, d)
        }
        mNormalBackground?.let {
            ViewCompat.setBackground(editText, it)
        }
    }

    private fun setPadding(editText: EditText) {
        if (mPadding != -1) {
            editText.setPadding(mPadding, mPadding, mPadding, mPadding)
        } else {
            if (editText.background != null) {
                val d = editText.background
                val r = Rect()
                d.getPadding(r)
                val left = if (mPaddingLeft != -1) mPaddingLeft else r.left
                val top = if (mPaddingTop != -1) mPaddingTop else r.top
                val right = if (mPaddingRight != -1) mPaddingRight else r.right
                val bottom = if (mPaddingBottom != -1) mPaddingBottom else r.bottom
                editText.setPadding(left, top, right, bottom)
            } else {
                editText.setPadding(mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom)
            }
        }
    }

    private val marginLayoutParams: MarginLayoutParams
        get() {
            val params = MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            if (mMargin != 0) {
                params.leftMargin = mMargin
                params.topMargin = mMargin
                params.rightMargin = mMargin
                params.bottomMargin = mMargin
            } else {
                params.leftMargin = mMarginLeft
                params.topMargin = mMarginTop
                params.rightMargin = mMarginRight
                params.bottomMargin = mMarginBottom
            }
            if (mWidth != 0) {
                params.width = mWidth
            }
            if (mHeight != 0) {
                params.height = mHeight
            }
            return params
        }
}