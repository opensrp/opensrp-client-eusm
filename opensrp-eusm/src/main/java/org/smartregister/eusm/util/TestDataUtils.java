package org.smartregister.eusm.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;
import org.smartregister.domain.Location;
import org.smartregister.domain.LocationProperty;
import org.smartregister.domain.PlanDefinition;
import org.smartregister.domain.Task;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.repository.LocationRepository;
import org.smartregister.repository.PlanDefinitionRepository;
import org.smartregister.repository.TaskRepository;
import org.smartregister.service.UserService;
import org.smartregister.util.DateTimeTypeConverter;
import org.smartregister.util.PropertiesConverter;

import java.util.List;
import java.util.Set;

/**
 * Created by samuelgithengi on 12/3/18.
 */
public class TestDataUtils {

    public static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .registerTypeAdapter(DateTime.class, new DateTimeTypeConverter())
            .registerTypeAdapter(LocationProperty.class, new PropertiesConverter()).create();
    private final TaskRepository taskRepository;
    private final LocationRepository locationRepository;
    //    private final StructureRepository structureRepository;
    private final String TEST_DATA_POPULATED = "test.data.populated";


    public TestDataUtils() {
        taskRepository = EusmApplication.getInstance().getTaskRepository();
        locationRepository = EusmApplication.getInstance().getLocationRepository();
//        structureRepository = EusmApplication.getInstance().getStructureRepository();
    }

//    public static void getPopulateInventory() {
//        String ip = "{\"stocks\":[{\"identifier\":4,\"transaction_type\":\"Inventory\",\"providerid\":\"34615126-f515-4b31-80ee-c42227f6f0c5\",\"value\":10,\"version\":1606728907714,\"deliveryDate\":\"2020-01-02T03:00:00.000+0300\",\"accountabilityEndDate\":\"2021-01-02T03:00:00.000+0300\",\"donor\":\"ADB\",\"serialNumber\":\"123434\",\"locationId\":\"f3199af5-2eaf-46df-87c9-40d59606a2fb\",\"customProperties\":{\"PO Number\":\"111\",\"UNICEF section\":\"Health\"},\"serverVersion\":5,\"type\":\"Stock\",\"id\":\"ddcaf383-882e-448b-b701-8b72cb0d4d7a\",\"revision\":\"v1\"},{\"identifier\":4,\"transaction_type\":\"Inventory\",\"providerid\":\"34615126-f515-4b31-80ee-c42227f6f0c5\",\"value\":10,\"version\":1606728907714,\"deliveryDate\":\"2020-01-02T03:00:00.000+0300\",\"accountabilityEndDate\":\"2021-01-02T03:00:00.000+0300\",\"donor\":\"ADB\",\"serialNumber\":\"123434\",\"locationId\":\"f3199af5-2eaf-46df-87c9-40d59606a2fb\",\"customProperties\":{\"PO Number\":\"111\",\"UNICEF section\":\"Health\"},\"serverVersion\":5,\"type\":\"Stock\",\"id\":\"d65e06d6-2ae8-485f-9d63-0335d0025bde\",\"revision\":\"v1\"},{\"identifier\":3,\"transaction_type\":\"Inventory\",\"providerid\":\"34615126-f515-4b31-80ee-c42227f6f0c5\",\"value\":10,\"version\":1606728907714,\"deliveryDate\":\"2020-01-02T03:00:00.000+0300\",\"accountabilityEndDate\":\"2021-01-02T03:00:00.000+0300\",\"donor\":\"ADB\",\"serialNumber\":\"123434\",\"locationId\":\"b8a7998c-5df6-49eb-98e6-f0675db71848\",\"customProperties\":{\"PO Number\":\"111\",\"UNICEF section\":\"Health\"},\"serverVersion\":5,\"type\":\"Stock\",\"id\":\"69227a92-7979-490c-b149-f28669c6b760\",\"revision\":\"v1\"}]}";
//        StockResponse stockResponse = GsonUtil.getGson().fromJson(ip, new TypeToken<StockResponse>() {
//        }.getType());
//        new EusmStockSyncConfiguration().getStockSyncIntentServiceHelper().batchInsertStocks(stockResponse.getStocks());
//    }

