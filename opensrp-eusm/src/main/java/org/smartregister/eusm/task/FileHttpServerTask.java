package org.smartregister.eusm.task;

import android.content.Context;
import android.os.AsyncTask;

import org.smartregister.eusm.util.AppConstants;
import org.smartregister.eusm.util.OfflineMapHelper;

import java.lang.ref.WeakReference;

/**
 * Created by Richard Kareko on 2/4/20.
 */

public class FileHttpServerTask extends AsyncTask<Void, Void, Void> {

    private final WeakReference<Context> context;

    public FileHttpServerTask(Context context) {
        this.context = new WeakReference<>(context);
    }

    @Override
    protected Void doInBackground(Void... params) {
        OfflineMapHelper.initializeFileHTTPServer(context.get(), AppConstants.DG_ID_PLACEHOLDER);
        return null;
    }
}
