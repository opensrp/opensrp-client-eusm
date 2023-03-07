package org.smartregister.eusm.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.testing.FragmentScenario;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.eusm.BaseUnitTest;
import org.smartregister.eusm.R;
import org.smartregister.eusm.activity.EusmTaskRegisterActivity;
import org.smartregister.eusm.contract.TaskRegisterFragmentContract;
import org.smartregister.eusm.domain.StructureDetail;
import org.smartregister.eusm.domain.TaskDetail;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;


public class EusmTasksRegisterFragmentTest extends BaseUnitTest {

    @Mock
    private SyncStatusBroadcastReceiver syncStatusBroadcastReceiver;

    private FragmentScenario<EusmTasksRegisterFragment> fragmentScenario;

    @Mock
    private TaskRegisterFragmentContract.Presenter presenter;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionHelpers.setStaticField(SyncStatusBroadcastReceiver.class, "singleton", syncStatusBroadcastReceiver);
        Bundle bundle = new Bundle();
        bundle.putSerializable(AppConstants.IntentData.STRUCTURE_DETAIL, new StructureDetail());
        fragmentScenario = FragmentScenario.launch(EusmTasksRegisterFragment.class, bundle);
    }

    @Test
    public void testOnUndoViewClickedShouldOpenUndoDialog() {
        TaskDetail taskDetail = new TaskDetail();
        taskDetail.setChecked(true);
        View view = new View(RuntimeEnvironment.application);
        view.setId(R.id.task_register_row);
        view.setTag(R.id.task_detail, taskDetail);
        fragmentScenario.onFragment(fragment -> {
            EusmTasksRegisterFragment fragmentSpy = spy(fragment);
            EusmTaskRegisterActivity activity = Robolectric.buildActivity(EusmTaskRegisterActivity.class).get();
            doReturn(activity).when(fragmentSpy).getActivity();
            fragmentSpy.onClick(view);
            verify(fragmentSpy).openUndoDialog(eq(taskDetail));
        });
    }

    @Test
    public void testOnTaskRegisterRowClickShouldStartServicePointCheckForm() {
        TaskDetail taskDetail = new TaskDetail();
        taskDetail.setNonProductTask(true);
        taskDetail.setEntityName(AppConstants.NonProductTasks.SERVICE_POINT_CHECK);
        View view = new View(RuntimeEnvironment.application);
        view.setId(R.id.task_register_row);
        view.setTag(R.id.task_detail, taskDetail);

        fragmentScenario.onFragment(fragment -> {
            EusmTasksRegisterFragment fragmentSpy = spy(fragment);
            ReflectionHelpers.setField(fragmentSpy, "presenter", presenter);
            EusmTaskRegisterActivity activity = Robolectric.buildActivity(EusmTaskRegisterActivity.class).get();
            doReturn(activity).when(fragmentSpy).getActivity();
            fragmentSpy.onClick(view);
            verify(presenter).startForm(any(StructureDetail.class), any(TaskDetail.class), eq(AppConstants.JsonForm.SERVICE_POINT_CHECK_FORM));
        });
    }

    @Test
    public void testOnTaskRegisterRowClickShouldStartRecordGpsForm() {
        TaskDetail taskDetail = new TaskDetail();
        taskDetail.setNonProductTask(true);
        taskDetail.setEntityName(AppConstants.NonProductTasks.RECORD_GPS);
        View view = new View(RuntimeEnvironment.application);
        view.setId(R.id.task_register_row);
        view.setTag(R.id.task_detail, taskDetail);

        fragmentScenario.onFragment(fragment -> {
            EusmTasksRegisterFragment fragmentSpy = spy(fragment);
            ReflectionHelpers.setField(fragmentSpy, "presenter", presenter);
            EusmTaskRegisterActivity activity = Robolectric.buildActivity(EusmTaskRegisterActivity.class).get();
            doReturn(activity).when(fragmentSpy).getActivity();
            fragmentSpy.onViewClicked(view);
            verify(presenter).startForm(any(StructureDetail.class), any(TaskDetail.class), eq(AppConstants.JsonForm.RECORD_GPS_FORM));
        });
    }

    @Test
    public void testOnTaskRegisterRowClickShouldStartFixProblemForm() {
        TaskDetail taskDetail = new TaskDetail();
        taskDetail.setTaskCode(AppConstants.EncounterType.FIX_PROBLEM);
        View view = new View(RuntimeEnvironment.application);
        view.setId(R.id.task_register_row);
        view.setTag(R.id.task_detail, taskDetail);

        fragmentScenario.onFragment(fragment -> {
            EusmTasksRegisterFragment fragmentSpy = spy(fragment);
            ReflectionHelpers.setField(fragmentSpy, "presenter", presenter);
            EusmTaskRegisterActivity activity = Robolectric.buildActivity(EusmTaskRegisterActivity.class).get();
            doReturn(activity).when(fragmentSpy).getActivity();
            fragmentSpy.onViewClicked(view);
            verify(presenter).startForm(any(StructureDetail.class), any(TaskDetail.class), eq(AppConstants.JsonForm.FIX_PROBLEM_FORM));
        });
    }

    @Test
    public void testOnTaskRegisterRowClickShouldStartProductInfoActivity() {
        TaskDetail taskDetail = new TaskDetail();
        View view = new View(RuntimeEnvironment.application);
        view.setId(R.id.task_register_row);
        view.setTag(R.id.task_detail, taskDetail);

        fragmentScenario.onFragment(fragment -> {
            EusmTasksRegisterFragment fragmentSpy = spy(fragment);
            ReflectionHelpers.setField(fragmentSpy, "presenter", presenter);
            EusmTaskRegisterActivity activity = spy(Robolectric.buildActivity(EusmTaskRegisterActivity.class).get());
            doReturn(activity).when(fragmentSpy).getActivity();
            fragmentSpy.onViewClicked(view);
            verify(activity).startActivity(any(Intent.class));
        });
    }


    @Test
    public void testStartFormActivityShouldInvokeStartActivityForResult() {
        fragmentScenario.onFragment(fragment -> {
            EusmTasksRegisterFragment fragmentSpy = spy(fragment);
            ReflectionHelpers.setField(fragmentSpy, "presenter", presenter);
            EusmTaskRegisterActivity activity = spy(Robolectric.buildActivity(EusmTaskRegisterActivity.class).get());
            doReturn(activity).when(fragmentSpy).getActivity();
            try {
                JSONObject jsonObject = new JSONObject("{\"count\":\"1\",\"encounter_type\":\"fix_problem\",\"form_version\":\"0.0.1\",\"entity_id\":\"\",\"metadata\":{\"start\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"start\",\"openmrs_entity_id\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"end\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"end\",\"openmrs_entity_id\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"today\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"encounter\",\"openmrs_entity_id\":\"encounter_date\"},\"deviceid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"deviceid\",\"openmrs_entity_id\":\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"subscriberid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"subscriberid\",\"openmrs_entity_id\":\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"simserial\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"simserial\",\"openmrs_entity_id\":\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"phonenumber\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"phonenumber\",\"openmrs_entity_id\":\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"encounter_location\":\"\"},\"step1\":{\"title\":\"Fix problem\",\"display_back_button\":true,\"fields\":[{\"key\":\"problem_fixed\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"native_radio\",\"label\":\"Was the problem fixed?\",\"options\":[{\"key\":\"yes\",\"text\":\"Yes\"},{\"key\":\"no\",\"text\":\"No\"}],\"v_required\":{\"value\":true,\"err\":\"Field requred\"}}]}}");
                fragmentSpy.startFormActivity(jsonObject);
                verify(activity).startActivityForResult(any(Intent.class), eq(AppConstants.RequestCode.REQUEST_CODE_GET_JSON));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void testOnTaskRegisterRowClickShouldStartFixProblemFormForConsultBeneficiaries() {
        TaskDetail taskDetail = new TaskDetail();
        taskDetail.setTaskCode(AppConstants.TaskCode.FIX_PROBLEM_CONSULT_BENEFICIARIES);
        taskDetail.setNonProductTask(true);
        taskDetail.setEntityName(AppConstants.TaskCode.FIX_PROBLEM_CONSULT_BENEFICIARIES);
        View view = new View(RuntimeEnvironment.application);
        view.setId(R.id.task_register_row);
        view.setTag(R.id.task_detail, taskDetail);

        Assert.assertNotNull(fragmentScenario);
        fragmentScenario.onFragment(fragment -> {
            EusmTasksRegisterFragment fragmentSpy = spy(fragment);
            ReflectionHelpers.setField(fragmentSpy, "presenter", presenter);
            EusmTaskRegisterActivity activity = Robolectric.buildActivity(EusmTaskRegisterActivity.class).get();
            doReturn(activity).when(fragmentSpy).getActivity();
            fragmentSpy.onViewClicked(view);
            verify(presenter).startForm(any(StructureDetail.class), eq(taskDetail), eq(AppConstants.JsonForm.FIX_PROBLEM_FORM));
        });
    }
}