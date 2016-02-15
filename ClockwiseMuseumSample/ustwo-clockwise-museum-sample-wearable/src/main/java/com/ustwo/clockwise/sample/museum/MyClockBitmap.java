package com.ustwo.clockwise.sample.museum;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.format.DateUtils;
import android.view.WindowInsets;

import com.ustwo.clockwise.WatchFaceTime;
import com.ustwo.clockwise.WatchShape;
import com.ustwo.clockwise.sample.utils.Spec;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by AnNguyen on 2/14/16.
 */
public class MyClockBitmap  extends BaseConfigurableWatchFace {

    /**
     * Constants for this face
     */
    private static final float DESIGN_SIZE = 320f;

    private static final int DATE_TEXT_COLOR = Color.parseColor("#660d13");
    private static final int TIME_TEXT_COLOR = Color.parseColor("#660d13");
    private static final int SHADOW_TEXT_COLOR = Color.parseColor("#000000");
    private static final int LOWBIT_BG_COLOR = Color.parseColor("#000000");
    private static final int BG_COLOR = Color.parseColor("#000000");

    private static final float DATE_TEXT_SIZE = 40.0f;
    private static final float TIME_TEXT_SIZE = 85.0f;

    private static final PointF DATE_TEXT_POSITION = new PointF(165f, 210f);
    private static final PointF TIME_TEXT_POSITION = new PointF(159f, 120f);

    /**
     * Bitmaps
     */
    private Bitmap bgBitmap;

    /**
     * Paints
     */
    private Paint dateTextPaint = new Paint();
    private Paint timeTextPaint = new Paint();
    private Paint bgPaint = new Paint();

    /**
     * Colors
     */
    private int dateColor;
    private int timeColor;
    private int shadowColor;

    /**
     * Texts will be drawn
     */
    private String timeText;
    private String dateText;

    /**
     * Current date
     */
    private Date date = new Date();

    /**
     * Date formats
     */
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd");
    private SimpleDateFormat timeFormat12 = new SimpleDateFormat("H:mm");
    private SimpleDateFormat timeFormat24 = new SimpleDateFormat("HH:mm");

    /**
     * Center point
     */
    private PointF watchFaceCenter = new PointF(0.0f, 0.0f);

    /**
     * Positions
     */

    private PointF datePosition = new PointF();
    private PointF timePosition = new PointF();


    @Override
    public void onCreate() {
        super.onCreate();

        this.bgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg1);

        this.dateColor = DATE_TEXT_COLOR;
        this.timeColor = TIME_TEXT_COLOR;
        this.shadowColor = SHADOW_TEXT_COLOR;

        this.dateTextPaint.setColor(this.dateColor);
        this.timeTextPaint.setColor(this.timeColor);

        this.dateTextPaint.setTypeface(Typeface.create("san-serif-light", Typeface.NORMAL));
        this.dateTextPaint.setTextAlign(Paint.Align.CENTER);

        this.timeTextPaint.setTypeface(Typeface.create("san-serif-light", Typeface.NORMAL));
        this.timeTextPaint.setTextAlign(Paint.Align.CENTER);

        this.bgPaint.setAntiAlias(true);
        this.dateTextPaint.setAntiAlias(true);
        this.timeTextPaint.setAntiAlias(true);
    }

    @Override
    protected void onLayout(WatchShape watchShape, Rect rect, WindowInsets windowInsets) {
        /** Convert spec dimension to current screen size */
        float renderSize = Math.min(getWidth(), getHeight());

        /** Apply center point */
        this.watchFaceCenter.set(getWidth() * 0.5f, getHeight() * 0.5f);

        Spec.applyPointValueFromSpec(this.timePosition, TIME_TEXT_POSITION, renderSize);
        Spec.applyPointValueFromSpec(this.datePosition, DATE_TEXT_POSITION, renderSize);

        this.timeTextPaint.setTextSize(Spec.getFloatValueFromSpec(TIME_TEXT_SIZE, renderSize, DESIGN_SIZE));
        this.dateTextPaint.setTextSize(Spec.getFloatValueFromSpec(DATE_TEXT_SIZE, renderSize, DESIGN_SIZE));

        refreshCurrentState();
        WatchFaceTime time = getTime();
        updateDateAndTimeText(time);
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

    @Override
    protected void onWatchFaceConfigChanged(SharedPreferences sharedPreferences, String key) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(BG_COLOR);
        canvas.drawBitmap(this.bgBitmap, 0, 0, this.bgPaint);

        canvas.save();

        /** Apply translation to canvas, all render operation can be in reference to the center of the face */
        //canvas.translate(this.watchFaceCenter.x, this.watchFaceCenter.y);
        canvas.drawText(this.timeText, this.timePosition.x, this.timePosition.y, this.timeTextPaint);
        canvas.drawText(this.dateText, this.datePosition.x, this.datePosition.y, this.dateTextPaint);

        canvas.restore();
    }

    @Override
    protected void applyLowBitState() {

    }

    @Override
    protected void applyAmbientState() {

    }

    @Override
    protected void applyInteractiveState() {

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
}
