package org.smartregister.eusm.presenter;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.joda.time.DateTime;
import org.json.JSONObject;
import org.smartregister.domain.Location;
import org.smartregister.eusm.contract.BaseFormFragmentContract;
import org.smartregister.eusm.interactor.BaseFormFragmentInteractor;
import org.smartregister.eusm.model.BaseTaskDetails;
import org.smartregister.eusm.repository.AppMappingHelper;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.eusm.util.AppJsonFormUtils;
import org.smartregister.eusm.util.PreferencesUtil;
import org.smartregister.eusm.util.Utils;
import org.smartregister.util.DateTimeTypeConverter;

import java.lang.ref.WeakReference;

import io.ona.kujaku.listeners.BaseLocationListener;

/**
 * Created by samuelgithengi on 4/18/19.
 */
public class BaseFormFragmentPresenter extends BaseLocationListener implements BaseFormFragmentContract.Presenter {

    private final WeakReference<BaseFormFragmentContract.View> view;
    protected AppMappingHelper mappingHelper;
    protected Gson gson = new GsonBuilder().setDateFormat(AppConstants.DateFormat.EVENT_DATE_FORMAT_Z)
            .registerTypeAdapter(DateTime.class, new DateTimeTypeConverter()).create();
    private AlertDialog passwordDialog;
    private Location structure;
    private BaseTaskDetails taskDetails;
    private final Context context;
    private final BaseFormFragmentInteractor interactor;
    private final PreferencesUtil prefsUtil;
    private final AppJsonFormUtils jsonFormUtils = new AppJsonFormUtils();

    protected BaseFormFragmentPresenter(BaseFormFragmentContract.View view, Context context) {
        this.context = context;
        this.view = new WeakReference<>(view);
        mappingHelper = new AppMappingHelper();
        interactor = new BaseFormFragmentInteractor(this);
        prefsUtil = PreferencesUtil.getInstance();
    }

    protected boolean validateFarStructures() {
        return Utils.validateFarStructures();
    }

    private void validateUserLocation() {
//        android.location.Location location = getView().getUserCurrentLocation();
//        if (location == null) {
//            locationPresenter.requestUserLocation();
//        } else {
//            locationPresenter.onGetUserLocation(location);
//        }
    }

    @Override
    public void onPasswordVerified() {
        onLocationValidated();
    }

    @Override
    public void onLocationValidated() {
//        if (!AppConstants.Intervention.REGISTER_FAMILY.equals(getTaskDetails().getTaskCode())) {
//            String formName = getView().getJsonFormUtils().getFormName(null, taskDetails.getTaskCode());
//            if (StringUtils.isBlank(formName)) {
//                getView().displayError(R.string.opening_form_title, R.string.form_not_found);
//            } else {
//                JSONObject formJSON = getView().getJsonFormUtils().getFormJSON(context, formName, taskDetails, structure);
//                if (AppConstants.Intervention.BEDNET_DISTRIBUTION.equals(taskDetails.getTaskCode())) {
//                    interactor.findNumberOfMembers(taskDetails.getTaskEntity(), formJSON);
//                    return;
//                } else if (AppConstants.Intervention.MDA_DISPENSE.equals(taskDetails.getTaskCode()) || AppConstants.Intervention.MDA_ADHERENCE.equals(taskDetails.getTaskCode())) {
//                    jsonFormUtils.populateServerOptions(EusmApplication.getInstance().getServerConfigs(), AppConstants.CONFIGURATION.MDA_CATCHMENT_AREAS, jsonFormUtils.getFields(formJSON).get(AppConstants.JsonForm.CATCHMENT_AREA), prefsUtil.getCurrentDistrict());
//                    getView().startForm(formJSON);
//                } else {
//                    getView().startForm(formJSON);
//                }
//            }
//        }
//        getView().hideProgressDialog();
    }

    public void showBasicForm(String formName) {
        JSONObject formJSON = getView().getJsonFormUtils().getFormJSON(context, formName, null, null);
        jsonFormUtils.populateFormWithServerOptions(formName, formJSON);
        getView().startForm(formJSON);
    }

    @Override
    public LatLng getTargetCoordinates() {
        android.location.Location center = mappingHelper.getCenter(gson.toJson(structure.getGeometry()));
        return new LatLng(center.getLatitude(), center.getLongitude());
    }

    @Override
    public void requestUserPassword() {
        if (passwordDialog != null) {
            passwordDialog.show();
        }
    }

    protected BaseFormFragmentContract.View getView() {
        return view.get();
    }

    @Override
    public void onStructureFound(Location structure, BaseTaskDetails details) {
        this.structure = structure;
        this.taskDetails = details;
        if (AppConstants.Intervention.IRS.equals(details.getTaskCode()) || AppConstants.Intervention.MOSQUITO_COLLECTION.equals(details.getTaskCode()) ||
                AppConstants.Intervention.LARVAL_DIPPING.equals(details.getTaskCode()) || AppConstants.Intervention.REGISTER_FAMILY.equals(details.getTaskCode()) ||
                AppConstants.Intervention.BEDNET_DISTRIBUTION.equals(details.getTaskCode()) || AppConstants.Intervention.CASE_CONFIRMATION.equals(details.getTaskCode()) ||
                AppConstants.Intervention.BLOOD_SCREENING.equals(details.getTaskCode())) {
            if (validateFarStructures()) {
                validateUserLocation();
            } else {
                onLocationValidated();
            }
        } else {
            onLocationValidated();
        }
    }

    public BaseTaskDetails getTaskDetails() {
        return taskDetails;
    }

    public void setTaskDetails(BaseTaskDetails taskDetails) {
        this.taskDetails = taskDetails;
    }

    public Location getStructure() {
        return structure;
    }
}
