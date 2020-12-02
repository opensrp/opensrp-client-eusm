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
import org.smartregister.eusm.configuration.EusmStockSyncConfiguration;
import org.smartregister.eusm.model.ProductInfoQuestion;
import org.smartregister.eusm.model.TaskDetail;
import org.smartregister.repository.LocationRepository;
import org.smartregister.repository.PlanDefinitionRepository;
import org.smartregister.repository.StructureRepository;
import org.smartregister.repository.TaskRepository;
import org.smartregister.stock.StockLibrary;
import org.smartregister.stock.configuration.StockSyncConfiguration;
import org.smartregister.stock.domain.Stock;
import org.smartregister.stock.domain.StockResponse;
import org.smartregister.stock.repository.StockRepository;
import org.smartregister.stock.util.GsonUtil;
import org.smartregister.util.DateTimeTypeConverter;
import org.smartregister.util.PropertiesConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by samuelgithengi on 12/3/18.
 */
public class TestDataUtils {

    public static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .registerTypeAdapter(DateTime.class, new DateTimeTypeConverter())
            .registerTypeAdapter(LocationProperty.class, new PropertiesConverter()).create();
    public static String locationJSon = "{\"id\": \"3537\", \"type\": \"Feature\", \"geometry\": {\"type\": \"MultiPolygon\", \"coordinates\": [[[[32.64555352892119, -14.15491759447286], [32.64564603883843, -14.154955463350856], [32.6457355072855, -14.1549997471252], [32.64582146782072, -14.155050214924412], [32.64590347228988, -14.155106603636565], [32.64598109316318, -14.155168619281367], [32.64605392576265, -14.155235938542202], [32.64612159037363, -14.155308210452002], [32.64617583569173, -14.155375290811488], [32.64622989128036, -14.155433027025364], [32.64629203516403, -14.155509874795538], [32.646348334291865, -14.155590897782698], [32.64639849514264, -14.155675673574304], [32.646442256196444, -14.155763760192595], [32.64647938929766, -14.15585469839834], [32.64650970084471, -14.155948014084741], [32.64653303279953, -14.156043220749606], [32.64654926351215, -14.156139822031488], [32.64655830835385, -14.156237314297524], [32.64656012016035, -14.156335189268926], [32.6465546894762, -14.15643293667102], [32.64654668917796, -14.156494377451903], [32.64655058689322, -14.156536389971048], [32.64655239870205, -14.156634264939893], [32.6465497266393, -14.1566823595881], [32.6465573089099, -14.156695174161765], [32.646595732244286, -14.156730688660044], [32.64666339729779, -14.15680296056092], [32.6467255415539, -14.156879808322397], [32.6467818410191, -14.156960831300236], [32.64683200217058, -14.157045607082122], [32.64687576348677, -14.157133693690351], [32.64691289681058, -14.157224631885638], [32.646943208539305, -14.15731794756152], [32.64696654063419, -14.157413154215472], [32.64698277144395, -14.15750975548644], [32.64699181633994, -14.157607247741337], [32.6469936281573, -14.157705122701483], [32.64698819744063, -14.157802870092436], [32.646975552494204, -14.157899980304077], [32.646955759233485, -14.157995947047421], [32.646928920842974, -14.158090269994997], [32.646895177237745, -14.158182457387907], [32.64685470433414, -14.158272028601115], [32.64680771313299, -14.158358516648601], [32.6467544486191, -14.158441470617898], [32.64671978913156, -14.158487667998141], [32.64669202938966, -14.15856350737192], [32.64665155641858, -14.158653078582287], [32.64660456513888, -14.158739566626933], [32.646551300536366, -14.158822520593727], [32.64649204030376, -14.158901507994866], [32.64642709339352, -14.158976117022458], [32.6463567984075, -14.15904595869517], [32.646281521831035, -14.159110668886509], [32.64620165612251, -14.159169910222996], [32.64611761766684, -14.159223373843357], [32.646029844606055, -14.159270781009353], [32.6459387945527, -14.159311884557834], [32.64584494220565, -14.159346470190595], [32.64574877687465, -14.159374357591329], [32.645650799928994, -14.15939540136554], [32.64555152218349, -14.159409491798893], [32.64545146123515, -14.159416555429171], [32.645351138764845, -14.159416555429171], [32.645251077816674, -14.159409491798893], [32.64515180007117, -14.15939540136554], [32.645053823125515, -14.159374357591329], [32.644957657794514, -14.159346470190595], [32.644863805447464, -14.159311884557834], [32.64477275539411, -14.159270781009353], [32.6446849823331, -14.159223373843357], [32.644600943877656, -14.159169910222996], [32.64452107816913, -14.159110668886509], [32.64444580159249, -14.15904595869517], [32.64437550660647, -14.158976117022458], [32.6443105596964, -14.158901507994866], [32.6442512994638, -14.158822520593727], [32.64424031976625, -14.158805420879727], [32.64421108728816, -14.158766457170767], [32.64413733482177, -14.158711750306846], [32.64406205837661, -14.158647040113348], [32.64399176351315, -14.158577198438362], [32.64392681671642, -14.158502589408268], [32.64386755658716, -14.158423602004572], [32.643814292077586, -14.158340648035104], [32.643767300879965, -14.158254159987504], [32.64372682797937, -14.158164588774124], [32.643693084376984, -14.158072401381101], [32.64366624598858, -14.157978078433528], [32.64364645272945, -14.157882111689897], [32.643633807783765, -14.157785001478143], [32.64362837706761, -14.157687254087076], [32.64363018888479, -14.157589379126645], [32.6436392337801, -14.157491886871693], [32.64365546458867, -14.157395285600611], [32.64366824398308, -14.1573431392556], [32.6436550474292, -14.15733335047781], [32.64357977143788, -14.157268640277035], [32.64350947699847, -14.157198798593864], [32.64344453059339, -14.157124189555358], [32.64338527082139, -14.157045202142566], [32.643332006632924, -14.156962248163662], [32.64328501571867, -14.156875760106171], [32.64324454306216, -14.15678618888256], [32.64321079966316, -14.156694001478908], [32.643183961436584, -14.156599678520646], [32.64316416829666, -14.156503711766046], [32.64315152342731, -14.15640660154315], [32.64314609274367, -14.156308854140887], [32.64314790454983, -14.156210979169314], [32.64315694939079, -14.156113486903108], [32.64317318010125, -14.15601688562094], [32.64319651205346, -14.155921678955902], [32.64322682359715, -14.155828363269276], [32.643263956693936, -14.155737425063418], [32.643307717742736, -14.155649338444785], [32.64335787858783, -14.155564562653066], [32.643414177709126, -14.155483539665791], [32.6434763215858, -14.155406691895333], [32.64354398622594, -14.155334419985987], [32.64361681885685, -14.155267100725776], [32.64369443976357, -14.155205085081544], [32.64377644426809, -14.155148696369789], [32.643862404840256, -14.155098228571031], [32.64395187332587, -14.155053944796972], [32.644044383282896, -14.15501607591926], [32.64413945241409, -14.154984819365549], [32.6442365850799, -14.15496033809023], [32.64433527488432, -14.154942759725142], [32.644435007312715, -14.154932175914379], [32.64453526241561, -14.154928641835792], [32.6445440285737, -14.15492895085043], [32.64457177107891, -14.15491759447286], [32.64466684016889, -14.154886337918807], [32.64476397279315, -14.154861856643318], [32.644862662554935, -14.154844278278059], [32.6449623949403, -14.154833694467182], [32.64506265000005, -14.154830160388652], [32.64516290505974, -14.154833694467182], [32.64526263744511, -14.154844278278059], [32.64536132720689, -14.154861856643318], [32.645458459831154, -14.154886337918807], [32.64555352892119, -14.15491759447286]]]]}, \"properties\": {\"name\": \"MTI_13\", \"status\": \"Active\", \"version\": 0, \"parentId\": \"2953\", \"geographicLevel\": 2}, \"serverVersion\": 1542965231622}";
    private final TaskRepository taskRepository;
    private final LocationRepository locationRepository;
    private final StructureRepository structureRepository;
    private final String TEST_DATA_POPULATED = "test.data.populated";


