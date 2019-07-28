package com.android.example.text.styling.renderer.spans

import org.junit.Assert.assertEquals
import org.mockito.Mockito.mock

import android.graphics.Color
import android.graphics.Typeface
import android.text.TextPaint

import org.junit.Test

/**
 * Tests for the [CodeBlockSpan] class
 */
class CodeBlockSpanTest {

    private val paint = mock(TextPaint::class.java)
    private val span = CodeBlockSpan(Typeface.DEFAULT, Color.RED)

    @Test fun updateDrawState() {
        // When the update draw state is called
        span.updateDrawState(paint)

        // Check that the correct color is set
        assertEquals(Color.RED, paint.bgColor)
    }
}