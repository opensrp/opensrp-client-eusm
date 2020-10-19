package org.smartregister.eusm.shadow;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.smartregister.eusm.view.NavigationDrawerView;

/**
 * Created by samuelgithengi on 1/27/20.
 */
@Implements(NavigationDrawerView.class)
public class DrawerMenuViewShadow {

    @Implementation
    public void initializeDrawerLayout() {//Do nothing
    }

    @Implementation
    public void onResume() {//Do nothing
    }

}
