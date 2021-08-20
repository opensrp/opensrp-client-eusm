package org.smartregister.eusm.viewholder;

import android.content.Context;
import android.view.View;

import com.vijay.jsonwizard.customviews.SelectableItemHolder;

public class TreeSelectionItemViewHolder extends SelectableItemHolder {

    public TreeSelectionItemViewHolder(Context context, String levelLabel) {
        super(context, levelLabel);
    }

    @Override
    public void toggleSelectionMode(boolean editModeEnabled) {
        nodeSelector.setVisibility(mNode.isSelectable() && editModeEnabled ? View.VISIBLE : View.GONE);
        nodeSelector.setChecked(mNode.isSelected());
    }
}
