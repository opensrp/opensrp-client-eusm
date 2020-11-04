package org.smartregister.eusm.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.domain.FetchStatus;
import org.smartregister.domain.SyncProgress;
import org.smartregister.domain.Task;
import org.smartregister.eusm.R;
import org.smartregister.eusm.contract.BaseDrawerContract;
import org.smartregister.eusm.contract.HomeActivityContract;
import org.smartregister.eusm.helper.GenericTextWatcher;
import org.smartregister.eusm.model.CardDetails;
import org.smartregister.eusm.model.TaskFilterParams;
import org.smartregister.eusm.presenter.HomeActivityPresenter;
import org.smartregister.eusm.util.AlertDialogUtils;
import org.smartregister.eusm.util.AppConstants.Action;
import org.smartregister.eusm.util.TestDataUtils;
import org.smartregister.eusm.util.AppUtils;
import org.smartregister.eusm.view.NavigationDrawerView;
import org.smartregister.receiver.SyncProgressBroadcastReceiver;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;

import io.ona.kujaku.callbacks.OnLocationComponentInitializedCallback;
import io.ona.kujaku.utils.Constants;
import timber.log.Timber;

import static org.smartregister.eusm.util.AppConstants.CONFIGURATION.LOCAL_SYNC_DONE;
import static org.smartregister.eusm.util.AppConstants.CONFIGURATION.UPDATE_LOCATION_BUFFER_RADIUS;
import static org.smartregister.eusm.util.AppConstants.DatabaseKeys.STRUCTURE_ID;
import static org.smartregister.eusm.util.AppConstants.DatabaseKeys.TASK_ID;
import static org.smartregister.eusm.util.AppConstants.Filter.FILTER_SORT_PARAMS;
import static org.smartregister.eusm.util.AppConstants.JSON_FORM_PARAM_JSON;
import static org.smartregister.eusm.util.AppConstants.RequestCode.REQUEST_CODE_FAMILY_PROFILE;
import static org.smartregister.eusm.util.AppConstants.RequestCode.REQUEST_CODE_FILTER_TASKS;
import static org.smartregister.eusm.util.AppConstants.RequestCode.REQUEST_CODE_GET_JSON;
import static org.smartregister.eusm.util.AppConstants.RequestCode.REQUEST_CODE_TASK_LISTS;
import static org.smartregister.eusm.util.AppUtils.getPixelsPerDPI;

