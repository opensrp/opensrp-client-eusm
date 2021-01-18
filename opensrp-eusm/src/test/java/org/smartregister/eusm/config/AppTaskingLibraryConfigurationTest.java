package org.smartregister.eusm.config;

import com.mapbox.geojson.Feature;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.annotation.LooperMode;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.domain.Location;
import org.smartregister.domain.Setting;
import org.smartregister.eusm.BaseUnitTest;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.repository.AllSettings;
import org.smartregister.tasking.contract.BaseDrawerContract;
import org.smartregister.tasking.contract.TaskingMapActivityContract;
import org.smartregister.tasking.model.CardDetails;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.os.Looper.getMainLooper;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;
import static org.robolectric.annotation.LooperMode.Mode.PAUSED;

@LooperMode(PAUSED)
public class AppTaskingLibraryConfigurationTest extends BaseUnitTest {

    private AppTaskingLibraryConfiguration appTaskingLibraryConfiguration;

    @Mock
    private TaskingMapActivityContract.Presenter presenter;

    @Mock
    private BaseDrawerContract.Presenter baseDrawerPresenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        appTaskingLibraryConfiguration = spy(new AppTaskingLibraryConfiguration());
    }

    @Test
    public void testOnFeatureSelectedByClickShouldInvokeRequiredMethod() throws InterruptedException {
        Feature feature = Feature.fromJson("{\"syncStatus\":\"Synced\",\"geometry\":{\"coordinates\":[49.584358215332,-16.4330005645752],\"type\":\"Point\"},\"id\":\"b8a7998c-5df6-49eb-98e6-f0675db71848\",\"properties\":{\"geographicLevel\":0,\"name\":\"Ambatoharanana\",\"parentId\":\"663d7935-35e7-4ccf-aaf5-6e16f2042570\",\"status\":\"Active\",\"type\":\"Water Point\",\"commune\":\"Ambamanigwe\",\"distanceMeta\":\"34 km\",\"structureId\":\"342-23\",\"taskStatus\":\"START\",\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18480,\"type\":\"Feature\"}");
        appTaskingLibraryConfiguration.onFeatureSelectedByClick(feature, presenter);
        shadowOf(getMainLooper()).idle();
        Thread.sleep(ASYNC_TIMEOUT);
        verify(presenter).onCardDetailsFetched(any(CardDetails.class));
    }

    @Test
    public void testFetchPlansShouldInvokeRequiredMethod() throws InterruptedException {
        appTaskingLibraryConfiguration.fetchPlans("re", baseDrawerPresenter);
        shadowOf(getMainLooper()).idle();
        Thread.sleep(ASYNC_TIMEOUT);
        verify(baseDrawerPresenter).onPlansFetched(anySet());
    }

    @Test
    public void testGetLocationsIdsForDownloadShouldReturnListOfLocations() {
        List<Location> locationList = appTaskingLibraryConfiguration.getLocationsIdsForDownload(new ArrayList<>());
        assertNotNull(locationList);
    }

    @Test
    public void testProcessServerConfigsShouldNotPopulateServerConfigsIfSettingIsNull() {
        EusmApplication eusmApplication = spy(EusmApplication.getInstance());

        ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", eusmApplication);

        ReflectionHelpers.setField(eusmApplication, "serverConfigs", new HashMap<>());

        appTaskingLibraryConfiguration.processServerConfigs();

        assertTrue(EusmApplication.getInstance().getServerConfigs().isEmpty());
    }

    @Test
    public void testProcessServerConfigsShouldPopulateServerConfigsIfSettingPresent() {
        EusmApplication eusmApplication = spy(EusmApplication.getInstance());

        ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", eusmApplication);

        ReflectionHelpers.setField(eusmApplication, "serverConfigs", new HashMap<>());

        AllSettings settingsRepository = mock(AllSettings.class);
        Setting setting = new Setting();
        setting.setKey(AppConstants.CONFIGURATION.GLOBAL_CONFIGS);
        setting.setDescription("its a description");
        setting.setIdentifier("test_setting");
        setting.setType("configuration");
        setting.setValue("{\n" +
                "  \"identifier\": \"inventory_unicef_sections\",\n" +
                "  \"settings\": [\n" +
                "    {\n" +
                "      \"settingMetadataId\": \"48\",\n" +
                "      \"serverVersion\": 0,\n" +
                "      \"description\": \"\",\n" +
                "      \"label\": \"Health\",\n" +
                "      \"type\": \"Setting\",\n" +
                "      \"value\": \"Health\",\n" +
                "      \"uuid\": \"8b723a4e-9df6-492f-98bf-2eaa5675a07e\",\n" +
                "      \"key\": \"HEALTH\",\n" +
                "      \"settingIdentifier\": \"inventory_unicef_sections\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"serverVersion\": 3,\n" +
                "  \"_rev\": \"v1\",\n" +
                "  \"_id\": \"1c2be1bb-3b1b-4978-849d-e2a0ef4d445d\",\n" +
                "  \"type\": \"SettingConfiguration\"\n" +
                "}");

        doReturn(setting).when(settingsRepository).getSetting(setting.getKey());

        doReturn(settingsRepository).when(eusmApplication).getSettingsRepository();

        appTaskingLibraryConfiguration.processServerConfigs();

        assertFalse(EusmApplication.getInstance().getServerConfigs().isEmpty());
    }
}