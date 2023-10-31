
`ViewPager2`正式推出已经一年多了，既不如3那样新潮，也不如老前辈`ViewPager`那样有众多开源库拥簇，比如它的灵魂伴侣`TabLayout`明显后援不足，好在`TabLayout`自身够硬！

ViewPager2灵魂伴侣是官方提供的：
```
com.google.android.material.tabs.TabLayout
```
`TabLayout` 利用其良好的设计，使得自定义非常容易。

像匹配`ViewPager`的优秀开源库FlycoTabLayout的效果，使用`TabLayout`都能比较容易的实现：

FlycoTabLayout 演示

![image.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/c563e436c4344814b6b0378967696edf~tplv-k3u1fbpfcp-watermark.image?)

实现上图中的几个常用效果`TabLayout` 仅需在xml重配置即可


![tablayout.gif](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/8e237249806744b98d0acb2f459aed1d~tplv-k3u1fbpfcp-watermark.image?)

不过稍微不同的是，上图中第二第三栏选中后的字体是有放大效果的。

这是利用`TabLayout.Tab`的`customView`属性达到的。下文便是实现的思路与过程记录。


### 正文

 #### 思路拆解：
* 介于此功能耦合点仅仅是`TabLayoutMediator`，选择使用拓展包装`TabLayoutMediator`，轻量且无侵入性，API还便捷
* 自定义`TabLayoutMediator`,设置`customView`，放入自己的`TextView`
* 内部自动添加一个`addOnTabSelectedListener`,在选中后使用动画渐进式的改变字体大小，同理取消选中时还原
    
#### 解决过的坑：
* `TextView`的文本在Size改变时，宽度动态变化，调用`requestLayout()`。Tab栏会因此触发重新测量与重绘，出现短促闪烁。塞两个`TextView`，一个作为最大边界并且设置`INVISIBLE`
* 同样是重测问题，导致`TabLayout`额外多从头绘制一次`Indicator`时,直观表现就是每次切换`Indicator`时，会出现闪现消失。采用自定义了一个`ScaleTexViewTabView`,动态控制是否触发`super.requestLayout`
(因为已经准备了两个View,负责展示效果的View最大范围是明确无法超过既定范围的，所以这个办法不算“黑”)

* #### 核心API：

```Kotlin

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
```
#### 使用：
* 绑定ViewPager2 

```kotlin
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

```
* 单独添加Tab
```java
vb.tl3.addScaleTabByTextView(tabs.toList(),Color.YELLOW,scaleConfig3)
```

本文只是从一种特殊（或者叫奇怪）的角度来定制View的样式，使用kotlin拓展API，实现与使用上保持轻量，不侵入自定义View，不影响XML。


[点击直达完整源码~，拷贝即用](https://github.com/HarkBen/LovelyTabLayoutExt/blob/master/app/src/main/java/com/grock/TabLayoutExt.kt)

# END
*** 
### 引用：
* [H07000223](https://github.com/H07000223)/**[FlycoTabLayout](https://github.com/H07000223/FlycoTabLayout)**
