package org.smartregister.eusm.fragment;

import androidx.fragment.app.testing.FragmentScenario;

import com.mapbox.geojson.FeatureCollection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.annotation.LooperMode;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.domain.Location;
import org.smartregister.eusm.BaseUnitTest;
import org.smartregister.eusm.R;
import org.smartregister.eusm.repository.AppStructureRepository;
import org.smartregister.tasking.contract.OfflineMapDownloadCallback;
import org.smartregister.tasking.model.OfflineMapModel;
import org.smartregister.util.AppExecutors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.os.Looper.getMainLooper;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;
import static org.robolectric.annotation.LooperMode.Mode.PAUSED;

import android.widget.CheckBox;

@LooperMode(PAUSED)
public class EusmAvailableOfflineMapsFragmentTest extends BaseUnitTest {

    private FragmentScenario<EusmAvailableOfflineMapsFragment> fragmentScenario;

    @Before
    public void setUp() throws Exception {
        fragmentScenario = FragmentScenario.launch(EusmAvailableOfflineMapsFragment.class);
    }

    @Test
    public void testGetAppStructureRepositoryNotNull() {
        Assert.assertNotNull(fragmentScenario);  // To remove codacy error for assertion
        fragmentScenario.onFragment(fragment -> {
            AppStructureRepository repository = fragment.getAppStructureRepository();
            Assert.assertNotNull(repository);
        });
    }

    @Test
    public void testGetAppExecutors() {
        Assert.assertNotNull(fragmentScenario);  // To remove codacy error for assertion
        fragmentScenario.onFragment(fragment -> {
            AppExecutors appExecutors = fragment.getAppExecutors();
            Assert.assertNotNull(appExecutors);
        });
    }

