package org.smartregister.eusm.repository;

import android.content.ContentValues;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.domain.Location;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.LocationRepository;

import timber.log.Timber;

public class AppLocationRepository extends LocationRepository {

    protected static final String GEOGRAPHICAL_LEVEL = "geographical_level";

    private static final String CREATE_LOCATION_TABLE =
            "CREATE TABLE " + LOCATION_TABLE + " (" +
                    ID + " VARCHAR NOT NULL PRIMARY KEY," +
                    UUID + " VARCHAR , " +
                    PARENT_ID + " VARCHAR , " +
                    NAME + " VARCHAR , " +
                    GEOGRAPHICAL_LEVEL + " VARCHAR , " +
                    SYNC_STATUS + " VARCHAR DEFAULT " + BaseRepository.TYPE_Synced + ", " +
                    GEOJSON + " VARCHAR NOT NULL ) ";

    private static final String CREATE_LOCATION_NAME_INDEX = "CREATE INDEX "
            + LOCATION_TABLE + "_" + NAME + "_ind ON " + LOCATION_TABLE + "(" + NAME + ")";

    public static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_LOCATION_TABLE);
        database.execSQL(CREATE_LOCATION_NAME_INDEX);
    }

    public void addOrUpdate(Location location) {
        if (StringUtils.isBlank(location.getId()))
            throw new IllegalArgumentException("id not provided");
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, location.getId());
        contentValues.put(UUID, location.getProperties().getUid());
        contentValues.put(PARENT_ID, location.getProperties().getParentId());
        contentValues.put(NAME, location.getProperties().getName());
        contentValues.put(GEOGRAPHICAL_LEVEL, location.getProperties().getGeographicLevel());
        contentValues.put(GEOJSON, gson.toJson(location));
        contentValues.put(SYNC_STATUS, location.getSyncStatus());
        getWritableDatabase().replace(getLocationTableName(), null, contentValues);
    }

    public Location getLocationByNameAndGeoLevel(String name, String level) {
        try (Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + getLocationTableName() +
                " WHERE " + NAME + " =? AND " + GEOGRAPHICAL_LEVEL + "=?", new String[]{name, level})) {
            if (cursor.moveToFirst()) {
                return readCursor(cursor);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }
}
