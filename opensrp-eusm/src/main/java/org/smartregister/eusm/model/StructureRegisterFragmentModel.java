package org.smartregister.eusm.model;

import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.repository.AppStructureRepository;
import org.smartregister.eusm.util.PreferencesUtil;

import java.util.List;

public class StructureRegisterFragmentModel {

    private AppStructureRepository appStructureRepository;

    public StructureRegisterFragmentModel() {
        appStructureRepository = EusmApplication.getInstance().getStructureRepository();
    }

    public List<StructureDetail> fetchStructures() {
        //join structure and task filtered by
        return appStructureRepository.fetchStructureDetails(PreferencesUtil.getInstance().getCurrentOperationalAreaId());
    }
}
