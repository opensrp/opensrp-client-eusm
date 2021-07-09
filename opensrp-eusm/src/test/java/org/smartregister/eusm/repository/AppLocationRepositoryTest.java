package org.smartregister.eusm.repository;

import android.content.ContentValues;

import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.smartregister.domain.Location;
import org.smartregister.domain.LocationProperty;
import org.smartregister.eusm.BaseUnitTest;
import org.smartregister.repository.BaseRepository;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class AppLocationRepositoryTest extends BaseUnitTest {

    private AppLocationRepository appLocationRepository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Before
    public void setUp() {
        appLocationRepository = spy(new AppLocationRepository());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddOrUpdateShouldThrowExceptionWhenLocationIdIsAbsent() {
        Location location = new Location();
        location.setType("Feature");
        location.setJurisdiction(true);
        location.setServerVersion(1L);
        location.setSyncStatus(BaseRepository.TYPE_Created);
        appLocationRepository.addOrUpdate(location);
    }

    @Test
    public void testAddOrUpdateShouldInvokeAddLocationIfIdPresent() {
        LocationProperty locationProperty = new LocationProperty();
        locationProperty.setGeographicLevel(0);
        locationProperty.setParentId(UUID.randomUUID().toString());
        locationProperty.setName("Location A");
        locationProperty.setUid(UUID.randomUUID().toString());

        Location location = new Location();
        location.setId(locationProperty.getUid());
        location.setType("Feature");
        location.setJurisdiction(true);
        location.setServerVersion(1L);
        location.setSyncStatus(BaseRepository.TYPE_Created);
        location.setProperties(locationProperty);

        doReturn(sqLiteDatabase).when(appLocationRepository).getWritableDatabase();

        appLocationRepository.addOrUpdate(location);

        verify(sqLiteDatabase).replace(eq("location"), isNull(), any(ContentValues.class));
    }
}