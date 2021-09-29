package org.smartregister.eusm.repository;

import android.content.ContentValues;
import android.location.Location;
import android.util.Pair;

import androidx.annotation.NonNull;

import net.sqlcipher.Cursor;
import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.ArrayUtils;
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
import java.util.Set;

import timber.log.Timber;

import static org.smartregister.AllConstants.ROWID;
import static org.smartregister.AllConstants.TYPE;

public class AppStructureRepository extends StructureRepository {

    protected static final String CREATE_STRUCTURE_TABLE =
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

    protected static final String CREATE_STRUCTURE_PARENT_INDEX = "CREATE INDEX "
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
                Location center = getHelper().getCenter(gson.toJson(location.getGeometry()));
                contentValues.put(LATITUDE, center.getLatitude());
                contentValues.put(LONGITUDE, center.getLongitude());
            }
        }
        getWritableDatabase().replace(getLocationTableName(), null, contentValues);
    }

    public int countOfStructures(String nameFilter, Set<String> locationParentIds, String planId) {
        int count = 0;

        if (locationParentIds == null || locationParentIds.isEmpty())
            return count;
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String query = "SELECT count(DISTINCT structure._id) from " + STRUCTURE_TABLE;

        String[] args = ArrayUtils.addAll(locationParentIds.toArray(new String[0]), planId);

        query += " join task on task.for = " + StructureRepository.STRUCTURE_TABLE + "._id ";

        query += " join location on location._id = " + StructureRepository.STRUCTURE_TABLE + ".parent_id ";

        query += " where " + "(" + StringUtils.repeat("location.parent_id = ? ", " OR ", locationParentIds.size()) + ")" + " AND task.plan_id = ?";

        if (StringUtils.isNotBlank(nameFilter)) {
            args = ArrayUtils.addAll(args, "%" + nameFilter + "%");

            query += " and " + STRUCTURE_TABLE + "." + NAME + " like ? ";
        }

        try (Cursor cursor = sqLiteDatabase.rawQuery(query, args)) {
            if (cursor != null && cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
        } catch (SQLException w) {
            Timber.e(w);
        }
        return count;
    }

    public List<StructureDetail> fetchStructureDetails(int pageNo, Set<String> locationParentIds, String nameFilter, String planId) {
        return fetchStructureDetails(pageNo, locationParentIds, nameFilter, false, planId);
    }

    public List<StructureDetail> fetchStructureDetails(Integer pageNo, Set<String> locationParentIds,
                                                       String nameFilter, boolean isForMapping, String planId) {
        if (locationParentIds == null || locationParentIds.isEmpty())
            return new ArrayList<>();

        List<StructureDetail> structureDetails = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        Pair<String, String[]> queryArgsPair = createFetchStructureDetailsQuery(pageNo, locationParentIds, nameFilter, planId);

        try (Cursor cursor = sqLiteDatabase.rawQuery(queryArgsPair.first, queryArgsPair.second)) {
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

    private Pair<String, String[]> createFetchStructureDetailsQuery(Integer pageNo, Set<String> locationParentIds,
                                                                    String nameFilter, String planId) {
        Location location = EusmApplication.getInstance().getUserLocation();

        String[] columns = new String[]{
                STRUCTURE_TABLE + "." + "_id",
                STRUCTURE_TABLE + "." + "name as structureName",
                STRUCTURE_TABLE + "." + "type",
                STRUCTURE_TABLE + "." + "latitude",
                STRUCTURE_TABLE + "." + "longitude",
                STRUCTURE_TABLE + "." + "parent_id as structureParentId",
                STRUCTURE_TABLE + "." + "geojson as structureGeoJson",
                LOCATION_TABLE + "." + "name as locationName",
                LOCATION_TABLE + "." + "parent_id as locationParentId",
                location == null ? "0 as dist " : "(((?  - longitude)*(?  - longitude)) + ((?  - latitude)*(?  - latitude))) as dist",
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
                + " join task on task.structure_id = " + StructureRepository.STRUCTURE_TABLE + "._id "
                + " join location on location._id = " + StructureRepository.STRUCTURE_TABLE + ".parent_id ";

        String[] args = ArrayUtils.addAll(new String[]{planId}, locationParentIds.toArray(new String[0]));

        query += " where task.plan_id = ? AND (" + StringUtils.repeat("locationParentId = ? ", " OR ", locationParentIds.size()) + ")";

        if (StringUtils.isNotBlank(nameFilter)) {
            args = ArrayUtils.addAll(args, "%" + nameFilter + "%");
            query += " and structureName like  ? ";
        }

        if (location != null) {
            String[] locationArgsArray = new String[]{
                    String.valueOf(location.getLongitude()),
                    String.valueOf(location.getLongitude()),
                    String.valueOf(location.getLatitude()),
                    String.valueOf(location.getLatitude()),
            };
            args = ArrayUtils.addAll(locationArgsArray, args);
        }

        query += " group by " + STRUCTURE_TABLE + "." + "_id" + " order by case when dist is null then 1 else 0 end, dist"
                + (pageNo == null ? "" : " LIMIT " + CURRENT_LIMIT + " OFFSET " + (pageNo * CURRENT_LIMIT));

        return Pair.create(query, args);
    }

    private StructureDetail createStructureDetail(@NonNull Cursor cursor, boolean isForMapping) {
        String id = cursor.getString(cursor.getColumnIndex(ID));
        String name = cursor.getString(cursor.getColumnIndex("structureName"));
        String type = cursor.getString(cursor.getColumnIndex(TYPE));
        String latitude = cursor.getString(cursor.getColumnIndex(LATITUDE));
        String longitude = cursor.getString(cursor.getColumnIndex(LONGITUDE));
        String taskStatus = cursor.getString(cursor.getColumnIndex("taskStatus"));
        String commune = cursor.getString(cursor.getColumnIndex("locationName"));
        String geojson = cursor.getString(cursor.getColumnIndex("structureGeoJson"));
        String structureParentId = cursor.getString(cursor.getColumnIndex("structureParentId"));

        StructureDetail structureDetail = new StructureDetail();
        structureDetail.setStructureId(id);
        structureDetail.setEntityName(name);
        structureDetail.setStructureType(type);
        structureDetail.setTaskStatus(taskStatus);
        structureDetail.setNumOfTasks(cursor.getString(cursor.getColumnIndex("numOfTasks")));
        structureDetail.setCommune(commune);
        structureDetail.setParentId(structureParentId);

        if (StringUtils.isNotBlank(latitude) && StringUtils.isNotBlank(longitude)) {
            Location location = new Location("b");
            location.setLatitude(Double.parseDouble(latitude));
            location.setLongitude(Double.parseDouble(longitude));

            Float distanceInMetres = AppUtils.distanceFromUserLocation(location);
            if (distanceInMetres != null) {
                structureDetail.setDistance(distanceInMetres);
                structureDetail.setDistanceMeta(formatDistance(distanceInMetres));
                structureDetail.setNearby(distanceInMetres <= AppConstants.NEARBY_DISTANCE_IN_METRES);
            } else {
                structureDetail.setDistanceMeta("-");
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

    public List<org.smartregister.domain.Location> getStructuresByDistrictId(String districtId) {
        List<org.smartregister.domain.Location> locations = new ArrayList<>();
        String[] columns = new String[]{
                LOCATION_TABLE + "." + "parent_id as locationParentId",
                STRUCTURE_TABLE + "." + "geojson as structureGeoJson",
                STRUCTURE_TABLE + "." + "latitude",
                STRUCTURE_TABLE + "." + "longitude",
        };

        String query = "SELECT " + StringUtils.join(columns, ",") + " from " + StructureRepository.STRUCTURE_TABLE
                + " join location on location._id = " + StructureRepository.STRUCTURE_TABLE + ".parent_id ";


        String[] args = new String[]{districtId};
        query += " where locationParentId = ? ";

        extractLocationFromCursor(locations, query, args);

        return locations;
    }

    private void extractLocationFromCursor(List<org.smartregister.domain.Location> locations, String query, String[] args) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        try (Cursor cursor = sqLiteDatabase.rawQuery(query, args)) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String geojson = cursor.getString(cursor.getColumnIndex("structureGeoJson"));
                    locations.add(gson.fromJson(geojson, org.smartregister.domain.Location.class));
                }
            }
        } catch (SQLException e) {
            Timber.e(e);
        }
    }
}
