package com.android.example.text.styling.renderer.spans;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextPaint;

import org.junit.Test;

/**
 * Tests for {@link CodeBlockSpan} class
 */
public class CodeBlockSpanTest {

    private TextPaint paint = mock(TextPaint.class);
    private CodeBlockSpan span = new CodeBlockSpan(Typeface.DEFAULT, Color.RED);

    @Test
    public void updateDrawState() {
        // When the update draw state is called
        span.updateDrawState(paint);

        // Check that the correct color is set
        assertEquals(Color.RED, paint.bgColor);
    }
}