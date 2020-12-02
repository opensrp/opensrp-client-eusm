package org.smartregister.eusm.interactor;

import com.vijay.jsonwizard.interactors.JsonFormInteractor;


/**
 * Created by samuelgithengi on 12/13/18.
 */
public class AppJsonFormInteractor extends JsonFormInteractor {

    private static final AppJsonFormInteractor INSTANCE = new AppJsonFormInteractor();

    private static final String GEOWIDGET = "geowidget";

    public static JsonFormInteractor getInstance() {
        return INSTANCE;
    }

}
