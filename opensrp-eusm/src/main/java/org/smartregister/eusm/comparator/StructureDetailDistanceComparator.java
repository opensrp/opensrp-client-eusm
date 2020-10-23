package org.smartregister.eusm.comparator;

import org.smartregister.eusm.model.StructureDetail;

import java.util.Comparator;

public class StructureDetailDistanceComparator implements Comparator<StructureDetail> {

    @Override
    public int compare(StructureDetail o1, StructureDetail o2) {
        return o1.getDistance().compareTo(o2.getDistance());
    }
}
