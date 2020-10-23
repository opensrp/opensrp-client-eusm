package org.smartregister.eusm.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.eusm.R;
import org.smartregister.eusm.util.AppConstants;

public class StructureItemViewHolder extends StructureRegisterViewHolder {

    private TextView servicePointNameView;
    private TextView taskStatusView;
    private TextView servicePointTypeView;
    private TextView communeView;
    private ImageView servicePointIconView;

    public StructureItemViewHolder(@NonNull View itemView) {
        super(itemView);
        servicePointNameView = itemView.findViewById(R.id.txt_service_point_name);
        taskStatusView = itemView.findViewById(R.id.txt_task_status);
        servicePointTypeView = itemView.findViewById(R.id.txt_service_point_type);
        communeView = itemView.findViewById(R.id.txt_service_point_commune);
        servicePointIconView = itemView.findViewById(R.id.img_service_point_icon);
    }

    public void setServicePointName(String servicePointName) {
        this.servicePointNameView.setText(servicePointName);
    }

    public void setServicePointIconView(@Nullable String taskStatus, @NonNull String serviceType) {
        this.servicePointIconView.setImageDrawable(ResourcesCompat.getDrawable(servicePointIconView.getResources(), R.drawable.ic_hq_sp, servicePointIconView.getContext().getTheme()));
        if (StringUtils.isNotBlank(taskStatus)) {
            int color = getColorByTaskStatus(taskStatus);
            this.servicePointIconView.setColorFilter(ContextCompat.getColor(servicePointIconView.getContext(), color));
        }
    }

//    private int getDrawableByTaskType(@NonNull String serviceType) {
//        int image = R.drawable.text_gray;
//        if (serviceType.equalsIgnoreCase(AppConstants.ServicePointType.EPP)) {
//            image = R.color.task_completed;
//        } else if (serviceType.equalsIgnoreCase(AppConstants.TaskStatus.IN_PROGRESS)) {
//            image = R.color.task_in_progress;
//        }
//        return image;
//    }

    private int getColorByTaskStatus(@NonNull String taskStatus) {
        int colorId = R.color.text_gray;
        if (taskStatus.equalsIgnoreCase(AppConstants.TaskStatus.COMPLETED)) {
            colorId = R.color.task_completed;
        } else if (taskStatus.equalsIgnoreCase(AppConstants.TaskStatus.IN_PROGRESS)) {
            colorId = R.color.task_in_progress;
        }
        return colorId;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatusView.setText(taskStatus);
    }

    public void setServicePointType(String servicePointType) {
        this.servicePointTypeView.setText(servicePointType);
    }

    public void setCommune(String commune) {
        this.communeView.setText(commune);
    }
}