public class HomeActivity extends BaseMapActivity implements HomeActivityContract.HomeActivityView,
        View.OnClickListener, SyncStatusBroadcastReceiver.SyncStatusListener,
        OnLocationComponentInitializedCallback, SyncProgressBroadcastReceiver.SyncProgressListener {

    private final RefreshGeoWidgetReceiver refreshGeowidgetReceiver = new RefreshGeoWidgetReceiver();
    private final SyncProgressBroadcastReceiver syncProgressBroadcastReceiver = new SyncProgressBroadcastReceiver(this);
    private HomeActivityPresenter homeActivityPresenter;
    private View rootView;
    private ProgressDialog progressDialog;
    private boolean hasRequestedLocation;

    private Snackbar syncProgressSnackbar;

    private BaseDrawerContract.View drawerView;

    private EditText searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeActivityPresenter = new HomeActivityPresenter(this, drawerView.getPresenter());
    }

    @Override
    protected void setUpViews() {
        super.setUpViews();
        new TestDataUtils().populateTestData();
        rootView = findViewById(R.id.content_frame);

        drawerView = new NavigationDrawerView(this);

        drawerView.initializeDrawerLayout();

        initializeProgressDialog();

        findViewById(R.id.drawerMenu)
                .setOnClickListener(this);

        TextView servicePointBtnRegisterTextView = findViewById(R.id.btn_structure_register);
        servicePointBtnRegisterTextView.setText(getString(R.string.list));
        servicePointBtnRegisterTextView.setOnClickListener(this);

        initializeToolbar();

        syncProgressSnackbar = Snackbar.make(rootView, getString(org.smartregister.R.string.syncing), Snackbar.LENGTH_INDEFINITE);

        //EusmApplication.getInstance().setUserLocation(getUserCurrentLocation());
    }

    private void initializeToolbar() {
        searchView = findViewById(R.id.edt_search);
        searchView.setSingleLine();
        searchView.addTextChangedListener(new GenericTextWatcher(searchView) {
            @Override
            public void handleAfterTextChanged(TextView textView, String value) {
                homeActivityPresenter.searchTasks(value);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_structure_register) {
            homeActivityPresenter.onOpenServicePointRegister();
        } else if (v.getId() == R.id.drawerMenu) {
            drawerView.openDrawerLayout();
        }
    }

    @Override
    public void openServicePointsActivity(TaskFilterParams filterParams) {
    }

    @Override
    public void openServicePointRegister(TaskFilterParams filterParams) {
        Intent intent = new Intent(this, StructureRegisterActivity.class);
        startActivity(intent);
    }

    @Override
    public void displayNotification(int title, int message, Object... formatArgs) {
        AlertDialogUtils.displayNotification(this, title, message, formatArgs);
    }

    @Override
    public void displayNotification(String message) {
        AlertDialogUtils.displayNotification(this, message);
    }

    @Override
    public void openCardView(CardDetails cardDetails) {
    }

    @Override
    public void startJsonForm(JSONObject form) {
        getJsonFormUtils().startJsonForm(form, this);
    }

    @Override
    public void displayToast(@StringRes int resourceId) {
        Toast.makeText(this, resourceId, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_GET_JSON && resultCode == RESULT_OK && data.hasExtra(JSON_FORM_PARAM_JSON)) {
            String json = data.getStringExtra(JSON_FORM_PARAM_JSON);
            Timber.d(json);
            homeActivityPresenter.saveJsonForm(json);
        } else if (requestCode == Constants.RequestCode.LOCATION_SETTINGS && hasRequestedLocation) {
//            if (resultCode == RESULT_OK) {
//                homeActivityPresenter.getLocationPresenter().waitForUserLocation();
//            } else if (resultCode == RESULT_CANCELED) {
//                homeActivityPresenter.getLocationPresenter().onGetUserLocationFailed();
//            }
            hasRequestedLocation = false;
        } else if (requestCode == REQUEST_CODE_FAMILY_PROFILE && resultCode == RESULT_OK && data.hasExtra(STRUCTURE_ID)) {
            String structureId = data.getStringExtra(STRUCTURE_ID);
            Task task = (Task) data.getSerializableExtra(TASK_ID);
            homeActivityPresenter.resetFeatureTasks(structureId, task);
        } else if (requestCode == REQUEST_CODE_FILTER_TASKS && resultCode == RESULT_OK && data.hasExtra(FILTER_SORT_PARAMS)) {
            TaskFilterParams filterParams = (TaskFilterParams) data.getSerializableExtra(FILTER_SORT_PARAMS);
            homeActivityPresenter.filterTasks(filterParams);
        } else if (requestCode == REQUEST_CODE_TASK_LISTS && resultCode == RESULT_OK && data.hasExtra(FILTER_SORT_PARAMS)) {
            TaskFilterParams filterParams = (TaskFilterParams) data.getSerializableExtra(FILTER_SORT_PARAMS);
            homeActivityPresenter.setTaskFilterParams(filterParams);
        }
    }

    private void initializeProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(R.string.fetching_structures_title);
        progressDialog.setMessage(getString(R.string.fetching_structures_message));
    }

    @Override
    public Location getUserCurrentLocation() {
        return super.getUserCurrentLocation();
    }

    @Override
    public void showProgressDialog(@StringRes int title, @StringRes int message) {
        showProgressDialog(title, message, new Object[0]);
    }

    @Override
    public void showProgressDialog(@StringRes int title, @StringRes int message, Object... formatArgs) {
        if (progressDialog != null) {
            progressDialog.setTitle(title);
            if (formatArgs.length == 0) {
                progressDialog.setMessage(getString(message));
            } else {
                progressDialog.setMessage(getString(message, formatArgs));
            }
            progressDialog.show();
        }
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void requestUserLocation() {
//        getKujakuMapView().setWarmGps(true, getString(R.string.location_service_disabled), "getString(R.string.location_services_disabled_spray)");
        hasRequestedLocation = true;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onDestroy() {
        homeActivityPresenter = null;
        super.onDestroy();
    }

    @Override
    public void onSyncStart() {
        if (SyncStatusBroadcastReceiver.getInstance().isSyncing()) {
            syncProgressSnackbar.show();
        }
        toggleProgressBarView(true);
    }

    @Override
    public void onSyncInProgress(FetchStatus fetchStatus) {
        if (FetchStatus.fetched.equals(fetchStatus)) {
            syncProgressSnackbar.show();
            return;
        }
        syncProgressSnackbar.dismiss();
        if (fetchStatus.equals(FetchStatus.fetchedFailed)) {
            Snackbar.make(rootView, org.smartregister.R.string.sync_failed, Snackbar.LENGTH_LONG).show();
        } else if (fetchStatus.equals(FetchStatus.nothingFetched)) {
            Snackbar.make(rootView, org.smartregister.R.string.sync_complete, Snackbar.LENGTH_LONG).show();
        } else if (fetchStatus.equals(FetchStatus.noConnection)) {
            Snackbar.make(rootView, org.smartregister.R.string.sync_failed_no_internet, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSyncComplete(FetchStatus fetchStatus) {
        onSyncInProgress(fetchStatus);
        //Check sync status and Update UI to show sync status
        drawerView.checkSynced();
        // revert to sync status view
        toggleProgressBarView(false);
    }

    @Override
    public void onResume() {
        super.onResume();

        SyncStatusBroadcastReceiver.getInstance().addSyncStatusListener(this);

        IntentFilter filter = new IntentFilter(Action.STRUCTURE_TASK_SYNCED);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(refreshGeowidgetReceiver, filter);

        IntentFilter syncProgressFilter = new IntentFilter(AllConstants.SyncProgressConstants.ACTION_SYNC_PROGRESS);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(syncProgressBroadcastReceiver, syncProgressFilter);

        drawerView.onResume();

        // homeActivityPresenter.onResume();

        if (SyncStatusBroadcastReceiver.getInstance().isSyncing()) {
            syncProgressSnackbar.show();
            toggleProgressBarView(true);
        }
    }

    @Override
    public void onPause() {
        SyncStatusBroadcastReceiver.getInstance().removeSyncStatusListener(this);

        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(refreshGeowidgetReceiver);

        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(syncProgressBroadcastReceiver);

        // EusmApplication.getInstance().setMyLocationComponentEnabled(appMapHelper.isMyLocationComponentActive(this, myLocationButton));
        super.onPause();
    }

    @Override
    public void onDrawerClosed() {
        homeActivityPresenter.onDrawerClosed();
    }

    @Override
    public AppCompatActivity getActivity() {
        return this;
    }


    @Override
    public void displayMarkStructureInactiveDialog() {
//        AlertDialogUtils.displayNotificationWithCallback(this, R.string.mark_location_inactive,
//                R.string.confirm_mark_location_inactive, R.string.confirm, R.string.cancel, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (which == BUTTON_POSITIVE)
//                            listTaskPresenter.onMarkStructureInactiveConfirmed();
//                        dialog.dismiss();
//                    }
//                });
    }

    @Override
    public void setSearchPhrase(String searchPhrase) {
        searchView.setText(searchPhrase);
    }

    @Override
    public void toggleProgressBarView(boolean syncing) {
        drawerView.toggleProgressBarView(syncing);
    }

    @Override
    public void setOperationalArea(String operationalArea) {
        drawerView.setOperationalArea(operationalArea);
    }

    @Override
    public void onSyncProgress(SyncProgress syncProgress) {
        int progress = syncProgress.getPercentageSynced();
        String entity = AppUtils.getSyncEntityString(syncProgress.getSyncEntity());
        ProgressBar syncProgressBar = findViewById(R.id.sync_progress_bar);
        TextView syncProgressBarLabel = findViewById(R.id.sync_progress_bar_label);
        String labelText = String.format(getResources().getString(R.string.progressBarLabel), entity, progress);
        syncProgressBar.setProgress(progress);
        syncProgressBarLabel.setText(labelText);
    }

    private class RefreshGeoWidgetReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            boolean localSyncDone;
            if (extras != null && extras.getBoolean(UPDATE_LOCATION_BUFFER_RADIUS)) {
                float bufferRadius = getLocationBuffer() / getPixelsPerDPI(getResources());
                getMapView().setLocationBufferRadius(bufferRadius);
            }
            localSyncDone = extras != null && extras.getBoolean(LOCAL_SYNC_DONE);
            homeActivityPresenter.refreshStructures(localSyncDone);
        }
    }
}
