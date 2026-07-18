package com.tospery.suite.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class SuiteGrowingMultilineTextFieldTest {
    @Test
    fun scrollBarThumbTracksViewportAndScrollPosition() {
        val thumb =
            calculateScrollBarThumb(
                viewportHeightPx = 300f,
                scrollValuePx = 150f,
                scrollMaxValuePx = 300f,
                minimumThumbHeightPx = 32f,
            )

        assertEquals(150f, thumb.heightPx, 0f)
        assertEquals(75f, thumb.offsetPx, 0f)
    }

    @Test
    fun scrollBarThumbHonorsMinimumHeight() {
        val thumb =
            calculateScrollBarThumb(
                viewportHeightPx = 300f,
                scrollValuePx = 9_700f,
                scrollMaxValuePx = 9_700f,
                minimumThumbHeightPx = 32f,
            )

        assertEquals(32f, thumb.heightPx, 0f)
        assertEquals(268f, thumb.offsetPx, 0f)
    }

    @Test
    fun scrollBarThumbIsEmptyWithoutOverflow() {
        val thumb =
            calculateScrollBarThumb(
                viewportHeightPx = 300f,
                scrollValuePx = 0f,
                scrollMaxValuePx = 0f,
                minimumThumbHeightPx = 32f,
            )

        assertEquals(0f, thumb.heightPx, 0f)
        assertEquals(0f, thumb.offsetPx, 0f)
    }
}
