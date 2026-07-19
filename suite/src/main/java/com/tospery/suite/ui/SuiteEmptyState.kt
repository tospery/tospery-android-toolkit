package com.tospery.suite.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * 通用空状态展示组件。
 *
 * 组件不判断数据为空的原因，也不依赖 AppError。调用方负责把业务状态、
 * 错误或搜索结果映射为展示文案，并决定组件占用的布局空间。
 */
@Composable
fun SuiteEmptyState(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    icon: (@Composable () -> Unit)? = null,
    action: (@Composable () -> Unit)? = null,
) {
    SuiteStateLayout(
        title = title,
        modifier = modifier,
        description = description,
        icon = icon,
        action = action,
    )
}
