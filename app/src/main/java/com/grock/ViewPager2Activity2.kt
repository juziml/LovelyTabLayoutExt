package com.grock

import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.grock.tc.R
import com.grock.tc.databinding.ActViewpager2Binding
import com.grock.tc.databinding.FragSimpleBinding
import com.grock.tc.databinding.TabItemBinding

/**
 *
 *create by zhusw on 5/8/21 10:33
 */
class ViewPager2Activity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vb = ActViewpager2Binding.inflate(layoutInflater)
        setContentView(vb.root)

        val tabs = arrayOf("A1", "B22", "C333", "D4444", "E55555", "F666666", "F7777777")
        val vp = vb.viewPager2

        val list: List<Fragment> = arrayListOf(
            VFragment.newInstance("1"),
            VFragment.newInstance("2"),
            VFragment.newInstance("3"),
            VFragment.newInstance("4"),
            VFragment.newInstance("5"),
            VFragment.newInstance("6"),
            VFragment.newInstance("7"),
        )
        vp.offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
        vp.adapter = FragmentAdapter(this, list)
        val scaleConfig = TextScaleConfig(
            onSelectTextSize = 18.dp,
            onUnSelectTextSize = 12.dp,
        )
       vb.tlNormal.createMediator(vb.viewPager2){tab, position ->
            tab.text = tabs[position]
        }.attach()

        val mediator = vb.tl.createTextScaleMediatorByTextView(vb.viewPager2,
            object : TextScaleTabViewConfig(scaleConfig) {
                override fun onBoundTextViewInit(boundSizeTextView: TextView, position: Int) {
                    boundSizeTextView.textSizePx = scaleConfig.onSelectTextSize
                    boundSizeTextView.text = tabs[position]
                }
                override fun onVisibleTextViewInit(dynamicSizeTextView: TextView, position: Int) {
                    dynamicSizeTextView.setTextColor(Color.WHITE)
                    dynamicSizeTextView.text = tabs[position]
                }
            })
        mediator.attach()
        val mediator2 = vb.tl2.createTextScaleMediatorByTextView(vb.viewPager2,
            object : TextScaleTabViewConfig(scaleConfig) {

                override fun onBoundTextViewInit(boundSizeTextView: TextView, position: Int) {
                    boundSizeTextView.textSizePx = scaleConfig.onSelectTextSize
                    boundSizeTextView.text = tabs[position]
                }

                override fun onVisibleTextViewInit(dynamicSizeTextView: TextView, position: Int) {
                    dynamicSizeTextView.setTextColor(Color.WHITE)
                    dynamicSizeTextView.text = tabs[position]
                }
            })
        mediator2.attach()
    }
}

val Int.dp get() = (this * Resources.getSystem().displayMetrics.density ).toInt()

var TextView.textSizePx: Int
    set(value) {
        setTextSize(TypedValue.COMPLEX_UNIT_PX, value.toFloat())
    }
    get() = textSize.toInt()

