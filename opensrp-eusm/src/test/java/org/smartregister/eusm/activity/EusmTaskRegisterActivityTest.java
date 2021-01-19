package org.smartregister.eusm.activity;

import android.app.Activity;
import android.content.Intent;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.LooperMode;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.domain.Event;
import org.smartregister.eusm.BaseActivityUnitTest;
import org.smartregister.eusm.contract.TaskRegisterActivityContract;
import org.smartregister.eusm.domain.StructureDetail;
import org.smartregister.eusm.presenter.TaskRegisterActivityPresenter;
import org.smartregister.eusm.util.AppConstants;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static android.os.Looper.getMainLooper;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;
import static org.robolectric.annotation.LooperMode.Mode.PAUSED;

@LooperMode(PAUSED)
public class EusmTaskRegisterActivityTest extends BaseActivityUnitTest {

    private EusmTaskRegisterActivity eusmTaskRegisterActivity;

    private ActivityController<EusmTaskRegisterActivity> controller;

    private final String recordGpsString = "{\"count\":\"1\",\"encounter_type\":\"record_gps\",\"form_version\":\"0.0.1\",\"entity_id\":\"\",\"metadata\":{\"start\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"start\",\"openmrs_entity_id\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"end\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"end\",\"openmrs_entity_id\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"today\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"encounter\",\"openmrs_entity_id\":\"encounter_date\"},\"deviceid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"deviceid\",\"openmrs_entity_id\":\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"subscriberid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"subscriberid\",\"openmrs_entity_id\":\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"simserial\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"simserial\",\"openmrs_entity_id\":\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"phonenumber\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"phonenumber\",\"openmrs_entity_id\":\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"encounter_location\":\"\"},\"step1\":{\"title\":\"Record GPS\",\"display_back_button\":true,\"fields\":[{\"key\":\"gps\",\"type\":\"gps\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"}]}}";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Context.bindtypes = new ArrayList<>();
        Intent intent = new Intent();
        intent.putExtra(AppConstants.IntentData.STRUCTURE_DETAIL, new StructureDetail());
        controller = Robolectric.buildActivity(EusmTaskRegisterActivity.class, intent).create().start();
        eusmTaskRegisterActivity = spy(controller.get());
    }

    @Test
    public void testSaveRecordGpsFormShouldInvokeOnFormSaved() throws JSONException, InterruptedException {
        JSONObject jsonForm = new JSONObject(recordGpsString);
        TaskRegisterActivityPresenter taskRegisterActivityPresenter = spy(new TaskRegisterActivityPresenter(eusmTaskRegisterActivity));
        TaskRegisterActivityContract.View view1 = eusmTaskRegisterActivity;
        doReturn(view1).when(taskRegisterActivityPresenter).getView();
        ReflectionHelpers.setField(eusmTaskRegisterActivity, "presenter", taskRegisterActivityPresenter);
        Intent intent = new Intent();
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, recordGpsString);
        eusmTaskRegisterActivity.onActivityResultExtended(AppConstants.RequestCode.REQUEST_CODE_GET_JSON, RESULT_OK, intent);
        shadowOf(getMainLooper()).idle();
        Thread.sleep(ASYNC_TIMEOUT);
        verify(taskRegisterActivityPresenter)
                .onFormSaved(eq(jsonForm.optString(JsonFormConstants.ENCOUNTER_TYPE))
                        , eq(true), any(Event.class));
    }

    @Override
    protected Activity getActivity() {
        return eusmTaskRegisterActivity;
    }

    @Override
    protected ActivityController getActivityController() {
        return controller;
    }

    @After
    public void tearDown() {
        destroyController();
    }
}