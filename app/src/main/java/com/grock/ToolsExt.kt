package com.grock

import android.content.res.Resources
import android.graphics.Typeface
import android.util.TypedValue
import android.widget.TextView

val Float.dp get():Int = (this * Resources.getSystem().displayMetrics.density).toInt()
val Float.dpF get():Float = this * Resources.getSystem().displayMetrics.density

val Int.dpF get() = this.dp.toFloat()

val Int.dp get():Int = (this * Resources.getSystem().displayMetrics.density).toInt()

var TextView.textSizePx: Int
    set(value) {textSizePx
        setTextSize(TypedValue.COMPLEX_UNIT_PX, value.toFloat())
    }
    get() = textSize.toInt()

var TextView.boldText: Boolean
    set(value) {
        if (value) {
            setTypeface(Typeface.create(typeface, Typeface.BOLD), Typeface.BOLD)
        } else {
            setTypeface(Typeface.create(typeface, Typeface.NORMAL), Typeface.NORMAL)
        }
        invalidate()
    }
    get() = typeface?.isBold ?: false