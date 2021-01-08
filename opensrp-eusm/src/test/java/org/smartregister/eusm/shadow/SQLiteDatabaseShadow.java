package org.smartregister.eusm.shadow;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

@Implements(SQLiteDatabase.class)
public class SQLiteDatabaseShadow {

    @Implementation
    public static synchronized void loadLibs(Context context) {
        //do nothing
    }
}
