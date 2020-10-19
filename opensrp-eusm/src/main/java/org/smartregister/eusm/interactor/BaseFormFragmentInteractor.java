package org.smartregister.eusm.interactor;

import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONObject;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.config.AppExecutors;
import org.smartregister.eusm.contract.BaseFormFragmentContract;
import org.smartregister.eusm.util.InteractorUtils;
import org.smartregister.repository.EventClientRepository;

/**
 * Created by samuelgithengi on 6/14/19.
 */
public class BaseFormFragmentInteractor implements BaseFormFragmentContract.Interactor {

    protected EventClientRepository eventClientRepository;
    private final BaseFormFragmentContract.Presenter presenter;
    private final CommonRepository commonRepository;
    private final AppExecutors appExecutors;
    private final SQLiteDatabase sqLiteDatabase;
    private final InteractorUtils interactorUtils;

    public BaseFormFragmentInteractor(BaseFormFragmentContract.Presenter presenter) {
        this.presenter = presenter;
        this.commonRepository = EusmApplication.getInstance().getContext().commonrepository("");//metadata().familyMemberRegister.tableName);
        appExecutors = EusmApplication.getInstance().getAppExecutors();
        sqLiteDatabase = EusmApplication.getInstance().getRepository().getReadableDatabase();
        eventClientRepository = EusmApplication.getInstance().getContext().getEventClientRepository();
        interactorUtils = new InteractorUtils();
    }

    @Override
    public void findNumberOfMembers(String structureId, JSONObject formJSON) {
//        appExecutors.diskIO().execute(() -> {
//            Cursor cursor = null;
//            int numberOfMembers = 0;
//            int numberOfMembersSleepingOutdoors = 0;
//            try {
//                cursor = sqLiteDatabase.rawQuery(
//                        String.format("SELECT count(*),SUM(CASE WHEN sleeps_outdoors='Yes' THEN 1 ELSE 0 END) FROM %s WHERE %s = ?",
//                                metadata().familyMemberRegister.tableName, Constants.DatabaseKeys.STRUCTURE_ID), new String[]{structureId});
//
//                while (cursor.moveToNext()) {
//                    numberOfMembers = cursor.getInt(0);
//                    numberOfMembersSleepingOutdoors = cursor.getInt(1);
//                }
//            } catch (Exception e) {
//                Timber.e(e, "Error find Number of members ");
//            } finally {
//                if (cursor != null)
//                    cursor.close();
//            }
//            int finalNumberOfMembers = numberOfMembers;
//            int finalNumberOfMembersSleepingOutdoors = numberOfMembersSleepingOutdoors;
//            appExecutors.mainThread().execute(() -> {
//                presenter.onFetchedMembersCount(new Pair<>(finalNumberOfMembers, finalNumberOfMembersSleepingOutdoors), formJSON);
//            });
//        });

    }

//    @Override
//    public void findMemberDetails(String structureId, JSONObject formJSON) {
//        appExecutors.diskIO().execute(() -> {
//            JSONArray familyMembers = new JSONArray();
//            Cursor cursor = null;
//            try {
//                cursor = sqLiteDatabase.rawQuery(
//                        String.format("SELECT %s, %s, %s FROM %s WHERE %s = ?", Constants.DatabaseKeys.BASE_ENTITY_ID, Constants.DatabaseKeys.FIRST_NAME, Constants.DatabaseKeys.LAST_NAME,
//                                metadata().familyMemberRegister.tableName, Constants.DatabaseKeys.STRUCTURE_ID), new String[]{structureId});
//                while (cursor.moveToNext()) {
//                    JSONObject member = new JSONObject();
//                    member.put(KEY, cursor.getString(cursor.getColumnIndex(Constants.DatabaseKeys.BASE_ENTITY_ID)));
//                    member.put(TEXT, String.format("%s %s", cursor.getString(cursor.getColumnIndex(Constants.DatabaseKeys.FIRST_NAME))
//                            , cursor.getString(cursor.getColumnIndex(Constants.DatabaseKeys.LAST_NAME))));
//                    familyMembers.put(member);
//                }
//            } catch (Exception e) {
//                Timber.e(e, "Error find Member Details ");
//            } finally {
//                if (cursor != null)
//                    cursor.close();
//            }
//            appExecutors.mainThread().execute(() -> {
//                presenter.onFetchedFamilyMembers(familyMembers, formJSON);
//            });
//        });
//    }

//    @Override
//    public void findSprayDetails(String interventionType, String structureId, JSONObject formJSON) {
//        if (Constants.Intervention.IRS.equals(interventionType)) {
//
//            appExecutors.diskIO().execute(() -> {
//                CommonPersonObject commonPersonObject = interactorUtils.fetchSprayDetails(interventionType, structureId,
//                        eventClientRepository, commonRepository);
//
//                appExecutors.mainThread().execute(() -> {
//                    presenter.onFetchedSprayDetails(commonPersonObject, formJSON);
//                });
//            });
//        }
//    }


}
