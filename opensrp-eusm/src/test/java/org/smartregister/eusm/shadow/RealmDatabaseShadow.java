package org.smartregister.eusm.shadow;

import android.content.Context;


import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

import io.ona.kujaku.data.realm.RealmDatabase;

@Implements(RealmDatabase.class)
public class RealmDatabaseShadow {

    @Implementation
    public static synchronized void init(Context context) {
        //do nothing
    }
}