    public void populateTestData() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(EusmApplication.getInstance().getApplicationContext());
        if (!sharedPreferences.getBoolean(TEST_DATA_POPULATED, false)) {
            UserService userService = EusmApplication.getInstance().context().userService();
            Set<String> strings = userService.fetchJurisdictionIds();
            strings.add("ad56bb3b-66c5-4a29-8003-0a60582540a6");
            strings.add("a7433a02-42be-4d19-8cbd-084dd5e0bbae");
            userService.saveJurisdictionIds(strings);
            createLocations();
            createPlanDefinition();
            createTasks();
            //createStructures();
//            getPopulateInventory();
            sharedPreferences.edit().putBoolean(TEST_DATA_POPULATED, true).apply();
        }
    }

    private void createTasks() {
        try {
            Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeConverter("yyyy-MM-dd'T'HHmm"))
                    .serializeNulls().create();
            String tasksJSON = "[{\"identifier\":\"076885f8-582e-4dc6-8a1a-510e1c8ed5d9\",\"campaignIdentifier\":\"IRS_2019_S1\",\"planIdentifier\":\"335ef7a3-7f35-58aa-8263-4419464946d8\",\"groupIdentifier\":\"8e74d042-4a71-4694-a652-bc3ba6369101\",\"status\":\"Ready\",\"businessStatus\":\"Visited\",\"priority\":\"routine\",\"code\":\"Record GPS\",\"description\":\"Record GPS\",\"focus\":\"Record GPS\",\"for\":\"a1b6e201-b91c-441a-8368-b75a82e06a42\",\"executionPeriod\":{\"start\":\"2018-11-10T22:00:00.000\"},\"authoredOn\":\"2018-11-29T0342\",\"lastModified\":\"2018-12-03T2212\",\"owner\":\"demoMTI\",\"location\":\"a1b6e201-b91c-441a-8368-b75a82e06a42\",\"note\":null,\"serverVersion\":1543867945202},{\"identifier\":\"634fa9fa-736d-4298-96aa-3de68ac02cae\",\"campaignIdentifier\":\"IRS_2019_S1\",\"planIdentifier\":\"335ef7a3-7f35-58aa-8263-4419464946d8\",\"groupIdentifier\":\"8e74d042-4a71-4694-a652-bc3ba6369101\",\"status\":\"Ready\",\"businessStatus\":\"Not Visited\",\"priority\":\"routine\",\"code\":\"IRS\",\"description\":\"Service Point Check\",\"focus\":\"Service Point Check\",\"for\":\"f3199af5-2eaf-46df-87c9-40d59606a2fb\",\"executionPeriod\":{\"start\":\"2018-11-10T22:00:00.000\"},\"authoredOn\":\"2018-11-29T0342\",\"lastModified\":\"2018-12-03T2212\",\"owner\":\"demoMTI\",\"location\":\"f3199af5-2eaf-46df-87c9-40d59606a2fb\",\"note\":null,\"serverVersion\":1543867945203},{\"identifier\":\"d3b237ff-f9d8-4077-9523-c7bf3552ff87\",\"campaignIdentifier\":\"IRS_2019_S1\",\"groupIdentifier\":\"8e74d042-4a71-4694-a652-bc3ba6369101\",\"planIdentifier\":\"335ef7a3-7f35-58aa-8263-4419464946d8\",\"status\":\"Ready\",\"businessStatus\":\"Not Visited\",\"priority\":\"routine\",\"code\":\"IRS\",\"description\":\"Spray House\",\"focus\":\"IRS Visit\",\"for\":\"c2635a23-a604-48fb-9e1c-8bf1e75e6759\",\"executionPeriod\":{\"start\":\"2018-11-10T22:00:00.000\"},\"authoredOn\":\"2018-11-29T0342\",\"lastModified\":\"2018-12-03T2212\",\"location\":\"f3199af5-2eaf-46df-87c9-40d59606a2fb\",\"owner\":\"demoMTI\",\"note\":null,\"serverVersion\":1543867945204},{\"identifier\":\"c6dd4abc-fb3e-4f72-afb8-923fc43f44d7\",\"campaignIdentifier\":\"IRS_2019_S1\",\"groupIdentifier\":\"8e74d042-4a71-4694-a652-bc3ba6369101\",\"status\":\"Ready\",\"planIdentifier\":\"335ef7a3-7f35-58aa-8263-4419464946d8\",\"businessStatus\":\"Not Visited\",\"priority\":\"routine\",\"code\":\"IRS\",\"description\":\"product\",\"focus\":\"product\",\"for\":\"ddcaf383-882e-448b-b701-8b72cb0d4d7a\",\"executionPeriod\":{\"start\":\"2018-11-10T22:00:00.000\"},\"authoredOn\":\"2018-12-03T2212\",\"lastModified\":\"2018-12-03T2212\",\"owner\":\"demoMTI\",\"location\":\"f3199af5-2eaf-46df-87c9-40d59606a2fb\",\"note\":null,\"serverVersion\":1543867945195},{\"identifier\":\"2caa810d-d4da-4e67-838b-badb9bd86e06\",\"campaignIdentifier\":\"IRS_2019_S1\",\"groupIdentifier\":\"8e74d042-4a71-4694-a652-bc3ba6369101\",\"status\":\"Ready\",\"planIdentifier\":\"335ef7a3-7f35-58aa-8263-4419464946d8\",\"businessStatus\":\"Not Visited\",\"priority\":\"routine\",\"code\":\"IRS\",\"description\":\"Service Point Check\",\"focus\":\"Service Point Check\",\"for\":\"b8a7998c-5df6-49eb-98e6-f0675db71848\",\"executionPeriod\":{\"start\":\"2018-11-10T22:00:00.000\"},\"authoredOn\":\"2018-12-03T2212\",\"lastModified\":\"2018-12-03T2212\",\"owner\":\"demoMTI\",\"location\":\"b8a7998c-5df6-49eb-98e6-f0675db71848\",\"note\":null,\"serverVersion\":1543867945196},{\"identifier\":\"bbf32ca5-9b83-444d-882f-2085974e90b5\",\"campaignIdentifier\":\"IRS_2019_S1\",\"groupIdentifier\":\"8e74d042-4a71-4694-a652-bc3ba6369101\",\"status\":\"Ready\",\"planIdentifier\":\"335ef7a3-7f35-58aa-8263-4419464946d8\",\"businessStatus\":\"Not Visited\",\"priority\":\"routine\",\"code\":\"Record GPS\",\"description\":\"Record GPS\",\"focus\":\"Record GPS\",\"for\":\"bb0b393c-2fd6-4d99-a5b1-f32ef498a76d\",\"executionPeriod\":{\"start\":\"2018-11-10T22:00:00.000\"},\"authoredOn\":\"2018-12-03T2212\",\"lastModified\":\"2018-12-03T2212\",\"owner\":\"demoMTI\",\"location\":\"bb0b393c-2fd6-4d99-a5b1-f32ef498a76d\",\"note\":null,\"serverVersion\":1543867945196},{\"identifier\":\"6c303b8b-e47c-45e9-8ab5-3374c8f539a3\",\"campaignIdentifier\":\"IRS_2019_S1\",\"groupIdentifier\":\"8e74d042-4a71-4694-a652-bc3ba6369101\",\"status\":\"Ready\",\"planIdentifier\":\"335ef7a3-7f35-58aa-8263-4419464946d8\",\"businessStatus\":\"Not Visited\",\"priority\":\"routine\",\"code\":\"IRS\",\"description\":\"product\",\"focus\":\"product\",\"for\":\"69227a92-7979-490c-b149-f28669c6b760\",\"executionPeriod\":{\"start\":\"2018-11-10T22:00:00.000\"},\"authoredOn\":\"2018-12-03T2212\",\"lastModified\":\"2018-12-03T2212\",\"owner\":\"demoMTI\",\"location\":\"b8a7998c-5df6-49eb-98e6-f0675db71848\",\"note\":null,\"serverVersion\":1543867945196}]";
            List<Task> tasks = gson.fromJson(tasksJSON, new TypeToken<List<Task>>() {
            }.getType());
            for (Task task : tasks) {
                try {
                    taskRepository.addOrUpdate(task);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createLocations() {
        try {

//            Location location = gson.fromJson(locationJSon, Location.class);
//            locationRepository.addOrUpdate(location);
//
            String location2JSon = "       {\n" +
                    "        \"type\": \"Feature\",\n" +
                    "        \"id\": \"ad56bb3b-66c5-4a29-8003-0a60582540a6\",\n" +
                    "        \"properties\": {\n" +
                    "            \"status\": \"Active\",\n" +
                    "            \"parentId\": \"bb30770a-d039-4bfa-9c02-a32d0f32af42\",\n" +
                    "            \"name\": \"MANANARA AVARATRA\",\n" +
                    "            \"geographicLevel\": 0,\n" +
                    "            \"version\": 0\n" +
                    "        },\n" +
                    "        \"serverVersion\": 3013\n" +
                    "   } ";
            Location location2 = gson.fromJson(location2JSon, Location.class);
            locationRepository.addOrUpdate(location2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void createStructures() {
//        try {
//            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
//                    .registerTypeAdapter(DateTime.class, new DateTimeTypeConverter())
//                    .registerTypeAdapter(LocationProperty.class, new PropertiesConverter()).create();
//            String locationJSon = "[{\"type\":\"Feature\",\"id\":\"f3199af5-2eaf-46df-87c9-40d59606a2fb\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[49.54933,-16.08306]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"8e74d042-4a71-4694-a652-bc3ba6369101\",\"name\":\"EPP Ambodisatrana 2\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18479},{\"type\":\"Feature\",\"id\":\"b8a7998c-5df6-49eb-98e6-f0675db71848\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[49.58436,-16.433]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"663d7935-35e7-4ccf-aaf5-6e16f2042570\",\"name\":\"Ambatoharanana\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18480},{\"type\":\"Feature\",\"id\":\"45e4bd97-fe11-458b-b481-294b7d7e8270\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[49.52125,-16.78147]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"c38e0c1e-3d72-424b-ac37-29e8d3e82026\",\"name\":\"Ambahoabe\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18481},{\"type\":\"Feature\",\"id\":\"b2a9b18c-5fa0-4d48-bee8-5db0b57c18ca\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[49.4302,-17.1601]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"51253521-e300-442a-9c54-2f10bcb59408\",\"name\":\"Ampihaonana\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18482},{\"type\":\"Feature\",\"id\":\"b2f87aab-bb70-42fc-910e-d55de623fd23\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[49.20031,-17.24602]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"0c11a6bc-6b78-475b-8015-e1b88c838c49\",\"name\":\"Ambodihasina\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18483},{\"type\":\"Feature\",\"id\":\"44e3cdb3-9f08-4498-96ac-c76f2bed7319\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[49.17101,-17.2573]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"0c11a6bc-6b78-475b-8015-e1b88c838c49\",\"name\":\"Ambodilaitra\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18484},{\"type\":\"Feature\",\"id\":\"ea4255eb-9f8d-4dd7-a2b8-4174b423858e\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[49.32238,-17.3515]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"7cd51ffb-ef6e-4e98-9000-34efc64c6eb8\",\"name\":\"Mahanoro\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18485},{\"type\":\"Feature\",\"id\":\"74b7f4d8-9c39-4837-b95f-f997e1cc1ccd\",\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"7cd51ffb-ef6e-4e98-9000-34efc64c6eb8\",\"name\":\"AMBALABE II\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18486},{\"type\":\"Feature\",\"id\":\"11b49baf-6654-4e0f-8103-9ce546010ab8\",\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"7cd51ffb-ef6e-4e98-9000-34efc64c6eb8\",\"name\":\"TANETILAVA ET VOHITSOA\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18487},{\"type\":\"Feature\",\"id\":\"e855ac03-b2fd-4e97-b11d-286d90c94ea5\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[49.31232,-17.37877]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"7cd51ffb-ef6e-4e98-9000-34efc64c6eb8\",\"name\":\"ANDRATANIMOINA\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18488},{\"type\":\"Feature\",\"id\":\"2390fcef-f15d-4820-bad3-3909c8275e29\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[49.28838,-17.44301]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"eadf078b-2054-4723-8af1-5d16c18ac240\",\"name\":\"MAROMITETY\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18489},{\"type\":\"Feature\",\"id\":\"3a77c1c3-a633-40f6-8cba-ecb9f8bc2d8d\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[49.18185,-17.63283]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"1fec2789-7741-499d-a28f-fc9e6c354ea5\",\"name\":\"EPP Miarinarivo\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18490},{\"type\":\"Feature\",\"id\":\"d7e4c785-fe54-4e8f-9e73-c5ed7f302246\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[47.60943,-22.52687]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"22cc7ef7-4aba-4e19-be91-cd1e40e57195\",\"name\":\"EPP Ambalatany\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18491},{\"type\":\"Feature\",\"id\":\"957070da-bee0-411d-a337-097380c161ce\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[47.82874,-22.81754]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"cacefb35-c11d-4556-a969-6b7cf3b245aa\",\"name\":\"EPP Ampataka\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18492},{\"type\":\"Feature\",\"id\":\"0bb4ba00-076c-4af6-a158-054fcd6efa1a\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[47.25314,-23.32697]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"599a5e27-9b21-4c63-b7ee-ae935abcdfb2\",\"name\":\"BEVATA\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18493},{\"type\":\"Feature\",\"id\":\"565231ba-2da2-4f85-a236-db86093c6d33\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[47.00884,-23.65442]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"7a781fab-5cf6-44df-a9e6-9203b3fb9e2a\",\"name\":\"EPP Ankazovelo\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18494},{\"type\":\"Feature\",\"id\":\"5d6e98c1-da57-405b-99b3-b076120e95cd\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[46.83199,-24.85807]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"b26243a3-a312-4841-a6cd-4f7718bfb508\",\"name\":\"MANDISO\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18495},{\"type\":\"Feature\",\"id\":\"395ca301-a9dd-4b7b-8eb0-2887da99c80d\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[47.01117,-24.8953]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"b5b1e61b-f82e-48bb-8077-c5251a677fe2\",\"name\":\"MANDROMONDROMOTRA\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18496},{\"type\":\"Feature\",\"id\":\"043b9f8f-72fb-4875-9f5e-4552af653bc8\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[46.92783,-24.92968]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"333e8803-a727-4874-9913-21a7136c220b\",\"name\":\"IFARANTSA\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18497},{\"type\":\"Feature\",\"id\":\"fb8d65b1-f007-49b2-b0a1-b9775afab4d3\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[46.74732,-24.97733]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"449f3116-932e-47b8-99f3-d5dab0a81f7f\",\"name\":\"TAVIALA\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18498},{\"type\":\"Feature\",\"id\":\"e15861ac-3a56-4fdf-b0a4-4a7dbba15418\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[46.68708,-25.00433]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"e10efd4e-f85a-4d38-9fd9-b88369d3a367\",\"name\":\"RANOPISO\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18499},{\"type\":\"Feature\",\"id\":\"5c5c6f56-85bc-4e18-9bee-ade736dedd84\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[47.26532,-23.42273]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"ba2ac9bc-d750-4d66-8a12-c5d3f651267d\",\"name\":\"EPP Ranomena\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18500},{\"type\":\"Feature\",\"id\":\"49e81290-ca8f-4be6-80dd-4648a559542e\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[47.014,-23.59364]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"19d81fa8-2435-43c8-b6df-30cfa7a975ec\",\"name\":\"EPP Centre Midongy\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18501},{\"type\":\"Feature\",\"id\":\"6f43713f-5227-4020-bb60-b6857d8a2475\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[46.98561,-23.82669]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"4fa9f736-89a7-4612-8a55-8fc5cc59bc9d\",\"name\":\"EPP Befotaka\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18502},{\"type\":\"Feature\",\"id\":\"de2c14e1-b294-4d34-baae-eb97936c6058\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[46.3777,-15.654]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"d32b989c-69a4-4e30-9c15-2d4f701150dd\",\"name\":\"ANKARAOBATO\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18503}]";
//            List<Location> structures = gson.fromJson(locationJSon, new TypeToken<List<Location>>() {
//            }.getType());
//            for (Location structure : structures) {
//                try {
//                    structureRepository.addOrUpdate(structure);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public void createPlanDefinition() {
        try {
            Gson gson = PlanDefinitionRepository.gson;

            String planDefinitionJSON = "[{\"identifier\":\"335ef7a3-7f35-58aa-8263-4419464946d8\",\"version\":\"1\",\"name\":\"EUSM Mission 2020-11-17\",\"title\":\"EUSM Mission 2020-11-17\",\"status\":\"active\",\"date\":\"2020-11-17\",\"effectivePeriod\":{\"start\":\"2020-11-17\",\"end\":\"2021-12-24\"},\"useContext\":[{\"code\":\"taskGenerationStatus\",\"valueCodableConcept\":\"internal\"}],\"jurisdiction\":[{\"code\":\"ad56bb3b-66c5-4a29-8003-0a60582540a6\"}],\"serverVersion\":1599112764477,\"goal\":[{\"id\":\"Product_Check\",\"description\":\"Check for all products (100%) within the jurisdiction\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"Percent of products checked\",\"detail\":{\"detailQuantity\":{\"value\":100,\"comparator\":\">\",\"unit\":\"Percent\"}},\"due\":\"2020-12-24\"}]},{\"id\":\"Fix_Product_Problem\",\"description\":\"Fix problems for all products (100%) within the jurisdiction\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"Percent of products problems fixed\",\"detail\":{\"detailQuantity\":{\"value\":100,\"comparator\":\">\",\"unit\":\"Percent\"}},\"due\":\"2020-12-24\"}]},{\"id\":\"Record_GPS\",\"description\":\"Record GPS for all service points without GPS within the jurisdiction\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"Percent of GPS recorded\",\"detail\":{\"detailQuantity\":{\"value\":100,\"comparator\":\">\",\"unit\":\"Percent\"}},\"due\":\"2020-12-24\"}]},{\"id\":\"Service_Point_Check\",\"description\":\"Conduct checks for all service point (100%) within the Jurisdiction\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"Percent of service points checked\",\"detail\":{\"detailQuantity\":{\"value\":100,\"comparator\":\">\",\"unit\":\"Percent\"}},\"due\":\"2020-12-24\"}]}],\"action\":[{\"identifier\":\"bd90510c-e769-5176-ad18-5a256822822a\",\"prefix\":1,\"title\":\"Product Check\",\"description\":\"Check for all products (100%) within the jurisdiction\",\"code\":\"Product Check\",\"timingPeriod\":{\"start\":\"2020-11-17\",\"end\":\"2020-12-24\"},\"reason\":\"Routine\",\"goalId\":\"Product_Check\",\"subjectCodableConcept\":{\"text\":\"Device\"},\"trigger\":[{\"type\":\"named-event\",\"name\":\"plan-activation\"}],\"condition\":[{\"kind\":\"applicability\",\"expression\":{\"description\":\"Product exists\",\"expression\":\"$this.is(FHIR.Device)\"}}],\"definitionUri\":\"product_check.json\",\"type\":\"create\"},{\"identifier\":\"bd90510c-e769-5176-ad18-5a256822822a\",\"prefix\":2,\"title\":\"Fix Product Problem\",\"description\":\"Fix problems for all products (100%) within the jurisdiction\",\"code\":\"Fix Product Problems\",\"timingPeriod\":{\"start\":\"2020-11-17\",\"end\":\"2020-12-24\"},\"reason\":\"Routine\",\"goalId\":\"fix_problem\",\"subjectCodableConcept\":{\"text\":\"Task\"},\"trigger\":[{\"type\":\"named-event\",\"name\":\"event-submission\",\"expression\":{\"description\":\"Trigger when a Flag problem event is submitted\",\"expression\":\"questionnaire = 'flag_problem'\"}}],\"condition\":[{\"kind\":\"applicability\",\"expression\":{\"description\":\"Product exists\",\"expression\":\"$this.is(FHIR.QuestionnaireResponse)\"}}],\"definitionUri\":\"product_check.json\",\"type\":\"create\"},{\"identifier\":\"bd90510c-e769-5176-ad18-5a256822822a\",\"prefix\":3,\"title\":\"Record GPS\",\"description\":\"Record GPS for all service points (100%) without GPS within the jurisdiction\",\"code\":\"Record GPS\",\"timingPeriod\":{\"start\":\"2020-11-17\",\"end\":\"2020-12-24\"},\"reason\":\"Routine\",\"goalId\":\"Record_GPS\",\"subjectCodableConcept\":{\"text\":\"Location\"},\"trigger\":[{\"type\":\"named-event\",\"name\":\"plan-activation\"}],\"condition\":[{\"kind\":\"applicability\",\"expression\":{\"description\":\"Service point does not have geometry\",\"expression\":\"$this.identifier.where(id='hasGeometry').value='false'\"}}],\"definitionUri\":\"record_gps.json\",\"type\":\"create\"},{\"identifier\":\"bd90510c-e769-5176-ad18-5a256822822a\",\"prefix\":3,\"title\":\"Service Point Check\",\"description\":\"Conduct checkfor all service points (100%) within the jurisdiction\",\"code\":\"Service Point Check\",\"timingPeriod\":{\"start\":\"2020-11-17\",\"end\":\"2020-12-24\"},\"reason\":\"Routine\",\"goalId\":\"Service_Point_Check\",\"subjectCodableConcept\":{\"text\":\"Location\"},\"trigger\":[{\"type\":\"named-event\",\"name\":\"plan-activation\"}],\"condition\":[{\"kind\":\"applicability\",\"expression\":{\"description\":\"All service points\",\"expression\":\"$this.is(FHIR.Location)\"}}],\"definitionUri\":\"service_point_check.json\",\"type\":\"create\"}],\"experimental\":false},{\"identifier\":\"ad355553-dde1-4ad8-ab95-21f77d2e95cc\",\"version\":\"1\",\"name\":\"EUSM Mission 2020-11-18\",\"title\":\"EUSM Mission 2020-11-18\",\"status\":\"active\",\"date\":\"2020-11-18\",\"effectivePeriod\":{\"start\":\"2020-11-18\",\"end\":\"2021-12-24\"},\"useContext\":[{\"code\":\"taskGenerationStatus\",\"valueCodableConcept\":\"internal\"}],\"jurisdiction\":[{\"code\":\"a7433a02-42be-4d19-8cbd-084dd5e0bbae\"}],\"serverVersion\":1599112764477,\"goal\":[{\"id\":\"Product_Check\",\"description\":\"Check for all products (100%) within the jurisdiction\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"Percent of products checked\",\"detail\":{\"detailQuantity\":{\"value\":100,\"comparator\":\">\",\"unit\":\"Percent\"}},\"due\":\"2020-12-24\"}]},{\"id\":\"Fix_Product_Problem\",\"description\":\"Fix problems for all products (100%) within the jurisdiction\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"Percent of products problems fixed\",\"detail\":{\"detailQuantity\":{\"value\":100,\"comparator\":\">\",\"unit\":\"Percent\"}},\"due\":\"2020-12-24\"}]},{\"id\":\"Record_GPS\",\"description\":\"Record GPS for all service points without GPS within the jurisdiction\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"Percent of GPS recorded\",\"detail\":{\"detailQuantity\":{\"value\":100,\"comparator\":\">\",\"unit\":\"Percent\"}},\"due\":\"2020-12-24\"}]},{\"id\":\"Service_Point_Check\",\"description\":\"Conduct checks for all service point (100%) within the Jurisdiction\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"Percent of service points checked\",\"detail\":{\"detailQuantity\":{\"value\":100,\"comparator\":\">\",\"unit\":\"Percent\"}},\"due\":\"2020-12-24\"}]}],\"action\":[{\"identifier\":\"5eb16a00-e238-4ae5-ba4e-784ddefbb74e\",\"prefix\":1,\"title\":\"Product Check\",\"description\":\"Check for all products (100%) within the jurisdiction\",\"code\":\"Product Check\",\"timingPeriod\":{\"start\":\"2020-11-17\",\"end\":\"2020-12-24\"},\"reason\":\"Routine\",\"goalId\":\"Product_Check\",\"subjectCodableConcept\":{\"text\":\"Device\"},\"trigger\":[{\"type\":\"named-event\",\"name\":\"plan-activation\"}],\"condition\":[{\"kind\":\"applicability\",\"expression\":{\"description\":\"Product exists\",\"expression\":\"$this.is(FHIR.Device)\"}}],\"definitionUri\":\"product_check.json\",\"type\":\"create\"},{\"identifier\":\"bd90510c-e769-5176-ad18-5a256822822a\",\"prefix\":2,\"title\":\"Fix Product Problem\",\"description\":\"Fix problems for all products (100%) within the jurisdiction\",\"code\":\"Fix Product Problems\",\"timingPeriod\":{\"start\":\"2020-11-17\",\"end\":\"2020-12-24\"},\"reason\":\"Routine\",\"goalId\":\"fix_problem\",\"subjectCodableConcept\":{\"text\":\"Task\"},\"trigger\":[{\"type\":\"named-event\",\"name\":\"event-submission\",\"expression\":{\"description\":\"Trigger when a Flag problem event is submitted\",\"expression\":\"questionnaire = 'flag_problem'\"}}],\"condition\":[{\"kind\":\"applicability\",\"expression\":{\"description\":\"Product exists\",\"expression\":\"$this.is(FHIR.QuestionnaireResponse)\"}}],\"definitionUri\":\"product_check.json\",\"type\":\"create\"},{\"identifier\":\"bd90510c-e769-5176-ad18-5a256822822a\",\"prefix\":3,\"title\":\"Record GPS\",\"description\":\"Record GPS for all service points (100%) without GPS within the jurisdiction\",\"code\":\"Record GPS\",\"timingPeriod\":{\"start\":\"2020-11-17\",\"end\":\"2020-12-24\"},\"reason\":\"Routine\",\"goalId\":\"Record_GPS\",\"subjectCodableConcept\":{\"text\":\"Location\"},\"trigger\":[{\"type\":\"named-event\",\"name\":\"plan-activation\"}],\"condition\":[{\"kind\":\"applicability\",\"expression\":{\"description\":\"Service point does not have geometry\",\"expression\":\"$this.identifier.where(id='hasGeometry').value='false'\"}}],\"definitionUri\":\"record_gps.json\",\"type\":\"create\"},{\"identifier\":\"857ed970-4171-418a-9609-45456ed76e5d\",\"prefix\":3,\"title\":\"Service Point Check\",\"description\":\"Conduct checkfor all service points (100%) within the jurisdiction\",\"code\":\"Service Point Check\",\"timingPeriod\":{\"start\":\"2020-11-17\",\"end\":\"2020-12-24\"},\"reason\":\"Routine\",\"goalId\":\"Service_Point_Check\",\"subjectCodableConcept\":{\"text\":\"Location\"},\"trigger\":[{\"type\":\"named-event\",\"name\":\"plan-activation\"}],\"condition\":[{\"kind\":\"applicability\",\"expression\":{\"description\":\"All service points\",\"expression\":\"$this.is(FHIR.Location)\"}}],\"definitionUri\":\"service_point_check.json\",\"type\":\"create\"}],\"experimental\":false}]";
            List<PlanDefinition> planDefinitions = gson.fromJson(planDefinitionJSON, new TypeToken<List<PlanDefinition>>() {
            }.getType());
            for (PlanDefinition planDefinition : planDefinitions) {
                EusmApplication.getInstance().getPlanDefinitionRepository().addOrUpdate(planDefinition);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
