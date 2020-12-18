package org.smartregister.eusm.helper;

import android.graphics.Bitmap;

import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.utils.ThreadUtils;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.eusm.BaseUnitTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class EusmTaskingMapHelperTest extends BaseUnitTest {

    private EusmTaskingMapHelper eusmTaskingMapHelper;

    @Before
    public void setUp() {
        eusmTaskingMapHelper = spy(new EusmTaskingMapHelper());
        ThreadUtils.init(RuntimeEnvironment.application);
    }

    @Test
    public void testAddCustomLayersShouldAddSymbolLayers() {
        Style mMapboxMapStyle = mock(Style.class);
        eusmTaskingMapHelper.addCustomLayers(mMapboxMapStyle, RuntimeEnvironment.application);
        verify(mMapboxMapStyle, atLeastOnce()).addImage(anyString(), any(Bitmap.class));
        verify(mMapboxMapStyle, atLeastOnce()).addLayer(any(SymbolLayer.class));
    }
}