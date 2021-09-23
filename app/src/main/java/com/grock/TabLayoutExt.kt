package com.grock

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.doOnDetach
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.grock.tc.R

interface CustomTabViewConfig<T : View> {
    fun getCustomView(context: Context): T
    fun onTabInit(tabView: T, position: Int)
}

class FixedWidthTextTabView(context: Context) : FrameLayout(context) {
    /**
     * 用于测量时能获得最大尺寸,
     */
    val boundSizeTextView: TextView = TextView(context).apply {
        visibility = View.INVISIBLE
    }
    val dynamicSizeTextView: ScaleTexViewTabView = ScaleTexViewTabView(context).apply {
        gravity = Gravity.CENTER
    }
    init {
        addView(boundSizeTextView)
        addView(dynamicSizeTextView)
    }
}

abstract class FixedWidthTextTabViewConfig(val scale: TextScaleConfig) :
    CustomTabViewConfig<FixedWidthTextTabView> {
    abstract fun getText(position: Int): String
    abstract fun onVisibleTextViewInit(tv: TextView)
    final override fun onTabInit(tabView: FixedWidthTextTabView, position: Int) {
        val text = getText(position)
        tabView.boundSizeTextView.text = text
        tabView.boundSizeTextView.textSizePx = scale.onSelectTextSize
        if(scale.switchBold){
            tabView.boundSizeTextView.typeface = Typeface.DEFAULT_BOLD
        }
        tabView.dynamicSizeTextView.text = text
        tabView.dynamicSizeTextView.textSizePx = scale.onUnSelectTextSize
        onVisibleTextViewInit(tabView.dynamicSizeTextView)
    }

    final override fun getCustomView(context: Context): FixedWidthTextTabView {
        return FixedWidthTextTabView(context)
    }
}

data class TextScaleConfig(
    val onSelectTextSize: Int,
    val onUnSelectTextSize: Int,
    val switchBold:Boolean = true
)

private val defaultScaleConfig = TextScaleConfig(18.dp, 16.dp)

abstract class TextScaleTabViewConfig(scale: TextScaleConfig = defaultScaleConfig) :
    FixedWidthTextTabViewConfig(
        scale
    )

private fun TabLayout.addScaleAnim(config: TextScaleConfig = defaultScaleConfig) {
    val key = R.id.tag_tabLayout_scale_ext
    val lastListener = getTag(key)
    if (lastListener != null) return
    val duration = 50L
    val animCacheMap = HashMap<TextView, ValueAnimator>()
    fun cancelLastAnim(tv: TextView) {
        animCacheMap[tv]?.cancel()
    }
    doOnDetach {
        animCacheMap.forEach {
            it.value.cancel()
        }
    }
    val listener = object : TabLayout.OnTabSelectedListener {
        private fun scaleTextSize(stv: ScaleTexViewTabView, size: Int, isBold: Boolean) {
            cancelLastAnim(stv)
            val animator =
                ValueAnimator.ofInt(stv.textSize.toInt(), size)
            animator.duration = duration
            animator.addUpdateListener { anim ->
                val textSize = anim.animatedValue as? Int
                textSize?.let { ts ->
                    stv.textSizePx = size
                }
            }
            animator.doOnStart {
                stv.skipRequestLayout = true
            }
            animator.doOnEnd {
                /*最后做一下修正*/
                stv.textSizePx = size
                if(config.switchBold){
                    stv.boldText = isBold
                }
                stv.skipRequestLayout = false
                animCacheMap.remove(stv)
            }
            animCacheMap[stv] = animator
            animator.start()
        }

        override fun onTabSelected(tab: TabLayout.Tab) {
            val cusV = tab.customView
            if (cusV is FixedWidthTextTabView) {
                val onSelectTextSize = config.onSelectTextSize
                val onUnSelectTextSize = config.onUnSelectTextSize
                if (onSelectTextSize != onUnSelectTextSize) {
                    scaleTextSize(cusV.dynamicSizeTextView, onSelectTextSize, true)
                }
            }
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {
            val cusV = tab.customView
            if (cusV is FixedWidthTextTabView) {
                val onSelectTextSize = config.onSelectTextSize
                val onUnSelectTextSize = config.onUnSelectTextSize
                if (onSelectTextSize != onUnSelectTextSize) {
                    scaleTextSize(cusV.dynamicSizeTextView, onUnSelectTextSize, false)
                }
            }
        }

        override fun onTabReselected(tab: TabLayout.Tab) {
        }
    }
    addOnTabSelectedListener(listener)
    setTag(key, listener)

}

class ScaleTexViewTabView(context: Context) : AppCompatTextView(context) {
    var skipRequestLayout = false
    override fun requestLayout() {
        if (!skipRequestLayout) {
            super.requestLayout()
        }
    }
}

fun TabLayout.createMediator(
    vp: ViewPager2,
    onInit: (tab: TabLayout.Tab, position: Int) -> Unit
): TabLayoutMediator {
    return TabLayoutMediator(this, vp) { tab, pos ->
        onInit(tab, pos)
    }
}

fun <T : View> TabLayout.createMediatorByCustomTabView(
    vp: ViewPager2,
    config: CustomTabViewConfig<T>
): TabLayoutMediator {
    return createMediator(vp){tab,pos->
        val tabView = config.getCustomView(tab.view.context)
        tab.customView = tabView
        config.onTabInit(tabView, pos)
    }
}


fun TabLayout.createTextScaleMediatorByTextView(
    vp: ViewPager2,
    config: TextScaleTabViewConfig
): TabLayoutMediator {
    addScaleAnim(config.scale)
    return createMediatorByCustomTabView(vp, config)
}

fun TabLayout.addScaleTabByTextView(
    textList: List<String>,
    textColor: Int,
    scale: TextScaleConfig = defaultScaleConfig
) {
    removeAllTabs()
    addScaleAnim(scale)
    textList.forEach { text ->
        val tab = newTab().apply {
            val tabView = FixedWidthTextTabView(view.context)
            customView = tabView
            tabView.boundSizeTextView.text = text
            tabView.boundSizeTextView.textSizePx = scale.onSelectTextSize
            if(scale.switchBold){
                //避免设置不必要的字体样式
                tabView.boundSizeTextView.boldText = true
            }
            tabView.dynamicSizeTextView.text = text
            tabView.dynamicSizeTextView.textSizePx = scale.onUnSelectTextSize
            tabView.dynamicSizeTextView.setTextColor(textColor)
        }
        addTab(tab)
    }
}