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
import org.smartregister.eusm.model.StructureTaskDetail;
import org.smartregister.repository.LocationRepository;
import org.smartregister.repository.PlanDefinitionRepository;
import org.smartregister.repository.StructureRepository;
import org.smartregister.repository.TaskRepository;
import org.smartregister.util.DateTimeTypeConverter;
import org.smartregister.util.PropertiesConverter;

import java.util.ArrayList;
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

    public static List<StructureTaskDetail> getStructureDetail() {

        StructureTaskDetail s = new StructureTaskDetail();
        s.setChecked(true);
        s.setProductName("Solar Fridge");
        s.setHeader(false);
        s.setNonProductTask(false);
        s.setQuantity("3");
        s.setProductSerial("3424");

        StructureTaskDetail s1 = new StructureTaskDetail();
        s1.setChecked(true);
        s1.setProductName("Scale,infant,springtype,25 kg x 100g");
        s1.setHeader(false);
        s1.setNonProductTask(false);
        s1.setQuantity("3");
        s1.setProductSerial("3424");

        StructureTaskDetail s11 = new StructureTaskDetail();
        s11.setChecked(true);
        s11.setProductName("Service Point Check");
        s11.setHeader(false);
        s11.setNonProductTask(true);

        StructureTaskDetail s3 = new StructureTaskDetail();
        s3.setChecked(true);
        s3.setProductName("Timer");
        s3.setHeader(false);
        s3.setNonProductTask(false);
        s3.setQuantity("3");
        s3.setProductSerial("3424");

        List<StructureTaskDetail> structureTaskDetails = new ArrayList<>();
        structureTaskDetails.add(s);
        structureTaskDetails.add(s1);
        structureTaskDetails.add(s11);
        structureTaskDetails.add(s3);

        return structureTaskDetails;
    }

    public void populateTestData() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(EusmApplication.getInstance().getApplicationContext());
        if (!sharedPreferences.getBoolean(TEST_DATA_POPULATED, false)) {
//            createPlanDefinition();
            createTasks();
            createStructures();
            sharedPreferences.edit().putBoolean(TEST_DATA_POPULATED, true).apply();
        }
    }

    private void createTasks() {
        try {

            Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeConverter("yyyy-MM-dd'T'HHmm"))
                    .serializeNulls().create();
            String tasksJSON = "[{\"identifier\":\"076885f8-582e-4dc6-8a1a-510e1c8ed5d9\",\"campaignIdentifier\":\"IRS_2019_S1\",\"planIdentifier\":\"1\",\"groupIdentifier\":\"3537\",\"status\":\"Ready\",\"businessStatus\":\"Not Visited\",\"priority\":3,\"code\":\"IRS\",\"description\":\"Spray House\",\"focus\":\"IRS Visit\",\"for\":\"1\",\"executionStartDate\":\"2018-11-10T2200\",\"executionEndDate\":null,\"authoredOn\":\"2018-11-29T0342\",\"lastModified\":\"2018-12-03T2212\",\"owner\":\"demoMTI\",\"note\":null,\"serverVersion\":1543867945202},{\"identifier\":\"634fa9fa-736d-4298-96aa-3de68ac02cae\",\"campaignIdentifier\":\"IRS_2019_S1\",\"planIdentifier\":\"2\",\"groupIdentifier\":\"3537\",\"status\":\"Ready\",\"businessStatus\":\"Not Visited\",\"priority\":3,\"code\":\"IRS\",\"description\":\"Spray House\",\"focus\":\"IRS Visit\",\"for\":\"2\",\"executionStartDate\":\"2018-11-10T2200\",\"executionEndDate\":null,\"authoredOn\":\"2018-11-29T0342\",\"lastModified\":\"2018-12-03T2212\",\"owner\":\"demoMTI\",\"note\":null,\"serverVersion\":1543867945203},{\"identifier\":\"d3b237ff-f9d8-4077-9523-c7bf3552ff87\",\"campaignIdentifier\":\"IRS_2019_S1\",\"groupIdentifier\":\"3537\",\"planIdentifier\":\"3\",\"status\":\"Ready\",\"businessStatus\":\"Not Visited\",\"priority\":3,\"code\":\"IRS\",\"description\":\"Spray House\",\"focus\":\"IRS Visit\",\"for\":\"3\",\"executionStartDate\":\"2018-11-10T2200\",\"executionEndDate\":null,\"authoredOn\":\"2018-11-29T0342\",\"lastModified\":\"2018-12-03T2212\",\"owner\":\"demoMTI\",\"note\":null,\"serverVersion\":1543867945204},{\"identifier\":\"c6dd4abc-fb3e-4f72-afb8-923fc43f44d7\",\"campaignIdentifier\":\"IRS_2019_S1\",\"groupIdentifier\":\"3537\",\"status\":\"Ready\",\"planIdentifier\":\"4\",\"businessStatus\":\"Not Visited\",\"priority\":3,\"code\":\"IRS\",\"description\":\"Spray House\",\"focus\":\"IRS Visit\",\"for\":\"4\",\"executionStartDate\":\"2018-11-10T2200\",\"executionEndDate\":null,\"authoredOn\":\"2018-12-03T2212\",\"lastModified\":\"2018-12-03T2212\",\"owner\":\"demoMTI\",\"note\":null,\"serverVersion\":1543867945195},{\"identifier\":\"2caa810d-d4da-4e67-838b-badb9bd86e06\",\"campaignIdentifier\":\"IRS_2019_S1\",\"groupIdentifier\":\"3537\",\"status\":\"Ready\",\"planIdentifier\":\"5\",\"businessStatus\":\"Not Visited\",\"priority\":3,\"code\":\"IRS\",\"description\":\"Spray House\",\"focus\":\"IRS Visit\",\"for\":\"5\",\"executionStartDate\":\"2018-11-10T2200\",\"executionEndDate\":null,\"authoredOn\":\"2018-12-03T2212\",\"lastModified\":\"2018-12-03T2212\",\"owner\":\"demoMTI\",\"note\":null,\"serverVersion\":1543867945196}]";
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

            Location location = gson.fromJson(locationJSon, Location.class);
            locationRepository.addOrUpdate(location);

            String location2JSon = "{\"id\": \"3366\", \"type\": \"Feature\", \"geometry\": {\"type\": \"MultiPolygon\", \"coordinates\": [[[[32.60019625316408, -14.16712789883206], [32.60026392129685, -14.167200170670068], [32.600326068380916, -14.167277018364924], [32.60038237040833, -14.167358041272504], [32.60043253384264, -14.167442816981005], [32.600461865346055, -14.167501855260806], [32.600467305754925, -14.167504082164154], [32.6005567791152, -14.167548365892056], [32.60064274437109, -14.16759883363829], [32.60072475334385, -14.167655222291442], [32.600802378479834, -14.167717237871212], [32.60087521507938, -14.167784557061506], [32.60094288340667, -14.167856828895705], [32.60100503066945, -14.167933676586241], [32.6010613328587, -14.168014699489389], [32.60111149643739, -14.16809947519317], [32.601155259871, -14.168187561720227], [32.60119239499158, -14.168278499831782], [32.60122270818726, -14.168371815421606], [32.601246041411336, -14.168467021987961], [32.60126227300679, -14.16856362316997], [32.60127131834104, -14.168661115335109], [32.601273130246504, -14.168758990205273], [32.60126809520835, -14.168849611326149], [32.601268746132114, -14.168884772675423], [32.60126331515021, -14.16898251997554], [32.601250669585276, -14.1690796300968], [32.6012308753564, -14.169175596751185], [32.60120403565332, -14.169269919610993], [32.6011702903973, -14.169362106918296], [32.60112981551378, -14.169451678048457], [32.60108282201332, -14.169538166015567], [32.60102955489338, -14.169621119908012], [32.60097029185993, -14.169700107238382], [32.60090534188031, -14.169774716199182], [32.6008350435718, -14.169844557809366], [32.600817649001606, -14.169859510027148], [32.60076264386304, -14.169914157803586], [32.60068736370561, -14.169978867936662], [32.60060749419761, -14.170038109219828], [32.600559934780954, -14.170068364123377], [32.60056014202427, -14.170070597813773], [32.6005619539411, -14.170168472672398], [32.60055652292868, -14.170266219962112], [32.60054387729263, -14.170363330073084], [32.600524082952795, -14.170459296717066], [32.60049724309874, -14.170553619566872], [32.600463497653145, -14.170645806864396], [32.60042302254208, -14.17073537798495], [32.60037602877764, -14.170821865942909], [32.600344492416745, -14.170870977832461], [32.60034438539213, -14.170871496706125], [32.60031754548958, -14.170965819552745], [32.60028379998311, -14.171058006847087], [32.600243324799, -14.171147577964515], [32.600196330949636, -14.17123406591946], [32.600143063434295, -14.171317019800309], [32.60008379996071, -14.171396007119537], [32.600018849498774, -14.171470616069879], [32.59994855066821, -14.171540457670288], [32.59987326997469, -14.17160516779455], [32.59979339989791, -14.171664409069702], [32.59970935684628, -14.171717872634868], [32.59964225969093, -14.17175411050408], [32.59958840035244, -14.171794059057728], [32.599504357253004, -14.171847522622325], [32.59941657934149, -14.171894929738581], [32.59932552425658, -14.17193603324409], [32.59923166672291, -14.171970618840756], [32.599135496077615, -14.171998506212216], [32.599037513717285, -14.172019549964542], [32.59893823048531, -14.17203364038323], [32.598838164007354, -14.172040704006005], [32.59873783599283, -14.172040704006005], [32.5986377695147, -14.17203364038323], [32.598538486282784, -14.172019549964542], [32.59844050392257, -14.171998506212216], [32.59836476694522, -14.171976544154969], [32.598302157782705, -14.172016372605977], [32.598214379806386, -14.172063779721551], [32.59812332465395, -14.172104883226435], [32.59802946705104, -14.172139468822703], [32.59793329633447, -14.172167356193764], [32.59783531390183, -14.17218839994575], [32.59773603059636, -14.172202490364267], [32.59763596404451, -14.172209553987042], [32.597535635955744, -14.172209553987042], [32.59743556940372, -14.172202490364267], [32.59735870544443, -14.17219158172912], [32.59734235282223, -14.172210366013356], [32.597272053763895, -14.172280207609443], [32.59721759844722, -14.172327016406367], [32.59719788529895, -14.172349660918595], [32.59712758619781, -14.172419502513943], [32.59705230521461, -14.172484212633432], [32.59697243483037, -14.172543453904321], [32.59691580121147, -14.17257948095516], [32.596914419626664, -14.172580505700747], [32.596830376237726, -14.172633969261877], [32.59674259802403, -14.172681376375007], [32.59665154262529, -14.172722479877844], [32.59655768476852, -14.172757065472238], [32.59646151379189, -14.17278495284199], [32.59636353109419, -14.172805996592896], [32.59626424752013, -14.172820087010617], [32.596164180697365, -14.172827150632937], [32.596063852337345, -14.172827150632937], [32.595963785514634, -14.172820087010617], [32.595864501940746, -14.172805996592896], [32.59576651924305, -14.17278495284199], [32.595670348266246, -14.172757065472238], [32.59562983666279, -14.17274213738909], [32.59557352665137, -14.17277254919497], [32.595482471216194, -14.172813652697524], [32.59538861332191, -14.172848238291575], [32.595346903973216, -14.172860333037871], [32.59534239579221, -14.172862368083766], [32.59524853787786, -14.172896953677704], [32.59515236684228, -14.172924841047173], [32.595054384084555, -14.17294588479774], [32.59495510044973, -14.172959975215402], [32.594855033565686, -14.172967038837665], [32.59475470514422, -14.172967038837665], [32.59465463826023, -14.172959975215402], [32.594555354625406, -14.17294588479774], [32.59445737186763, -14.172924841047173], [32.59442506589232, -14.172915473063707], [32.59433007182053, -14.172915473063707], [32.59423000495905, -14.17290840944133], [32.594130721346794, -14.172894319023781], [32.59403273861107, -14.172873275273044], [32.593936567597375, -14.172845387903461], [32.59384270970417, -14.172810802309353], [32.59375165427024, -14.1727696988068], [32.59366387602261, -14.172722291693898], [32.59357983260116, -14.172668828133279], [32.593499962173034, -14.172609586863073], [32.5934246811484, -14.172544876744151], [32.5934126306829, -14.172532904709552], [32.59335004540372, -14.17247910759323], [32.593279746284274, -14.172409265998223], [32.593214795555234, -14.172334657053792], [32.59315553183807, -14.172255669740649], [32.59310226410371, -14.172172715866392], [32.59305527006142, -14.172086227918097], [32.593014794710705, -14.171996656807604], [32.592981049065706, -14.171904469520424], [32.59295420905283, -14.171810146681306], [32.592934414595895, -14.17171418004801], [32.59292176888527, -14.171617069947953], [32.59291633784067, -14.171519322669322], [32.59291814976836, -14.171421447821611], [32.592927195211864, -14.171323955678812], [32.59294342700377, -14.17122735451903], [32.5929667605099, -14.171132147974616], [32.59299707407205, -14.171038832406278], [32.593034209641694, -14.170947894315642], [32.59307797360429, -14.17085980780882], [32.59312813778927, -14.170775032124592], [32.59318444065922, -14.170694009240147], [32.593246588673246, -14.170617161567233], [32.59331425781857, -14.170544889749747], [32.59338709529839, -14.17047757057497], [32.59346472137269, -14.170415555009466], [32.59354673133668, -14.17035916636939], [32.593632697631556, -14.170308698634809], [32.59372217207328, -14.17026441491714], [32.59381468818895, -14.17022654608763], [32.59390976364897, -14.170195289573538], [32.59400690278124, -14.17017080832937], [32.59410559915556, -14.17015322998668], [32.594108641289544, -14.170152907171083], [32.59410452416977, -14.170078807274818], [32.594104747347494, -14.170066751816304], [32.59409492477396, -14.170019130079421], [32.594082279157185, -14.169922019965721], [32.59407684815306, -14.169824272673221], [32.59407866006705, -14.169726397811754], [32.59408770544342, -14.169628905655198], [32.5941039371147, -14.169532304481717], [32.59412727044752, -14.169437097923888], [32.59415758378457, -14.169343782342306], [32.59419471907852, -14.16925284423888], [32.59423848271643, -14.169164757719555], [32.59428864652886, -14.169079982023387], [32.59434494898084, -14.168998959127517], [32.59440709653352, -14.168922111443745], [32.594474765176464, -14.16884983961597], [32.59454760211582, -14.168782520431701], [32.59462522761384, -14.168720504857387], [32.594707236969036, -14.168664116209353], [32.59479320262607, -14.168613648467609], [32.59488267640358, -14.168569364743629], [32.594975191832525, -14.168531495908722], [32.59507026658701, -14.168500239390308], [32.59516740499827, -14.168475758142618], [32.59526610063994, -14.168458179797423], [32.59529244876699, -14.168455383848364], [32.59529777359045, -14.168442344202102], [32.59534153707289, -14.16835425767692], [32.595391700707346, -14.168269481975013], [32.595448002959465, -14.16818845907363], [32.59551015029154, -14.168111611384685], [32.59557781869437, -14.16803933955202], [32.5956506553752, -14.167972020363091], [32.595728280597704, -14.16791000478474], [32.59578277701842, -14.167872533564658], [32.59578663074716, -14.167869454781108], [32.59586863979683, -14.167813066128982], [32.595870154342826, -14.167812176983087], [32.59588304190657, -14.167790397169766], [32.595939344040566, -14.167709374265083], [32.59600149124241, -14.167632526573069], [32.59606915950308, -14.167560254737506], [32.59614199603117, -14.167492935545964], [32.59621962109099, -14.167430919964941], [32.59630162998314, -14.167374531310939], [32.59638759515462, -14.16732406356368], [32.59647706842696, -14.167279779834924], [32.596569583333626, -14.167241910996037], [32.59666465755111, -14.1672106544741], [32.59676179541406, -14.16718617322385], [32.59686049049849, -14.167168594876781], [32.59696022826284, -14.167158011076763], [32.597060488729475, -14.167154477001755], [32.59716074919606, -14.167158011076763], [32.5972604869604, -14.167168594876781], [32.59731452694961, -14.167178219810861], [32.59732387518306, -14.167149442233491], [32.597361010120146, -14.167058504113466], [32.59740477333741, -14.166970417578053], [32.59745493666792, -14.166885641866255], [32.59751123857881, -14.166804618955549], [32.59757338553447, -14.16672777125768], [32.59764105352729, -14.166655499416716], [32.59771388976673, -14.16658818022], [32.597791514519024, -14.166526164634373], [32.59787352308643, -14.166469775975994], [32.597959487917244, -14.166419308225041], [32.59804896083539, -14.166375024492819], [32.59814147537559, -14.166337155650975], [32.59823654921649, -14.166305899126767], [32.598333686694666, -14.166281417874698], [32.59843238138813, -14.16626383952632], [32.598480894615356, -14.16625869146247], [32.59854368499213, -14.166247507971715], [32.59864342235414, -14.166236924170786], [32.59874368241662, -14.16623339009561], [32.59884394247893, -14.166236924170786], [32.598943679841106, -14.166247507971715], [32.59904237452752, -14.166265086320092], [32.59913951199871, -14.166289567572331], [32.59923458583284, -14.166320824096541], [32.5993271003665, -14.166358692938498], [32.599416573278056, -14.166402976670607], [32.59950253810285, -14.166453444421673], [32.599584546664396, -14.166509833080223], [32.599662171411175, -14.16657184866585], [32.59973500764527, -14.166639167862566], [32.59980267563332, -14.166711439703702], [32.59986482258454, -14.166788287401683], [32.59992112449134, -14.166869310312448], [32.59996455353569, -14.16694270512846], [32.60004579186107, -14.166998564054833], [32.600123416774004, -14.167060579638072], [32.60019625316408, -14.16712789883206]]]]}, \"properties\": {\"name\": \"MKB_5\", \"status\": \"Active\", \"version\": 0, \"parentId\": \"2953\", \"geographicLevel\": 2}, \"serverVersion\": 1542965231623}";
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
            String locationJSon = "[{\"id\":\"1\",\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[32.6454013,-14.1580617]},\"properties\":{\"status\":\"Active\",\"name\":\"Ankorona\",\"type\":\"CBS\",\"version\":0,\"parentId\":\"3537\",\"geographicLevel\":4},\"serverVersion\":1542970626312},{\"id\":\"2\",\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[32.6454013,-14.1580617]},\"properties\":{\"status\":\"Active\",\"name\":\"Place 2\",\"type\":\"Site Communautaire\",\"version\":0,\"parentId\":\"3537\",\"geographicLevel\":4},\"serverVersion\":1542970626312},{\"id\":\"3\",\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[32.6454013,-14.1580617]},\"properties\":{\"status\":\"Active\",\"name\":\"Place 3\",\"type\":\"NGO Partner\",\"version\":0,\"parentId\":\"3537\",\"geographicLevel\":4},\"serverVersion\":1542970626312},{\"id\":\"4\",\"type\":\"Feature\",\"properties\":{\"status\":\"Active\",\"name\":\"A Place 4\",\"type\":\"NGO Partner\",\"version\":0,\"parentId\":\"3537\",\"geographicLevel\":4},\"serverVersion\":1542970626312},{\"id\":\"5\",\"type\":\"Feature\",\"properties\":{\"status\":\"Active\",\"name\":\"Place 5\",\"type\":\"NGO Partner\",\"version\":0,\"parentId\":\"3537\",\"geographicLevel\":4},\"serverVersion\":1542970626312}]";
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

            String planDefinitionJSON = "{\"identifier\":\"4708ca0a-d0d6-4199-bb1b-8701803c2d02\",\"version\":\"1\",\"name\":\"2019_IRS_Season\",\"title\":\"2019 IRS Operational Plan\",\"status\":\"active\",\"date\":\"2019-03-27\",\"effectivePeriod\":{\"start\":\"2019-04-01\",\"end\":\"2019-07-31\"},\"jurisdiction\":[{\"code\":\"3421\"},{\"code\":\"3429\"},{\"code\":\"3436\"},{\"code\":\"3439\"}],\"goal\":[{\"id\":\"BCC_complete\",\"description\":\"Complete BCC for the operational area\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"Number of BCC communication activities that happened\",\"detail\":{\"detailQuantity\":{\"value\":1.0,\"comparator\":\">=\",\"unit\":\"each\"}},\"due\":\"2019-04-01\"}]},{\"id\":\"90_percent_of_structures_sprayed\",\"description\":\"Spray 90 % of structures in the operational area\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"Percent of structures sprayed\",\"detail\":{\"detailQuantity\":{\"value\":90.0,\"comparator\":\">=\",\"unit\":\"percent\"}},\"due\":\"2019-05-31\"}]}],\"action\":[{\"identifier\":\"990af508-f1a9-4793-841f-49a7b6438827\",\"prefix\":1,\"title\":\"Perform BCC\",\"description\":\"Perform BCC for the operational area\",\"code\":\"BCC\",\"timingPeriod\":{\"start\":\"2019-04-01\",\"end\":\"2019-04-10\"},\"reason\":\"Routine\",\"goalId\":\"BCC_complete\",\"subjectCodableConcept\":{\"text\":\"Operational_Area\"},\"taskTemplate\":\"Action1_Perform_BCC\"},{\"identifier\":\"8276be06-97d3-4815-8d39-0bc158dc1d91\",\"prefix\":2,\"title\":\"Spray Structures\",\"description\":\"Visit each structure in the operational area and attempt to spray\",\"code\":\"IRS\",\"timingPeriod\":{\"start\":\"2019-04-10\",\"end\":\"2019-07-31\"},\"reason\":\"Routine\",\"goalId\":\"90_percent_of_structures_sprayed\",\"subjectCodableConcept\":{\"text\":\"Residential_Structure\"},\"taskTemplate\":\"Action2_Spray_Structures\"}]}";


            PlanDefinition planDefinition = gson.fromJson(planDefinitionJSON, PlanDefinition.class);
            EusmApplication.getInstance().getPlanDefinitionRepository().addOrUpdate(planDefinition);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
