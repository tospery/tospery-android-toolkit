package com.tospery.suite.ui

import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity

/**
 * 使用一个物理像素绘制的浅色水平分割线。
 */
@Composable
fun SuiteHorizontalDivider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f),
) {
    val thickness = with(LocalDensity.current) { 1.toDp() }
    HorizontalDivider(
        modifier = modifier,
        thickness = thickness,
        color = color,
    )
}