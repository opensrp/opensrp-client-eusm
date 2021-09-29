package org.smartregister.eusm.model;

import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.domain.StructureDetail;
import org.smartregister.eusm.repository.AppStructureRepository;
import org.smartregister.tasking.util.PreferencesUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StructureRegisterFragmentModel {

    private final AppStructureRepository appStructureRepository;

    public StructureRegisterFragmentModel() {
        appStructureRepository = EusmApplication.getInstance().getStructureRepository();
    }

    public int countOfStructures(String nameFilter) {
        Set<String> locations = PreferencesUtil.getInstance().getCurrentOperationalAreaIds();
        if (!locations.isEmpty()) {
            return appStructureRepository.countOfStructures(nameFilter, locations, PreferencesUtil.getInstance().getCurrentPlanId());
        } else {
            return 0;
        }
    }

    public List<StructureDetail> fetchStructures(int pageNo, String nameFilter) {
        Set<String> locations = PreferencesUtil.getInstance().getCurrentOperationalAreaIds();
        if (!locations.isEmpty()) {
            return appStructureRepository.fetchStructureDetails(pageNo, locations, nameFilter, PreferencesUtil.getInstance().getCurrentPlanId());
        } else {
            return new ArrayList<>();
        }
    }
}
