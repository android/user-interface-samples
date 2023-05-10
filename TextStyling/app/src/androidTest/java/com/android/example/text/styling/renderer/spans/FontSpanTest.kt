package com.android.example.text.styling.renderer.spans

import junit.framework.Assert.assertEquals

import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

import android.graphics.Typeface
import android.text.TextPaint

import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor

/**
 * Tests for the [FontSpan] class.
 */
class FontSpanTest {

    private val paint = mock(TextPaint::class.java)
    private val span = FontSpan(Typeface.DEFAULT)
    private val defaultTypeface = Typeface.create("serif", Typeface.BOLD)

    @Before fun setUp() {
        `when`(paint.typeface).thenReturn(defaultTypeface)
    }

    @Test fun updateMeasureState() {
        // When the update measure state is called
        span.updateMeasureState(paint)

        // Check that the typeface set has the correct typeface and style
        val captor = ArgumentCaptor.forClass(Typeface::class.java)
        verify(paint).typeface = captor.capture()
        assertEquals(Typeface.BOLD, captor.value.style)
    }

    @Test fun updateDrawState() {
        // When the update draw state is called
        span.updateDrawState(paint)

        // Check that the typeface set has the correct typeface and style
        val captor = ArgumentCaptor.forClass(Typeface::class.java)
        verify(paint).typeface = captor.capture()
        assertEquals(Typeface.BOLD, captor.value.style)
    }

}