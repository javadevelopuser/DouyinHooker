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
                override fun afterHookedMethod(param: MethodHookParam) {
                    Log.d(TAG, "After attach application.")

                    val classLoader = (param.args[0] as Context).classLoader
                    val aweme: Class<*>? = classLoader.loadClass("com.ss.android.ugc.aweme.feed.model.Aweme")
                    val clazz: Class<*>? = classLoader.loadClass("com.ss.android.ugc.trill.share.a.c")

                    if (aweme != null && clazz != null) {
                        // 去水印，下载更快，文件更小，清晰度不变
                        XposedHelpers.findAndHookMethod(clazz, "share", aweme, String::class.java, Boolean::class.java, object : XC_MethodHook() {
                            override fun beforeHookedMethod(param: MethodHookParam) {
                                super.beforeHookedMethod(param);

                                Log.d(TAG, "Hook before share.")
                                Log.d(TAG, "Param[0]: ${param.args[0]}\nParam[1]: ${param.args[1]}\nParam[2]: ${param.args[2]}")

                                param.args[2] = true

//                                val videoMethod = param.args[0]::class.java.getMethod("getVideo")
//                                val videoObj = videoMethod.invoke(param.args[0])
//                                Log.d(TAG, "VideoObj: $videoObj")
//
//                                val playAddrMethod = videoObj::class.java.getMethod("getPlayAddr")
//                                val playAddrObj = playAddrMethod.invoke(videoObj)
//                                val playUrlListMethod = playAddrObj::class.java.getMethod("getUrlList")
//                                val playUrlList : MutableList<String> = playUrlListMethod.invoke(playAddrObj) as MutableList<String>
//                                Log.d(TAG, "playUrlList: $playUrlList")
//
//                                val downAddrMethod = videoObj::class.java.getMethod("getDownloadAddr")
//                                val downAddrObj = downAddrMethod.invoke(videoObj)
//                                val downUrlListMethod = downAddrObj::class.java.getMethod("getUrlList")
//                                val downUrlList : MutableList<String> = downUrlListMethod.invoke(downAddrObj) as MutableList<String>
//                                Log.d(TAG, "downUrlList: $downUrlList")
//
//                                downUrlList.clear()
//                                downUrlList.addAll(playUrlList)
                            }
                        })
                    }
                }
            })
        }
    }
}