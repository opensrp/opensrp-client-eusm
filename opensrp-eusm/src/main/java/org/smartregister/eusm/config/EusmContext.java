package org.smartregister.eusm.config;

import org.smartregister.Context;
import org.smartregister.eusm.repository.AppLocationRepository;
import org.smartregister.repository.LocationRepository;

public class EusmContext extends Context {

    private AppLocationRepository appLocationRepository;

    private static Context context = new EusmContext();

    public static Context getInstance() {
        if (context == null) {
            context = new EusmContext();
        }
        return context;
    }

    @Override
    public LocationRepository getLocationRepository() {
        if (appLocationRepository == null) {
            appLocationRepository = new AppLocationRepository();
        }
        return appLocationRepository;
    }
}
