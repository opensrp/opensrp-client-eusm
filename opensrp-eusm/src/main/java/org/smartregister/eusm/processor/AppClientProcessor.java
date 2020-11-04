package org.smartregister.eusm.processor;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.joda.time.DateTime;
import org.smartregister.domain.Client;
import org.smartregister.domain.Event;
import org.smartregister.domain.Location;
import org.smartregister.domain.LocationProperty.PropertyStatus;
import org.smartregister.domain.Obs;
import org.smartregister.domain.Task;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.jsonmapping.ClientClassification;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.eusm.util.AppUtils;
import org.smartregister.eusm.util.PreferencesUtil;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.StructureRepository;
import org.smartregister.repository.TaskRepository;
import org.smartregister.sync.ClientProcessorForJava;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by samuelgithengi on 12/7/18.
 */
public class AppClientProcessor extends ClientProcessorForJava {

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

        return (AppClientProcessor) instance;
    }

    @Override
    public synchronized void processClient(List<EventClient> eventClientList) {
        processClient(eventClientList, false);
    }

    public void processClient(List<EventClient> eventClients, boolean localEvents) {
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
                if (eventType.equals(AppConstants.REGISTER_STRUCTURE_EVENT)) {
                    operationalAreaId = processRegisterStructureEvent(event, clientClassification);
                } else if (AppConstants.EventType.SUMMARY_EVENT_TYPES.contains(event.getEventType())) {
                    processSummaryFormEvent(event, clientClassification);
                } else {
                    Client client = eventClient.getClient();

                    if (client != null) {
                        clients.add(client);
                        try {
                            if (event.getDetails() != null && event.getDetails().get(AppConstants.Properties.TASK_IDENTIFIER) != null) {
                                updateTask(event, localEvents);
                            }
                            processEvent(event, client, clientClassification);
                        } catch (Exception e) {
                            Timber.e(e);
                        }

                    }
                }
                if (!hasSyncedEventsInTarget && operationalAreaLocationId != null &&
                        operationalAreaLocationId.equals(operationalAreaId)) {
                    hasSyncedEventsInTarget = true;
                }
            }
        }

