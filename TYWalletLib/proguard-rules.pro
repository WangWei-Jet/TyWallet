# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#-dontskipnonpubliclibraryclasses # 不忽略非公共的库类
-optimizationpasses 5            # 指定代码的压缩级别
-dontusemixedcaseclassnames      # 是否使用大小写混合
-dontpreverify                   # 混淆时是否做预校验
-verbose                         # 混淆时是否记录日志
-keepattributes *Annotation*     # 保持注解
-ignorewarning                   # 忽略警告
-dontoptimize                    # 优化不优化输入的类文件
-keeppackagenames com.whty.blockchain.tyblockchainlib.api.core,com.whty.blockchain.tyblockchainlib.api.entity,com.whty.blockchain.tyblockchainlib.api.util

-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*  # 混淆时所采用的算法

#保持哪些类不被混淆
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

#生成日志数据，gradle build时在本项目根目录输出
-dump class_files.txt            #apk包内所有class的内部结构
-printseeds seeds.txt            #未混淆的类和成员
-printusage unused.txt           #打印未被使用的代码
-printmapping mapping.txt        #混淆前后的映射

-keep public class * extends android.support.** #如果有引用v4或者v7包，需添加
#-libraryjars libs/ADIPlugins_V1.00.00.R6298.jar       #混淆第三方jar包，其中xxx为jar包名
#-keep class com.xxx.**{*;}       #不混淆某个包内的所有文件
#-dontwarn com.xxx**              #忽略某个包的警告
-keepattributes Signature        #不混淆泛型
-keepnames class * implements java.io.Serializable #不混淆Serializable

#不混淆资源类
#-keep public class **.R$* {
#　　public static final int *;
#}
# 保持 native 方法不被混淆
-keepclasseswithmembernames class * {
   native <methods>;
}
# 保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
# 保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
# 保持自定义控件类不被混淆
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}
# 保持枚举 enum 类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
# 保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

#gson
-keep class com.google.**{*;}

#web3j
-keep class org.web3j.**{*;}

#rxjava
-keep class io.reactivex.**{*;}



#adi

-keep class com.whty.bluetooth*.**{*;}

-keep class com.whty.comm.**{*;}

-keep class com.whty.device.**{*;}

-keep class com.whty.adi*.**{*;}

#blockchain

-keep class com.whty.blockchain.tyblockchainlib.api.core.TyWallet{*;}

-keep class com.whty.blockchain.tyblockchainlib.api.core.TyWalletDriver{*;}

-keep class com.whty.blockchain.tyblockchainlib.api.core.WalletObserver{*;}

-keep class com.whty.blockchain.tyblockchainlib.api.entity.**{*;}

-keep class com.whty.blockchain.tyblockchainlib.api.pojo.**{*;}

-keep class com.whty.blockchain.tyblockchainlib.api.TyWalletFactory{*;}

-keep class com.whty.blockchain.tyblockchainlib.api.util.UpgradeListener{*;}




