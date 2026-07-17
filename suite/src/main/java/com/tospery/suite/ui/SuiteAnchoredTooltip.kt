package com.tospery.suite.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min

private val SuiteTooltipHorizontalMargin = 8.dp
private val SuiteTooltipContentPadding = 12.dp
private val SuiteTooltipCornerRadius = 14.dp
private val SuiteTooltipCaretWidth = 16.dp
private val SuiteTooltipCaretHeight = 8.dp

/**
 * 在本地布局坐标中的锚点附近展示的通用提示气泡。
 *
 * [anchorBounds] 必须相对调用方提供的布局区域。组件会根据可用空间自动选择上方或下方，
 * 并限制气泡不超出左右边界。点击气泡外区域时调用 [onDismissRequest]；调用方决定显示状态
 * 和文案，组件不包含业务语义。[caretTipOffset] 可使尖端在锚点内水平偏移，用于贴边目标
 * 的短斜箭头效果；正值向右，负值向左。[caretCornerInset] 可缩短贴边箭头的斜边；未传入时
 * 仍完整避开气泡圆角。
 */
@Composable
fun SuiteAnchoredTooltip(
    anchorBounds: IntRect?,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    caretTipOffset: Dp = 0.dp,
    caretCornerInset: Dp? = null,
    content: @Composable () -> Unit,
) {
    val anchor = anchorBounds ?: return
    val tooltipColor = MaterialTheme.colorScheme.inverseSurface
    val tooltipContentColor = MaterialTheme.colorScheme.inverseOnSurface

    BoxWithConstraints(modifier = modifier) {
        var tooltipSize by remember { mutableStateOf(IntSize.Zero) }
        val density = androidx.compose.ui.platform.LocalDensity.current
        val containerWidth = with(density) { maxWidth.roundToPx() }
        val caretWidth = with(density) { SuiteTooltipCaretWidth.roundToPx() }
        val caretHeight = with(density) { SuiteTooltipCaretHeight.roundToPx() }
        val horizontalMargin = with(density) { SuiteTooltipHorizontalMargin.roundToPx() }
        val caretCornerInsetPx =
            with(density) { (caretCornerInset ?: SuiteTooltipCornerRadius).roundToPx() }
        val canShowAbove = anchor.top >= tooltipSize.height + caretHeight
        val rawTooltipLeft = anchor.center.x - (tooltipSize.width / 2)
        val tooltipLeft =
            rawTooltipLeft.coerceIn(
                minimumValue = horizontalMargin,
                maximumValue = max(horizontalMargin, containerWidth - horizontalMargin - tooltipSize.width),
            )
        val tooltipTop =
            if (canShowAbove) {
                anchor.top - tooltipSize.height - caretHeight
            } else {
                anchor.bottom + caretHeight
            }
        val caretTipCenterX =
            anchor.center.x + with(density) { caretTipOffset.roundToPx() }
        val rawCaretLeft = caretTipCenterX - (caretWidth / 2)
        // 气泡贴边时，底边默认避开圆角；边缘调用方可缩小避让距离以保持短斜箭头。
        // 尖端默认对准锚点中心，也可在锚点内偏移。
        // 否则最后一列等靠近边界的目标会与前一列共用被截断的箭头位置。
        val caretBaseLeft =
            rawCaretLeft.coerceIn(
                minimumValue = tooltipLeft + caretCornerInsetPx,
                maximumValue =
                    tooltipLeft +
                        max(
                            caretCornerInsetPx,
                            tooltipSize.width - caretCornerInsetPx - caretWidth,
                        ),
            )
        val caretCanvasLeft = min(rawCaretLeft, caretBaseLeft)
        val caretCanvasWidth =
            max(rawCaretLeft + caretWidth, caretBaseLeft + caretWidth) - caretCanvasLeft
        val caretBaseStart = caretBaseLeft - caretCanvasLeft
        val caretBaseEnd = caretBaseStart + caretWidth
        val caretTipX = caretTipCenterX - caretCanvasLeft
        val caretTop =
            if (canShowAbove) {
                tooltipTop + tooltipSize.height
            } else {
                tooltipTop - caretHeight
            }

        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .pointerInput(tooltipLeft, tooltipTop, tooltipSize, onDismissRequest) {
                            detectTapGestures { tapOffset ->
                                val tappedTooltip =
                                    tapOffset.x in
                                        tooltipLeft.toFloat()..
                                        (tooltipLeft + tooltipSize.width).toFloat() &&
                                        tapOffset.y in
                                        tooltipTop.toFloat()..
                                        (tooltipTop + tooltipSize.height).toFloat()
                                if (!tappedTooltip) {
                                    onDismissRequest()
                                }
                            }
                        },
            )
            Surface(
                color = tooltipColor,
                contentColor = tooltipContentColor,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(SuiteTooltipCornerRadius),
                modifier =
                    Modifier
                        .offset { IntOffset(tooltipLeft, tooltipTop) }
                        .onSizeChanged { tooltipSize = it }
                        .shadow(6.dp, androidx.compose.foundation.shape.RoundedCornerShape(SuiteTooltipCornerRadius)),
            ) {
                Box(modifier = Modifier.padding(SuiteTooltipContentPadding)) {
                    content()
                }
            }
            Canvas(
                modifier =
                    Modifier
                        .offset { IntOffset(caretCanvasLeft, caretTop) }
                        .size(
                            width = with(density) { caretCanvasWidth.toDp() },
                            height = SuiteTooltipCaretHeight,
                        ),
            ) {
                val path = Path()
                if (canShowAbove) {
                    path.moveTo(caretBaseStart.toFloat(), 0f)
                    path.lineTo(caretBaseEnd.toFloat(), 0f)
                    path.lineTo(caretTipX.toFloat(), size.height)
                } else {
                    path.moveTo(caretBaseStart.toFloat(), size.height)
                    path.lineTo(caretBaseEnd.toFloat(), size.height)
                    path.lineTo(caretTipX.toFloat(), 0f)
                }
                path.close()
                drawPath(path = path, color = tooltipColor, style = Fill)
            }
        }
    }
}
