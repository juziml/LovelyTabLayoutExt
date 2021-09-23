package com.grock

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.grock.tc.databinding.ActViewpager2Binding

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

        val scaleConfig1 = TextScaleConfig(
            onSelectTextSize = 18.dp,
            onUnSelectTextSize = 12.dp,
            switchBold = false
        )

        vb.tlNormal.createMediator(vb.viewPager2) { tab, position ->
            tab.text = tabs[position]
        }.attach()

        val mediator = vb.tl.createTextScaleMediatorByTextView(vb.viewPager2,
            object : TextScaleTabViewConfig(scaleConfig1) {
                override fun getText(position: Int): String {
                    return tabs[position]
                }

                override fun onVisibleTextViewInit(tv: TextView) {
                    tv.setTextColor(Color.WHITE)
                }
            })
        mediator.attach()
        val scaleConfig2 = TextScaleConfig(
            onSelectTextSize = 18.dp,
            onUnSelectTextSize = 12.dp,
            switchBold = true
        )

        val mediator2 = vb.tl2.createTextScaleMediatorByTextView(vb.viewPager2,
            object : TextScaleTabViewConfig(scaleConfig2) {
                override fun getText(position: Int): String {
                    return tabs[position]
                }

                override fun onVisibleTextViewInit(tv: TextView) {
                    tv.setTextColor(Color.WHITE)
                }
            })
        mediator2.attach()

        val scaleConfig3 = TextScaleConfig(
            onSelectTextSize = 18.dp,
            onUnSelectTextSize = 12.dp,
            switchBold = true
        )
        vb.tl3.addScaleTabByTextView(tabs.toList(),Color.YELLOW,scaleConfig3)
    }
}


