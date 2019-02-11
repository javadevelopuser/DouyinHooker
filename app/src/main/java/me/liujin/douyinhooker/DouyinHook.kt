package me.liujin.douyinhooker

import android.app.Application
import android.content.Context
import android.util.Log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XposedBridge


class DouyinHook : IXposedHookLoadPackage {
    companion object {
        val TAG = DouyinHook::class.java.simpleName
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        // 修改地区
        XposedBridge.hookAllMethods(
            android.telephony.TelephonyManager::class.java,
            "getSimCountryIso",
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val country = "KR"
                    param.result = country
                    // Log.d(TAG, "Change country to: $country")
                }
            }
        )

        if (lpparam.packageName.equals("com.ss.android.ugc.trill")) {
            Log.d(TAG, "Douyin found!")

            XposedHelpers.findAndHookMethod(Application::class.java, "attach", Context::class.java, object : XC_MethodHook() {
                override fun afterHookedMethod(parentParam: MethodHookParam) {
                    Log.d(TAG, "After attach application.")

                    val classLoader = (parentParam.args[0] as Context).classLoader
                    val aweme: Class<*>? = classLoader.loadClass("com.ss.android.ugc.aweme.feed.model.Aweme")
                    val clazz: Class<*>? = classLoader.loadClass("com.ss.android.ugc.trill.share.a.c")
                    val likeListenerClass: Class<*>? = classLoader.loadClass("com.ss.android.ugc.aweme.feed.ui.at\$1")
                    val atClass: Class<*>? = classLoader.loadClass("com.ss.android.ugc.aweme.feed.ui.at")
                    var shareObj: Any? = null

                    if (aweme != null && clazz != null && likeListenerClass != null) {
                        // 去水印，下载更快，文件更小，清晰度不变
                        XposedHelpers.findAndHookMethod(clazz, "share", aweme, String::class.java, Boolean::class.java, object : XC_MethodHook() {
                            override fun beforeHookedMethod(param: MethodHookParam) {
                                Log.d(TAG, "Hook before share.")
                                Log.d(TAG, "Param[0]: ${param.args[0]}\nParam[1]: ${param.args[1]}\nParam[2]: ${param.args[2]}")
                                param.args[2] = true
                                shareObj = param.thisObject as Any
                            }
                        })

                        // 点赞自动下载视频
                        XposedHelpers.findAndHookMethod(atClass, "handleDiggClick", aweme, object : XC_MethodHook() {
                            override fun beforeHookedMethod(diggParam: MethodHookParam) {
                                Log.d(TAG, "Hook before atClass.handleDiggClick.")
                                Log.d(TAG, "Param[0]: ${diggParam.args[0]}")

                                val shareMethod = clazz.getMethod("share", aweme, Boolean::class.java)
                                shareMethod.invoke(shareObj, diggParam.args[0], false)
                            }
                        })
                    }
                }
            })
        }
    }
}