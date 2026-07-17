package com.tospery.suite.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * 确认操作的语义样式。
 *
 * [DESTRUCTIVE] 用于清除、删除等不可逆或需要额外提醒的操作；具体业务含义仍由调用方定义。
 */
enum class ConfirmActionStyle {
    PRIMARY,
    DESTRUCTIVE,
}

/**
 * 跨业务通用的二次确认弹窗。
 *
 * 文案与确认操作的语义由业务调用方提供，组件只负责统一的 Android 平台交互与视觉层级。
 * 自定义布局确保标题、说明和操作区的对齐方式不会随平台默认 [androidx.compose.material3.AlertDialog] 改变。
 */
@Composable
fun ConfirmActionDialog(
    title: String,
    message: String?,
    confirmText: String,
    dismissText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmActionStyle: ConfirmActionStyle = ConfirmActionStyle.PRIMARY,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface,
            tonalElevation = 6.dp,
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                // 操作按钮按对话框宽度计算，避免文案长度改变视觉重心。
                val actionWidth = maxWidth / 3

                Column(
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = title,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineSmall,
                    )

                    message?.let {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = it,
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.width(actionWidth),
                            shape = RoundedCornerShape(14.dp),
                        ) {
                            Text(
                                text = dismissText,
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                        Button(
                            onClick = onConfirm,
                            modifier = Modifier.width(actionWidth),
                            shape = RoundedCornerShape(14.dp),
                            colors =
                                when (confirmActionStyle) {
                                    ConfirmActionStyle.PRIMARY -> ButtonDefaults.buttonColors()
                                    ConfirmActionStyle.DESTRUCTIVE ->
                                        ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.error,
                                            contentColor = MaterialTheme.colorScheme.onError,
                                        )
                                },
                        ) {
                            Text(
                                text = confirmText,
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                    }
                }
            }
        }
    }
}
