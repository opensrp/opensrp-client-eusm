package org.smartregister.eusm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.domain.Task;
import org.smartregister.eusm.R;
import org.smartregister.eusm.model.CardDetails;
import org.smartregister.eusm.model.TaskDetails;
import org.smartregister.eusm.viewholder.TaskRegisterViewHolder;

import java.util.ArrayList;
import java.util.List;

public class TaskRegisterAdapter extends RecyclerView.Adapter<TaskRegisterViewHolder> {

    private List<TaskDetails> taskDetails = new ArrayList<>();

    private final Context context;

    private final View.OnClickListener registerActionHandler;

    public TaskRegisterAdapter(Context context, View.OnClickListener registerActionHandler) {
        this.context = context;
        this.registerActionHandler = registerActionHandler;
    }

    @NonNull
    @Override
    public TaskRegisterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_register_row, parent, false);
        return new TaskRegisterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskRegisterViewHolder viewHolder, int position) {
        TaskDetails task = taskDetails.get(position);
        Float distance = task.getDistanceFromUser();
        String name = task.getStructureName();
        String action = null;
        boolean hasIcon = false;

        viewHolder.setTaskName(name);
        CardDetails cardDetails = new CardDetails(task.getBusinessStatus());
        if (Task.TaskStatus.COMPLETED.name().equals(task.getTaskStatus())) {
//            if (task.getBusinessStatus() != null) {
//                action = CardDetailsUtil.getTranslatedBusinessStatus(task.getBusinessStatus()).replaceAll(" ", "\n");
//            }
//            CardDetailsUtil.formatCardDetails(cardDetails);
        }
        viewHolder.setTaskAction(action, task, cardDetails, registerActionHandler);
        viewHolder.setDistanceFromStructure(distance, task.isDistanceFromCenter());
        viewHolder.setTaskDetails(task.getBusinessStatus(), task.getTaskDetails());
        if (hasIcon) {
            viewHolder.hideDistanceFromStructure();
        } else {
            viewHolder.hideIcon();
        }

        if (StringUtils.isNotEmpty(task.getHouseNumber())) {
            viewHolder.showHouseNumber();
//            viewHolder.setHouseNumber(context.getString(R.string.numero_sign) + " " + task.getHouseNumber());
        } else {
            viewHolder.hideHouseNumber();
        }
    }

    @Override
    public int getItemCount() {
        return taskDetails.size();
    }

    public void setTaskDetails(List<TaskDetails> taskDetails) {
        this.taskDetails = taskDetails;
        notifyDataSetChanged();
    }
}
