package org.smartregister.eusm.activity;

import android.content.Intent;
import android.view.View;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.LooperMode;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.eusm.BaseUnitTest;
import org.smartregister.eusm.R;
import org.smartregister.eusm.contract.ProductInfoActivityContract;
import org.smartregister.eusm.domain.StructureDetail;
import org.smartregister.eusm.domain.TaskDetail;
import org.smartregister.eusm.presenter.ProductInfoActivityPresenter;
import org.smartregister.eusm.util.AppConstants;

import static android.app.Activity.RESULT_OK;
import static android.os.Looper.getMainLooper;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;
import static org.robolectric.annotation.LooperMode.Mode.PAUSED;

@LooperMode(PAUSED)
public class ProductInfoActivityTest extends BaseUnitTest {

    private ProductInfoActivity activity;

    private String flagProblemForm = "{\"count\":\"2\",\"encounter_type\":\"flag_problem\",\"form_version\":\"0.0.1\",\"entity_id\":\"\",\"metadata\":{\"start\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"start\",\"openmrs_entity_id\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"end\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"end\",\"openmrs_entity_id\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"today\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"encounter\",\"openmrs_entity_id\":\"encounter_date\"},\"deviceid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"deviceid\",\"openmrs_entity_id\":\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"subscriberid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"subscriberid\",\"openmrs_entity_id\":\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"simserial\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"simserial\",\"openmrs_entity_id\":\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"phonenumber\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"phonenumber\",\"openmrs_entity_id\":\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"encounter_location\":\"\"},\"step1\":{\"title\":\"Flag Problem\",\"next\":\"step2\",\"fields\":[{\"key\":\"flag_problem\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"check_box\",\"label\":\"Flag problem(s)\",\"label_text_style\":\"bold\",\"exclusive\":[\"not_there\"],\"options\":[{\"key\":\"not_there\",\"text\":\"Product is not there\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"not_good\",\"text\":\"Product is not in good condition\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"misuse\",\"text\":\"Product is not being used appropriately\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"}],\"v_required\":{\"value\":true,\"err\":\"Field required\"}}]},\"step2\":{\"title\":\"Reason\",\"fields\":[{\"key\":\"product_picture\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"choose_image\",\"uploadButtonText\":\"Take a photo of the product\",\"relevance\":{\"step1:flag_problem\":{\"ex-checkbox\":[{\"or\":[\"not_good\",\"misuse\"]}]}}},{\"key\":\"not_good\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"native_radio\",\"label\":\"What is wrong with the product?\",\"label_text_style\":\"bold\",\"options\":[{\"key\":\"worn_broken\",\"text\":\"Worn, damaged, or broken\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"expired\",\"text\":\"Expired\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"parts_missing\",\"text\":\"Parts missing\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"other\",\"text\":\"Other (specify)\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"}],\"v_required\":{\"value\":true,\"err\":\"Field required\"},\"relevance\":{\"step1:flag_problem\":{\"ex-checkbox\":[{\"or\":[\"not_good\"]}]}}},{\"key\":\"not_good_specify_other\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"edit_text\",\"hint\":\"Specify other:\",\"relevance\":{\"step2:not_good\":{\"type\":\"string\",\"ex\":\"equalTo(., \\\"other\\\")\"}}},{\"key\":\"not_there\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"native_radio\",\"label\":\"What happened to the product?\",\"label_text_style\":\"bold\",\"options\":[{\"key\":\"lost_stolen\",\"text\":\"Lost / Stolen\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"never_received\",\"text\":\"Never received\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"discarded\",\"text\":\"Discarded (e.g. damaged on delivery, expired)\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"stock_out\",\"text\":\"Stock out\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"other\",\"text\":\"Other (specify)\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"}],\"v_required\":{\"value\":true,\"err\":\"Field required\"},\"relevance\":{\"step1:flag_problem\":{\"ex-checkbox\":[{\"or\":[\"not_there\"]}]}}},{\"key\":\"not_there_specify_other\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"edit_text\",\"hint\":\"Specify other:\",\"relevance\":{\"step2:not_there\":{\"type\":\"string\",\"ex\":\"equalTo(., \\\"other\\\")\"}}},{\"key\":\"misuse\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"native_radio\",\"label\":\"How is the product not being used appropriately?\",\"label_text_style\":\"bold\",\"options\":[{\"key\":\"lacks_skills\",\"text\":\"End user lacks necessary skills or knowledge\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"fraud_use\",\"text\":\"End user is using the product for unauthorised / fraudulent purposes\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"}],\"v_required\":{\"value\":true,\"err\":\"Field required\"},\"relevance\":{\"step1:flag_problem\":{\"ex-checkbox\":[{\"or\":[\"misuse\"]}]}}},{\"key\":\"issue_details\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"edit_text\",\"hint\":\"Provide some details on the specific issue, including any advice you were able to give.\"}]}}";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        TaskDetail taskDetail = new TaskDetail();
        StructureDetail structureDetail = new StructureDetail();
        Intent intent = new Intent();
        intent.putExtra(AppConstants.IntentData.TASK_DETAIL, taskDetail);
        intent.putExtra(AppConstants.IntentData.STRUCTURE_DETAIL, structureDetail);
        activity = Robolectric
                .buildActivity(ProductInfoActivity.class, intent)
                .create().start().get();

        activity = spy(activity);
    }

    @Test
    public void testClickOnLayoutFlagProblemClickShouldOpenForm() throws InterruptedException {
        View view = new View(RuntimeEnvironment.application);
        view.setId(R.id.layout_flag_problem);
        ProductInfoActivityPresenter presenter = spy(new ProductInfoActivityPresenter(activity));
        ProductInfoActivityContract.View view1 = activity;
        doReturn(view1).when(presenter).getView();
        ReflectionHelpers.setField(activity, "presenter", presenter);
        activity.onClick(view);
        shadowOf(getMainLooper()).idle();
        Thread.sleep(2000);
        verify(activity).startFlagProblemForm();
        verify(view1).startForm(any(JSONObject.class));
    }

    @Test
    public void testClickOnLayoutLooksGoodShouldOpenConfirmationDialog() {
        View view = new View(RuntimeEnvironment.application);
        view.setId(R.id.layout_product_looks_good);
        activity.onClick(view);
        verify(activity).openLooksGoodConfirmationDialog();
    }

    @Test
    public void testOnActivityResultShouldSaveFlagProblemForm() {
        ProductInfoActivityContract.Presenter presenter = spy(new ProductInfoActivityPresenter(activity));
        ReflectionHelpers.setField(activity, "presenter", presenter);

        Intent intent = new Intent();
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, flagProblemForm);
        activity.onActivityResult(AppConstants.RequestCode.REQUEST_CODE_GET_JSON,
                RESULT_OK, intent);
        verify(presenter)
                .saveFlagProblemForm(any(TaskDetail.class), anyString(), any(JSONObject.class), any(StructureDetail.class));
    }

    @After
    public void tearDown() {
        activity.finish();
    }
}