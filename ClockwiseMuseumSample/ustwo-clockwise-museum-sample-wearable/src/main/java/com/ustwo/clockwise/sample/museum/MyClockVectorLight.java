package com.ustwo.clockwise.sample.museum;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
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
import com.ustwo.clockwise.sample.utils.Spec;
import com.ustwo.clockwise.util.TimeUtil;

import java.util.TimeZone;

/**
 * Created by anguyen on 2/12/2016.
 */
public class MyClockVectorLight extends ConfigurableConnectedWatchFace {

    private static final int NORMAL_BG_COLROR = Color.WHITE;
    private static final int LOWBIT_BG_COLRO = Color.BLACK;
    private static final int NORMAL_MINUTE_HAND_COLOR = Color.parseColor("#4DCDC0");
    private static final int LOWBIT_MINUTE_HAND_COLOR = Color.parseColor("#4DCDC0");
    private static final int NORMAL_HOUR_HAND_COLOR = Color.parseColor("#FC8F7A");
    private static final int LOWBIT_HOUR_HAND_COLOR = Color.parseColor("#FC8F7A");

    private static final int FORE_GROUND_COLOR = Color.parseColor("#007074");

    private static final float HOUR_HAND_LONG = 100.0f;
    private static final float HOUR_HAND_WIDTH = 10.0f;
    private static final float MINUTE_HANDE_LONG = 120.0f;
    private static final float MINUTE_HANDE_WIDTH = 8.0f;
    private static final float CENTER_DOT_DIMENS = 10.0f;

    /**
     * Bitmaps
     */

    private Bitmap bgBitmap;

    /**
     * Colors
     */

    private int bgColor;
    private int hourHandColor;
    private int minuteHandColor;
    private int foreGroundColor;

    /**
     * Paints
     */
    private Paint bitmapPaint = new Paint();
    private Paint hourHandPaint = new Paint();
    private Paint minuteHandPaint = new Paint();
    private Paint textPaint = new Paint();

    /**
     * Position of the center of the watch face (in pixels)
     */
    private PointF watchFaceCenter = new PointF(0f, 0f);

    /**
     * Degrees at which hands will be drawn on the next draw cycle
     */
    private float currentDegreeHourHand = 0.0f;
    private float currentDegreeMinuteHand = 0.0f;

    /**
     * Dimens of hands
     */
    private float hourHandLong;
    private float hourHandWidth;
    private float minuteHandLong;
    private float minuteHandWidth;

    private float centerDotDimens;

    @Override
    public void onCreate() {
        super.onCreate();

        this.textPaint.setTypeface(Typeface.DEFAULT);

        this.bitmapPaint.setAntiAlias(true);
        this.hourHandPaint.setAntiAlias(true);
        this.minuteHandPaint.setAntiAlias(true);
        this.textPaint.setAntiAlias(true);
    }

    @Override
    protected void onLayout(WatchShape watchShape, Rect rect, WindowInsets windowInsets) {
        // Convert spec dimensions to current screen size
        float renderSize = Math.min(getWidth(), getHeight());

        // set center point
        this.watchFaceCenter.set(getWidth() * 0.5f, getHeight() * 0.5f);

        // Set scaled hands long
        this.hourHandLong = Spec.getFloatValueFromSpec(HOUR_HAND_LONG, renderSize);
        this.minuteHandLong = Spec.getFloatValueFromSpec(MINUTE_HANDE_LONG, renderSize);

        // Set scaled hands width
        this.hourHandWidth = Spec.getFloatValueFromSpec(HOUR_HAND_WIDTH, renderSize);
        this.minuteHandWidth = Spec.getFloatValueFromSpec(MINUTE_HANDE_WIDTH, renderSize);

        this.centerDotDimens = Spec.getFloatValueFromSpec(CENTER_DOT_DIMENS, renderSize);

        this.hourHandPaint.setStyle(Paint.Style.STROKE);
        this.hourHandPaint.setStrokeWidth(this.hourHandWidth);

        this.minuteHandPaint.setStyle(Paint.Style.STROKE);
        this.minuteHandPaint.setStrokeWidth(this.minuteHandWidth);

        this.textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.textPaint.setStrokeWidth(this.minuteHandWidth);

        /*update current state in the beginning*/
        this.refreshCurrentState();
        this.updateHandPositions(getTime());
    }

