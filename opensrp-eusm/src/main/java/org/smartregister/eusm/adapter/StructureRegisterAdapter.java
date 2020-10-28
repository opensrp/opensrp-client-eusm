package org.smartregister.eusm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.eusm.R;
import org.smartregister.eusm.model.StructureDetail;
import org.smartregister.eusm.viewholder.StructureRegisterViewHolder;
import org.smartregister.eusm.viewholder.GenericTitleViewHolder;

import java.util.ArrayList;
import java.util.List;

public class StructureRegisterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;

    private List<StructureDetail> structureDetailList = new ArrayList<>();

    private View.OnClickListener registerActionHandler;

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
        StructureDetail structureTasksBody = structureDetailList.get(position);
        if (!structureTasksBody.isHeader()) {

            holder.itemView.setOnClickListener(registerActionHandler);
            holder.itemView.setTag(R.id.structure_detail, structureTasksBody);

            StructureRegisterViewHolder viewHolder = (StructureRegisterViewHolder) holder;
            viewHolder.setServicePointName(structureTasksBody.getStructureName());
            viewHolder.setServicePointIcon(structureTasksBody.getTaskStatus(), structureTasksBody.getStructureType());
            viewHolder.setServicePointType(structureTasksBody);
//            viewHolder.setCommune("commune");
            viewHolder.setTaskStatus(structureTasksBody.getTaskStatus());
        } else {
            GenericTitleViewHolder viewHolder = (GenericTitleViewHolder) holder;
            viewHolder.setTitle(structureTasksBody.getStructureName());
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
        structureDetailList.addAll(structureDetails);
        notifyDataSetChanged();
    }

    public void clearData() {
        if (structureDetailList != null) {
            structureDetailList.clear();
        }
    }
}
