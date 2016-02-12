package com.ustwo.clockwise.sample.museum;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.WindowInsets;

import com.ustwo.clockwise.WatchFaceTime;
import com.ustwo.clockwise.WatchMode;
import com.ustwo.clockwise.WatchShape;
import com.ustwo.clockwise.sample.common.ConfigurableConnectedWatchFace;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by anguyen on 2/11/2016.
 */
public class MyClockWiseSample extends ConfigurableConnectedWatchFace{

    private static class Spec {
        /**
         * The design display size, in reference to which specs are created
         */
        public static final float SPEC_SIZE = 320.0f;

        // Center circle elements specs
        /**
         * Position of the time text, in relation to the top-left corner of the inner circle
         */
        public static final PointF SPEC_timeTextPosition = new PointF(100f, 64f);

        /**
         * Position of the date text, in relation to the top-left corner of the inner circle
         */
        public static final PointF SPEC_dateTextPosition = new PointF(100f, 140f);

        /**
         * Font size of the time text
         */
        public static final float SPEC_timeTextSize = 36.0f;

        /**
         * Font size of the date text
         */
        public static final float SPEC_dateTextSize = 18.0f;

        public static final int SPEC_COLOR_TIME_TEXT = 0x42FFFFFF;
        public static final int SPEC_COLOR_DATE_TEXT = 0x42FFFFFF;  // 26% alpha

        public static final int SPEC_COLOR_NORMAL_BACKGROUND = 0xFF000000;

        public static final int SPEC_COLOR_LOWBIT_BACKGROUND = 0xFF000000;
        public static final int SPEC_COLOR_LOWBIT_FOREGROUND = 0xFFFFFFFF;
    }

    /**
     * Paints to draw bitmap (BG), time, date
     */
    private Paint bitmapPaint = new Paint();
    private Paint timeTextPaint = new Paint();
    private Paint dateTextPaint = new Paint();

    /**
     * Position of the center of the watch face (in pixels)
     */
    private PointF watchFaceCenter = new PointF(0f, 0f);

    /**
     * Current time text (will be drawn on next draw circle)
     */
    private String timeText = "00:00";

    /**
     * Current date text (will be drawn on next draw circle)
     */
    private String dateText = "";

    /**
     * Current date
     */
    private Date date = new Date();

    /**
     * Date formats to format time/date
     */
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd");
    private SimpleDateFormat timeFormat12 = new SimpleDateFormat("h:mm");
    private SimpleDateFormat timeFormat24 = new SimpleDateFormat("HH:mm");

    /**
     * Specs scaled to current device dimension
     */
    private PointF timeTextPosition = new PointF(0f, 0f);
    private PointF dateTextPosition = new PointF(0f, 0f);

    /**
     * Colors
     */

    private int currentBGColor;
    private int dateTextColor;
    private int timeTextColor;

    /**
     * Bitmaps
     */
    private Bitmap currentBGBitmap;

