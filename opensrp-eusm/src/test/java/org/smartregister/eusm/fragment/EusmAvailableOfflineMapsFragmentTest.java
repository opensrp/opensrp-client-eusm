package org.smartregister.eusm.fragment;

import androidx.fragment.app.testing.FragmentScenario;

import com.mapbox.geojson.FeatureCollection;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.annotation.LooperMode;
import org.smartregister.domain.Location;
import org.smartregister.eusm.BaseUnitTest;
import org.smartregister.eusm.repository.AppStructureRepository;

import java.util.Collections;

import static android.os.Looper.getMainLooper;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;
import static org.robolectric.annotation.LooperMode.Mode.PAUSED;

@LooperMode(PAUSED)
public class EusmAvailableOfflineMapsFragmentTest extends BaseUnitTest {

    private FragmentScenario<EusmAvailableOfflineMapsFragment> fragmentScenario;

    @Before
    public void setUp() throws Exception {
        fragmentScenario = FragmentScenario.launch(EusmAvailableOfflineMapsFragment.class);
    }

    @Test
    public void testDownloadLocationShouldStartOfflineMap() {
        fragmentScenario.onFragment(fragment -> {
            EusmAvailableOfflineMapsFragment fragmentSpy = spy(fragment);
            Location location = new Location();
            location.setId("4322-23");
            location.setType("Feature");
            doNothing().when(fragmentSpy).downloadMap(any(FeatureCollection.class), anyString());
            AppStructureRepository appStructureRepository = mock(AppStructureRepository.class);
            doReturn(appStructureRepository).when(fragmentSpy).getAppStructureRepository();
            doReturn(Collections.singletonList(location)).when(appStructureRepository).getStructuresByDistrictId(anyString());
            doNothing().when(fragmentSpy).displayToast(anyString());
            fragmentSpy.downloadLocation(location);
            shadowOf(getMainLooper()).idle();
            try {
                Thread.sleep(ASYNC_TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            verify(fragmentSpy).downloadMap(any(FeatureCollection.class), eq(location.getId()));
        });
    }

}