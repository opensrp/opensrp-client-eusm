package org.smartregister.eusm.util;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.domain.ProfileImage;
import org.smartregister.eusm.BaseUnitTest;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.domain.StructureDetail;
import org.smartregister.eusm.domain.TaskDetail;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.ImageRepository;
import org.smartregister.stock.util.Constants;
import org.smartregister.view.activity.DrishtiApplication;

import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.smartregister.client.utils.constants.JsonFormConstants.Properties.DETAILS;

public class AppJsonFormUtilsTest extends BaseUnitTest {

    private AppJsonFormUtils jsonFormUtils;

    private final String flagProblemForm = "{\"count\":\"2\",\"entity_id\":\"2323\",\"encounter_type\":\"flag_problem\",\"form_version\":\"0.0.1\",\"metadata\":{\"start\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"start\",\"openmrs_entity_id\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"end\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"end\",\"openmrs_entity_id\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"today\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"encounter\",\"openmrs_entity_id\":\"encounter_date\"},\"deviceid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"deviceid\",\"openmrs_entity_id\":\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"subscriberid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"subscriberid\",\"openmrs_entity_id\":\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"simserial\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"simserial\",\"openmrs_entity_id\":\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"phonenumber\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"phonenumber\",\"openmrs_entity_id\":\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"encounter_location\":\"\"},\"step1\":{\"title\":\"Flag Problem\",\"next\":\"step2\",\"fields\":[{\"key\":\"flag_problem\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"check_box\",\"label\":\"Flag problem(s)\",\"label_text_style\":\"bold\",\"exclusive\":[\"not_there\"],\"options\":[{\"key\":\"not_there\",\"text\":\"Product is not there\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"not_good\",\"text\":\"Product is not in good condition\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"misuse\",\"text\":\"Product is not being used appropriately\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"}],\"v_required\":{\"value\":true,\"err\":\"Field required\"}}]},\"step2\":{\"title\":\"Reason\",\"fields\":[{\"key\":\"product_picture\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"choose_image\",\"value\":\"/\",\"uploadButtonText\":\"Take a photo of the product\",\"relevance\":{\"step1:flag_problem\":{\"ex-checkbox\":[{\"or\":[\"not_good\",\"misuse\"]}]}}},{\"key\":\"not_good\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"native_radio\",\"label\":\"What is wrong with the product?\",\"label_text_style\":\"bold\",\"options\":[{\"key\":\"worn_broken\",\"text\":\"Worn, damaged, or broken\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"expired\",\"text\":\"Expired\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"parts_missing\",\"text\":\"Parts missing\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"other\",\"text\":\"Other (specify)\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"}],\"v_required\":{\"value\":true,\"err\":\"Field required\"},\"relevance\":{\"step1:flag_problem\":{\"ex-checkbox\":[{\"or\":[\"not_good\"]}]}}},{\"key\":\"not_good_specify_other\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"edit_text\",\"hint\":\"Specify other:\",\"relevance\":{\"step2:not_good\":{\"type\":\"string\",\"ex\":\"equalTo(., \\\"other\\\")\"}}},{\"key\":\"not_there\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"native_radio\",\"label\":\"What happened to the product?\",\"label_text_style\":\"bold\",\"options\":[{\"key\":\"lost_stolen\",\"text\":\"Lost / Stolen\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"never_received\",\"text\":\"Never received\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"discarded\",\"text\":\"Discarded (e.g. damaged on delivery, expired)\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"stock_out\",\"text\":\"Stock out\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"other\",\"text\":\"Other (specify)\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"}],\"v_required\":{\"value\":true,\"err\":\"Field required\"},\"relevance\":{\"step1:flag_problem\":{\"ex-checkbox\":[{\"or\":[\"not_there\"]}]}}},{\"key\":\"not_there_specify_other\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"edit_text\",\"hint\":\"Specify other:\",\"relevance\":{\"step2:not_there\":{\"type\":\"string\",\"ex\":\"equalTo(., \\\"other\\\")\"}}},{\"key\":\"misuse\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"native_radio\",\"label\":\"How is the product not being used appropriately?\",\"label_text_style\":\"bold\",\"options\":[{\"key\":\"lacks_skills\",\"text\":\"End user lacks necessary skills or knowledge\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"fraud_use\",\"text\":\"End user is using the product for unauthorised / fraudulent purposes\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"}],\"v_required\":{\"value\":true,\"err\":\"Field required\"},\"relevance\":{\"step1:flag_problem\":{\"ex-checkbox\":[{\"or\":[\"misuse\"]}]}}},{\"key\":\"issue_details\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"edit_text\",\"hint\":\"Provide some details on the specific issue, including any advice you were able to give.\"}]}}";

    @Mock
    private EusmApplication eusmApplication;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        jsonFormUtils = spy(AppJsonFormUtils.class);
    }

    @Test
    public void testSaveImageShouldSaveImageToFileAndImageTable() throws JSONException, IOException {
        String providerId = "demo";
        String baseEntityId = "2323";

        ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", eusmApplication);

        when(eusmApplication.getApplicationContext()).thenReturn(RuntimeEnvironment.application);

        Compressor compressor = mock(Compressor.class);
        Bitmap bitmap = mock(Bitmap.class);
        Context opensrpContext = mock(Context.class);
        ImageRepository imageRepository = mock(ImageRepository.class);
        AllSharedPreferences allSharedPreferences = mock(AllSharedPreferences.class);

        doReturn(imageRepository).when(opensrpContext).imageRepository();
        doReturn(providerId).when(allSharedPreferences).fetchRegisteredANM();
        doReturn(allSharedPreferences).when(opensrpContext).allSharedPreferences();
        doReturn(opensrpContext).when(eusmApplication).context();
        doReturn(bitmap).when(compressor).compressToBitmap(any(File.class));
        doReturn(compressor).when(eusmApplication).getCompressor();
        JSONObject jsonObject = new JSONObject(flagProblemForm);
        JSONObject detailsJsonObject = new JSONObject();
        detailsJsonObject.put(AppConstants.EventDetailKey.PLAN_IDENTIFIER, "3432");
        jsonObject.put(DETAILS, detailsJsonObject);
        jsonFormUtils.saveImage(jsonObject, "product");

        ArgumentCaptor<ProfileImage> profileImageArgumentCaptor = ArgumentCaptor.forClass(ProfileImage.class);
        verify(imageRepository).add(profileImageArgumentCaptor.capture());

        ProfileImage profileImage = profileImageArgumentCaptor.getValue();
        assertNotNull(profileImage);

        assertEquals(providerId, profileImage.getAnmId());
        assertEquals(Constants.PRODUCT_IMAGE, profileImage.getFilecategory());
        assertEquals(baseEntityId.concat("_").concat("3432"), profileImage.getEntityID());
    }

    @Test
    public void testGetFormObjectWithDetailsShouldReturnJsonForm() {
        TaskDetail taskDetail = new TaskDetail();
        taskDetail.setTaskCode("fix_problem");
        assertNotNull(jsonFormUtils);
        JSONObject jsonObject = jsonFormUtils.getFormObjectWithDetails(RuntimeEnvironment.application,
                AppConstants.JsonForm.FIX_PROBLEM_FORM, new StructureDetail(), taskDetail);
        assertNotNull(jsonObject);
    }
}