package org.smartregister.eusm.shadow;

import com.mapbox.mapboxsdk.style.sources.RasterSource;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

@Implements(RasterSource.class)
public class RasterSourceShadow {

    @Implementation
    protected void initialize(String layerId, Object payload) {
        //do nothing
    }
}
