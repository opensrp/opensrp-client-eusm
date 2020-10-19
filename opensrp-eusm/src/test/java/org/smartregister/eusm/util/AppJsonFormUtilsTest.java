package org.smartregister.eusm.util;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.Geometry;
import org.smartregister.domain.Location;
import org.smartregister.domain.LocationProperty;
import org.smartregister.eusm.BaseUnitTest;
import org.smartregister.eusm.model.StructureTaskDetails;
import org.smartregister.eusm.util.AppConstants.JsonForm;
import org.smartregister.util.AssetHandler;
import org.smartregister.util.JsonFormUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.vijay.jsonwizard.constants.JsonFormConstants.TEXT;
import static org.junit.Assert.assertEquals;
import static org.smartregister.eusm.util.AppConstants.STRUCTURE;
import static org.smartregister.eusm.util.AppConstants.TASK_RESET_EVENT;

public class AppJsonFormUtilsTest extends BaseUnitTest {

    private final Context context = RuntimeEnvironment.application;
    private AppJsonFormUtils appJsonFormUtils;

    @Before
    public void setUp() {
        appJsonFormUtils = new AppJsonFormUtils();
    }

    @Test
    public void testPopulateField() throws JSONException {
        JSONObject form = new JSONObject(AssetHandler.readFileFromAssetsFolder(JsonForm.ADD_STRUCTURE_FORM, context));
        assertEquals("", JsonFormUtils.getFieldJSONObject(JsonFormUtils.fields(form), JsonForm.SELECTED_OPERATIONAL_AREA_NAME).get(TEXT).toString());

        appJsonFormUtils.populateField(form, AppConstants.JsonForm.SELECTED_OPERATIONAL_AREA_NAME, "TLV1", TEXT);
        assertEquals("TLV1", JsonFormUtils.getFieldJSONObject(JsonFormUtils.fields(form), JsonForm.SELECTED_OPERATIONAL_AREA_NAME).get(TEXT));
    }

    @Test
    public void testGetFormJSON() throws JSONException {
        StructureTaskDetails task = new StructureTaskDetails("d12202fb-d347-4d7a-8859-fb370304c34c");
        task.setBusinessStatus("Not Visited");
        task.setTaskEntity("c72310fd-9c60-403e-a6f8-e38bf5d6359b");
        task.setStructureId("e5246812-f66c-41d9-8739-464f913b112d");
        task.setTaskCode("Blood Screening");
        task.setTaskStatus("READY");
        task.setTaskName("Yogi  Feri, 9");
        task.setTaskAction("Record\n" + "Screening");

        Location structure = new Location();
        structure.setId("e5246812-f66c-41d9-8739-464f913b112d");
        structure.setServerVersion(1569490867604L);
        structure.setSyncStatus("Synced");
        structure.setType("Feature");
        structure.setJurisdiction(false);

        Geometry g = new Geometry();
        g.setType(Geometry.GeometryType.MULTI_POLYGON);
        structure.setGeometry(g);

        LocationProperty lp = new LocationProperty();
        lp.setParentId("6fffaf7f-f16f-4713-a1ac-0cf6e2fe7f2a");
        HashMap<String, String> hm = new HashMap<>();
        hm.put("houseNumber", "6533");
        lp.setCustomProperties(hm);
        lp.setStatus(LocationProperty.PropertyStatus.ACTIVE);
        structure.setProperties(lp);

        JSONObject jsonObject = appJsonFormUtils.getFormJSON(context, JsonForm.BLOOD_SCREENING_FORM, task, structure);
        assertEquals(jsonObject.getJSONObject("details").getString(AppConstants.Properties.FORM_VERSION), "0.0.1");
    }

//    @Test
//    public void testGetFormJsonFromFeature() throws JSONException {
//        Feature structure = TestingUtils.getStructure();
//        String expectedTaskIdentifier = getPropertyValue(structure, AppConstants.Properties.TASK_IDENTIFIER);
//        JSONObject jsonObject = appJsonFormUtils.getFormJSON(context, JsonForm.SPRAY_FORM, structure, AppConstants.BusinessStatus.SPRAYED, "John");
//        assertNotNull(jsonObject);
//        assertEquals(structure.id(), jsonObject.getJSONObject("details").getString(AppConstants.Properties.LOCATION_ID));
//        assertEquals(expectedTaskIdentifier, jsonObject.getJSONObject("details").getString(AppConstants.Properties.TASK_IDENTIFIER));
//    }

    @Test
    public void testCreateEvent() {

        String baseEntityId = UUID.randomUUID().toString();
        String locationId = UUID.randomUUID().toString();
        Map<String, String> details = new HashMap<>();
        String eventType = TASK_RESET_EVENT;
        String entityType = STRUCTURE;

        Event actualEvent = AppJsonFormUtils.createTaskEvent(baseEntityId, locationId, details, eventType, entityType);

        assertEquals(baseEntityId, actualEvent.getBaseEntityId());
        assertEquals(locationId, actualEvent.getLocationId());
        assertEquals(eventType, actualEvent.getEventType());
        assertEquals(entityType, actualEvent.getEntityType());
        assertEquals(baseEntityId, actualEvent.getBaseEntityId());
    }

}

