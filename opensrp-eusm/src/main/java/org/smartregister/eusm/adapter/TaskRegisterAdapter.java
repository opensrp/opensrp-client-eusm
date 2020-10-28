package org.smartregister.eusm.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.eusm.R;
import org.smartregister.eusm.model.StructureTaskDetail;
import org.smartregister.eusm.viewholder.GenericEmptyViewHolder;
import org.smartregister.eusm.viewholder.GenericTitleViewHolder;
import org.smartregister.eusm.viewholder.TaskRegisterViewHolder;

import java.util.ArrayList;
import java.util.List;

public class TaskRegisterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final View.OnClickListener onClickListener;

    private List<StructureTaskDetail> structureTaskDetails = new ArrayList<>();

    public TaskRegisterAdapter(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.generic_title_row, viewGroup, false);
            return new GenericTitleViewHolder(view);
        } else if (viewType == 1) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.generic_empty_row, viewGroup, false);
            return new GenericEmptyViewHolder(view);
        } else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.task_register_row, viewGroup, false);
            return new TaskRegisterViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        StructureTaskDetail structureTaskDetail = structureTaskDetails.get(position);
        if (structureTaskDetail.isHeader()) {
            GenericTitleViewHolder titleViewHolder = (GenericTitleViewHolder) viewHolder;
            titleViewHolder.setTitle(structureTaskDetail.getProductName());
        } else if (structureTaskDetail.isEmptyView()) {
            GenericEmptyViewHolder emptyViewHolder = (GenericEmptyViewHolder) viewHolder;
            emptyViewHolder.setTitle(structureTaskDetail.getProductName());
        } else {
            TaskRegisterViewHolder taskRegisterViewHolder = (TaskRegisterViewHolder) viewHolder;
            taskRegisterViewHolder.setProductName(structureTaskDetail.getProductName());
            taskRegisterViewHolder.setProductSerial(structureTaskDetail);
            taskRegisterViewHolder.setProductImage(structureTaskDetail);
            taskRegisterViewHolder.itemView.setOnClickListener(onClickListener);
            taskRegisterViewHolder.itemView.setTag(R.id.structure_task_detail, structureTaskDetail);
        }
    }

    @Override
    public int getItemCount() {
        return structureTaskDetails.size();
    }

    public void setData(List<StructureTaskDetail> structureTaskDetails) {
        this.structureTaskDetails = structureTaskDetails;
        notifyDataSetChanged();
    }

    public void clearData() {
        if (structureTaskDetails != null) {
            structureTaskDetails.clear();
        }
    }

    @Override
    public int getItemViewType(int position) {
        StructureTaskDetail structureTaskDetail = structureTaskDetails.get(position);
        if (structureTaskDetail.isHeader())
            return 0;
        else if (structureTaskDetail.isEmptyView())
            return 1;
        else
            return 2;
    }
}
