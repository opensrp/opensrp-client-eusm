package org.smartregister.eusm.viewholder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.eusm.R;
import org.smartregister.eusm.domain.TaskDetail;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.util.FileUtilities;

public class TaskRegisterViewHolder extends RecyclerView.ViewHolder {

    private final Context context;

    private final TextView productNameView;

    private final ImageView productImageView;

    private final ImageView rectangleOverlayImageView;

    private final ImageView checkedOverlayImageView;

    private final ImageView statusOverlayImageView;

    private final TextView productSerialView;

    public TaskRegisterViewHolder(@NonNull View itemView) {
        super(itemView);
        context = itemView.getContext();
        productNameView = itemView.findViewById(R.id.txt_product_name);
        productImageView = itemView.findViewById(R.id.img_product_image);
        productSerialView = itemView.findViewById(R.id.txt_product_serial);
        rectangleOverlayImageView = itemView.findViewById(R.id.img_rectangle_overlay);
        checkedOverlayImageView = itemView.findViewById(R.id.img_checked_overlay);
        statusOverlayImageView = itemView.findViewById(R.id.img_status_overlay);
    }

    public void setProductImage(@Nullable TaskDetail taskDetail) {
        this.productImageView.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.default_product_photo_thumbnail, context.getTheme()));
        statusOverlayImageView.setVisibility(View.GONE);

        if (StringUtils.isNotBlank(taskDetail.getProductImage())) {
            Bitmap bitmap = FileUtilities.retrieveStaticImageFromDisk(taskDetail.getProductImage());
            this.productImageView.setImageBitmap(bitmap);
        }

        if (taskDetail.isChecked()) {
            checkedOverlayImageView.setVisibility(View.VISIBLE);
            rectangleOverlayImageView.setVisibility(View.VISIBLE);
            if (AppConstants.BusinessStatus.HAS_PROBLEM.equals(taskDetail.getBusinessStatus())) {
                statusOverlayImageView.setVisibility(View.VISIBLE);
                if (taskDetail.isChecked()) {
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 55);
                    layoutParams.gravity = Gravity.END | Gravity.BOTTOM;
                    layoutParams.bottomMargin = 50;
                    layoutParams.rightMargin = 10;
                    statusOverlayImageView.setScaleType(ImageView.ScaleType.FIT_END);
                    statusOverlayImageView.setLayoutParams(layoutParams);
                }
            }
        } else {
            checkedOverlayImageView.setVisibility(View.GONE);
            rectangleOverlayImageView.setVisibility(View.GONE);
            if (AppConstants.EncounterType.FIX_PROBLEM.equals(taskDetail.getTaskCode())
                    || AppConstants.TaskCode.FIX_PROBLEM_CONSULT_BENEFICIARIES.equalsIgnoreCase(taskDetail.getTaskCode())) {
                statusOverlayImageView.setVisibility(View.VISIBLE);
            }
        }

        if (taskDetail.isNonProductTask()) {
            if (AppConstants.NonProductTasks.SERVICE_POINT_CHECK.equalsIgnoreCase(taskDetail.getEntityName())) {
                this.productImageView.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.service_point_check_thumbnail, context.getTheme()));
            } else if (AppConstants.NonProductTasks.RECORD_GPS.equalsIgnoreCase(taskDetail.getEntityName())) {
                this.productImageView.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.record_gps_thumbnail, context.getTheme()));
            } else if (AppConstants.NonProductTasks.CONSULT_BENEFICIARIES.equalsIgnoreCase(taskDetail.getEntityName())) {
                this.productImageView.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.service_point_check_thumbnail, context.getTheme()));
            } else if (AppConstants.NonProductTasks.WAREHOUSE_CHECK.equalsIgnoreCase(taskDetail.getEntityName())) {
                this.productImageView.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.service_point_check_thumbnail, context.getTheme()));
            } else if (AppConstants.TaskCode.FIX_PROBLEM_CONSULT_BENEFICIARIES.equalsIgnoreCase(taskDetail.getEntityName())) {
                this.productImageView.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.service_point_check_thumbnail, context.getTheme()));
            }
        }
    }

    /**
     * holds text for serial, and quantity if available and date checked
     *
     * @param taskDetail
     */
    public void setProductSerial(@NonNull TaskDetail taskDetail) {
        if (!taskDetail.isNonProductTask()) {
            String stringTemplate;
            stringTemplate = context.getString(R.string.product_serial_n_quantity);
            String result = String.format(stringTemplate,
                    StringUtils.isNotBlank(taskDetail.getQuantity()) ? context.getString(R.string.product_quantity_text) + taskDetail.getQuantity() : "",
                    StringUtils.isNotBlank(taskDetail.getProductSerial()) ? context.getString(R.string.product_serial_text) + taskDetail.getProductSerial() : "",
                    "");

            this.productSerialView.setText(result);
            if (taskDetail.isChecked()) {
                this.productSerialView.setTextColor(context.getResources().getColor(R.color.text_checked));
            } else {
                this.productSerialView.setTextColor(context.getResources().getColor(R.color.black));
            }
            this.productSerialView.setVisibility(View.VISIBLE);
        } else {
            this.productSerialView.setVisibility(View.GONE);
        }

    }

    /**
     * holds text fot name and some description
     *
     * @param productName
     */
    public void setProductName(@NonNull String productName, boolean isChecked) {
        this.productNameView.setText(productName);
        if (isChecked) {
            this.productNameView.setTextColor(context.getResources().getColor(R.color.text_checked));
            this.productNameView.setTypeface(null, Typeface.NORMAL);
        } else {
            this.productNameView.setTypeface(null, Typeface.BOLD);
            this.productNameView.setTextColor(context.getResources().getColor(R.color.black));
        }
    }
}
