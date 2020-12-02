package org.smartregister.eusm.shadow;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

/**
 * Created by samuelgithengi on 1/23/20.
 */
@Implements(AppMapView.class)
public class RevealMapViewShadow extends KujakuMapViewShadow {

    @Implementation
    public void __constructor__(@NonNull Context context, @Nullable AttributeSet attrs) {
        // Do nothing
    }

}
