package org.smartregister.eusm.view;

import android.content.Context;

import com.unnamed.b.atv.model.TreeNode;
import com.vijay.jsonwizard.customviews.TreeViewDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.eusm.viewholder.TreeSelectionItemViewHolder;
import org.smartregister.tasking.util.PreferencesUtil;
import org.smartregister.tasking.util.TaskingConstants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class EusmTreeViewDialog extends TreeViewDialog {

    private Set<String> operationalRegions;

    public EusmTreeViewDialog(Context context, int theme, JSONArray structure,
                              ArrayList<String> defaultValue, ArrayList<String> value,
                              boolean isSelectionMode) throws JSONException {

        super(context, theme, structure, defaultValue, value, isSelectionMode);
    }

    @Override
    public void init(JSONArray nodes, ArrayList<String> defaultValue, ArrayList<String> value, boolean isSelectionMode) throws JSONException {
        try {
            // Get selected regions from districts
            this.operationalRegions = populateOperationalRegions(nodes);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        super.init(nodes, defaultValue, value, isSelectionMode);
    }

    private Set<String> populateOperationalRegions(JSONArray nodes) throws JSONException {
        Set<String> opRegions = new HashSet<>();
        Set<String> operationalAreas = PreferencesUtil.getInstance().getCurrentOperationalAreas();
        JSONArray regions = nodes.getJSONObject(0).getJSONArray(AppConstants.JsonForm.NODES);
        for (int i = 0; i < regions.length(); i++) {
            JSONObject location = regions.getJSONObject(i);
            String locName = location.getString(TaskingConstants.CONFIGURATION.KEY);
            if (location.has(AppConstants.JsonForm.NODES)) {
                JSONArray childNodes = location.getJSONArray(AppConstants.JsonForm.NODES);
                for (int j = 0; j < childNodes.length(); j++) {
                    if (operationalAreas.contains(childNodes.getJSONObject(j).getString(TaskingConstants.CONFIGURATION.KEY))) {
                        opRegions.add(locName);
                        break;
                    }
                }
                // Hide District labels from hierarchy
                location.put(AppConstants.JsonForm.NODES, new JSONArray());
            }
        }
        return opRegions;
    }

    @Override
    public TreeNode.BaseNodeViewHolder<String> getNodeViewHolder(Context context, JSONObject structure) {
        return new TreeSelectionItemViewHolder(context, structure.optString(KEY_LEVEL, ""));
    }

    @Override
    public void updateTreeNode(int level, TreeNode curNode, TreeNode parentNode) {
        curNode.setSelectable(level == 2);
        boolean isSelected = operationalRegions.contains(curNode.getValue().toString());
        curNode.setSelected(isSelected);
    }

    @Override
    public boolean shouldExpandAllNodes() {
        return true;
    }
}
