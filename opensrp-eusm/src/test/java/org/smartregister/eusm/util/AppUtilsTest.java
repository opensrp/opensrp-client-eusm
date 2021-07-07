package org.smartregister.eusm.util;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.domain.Geometry;
import org.smartregister.domain.Location;
import org.smartregister.eusm.BaseUnitTest;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.domain.StructureDetail;
import org.smartregister.eusm.repository.AppStructureRepository;
import org.smartregister.repository.BaseRepository;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AppUtilsTest extends BaseUnitTest {

    @Test
    public void testGetHiddenFieldTemplateShouldReturnTemplate() {
        JSONObject resultJsonObject = AppUtils.getHiddenFieldTemplate("keyA", "valueA");
        assertNotNull(resultJsonObject);
        assertTrue(resultJsonObject.has(JsonFormConstants.TYPE));
        assertTrue(resultJsonObject.has(JsonFormConstants.KEY));
        assertTrue(resultJsonObject.has(JsonFormConstants.VALUE));
        assertEquals(resultJsonObject.optString(JsonFormConstants.TYPE), JsonFormConstants.HIDDEN);
    }

    @Test
    public void testUpdateLocationCoordinatesFromFormShouldInvokeLocationUpdate() throws JSONException {
        String strRecordGps = "{\"count\":\"1\",\"encounter_type\":\"record_gps\",\"form_version\":\"0.0.1\",\"entity_id\":\"\",\"metadata\":{\"start\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"start\",\"openmrs_entity_id\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"end\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"end\",\"openmrs_entity_id\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"today\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"encounter\",\"openmrs_entity_id\":\"encounter_date\"},\"deviceid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"deviceid\",\"openmrs_entity_id\":\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"subscriberid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"subscriberid\",\"openmrs_entity_id\":\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"simserial\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"simserial\",\"openmrs_entity_id\":\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"phonenumber\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"phonenumber\",\"openmrs_entity_id\":\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"encounter_location\":\"\"},\"step1\":{\"title\":\"Record GPS\",\"display_back_button\":true,\"fields\":[{\"key\":\"gps\",\"type\":\"gps\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"value\":\"-2.3434 45.3 4 32\"}]}}";
        JSONObject jsonObject = new JSONObject(strRecordGps);
        StructureDetail structureDetail = new StructureDetail();
        structureDetail.setStructureId(UUID.randomUUID().toString());

        AppStructureRepository structureRepository = mock(AppStructureRepository.class);

        Location location = new Location();
        location.setId(structureDetail.getStructureId());
        doReturn(location).when(structureRepository).getLocationById(eq(structureDetail.getStructureId()));
        doNothing().when(structureRepository).addOrUpdate(any(Location.class));

        ReflectionHelpers.setField(EusmApplication.getInstance(), "appStructureRepository", structureRepository);
        AppUtils.updateLocationCoordinatesFromForm(structureDetail, jsonObject);

        ArgumentCaptor<Location> locationArgumentCaptor = ArgumentCaptor.forClass(Location.class);
        verify(structureRepository).addOrUpdate(locationArgumentCaptor.capture());

        Location argLocation = locationArgumentCaptor.getValue();
        Geometry geometry = argLocation.getGeometry();
        assertEquals("45.3", geometry.getCoordinates().get(0).getAsString());
        assertEquals("-2.3434", geometry.getCoordinates().get(1).getAsString());
        assertEquals(BaseRepository.TYPE_Created, argLocation.getSyncStatus());

        ReflectionHelpers.setField(EusmApplication.getInstance(), "appStructureRepository", null);
    }
}