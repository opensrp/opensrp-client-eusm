package org.smartregister.eusm.helper;

import com.google.gson.reflect.TypeToken;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.Location;
import org.smartregister.eusm.BaseUnitTest;
import org.smartregister.repository.LocationRepository;
import org.smartregister.util.JsonFormUtils;
import org.smartregister.util.SyncUtils;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.doReturn;

public class AppValidateAssignmentHelperTest extends BaseUnitTest {

    private AppValidateAssignmentHelper appValidateAssignmentHelper;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private CoreLibrary coreLibrary;

    @Mock
    private Context opensrpContext;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        appValidateAssignmentHelper = new AppValidateAssignmentHelper(new SyncUtils(RuntimeEnvironment.application));
    }

    @Test
    public void testGetExistingJurisdictionsShouldReturnOnlyCommunes() {
        doReturn(locationRepository).when(opensrpContext).getLocationRepository();

        doReturn(opensrpContext).when(coreLibrary).context();

        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);

        String locationJSon = "[{\"type\":\"Feature\",\"id\":\"f3199af5-2eaf-46df-87c9-40d59606a2fb\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[49.54933,-16.08306]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"8e74d042-4a71-4694-a652-bc3ba6369101\",\"name\":\"EPP Ambodisatrana 2\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"District\"},\"serverVersion\":18479},{\"type\":\"Feature\",\"id\":\"b8a7998c-5df6-49eb-98e6-f0675db71848\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[49.58436,-16.433]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"663d7935-35e7-4ccf-aaf5-6e16f2042570\",\"name\":\"Ambatoharanana\",\"geographicLevel\":3,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18480},{\"type\":\"Feature\",\"id\":\"45e4bd97-fe11-458b-b481-294b7d7e8270\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[49.52125,-16.78147]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"c38e0c1e-3d72-424b-ac37-29e8d3e82026\",\"name\":\"Ambahoabe\",\"geographicLevel\":3,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18481}]";
        List<Location> locationList = JsonFormUtils.gson.fromJson(locationJSon, new TypeToken<List<Location>>() {
        }.getType());

        doReturn(locationList).when(locationRepository).getAllLocations();

        Set<String> strings = appValidateAssignmentHelper.getExistingJurisdictions();

        assertFalse(strings.isEmpty());

        assertEquals(2, strings.size());
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", null);
    }
}