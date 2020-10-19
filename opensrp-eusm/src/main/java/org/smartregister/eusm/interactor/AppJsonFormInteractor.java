package org.smartregister.eusm.interactor;

import com.vijay.jsonwizard.interactors.JsonFormInteractor;

import org.smartregister.eusm.widget.GeoWidgetFactory;

/**
 * Created by samuelgithengi on 12/13/18.
 */
public class AppJsonFormInteractor extends JsonFormInteractor {


    private static final AppJsonFormInteractor INSTANCE = new AppJsonFormInteractor();

    private static final String GEOWIDGET = "geowidget";

    public static JsonFormInteractor getInstance() {
        return INSTANCE;
    }

    @Override
    protected void registerWidgets() {
        super.registerWidgets();
        map.put(GEOWIDGET, new GeoWidgetFactory(false));
    }

}
