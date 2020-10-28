package org.smartregister.eusm.contract;

public interface TaskRegisterActivityContract {

    interface View {

        String getStructureIcon();

        String getStructureName();

        String getStructureType();

        String getDistance();

        String getCommune();

        TaskRegisterActivityContract.Presenter presenter();
    }

    interface Presenter {

        TaskRegisterActivityContract.View getView();
    }

}
