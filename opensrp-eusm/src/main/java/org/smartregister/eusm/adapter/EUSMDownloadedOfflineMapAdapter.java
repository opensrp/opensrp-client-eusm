package org.smartregister.eusm.adapter;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import org.smartregister.tasking.adapter.DownloadedOfflineMapAdapter;
import org.smartregister.tasking.model.OfflineMapModel;
import org.smartregister.tasking.viewholder.DownloadedOfflineMapViewHolder;

import java.util.ArrayList;
import java.util.List;

public class EUSMDownloadedOfflineMapAdapter extends DownloadedOfflineMapAdapter {

    private List<OfflineMapModel> offlineMapModels = new ArrayList<>();

    public EUSMDownloadedOfflineMapAdapter(Context context, View.OnClickListener offlineMapClickHandler) {
        super(context, offlineMapClickHandler);
    }

    @Override
    public void setOfflineMapModels(List<OfflineMapModel> offlineMapModels) {
        this.offlineMapModels = offlineMapModels;
        super.setOfflineMapModels(offlineMapModels);
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadedOfflineMapViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);
        OfflineMapModel offlineMapModel = offlineMapModels.get(position);

        switch (offlineMapModel.getOfflineMapStatus()) {
            case READY:
            case DOWNLOADED:
                viewHolder.checkCheckBox(false);
                viewHolder.enableCheckBox(true);
                break;
            case SELECTED_FOR_DOWNLOAD:
                viewHolder.checkCheckBox(true);
                break;
            case DOWNLOAD_STARTED:
                viewHolder.checkCheckBox(true);
                viewHolder.enableCheckBox(false);
                break;
            default:
                break;

        }
    }
}
