package org.smartregister.eusm.repository;

import org.smartregister.tasking.repository.TaskingRepository;

public class AppTaskingRepository extends TaskingRepository {

    @Override
    protected String getStructureNamesSelect(String mainCondition) {
//        SmartRegisterQueryBuilder queryBuilder = new SmartRegisterQueryBuilder();
//        queryBuilder.selectInitiateMainTable(STRUCTURES_TABLE, new String[]{
//                String.format("%s", NAME)}, ID);
        return "SELECT structure.name, structure.parent_id as thn , ? as test, location.parent_id as parent_id, structure._id as _id from structure " +
                " join location on structure.parent_id = location._id";//queryBuilder.mainCondition(mainCondition);
    }
}