    @Override
    protected void onWatchFaceConfigChanged(SharedPreferences sharedPreferences, String key) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(this.bgColor);
        if(this.bgBitmap != null) {
            canvas.drawBitmap(this.bgBitmap, 0, 0, this.bitmapPaint);
        }

        drawHand(canvas, this.hourHandLong, this.hourHandPaint, this.currentDegreeHourHand);
        drawHand(canvas, this.minuteHandLong, this.minuteHandPaint, this.currentDegreeMinuteHand);

        canvas.save();
        canvas.translate(this.watchFaceCenter.x, this.watchFaceCenter.y);
        canvas.drawCircle(0f, 0, this.centerDotDimens, this.textPaint);
        canvas.restore();

        drawNumbers(canvas, this.textPaint);
    }

    @Override
    protected void onTimeChanged(WatchFaceTime oldTime, WatchFaceTime newTime) {
        updateHandPositions(newTime);
    }

    @Override
    protected long getInteractiveModeUpdateRate() {
        return DateUtils.MINUTE_IN_MILLIS;
    }

    @Override
    public void onWatchModeChanged(WatchMode watchMode) {
        refreshCurrentState();
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

    private void updateHandPositions(WatchFaceTime timeStamp) {
        this.currentDegreeHourHand = TimeUtil.getHourDegrees(timeStamp);
        this.currentDegreeMinuteHand = TimeUtil.getMinuteDegrees(timeStamp);
    }

    private void drawNumbers(Canvas canvas, Paint paint) {
        for (int i = 1; i <= 12; i++) {
            canvas.save();
            float degree = TimeUtil.getHourDegrees(i);
            canvas.rotate(degree, this.watchFaceCenter.x, this.watchFaceCenter.y);
            canvas.drawLine(this.watchFaceCenter.x, this.watchFaceCenter.y + 150f, this.watchFaceCenter.x, this.watchFaceCenter.y + 160f, paint);
            canvas.restore();
        }
    }

    private void drawHand(Canvas canvas, float length, Paint paint, float degree) {
        canvas.save();
        canvas.rotate(degree, this.watchFaceCenter.x, this.watchFaceCenter.y);
        canvas.drawLine(this.watchFaceCenter.x, this.watchFaceCenter.y + 30.0f,
                this.watchFaceCenter.x, this.watchFaceCenter.y - length, paint);
        canvas.restore();
    }

    private void refreshCurrentState() {
        WatchMode currentWatchMode = getCurrentWatchMode();

        switch (currentWatchMode) {
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

    private void applyLowBitState() {
        this.bgColor = LOWBIT_BG_COLRO;
        this.hourHandColor = LOWBIT_HOUR_HAND_COLOR;
        this.minuteHandColor = LOWBIT_MINUTE_HAND_COLOR;
        this.foreGroundColor = FORE_GROUND_COLOR;

        this.hourHandPaint.setColor(this.hourHandColor);
        this.minuteHandPaint.setColor(this.minuteHandColor);
        this.textPaint.setColor(this.foreGroundColor);
    }

    private void applyAmbientState() {
        this.bgColor = LOWBIT_BG_COLRO;
        this.hourHandColor = LOWBIT_HOUR_HAND_COLOR;
        this.minuteHandColor = LOWBIT_MINUTE_HAND_COLOR;
        this.foreGroundColor = FORE_GROUND_COLOR;

        this.hourHandPaint.setColor(this.hourHandColor);
        this.minuteHandPaint.setColor(this.minuteHandColor);
        this.textPaint.setColor(this.foreGroundColor);
    }

    private void applyInteractiveState() {
        this.bgColor = NORMAL_BG_COLROR;
        this.hourHandColor = NORMAL_HOUR_HAND_COLOR;
        this.minuteHandColor = NORMAL_MINUTE_HAND_COLOR;
        this.foreGroundColor = FORE_GROUND_COLOR;

        this.hourHandPaint.setColor(this.hourHandColor);
        this.minuteHandPaint.setColor(this.minuteHandColor);
        this.textPaint.setColor(this.foreGroundColor);
    }
}
