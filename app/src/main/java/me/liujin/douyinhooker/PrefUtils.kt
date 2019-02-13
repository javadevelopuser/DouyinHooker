package me.liujin.douyinhooker

import de.robv.android.xposed.XSharedPreferences


object PreferenceUtils {

    val REGION = "region"
    val ISDISABLEDWATERMARK = "isDisabledWatermark"
    val AUTOSAVE = "autosave"

    private var intance: XSharedPreferences? = null

    fun getIntance(): XSharedPreferences? {
        if (intance == null) {
            intance = XSharedPreferences("me.liujin.douyinhooker")
            intance!!.makeWorldReadable()
        } else {
            intance!!.reload()
        }
        return intance
    }

    fun region(): String? {
        return getIntance()!!.getString(REGION, "")
    }

    fun isDisabledWatermark(): Boolean {
        return getIntance()!!.getBoolean(ISDISABLEDWATERMARK, true)
    }

    fun autosave(): Boolean {
        return getIntance()!!.getBoolean(AUTOSAVE, true)
    }
}