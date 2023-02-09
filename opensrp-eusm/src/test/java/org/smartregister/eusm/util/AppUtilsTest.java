package org.smartregister.eusm.util;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.domain.Geometry;
import org.smartregister.domain.Location;
import org.smartregister.eusm.BaseUnitTest;
import org.smartregister.eusm.TestEusmApplication;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.domain.StructureDetail;
import org.smartregister.eusm.repository.AppStructureRepository;
import org.smartregister.repository.AllSettings;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.BaseRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.smartregister.eusm.util.AppConstants.PreferenceKey.DISABLE_SCHEDULED_JOBS;
import static org.smartregister.eusm.util.AppConstants.STRUCTURE_IDS;

import android.content.SharedPreferences;


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

    @Test
    public void testSaveStructureIdsShouldUpdateStructuresPreferences() {
        AllSharedPreferences allSharedPreferences = mock(AllSharedPreferences.class);
        ReflectionHelpers.setField(TestEusmApplication.getInstance().context(), "allSharedPreferences", allSharedPreferences);

        doReturn("").when(allSharedPreferences).getPreference(eq(STRUCTURE_IDS));
        List<String> structureIds = new ArrayList<>();
        structureIds.add(UUID.randomUUID().toString());
        AppUtils.saveStructureIds(structureIds);

        verify(allSharedPreferences).savePreference(eq(STRUCTURE_IDS), eq(structureIds.get(0)));
        ReflectionHelpers.setField(TestEusmApplication.getInstance().context(), "allSharedPreferences", null);
    }

    @Test
    public void testGetLocationLevelFromLocationHierarchyShouldReturnDistrictsForDistrictsTag() {
        String strLocationHierarchy = "{\"locationsHierarchy\":{\"map\":{\"03176924-6b3c-4b74-bccd-32afcceebabd\":{\"children\":{\"5876f357-cff5-4f03-ad97-fc79b7375bec\":{\"children\":{\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\":{\"children\":{\"024d01e6-01f4-4860-9780-846cdbec0836\":{\"id\":\"024d01e6-01f4-4860-9780-846cdbec0836\",\"label\":\"Manakopy\",\"node\":{\"attributes\":{\"geographicLevel\":3.0},\"locationId\":\"024d01e6-01f4-4860-9780-846cdbec0836\",\"name\":\"Manakopy\",\"parentLocation\":{\"locationId\":\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Commune\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\"},\"037f61e0-6af0-4530-8ce2-63124eba2a9b\":{\"id\":\"037f61e0-6af0-4530-8ce2-63124eba2a9b\",\"label\":\"Tanandava Sarisambo\",\"node\":{\"attributes\":{\"geographicLevel\":3.0},\"locationId\":\"037f61e0-6af0-4530-8ce2-63124eba2a9b\",\"name\":\"Tanandava Sarisambo\",\"parentLocation\":{\"locationId\":\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Commune\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\"},\"35079e66-d6ee-4cf2-92e7-06912ef5ce56\":{\"id\":\"35079e66-d6ee-4cf2-92e7-06912ef5ce56\",\"label\":\"Anivorano Mitsinjo\",\"node\":{\"attributes\":{\"geographicLevel\":3.0},\"locationId\":\"35079e66-d6ee-4cf2-92e7-06912ef5ce56\",\"name\":\"Anivorano Mitsinjo\",\"parentLocation\":{\"locationId\":\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Commune\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\"},\"3f9a4d6c-ec41-43cc-983e-87211280c482\":{\"id\":\"3f9a4d6c-ec41-43cc-983e-87211280c482\",\"label\":\"Antsakoamaro\",\"node\":{\"attributes\":{\"geographicLevel\":3.0},\"locationId\":\"3f9a4d6c-ec41-43cc-983e-87211280c482\",\"name\":\"Antsakoamaro\",\"parentLocation\":{\"locationId\":\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Commune\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\"},\"4342c258-a4f9-4f05-8356-42be448ffec7\":{\"id\":\"4342c258-a4f9-4f05-8356-42be448ffec7\",\"label\":\"Bekily-Centrale\",\"node\":{\"attributes\":{\"geographicLevel\":3.0},\"locationId\":\"4342c258-a4f9-4f05-8356-42be448ffec7\",\"name\":\"Bekily-Centrale\",\"parentLocation\":{\"locationId\":\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Commune\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\"},\"5d25d248-4468-4c96-a566-4a539c7928f8\":{\"id\":\"5d25d248-4468-4c96-a566-4a539c7928f8\",\"label\":\"Tanambao Tsirandrany\",\"node\":{\"attributes\":{\"geographicLevel\":3.0},\"locationId\":\"5d25d248-4468-4c96-a566-4a539c7928f8\",\"name\":\"Tanambao Tsirandrany\",\"parentLocation\":{\"locationId\":\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Commune\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\"},\"5e1ddbbb-a08c-4a05-a225-36788acf6baa\":{\"id\":\"5e1ddbbb-a08c-4a05-a225-36788acf6baa\",\"label\":\"Ambatosola\",\"node\":{\"attributes\":{\"geographicLevel\":3.0},\"locationId\":\"5e1ddbbb-a08c-4a05-a225-36788acf6baa\",\"name\":\"Ambatosola\",\"parentLocation\":{\"locationId\":\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Commune\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\"},\"5e2597af-1eb0-43db-a53c-f2e0aa3222a1\":{\"id\":\"5e2597af-1eb0-43db-a53c-f2e0aa3222a1\",\"label\":\"Beteza\",\"node\":{\"attributes\":{\"geographicLevel\":3.0},\"locationId\":\"5e2597af-1eb0-43db-a53c-f2e0aa3222a1\",\"name\":\"Beteza\",\"parentLocation\":{\"locationId\":\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Commune\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\"},\"624594ab-8385-4855-8d44-3d5279740ab3\":{\"id\":\"624594ab-8385-4855-8d44-3d5279740ab3\",\"label\":\"Tanandava\",\"node\":{\"attributes\":{\"geographicLevel\":3.0},\"locationId\":\"624594ab-8385-4855-8d44-3d5279740ab3\",\"name\":\"Tanandava\",\"parentLocation\":{\"locationId\":\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Commune\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\"},\"6b9d2aa3-1ff5-4a2e-ba40-aaf6237e4d65\":{\"id\":\"6b9d2aa3-1ff5-4a2e-ba40-aaf6237e4d65\",\"label\":\"Bevitiky\",\"node\":{\"attributes\":{\"geographicLevel\":3.0},\"locationId\":\"6b9d2aa3-1ff5-4a2e-ba40-aaf6237e4d65\",\"name\":\"Bevitiky\",\"parentLocation\":{\"locationId\":\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Commune\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\"},\"749e8f17-0875-423f-a1ec-953edfcd171c\":{\"id\":\"749e8f17-0875-423f-a1ec-953edfcd171c\",\"label\":\"Besakoa\",\"node\":{\"attributes\":{\"geographicLevel\":3.0},\"locationId\":\"749e8f17-0875-423f-a1ec-953edfcd171c\",\"name\":\"Besakoa\",\"parentLocation\":{\"locationId\":\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Commune\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\"},\"75b22baf-37a9-4d42-8fd6-7ca7912d2a4a\":{\"id\":\"75b22baf-37a9-4d42-8fd6-7ca7912d2a4a\",\"label\":\"Belindo-Mahasoa\",\"node\":{\"attributes\":{\"geographicLevel\":3.0},\"locationId\":\"75b22baf-37a9-4d42-8fd6-7ca7912d2a4a\",\"name\":\"Belindo-Mahasoa\",\"parentLocation\":{\"locationId\":\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Commune\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\"},\"8580a687-cfd7-4ca7-85df-12e585021277\":{\"id\":\"8580a687-cfd7-4ca7-85df-12e585021277\",\"label\":\"Bekitro\",\"node\":{\"attributes\":{\"geographicLevel\":3.0},\"locationId\":\"8580a687-cfd7-4ca7-85df-12e585021277\",\"name\":\"Bekitro\",\"parentLocation\":{\"locationId\":\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Commune\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\"},\"881e02a8-b2c9-4491-b303-27b008587a39\":{\"id\":\"881e02a8-b2c9-4491-b303-27b008587a39\",\"label\":\"Mikaikarivo Ambatomainty\",\"node\":{\"attributes\":{\"geographicLevel\":3.0},\"locationId\":\"881e02a8-b2c9-4491-b303-27b008587a39\",\"name\":\"Mikaikarivo Ambatomainty\",\"parentLocation\":{\"locationId\":\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Commune\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\"},\"9e96eb0a-60f8-4b35-aafe-fcf3cf3f773f\":{\"id\":\"9e96eb0a-60f8-4b35-aafe-fcf3cf3f773f\",\"label\":\"Tsikolaky\",\"node\":{\"attributes\":{\"geographicLevel\":3.0},\"locationId\":\"9e96eb0a-60f8-4b35-aafe-fcf3cf3f773f\",\"name\":\"Tsikolaky\",\"parentLocation\":{\"locationId\":\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Commune\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\"},\"baf46f85-04db-4d35-a9b5-237a12d20e88\":{\"id\":\"baf46f85-04db-4d35-a9b5-237a12d20e88\",\"label\":\"Vohimanga\",\"node\":{\"attributes\":{\"geographicLevel\":3.0},\"locationId\":\"baf46f85-04db-4d35-a9b5-237a12d20e88\",\"name\":\"Vohimanga\",\"parentLocation\":{\"locationId\":\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Commune\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\"},\"d0a79494-c31d-4009-86ca-c1decbc3d6d9\":{\"id\":\"d0a79494-c31d-4009-86ca-c1decbc3d6d9\",\"label\":\"Beraketa\",\"node\":{\"attributes\":{\"geographicLevel\":3.0},\"locationId\":\"d0a79494-c31d-4009-86ca-c1decbc3d6d9\",\"name\":\"Beraketa\",\"parentLocation\":{\"locationId\":\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Commune\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\"},\"dfef62fd-14cb-41e0-9bbf-321d9a8b9311\":{\"id\":\"dfef62fd-14cb-41e0-9bbf-321d9a8b9311\",\"label\":\"Anja Nord\",\"node\":{\"attributes\":{\"geographicLevel\":3.0},\"locationId\":\"dfef62fd-14cb-41e0-9bbf-321d9a8b9311\",\"name\":\"Anja Nord\",\"parentLocation\":{\"locationId\":\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Commune\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\"},\"e9beb16a-fb0b-4aa2-97b5-9668f323af7b\":{\"id\":\"e9beb16a-fb0b-4aa2-97b5-9668f323af7b\",\"label\":\"Ambahita\",\"node\":{\"attributes\":{\"geographicLevel\":3.0},\"locationId\":\"e9beb16a-fb0b-4aa2-97b5-9668f323af7b\",\"name\":\"Ambahita\",\"parentLocation\":{\"locationId\":\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Commune\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\"},\"eff7b5fc-4224-4400-9165-0f3f7733a4a1\":{\"id\":\"eff7b5fc-4224-4400-9165-0f3f7733a4a1\",\"label\":\"Maroviro\",\"node\":{\"attributes\":{\"geographicLevel\":3.0},\"locationId\":\"eff7b5fc-4224-4400-9165-0f3f7733a4a1\",\"name\":\"Maroviro\",\"parentLocation\":{\"locationId\":\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Commune\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\"},\"fff386bb-d84f-435f-9b88-e25027d06662\":{\"id\":\"fff386bb-d84f-435f-9b88-e25027d06662\",\"label\":\"Ankaranabo Nord\",\"node\":{\"attributes\":{\"geographicLevel\":3.0},\"locationId\":\"fff386bb-d84f-435f-9b88-e25027d06662\",\"name\":\"Ankaranabo Nord\",\"parentLocation\":{\"locationId\":\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Commune\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\"}},\"id\":\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\",\"label\":\"Bekily\",\"node\":{\"attributes\":{\"geographicLevel\":2.0},\"locationId\":\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\",\"name\":\"Bekily\",\"parentLocation\":{\"locationId\":\"5876f357-cff5-4f03-ad97-fc79b7375bec\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"District\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"5876f357-cff5-4f03-ad97-fc79b7375bec\"}},\"id\":\"5876f357-cff5-4f03-ad97-fc79b7375bec\",\"label\":\"ANDROY\",\"node\":{\"attributes\":{\"geographicLevel\":1.0},\"locationId\":\"5876f357-cff5-4f03-ad97-fc79b7375bec\",\"name\":\"ANDROY\",\"parentLocation\":{\"locationId\":\"03176924-6b3c-4b74-bccd-32afcceebabd\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Region\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"03176924-6b3c-4b74-bccd-32afcceebabd\"},\"d6fb17c9-ef9d-4e82-877c-a4a50a8f64cb\":{\"children\":{\"a9d70fa1-ec3c-49f1-8e8b-6ae781b395e7\":{\"children\":{\"87073296-0c8f-4c3d-be03-657969c6517a\":{\"id\":\"87073296-0c8f-4c3d-be03-657969c6517a\",\"label\":\"3e Arrondissement\",\"node\":{\"attributes\":{\"geographicLevel\":3.0},\"locationId\":\"87073296-0c8f-4c3d-be03-657969c6517a\",\"name\":\"3e Arrondissement\",\"parentLocation\":{\"locationId\":\"a9d70fa1-ec3c-49f1-8e8b-6ae781b395e7\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Commune\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"a9d70fa1-ec3c-49f1-8e8b-6ae781b395e7\"},\"8f127dd1-8e58-4d53-b273-bf8945e3e43a\":{\"id\":\"8f127dd1-8e58-4d53-b273-bf8945e3e43a\",\"label\":\"5e Arrondissement\",\"node\":{\"attributes\":{\"geographicLevel\":3.0},\"locationId\":\"8f127dd1-8e58-4d53-b273-bf8945e3e43a\",\"name\":\"5e Arrondissement\",\"parentLocation\":{\"locationId\":\"a9d70fa1-ec3c-49f1-8e8b-6ae781b395e7\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Commune\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"a9d70fa1-ec3c-49f1-8e8b-6ae781b395e7\"},\"a5e56324-dfbe-4373-827e-772232cfc7e0\":{\"id\":\"a5e56324-dfbe-4373-827e-772232cfc7e0\",\"label\":\"1er Arrondissement\",\"node\":{\"attributes\":{\"geographicLevel\":3.0},\"locationId\":\"a5e56324-dfbe-4373-827e-772232cfc7e0\",\"name\":\"1er Arrondissement\",\"parentLocation\":{\"locationId\":\"a9d70fa1-ec3c-49f1-8e8b-6ae781b395e7\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Commune\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"a9d70fa1-ec3c-49f1-8e8b-6ae781b395e7\"},\"cfcdfdc5-3a52-4528-ba1f-753d8eed8aea\":{\"id\":\"cfcdfdc5-3a52-4528-ba1f-753d8eed8aea\",\"label\":\"2e Arrondissement\",\"node\":{\"attributes\":{\"geographicLevel\":3.0},\"locationId\":\"cfcdfdc5-3a52-4528-ba1f-753d8eed8aea\",\"name\":\"2e Arrondissement\",\"parentLocation\":{\"locationId\":\"a9d70fa1-ec3c-49f1-8e8b-6ae781b395e7\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Commune\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"a9d70fa1-ec3c-49f1-8e8b-6ae781b395e7\"},\"e70844ea-2707-4622-8105-8046abf81235\":{\"id\":\"e70844ea-2707-4622-8105-8046abf81235\",\"label\":\"4e Arrondissement\",\"node\":{\"attributes\":{\"geographicLevel\":3.0},\"locationId\":\"e70844ea-2707-4622-8105-8046abf81235\",\"name\":\"4e Arrondissement\",\"parentLocation\":{\"locationId\":\"a9d70fa1-ec3c-49f1-8e8b-6ae781b395e7\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Commune\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"a9d70fa1-ec3c-49f1-8e8b-6ae781b395e7\"},\"fd2f9598-c615-4599-b203-c706e44971a1\":{\"id\":\"fd2f9598-c615-4599-b203-c706e44971a1\",\"label\":\"6e Arrondissement\",\"node\":{\"attributes\":{\"geographicLevel\":3.0},\"locationId\":\"fd2f9598-c615-4599-b203-c706e44971a1\",\"name\":\"6e Arrondissement\",\"parentLocation\":{\"locationId\":\"a9d70fa1-ec3c-49f1-8e8b-6ae781b395e7\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Commune\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"a9d70fa1-ec3c-49f1-8e8b-6ae781b395e7\"}},\"id\":\"a9d70fa1-ec3c-49f1-8e8b-6ae781b395e7\",\"label\":\"Antananarivo Renivohitra\",\"node\":{\"attributes\":{\"geographicLevel\":2.0},\"locationId\":\"a9d70fa1-ec3c-49f1-8e8b-6ae781b395e7\",\"name\":\"Antananarivo Renivohitra\",\"parentLocation\":{\"locationId\":\"d6fb17c9-ef9d-4e82-877c-a4a50a8f64cb\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"District\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"d6fb17c9-ef9d-4e82-877c-a4a50a8f64cb\"}},\"id\":\"d6fb17c9-ef9d-4e82-877c-a4a50a8f64cb\",\"label\":\"ANALAMANGA\",\"node\":{\"attributes\":{\"geographicLevel\":1.0},\"locationId\":\"d6fb17c9-ef9d-4e82-877c-a4a50a8f64cb\",\"name\":\"ANALAMANGA\",\"parentLocation\":{\"locationId\":\"03176924-6b3c-4b74-bccd-32afcceebabd\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Region\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"03176924-6b3c-4b74-bccd-32afcceebabd\"}},\"id\":\"03176924-6b3c-4b74-bccd-32afcceebabd\",\"label\":\"Madagascar\",\"node\":{\"attributes\":{\"geographicLevel\":0.0},\"locationId\":\"03176924-6b3c-4b74-bccd-32afcceebabd\",\"name\":\"Madagascar\",\"tags\":[\"Country\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"}}},\"parentChildren\":{\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\":[\"024d01e6-01f4-4860-9780-846cdbec0836\",\"037f61e0-6af0-4530-8ce2-63124eba2a9b\",\"35079e66-d6ee-4cf2-92e7-06912ef5ce56\",\"3f9a4d6c-ec41-43cc-983e-87211280c482\",\"4342c258-a4f9-4f05-8356-42be448ffec7\",\"5d25d248-4468-4c96-a566-4a539c7928f8\",\"5e1ddbbb-a08c-4a05-a225-36788acf6baa\",\"5e2597af-1eb0-43db-a53c-f2e0aa3222a1\",\"624594ab-8385-4855-8d44-3d5279740ab3\",\"6b9d2aa3-1ff5-4a2e-ba40-aaf6237e4d65\",\"749e8f17-0875-423f-a1ec-953edfcd171c\",\"75b22baf-37a9-4d42-8fd6-7ca7912d2a4a\",\"8580a687-cfd7-4ca7-85df-12e585021277\",\"881e02a8-b2c9-4491-b303-27b008587a39\",\"9e96eb0a-60f8-4b35-aafe-fcf3cf3f773f\",\"baf46f85-04db-4d35-a9b5-237a12d20e88\",\"d0a79494-c31d-4009-86ca-c1decbc3d6d9\",\"dfef62fd-14cb-41e0-9bbf-321d9a8b9311\",\"e9beb16a-fb0b-4aa2-97b5-9668f323af7b\",\"eff7b5fc-4224-4400-9165-0f3f7733a4a1\",\"fff386bb-d84f-435f-9b88-e25027d06662\"],\"5876f357-cff5-4f03-ad97-fc79b7375bec\":[\"36be60f1-dde6-4c5a-80ae-08df201ff1c5\"],\"a9d70fa1-ec3c-49f1-8e8b-6ae781b395e7\":[\"87073296-0c8f-4c3d-be03-657969c6517a\",\"8f127dd1-8e58-4d53-b273-bf8945e3e43a\",\"a5e56324-dfbe-4373-827e-772232cfc7e0\",\"cfcdfdc5-3a52-4528-ba1f-753d8eed8aea\",\"e70844ea-2707-4622-8105-8046abf81235\",\"fd2f9598-c615-4599-b203-c706e44971a1\"],\"d6fb17c9-ef9d-4e82-877c-a4a50a8f64cb\":[\"a9d70fa1-ec3c-49f1-8e8b-6ae781b395e7\"],\"03176924-6b3c-4b74-bccd-32afcceebabd\":[\"5876f357-cff5-4f03-ad97-fc79b7375bec\",\"d6fb17c9-ef9d-4e82-877c-a4a50a8f64cb\"]}}}";
        AllSettings allSettings = mock(AllSettings.class);
        doReturn(strLocationHierarchy).when(allSettings).fetchANMLocation();
        ReflectionHelpers.setField(TestEusmApplication.getInstance().context(), "allSettings", allSettings);
        Set<String> districts = AppUtils.getLocationLevelFromLocationHierarchy(AppConstants.LocationLevels.DISTRICT_TAG);
        assertFalse(districts.isEmpty());
        assertEquals(2, districts.size());
        assertTrue(districts.contains("a9d70fa1-ec3c-49f1-8e8b-6ae781b395e7"));
        assertTrue(districts.contains("36be60f1-dde6-4c5a-80ae-08df201ff1c5"));
        ReflectionHelpers.setField(TestEusmApplication.getInstance().context(), "allSettings", null);
    }

    @Test
    public void testGetRegionsForDistrictsShouldReturnRegionsForTheDistrictsList() throws Exception{
        String locationHierarchy = "[{\"key\":\"Madagascar\",\"level\":\"\",\"name\":\"Madagascar\",\"nodes\":[{\"key\":\"ANDROY\",\"level\":\"\",\"name\":\"ANDROY\",\"nodes\":[{\"key\":\"Ambovombe\",\"level\":\"\",\"name\":\"Ambovombe\",\"nodes\":[]},{\"key\":\"Bekily\",\"level\":\"\",\"name\":\"Bekily\",\"nodes\":[]},{\"key\":\"Beloha\",\"level\":\"\",\"name\":\"Beloha\",\"nodes\":[]},{\"key\":\"Tsihombe\",\"level\":\"\",\"name\":\"Tsihombe\",\"nodes\":[]}]},{\"key\":\"ATSIMO ATSINANANA\",\"level\":\"\",\"name\":\"ATSIMO ATSINANANA\",\"nodes\":[{\"key\":\"Befotaka\",\"level\":\"\",\"name\":\"Befotaka\",\"nodes\":[]},{\"key\":\"Farafangana\",\"level\":\"\",\"name\":\"Farafangana\",\"nodes\":[]},{\"key\":\"Midongy Atsimo\",\"level\":\"\",\"name\":\"Midongy Atsimo\",\"nodes\":[]},{\"key\":\"Vangaindrano\",\"level\":\"\",\"name\":\"Vangaindrano\",\"nodes\":[]},{\"key\":\"Vondrozo\",\"level\":\"\",\"name\":\"Vondrozo\",\"nodes\":[]}]},{\"key\":\"ITASY\",\"level\":\"\",\"name\":\"ITASY\",\"nodes\":[{\"key\":\"Arivonimamo\",\"level\":\"\",\"name\":\"Arivonimamo\",\"nodes\":[]},{\"key\":\"Miarinarivo\",\"level\":\"\",\"name\":\"Miarinarivo\",\"nodes\":[]},{\"key\":\"Soavinandriana\",\"level\":\"\",\"name\":\"Soavinandriana\",\"nodes\":[]}]}]}]";
        JSONArray arrayLocationHierarchy = new JSONArray(locationHierarchy);

        String districts = "Ambovombe,Midongy Atsimo,Vondrozo,Arivonimamo,Soavinandriana,Vangaindrano,Befotaka,Bekily,Miarinarivo,Tsihombe,Beloha,Farafangana";
        HashSet<String> operationalAreas = new HashSet<>(Arrays.asList(districts.split(",")));

        ArrayList<String> regions = AppUtils.getRegionsForDistricts(arrayLocationHierarchy, operationalAreas, false);
        assertEquals(3, regions.size());
        assertEquals(Arrays.asList("ANDROY,ATSIMO ATSINANANA,ITASY".split(",")), regions);
    }

    @Test
    public void testHasDisabledScheduledJobs() {
        AllSharedPreferences allSharedPreferences = mock(AllSharedPreferences.class);
        SharedPreferences sharedPreferences = mock(SharedPreferences.class);
        ReflectionHelpers.setField(TestEusmApplication.getInstance().context(), "allSharedPreferences", allSharedPreferences);

        doReturn(sharedPreferences).when(allSharedPreferences).getPreferences();
        doReturn(true).when(sharedPreferences).getBoolean(eq(DISABLE_SCHEDULED_JOBS), eq(false));
        boolean hasUpgraded = AppUtils.hasDisabledScheduledJobs();

        assertTrue(hasUpgraded);
    }

    @Test
    public void testSaveHasDisabledScheduledJobs() {
        AllSharedPreferences allSharedPreferences = mock(AllSharedPreferences.class);
        SharedPreferences sharedPreferences = mock(SharedPreferences.class);
        SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);
        ReflectionHelpers.setField(TestEusmApplication.getInstance().context(), "allSharedPreferences", allSharedPreferences);

        doReturn(sharedPreferences).when(allSharedPreferences).getPreferences();
        doReturn(editor).when(sharedPreferences).edit();
        doReturn(editor).when(editor).putBoolean(anyString(), anyBoolean());
        doNothing().when(editor).apply();
        doReturn(true).when(sharedPreferences).getBoolean(eq(DISABLE_SCHEDULED_JOBS), eq(false));
        AppUtils.saveHasDisabledScheduledJobs();

        verify(editor, times(1)).apply();
    }


    @After
    public void tearDown() {
        ReflectionHelpers.setField(TestEusmApplication.getInstance().context(), "allSharedPreferences", null);
    }

}