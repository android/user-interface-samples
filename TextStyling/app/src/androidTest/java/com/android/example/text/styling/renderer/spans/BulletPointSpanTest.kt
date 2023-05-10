package com.android.example.text.styling.renderer.spans

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.text.Layout
import android.text.SpannableString
import android.text.Spanned
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Matchers
import org.mockito.Matchers.eq
import org.mockito.Mockito.*

private const val GAP_WIDTH = 5

class BulletPointSpanTest {

    private val canvas = mock(Canvas::class.java)
    private val paint = mock(Paint::class.java)
    private val text = SpannableString("text")

    @Test
    fun getLeadingMargin() {
        // Given a span with a certain gap width
        val span = BulletPointSpan(GAP_WIDTH, 0)

        // Check that the margin is set correctly
        val expectedMargin = (2 * BulletPointSpan.DEFAULT_BULLET_RADIUS + 2 * GAP_WIDTH).toInt()
        assertEquals(expectedMargin.toLong(), span.getLeadingMargin(true).toLong())
    }

    @Test
    fun drawLeadingMarginWithoutText() {
        // Given a span
        val span = BulletPointSpan(GAP_WIDTH, 0)

        // When the leading margin is drawn but no text is set
        span.drawLeadingMargin(canvas, paint, 0, 0, 0, 0, 0, text, 0, 0, true,
                mock(Layout::class.java))

        // Check that no drawing methods are called
        verifyZeroInteractions(canvas)
        verifyZeroInteractions(paint)
    }

    @Test
    fun drawLeadingMarginHardwareAccelerated() {
        val x = 10
        val dir = 15
        val top = 5
        val bottom = 7
        val color = Color.RED
        // Given a span that is set on a text
        val span = BulletPointSpan(GAP_WIDTH, color)
        text.setSpan(span, 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        `when`(canvas.isHardwareAccelerated).thenReturn(true)

        // When the leading margin is drawn
        span.drawLeadingMargin(canvas, paint, x, dir, top, 0, bottom, text, 0, 0, true,
                mock(Layout::class.java))

        // Check that the correct canvas and paint methods are called, in the correct order
        val inOrder = inOrder(canvas, paint)
        inOrder.verify(paint).color = color
        inOrder.verify(paint).style = eq<Paint.Style>(Paint.Style.FILL)
        inOrder.verify(canvas).translate(
                eq(GAP_WIDTH.toFloat() + x.toFloat() + dir * BulletPointSpan.DEFAULT_BULLET_RADIUS),
                eq((top + bottom) / 2f))
        inOrder.verify(canvas).drawPath(Matchers.any(Path::class.java), eq(paint))
    }

    @Test
    fun drawLeadingMarginNotHardwareAccelerated() {
        val x = 10
        val dir = 15
        val top = 5
        val bottom = 7
        val color = Color.RED
        // Given a span that is set on a text
        val span = BulletPointSpan(GAP_WIDTH, color)
        text.setSpan(span, 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        `when`(canvas.isHardwareAccelerated).thenReturn(false)

        // When the leading margin is drawn
        span.drawLeadingMargin(canvas, paint, x, dir, top, 0, bottom, text, 0, 0, true,
                mock(Layout::class.java))

        // Check that the correct canvas and paint methods are called, in the correct order
        val inOrder = inOrder(canvas, paint)
        inOrder.verify(paint).color = color
        inOrder.verify(paint).style = eq<Paint.Style>(Paint.Style.FILL)
        inOrder.verify(canvas).drawCircle(eq(GAP_WIDTH.toFloat() + x.toFloat()
                + dir * BulletPointSpan.DEFAULT_BULLET_RADIUS),
                eq((top + bottom) / 2f), eq(BulletPointSpan.DEFAULT_BULLET_RADIUS), eq(paint))
        verify(canvas, never()).save()
        verify(canvas, never()).translate(
                eq(GAP_WIDTH.toFloat() + x.toFloat() + dir * BulletPointSpan.DEFAULT_BULLET_RADIUS),
                eq((top + bottom) / 2f))
    }
}