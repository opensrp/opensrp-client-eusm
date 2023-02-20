package org.smartregister.eusm.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import org.smartregister.eusm.R;
import org.smartregister.eusm.adapter.EUSMDownloadedOfflineMapAdapter;
import org.smartregister.tasking.TaskingLibrary;
import org.smartregister.tasking.contract.OfflineMapDownloadCallback;
import org.smartregister.tasking.fragment.DownloadedOfflineMapsFragment;
import org.smartregister.tasking.model.OfflineMapModel;
import org.smartregister.tasking.presenter.DownloadedOfflineMapsPresenter;

import java.util.ArrayList;
import java.util.List;

import io.ona.kujaku.helpers.OfflineServiceHelper;

public class EusmDownloadedOfflineMapsFragment extends DownloadedOfflineMapsFragment {


    private EUSMDownloadedOfflineMapAdapter adapter;

    private List<OfflineMapModel> downloadedOfflineMapModelList = new ArrayList<>();

    private static DownloadedOfflineMapsPresenter fragmentPresenter = null;

    private OfflineMapDownloadCallback callback;

    public static EusmDownloadedOfflineMapsFragment newInstance(Bundle bundle, @NonNull Context context) {
        EusmDownloadedOfflineMapsFragment fragment = new EusmDownloadedOfflineMapsFragment();
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        fragmentPresenter = new DownloadedOfflineMapsPresenter(fragment, context);
        fragment.setPresenter(fragmentPresenter);
        return fragment;
    }

    @Override
    public void displayToast(String message) {
        // Do nothing
    }

    @Override
    public void displayError(int title, String message) {
        // Do nothing
    }

    @Override
    public void setDownloadedOfflineMapModelList(List<OfflineMapModel> downloadedOfflineMapModelList) {
        if (adapter == null) {
            this.downloadedOfflineMapModelList = downloadedOfflineMapModelList;
        } else {
            adapter.setOfflineMapModels(downloadedOfflineMapModelList);
            this.downloadedOfflineMapModelList = downloadedOfflineMapModelList;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpViews(view);
    }

    protected void setUpViews(View view) {
        RecyclerView downloadedMapsRecyclerView = view.findViewById(R.id.offline_map_recyclerView);
        initAdapter(downloadedMapsRecyclerView);

        Button btnDeleteMap = view.findViewById(R.id.download_map);
        btnDeleteMap.setText(getString(R.string.delete).toUpperCase());
        btnDeleteMap.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.delete_map_bg));

        btnDeleteMap.setOnClickListener(v -> startDeleteProcess());
    }

    protected void startDeleteProcess() {
        List<OfflineMapModel> mapsToDownload = new ArrayList<>();
        for (OfflineMapModel offlineMapModel : downloadedOfflineMapModelList) {
            if (offlineMapModel.getOfflineMapStatus().equals(OfflineMapModel.OfflineMapStatus.SELECTED_FOR_DOWNLOAD)) {
                mapsToDownload.add(offlineMapModel);
            }
        }

        if (mapsToDownload.isEmpty()) {
            displaySnackBar(getString(R.string.select_offline_map_to_delete));
            return;
        }
        fragmentPresenter.onDeleteDownloadMap(mapsToDownload);
    }

    private void initAdapter(RecyclerView downloadedMapsRecyclerView) {
        adapter = new EUSMDownloadedOfflineMapAdapter(this.getContext(), this);
        downloadedMapsRecyclerView.setAdapter(adapter);
        if (downloadedOfflineMapModelList != null) {
            setDownloadedOfflineMapModelList(downloadedOfflineMapModelList);
        }
    }

    @Override
    public void updateOfflineMapsTodelete(View view) {
        CheckBox checkBox = (CheckBox) view;
        OfflineMapModel offlineMapModel = (OfflineMapModel) view.getTag(R.id.offline_map_checkbox);

        for (OfflineMapModel model : downloadedOfflineMapModelList) {
            if (model.getDownloadAreaId().equals(offlineMapModel.getDownloadAreaId())) {
                if (checkBox.isChecked()) {
                    model.setOfflineMapStatus(OfflineMapModel.OfflineMapStatus.SELECTED_FOR_DOWNLOAD);
                } else {
                    model.setOfflineMapStatus(OfflineMapModel.OfflineMapStatus.READY);
                }
            }
        }

        setDownloadedOfflineMapModelList(downloadedOfflineMapModelList);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fragmentPresenter = null;
    }

    @Override
    public void deleteDownloadedOfflineMaps() {
        displaySnackBar(getString(R.string.deleting_map));

        for (OfflineMapModel offlineMapModel : downloadedOfflineMapModelList) {
            if (offlineMapModel.getOfflineMapStatus().equals(OfflineMapModel.OfflineMapStatus.SELECTED_FOR_DOWNLOAD)) {
                offlineMapModel.setOfflineMapStatus(OfflineMapModel.OfflineMapStatus.DOWNLOAD_STARTED);
                OfflineServiceHelper.deleteOfflineMap(requireActivity(),
                        offlineMapModel.getDownloadAreaId(),
                        TaskingLibrary.getInstance().getMapboxAccessToken());
            }
        }
        setDownloadedOfflineMapModelList(downloadedOfflineMapModelList);
    }

    @Override
    public void setOfflineMapDownloadCallback(OfflineMapDownloadCallback callBack) {
        this.callback = callBack;
    }

    @Override
    protected void mapDeletedSuccessfully(String deletedMapName) {
        List<OfflineMapModel> toRemove = new ArrayList<>();
        for (OfflineMapModel offlineMapModel : downloadedOfflineMapModelList) {
            if (offlineMapModel.getDownloadAreaId().equals(deletedMapName)) {
                toRemove.add(offlineMapModel);
                offlineMapModel.setOfflineMapStatus(OfflineMapModel.OfflineMapStatus.READY);
                callback.onOfflineMapDeleted(offlineMapModel);
            }
        }
        downloadedOfflineMapModelList.removeAll(toRemove);
        setDownloadedOfflineMapModelList(downloadedOfflineMapModelList);
    }

    protected void displaySnackBar(String message) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show();
    }

}
