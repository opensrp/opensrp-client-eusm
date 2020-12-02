package org.smartregister.eusm.repository;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.AllConstants;
import org.smartregister.configurableviews.repository.ConfigurableViewsRepository;
import org.smartregister.eusm.BuildConfig;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.CampaignRepository;
import org.smartregister.repository.ClientFormRepository;
import org.smartregister.repository.ClientRelationshipRepository;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.LocationRepository;
import org.smartregister.repository.ManifestRepository;
import org.smartregister.repository.PlanDefinitionRepository;
import org.smartregister.repository.PlanDefinitionSearchRepository;
import org.smartregister.repository.Repository;
import org.smartregister.repository.SettingsRepository;
import org.smartregister.repository.TaskRepository;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.stock.repository.StockRepository;
import org.smartregister.stock.repository.StockTypeRepository;
import org.smartregister.tasking.util.TaskingConstants;
import org.smartregister.util.DatabaseMigrationUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import timber.log.Timber;

import static org.smartregister.repository.EventClientRepository.Table.event;
import static org.smartregister.util.DatabaseMigrationUtils.isColumnExists;


public class EusmRepository extends Repository {

    protected SQLiteDatabase readableDatabase;
    protected SQLiteDatabase writableDatabase;


    public EusmRepository(Context context, org.smartregister.Context openSRPContext) {
        super(context, AllConstants.DATABASE_NAME, BuildConfig.DATABASE_VERSION, openSRPContext.session(), EusmApplication.createCommonFtsObject(), openSRPContext.sharedRepositoriesArray());
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        super.onCreate(database);
        ConfigurableViewsRepository.createTable(database);
        EventClientRepository.createTable(database, EventClientRepository.Table.client, EventClientRepository.client_column.values());
        EventClientRepository.createTable(database, event, EventClientRepository.event_column.values());

        StockTypeRepository.createTable(database);
        StockRepository.createTable(database);
        CampaignRepository.createTable(database);
        TaskRepository.createTable(database);
        LocationRepository.createTable(database);
        AppStructureRepository.createTable(database);
        PlanDefinitionRepository.createTable(database);
        PlanDefinitionSearchRepository.createTable(database);
        SettingsRepository.onUpgrade(database);

        ClientFormRepository.createTable(database);
        ManifestRepository.createTable(database);
        if (!ManifestRepository.isVersionColumnExist(database)) {
            ManifestRepository.addVersionColumn(database);
        }

        ClientRelationshipRepository.createTable(database);

        EventClientRepository.createAdditionalColumns(database);

        EventClientRepository.addEventLocationId(database);

        DatabaseMigrationUtils.createAddedECTables(database,
                new HashSet<>(Collections.singletonList(TaskingConstants.Tables.EC_EVENT)),
                EusmApplication.createCommonFtsObject());

        EventClientRepository.createTable(database,
                EventClientRepository.Table.foreignEvent,
                EventClientRepository.event_column.values());

        EventClientRepository.createTable(database,
                EventClientRepository.Table.foreignClient,
                EventClientRepository.client_column.values());

        UniqueIdRepository.createTable(database);

        if (!isColumnExists(database, TaskingConstants.Tables.LOCATION_TABLE, TaskingConstants.Columns.Location.SYNC_STATUS)) {
            database.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s VARCHAR DEFAULT %s ", TaskingConstants.Tables.LOCATION_TABLE,
                    TaskingConstants.Columns.Location.SYNC_STATUS, BaseRepository.TYPE_Synced));
        }

        onUpgrade(database, 1, BuildConfig.DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Timber.w("Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        int upgradeTo = oldVersion + 1;
        while (upgradeTo <= newVersion) {
            switch (upgradeTo) {
                case 2:
                    //upgradeToVersion2(db);
                default:
                    break;
            }
            upgradeTo++;
        }
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        return getReadableDatabase(EusmApplication.getInstance().getPassword());
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return getWritableDatabase(EusmApplication.getInstance().getPassword());
    }

    @Override
    public synchronized SQLiteDatabase getReadableDatabase(String password) {
        if (StringUtils.isBlank(password)) {
            throw new IllegalStateException("Password is blank");
        }
        try {
            if (readableDatabase == null || !readableDatabase.isOpen()) {
                readableDatabase = super.getReadableDatabase(password);
            }
            return readableDatabase;
        } catch (Exception e) {
            Timber.e(e, "Database Error. ");
            return null;
        }

    }

    @Override
    public synchronized SQLiteDatabase getWritableDatabase(String password) {
        if (StringUtils.isBlank(password)) {
            throw new IllegalStateException("Password is blank");
        } else if (writableDatabase == null || !writableDatabase.isOpen()) {
            writableDatabase = super.getWritableDatabase(password);
        }
        return writableDatabase;
    }

    @Override
    public synchronized void close() {
        if (readableDatabase != null) {
            readableDatabase.close();
        }

        if (writableDatabase != null) {
            writableDatabase.close();
        }
        super.close();
    }
}