    @Override
    public void onCreate() {
        super.onCreate();

        /** set up paints */
        timeTextPaint.setTypeface(Typeface.create("san-serif-light", Typeface.NORMAL));
        timeTextPaint.setTextAlign(Paint.Align.CENTER);

        dateTextPaint.setTypeface(Typeface.create("san-serif-light", Typeface.NORMAL));
        dateTextPaint.setTextAlign(Paint.Align.CENTER);

        bitmapPaint.setAntiAlias(true);
        timeTextPaint.setAntiAlias(true);
        dateTextPaint.setAntiAlias(true);

        this.currentBGBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.jpay_bg);
    }

    @Override
    protected void onLayout(WatchShape watchShape, Rect rect, WindowInsets windowInsets) {
        /** Convert spec dimension to current screen size */
        float renderSize = Math.min(getWidth(), getHeight());

        /** Apply center point */
        this.watchFaceCenter.set(getWidth() * 0.5f, getHeight() * 0.5f);

        ClockwiseSampleMuseumWatchFace.applyPointValueFromSpec(timeTextPosition, Spec.SPEC_timeTextPosition, renderSize);
        ClockwiseSampleMuseumWatchFace.applyPointValueFromSpec(dateTextPosition, Spec.SPEC_dateTextPosition, renderSize);

        timeTextPaint.setTextSize(ClockwiseSampleMuseumWatchFace.getFloatValueFromSpec(Spec.SPEC_timeTextSize, renderSize));
        dateTextPaint.setTextSize(ClockwiseSampleMuseumWatchFace.getFloatValueFromSpec(Spec.SPEC_dateTextSize, renderSize));

        refreshCurrentState();
        WatchFaceTime time = getTime();
        updateDateAndTimeText(time);
    }

    @Override
    protected void onWatchFaceConfigChanged(SharedPreferences sharedPreferences, String key) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.drawColor(this.currentBGColor);
        canvas.drawBitmap(this.currentBGBitmap, 0, 0, this.bitmapPaint);
        /** Apply translation to canvas, all render operation can be in reference to the center of the face */
        canvas.translate(this.watchFaceCenter.x, this.watchFaceCenter.y);
        canvas.drawText(this.timeText, 0, -75, this.timeTextPaint);
        canvas.drawText(this.dateText, 0, 70, this.dateTextPaint);

        canvas.restore();
    }

    @Override
    protected WatchFaceStyle getWatchFaceStyle() {
        WatchFaceStyle.Builder builder =
                new WatchFaceStyle.Builder(this)
                        .setCardPeekMode(WatchFaceStyle.PEEK_MODE_VARIABLE)
                        .setAmbientPeekMode(WatchFaceStyle.AMBIENT_PEEK_MODE_VISIBLE)
                        .setPeekOpacityMode(WatchFaceStyle.PEEK_OPACITY_MODE_TRANSLUCENT)
                        .setCardProgressMode(WatchFaceStyle.PROGRESS_MODE_NONE)
                        .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                        .setViewProtection(WatchFaceStyle.PROTECT_HOTWORD_INDICATOR | WatchFaceStyle.PROTECT_STATUS_BAR)
                        .setHotwordIndicatorGravity(Gravity.TOP | Gravity.LEFT)
                        .setStatusBarGravity(Gravity.TOP | Gravity.LEFT)
                        .setShowSystemUiTime(false);
        return builder.build();
    }

    @Override
    protected long getInteractiveModeUpdateRate() {
        return DateUtils.SECOND_IN_MILLIS;
    }

    @Override
    protected void on24HourFormatChanged(boolean is24HourFormat) {
        // Handle 24-hour format setting changes (if using digital time display)

        this.timeText = is24HourFormat() ? this.timeFormat24.format(this.date) :
                this.timeFormat12.format(this.date);
    }

    @Override
    public void onWatchModeChanged(WatchMode watchMode) {
        refreshCurrentState();
    }

    @Override
    protected void onTimeChanged(WatchFaceTime oldTime, WatchFaceTime newTime) {
        if(newTime.hasTimeZoneChanged(oldTime)) {
            TimeZone timeZone = TimeZone.getTimeZone(newTime.timezone);
            this.timeFormat12.setTimeZone(timeZone);
            this.timeFormat24.setTimeZone(timeZone);
            this.dateFormat.setTimeZone(timeZone);
        }

        if(newTime.hasMinuteChanged(oldTime) || newTime.hasHourChanged(oldTime) || newTime.hasDateChanged(oldTime)) {
            this.updateDateAndTimeText(newTime);
        }
    }

    /**
     * Refresh date and time text with new timeStamp
     * @param timeStamp - new timestamp
     */
    private void updateDateAndTimeText(WatchFaceTime timeStamp) {
        this.date.setTime(timeStamp.toMillis(false));

        this.dateText = this.dateFormat.format(this.date);
        this.timeText = is24HourFormat() ? this.timeFormat24.format(this.date) :
                this.timeFormat12.format(this.date);
    }

    private void refreshCurrentState() {
        WatchMode currentWatchMode = getCurrentWatchMode();

        switch(currentWatchMode) {
            case INTERACTIVE:
                applyInteractiveState();
                break;
            case AMBIENT:
                // Non-low bit ambient mode
                applyAmbientState();
                break;
            default:
                // Other ambient modes (LOW_BIT, BURN_IN, LOW_BIT_BURN_IN)
                applyLowBitState();
                break;
        }
    }

    /**
     * Apply interactive-mode paint colors and background images
     */
    private void applyInteractiveState() {
        currentBGColor = Spec.SPEC_COLOR_NORMAL_BACKGROUND;
        dateTextColor = Spec.SPEC_COLOR_DATE_TEXT;
        timeTextColor = Spec.SPEC_COLOR_TIME_TEXT;

        timeTextPaint.setColor(timeTextColor);
        dateTextPaint.setColor(dateTextColor);
    }

    /**
     * Apply low-bit ambient mode paint colors and background images
     */
    private void applyLowBitState() {
        currentBGColor = Spec.SPEC_COLOR_LOWBIT_BACKGROUND;
        dateTextColor = Spec.SPEC_COLOR_LOWBIT_FOREGROUND;
        timeTextColor = Spec.SPEC_COLOR_LOWBIT_FOREGROUND;

        timeTextPaint.setColor(timeTextColor);
        dateTextPaint.setColor(dateTextColor);
    }

    /**
     * Apply ambient-mode (non-low bit) paint colors and background images.
     * This mode is similar to the dark interactive mode, but doesn't show the second hand.
     */
    private void applyAmbientState() {
        currentBGColor = Spec.SPEC_COLOR_LOWBIT_BACKGROUND;
        dateTextColor = Spec.SPEC_COLOR_LOWBIT_FOREGROUND;
        timeTextColor = Spec.SPEC_COLOR_LOWBIT_FOREGROUND;

        timeTextPaint.setColor(timeTextColor);
        dateTextPaint.setColor(dateTextColor);
    }
}