    public TestDataUtils() {
        taskRepository = EusmApplication.getInstance().getTaskRepository();
        locationRepository = EusmApplication.getInstance().getLocationRepository();
        structureRepository = EusmApplication.getInstance().getStructureRepository();
    }

    public static void getPopulateInventory() {
        String ip = "{\"stocks\":[{\"identifier\":4,\"transaction_type\":\"Inventory\",\"providerid\":\"34615126-f515-4b31-80ee-c42227f6f0c5\",\"value\":10,\"version\":1606728907714,\"deliveryDate\":\"2020-01-02T03:00:00.000+0300\",\"accountabilityEndDate\":\"2021-01-02T03:00:00.000+0300\",\"donor\":\"ADB\",\"serialNumber\":\"123434\",\"locationId\":\"f3199af5-2eaf-46df-87c9-40d59606a2fb\",\"customProperties\":{\"PO Number\":\"111\",\"UNICEF section\":\"Health\"},\"serverVersion\":5,\"type\":\"Stock\",\"id\":\"ddcaf383-882e-448b-b701-8b72cb0d4d7a\",\"revision\":\"v1\"},{\"identifier\":4,\"transaction_type\":\"Inventory\",\"providerid\":\"34615126-f515-4b31-80ee-c42227f6f0c5\",\"value\":10,\"version\":1606728907714,\"deliveryDate\":\"2020-01-02T03:00:00.000+0300\",\"accountabilityEndDate\":\"2021-01-02T03:00:00.000+0300\",\"donor\":\"ADB\",\"serialNumber\":\"123434\",\"locationId\":\"f3199af5-2eaf-46df-87c9-40d59606a2fb\",\"customProperties\":{\"PO Number\":\"111\",\"UNICEF section\":\"Health\"},\"serverVersion\":5,\"type\":\"Stock\",\"id\":\"d65e06d6-2ae8-485f-9d63-0335d0025bde\",\"revision\":\"v1\"},{\"identifier\":3,\"transaction_type\":\"Inventory\",\"providerid\":\"34615126-f515-4b31-80ee-c42227f6f0c5\",\"value\":10,\"version\":1606728907714,\"deliveryDate\":\"2020-01-02T03:00:00.000+0300\",\"accountabilityEndDate\":\"2021-01-02T03:00:00.000+0300\",\"donor\":\"ADB\",\"serialNumber\":\"123434\",\"locationId\":\"b8a7998c-5df6-49eb-98e6-f0675db71848\",\"customProperties\":{\"PO Number\":\"111\",\"UNICEF section\":\"Health\"},\"serverVersion\":5,\"type\":\"Stock\",\"id\":\"69227a92-7979-490c-b149-f28669c6b760\",\"revision\":\"v1\"}]}";
        StockResponse stockResponse = GsonUtil.getGson().fromJson(ip, new TypeToken<StockResponse>() {
        }.getType());
        new EusmStockSyncConfiguration().getStockSyncIntentServiceHelper().batchInsertStocks(stockResponse.getStocks());
    }

