package org.smartregister.eusm.repository;

import android.content.ContentValues;
import android.location.Location;

import androidx.annotation.NonNull;

import net.sqlcipher.Cursor;
import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.domain.Geometry;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.domain.StructureDetail;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.eusm.util.AppUtils;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.StructureRepository;
import org.smartregister.repository.helper.MappingHelper;
import org.smartregister.util.P2PUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

import static org.smartregister.AllConstants.ROWID;
import static org.smartregister.AllConstants.TYPE;

public class AppStructureRepository extends StructureRepository {

    private static final String CREATE_STRUCTURE_TABLE =
            "CREATE TABLE " + STRUCTURE_TABLE + " (" +
                    ID + " VARCHAR NOT NULL PRIMARY KEY," +
                    UUID + " VARCHAR , " +
                    PARENT_ID + " VARCHAR , " +
                    NAME + " VARCHAR , " +
                    AppConstants.Column.Structure.TYPE + " VARCHAR , " +
                    SYNC_STATUS + " VARCHAR DEFAULT " + BaseRepository.TYPE_Synced + ", " +
                    LATITUDE + " FLOAT , " +
                    LONGITUDE + " FLOAT , " +
                    GEOJSON + " VARCHAR ) ";

    private static final String CREATE_STRUCTURE_PARENT_INDEX = "CREATE INDEX "
            + STRUCTURE_TABLE + "_" + PARENT_ID + "_ind ON " + STRUCTURE_TABLE + "(" + PARENT_ID + ")";
    private final int CURRENT_LIMIT = AppConstants.STRUCTURE_REGISTER_PAGE_SIZE;
    private MappingHelper helper;

