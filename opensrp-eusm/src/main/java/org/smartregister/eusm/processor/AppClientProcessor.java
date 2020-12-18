package org.smartregister.eusm.processor;

import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.smartregister.domain.Client;
import org.smartregister.domain.Event;
import org.smartregister.domain.Location;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.jsonmapping.ClientClassification;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.eusm.util.AppUtils;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.StructureRepository;
import org.smartregister.repository.TaskRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.tasking.util.PreferencesUtil;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by samuelgithengi on 12/7/18.
 */
public class AppClientProcessor extends ClientProcessorForJava {

    private static AppClientProcessor instance;
    private final EventClientRepository eventClientRepository;
    private final TaskRepository taskRepository;
    private final StructureRepository structureRepository;
    private final EusmApplication eusmApplication;

    public AppClientProcessor(Context context) {
        super(context);
        eusmApplication = EusmApplication.getInstance();
        eventClientRepository = eusmApplication.getContext().getEventClientRepository();
        taskRepository = eusmApplication.getTaskRepository();
        structureRepository = eusmApplication.getStructureRepository();
    }


    public static AppClientProcessor getInstance(Context context) {
        if (instance == null) {
            instance = new AppClientProcessor(context);
        }
        return instance;
    }

    //    @Override
//    public synchronized void processClient(List<EventClient> eventClientList) {
//        processClient(eventClientList, false);
//    }
//
    public void processClient(List<EventClient> eventClients, boolean localSubmission) {
        ClientClassification clientClassification = assetJsonToJava("ec_client_classification.json", ClientClassification.class);
        if (clientClassification == null) {
            return;
        }

        ArrayList<Client> clients = new ArrayList<>();
        Location operationalArea = AppUtils.getOperationalAreaLocation(PreferencesUtil.getInstance().getCurrentOperationalArea());
        String operationalAreaLocationId = operationalArea == null ? null : operationalArea.getId();
        boolean hasSyncedEventsInTarget = false;
        if (!eventClients.isEmpty()) {
            String operationalAreaId = null;
            for (EventClient eventClient : eventClients) {
                Event event = eventClient.getEvent();
                if (event == null || event.getEventType() == null) {
                    continue;
                }
                String eventType = event.getEventType();

                try {
                    processEvent(event, new Client(event.getBaseEntityId()), clientClassification);
                } catch (Exception e) {
                    Timber.e(e);
                }
                if (!hasSyncedEventsInTarget && operationalAreaLocationId != null &&
                        operationalAreaLocationId.equals(operationalAreaId)) {
                    hasSyncedEventsInTarget = true;
                }
//                if (localSubmission && CoreLibrary.getInstance().getSyncConfiguration().runPlanEvaluationOnClientProcessing()) {
//                    processPlanEvaluation(eventClient);
//                }
            }
        }

        if (hasSyncedEventsInTarget) {
            Intent intent = new Intent(AppConstants.Action.STRUCTURE_TASK_SYNCED);
            intent.putExtra(AppConstants.CONFIGURATION.LOCAL_SYNC_DONE, localSubmission);
            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
        }
    }

    @Override
    protected void updateRegisterCount(String entityId) {
        //do nothing. Save performance on unrequired functionality
    }
}
