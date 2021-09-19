package com.grock

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
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
class ViewPager2Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vb = ActViewpager2Binding.inflate(layoutInflater)
        setContentView(vb.root)

        val tabs = arrayOf("A00001","B00001","C00001","D00001","E00001","F00001","G00001")
        val vp = vb.viewPager2
        val tl = vb.tl
        val list:List<Fragment> = arrayListOf(VFragment.newInstance("1"),
            VFragment.newInstance("2"),
            VFragment.newInstance("3"),
            VFragment.newInstance("4"),
            VFragment.newInstance("5"),
            VFragment.newInstance("6"),
            VFragment.newInstance("7"),
        )
        vp.offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
        vp.adapter = FragmentAdapter(this,list)
        val mediator = TabLayoutMediator(tl,vp
        ) { tab, position ->
            val view = TabItemBinding.inflate(LayoutInflater.from(tab.view.context))
            tab.customView = view.root
            view.tv.text = tabs[position]
        }
        mediator.attach()
        vb.tl.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

    }
}


class FragmentAdapter(activity: AppCompatActivity,private val list:List<Fragment>):FragmentStateAdapter(activity){
    override fun getItemCount(): Int = list.size

    override fun createFragment(position: Int): Fragment= list[position]

}

class VFragment : Fragment(R.layout.frag_simple) {
    companion object {
        fun newInstance(str: String): VFragment {
            val f = VFragment()
            val bundle = Bundle()
            bundle.putString("str", str)
            f.arguments = bundle
            return f
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val vb = FragSimpleBinding.bind(view)
        arguments?.let {
            vb.tv.text = it.getString("str")
        }
    }
}