package org.smartregister.eusm.configuration;

import org.smartregister.stock.configuration.StockSyncConfiguration;

public class EusmStockSyncConfiguration extends StockSyncConfiguration {

    @Override
    public String getStockSyncParams() {
//        Set<String> set = AppUtils.fetchStructureIds();
//        String locations = "";
//        if (set != null) {
//            locations = StringUtils.join(set, ",");
//        }
//        return String.format("&locations=%s", locations);//TODO add all structureIds
        return "";
    }

    @Override
    public boolean canPushStockToServer() {
        return false;
    }

    @Override
    public boolean hasActions() {
        return false;
    }

    @Override
    public boolean shouldFetchStockTypeImages() {
        return true;
    }

    @Override
    public boolean useDefaultStockExistenceCheck() {
        return false;
    }
}
