package org.smartregister.eusm.viewholder;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.eusm.R;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.config.ServicePointType;
import org.smartregister.eusm.model.StructureDetail;
import org.smartregister.eusm.util.AppConstants;

public class StructureRegisterViewHolder extends RecyclerView.ViewHolder {

    private TextView servicePointNameView;
    private TextView taskStatusView;
    private TextView servicePointTypeView;
    private TextView communeView;
    private ImageView servicePointIconView;
    private Context context;
    private View gpsUnknownView;

    public StructureRegisterViewHolder(@NonNull View itemView) {
        super(itemView);
        context = itemView.getContext();
        servicePointNameView = itemView.findViewById(R.id.txt_service_point_name);
        taskStatusView = itemView.findViewById(R.id.txt_task_status);
        servicePointTypeView = itemView.findViewById(R.id.txt_service_point_type);
        communeView = itemView.findViewById(R.id.txt_service_point_commune);
        servicePointIconView = itemView.findViewById(R.id.img_service_point_icon);
        gpsUnknownView = itemView.findViewById(R.id.gps_unknown_view);
    }

    public void setServicePointName(String servicePointName) {
        this.servicePointNameView.setText(servicePointName);
    }

    public void setServicePointIcon(@Nullable String taskStatus, @NonNull String serviceType) {
        this.servicePointIconView.setImageDrawable(ResourcesCompat.getDrawable(servicePointIconView.getResources(), getDrawableByTaskType(serviceType), context.getTheme()));
        if (StringUtils.isNotBlank(taskStatus)) {
            int color = getColorByTaskStatus(taskStatus);
            this.servicePointIconView.setColorFilter(ContextCompat.getColor(context, color));
        }
    }

    private Integer getDrawableByTaskType(@NonNull String serviceType) {
        ServicePointType servicePointType = EusmApplication.getInstance().getServicePointKeyToType().get(serviceType.toLowerCase().replaceAll(" ", "_"));
        if (servicePointType == null) {
            return R.drawable.ic_health_sp;
        } else {
            return servicePointType.drawableId;
        }
    }

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

    public void setCommune(String commune) {
        this.communeView.setText(commune);
    }

    public void setServicePointType(StructureDetail structureTasksBody) {
        if (StringUtils.isNotBlank(structureTasksBody.getDistanceMeta())) {
            gpsUnknownView.setVisibility(View.GONE);
            this.servicePointTypeView.setText(String.format(context.getString(R.string.distance_from_structure), structureTasksBody.getStructureType(), structureTasksBody.getDistanceMeta()));
        } else {
            gpsUnknownView.setVisibility(View.VISIBLE);

            ImageView imageViewUnlistedLocation = gpsUnknownView.findViewById(R.id.img_service_point_gps_unknown);
            imageViewUnlistedLocation.setColorFilter(ContextCompat.getColor(context, R.color.task_in_progress));

            TextView txtUnlistedLocation = gpsUnknownView.findViewById(R.id.txt_service_point_gps_unknown);
            txtUnlistedLocation.setTextColor(Color.BLACK);

            this.servicePointTypeView.setText(String.format(context.getString(R.string.unlisted_distance_from_structure), structureTasksBody.getStructureType()));
        }
    }
}
