package org.smartregister.eusm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.domain.Task;
import org.smartregister.eusm.R;
import org.smartregister.eusm.model.StructureTaskDetails;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.eusm.viewholder.StructureTaskViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StructureTaskAdapter extends RecyclerView.Adapter<StructureTaskViewHolder> {

    private Context context;

    private List<StructureTaskDetails> taskDetailsList = new ArrayList<>();

    private final View.OnClickListener onClickListener;

    public StructureTaskAdapter(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public StructureTaskViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        context = viewGroup.getContext();
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.structure_task_row, viewGroup, false);
        return new StructureTaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StructureTaskViewHolder viewHolder, int position) {
        StructureTaskDetails taskDetails = taskDetailsList.get(position);
        if (AppConstants.Intervention.MDA_DISPENSE.equals(taskDetails.getTaskCode()) ||
                AppConstants.Intervention.MDA_ADHERENCE.equals(taskDetails.getTaskCode())) {
            viewHolder.setTaskName(taskDetails.getTaskName(), taskDetails.getTaskCode());
        } else {
            viewHolder.setTaskName(taskDetails.getTaskName());
        }
        viewHolder.setTaskAction(taskDetails, onClickListener);

    }

    @Override
    public int getItemCount() {
        return taskDetailsList.size();
    }

    public void setTaskDetailsList(List<StructureTaskDetails> taskDetailsList) {
        this.taskDetailsList = taskDetailsList;
        notifyDataSetChanged();
    }

    private int updateTaskStatus(String taskID, Task.TaskStatus taskStatus, String businessStatus) {
        int position = taskDetailsList.indexOf(new StructureTaskDetails(taskID));
        if (position != -1) {
            StructureTaskDetails taskDetails = taskDetailsList.get(position);
            taskDetails.setBusinessStatus(businessStatus);
            taskDetails.setTaskStatus(taskStatus.name());
        }
        return position;
    }

    public void updateTask(String taskID, Task.TaskStatus taskStatus, String businessStatus) {
        int position = updateTaskStatus(taskID, taskStatus, businessStatus);
        if (position != -1) {
            notifyItemChanged(position);
        }
    }

    public void updateTasks(String taskID, Task.TaskStatus taskStatus, String businessStatus, Set<Task> removedTasks) {
        updateTaskStatus(taskID, taskStatus, businessStatus);
        for (Task task : removedTasks) {
            taskDetailsList.remove(new StructureTaskDetails(task.getIdentifier()));
        }
        notifyDataSetChanged();
    }
}
