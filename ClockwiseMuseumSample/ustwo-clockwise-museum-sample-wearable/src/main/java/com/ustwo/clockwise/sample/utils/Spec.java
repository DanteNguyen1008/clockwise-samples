package com.ustwo.clockwise.sample.utils;

import android.graphics.PointF;

/**
 * Created by anguyen on 2/12/2016.
 */
public class Spec {
    /**
     * The design display size, in reference to which specs are created
     */
    public static final float SPEC_SIZE = 320.0f;

    /**
     * Scales a float dimension from a default spec value to the specified screen size
     * @param specValue The spec value to scale
     * @param currentRenderSize The screen size
     * @return The scaled dimension
     */
    public static float getFloatValueFromSpec(float specValue, float currentRenderSize) {
        return getFloatValueFromSpec(specValue, currentRenderSize, SPEC_SIZE);
    }

    /**
     * Scales a float dimension from a spec value to the specified screen size
     *
     * @param specValue The spec value to scale
     * @param currentRenderSize The screen size
     * @param specSize the specified spec size
     * @return
     */
    public static float getFloatValueFromSpec(float specValue, float currentRenderSize, float specSize) {
        return (specValue / specSize) * currentRenderSize;
    }

    /**
     * Scales a point value from a spec value to the specified screen size, and returns result in output
     * @param output The point which will be set to the output of the operation
     * @param specValue The spec point to be scaled
     * @param currentRenderSize The screen size
     */
    public static void applyPointValueFromSpec(PointF output, PointF specValue, float currentRenderSize) {
        if(output == null || specValue == null) {
            return;
        }
        output.set(getFloatValueFromSpec(specValue.x, currentRenderSize), getFloatValueFromSpec(specValue.y, currentRenderSize));
    }

}