    @Test
    public void testDownloadLocationShouldStartOfflineMap() {
        Assert.assertNotNull(fragmentScenario);  // To remove codacy error for assertion
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

    @Test
    public void testDownloadLocationShouldReturnWhenNoStructuresForLocation() {
        Assert.assertNotNull(fragmentScenario);  // To remove codacy error for assertion
        fragmentScenario.onFragment(fragment -> {
            EusmAvailableOfflineMapsFragment fragmentSpy = spy(fragment);
            doNothing().when(fragmentSpy).downloadMap(any(FeatureCollection.class), anyString());
            AppStructureRepository appStructureRepository = mock(AppStructureRepository.class);
            doReturn(appStructureRepository).when(fragmentSpy).getAppStructureRepository();
            doReturn(Collections.singletonList(new ArrayList<Location>())).when(appStructureRepository).getStructuresByDistrictId(anyString());
            doNothing().when(fragmentSpy).displayToast(anyString());

            OfflineMapModel offlineMapModel = new OfflineMapModel();
            offlineMapModel.setOfflineMapStatus(OfflineMapModel.OfflineMapStatus.DOWNLOAD_STARTED);
            List<OfflineMapModel> offlineMapModelList = new ArrayList<>();
            offlineMapModelList.add(offlineMapModel);
            ReflectionHelpers.setField(fragmentSpy, "offlineMapModelList", offlineMapModelList);

            fragmentSpy.downloadLocation(new Location());

            shadowOf(getMainLooper()).idle();
            try {
                Thread.sleep(ASYNC_TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            List<OfflineMapModel> offlineMapModelListProcessed =  ReflectionHelpers.getField(fragmentSpy, "offlineMapModelList");
            Assert.assertEquals(OfflineMapModel.OfflineMapStatus.READY, offlineMapModelListProcessed.get(0).getOfflineMapStatus());
        });
    }

    @Test
    public void testMoveDownloadedOAToDownloadedListRemovesItMapFromList() {
        Assert.assertNotNull(fragmentScenario);  // To remove codacy error for assertion
        fragmentScenario.onFragment(fragment -> {
            EusmAvailableOfflineMapsFragment fragmentSpy = spy(fragment);

            Location location = new Location();
            location.setId("4322-23");
            location.setType("Feature");
            List<Location> operationalAreasToDownload = new ArrayList<>();
            operationalAreasToDownload.add(location);
            ReflectionHelpers.setField(fragmentSpy, "operationalAreasToDownload", operationalAreasToDownload);

            OfflineMapModel offlineMapModel = new OfflineMapModel();
            offlineMapModel.setOfflineMapStatus(OfflineMapModel.OfflineMapStatus.DOWNLOAD_STARTED);
            offlineMapModel.setLocation(location);
            List<OfflineMapModel> offlineMapModelList = new ArrayList<>();
            offlineMapModelList.add(offlineMapModel);
            ReflectionHelpers.setField(fragmentSpy, "offlineMapModelList", offlineMapModelList);

            OfflineMapDownloadCallback callback  = mock(OfflineMapDownloadCallback.class);
            doNothing().when(callback).onMapDownloaded(any());
            ReflectionHelpers.setField(fragmentSpy, "callback", callback);

            fragmentSpy.moveDownloadedOAToDownloadedList("4322-23");

            List<OfflineMapModel> offlineMapModelListProcessed =  ReflectionHelpers.getField(fragmentSpy, "offlineMapModelList");
            Assert.assertEquals(OfflineMapModel.OfflineMapStatus.READY, offlineMapModelListProcessed.get(0).getOfflineMapStatus());
            List<Location> operationalAreasToDownloadProcessed =  ReflectionHelpers.getField(fragmentSpy, "operationalAreasToDownload");
            Assert.assertEquals(0, operationalAreasToDownloadProcessed.size());
        });
    }

    @Test
    public void testUpdateOperationalAreasToDownloadShouldSelectSingleMapAtOneTIme() {
        Assert.assertNotNull(fragmentScenario);  // To remove codacy error for assertion
        fragmentScenario.onFragment(fragment -> {
            EusmAvailableOfflineMapsFragment fragmentSpy = spy(fragment);

            Location location1 = new Location();
            location1.setId("4322-23");
            location1.setType("Feature");
            Location location2 = new Location();
            location2.setId("4322-24");
            location2.setType("Feature");

            List<Location> operationalAreasToDownload = new ArrayList<>();
            operationalAreasToDownload.add(location1);
            operationalAreasToDownload.add(location2);
            ReflectionHelpers.setField(fragmentSpy, "operationalAreasToDownload", operationalAreasToDownload);

            OfflineMapModel offlineMapModel = new OfflineMapModel();
            offlineMapModel.setOfflineMapStatus(OfflineMapModel.OfflineMapStatus.READY);
            offlineMapModel.setLocation(location1);
            OfflineMapModel offlineMapModel2 = new OfflineMapModel();
            offlineMapModel2.setOfflineMapStatus(OfflineMapModel.OfflineMapStatus.SELECTED_FOR_DOWNLOAD);
            offlineMapModel2.setLocation(location2);

            CheckBox checkBox = mock(CheckBox.class);
            doReturn(true).when(checkBox).isChecked();
            doReturn(offlineMapModel).when(checkBox).getTag(eq(R.id.offline_map_checkbox));

            List<OfflineMapModel> offlineMapModelList = new ArrayList<>();
            offlineMapModelList.add(offlineMapModel);
            offlineMapModelList.add(offlineMapModel2);
            ReflectionHelpers.setField(fragmentSpy, "offlineMapModelList", offlineMapModelList);

            fragmentSpy.updateOperationalAreasToDownload(checkBox);

            List<OfflineMapModel> offlineMapModelListProcessed =  ReflectionHelpers.getField(fragmentSpy, "offlineMapModelList");
            for (OfflineMapModel model : offlineMapModelListProcessed) {
                if (model.getDownloadAreaId().equals(offlineMapModel.getDownloadAreaId())){
                    Assert.assertEquals(OfflineMapModel.OfflineMapStatus.SELECTED_FOR_DOWNLOAD, model.getOfflineMapStatus());
                } else if (model.getDownloadAreaId().equals(offlineMapModel2.getDownloadAreaId())){
                    Assert.assertEquals(OfflineMapModel.OfflineMapStatus.READY, model.getOfflineMapStatus());
                }
            }

            doReturn(false).when(checkBox).isChecked();
            operationalAreasToDownload.clear();
            operationalAreasToDownload.add(location1);
            operationalAreasToDownload.add(location2);
            ReflectionHelpers.setField(fragmentSpy, "operationalAreasToDownload", operationalAreasToDownload);
            fragmentSpy.updateOperationalAreasToDownload(checkBox);

            List<Location> operationalAreasToDownloadProcessed =  ReflectionHelpers.getField(fragmentSpy, "operationalAreasToDownload");
            Assert.assertEquals(1, operationalAreasToDownloadProcessed.size());
        });
    }

    @Test
    public void testInitiateMapDownloadShouldNotDownloadIfAnotherMapInProgress() {
        Assert.assertNotNull(fragmentScenario);  // To remove codacy error for assertion
        fragmentScenario.onFragment(fragment -> {
            EusmAvailableOfflineMapsFragment fragmentSpy = spy(fragment);
            doNothing().when(fragmentSpy).displayToast(anyString());

            Location location = new Location();
            location.setId("4322-23");
            location.setType("Feature");
            List<Location> operationalAreasToDownload = new ArrayList<>();
            operationalAreasToDownload.add(location);
            ReflectionHelpers.setField(fragmentSpy, "operationalAreasToDownload", operationalAreasToDownload);

            OfflineMapModel offlineMapModel = new OfflineMapModel();
            offlineMapModel.setOfflineMapStatus(OfflineMapModel.OfflineMapStatus.DOWNLOAD_STARTED);
            offlineMapModel.setLocation(location);
            List<OfflineMapModel> offlineMapModelList = new ArrayList<>();
            offlineMapModelList.add(offlineMapModel);
            ReflectionHelpers.setField(fragmentSpy, "offlineMapModelList", offlineMapModelList);

            fragmentSpy.initiateMapDownload();
            verify(fragmentSpy, times(1)).displayToast(anyString());
        });
    }

}