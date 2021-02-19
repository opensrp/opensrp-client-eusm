package org.smartregister.eusm.config;

import org.json.JSONArray;
import org.smartregister.eusm.util.AppUtils;
import org.smartregister.stock.configuration.StockSyncConfiguration;
import org.smartregister.stock.util.Constants;

import java.util.Map;
import java.util.Set;

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

    @Override
    public boolean syncStockByPost() {
        return true;
    }

    @Override
    public String stockSyncRequestBody(Map<String, Object> syncParams) {
        Set<String> savedStructureIds = AppUtils.fetchStructureIds();

        JSONArray jsonArray = null;
        if (savedStructureIds != null && !savedStructureIds.isEmpty()) {
            jsonArray = new JSONArray();
            for (String location : savedStructureIds) {
                jsonArray.put(location);
            }
        }

        if (jsonArray != null) {
            syncParams.put(Constants.StockResponseKey.LOCATIONS, jsonArray);
        }
        return super.stockSyncRequestBody(syncParams);
    }
}
