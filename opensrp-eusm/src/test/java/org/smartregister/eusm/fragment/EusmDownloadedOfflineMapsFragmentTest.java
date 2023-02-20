package org.smartregister.eusm.fragment;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.robolectric.annotation.LooperMode.Mode.PAUSED;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.annotation.LooperMode;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.domain.Location;
import org.smartregister.eusm.BaseUnitTest;
import org.smartregister.eusm.R;
import org.smartregister.tasking.contract.OfflineMapDownloadCallback;
import org.smartregister.tasking.model.OfflineMapModel;
import org.smartregister.tasking.presenter.DownloadedOfflineMapsPresenter;

import java.util.ArrayList;
import java.util.List;

@LooperMode(PAUSED)
public class EusmDownloadedOfflineMapsFragmentTest extends BaseUnitTest {

    private EusmDownloadedOfflineMapsFragment fragment;

    @Before
    public void setUp() {
        fragment = new EusmDownloadedOfflineMapsFragment();
    }

    @Test
    public void testGetInstanceMethodNotNull() {
        Assert.assertNotNull(EusmDownloadedOfflineMapsFragment.newInstance(new Bundle(), mock(Context.class)));
    }

        @Test
    public void testSetUpViewsShouldInitAdapter() {
        EusmDownloadedOfflineMapsFragment fragmentSpy = spy(fragment);
        doReturn(mock(Context.class)).when(fragmentSpy).requireContext();

        View view = mock(View.class);

        RecyclerView recyclerView = mock(RecyclerView.class);
        doReturn(recyclerView).when(view).findViewById(eq(R.id.offline_map_recyclerView));

        Button btnDeleteMap = mock(Button.class);
        doReturn(btnDeleteMap).when(view).findViewById(eq(R.id.download_map));
        doReturn("").when(fragmentSpy).getString(anyInt());
        fragmentSpy.setUpViews(view);

        Assert.assertNotNull(ReflectionHelpers.getField(fragmentSpy, "adapter"));
    }

    @Test
    public void testStartDeleteProcessShouldStartDeletingSelectedMaps() {
        EusmDownloadedOfflineMapsFragment fragmentSpy = spy(fragment);
        DownloadedOfflineMapsPresenter presenter = mock(DownloadedOfflineMapsPresenter.class);
        ReflectionHelpers.setField(fragmentSpy, "fragmentPresenter", presenter);

        List<OfflineMapModel> downloadedOfflineMapModelList = new ArrayList<>();
        OfflineMapModel offlineMapModel1 = new OfflineMapModel();
        offlineMapModel1.setOfflineMapStatus(OfflineMapModel.OfflineMapStatus.SELECTED_FOR_DOWNLOAD);
        OfflineMapModel offlineMapModel2 = new OfflineMapModel();
        offlineMapModel2.setOfflineMapStatus(OfflineMapModel.OfflineMapStatus.READY);
        ReflectionHelpers.setField(fragmentSpy, "downloadedOfflineMapModelList", downloadedOfflineMapModelList);
        doReturn("").when(fragmentSpy).getString(anyInt());
        doNothing().when(fragmentSpy).displaySnackBar(anyString());
        fragmentSpy.startDeleteProcess();
        verifyNoInteractions(presenter);

        downloadedOfflineMapModelList.add(offlineMapModel2);
        downloadedOfflineMapModelList.add(offlineMapModel1);
        ReflectionHelpers.setField(fragmentSpy, "downloadedOfflineMapModelList", downloadedOfflineMapModelList);
        fragmentSpy.startDeleteProcess();

        List<OfflineMapModel> mapsToDownload = new ArrayList<>();
        mapsToDownload.add(offlineMapModel1);
        verify(presenter, times(1)).onDeleteDownloadMap(eq(mapsToDownload));
    }

    @Test
    public void testUpdateOfflineMapsTodeleteShouldMarkSelectedForDownloadWHenChecked() {
        EusmDownloadedOfflineMapsFragment fragmentSpy = spy(fragment);

        Location location = new Location();
        location.setId("4322-23");
        location.setType("Feature");

        OfflineMapModel offlineMapModel = new OfflineMapModel();
        offlineMapModel.setOfflineMapStatus(OfflineMapModel.OfflineMapStatus.READY);
        offlineMapModel.setLocation(location);

        List<OfflineMapModel> downloadedOfflineMapModelList = new ArrayList<>();
        downloadedOfflineMapModelList.add(offlineMapModel);
        ReflectionHelpers.setField(fragmentSpy, "downloadedOfflineMapModelList", downloadedOfflineMapModelList);

        CheckBox checkBox = mock(CheckBox.class);
        doReturn(true).when(checkBox).isChecked();
        doReturn(offlineMapModel).when(checkBox).getTag(eq(R.id.offline_map_checkbox));

        fragmentSpy.updateOfflineMapsTodelete(checkBox);
        List<OfflineMapModel> downloadedOfflineMapModelListProcessed = ReflectionHelpers.getField(fragmentSpy, "downloadedOfflineMapModelList");
        Assert.assertEquals(OfflineMapModel.OfflineMapStatus.SELECTED_FOR_DOWNLOAD, downloadedOfflineMapModelListProcessed.get(0).getOfflineMapStatus());
    }

