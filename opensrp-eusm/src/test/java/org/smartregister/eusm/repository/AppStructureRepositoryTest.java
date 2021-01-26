package org.smartregister.eusm.repository;

import android.content.ContentValues;

import com.google.gson.JsonArray;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.domain.Geometry;
import org.smartregister.domain.Location;
import org.smartregister.domain.LocationProperty;
import org.smartregister.eusm.BaseUnitTest;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.domain.StructureDetail;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.smartregister.tasking.util.TaskingConstants.DatabaseKeys.LATITUDE;
import static org.smartregister.tasking.util.TaskingConstants.DatabaseKeys.LONGITUDE;

public class AppStructureRepositoryTest extends BaseUnitTest {

    private AppStructureRepository appStructureRepository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Mock
    private Cursor cursor;

    @Mock
    private EusmApplication eusmApplication;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        appStructureRepository = spy(new AppStructureRepository());
    }

    @Test
    public void testCreateTableShouldExecuteTwoQueries() {
        AppStructureRepository.createTable(sqLiteDatabase);
        verify(sqLiteDatabase).execSQL(eq(AppStructureRepository.CREATE_STRUCTURE_TABLE));
        verify(sqLiteDatabase).execSQL(eq(AppStructureRepository.CREATE_STRUCTURE_PARENT_INDEX));
    }

    @Test
    public void testAddOrUpdateShouldInvokeDbReplaceMethod() {
        JsonArray coordinateJsonElements = new JsonArray();
        coordinateJsonElements.add(22.122323);
        coordinateJsonElements.add(34.43423);

        Geometry geometry = new Geometry();
        geometry.setType(Geometry.GeometryType.POINT);
        geometry.setCoordinates(coordinateJsonElements);

        LocationProperty locationProperty = new LocationProperty();
        locationProperty.setUid("4323");
        locationProperty.setParentId("212");
        locationProperty.setType("Water Point");

        Location location = new Location();
        location.setId("323");
        location.setProperties(locationProperty);
        location.setGeometry(geometry);

        doReturn(sqLiteDatabase).when(appStructureRepository).getWritableDatabase();

        appStructureRepository.addOrUpdate(location);

        verify(sqLiteDatabase).replace(eq(AppStructureRepository.STRUCTURE_TABLE), isNull(), any(ContentValues.class));
    }

    @Test
    public void testCountOfStructuresShouldReturnCount() {
        doReturn(sqLiteDatabase).when(appStructureRepository).getReadableDatabase();

        doReturn(cursor).when(sqLiteDatabase)
                .rawQuery(anyString(), isNull());

        doAnswer(new Answer() {
            int count = -1;

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                count++;
                return count == 0;
            }
        }).when(cursor).moveToNext();

        int result = appStructureRepository.countOfStructures("tes", UUID.randomUUID().toString(), UUID.randomUUID().toString());

        assertEquals(0, result);
    }

    @Test
    public void testGetStructuresByDistrictIdShouldReturnListOfStructures() {

        doReturn(sqLiteDatabase).when(appStructureRepository).getReadableDatabase();

        doReturn(cursor).when(sqLiteDatabase)
                .rawQuery(anyString(), any(String[].class));

        doAnswer(new Answer() {
            int count = -1;

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                count++;
                return count == 0;
            }
        }).when(cursor).moveToNext();

        String geoJson = "{\"syncStatus\":\"Synced\",\"geometry\":{\"coordinates\":[49.584358215332,-16.4330005645752],\"type\":\"Point\"},\"id\":\"b8a7998c-5df6-49eb-98e6-f0675db71848\",\"properties\":{\"geographicLevel\":0,\"name\":\"Ambatoharanana\",\"parentId\":\"663d7935-35e7-4ccf-aaf5-6e16f2042570\",\"status\":\"Active\",\"type\":\"Water Point\",\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18480,\"type\":\"Feature\"}";

        doReturn(geoJson).when(cursor).getString(0);

        List<Location> locationList = appStructureRepository.getStructuresByDistrictId("23-4");

        assertEquals(1, locationList.size());
    }

    @Test
    public void testFetchStructureDetailsShouldReturnListOfStructures() {
        ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", eusmApplication);
        android.location.Location location = new android.location.Location("e");
        location.setLatitude(-1.11111);
        location.setLongitude(37.255);

        doReturn(location).when(eusmApplication).getUserLocation();

        doReturn(1).when(cursor).getColumnIndex(LATITUDE);
        doReturn("-1.234").when(cursor).getString(1);

        doReturn(2).when(cursor).getColumnIndex(LONGITUDE);
        doReturn("37.234").when(cursor).getString(2);

        doReturn(sqLiteDatabase).when(appStructureRepository).getReadableDatabase();

        doReturn(cursor).when(sqLiteDatabase)
                .rawQuery(anyString(), any(String[].class));

        doAnswer(new Answer() {
            int count = -1;

            @Override
            public Object answer(InvocationOnMock invocation) {
                count++;
                return count == 0;
            }
        }).when(cursor).moveToNext();

        List<StructureDetail> structureDetails = appStructureRepository.fetchStructureDetails(0, "23-2", "tes", false, UUID.randomUUID().toString());

        assertEquals(1, structureDetails.size());
    }
}