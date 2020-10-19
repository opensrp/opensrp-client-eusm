package org.smartregister.eusm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.eusm.R;
import org.smartregister.eusm.model.ServicePoint;
import org.smartregister.eusm.viewholder.ServicePointItemViewHolder;
import org.smartregister.eusm.viewholder.ServicePointTitleViewHolder;

import java.util.List;

public class ServicePointRegisterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ServicePoint> servicePoints;
    private final Context context;

    public ServicePointRegisterAdapter(List<ServicePoint> servicePoints, Context context) {
        this.servicePoints = servicePoints;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(context).inflate(R.layout.service_point_title_row, parent, false);
            return new ServicePointTitleViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.service_point_item_row, parent, false);
            return new ServicePointItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ServicePoint servicePoint = servicePoints.get(position);
        if (holder instanceof ServicePointItemViewHolder) {
//            View view = LayoutInflater.from(context).inflate(R.layout.service_point_title_row, parent, false);
//            return new ServicePointTitleViewHolder(view);
        } else {
//            View view = LayoutInflater.from(context).inflate(R.layout.service_point_item_row, parent, false);
//            return new ServicePointItemViewHolder(view);
        }
    }

    @Override
    public int getItemCount() {
        return servicePoints.size();
    }

    @Override
    public int getItemViewType(int position) {
        ServicePoint servicePoint = servicePoints.get(position);
        return servicePoint.isHeader() ? 0 : 1;
    }
}
