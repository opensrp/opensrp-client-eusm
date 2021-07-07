package org.smartregister.eusm.shadow;

import com.mapbox.mapboxsdk.style.layers.RasterLayer;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

@Implements(RasterLayer.class)
public class RasterLayerShadow extends LayerShadow {

    @Implementation
    protected void initialize(String layerId, String sourceId) {
        //Do nothing
    }
}
