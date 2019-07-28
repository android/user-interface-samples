package com.android.example.text.styling.renderer.spans;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Matchers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link BulletPointSpan} class
 */
public class BulletPointSpanTest {

    private static final int GAP_WIDTH = 5;
    private Canvas canvas = mock(Canvas.class);
    private Paint paint = mock(Paint.class);
    private SpannableString text = new SpannableString("text");

    @Test
    public void getLeadingMargin() {
        // Given a span with a certain gap width
        BulletPointSpan span = new BulletPointSpan(GAP_WIDTH, 0);

        // Check that the margin is set correctly
        int expectedMargin = (int) (2 * BulletPointSpan.BULLET_RADIUS + 2 * GAP_WIDTH);
        assertEquals(expectedMargin, span.getLeadingMargin(true));
    }

    @Test
    public void drawLeadingMarginWithoutText() {
        // Given a span
        BulletPointSpan span = new BulletPointSpan(GAP_WIDTH, 0);

        // When the leading margin is drawn but no text is set
        span.drawLeadingMargin(canvas, paint, 0, 0, 0, 0, 0, text, 0, 0, true,
                mock(Layout.class));

        // Check that no drawing methods are called
        verifyZeroInteractions(canvas);
        verifyZeroInteractions(paint);
    }

    @Test
    public void drawLeadingMarginHardwareAccelerated() {
        int x = 10;
        int dir = 15;
        int top = 5;
        int bottom = 7;
        int color = Color.RED;
        // Given a span that is set on a text
        BulletPointSpan span = new BulletPointSpan(GAP_WIDTH, color);
        text.setSpan(span, 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        when(canvas.isHardwareAccelerated()).thenReturn(true);

        // When the leading margin is drawn
        span.drawLeadingMargin(canvas, paint, x, dir, top, 0, bottom, text, 0, 0, true, mock
                (Layout.class));

        // Check that the correct canvas and paint methods are called, in the correct order
        InOrder inOrder = inOrder(canvas, paint);
        inOrder.verify(paint).setColor(color);
        inOrder.verify(paint).setStyle(eq(Paint.Style.FILL));
        inOrder.verify(canvas).save();
        inOrder.verify(canvas).translate(
                eq(GAP_WIDTH + x + dir * BulletPointSpan.BULLET_RADIUS),
                eq((top + bottom) / 2f));
        inOrder.verify(canvas).drawPath(Matchers.any(Path.class), eq(paint));
        inOrder.verify(canvas).restore();
    }

    @Test
    public void drawLeadingMarginNotHardwareAccelerated() {
        int x = 10;
        int dir = 15;
        int top = 5;
        int bottom = 7;
        int color = Color.RED;
        // Given a span that is set on a text
        BulletPointSpan span = new BulletPointSpan(GAP_WIDTH, color);
        text.setSpan(span, 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        when(canvas.isHardwareAccelerated()).thenReturn(false);

        // When the leading margin is drawn
        span.drawLeadingMargin(canvas, paint, x, dir, top, 0, bottom, text, 0, 0, true, mock
                (Layout.class));

        // Check that the correct canvas and paint methods are called, in the correct order
        InOrder inOrder = inOrder(canvas, paint);
        inOrder.verify(paint).setColor(color);
        inOrder.verify(paint).setStyle(eq(Paint.Style.FILL));
        inOrder.verify(canvas).drawCircle(eq(GAP_WIDTH + x + dir * BulletPointSpan.BULLET_RADIUS),
                eq((top + bottom) / 2f), eq(BulletPointSpan.BULLET_RADIUS), eq(paint));
        verify(canvas, never()).save();
        verify(canvas, never()).translate(
                eq(GAP_WIDTH + x + dir * BulletPointSpan.BULLET_RADIUS),
                eq((top + bottom) / 2f));
        verify(canvas, never()).restore();
    }
}