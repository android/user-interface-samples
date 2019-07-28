package com.android.example.text.styling.renderer.spans;

import static junit.framework.Assert.assertEquals;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.graphics.Typeface;
import android.text.TextPaint;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

/**
 * Tests for the {@link FontSpan} class
 */
public class FontSpanTest {

    private TextPaint paint = mock(TextPaint.class);
    private FontSpan span = new FontSpan(Typeface.DEFAULT);
    private Typeface defaultTypeface = Typeface.create("serif", Typeface.BOLD);

    @Before
    public void setUp() {
        when(paint.getTypeface()).thenReturn(defaultTypeface);
    }

    @Test
    public void updateMeasureState() {
        // When the update measure state is called
        span.updateMeasureState(paint);

        // Check that the typeface set has the correct typeface and style
        ArgumentCaptor<Typeface> captor = ArgumentCaptor.forClass(Typeface.class);
        verify(paint).setTypeface(captor.capture());
        assertEquals(Typeface.BOLD, captor.getValue().getStyle());
    }

    @Test
    public void updateDrawState() {
        // When the update draw state is called
        span.updateDrawState(paint);

        // Check that the typeface set has the correct typeface and style
        ArgumentCaptor<Typeface> captor = ArgumentCaptor.forClass(Typeface.class);
        verify(paint).setTypeface(captor.capture());
        assertEquals(Typeface.BOLD, captor.getValue().getStyle());
    }

}