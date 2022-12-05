package org.smartregister.eusm.view;

import android.content.Context;

import com.unnamed.b.atv.model.TreeNode;
import com.vijay.jsonwizard.customviews.TreeViewDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.eusm.util.AppUtils;
import org.smartregister.eusm.viewholder.TreeSelectionItemViewHolder;
import org.smartregister.tasking.util.PreferencesUtil;

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
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.init(nodes, defaultValue, value, isSelectionMode);
    }

    private Set<String> populateOperationalRegions(JSONArray nodes) throws Exception {
        ArrayList<String> regions = AppUtils.getRegionsForDistricts(nodes,
                PreferencesUtil.getInstance().getCurrentOperationalAreas(),
                true);
        return new HashSet<>(regions);
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
