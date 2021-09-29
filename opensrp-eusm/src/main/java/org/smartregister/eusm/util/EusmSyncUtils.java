package org.smartregister.eusm.util;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import org.smartregister.AllConstants;
import org.smartregister.eusm.R;
import org.smartregister.util.SyncUtils;

import static org.smartregister.AllConstants.ACCOUNT_DISABLED;

public class EusmSyncUtils extends SyncUtils {

    private Context context;

    public EusmSyncUtils(Context context) {
        super(context);
        this.context = context;
    }

    @NonNull
    @Override
    protected Intent getLogoutUserIntent(int logoutMessage) {
        Intent intent = super.getLogoutUserIntent(logoutMessage);
        intent.removeExtra(ACCOUNT_DISABLED);
        intent.putExtra(AllConstants.INTENT_KEY.DIALOG_MESSAGE, context.getString(logoutMessage));
        intent.putExtra(AllConstants.INTENT_KEY.DIALOG_TITLE, context.getString(R.string.new_assignment_dialog_title));
        return intent;
    }
}