    public static List<TaskDetail> getStructureDetail() {

        TaskDetail s = new TaskDetail();
        s.setChecked(false);
        s.setEntityName("Solar Fridge");
        s.setHeader(false);
        s.setHasProblem(true);
        s.setNonProductTask(false);
        s.setQuantity("3");
        s.setProductSerial("3424");

        TaskDetail s1 = new TaskDetail();
        s1.setChecked(false);
        s1.setEntityName("Scale,infant,springtype,25 kg x 100g");
        s1.setHeader(false);
        s1.setNonProductTask(false);
        s1.setQuantity("3");
        s1.setProductSerial("3424");

        TaskDetail s11 = new TaskDetail();
        s11.setChecked(false);
        s11.setEntityName("Service Point Check");
        s11.setHeader(false);
        s11.setNonProductTask(true);

        TaskDetail s12 = new TaskDetail();
        s12.setChecked(false);
        s12.setEntityName("Record Gps");
        s12.setHeader(false);
        s12.setNonProductTask(true);

        TaskDetail s3 = new TaskDetail();
        s3.setChecked(true);
        s3.setHasProblem(true);
        s3.setEntityName("Timer");
        s3.setHeader(false);
        s3.setNonProductTask(false);
        s3.setQuantity("3");
        s3.setProductSerial("3424");

        List<TaskDetail> taskDetails = new ArrayList<>();
        taskDetails.add(s);
        taskDetails.add(s1);
        taskDetails.add(s11);
        taskDetails.add(s12);
        taskDetails.add(s3);

        return taskDetails;
    }

