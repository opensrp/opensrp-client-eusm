package org.smartregister.eusm.validators;

import com.mapbox.geojson.Feature;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.reflect.Whitebox;
import org.smartregister.eusm.BaseUnitTest;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Richard Kareko on 4/16/20.
 */

public class GeoFencingValidatorTest extends BaseUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private AppMapView appMapView;

    @Mock
    private Feature operationalArea;

    @Mock
    private MapboxMap mapboxMap;

    private GeoFencingValidator validator;

    @Before
    public void setUp() {
        validator = new GeoFencingValidator("error", appMapView, operationalArea);
    }

    @Test
    public void testInitialization() {
        assertNotNull(Whitebox.getInternalState(validator, "mapView"));
        assertNotNull(Whitebox.getInternalState(validator, "operationalArea"));
    }

//    @Test
//    public void testIsValidWithPointInsideOA() {
//        LatLng target = new LatLng(15.0665603, 101.1760481);
//        initializeMocks(target);
//
//        boolean isvalid = validator.isValid("criteria", false);
//        assertTrue(isvalid);
//
//    }
//
//    @Test
//    public void testIsValidWithPointOutsideOA() {
//        LatLng target = new LatLng(19.0665603, 90.1760481);
//        initializeMocks(target);
//
//        boolean isvalid = validator.isValid("criteria", false);
//        assertFalse(isvalid);
//
//    }

//    @Test
//    public void testIsValidWithPointOutsideOAWhenOnOtherOA() {
//        when(operationalArea.hasProperty(LOCATION_NAME)).thenReturn(true);
//        String name = "287 Palms Other";
//        when(operationalArea.getStringProperty(LOCATION_NAME)).thenReturn(name);
//        validator = new GeoFencingValidator("error", appMapView, operationalArea);
//        LatLng target = new LatLng(19.0665603, 90.1760481);
//        initializeMocks(target);
//
//        boolean isValid = validator.isValid("criteria", false);
//        assertFalse(isValid);
//        assertEquals(R.string.point_not_within_other_operational_area, validator.getErrorId());
//        assertEquals(name, validator.getSelectedOperationalArea());
//        assertArrayEquals(new String[]{name}, validator.getErrorMessageArgs());
//
//    }
//
//    @Test
//    public void testIsValidWithPointInAnotherOperationalArea() {
//
//        Feature validOA = Feature.fromJson(TestingUtils.operationalArea2Feature);
//        validator.getOperationalAreas().add(validOA);
//        LatLng target = new LatLng(-9.342380199986167, 28.857339199944192);
//        initializeMocks(target);
//
//        boolean isValid = validator.isValid("criteria", false);
//        assertFalse(isValid);
//        assertEquals(R.string.point_within_known_operational_area, validator.getErrorId());
//        assertEquals(validOA.getStringProperty(LOCATION_NAME), validator.getSelectedOperationalArea());
//        assertArrayEquals(new String[]{validOA.getStringProperty(LOCATION_NAME)}, validator.getErrorMessageArgs());
//
//    }
//
//
//    @Test
//    public void testIsValidWithPointOutsideOAWhenOtherOAIsDefined() {
//
//        Feature otherOA = Feature.fromJson(TestingUtils.operationalArea2Feature);
//        validator.setOtherOperationalArea(otherOA);
//        LatLng target = new LatLng(-9.342380199986167, 28.857339199944192);
//        initializeMocks(target);
//
//        boolean isValid = validator.isValid("criteria", false);
//        assertFalse(isValid);
//        assertEquals(R.string.point_not_within_known_operational_area, validator.getErrorId());
//        assertEquals(otherOA.getStringProperty(LOCATION_NAME), validator.getSelectedOperationalArea());
//        assertArrayEquals(new String[]{otherOA.getStringProperty(LOCATION_NAME)}, validator.getErrorMessageArgs());
//
//    }
//
//    @Test
//    public void testSetDisabled() {
//        assertFalse(Whitebox.getInternalState(validator, "disabled"));
//        validator.setDisabled(true);
//        assertTrue(Whitebox.getInternalState(validator, "disabled"));
//    }
//
//    private void initializeMocks(LatLng target) {
//        when(mapboxMap.getCameraPosition()).thenReturn(new CameraPosition.Builder().zoom(18).target(target).build());
//        when(appMapView.getMapboxMap()).thenReturn(mapboxMap);
//        Feature feature = Feature.fromJson(TestingUtils.feature);
//        when(operationalArea.geometry()).thenReturn(feature.geometry());
//    }
}
