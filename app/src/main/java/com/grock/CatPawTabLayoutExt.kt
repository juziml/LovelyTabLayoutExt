package com.grock

import android.animation.ValueAnimator
import android.content.Context
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

interface CustomTabViewConfig<T : View> {
    fun getCustomView(context: Context): T
    fun onTabInit(tabView: T, position: Int)
}
fun TabLayout.createMediator(
    vp: ViewPager2,
    onInit:(tab:TabLayout.Tab, position:Int)->Unit
): TabLayoutMediator {
    return TabLayoutMediator(this, vp) { tab, pos ->
        onInit(tab,pos)
    }
}

fun <T : View> TabLayout.createMediatorByCustomTabView(
    vp: ViewPager2,
    config: CustomTabViewConfig<T>
): TabLayoutMediator {
    return TabLayoutMediator(this, vp) { tab, pos ->
        val tabView = config.getCustomView(tab.view.context)
        tab.customView = tabView
        config.onTabInit(tabView, pos)
    }
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
        addView(dynamicSizeTextView,
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }
}

abstract class FixedWidthTextTabViewConfig : CustomTabViewConfig<FixedWidthTextTabView> {
    abstract fun onBoundTextViewInit(boundSizeTextView: TextView, position: Int)
    abstract fun onVisibleTextViewInit(dynamicSizeTextView: TextView, position: Int)
    final override fun onTabInit(tabView: FixedWidthTextTabView, position: Int) {
        onBoundTextViewInit(tabView.boundSizeTextView, position)
        onVisibleTextViewInit(tabView.dynamicSizeTextView, position)
    }
    final override fun getCustomView(context: Context): FixedWidthTextTabView {
        return FixedWidthTextTabView(context)
    }
}

data class TextScaleConfig(
    val onSelectTextSize: Int,
    val onUnSelectTextSize: Int,
)

abstract class TextScaleTabViewConfig(val scale: TextScaleConfig) : FixedWidthTextTabViewConfig()

fun TabLayout.createTextScaleMediatorByTextView(
    vp: ViewPager2,
    config: TextScaleTabViewConfig
): TabLayoutMediator {
    val mediator = createMediatorByCustomTabView(vp, config)
    val animCacheMap = HashMap<TextView, ValueAnimator>()
    fun cancelLastAnim(tv: TextView) {
        animCacheMap[tv]?.cancel()
    }
    doOnDetach {
        animCacheMap.forEach {
            it.value.cancel()
        }
    }
    val duration = 50L
    addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        private fun scaleTextSize(stv: ScaleTexViewTabView,size:Int){
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
                        stv.skipRequestLayout = false
                        animCacheMap.remove(stv)
                    }
                    animCacheMap[stv] = animator
                    animator.start()
        }
        override fun onTabSelected(tab: TabLayout.Tab) {
            val cusV = tab.customView
            if (cusV is FixedWidthTextTabView) {
                val onSelectTextSize = config.scale.onSelectTextSize
                val onUnSelectTextSize = config.scale.onUnSelectTextSize
                if (onSelectTextSize != onUnSelectTextSize) {
                    scaleTextSize(cusV.dynamicSizeTextView,onSelectTextSize)
                }
            }
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {
            val cusV = tab.customView
            if (cusV is FixedWidthTextTabView) {
                val onSelectTextSize = config.scale.onSelectTextSize
                val onUnSelectTextSize = config.scale.onUnSelectTextSize
                if (onSelectTextSize != onUnSelectTextSize) {
                    scaleTextSize(cusV.dynamicSizeTextView,onUnSelectTextSize)
                }
            }
        }
        override fun onTabReselected(tab: TabLayout.Tab) {
        }
    })
    return mediator
}
class ScaleTexViewTabView(context: Context):AppCompatTextView(context){
    var skipRequestLayout = false
    override fun requestLayout() {
        if(!skipRequestLayout){
            super.requestLayout()
        }
    }
}