    @Test
    public void testUpdateOfflineMapsTodeleteShouldMarkSReadyWHenUnChecked() {
        EusmDownloadedOfflineMapsFragment fragmentSpy = spy(fragment);

        Location location = new Location();
        location.setId("4322-23");
        location.setType("Feature");

        OfflineMapModel offlineMapModel = new OfflineMapModel();
        offlineMapModel.setOfflineMapStatus(OfflineMapModel.OfflineMapStatus.SELECTED_FOR_DOWNLOAD);
        offlineMapModel.setLocation(location);

        List<OfflineMapModel> downloadedOfflineMapModelList = new ArrayList<>();
        downloadedOfflineMapModelList.add(offlineMapModel);
        ReflectionHelpers.setField(fragmentSpy, "downloadedOfflineMapModelList", downloadedOfflineMapModelList);

        CheckBox checkBox = mock(CheckBox.class);
        doReturn(false).when(checkBox).isChecked();
        doReturn(offlineMapModel).when(checkBox).getTag(eq(R.id.offline_map_checkbox));

        fragmentSpy.updateOfflineMapsTodelete(checkBox);
        List<OfflineMapModel> downloadedOfflineMapModelListProcessed = ReflectionHelpers.getField(fragmentSpy, "downloadedOfflineMapModelList");
        Assert.assertEquals(OfflineMapModel.OfflineMapStatus.READY, downloadedOfflineMapModelListProcessed.get(0).getOfflineMapStatus());
    }

    @Test
    public void testDeleteDownloadedOfflineMapsShouldMarkSelectedForDownloadAsDownloadStarted() {
        EusmDownloadedOfflineMapsFragment fragmentSpy = spy(fragment);
        Location location = new Location();
        location.setId("4322-23");
        location.setType("Feature");

        OfflineMapModel offlineMapModel = new OfflineMapModel();
        offlineMapModel.setOfflineMapStatus(OfflineMapModel.OfflineMapStatus.SELECTED_FOR_DOWNLOAD);
        offlineMapModel.setLocation(location);

        List<OfflineMapModel> downloadedOfflineMapModelList = new ArrayList<>();
        downloadedOfflineMapModelList.add(offlineMapModel);
        ReflectionHelpers.setField(fragmentSpy, "downloadedOfflineMapModelList", downloadedOfflineMapModelList);

        doReturn("").when(fragmentSpy).getString(anyInt());
        doReturn(mock(FragmentActivity.class)).when(fragmentSpy).requireActivity();
        doNothing().when(fragmentSpy).displaySnackBar(anyString());

        fragmentSpy.deleteDownloadedOfflineMaps();
        List<OfflineMapModel> downloadedOfflineMapModelListProcessed = ReflectionHelpers.getField(fragmentSpy, "downloadedOfflineMapModelList");
        Assert.assertEquals(OfflineMapModel.OfflineMapStatus.DOWNLOAD_STARTED, downloadedOfflineMapModelListProcessed.get(0).getOfflineMapStatus());
    }

    @Test
    public void testMapDeletedSuccessfullyShouldRemoveOfflineMapFromList() {
        EusmDownloadedOfflineMapsFragment fragmentSpy = spy(fragment);
        Location location = new Location();
        location.setId("4322-23");
        location.setType("Feature");

        OfflineMapModel offlineMapModel = new OfflineMapModel();
        offlineMapModel.setOfflineMapStatus(OfflineMapModel.OfflineMapStatus.DOWNLOAD_STARTED);
        offlineMapModel.setLocation(location);

        List<OfflineMapModel> downloadedOfflineMapModelList = new ArrayList<>();
        downloadedOfflineMapModelList.add(offlineMapModel);
        ReflectionHelpers.setField(fragmentSpy, "downloadedOfflineMapModelList", downloadedOfflineMapModelList);

        OfflineMapDownloadCallback callback = mock(OfflineMapDownloadCallback.class);
        fragmentSpy.setOfflineMapDownloadCallback(callback);

        fragmentSpy.mapDeletedSuccessfully("4322-23");
        List<OfflineMapModel> downloadedOfflineMapModelListProcessed = ReflectionHelpers.getField(fragmentSpy, "downloadedOfflineMapModelList");
        Assert.assertTrue(downloadedOfflineMapModelListProcessed.isEmpty());

    }

}