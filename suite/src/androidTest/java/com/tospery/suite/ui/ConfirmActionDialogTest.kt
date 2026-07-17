package com.tospery.suite.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ConfirmActionDialogTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun destructiveActionDisplaysProvidedContentAndInvokesCallbacks() {
        var confirmedCount = 0
        var dismissedCount = 0

        composeTestRule.setContent {
            MaterialTheme {
                ConfirmActionDialog(
                    title = "确认退出登录？",
                    message = "本地登录状态将被清除。",
                    confirmText = "退出登录",
                    dismissText = "取消",
                    onConfirm = { confirmedCount++ },
                    onDismiss = { dismissedCount++ },
                    confirmActionStyle = ConfirmActionStyle.DESTRUCTIVE,
                )
            }
        }

        composeTestRule.onNodeWithText("确认退出登录？").assertIsDisplayed()
        composeTestRule.onNodeWithText("本地登录状态将被清除。").assertIsDisplayed()
        composeTestRule.onNodeWithText("取消").performClick()
        composeTestRule.onNodeWithText("退出登录").performClick()

        assertEquals(1, dismissedCount)
        assertEquals(1, confirmedCount)
    }
}