    public static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_STRUCTURE_TABLE);
        database.execSQL(CREATE_STRUCTURE_PARENT_INDEX);
    }

    public MappingHelper getHelper() {
        return helper;
    }

    public void setHelper(MappingHelper helper) {
        this.helper = helper;
    }

    @Override
    public void addOrUpdate(org.smartregister.domain.Location location) {
        if (StringUtils.isBlank(location.getId()))
            throw new IllegalArgumentException("id not provided");
        ContentValues contentValues = new ContentValues();

        if (P2PUtil.checkIfExistsById(STRUCTURE_TABLE, location.getId(), getWritableDatabase())) {
            int maxRowId = P2PUtil.getMaxRowId(STRUCTURE_TABLE, getWritableDatabase());
            contentValues.put(ROWID, ++maxRowId);
        }

        contentValues.put(ID, location.getId());
        contentValues.put(UUID, location.getProperties().getUid());
        contentValues.put(PARENT_ID, location.getProperties().getParentId());
        contentValues.put(NAME, location.getProperties().getName());
        contentValues.put(AppConstants.Column.Structure.TYPE, location.getProperties().getType());
        contentValues.put(SYNC_STATUS, location.getSyncStatus());
        contentValues.put(GEOJSON, gson.toJson(location));
        if (location.getGeometry() != null) {
            if (Geometry.GeometryType.POINT.equals(location.getGeometry().getType())) {
                contentValues.put(LONGITUDE, location.getGeometry().getCoordinates().get(0).getAsFloat());
                contentValues.put(LATITUDE, location.getGeometry().getCoordinates().get(1).getAsFloat());
            } else if (getHelper() != null) {
                android.location.Location center = getHelper().getCenter(gson.toJson(location.getGeometry()));
                contentValues.put(LATITUDE, center.getLatitude());
                contentValues.put(LONGITUDE, center.getLongitude());
            }
        }
        getWritableDatabase().replace(getLocationTableName(), null, contentValues);
    }

    public int countOfStructures(String nameFilter) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String query = "SELECT count(" + STRUCTURE_TABLE + "._id" + ") from " + STRUCTURE_TABLE;
        if (StringUtils.isNotBlank(nameFilter)) {
            query += " where " + STRUCTURE_TABLE + "." + NAME + " like '%" + nameFilter + "%'";
        }
        query += " join task on task.for = " + StructureRepository.STRUCTURE_TABLE + "._id ";

        int count = 0;
        try (Cursor cursor = sqLiteDatabase.rawQuery(query, null)) {
            if (cursor != null && cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
        } catch (SQLException w) {
            Timber.e(w);
        }
        return count;
    }

    public List<StructureDetail> fetchStructureDetails(int pageNo, String locationParentId, String nameFilter) {
        return fetchStructureDetails(pageNo, locationParentId, nameFilter, false);
    }

    public List<StructureDetail> fetchStructureDetails(String locationParentId, String nameFilter) {
        return fetchStructureDetails(null, locationParentId, nameFilter, false);
    }

    public List<StructureDetail> fetchStructureDetails(Integer pageNo, String locationParentId,
                                                       String nameFilter, boolean isForMapping) {
        List<StructureDetail> structureDetails = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String[] columns = new String[]{
                STRUCTURE_TABLE + "." + "_id",
                STRUCTURE_TABLE + "." + "name",
                STRUCTURE_TABLE + "." + "type",
                STRUCTURE_TABLE + "." + "latitude",
                STRUCTURE_TABLE + "." + "longitude",
                STRUCTURE_TABLE + "." + "geojson as structureGeoJson",
                LOCATION_TABLE + "." + "name as locationName",
                LOCATION_TABLE + "." + "parent_id as locationParentId",
                "(((?  - longitude)*(?  - longitude)) + ((?  - latitude)*(?  - latitude))) as dist",
                "case \n" +
                        "\twhen (sum(task.status = 'COMPLETED')*1.0/sum(task.status= 'READY')*1.0) = 0.0 \n" +
                        "\t\tthen sum(task.status= 'READY')\n" +
                        "\twhen (sum(task.status = 'COMPLETED')*1.0/sum(task.status= 'READY')*1.0) > 0.0 \n" +
                        "\t\tthen 'in_progress'\n" +
                        "\telse \n" +
                        "\t    'completed' \n" +
                        "\tend as taskStatus",
                "count(task._id) as numOfTasks"
        };

        String query = "SELECT " + StringUtils.join(columns, ",") + " from " + StructureRepository.STRUCTURE_TABLE
                + " join task on task.location = " + StructureRepository.STRUCTURE_TABLE + "._id "
                + " join location on location._id = " + StructureRepository.STRUCTURE_TABLE + ".parent_id ";


        Location location = EusmApplication.getInstance().getUserLocation();
        String[] args = new String[]{
                String.valueOf(location.getLongitude()),
                String.valueOf(location.getLongitude()),
                String.valueOf(location.getLatitude()),
                String.valueOf(location.getLatitude()),
                locationParentId};
        query += " where locationParentId = ? ";

        if (StringUtils.isNotBlank(nameFilter)) {
            query += " and name like '%" + nameFilter + "%'";
        }

        query += " group by " + STRUCTURE_TABLE + "." + "_id" + " order by case when dist is null then 1 else 0 end, dist"
                + (pageNo == null ? "" : " LIMIT " + CURRENT_LIMIT + " OFFSET " + (pageNo * CURRENT_LIMIT));

        try (Cursor cursor = sqLiteDatabase.rawQuery(query, args)) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    structureDetails.add(createStructureDetail(cursor, isForMapping));
                }
            }
        } catch (SQLException e) {
            Timber.e(e);
        }
        return structureDetails;
    }

    private StructureDetail createStructureDetail(@NonNull Cursor cursor, boolean isForMapping) {
        String id = cursor.getString(cursor.getColumnIndex(ID));
        String name = cursor.getString(cursor.getColumnIndex(NAME));
        String type = cursor.getString(cursor.getColumnIndex(TYPE));
        String latitude = cursor.getString(cursor.getColumnIndex(LATITUDE));
        String longitude = cursor.getString(cursor.getColumnIndex(LONGITUDE));
        String taskStatus = cursor.getString(cursor.getColumnIndex("taskStatus"));
        String commune = cursor.getString(cursor.getColumnIndex("locationName"));
        String geojson = cursor.getString(cursor.getColumnIndex("structureGeoJson"));

        StructureDetail structureDetail = new StructureDetail();
        structureDetail.setStructureId(id);
        structureDetail.setStructureName(name);
        structureDetail.setStructureType(type);
        structureDetail.setTaskStatus(taskStatus);
        structureDetail.setNumOfTasks(cursor.getString(cursor.getColumnIndex("numOfTasks")));
        structureDetail.setCommune(commune);

        if (StringUtils.isNotBlank(latitude) && StringUtils.isNotBlank(longitude)) {
            Location location = new Location("b");
            location.setLatitude(Double.parseDouble(latitude));
            location.setLongitude(Double.parseDouble(longitude));

            Float distanceInMetres = AppUtils.distanceFromUserLocation(location);
            if (distanceInMetres != null) {
                structureDetail.setDistance(distanceInMetres);
                structureDetail.setDistanceMeta(formatDistance(distanceInMetres));
                structureDetail.setNearby(distanceInMetres <= AppConstants.NEARBY_DISTANCE_IN_METRES);
            }

            if (isForMapping)
                structureDetail.setGeojson(gson.fromJson(geojson, org.smartregister.domain.Location.class));

        }

        return structureDetail;
    }

    private String formatDistance(Float distanceInMetres) {
        String result = "";
        if (distanceInMetres >= 0.0F && distanceInMetres <= 0.9F) {
            result = String.format(Locale.ENGLISH, "%.1f m", distanceInMetres);
        } else if (distanceInMetres >= 1000F) {
            result = String.format(Locale.ENGLISH, "%.1f km", (distanceInMetres / 1000));
        } else {
            result = String.format(Locale.ENGLISH, "%.1f m", distanceInMetres);
        }
        return result;
    }
}
