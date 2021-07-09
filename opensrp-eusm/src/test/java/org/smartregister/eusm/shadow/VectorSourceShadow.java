package org.smartregister.eusm.shadow;

import com.mapbox.mapboxsdk.style.sources.VectorSource;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

@Implements(VectorSource.class)
public class VectorSourceShadow {

    @Implementation
    protected void initialize(String layerId, Object payload) {
        //do nothing
    }
}
