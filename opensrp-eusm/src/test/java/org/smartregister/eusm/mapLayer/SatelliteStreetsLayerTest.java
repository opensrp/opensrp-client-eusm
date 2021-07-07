package org.smartregister.eusm.mapLayer;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.sources.Source;
import com.mapbox.mapboxsdk.style.sources.VectorSource;
import com.mapbox.mapboxsdk.utils.ThreadUtils;

import org.junit.Before;
import org.junit.Test;
import org.smartregister.eusm.BaseUnitTest;

import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SatelliteStreetsLayerTest extends BaseUnitTest {

    private SatelliteStreetsLayer satelliteStreetsLayer;

    private Context context = ApplicationProvider.getApplicationContext();

    @Before
    public void setUp() {
        ThreadUtils.init(context);
        satelliteStreetsLayer = new SatelliteStreetsLayer(context);
    }

    @Test
    public void testLayerInitializedCorrectly() {
        List<Source> sources = satelliteStreetsLayer.getSources();
        assertFalse(sources.isEmpty());
        assertTrue(sources.get(0) instanceof VectorSource);

        LinkedHashSet<Layer> layers = satelliteStreetsLayer.getLayers();
        assertFalse(layers.isEmpty());
        assertEquals(77, layers.size());
    }

    @Test
    public void testGetLayerIdsShouldReturnCorrectArraySize() {
        String[] layerIds = satelliteStreetsLayer.getLayerIds();
        assertEquals(77, layerIds.length);
    }
}