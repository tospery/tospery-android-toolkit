package com.tospery.core.resources

import android.content.Context
import androidx.annotation.StringRes

/**
 * 基于 Android Context 的字符串资源读取实现。
 */
class AndroidStringResourceProvider(
    context: Context,
) : StringResourceProvider {
    private val appContext = context.applicationContext

    override fun getString(@StringRes resId: Int): String {
        return appContext.getString(resId)
    }
}
