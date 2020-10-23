package org.smartregister.eusm.repository;

import android.content.ContentValues;
import android.location.Location;

import androidx.annotation.NonNull;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.domain.Geometry;
import org.smartregister.eusm.model.StructureDetail;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.StructureRepository;
import org.smartregister.repository.helper.MappingHelper;
import org.smartregister.util.P2PUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.smartregister.AllConstants.ROWID;

public class AppStructureRepository extends StructureRepository {

    private static final String CREATE_STRUCTURE_TABLE =
            "CREATE TABLE " + STRUCTURE_TABLE + " (" +
                    ID + " VARCHAR NOT NULL PRIMARY KEY," +
                    UUID + " VARCHAR , " +
                    PARENT_ID + " VARCHAR , " +
                    NAME + " VARCHAR , " +
                    "type VARCHAR , " +
                    SYNC_STATUS + " VARCHAR DEFAULT " + BaseRepository.TYPE_Synced + ", " +
                    LATITUDE + " FLOAT , " +
                    LONGITUDE + " FLOAT , " +
                    GEOJSON + " VARCHAR NOT NULL ) ";

    private static final String CREATE_STRUCTURE_PARENT_INDEX = "CREATE INDEX "
            + STRUCTURE_TABLE + "_" + PARENT_ID + "_ind ON " + STRUCTURE_TABLE + "(" + PARENT_ID + ")";

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
        contentValues.put("type", location.getProperties().getType());
        contentValues.put(SYNC_STATUS, location.getSyncStatus());
        contentValues.put(GEOJSON, gson.toJson(location));
        if (location.getGeometry().getType().equals(Geometry.GeometryType.POINT)) {
            contentValues.put(LONGITUDE, location.getGeometry().getCoordinates().get(0).getAsFloat());
            contentValues.put(LATITUDE, location.getGeometry().getCoordinates().get(1).getAsFloat());
        } else if (getHelper() != null) {
            android.location.Location center = getHelper().getCenter(gson.toJson(location.getGeometry()));
            contentValues.put(LATITUDE, center.getLatitude());
            contentValues.put(LONGITUDE, center.getLongitude());
        }
        getWritableDatabase().replace(getLocationTableName(), null, contentValues);
    }

    public List<StructureDetail> fetchStructureDetails(String locationId) {
        List<StructureDetail> structureDetails = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String columns[] = new String[]{
                STRUCTURE_TABLE + "." + "_id",
                "task" + "." + "business_status",
                "task" + "." + "status",
                "task" + "." + "sync_status",
                "task" + "." + "focus",
                STRUCTURE_TABLE + "." + "name",
                STRUCTURE_TABLE + "." + "type",
                STRUCTURE_TABLE + "." + "latitude",
                STRUCTURE_TABLE + "." + "longitude",
                STRUCTURE_TABLE + "." + "geojson"
        };

        String query = "SELECT " + StringUtils.join(columns, ",") + " from " + org.smartregister.repository.StructureRepository.STRUCTURE_TABLE +
                " structure join task on structure._id = task.for";
        if (StringUtils.isNotBlank(locationId)) {
            query = " where group_id = " + locationId;
        }

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                structureDetails.add(createStructureDetail(cursor));
            }
        }

        return structureDetails;
    }

    private StructureDetail createStructureDetail(@NonNull Cursor cursor) {
        String id = cursor.getString(0);
        String businessStatus = cursor.getString(1);
        String status = cursor.getString(2);
        String syncStatus = cursor.getString(3);
        String focus = cursor.getString(4);
        String name = cursor.getString(5);
        String type = cursor.getString(6);
        Float latitude = cursor.getFloat(7);
        Float longitude = cursor.getFloat(8);
        String geojson = cursor.getString(9);

        StructureDetail structureDetail = new StructureDetail();
        structureDetail.setStructureName(name);
        structureDetail.setStructureType(type);
        structureDetail.setTaskStatus(status);

        if (latitude != null && longitude != null) {
            Location location = new Location("b");
            location.setLatitude(latitude);
            location.setLongitude(longitude);

            Float distanceInMetres = Float.valueOf(new Random().nextInt(10));//Utils.distanceFromUserLocation(location);
            structureDetail.setDistance(distanceInMetres);
            structureDetail.setNearby(distanceInMetres <= AppConstants.NEARBY_DISTANCE_IN_METRES);
        }

        return structureDetail;
    }
}
