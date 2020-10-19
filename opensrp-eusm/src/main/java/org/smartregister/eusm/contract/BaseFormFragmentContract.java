package org.smartregister.eusm.contract;

import org.json.JSONObject;
import org.smartregister.domain.Location;
import org.smartregister.eusm.model.BaseTaskDetails;
import org.smartregister.eusm.util.AppJsonFormUtils;

/**
 * Created by samuelgithengi on 4/18/19.
 */
public interface BaseFormFragmentContract {

    interface Presenter extends UserLocationContract.UserLocationCallback, PasswordRequestCallback {

        void onStructureFound(Location structure, BaseTaskDetails details);
    }

    interface View extends UserLocationContract.UserLocationView {
        void displayToast(String format);

        AppJsonFormUtils getJsonFormUtils();

        void startForm(JSONObject formJSON);

        void displayError(int title, int message);
    }

    interface Interactor {

        void findNumberOfMembers(String structureId, JSONObject formJSON);

//        void findMemberDetails(String structureId, JSONObject formJSON);
//
//        void findSprayDetails(String interventionType, String structureId, JSONObject formJSON);
    }
}
