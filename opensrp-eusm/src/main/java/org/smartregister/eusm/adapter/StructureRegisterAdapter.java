package org.smartregister.eusm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.eusm.R;
import org.smartregister.eusm.model.StructureDetail;
import org.smartregister.eusm.viewholder.StructureItemViewHolder;
import org.smartregister.eusm.viewholder.StructureRegisterViewHolder;
import org.smartregister.eusm.viewholder.StructureTitleViewHolder;

import java.util.ArrayList;
import java.util.List;

public class StructureRegisterAdapter extends RecyclerView.Adapter<StructureRegisterViewHolder> implements Filterable {

    private final Context context;
    private List<StructureDetail> structureDetailList = new ArrayList<>();

    private List<StructureDetail> structureDetailListOrig = new ArrayList<>();

    public StructureRegisterAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public StructureRegisterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(context).inflate(R.layout.structure_title_row, parent, false);
            return new StructureTitleViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.structure_item_row, parent, false);
            return new StructureItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull StructureRegisterViewHolder holder, int position) {
        StructureDetail structureTasksBody = structureDetailList.get(position);
        if (!structureTasksBody.isHeader()) {
            StructureItemViewHolder viewHolder = (StructureItemViewHolder) holder;
            viewHolder.setServicePointName(structureTasksBody.getStructureName());
            viewHolder.setServicePointIconView(structureTasksBody.getTaskStatus());
            viewHolder.setServicePointType(String.format(context.getString(R.string.distance_from_structure), structureTasksBody.getStructureType(), structureTasksBody.getDistance()));
//            viewHolder.setCommune("commune");
            viewHolder.setTaskStatus(structureTasksBody.getTaskStatus());
        } else {
            StructureTitleViewHolder viewHolder = (StructureTitleViewHolder) holder;
            viewHolder.setServicePointName(structureTasksBody.getStructureName());
        }
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<StructureDetail> filteredItems = null;
                if (constraint.length() == 0) {
                    filteredItems = structureDetailListOrig;
                } else {
                    filteredItems = getFilteredResults(constraint.toString());
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredItems;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                structureDetailList = (List<StructureDetail>) results.values;
                notifyDataSetChanged();
            }

            protected List<StructureDetail> getFilteredResults(String constraint) {
                List<StructureDetail> results = new ArrayList<>();

                for (StructureDetail item : structureDetailListOrig) {
                    if (item.getStructureName().toLowerCase().contains(constraint.toLowerCase()) || item.isHeader()) {
                        results.add(item);
                    }
                }
                return results;
            }
        };
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
        structureDetailListOrig.addAll(structureDetails);
        notifyDataSetChanged();
    }
}
