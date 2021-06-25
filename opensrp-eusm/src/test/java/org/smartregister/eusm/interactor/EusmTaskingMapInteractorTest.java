package org.smartregister.eusm.interactor;

import com.google.gson.JsonArray;
import com.mapbox.geojson.Feature;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.annotation.LooperMode;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.domain.Geometry;
import org.smartregister.domain.Location;
import org.smartregister.domain.LocationProperty;
import org.smartregister.eusm.BaseUnitTest;
import org.smartregister.eusm.domain.StructureDetail;
import org.smartregister.eusm.repository.AppStructureRepository;
import org.smartregister.tasking.contract.TaskingMapActivityContract;

import java.util.ArrayList;
import java.util.List;

import static android.os.Looper.getMainLooper;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;
import static org.robolectric.annotation.LooperMode.Mode.PAUSED;

@LooperMode(PAUSED)
public class EusmTaskingMapInteractorTest extends BaseUnitTest {

    private EusmTaskingMapInteractor eusmTaskingMapInteractor;

    @Mock
    private AppStructureRepository appStructureRepository;

    @Mock
    private TaskingMapActivityContract.Presenter presenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        eusmTaskingMapInteractor = spy(new EusmTaskingMapInteractor(presenter));
    }

    @Test
    public void testFetchLocationsWhenOperationalAreaUnavailableShouldInvokeRequiredMethod() throws InterruptedException {
        String plan = "planA";
        String operationalArea = "operationalArea";
        String point = "";
        boolean locationComponentActive = false;
        eusmTaskingMapInteractor.fetchLocations(plan, operationalArea, point, locationComponentActive);
        shadowOf(getMainLooper()).idle();
        Thread.sleep(ASYNC_TIMEOUT);

        TaskingMapActivityContract.Presenter presenter = ReflectionHelpers.getField(eusmTaskingMapInteractor, "presenter");

        verify(presenter).onStructuresFetched(any(JSONObject.class), isNull(), isNull());
    }

    @Test
    public void testFetchLocationsWhenOperationalAreaAvailableShouldInvokeRequiredMethod() throws InterruptedException {
        String plan = "planA";
        String operationalArea = "operationalArea";
        String point = "";
        boolean locationComponentActive = false;

        Location location = new Location();
        location.setId("324");
        location.setType("Feature");

        ReflectionHelpers.setField(eusmTaskingMapInteractor, "appStructureRepository", appStructureRepository);


        List<StructureDetail> structureDetails = new ArrayList<>();

        Geometry geometry = new Geometry();
        geometry.setType(Geometry.GeometryType.POINT);
        JsonArray coordinatesArray = new JsonArray();
        coordinatesArray.add(49.584358215332d);
        coordinatesArray.add(-16.4330005645752d);
        geometry.setCoordinates(coordinatesArray);


        LocationProperty locationProperty = new LocationProperty();
        Location location1 = new Location();
        location1.setGeometry(geometry);
        location1.setProperties(locationProperty);
        StructureDetail structureDetail = new StructureDetail();
        structureDetail.setStructureId("wer34-3424");
        structureDetail.setStructureType("Water Point");
        structureDetail.setTaskStatus("STARTED");
        structureDetail.setGeojson(location1);

        structureDetails.add(structureDetail);

        doReturn(structureDetails).when(appStructureRepository)
                .fetchStructureDetails(isNull(), anyString(), isNull(), eq(true), isNull());

        doReturn(location).when(eusmTaskingMapInteractor).getOperationalAreaLocation(eq(operationalArea));

        eusmTaskingMapInteractor.fetchLocations(plan, operationalArea, point, locationComponentActive);

        shadowOf(getMainLooper()).idle();
        Thread.sleep(ASYNC_TIMEOUT);

        TaskingMapActivityContract.Presenter presenter = ReflectionHelpers.getField(eusmTaskingMapInteractor, "presenter");

        verify(presenter).onStructuresFetched(any(JSONObject.class), any(Feature.class), isNull(), eq(point), eq(locationComponentActive));
    }
}