package org.smartregister.eusm.configuration;

import org.smartregister.stock.configuration.StockSyncConfiguration;

public class EusmStockSyncConfiguration extends StockSyncConfiguration {

    @Override
    public String getStockSyncParams() {
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
