package com.amulyakhare.textdrawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;

public class TextDrawable extends ShapeDrawable {

    private final Paint textPaint;
    private final Paint borderPaint;

    private final String text;
    private final int width;
    private final int height;

    private final int borderThickness;

    private TextDrawable(Builder builder) {
        super(builder.shape);

        this.text = builder.text;
        this.width = builder.width;
        this.height = builder.height;
        this.borderThickness = builder.borderThickness;

        getPaint().setColor(builder.color);
        getPaint().setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setColor(builder.textColor);
        textPaint.setAntiAlias(true);
        textPaint.setFakeBoldText(builder.isBold);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTypeface(builder.font);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setStrokeWidth(builder.borderThickness);

        borderPaint = new Paint();
        borderPaint.setColor(darkenColor(builder.color));
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(builder.borderThickness);
        borderPaint.setAntiAlias(true);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        Rect bounds = getBounds();

        if (borderThickness > 0) {
            drawBorder(canvas, bounds);
        }

        int actualWidth = bounds.width();
        int actualHeight = bounds.height();

        int drawableWidth = width > 0 ? width : actualWidth;
        int drawableHeight = height > 0 ? height : actualHeight;

        float textSize = Math.min(drawableWidth, drawableHeight) / 2f;
        textPaint.setTextSize(textSize);

        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();

        float x = bounds.centerX();
        float y = bounds.centerY()
                - (fontMetrics.ascent + fontMetrics.descent) / 2f;

        canvas.drawText(text, x, y, textPaint);
    }

    private void drawBorder(Canvas canvas, Rect bounds) {
        float inset = borderThickness / 2f;

        canvas.drawRect(
                bounds.left + inset,
                bounds.top + inset,
                bounds.right - inset,
                bounds.bottom - inset,
                borderPaint
        );
    }

    @Override
    public int getIntrinsicWidth() {
        return width;
    }

    @Override
    public int getIntrinsicHeight() {
        return height;
    }

    @Override
    public void setAlpha(int alpha) {
        textPaint.setAlpha(alpha);
        getPaint().setAlpha(alpha);
        borderPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        textPaint.setColorFilter(colorFilter);
        getPaint().setColorFilter(colorFilter);
        borderPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    private static int darkenColor(int color) {
        return Color.rgb(
                (int) (Color.red(color) * 0.9),
                (int) (Color.green(color) * 0.9),
                (int) (Color.blue(color) * 0.9)
        );
    }

    public static class Builder {

        private int width = -1;
        private int height = -1;

        private int color = Color.GRAY;
        private int textColor = Color.WHITE;
        private int borderThickness = 0;

        private String text = "";

        private boolean isBold = false;
        private boolean toUpperCase = false;

        private Typeface font = Typeface.create(
                "sans-serif-light",
                Typeface.NORMAL
        );

        private android.graphics.drawable.shapes.Shape shape =
                new RectShape();

        private float radius = 0f;

        public Builder beginConfig() {
            return this;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder height(int height) {
            this.height = height;
            return this;
        }

        public Builder textColor(int textColor) {
            this.textColor = textColor;
            return this;
        }

        public Builder useFont(Typeface font) {
            if (font != null) {
                this.font = font;
            }

            return this;
        }

        public Builder fontSize(int size) {
            return this;
        }

        public Builder bold() {
            this.isBold = true;
            return this;
        }

        public Builder toUpperCase() {
            this.toUpperCase = true;
            return this;
        }

        public Builder withBorder(int thickness) {
            this.borderThickness = thickness;
            return this;
        }

        public Builder endConfig() {
            return this;
        }

        public TextDrawable build(String text, int color) {
            this.shape = new RectShape();
            prepare(text, color);

            return new TextDrawable(this);
        }

        public TextDrawable buildRound(String text, int color) {
            this.shape = new OvalShape();
            prepare(text, color);

            return new TextDrawable(this);
        }

        public TextDrawable buildRoundRect(
                String text,
                int color,
                int radius
        ) {
            this.radius = radius;

            float[] radii = new float[]{
                    radius, radius,
                    radius, radius,
                    radius, radius,
                    radius, radius
            };

            this.shape = new RoundRectShape(
                    radii,
                    null,
                    null
            );

            prepare(text, color);

            return new TextDrawable(this);
        }

        private void prepare(String text, int color) {
            String safeText = text == null ? "" : text;

            if (toUpperCase) {
                safeText = safeText.toUpperCase();
            }

            this.text = safeText;
            this.color = color;
        }
    }
}