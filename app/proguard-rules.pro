# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\4Software-IDE\android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}


################### region for xUtils
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,*Annotation*,Synthetic,EnclosingMethod

-keep public class org.xutils.** {
    public protected *;
}
-keep public interface org.xutils.** {
    public protected *;
}
-keepclassmembers class * extends org.xutils.** {
    public protected *;
}
-keepclassmembers class * extends org.xutils.http.RequestParams {*;}
-keepclassmembers class * {
   void *(android.view.View);
   *** *Click(...);
   *** *Event(...);
}
#################### end region

################### bmob
-ignorewarnings
# 这里根据具体的SDK版本修改
#-libraryjars libs/BmobSDK_V3.4.5_1111.jar

-keepattributes Signature
-keep class cn.bmob.v3.** {*;}

# 保证继承自BmobObject、BmobUser类的JavaBean不被混淆
-keep class com.ccqiuqiu.fmoney.Model.*{*;}
# 如果你使用了okhttp、okio的包，请添加以下混淆代码
-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp.** { *;}
-keep interface com.squareup.okhttp.** { *; }
-dontwarn okio.**
-keep class c.b.* {*;}
-keep class c.b.* {*;}

################### dmytrodanylyk/circular-progress-button
-keep class com.dd.* {*;}
