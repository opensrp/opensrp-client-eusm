package org.smartregister.eusm.shadow;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.smartregister.tasking.view.TaskingMapView;

/**
 * Created by samuelgithengi on 1/23/20.
 */
@Implements(TaskingMapView.class)
public class TaskingMapViewShadow extends KujakuMapViewShadow {

    @Implementation
    public void __constructor__(@NonNull Context context, @Nullable AttributeSet attrs) {
        // Do nothing
    }

}
