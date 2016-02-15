package com.ustwo.clockwise.sample.museum;

import com.ustwo.clockwise.WatchMode;
import com.ustwo.clockwise.sample.common.ConfigurableConnectedWatchFace;

/**
 * Created by AnNguyen on 2/14/16.
 */
public abstract class BaseConfigurableWatchFace extends ConfigurableConnectedWatchFace {

    @Override
    public void onWatchModeChanged(WatchMode watchMode) {
        refreshCurrentState();
    }

    protected void refreshCurrentState() {
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

    protected abstract void applyLowBitState();
    protected abstract void applyAmbientState();
    protected abstract void applyInteractiveState();
}
