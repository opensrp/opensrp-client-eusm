package org.smartregister.eusm.util;

import com.google.gson.reflect.TypeToken;

import org.junit.Before;
import org.junit.Test;
import org.smartregister.domain.Location;
import org.smartregister.domain.LocationProperty;
import org.smartregister.domain.Task;
import org.smartregister.eusm.BaseUnitTest;
import org.smartregister.eusm.domain.StructureDetail;
import org.smartregister.util.JsonFormUtils;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.spy;

public class GeoJsonUtilsTest extends BaseUnitTest {

    private GeoJsonUtils geoJsonUtils;

    @Before
    public void setUp() {
        geoJsonUtils = spy(new GeoJsonUtils());
    }

    @Test
    public void testGetGeoJsonFromStructureDetailShouldReturnJsonStringWithMorePropsIfGeoJsonIsNotNull() {
        Location location = new Location();
        location.setProperties(new LocationProperty());
        StructureDetail structureDetail = new StructureDetail();
        structureDetail.setGeojson(location);
        structureDetail.setParentId(UUID.randomUUID().toString());
        structureDetail.setDistanceMeta("1.5 m away");
        structureDetail.setCommune("Amabtorohanana");
        structureDetail.setStructureId(UUID.randomUUID().toString());
        structureDetail.setTaskStatus(Task.TaskStatus.READY.toString());
        structureDetail.setStructureType("waterpoint");
        String jsonArrayString = geoJsonUtils.getGeoJsonFromStructureDetail(Collections.singletonList(structureDetail));
        List<Location> structures = JsonFormUtils.gson.fromJson(jsonArrayString, new TypeToken<List<Location>>() {
        }.getType());
        assertNotNull(structures);
        assertEquals(1, structures.size());
    }

    @Test
    public void testGetGeoJsonFromStructureDetailShouldReturnEmptyJsonStringIfGeoJsonIsNull() {
        StructureDetail structureDetail = new StructureDetail();
        structureDetail.setParentId(UUID.randomUUID().toString());
        structureDetail.setDistanceMeta("1.5 m away");
        structureDetail.setCommune("Amabtorohanana");
        structureDetail.setStructureId(UUID.randomUUID().toString());
        structureDetail.setTaskStatus(Task.TaskStatus.READY.toString());
        String jsonArrayString = geoJsonUtils.getGeoJsonFromStructureDetail(Collections.singletonList(structureDetail));
        List<Location> structures = JsonFormUtils.gson.fromJson(jsonArrayString, new TypeToken<List<Location>>() {
        }.getType());
        assertNotNull(structures);
        assertEquals(0, structures.size());
    }
}