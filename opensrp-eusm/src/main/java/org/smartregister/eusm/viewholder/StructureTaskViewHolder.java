package org.smartregister.eusm.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.eusm.R;
import org.smartregister.eusm.model.StructureTaskDetails;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by samuelgithengi on 4/11/19.
 */
public class StructureTaskViewHolder extends RecyclerView.ViewHolder {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("M/dd", Locale.getDefault());
    private final Context context;

    private final TextView nameTextView;

    private final TextView actionTextView;

    private ImageView viewEditImageView;

    private TextView lastEditedTextView;

    private final TextView detailsTextView;

    private ImageView viewUndoImageView;

    public StructureTaskViewHolder(@NonNull View itemView) {
        super(itemView);
        context = itemView.getContext();
        nameTextView = itemView.findViewById(R.id.task_name);
        actionTextView = itemView.findViewById(R.id.task_action);
//        viewEditImageView = itemView.findViewById(R.id.view_edit);
//        lastEditedTextView = itemView.findViewById(R.id.last_edited);
        detailsTextView = itemView.findViewById(R.id.task_details);
//        viewUndoImageView = itemView.findViewById(R.id.view_undo);
    }

    public void setTaskName(String name) {
        nameTextView.setText(name);
        detailsTextView.setVisibility(View.GONE);
    }

    public void setTaskName(String taskName, String taskCode) {
        nameTextView.setText(taskName);
        detailsTextView.setText(taskCode);
        detailsTextView.setVisibility(View.VISIBLE);

    }

    public void setTaskAction(StructureTaskDetails taskDetails, View.OnClickListener onClickListener) {
//        if (!AppConstants.BusinessStatus.NOT_VISITED.equals(taskDetails.getBusinessStatus())) {
//            if (AppConstants.Intervention.CASE_CONFIRMATION.equals(taskDetails.getTaskCode())) {
//                actionTextView.setText(context.getResources().getString(R.string.index_case_confirmed));
//            } else if (StringUtils.isNotBlank(taskDetails.getPersonTested())
//                    && AppConstants.Intervention.BLOOD_SCREENING.equals(taskDetails.getTaskCode())
//                    && AppConstants.BusinessStatus.COMPLETE.equals(taskDetails.getBusinessStatus())) {
//                String screening = context.getString(R.string.yes).equals(taskDetails.getPersonTested()) ?
//                        context.getString(R.string.tested) : context.getString(R.string.not_tested);
//                actionTextView.setText(screening);
//            } else {
//                actionTextView.setText(CardDetailsUtil.getTranslatedBusinessStatus(taskDetails.getBusinessStatus()));
//            }
//            actionTextView.setBackground(null);
//            CardDetails cardDetails = new CardDetails(taskDetails.getBusinessStatus());
//            CardDetailsUtil.formatCardDetails(cardDetails);
//            actionTextView.setTextColor(context.getResources().getColor(cardDetails.getStatusColor()));
//        } else {
//            actionTextView.setText(taskDetails.getTaskAction());
//            actionTextView.setBackground(context.getResources().getDrawable(R.drawable.structure_task_action_bg));
//            actionTextView.setTextColor(context.getResources().getColor(R.color.task_not_done));
//        }
//
//        if (AppConstants.BusinessStatus.COMPLETE.equals(taskDetails.getBusinessStatus()) &&
//                (AppConstants.Intervention.BEDNET_DISTRIBUTION.equals(taskDetails.getTaskCode()) || AppConstants.Intervention.BLOOD_SCREENING.equals(taskDetails.getTaskCode()))) {
//
//            viewEditImageView.setVisibility(View.VISIBLE);
//            setClickHandler(onClickListener, taskDetails, viewEditImageView);
//            viewUndoImageView.setVisibility(View.VISIBLE);
//            setClickHandler(onClickListener, taskDetails, viewUndoImageView);
//            Date lastEdited = taskDetails.getLastEdited();
//            if (lastEdited != null) {
//                lastEditedTextView.setVisibility(View.VISIBLE);
//                lastEditedTextView.setText(context.getString(R.string.last_edited, dateFormat.format(lastEdited)));
//                actionTextView.setPadding(0, 0, 0, 0);
//            } else {
//                lastEditedTextView.setVisibility(View.GONE);
//            }
//        } else {
//            viewEditImageView.setVisibility(View.GONE);
//            lastEditedTextView.setVisibility(View.GONE);
//            viewUndoImageView.setVisibility(View.GONE);
//        }
//        setClickHandler(onClickListener, taskDetails, actionTextView);

    }

    private void setClickHandler(View.OnClickListener onClickListener, StructureTaskDetails taskDetails, View view) {
        view.setOnClickListener(onClickListener);
        view.setTag(R.id.task_details, taskDetails);
    }


}
