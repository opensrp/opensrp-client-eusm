package org.smartregister.eusm.shadow;

import com.mapbox.mapboxsdk.style.layers.LineLayer;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

@Implements(LineLayer.class)
public class LineLayerShadow extends LayerShadow {

    @Implementation
    protected void initialize(String layerId, String sourceId) {
        //do nothing
    }

}
