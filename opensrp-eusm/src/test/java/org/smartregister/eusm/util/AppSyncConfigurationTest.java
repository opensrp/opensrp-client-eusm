package org.smartregister.eusm.util;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.smartregister.SyncFilter;
import org.smartregister.eusm.BaseUnitTest;
import org.smartregister.eusm.BuildConfig;
import org.smartregister.eusm.config.AppSyncConfiguration;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.LocationRepository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by samuelgithengi on 5/22/19.
 */
public class AppSyncConfigurationTest extends BaseUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private AllSharedPreferences allSharedPreferences;

    private AppSyncConfiguration syncConfiguration;

    @Before
    public void setUp() {
        syncConfiguration = new AppSyncConfiguration(locationRepository, allSharedPreferences);
    }

    @Test
    public void getSyncMaxRetries() {
        assertEquals(BuildConfig.MAX_SYNC_RETRIES, syncConfiguration.getSyncMaxRetries());
    }

    @Test
    public void getUniqueIdSource() {
        assertEquals(BuildConfig.OPENMRS_UNIQUE_ID_SOURCE, syncConfiguration.getUniqueIdSource());
    }

    @Test
    public void getUniqueIdBatchSize() {
        assertEquals(BuildConfig.OPENMRS_UNIQUE_ID_BATCH_SIZE, syncConfiguration.getUniqueIdBatchSize());
    }

    @Test
    public void getUniqueIdInitialBatchSize() {
        assertEquals(BuildConfig.OPENMRS_UNIQUE_ID_INITIAL_BATCH_SIZE, syncConfiguration.getUniqueIdInitialBatchSize());
    }

    @Test
    public void isSyncSettings() {
        assertTrue(syncConfiguration.isSyncSettings());
    }

    @Test
    public void disableSyncToServerIfUserIsDisabled() {
        assertTrue(syncConfiguration.disableSyncToServerIfUserIsDisabled());
    }

    @Test
    public void getEncryptionParam() {
        assertEquals(SyncFilter.TEAM_ID, syncConfiguration.getEncryptionParam());
    }

    @Test
    public void updateClientDetailsTable() {
        assertFalse(syncConfiguration.updateClientDetailsTable());
    }

    @Test
    public void testGetConnectionTimeOut() {
        assertEquals(300000, syncConfiguration.getConnectTimeout());
    }

    @Test
    public void testIsSyncUsingPost() {
        assertTrue(syncConfiguration.isSyncUsingPost());
    }

    @Test
    public void testGetSynchronizationTags() {
        assertNull(syncConfiguration.getSynchronizedLocationTags());
    }

    @Test
    public void testGetSettingsSyncFilterParam() {
        assertEquals(SyncFilter.TEAM_ID, syncConfiguration.getSettingsSyncFilterParam());
    }

}
