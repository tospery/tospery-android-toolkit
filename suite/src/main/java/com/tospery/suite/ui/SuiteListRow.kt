package com.tospery.suite.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

private val SuiteListRowContentPadding =
    PaddingValues(horizontal = 20.dp, vertical = 12.dp)

/**
 * 面向设置、资料等列表场景的无业务通用行。
 *
 * [trailingText] 适合“标题 + 可省略的右侧值 + 箭头”等常见资料行；
 * [trailingContent] 则用于图标、开关等自定义尾部内容。
 */
@Composable
fun SuiteListRow(
    title: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    leadingContent: (@Composable () -> Unit)? = null,
    titleStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    titleTextAlign: TextAlign = TextAlign.Start,
    trailingText: String? = null,
    trailingTextStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    trailingTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    trailingContent: (@Composable () -> Unit)? = null,
    contentPadding: PaddingValues = SuiteListRowContentPadding,
    showDivider: Boolean = true,
) {
    val layoutDirection = LocalLayoutDirection.current
    val rowModifier =
        if (onClick == null) {
            modifier
        } else {
            modifier.clickable(onClick = onClick)
        }
    val titleWeight = if (trailingText == null) 1f else 0.4f

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier =
                rowModifier
                    .fillMaxWidth()
                    .padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            leadingContent?.let { content ->
                content()
                Spacer(modifier = Modifier.width(14.dp))
            }
            Text(
                text = title,
                modifier = Modifier.weight(titleWeight),
                color = titleColor,
                style = titleStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = titleTextAlign,
            )
            trailingText?.let { value ->
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = value,
                    modifier = Modifier.weight(1f),
                    color = trailingTextColor,
                    style = trailingTextStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.End,
                )
            }
            trailingContent?.let { content ->
                Spacer(modifier = Modifier.width(8.dp))
                content()
            }
        }
        if (showDivider) {
            SuiteHorizontalDivider(
                modifier =
                    Modifier.padding(
                        start = contentPadding.calculateLeftPadding(layoutDirection),
                    ),
            )
        }
    }
}