//        taskRepository.updateTaskStructureIdFromClient(clients, FamilyConstants.RELATIONSHIP.RESIDENCE);
//        taskRepository.updateTaskStructureIdsFromExistingStructures();
//        taskRepository.updateTaskStructureIdsFromExistingClients(FamilyConstants.TABLE_NAME.FAMILY_MEMBER);

        if (hasSyncedEventsInTarget) {
            Intent intent = new Intent(AppConstants.Action.STRUCTURE_TASK_SYNCED);
            intent.putExtra(AppConstants.CONFIGURATION.LOCAL_SYNC_DONE, localEvents);
            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
        }
    }

    private String processRegisterStructureEvent(Event event, ClientClassification clientClassification) {
        try {
            processEvent(event, new Client(event.getBaseEntityId()), clientClassification);
            if (event.getDetails() != null && event.getDetails().get(AppConstants.Properties.LOCATION_PARENT) != null) {
                return event.getDetails().get(AppConstants.Properties.LOCATION_PARENT);
            }
        } catch (Exception e) {
            Timber.e(e, "Error processing register structure event");
        }
        return null;
    }

    private String processEvent(Event event, ClientClassification clientClassification, boolean localEvents, @NonNull String formField) {
        String operationalAreaId = null;
        if (event.getDetails() != null && event.getDetails().get(AppConstants.Properties.TASK_IDENTIFIER) != null) {
            operationalAreaId = updateTask(event, localEvents);

            Location structure = structureRepository.getLocationById(event.getBaseEntityId());
            if (structure != null) {
                Obs eventObs = event.findObs(null, false, formField);
                if (eventObs != null && AppConstants.JsonForm.PAOT_STATUS.equals(formField)) {
                    structure.getProperties().setStatus(PropertyStatus.valueOf(eventObs.getValue().toString().toUpperCase()));
                    structureRepository.addOrUpdate(structure);
                } else if (eventObs != null && AppConstants.JsonForm.STRUCTURE_TYPE.equals(formField)) {
                    structure.getProperties().setType(eventObs.getValue().toString());
                    structureRepository.addOrUpdate(structure);
                }
                if (operationalAreaId == null) {
                    operationalAreaId = structure.getProperties().getParentId();
                }
            }

            try {
                Client client = new Client(event.getBaseEntityId());
                processEvent(event, client, clientClassification);
            } catch (Exception e) {
                Timber.e(e, "Error processing %s event", event.getEventType());
            }
        } else {
            Timber.w("%s Event %s does not have task details", event.getEventType(), event.getEventId());
        }
        return operationalAreaId;
    }

    private String processEvent(Event event, ClientClassification clientClassification, boolean localEvents) {
        String operationalAreaId = null;
        if (event.getDetails() != null && event.getDetails().get(AppConstants.Properties.TASK_IDENTIFIER) != null) {
            operationalAreaId = updateTask(event, localEvents);
            try {
                Client client = new Client(event.getBaseEntityId());
                processEvent(event, client, clientClassification);
            } catch (Exception e) {
                Timber.e(e, "Error processing spray event");
            }
        }
        return operationalAreaId;
    }

    private void processSummaryFormEvent(Event event, ClientClassification clientClassification) {
        try {
            processEvent(event, new Client(event.getBaseEntityId()), clientClassification);
        } catch (Exception e) {
            Timber.e(e, "Error processing register structure event");
        }
    }

    private String updateTask(Event event, boolean localEvents) {
        String taskIdentifier = event.getDetails().get(AppConstants.Properties.TASK_IDENTIFIER);
        Task task = taskRepository.getTaskByIdentifier(taskIdentifier);
        String operationalAreaId = null;
        if (task != null && task.getStatus() != Task.TaskStatus.CANCELLED && task.getStatus() != Task.TaskStatus.ARCHIVED) {
            task.setBusinessStatus(calculateBusinessStatus(event));
            task.setStatus(Task.TaskStatus.COMPLETED);
            task.setLastModified(new DateTime());
            // update task sync status to unsynced if it was already synced,
            // ignore if task status is created so that it will be created on server
            if (localEvents && BaseRepository.TYPE_Synced.equals(task.getSyncStatus())) {
                task.setSyncStatus(BaseRepository.TYPE_Unsynced);
                eusmApplication.setSynced(false);
            } else if (!localEvents && event.getServerVersion() != 0) {
                // for events synced from server and task exists mark events as being fully synced
                eventClientRepository.markEventAsSynced(event.getFormSubmissionId());
            }
            taskRepository.addOrUpdate(task);
            operationalAreaId = task.getGroupIdentifier();
        } else if (!localEvents) {
            eventClientRepository.markEventAsTaskUnprocessed(event.getFormSubmissionId());
        }
        return operationalAreaId;
    }

    public String calculateBusinessStatus(Event event) {
//        if (FamilyConstants.EventType.FAMILY_REGISTRATION.equals(event.getEventType()) || FamilyConstants.EventType.FAMILY_MEMBER_REGISTRATION.equals(event.getEventType())) {
//            return AppConstants.BusinessStatus.COMPLETE;
//        }
//        Obs businessStatusObs = event.findObs(null, false, AppConstants.JsonForm.BUSINESS_STATUS);
//        if (businessStatusObs != null) {
//            return businessStatusObs.getValue().toString();
//        } else {
//            //supported only for backward compatibility, business status now being calculated on form
//            Obs structureType = event.findObs(null, false, AppConstants.JsonForm.STRUCTURE_TYPE);
//            if (structureType != null && !AppConstants.StructureType.RESIDENTIAL.equals(structureType.getValue().toString())) {
//                return AppConstants.BusinessStatus.NOT_SPRAYABLE;
//            } else {
//                Obs sprayStatus = event.findObs(null, false, AppConstants.JsonForm.SPRAY_STATUS);
//                return sprayStatus == null ? null : sprayStatus.getValue().toString();
//            }
//        }
        return "";
    }

    @Override
    protected void updateRegisterCount(String entityId) {
        //do nothing. Save performance on unrequired functionality
    }
}