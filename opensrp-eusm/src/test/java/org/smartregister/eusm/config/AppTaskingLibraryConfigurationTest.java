package org.smartregister.eusm.config;

import com.mapbox.geojson.Feature;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.annotation.LooperMode;
import org.smartregister.domain.Location;
import org.smartregister.eusm.BaseUnitTest;
import org.smartregister.tasking.contract.BaseDrawerContract;
import org.smartregister.tasking.contract.TaskingMapActivityContract;
import org.smartregister.tasking.model.CardDetails;

import java.util.ArrayList;
import java.util.List;

import static android.os.Looper.getMainLooper;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
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
    public void testFetchPlansShouldInvokeRequiredMethod() {
        appTaskingLibraryConfiguration.fetchPlans("re", baseDrawerPresenter);
        verify(baseDrawerPresenter).onPlansFetched(anySet());
    }

    @Test
    public void testGetLocationsIdsForDownloadShouldReturnListOfLocations() {
        List<Location> locationList = appTaskingLibraryConfiguration.getLocationsIdsForDownload(new ArrayList<>());
        assertNotNull(locationList);
    }
}