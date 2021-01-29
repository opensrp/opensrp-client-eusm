package org.smartregister.eusm.configuration;

import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.smartregister.eusm.BaseUnitTest;
import org.smartregister.stock.util.Constants;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class EusmStockSyncConfigurationTest extends BaseUnitTest {

    private EusmStockSyncConfiguration stockSyncConfiguration;

    @Before
    public void setUp() {
        stockSyncConfiguration = new EusmStockSyncConfiguration();
    }

    @Test
    public void testStockSyncRequestBody() {
        String[] locationsArr = new String[]{"2"};

        JSONArray jsonArray = new JSONArray();

        for (String s : locationsArr) {
            jsonArray.put(s);
        }

        Map<String, Object> syncParams = new HashMap<>();
        syncParams.put(Constants.StockResponseKey.SERVER_VERSION, 5);
        syncParams.put(Constants.StockResponseKey.LOCATIONS, jsonArray);

        assertEquals("{\"serverVersion\":5,\"locations\":[\"2\"]}", stockSyncConfiguration.stockSyncRequestBody(syncParams));
    }
}