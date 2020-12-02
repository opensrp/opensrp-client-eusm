package org.smartregister.eusm.activity;

import com.mapbox.mapboxsdk.offline.OfflineManager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.smartregister.eusm.BaseUnitTest;
import org.smartregister.tasking.adapter.ViewPagerAdapter;
import org.smartregister.tasking.fragment.AvailableOfflineMapsFragment;
import org.smartregister.tasking.fragment.DownloadedOfflineMapsFragment;
import org.smartregister.tasking.model.OfflineMapModel;

import java.util.ArrayList;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Richard Kareko on 1/30/20.
 */

public class EusmOfflineMapsActivityTest extends BaseUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private ViewPagerAdapter adapter;

    @Mock
    private OfflineManager offlineManager;

    @Mock
    private AvailableOfflineMapsFragment availableOfflineMapsFragment;

    @Mock
    private DownloadedOfflineMapsFragment downloadedOfflineMapsFragment;

    @Captor
    private ArgumentCaptor<Boolean> booleanArgumentCaptor;

    @Captor
    private ArgumentCaptor<OfflineMapModel> offlineMapModelArgumentCaptor;

    private EusmOfflineMapsActivity eusmOfflineMapsActivity;

    @Before
    public void setUp() {
        org.smartregister.Context.bindtypes = new ArrayList<>();
        eusmOfflineMapsActivity = Robolectric.buildActivity(EusmOfflineMapsActivity.class).create().get();
        Whitebox.setInternalState(eusmOfflineMapsActivity, "adapter", adapter);
        Whitebox.setInternalState(eusmOfflineMapsActivity, "availableOfflineMapsFragment", availableOfflineMapsFragment);
        Whitebox.setInternalState(eusmOfflineMapsActivity, "downloadedOfflineMapsFragment", downloadedOfflineMapsFragment);
        Whitebox.setInternalState(eusmOfflineMapsActivity, "offlineManager", offlineManager);

    }

    @Test
    public void testOnCreate() {
        assertNotNull(eusmOfflineMapsActivity);
    }

//    @Test
//    public void testOnMapDownloaded() {
//        offlineMapsActivity = spy(offlineMapsActivity);
//        offlineMapsActivity.onMapDownloaded(TestingUtils.getOfflineMapModel());
//        verify(offlineMapsActivity).getOfflineDownloadedRegions(booleanArgumentCaptor.capture());
//        assertTrue(booleanArgumentCaptor.getValue());
//    }
//
//    @Test
//    public void testOnOfflineMapDeleted() {
//        OfflineMapModel expectedOfflineMapModel = TestingUtils.getOfflineMapModel();
//        offlineMapsActivity = spy(offlineMapsActivity);
//        when(adapter.getItem(anyInt())).thenReturn(availableOfflineMapsFragment);
//        offlineMapsActivity.onOfflineMapDeleted(expectedOfflineMapModel);
//        verify(availableOfflineMapsFragment).updateOperationalAreasToDownload(offlineMapModelArgumentCaptor.capture());
//
//        OfflineMapModel actualOfflineMapModel = offlineMapModelArgumentCaptor.getValue();
//        assertNotNull(actualOfflineMapModel);
//        assertEquals(expectedOfflineMapModel.getLocation().getId(), actualOfflineMapModel.getLocation().getId());
//    }

//    @Test
//    public void testSetOfflineDownloadedMapNames() throws  Exception {
//        offlineMapsActivity = spy(offlineMapsActivity);
//
//        when(adapter.getItem(OfflineMapsActivity.AVAILABLE_OFFLINE_MAPS_FRAGMENT_INDEX)).thenReturn(availableOfflineMapsFragment);
//        when(adapter.getItem(OfflineMapsActivity.DOWNLOADED_OFFLINE_MAPS_FRAGMENT_INDEX)).thenReturn(downloadedOfflineMapsFragment);
//
//        Pair<List<String>, Map<String, OfflineRegion>> offlineRegionInfo = initOfflineRegionInfo();
//
//        offlineMapsActivity.setOfflineDownloadedMapNames(offlineRegionInfo, false);
//        verify(downloadedOfflineMapsFragment).setOfflineDownloadedMapNames(offlineRegionInfo);
//        verify(availableOfflineMapsFragment).setOfflineDownloadedMapNames(offlineRegionInfo.first);
//    }

//    @Test
//    public void testSetOfflineDownloadedMapNamesRefreshDownloadListOnly() throws  Exception {
//        offlineMapsActivity = spy(offlineMapsActivity);
//
//        when(adapter.getItem(OfflineMapsActivity.AVAILABLE_OFFLINE_MAPS_FRAGMENT_INDEX)).thenReturn(availableOfflineMapsFragment);
//        when(adapter.getItem(OfflineMapsActivity.DOWNLOADED_OFFLINE_MAPS_FRAGMENT_INDEX)).thenReturn(downloadedOfflineMapsFragment);
//
//        Pair<List<String>, Map<String, OfflineRegion>> offlineRegionInfo = initOfflineRegionInfo();
//
//        offlineMapsActivity.setOfflineDownloadedMapNames(offlineRegionInfo, true);
//        verify(downloadedOfflineMapsFragment).setOfflineDownloadedMapNames(offlineRegionInfo);
//        verify(availableOfflineMapsFragment, times(0)).setOfflineDownloadedMapNames(offlineRegionInfo.first);
//    }


//    private Pair<List<String>, Map<String, OfflineRegion>> initOfflineRegionInfo() throws Exception {
//        List<String> offlineRegionNames = Collections.singletonList("Akros_1");
//        OfflineRegion offlineRegion = TestingUtils.createMockOfflineRegion();
//        OfflineRegion[] offlineRegions = {offlineRegion};
//
//        JSONObject task = new JSONObject();
//        task.put(METADATA_JSON_FIELD_REGION_NAME, "Akros_1");
//
//        MapBoxOfflineQueueTask offlineQueueTask = new MapBoxOfflineQueueTask();
//        offlineQueueTask.setTaskStatus(MapBoxOfflineQueueTask.TASK_STATUS_DONE);
//        offlineQueueTask.setTaskType(MapBoxOfflineQueueTask.TASK_TYPE_DOWNLOAD);
//        offlineQueueTask.setTask(task);
//
//        Map<String, MapBoxOfflineQueueTask> offlineQueueTaskMap = new HashMap<>();
//        offlineQueueTaskMap.put("Akros", offlineQueueTask);
//
//        return new Pair(offlineRegionNames, offlineRegions);
//    }

}