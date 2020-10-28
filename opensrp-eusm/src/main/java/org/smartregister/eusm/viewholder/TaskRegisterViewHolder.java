package org.smartregister.eusm.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.eusm.R;
import org.smartregister.eusm.model.StructureTaskDetail;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class TaskRegisterViewHolder extends RecyclerView.ViewHolder {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("M/dd", Locale.getDefault());

    private final Context context;

    private TextView productNameView;

    private ImageView productImageView;

    private ImageView rectangleOverlayImageView;

    private ImageView checkedOverlayImageView;


    private TextView productSerialView;

    public TaskRegisterViewHolder(@NonNull View itemView) {
        super(itemView);
        context = itemView.getContext();
        productNameView = itemView.findViewById(R.id.txt_product_name);
        productImageView = itemView.findViewById(R.id.img_product_image);
        productSerialView = itemView.findViewById(R.id.txt_product_serial);
        rectangleOverlayImageView = itemView.findViewById(R.id.img_rectangle_overlay);
        checkedOverlayImageView = itemView.findViewById(R.id.img_checked_overlay);
    }

    public void setProductImage(@Nullable StructureTaskDetail structureTaskDetail) {
        this.productImageView.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.login_logo, context.getTheme()));
        if (structureTaskDetail.isChecked()) {
            checkedOverlayImageView.setVisibility(View.VISIBLE);
            rectangleOverlayImageView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * holds text for serial, and quantity if available and date checked
     *
     * @param structureTaskDetail
     */
    public void setProductSerial(@NonNull StructureTaskDetail structureTaskDetail) {

        if (StringUtils.isNotBlank(structureTaskDetail.getProductSerial()) && !structureTaskDetail.isNonProductTask()) {
            String stringTemplate;
            String result = "";
            if (StringUtils.isNotBlank(structureTaskDetail.getQuantity())) {
                stringTemplate = context.getString(R.string.product_serial_n_quantity);
                result = String.format(stringTemplate, structureTaskDetail.getQuantity(), structureTaskDetail.getProductSerial());
            }

            if (structureTaskDetail.getDateChecked() != null) {
                stringTemplate = context.getString(R.string.product_serial_n_date_checked);
                result = String.format(stringTemplate, structureTaskDetail.getProductSerial(), "date checked");
            }
            if (StringUtils.isBlank(result)) {
                stringTemplate = context.getString(R.string.product_serial);
                result = String.format(stringTemplate, structureTaskDetail.getProductSerial());
            }
            this.productSerialView.setText(result);
        } else {
            this.productSerialView.setVisibility(View.GONE);
        }
    }

    /**
     * holds text fot name and some description
     *
     * @param productName
     */
    public void setProductName(@NonNull String productName) {
        this.productNameView.setText(productName);
    }
}
