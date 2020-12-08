package org.smartregister.eusm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.eusm.R;
import org.smartregister.eusm.domain.TaskDetail;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.eusm.viewholder.GenericEmptyViewHolder;
import org.smartregister.eusm.viewholder.GenericTitleViewHolder;
import org.smartregister.eusm.viewholder.TaskRegisterViewHolder;
import org.smartregister.tasking.adapter.TaskRegisterAdapter;

import java.util.ArrayList;
import java.util.List;

public class EusmTaskRegisterAdapter extends TaskRegisterAdapter {

    private final View.OnClickListener onClickListener;
    private final Context context;
    private List<TaskDetail> taskDetails = new ArrayList<>();

    public EusmTaskRegisterAdapter(Context context, View.OnClickListener onClickListener) {
        super(context, onClickListener);
        this.context = context;
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
        TaskDetail taskDetail = taskDetails.get(position);
        if (taskDetail.isHeader()) {
            GenericTitleViewHolder titleViewHolder = (GenericTitleViewHolder) viewHolder;
            titleViewHolder.setTitle(taskDetail.getEntityName());
        } else if (taskDetail.isEmptyView()) {
            GenericEmptyViewHolder emptyViewHolder = (GenericEmptyViewHolder) viewHolder;
            emptyViewHolder.setTitle(taskDetail.getEntityName());
        } else {
            TaskRegisterViewHolder taskRegisterViewHolder = (TaskRegisterViewHolder) viewHolder;
            String productName = taskDetail.getEntityName();
            if (AppConstants.EncounterType.FIX_PROBLEM.equals(taskDetail.getTaskCode())) {
                productName = String.format(context.getString(R.string.fix_problem_prefix), productName);
            }
            taskRegisterViewHolder.setProductName(productName);
            taskRegisterViewHolder.setProductSerial(taskDetail);
            taskRegisterViewHolder.setProductImage(taskDetail);
            taskRegisterViewHolder.itemView.setOnClickListener(onClickListener);
            taskRegisterViewHolder.itemView.setTag(R.id.task_detail, taskDetail);
        }
    }

    @Override
    public int getItemCount() {
        return taskDetails.size();
    }

    public void setData(List<TaskDetail> taskDetails) {
        this.taskDetails = taskDetails;
        notifyDataSetChanged();
    }

    public void clearData() {
        if (taskDetails != null) {
            taskDetails.clear();
        }
    }

    @Override
    public int getItemViewType(int position) {
        TaskDetail taskDetail = taskDetails.get(position);
        if (taskDetail.isHeader())
            return 0;
        else if (taskDetail.isEmptyView())
            return 1;
        else
            return 2;
    }
}
