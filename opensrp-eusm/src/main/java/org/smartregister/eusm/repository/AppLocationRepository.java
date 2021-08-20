package org.smartregister.eusm.repository;

import android.content.ContentValues;

import net.sqlcipher.Cursor;
import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.smartregister.domain.Location;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.LocationRepository;

import java.util.HashSet;
import java.util.Set;

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
        } catch (SQLException e) {
            Timber.e(e);
        }
        return null;
    }

    public Set<Location> getLocationByNameAndGeoLevel(Set<String> names, String level) {
        Set<Location> locations = new HashSet<>();

        if (names == null || names.isEmpty())
            return locations;
        try (Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + getLocationTableName() +
                " WHERE " + StringUtils.repeat(NAME + " =?", " OR ", names.size()) + " AND " + GEOGRAPHICAL_LEVEL + "=?", ArrayUtils.add(names.toArray(new String[0]), level))) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    locations.add(readCursor(cursor));
                }
            }
        } catch (SQLException e) {
            Timber.e(e);
        }
        return locations;
    }
}
