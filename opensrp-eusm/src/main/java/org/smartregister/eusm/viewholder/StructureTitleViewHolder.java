package org.smartregister.eusm.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.smartregister.eusm.R;

public class StructureTitleViewHolder extends StructureRegisterViewHolder {
    private TextView servicePointNameView;

    public StructureTitleViewHolder(@NonNull View itemView) {
        super(itemView);
        servicePointNameView = itemView.findViewById(R.id.txt_service_point_name);
    }

    public void setServicePointName(String servicePointName) {
        servicePointNameView.setText(servicePointName);
    }
}
