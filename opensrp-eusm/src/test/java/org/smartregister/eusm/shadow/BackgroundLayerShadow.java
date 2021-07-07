package org.smartregister.eusm.shadow;

import com.mapbox.mapboxsdk.style.layers.BackgroundLayer;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

@Implements(BackgroundLayer.class)
public class BackgroundLayerShadow extends LayerShadow{

    @Implementation
    protected void initialize(String layerId) {
        //Do nothing
    }
}
