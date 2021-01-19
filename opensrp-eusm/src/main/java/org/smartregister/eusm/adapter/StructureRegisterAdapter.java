package org.smartregister.eusm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.eusm.R;
import org.smartregister.eusm.domain.StructureDetail;
import org.smartregister.eusm.viewholder.GenericTitleViewHolder;
import org.smartregister.eusm.viewholder.StructureRegisterViewHolder;

import java.util.ArrayList;
import java.util.List;

public class StructureRegisterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;

    private final List<StructureDetail> structureDetailList = new ArrayList<>();

    private final View.OnClickListener registerActionHandler;

    public StructureRegisterAdapter(Context context, View.OnClickListener registerActionHandler) {
        this.context = context;
        this.registerActionHandler = registerActionHandler;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(context).inflate(R.layout.generic_title_row, parent, false);
            return new GenericTitleViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.structure_register_row, parent, false);
            return new StructureRegisterViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        StructureDetail structureDetail = structureDetailList.get(position);
        if (!structureDetail.isHeader()) {

            holder.itemView.setOnClickListener(registerActionHandler);
            holder.itemView.setTag(R.id.structure_detail, structureDetail);

            StructureRegisterViewHolder viewHolder = (StructureRegisterViewHolder) holder;
            viewHolder.setServicePointName(structureDetail.getEntityName());
            viewHolder.setServicePointIcon(structureDetail.getTaskStatus(), structureDetail.getStructureType());
            viewHolder.setServicePointType(structureDetail);
            viewHolder.setCommune(structureDetail.getCommune());
            viewHolder.setTaskStatus(structureDetail);
        } else {
            GenericTitleViewHolder viewHolder = (GenericTitleViewHolder) holder;
            viewHolder.setTitle(structureDetail.getEntityName());
        }
    }

    @Override
    public int getItemCount() {
        return structureDetailList.size();
    }

    @Override
    public int getItemViewType(int position) {
        StructureDetail structureTasksBody = structureDetailList.get(position);
        return structureTasksBody.isHeader() ? 0 : 1;
    }

    public void setData(List<StructureDetail> structureDetails) {
        structureDetailList.clear();
        structureDetailList.addAll(structureDetails);
        notifyDataSetChanged();
    }

    public void clearData() {
        if (structureDetailList != null) {
            structureDetailList.clear();
            notifyDataSetChanged();
        }
    }
}