    public static List<ProductInfoQuestion> getProductInfoQuestionLIst() {
        ProductInfoQuestion p1 = new ProductInfoQuestion();
        p1.setAnswer("Solar Direct Drive Refrigerator for storage vaccines");
        p1.setQuestion("Is it there?");

        ProductInfoQuestion p2 = new ProductInfoQuestion();
        p2.setAnswer("Supplied with temperature monitoring device, 10 fuses of each type, lid with loc, plus 2 keys and 6 baskets." +
                " Compass");
        p2.setQuestion("Is it in good condition?");

        ProductInfoQuestion p3 = new ProductInfoQuestion();
        p3.setAnswer("Ambient operating temperatire: Min. +5 C , Max +43 ");
        p3.setQuestion("Is it being used appropriately?");

        return new ArrayList<>(Arrays.asList(p1, p2, p3));
    }

    public void populateTestData() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(EusmApplication.getInstance().getApplicationContext());
        if (!sharedPreferences.getBoolean(TEST_DATA_POPULATED, false)) {
            createLocations();
            createPlanDefinition();
            createTasks();
            createStructures();
            getPopulateInventory();
            sharedPreferences.edit().putBoolean(TEST_DATA_POPULATED, true).apply();
        }
    }

    private void createTasks() {
        try {
            Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeConverter("yyyy-MM-dd'T'HHmm"))
                    .serializeNulls().create();
            String tasksJSON = "[{\"identifier\":\"076885f8-582e-4dc6-8a1a-510e1c8ed5d9\",\"campaignIdentifier\":\"IRS_2019_S1\",\"planIdentifier\":\"4708ca0a-d0d6-4199-bb1b-8701803c2d02\",\"groupIdentifier\":\"3537\",\"status\":\"Ready\",\"businessStatus\":\"Visited\",\"priority\":\"routine\",\"code\":\"GPS\",\"description\":\"Record Gps\",\"focus\":\"Record Gps\",\"for\":\"f3199af5-2eaf-46df-87c9-40d59606a2fb\",\"executionPeriod\":{\"start\":\"2018-11-10T22:00:00.000\"},\"authoredOn\":\"2018-11-29T0342\",\"lastModified\":\"2018-12-03T2212\",\"owner\":\"demoMTI\",\"location\":\"f3199af5-2eaf-46df-87c9-40d59606a2fb\",\"note\":null,\"serverVersion\":1543867945202},{\"identifier\":\"634fa9fa-736d-4298-96aa-3de68ac02cae\",\"campaignIdentifier\":\"IRS_2019_S1\",\"planIdentifier\":\"4708ca0a-d0d6-4199-bb1b-8701803c2d02\",\"groupIdentifier\":\"3537\",\"status\":\"Ready\",\"businessStatus\":\"Not Visited\",\"priority\":\"routine\",\"code\":\"IRS\",\"description\":\"Service Point Check\",\"focus\":\"Service Point Check\",\"for\":\"f3199af5-2eaf-46df-87c9-40d59606a2fb\",\"executionPeriod\":{\"start\":\"2018-11-10T22:00:00.000\"},\"authoredOn\":\"2018-11-29T0342\",\"lastModified\":\"2018-12-03T2212\",\"owner\":\"demoMTI\",\"location\":\"f3199af5-2eaf-46df-87c9-40d59606a2fb\",\"note\":null,\"serverVersion\":1543867945203},{\"identifier\":\"d3b237ff-f9d8-4077-9523-c7bf3552ff87\",\"campaignIdentifier\":\"IRS_2019_S1\",\"groupIdentifier\":\"3537\",\"planIdentifier\":\"4708ca0a-d0d6-4199-bb1b-8701803c2d02\",\"status\":\"Ready\",\"businessStatus\":\"Not Visited\",\"priority\":\"routine\",\"code\":\"IRS\",\"description\":\"Spray House\",\"focus\":\"IRS Visit\",\"for\":\"f3199af5-2eaf-46df-87c9-40d59606a2fb\",\"executionPeriod\":{\"start\":\"2018-11-10T22:00:00.000\"},\"authoredOn\":\"2018-11-29T0342\",\"lastModified\":\"2018-12-03T2212\",\"location\":\"f3199af5-2eaf-46df-87c9-40d59606a2fb\",\"owner\":\"demoMTI\",\"note\":null,\"serverVersion\":1543867945204},{\"identifier\":\"c6dd4abc-fb3e-4f72-afb8-923fc43f44d7\",\"campaignIdentifier\":\"IRS_2019_S1\",\"groupIdentifier\":\"3537\",\"status\":\"Ready\",\"planIdentifier\":\"4708ca0a-d0d6-4199-bb1b-8701803c2d02\",\"businessStatus\":\"Not Visited\",\"priority\":\"routine\",\"code\":\"IRS\",\"description\":\"product\",\"focus\":\"product\",\"for\":\"4\",\"executionPeriod\":{\"start\":\"2018-11-10T22:00:00.000\"},\"authoredOn\":\"2018-12-03T2212\",\"lastModified\":\"2018-12-03T2212\",\"owner\":\"demoMTI\",\"location\":\"f3199af5-2eaf-46df-87c9-40d59606a2fb\",\"note\":null,\"serverVersion\":1543867945195},{\"identifier\":\"2caa810d-d4da-4e67-838b-badb9bd86e06\",\"campaignIdentifier\":\"IRS_2019_S1\",\"groupIdentifier\":\"3537\",\"status\":\"Ready\",\"planIdentifier\":\"4708ca0a-d0d6-4199-bb1b-8701803c2d02\",\"businessStatus\":\"Not Visited\",\"priority\":\"routine\",\"code\":\"IRS\",\"description\":\"Service Point Check\",\"focus\":\"Service Point Check\",\"for\":\"b8a7998c-5df6-49eb-98e6-f0675db71848\",\"executionPeriod\":{\"start\":\"2018-11-10T22:00:00.000\"},\"authoredOn\":\"2018-12-03T2212\",\"lastModified\":\"2018-12-03T2212\",\"owner\":\"demoMTI\",\"location\":\"b8a7998c-5df6-49eb-98e6-f0675db71848\",\"note\":null,\"serverVersion\":1543867945196},{\"identifier\":\"bbf32ca5-9b83-444d-882f-2085974e90b5\",\"campaignIdentifier\":\"IRS_2019_S1\",\"groupIdentifier\":\"3537\",\"status\":\"Ready\",\"planIdentifier\":\"4708ca0a-d0d6-4199-bb1b-8701803c2d02\",\"businessStatus\":\"Not Visited\",\"priority\":\"routine\",\"code\":\"IRS\",\"description\":\"Record Gps\",\"focus\":\"Record Gps\",\"for\":\"b8a7998c-5df6-49eb-98e6-f0675db71848\",\"executionPeriod\":{\"start\":\"2018-11-10T22:00:00.000\"},\"authoredOn\":\"2018-12-03T2212\",\"lastModified\":\"2018-12-03T2212\",\"owner\":\"demoMTI\",\"location\":\"b8a7998c-5df6-49eb-98e6-f0675db71848\",\"note\":null,\"serverVersion\":1543867945196},{\"identifier\":\"6c303b8b-e47c-45e9-8ab5-3374c8f539a3\",\"campaignIdentifier\":\"IRS_2019_S1\",\"groupIdentifier\":\"3537\",\"status\":\"Ready\",\"planIdentifier\":\"4708ca0a-d0d6-4199-bb1b-8701803c2d02\",\"businessStatus\":\"Not Visited\",\"priority\":\"routine\",\"code\":\"IRS\",\"description\":\"product\",\"focus\":\"product\",\"for\":\"3\",\"executionPeriod\":{\"start\":\"2018-11-10T22:00:00.000\"},\"authoredOn\":\"2018-12-03T2212\",\"lastModified\":\"2018-12-03T2212\",\"owner\":\"demoMTI\",\"location\":\"b8a7998c-5df6-49eb-98e6-f0675db71848\",\"note\":null,\"serverVersion\":1543867945196}]";
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

    private void createLocations() {
        try {

//            Location location = gson.fromJson(locationJSon, Location.class);
//            locationRepository.addOrUpdate(location);
//
            String location2JSon = "    {\n" +
                    "        \"type\": \"Feature\",\n" +
                    "        \"id\": \"11fccc09-14f9-4dc7-9d1e-959bb58ce807\",\n" +
                    "        \"properties\": {\n" +
                    "            \"status\": \"Active\",\n" +
                    "            \"parentId\": \"7cd51ffb-ef6e-4e98-9000-34efc64c6eb8\",\n" +
                    "            \"name\": \"Ambinanindilana\",\n" +
                    "            \"geographicLevel\": 0,\n" +
                    "            \"version\": 0\n" +
                    "        },\n" +
                    "        \"serverVersion\": 5565,\n" +
                    "        \"locationTags\": [\n" +
                    "            {\n" +
                    "                \"id\": 4,\n" +
                    "                \"name\": \"Commune\"\n" +
                    "            }\n" +
                    "        ]\n" +
                    "    }";
            Location location2 = gson.fromJson(location2JSon, Location.class);
            locationRepository.addOrUpdate(location2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createStructures() {
        try {
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                    .registerTypeAdapter(DateTime.class, new DateTimeTypeConverter())
                    .registerTypeAdapter(LocationProperty.class, new PropertiesConverter()).create();
            String locationJSon = "[{\"type\":\"Feature\",\"id\":\"f3199af5-2eaf-46df-87c9-40d59606a2fb\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[49.54933,-16.08306]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"8e74d042-4a71-4694-a652-bc3ba6369101\",\"name\":\"EPP Ambodisatrana 2\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18479},{\"type\":\"Feature\",\"id\":\"b8a7998c-5df6-49eb-98e6-f0675db71848\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[49.58436,-16.433]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"663d7935-35e7-4ccf-aaf5-6e16f2042570\",\"name\":\"Ambatoharanana\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18480},{\"type\":\"Feature\",\"id\":\"45e4bd97-fe11-458b-b481-294b7d7e8270\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[49.52125,-16.78147]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"c38e0c1e-3d72-424b-ac37-29e8d3e82026\",\"name\":\"Ambahoabe\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18481},{\"type\":\"Feature\",\"id\":\"b2a9b18c-5fa0-4d48-bee8-5db0b57c18ca\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[49.4302,-17.1601]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"51253521-e300-442a-9c54-2f10bcb59408\",\"name\":\"Ampihaonana\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18482},{\"type\":\"Feature\",\"id\":\"b2f87aab-bb70-42fc-910e-d55de623fd23\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[49.20031,-17.24602]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"0c11a6bc-6b78-475b-8015-e1b88c838c49\",\"name\":\"Ambodihasina\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18483},{\"type\":\"Feature\",\"id\":\"44e3cdb3-9f08-4498-96ac-c76f2bed7319\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[49.17101,-17.2573]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"0c11a6bc-6b78-475b-8015-e1b88c838c49\",\"name\":\"Ambodilaitra\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18484},{\"type\":\"Feature\",\"id\":\"ea4255eb-9f8d-4dd7-a2b8-4174b423858e\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[49.32238,-17.3515]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"7cd51ffb-ef6e-4e98-9000-34efc64c6eb8\",\"name\":\"Mahanoro\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18485},{\"type\":\"Feature\",\"id\":\"74b7f4d8-9c39-4837-b95f-f997e1cc1ccd\",\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"7cd51ffb-ef6e-4e98-9000-34efc64c6eb8\",\"name\":\"AMBALABE II\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18486},{\"type\":\"Feature\",\"id\":\"11b49baf-6654-4e0f-8103-9ce546010ab8\",\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"7cd51ffb-ef6e-4e98-9000-34efc64c6eb8\",\"name\":\"TANETILAVA ET VOHITSOA\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18487},{\"type\":\"Feature\",\"id\":\"e855ac03-b2fd-4e97-b11d-286d90c94ea5\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[49.31232,-17.37877]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"7cd51ffb-ef6e-4e98-9000-34efc64c6eb8\",\"name\":\"ANDRATANIMOINA\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18488},{\"type\":\"Feature\",\"id\":\"2390fcef-f15d-4820-bad3-3909c8275e29\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[49.28838,-17.44301]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"eadf078b-2054-4723-8af1-5d16c18ac240\",\"name\":\"MAROMITETY\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18489},{\"type\":\"Feature\",\"id\":\"3a77c1c3-a633-40f6-8cba-ecb9f8bc2d8d\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[49.18185,-17.63283]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"1fec2789-7741-499d-a28f-fc9e6c354ea5\",\"name\":\"EPP Miarinarivo\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18490},{\"type\":\"Feature\",\"id\":\"d7e4c785-fe54-4e8f-9e73-c5ed7f302246\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[47.60943,-22.52687]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"22cc7ef7-4aba-4e19-be91-cd1e40e57195\",\"name\":\"EPP Ambalatany\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18491},{\"type\":\"Feature\",\"id\":\"957070da-bee0-411d-a337-097380c161ce\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[47.82874,-22.81754]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"cacefb35-c11d-4556-a969-6b7cf3b245aa\",\"name\":\"EPP Ampataka\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18492},{\"type\":\"Feature\",\"id\":\"0bb4ba00-076c-4af6-a158-054fcd6efa1a\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[47.25314,-23.32697]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"599a5e27-9b21-4c63-b7ee-ae935abcdfb2\",\"name\":\"BEVATA\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18493},{\"type\":\"Feature\",\"id\":\"565231ba-2da2-4f85-a236-db86093c6d33\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[47.00884,-23.65442]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"7a781fab-5cf6-44df-a9e6-9203b3fb9e2a\",\"name\":\"EPP Ankazovelo\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18494},{\"type\":\"Feature\",\"id\":\"5d6e98c1-da57-405b-99b3-b076120e95cd\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[46.83199,-24.85807]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"b26243a3-a312-4841-a6cd-4f7718bfb508\",\"name\":\"MANDISO\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18495},{\"type\":\"Feature\",\"id\":\"395ca301-a9dd-4b7b-8eb0-2887da99c80d\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[47.01117,-24.8953]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"b5b1e61b-f82e-48bb-8077-c5251a677fe2\",\"name\":\"MANDROMONDROMOTRA\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18496},{\"type\":\"Feature\",\"id\":\"043b9f8f-72fb-4875-9f5e-4552af653bc8\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[46.92783,-24.92968]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"333e8803-a727-4874-9913-21a7136c220b\",\"name\":\"IFARANTSA\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18497},{\"type\":\"Feature\",\"id\":\"fb8d65b1-f007-49b2-b0a1-b9775afab4d3\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[46.74732,-24.97733]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"449f3116-932e-47b8-99f3-d5dab0a81f7f\",\"name\":\"TAVIALA\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18498},{\"type\":\"Feature\",\"id\":\"e15861ac-3a56-4fdf-b0a4-4a7dbba15418\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[46.68708,-25.00433]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"e10efd4e-f85a-4d38-9fd9-b88369d3a367\",\"name\":\"RANOPISO\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18499},{\"type\":\"Feature\",\"id\":\"5c5c6f56-85bc-4e18-9bee-ade736dedd84\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[47.26532,-23.42273]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"ba2ac9bc-d750-4d66-8a12-c5d3f651267d\",\"name\":\"EPP Ranomena\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18500},{\"type\":\"Feature\",\"id\":\"49e81290-ca8f-4be6-80dd-4648a559542e\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[47.014,-23.59364]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"19d81fa8-2435-43c8-b6df-30cfa7a975ec\",\"name\":\"EPP Centre Midongy\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18501},{\"type\":\"Feature\",\"id\":\"6f43713f-5227-4020-bb60-b6857d8a2475\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[46.98561,-23.82669]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"4fa9f736-89a7-4612-8a55-8fc5cc59bc9d\",\"name\":\"EPP Befotaka\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18502},{\"type\":\"Feature\",\"id\":\"de2c14e1-b294-4d34-baae-eb97936c6058\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[46.3777,-15.654]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"d32b989c-69a4-4e30-9c15-2d4f701150dd\",\"name\":\"ANKARAOBATO\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18503}]";
            List<Location> structures = gson.fromJson(locationJSon, new TypeToken<List<Location>>() {
            }.getType());
            for (Location structure : structures) {
                try {
                    structureRepository.addOrUpdate(structure);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createPlanDefinition() {

        try {
            Gson gson = PlanDefinitionRepository.gson;


            String planDefinitionJSON = "{\"identifier\":\"4708ca0a-d0d6-4199-bb1b-8701803c2d02\",\"version\":\"1\",\"name\":\"Plan A\",\"title\":\"Plan A\",\"status\":\"active\",\"date\":\"2020-03-27\",\"effectivePeriod\":{\"start\":\"2020-04-01\",\"end\":\"2020-07-31\"},\"jurisdiction\":[{\"code\":\"32d0504c-f284-4599-9cc3-638c01a04c65\"}],\"goal\":[{\"id\":\"BCC_complete\",\"description\":\"Complete BCC for the operational area\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"Number of BCC communication activities that happened\",\"detail\":{\"detailQuantity\":{\"value\":1,\"comparator\":\">=\",\"unit\":\"each\"}},\"due\":\"2019-04-01\"}]}],\"action\":[{\"identifier\":\"990af508-f1a9-4793-841f-49a7b6438827\",\"prefix\":1,\"title\":\"Perform BCC\",\"description\":\"Perform check on the operational area\",\"code\":\"BCC\",\"timingPeriod\":{\"start\":\"2019-04-01\",\"end\":\"2019-04-10\"},\"reason\":\"Routine\",\"goalId\":\"BCC_complete\",\"subjectCodableConcept\":{\"text\":\"Operational_Area\"},\"taskTemplate\":\"Action1_Perform_BCC\"}]}";

            PlanDefinition planDefinition = gson.fromJson(planDefinitionJSON, PlanDefinition.class);
            EusmApplication.getInstance().getPlanDefinitionRepository().addOrUpdate(planDefinition);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
