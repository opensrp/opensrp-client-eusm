package org.smartregister.eusm.util;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONObject;
import org.junit.Test;
import org.smartregister.eusm.BaseUnitTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AppUtilsTest extends BaseUnitTest {

    @Test
    public void testGetHiddenFieldTemplateShouldReturnTemplate() {
        JSONObject resultJsonObject = AppUtils.getHiddenFieldTemplate("keyA", "valueA");
        assertNotNull(resultJsonObject);
        assertTrue(resultJsonObject.has(JsonFormConstants.TYPE));
        assertTrue(resultJsonObject.has(JsonFormConstants.KEY));
        assertTrue(resultJsonObject.has(JsonFormConstants.VALUE));
        assertEquals(resultJsonObject.optString(JsonFormConstants.TYPE), JsonFormConstants.HIDDEN);
    }
}