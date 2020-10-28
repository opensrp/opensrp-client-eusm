package org.smartregister.eusm.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.eusm.R;

public class GenericEmptyViewHolder extends RecyclerView.ViewHolder {

    private TextView titleView;

    public GenericEmptyViewHolder(@NonNull View itemView) {
        super(itemView);
        titleView = itemView.findViewById(R.id.txt_title);
    }

    public void setTitle(String title) {
        titleView.setText(title);
    }
}
