
# 1.指定混淆时采用的算法，后面的参数是一个过滤器
# 这个过滤器是谷歌推荐的算法，一般不改变
-optimizations !code/simplification/artithmetic,!field/*,!class/merging/*

# 2.代码混淆压缩比，在0~7之间，默认为5,一般不需要修改
-optimizationpasses 5

# 3.混淆时不使用大小写混合，混淆后的类名为小写
# windows下的同学还是加入这个选项吧(windows大小写不敏感)
-dontusemixedcaseclassnames

# 4.指定不去忽略非公共的库的类 默认跳过，有些情况下编写的代码与类库中的类在同一个包下，并且持有包中内容的引用，此时就需要加入此条声明
-dontskipnonpubliclibraryclasses

# 5.指定不去忽略非公共的库的类的成员
-dontskipnonpubliclibraryclassmembers

# 6.不做预检验，preverify是proguard的四个步骤之一  Android不需要preverify，去掉这一步可以加快混淆速度
-dontpreverify
-dontshrink