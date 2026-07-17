package com.tospery.core.resources

import androidx.annotation.StringRes

/**
 * 字符串资源读取抽象。
 *
 * 用于让 ViewModel 或非 Compose 层按资源 id 获取本地化文案，
 * 同时避免业务代码直接依赖具体 Context。
 */
fun interface StringResourceProvider {
    fun getString(@StringRes resId: Int): String
}
