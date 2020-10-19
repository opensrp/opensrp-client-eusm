package org.smartregister.eusm;

import android.os.Build;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.smartregister.eusm.shadow.AsyncTaskShadow;
import org.smartregister.eusm.shadow.CloudantDataHandlerShadowUtils;
import org.smartregister.eusm.shadow.CustomFontTextViewShadow;
import org.smartregister.eusm.shadow.GeoJsonSourceShadow;
import org.smartregister.eusm.shadow.KujakuMapViewShadow;
import org.smartregister.eusm.shadow.LayerShadow;
import org.smartregister.eusm.shadow.LineLayerShadow;
import org.smartregister.eusm.shadow.MapViewShadow;
import org.smartregister.eusm.shadow.OfflineManagerShadow;
import org.smartregister.eusm.shadow.RevealMapViewShadow;
import org.smartregister.eusm.shadow.SourceShadow;
import org.smartregister.eusm.shadow.SymbolLayerShadow;
import org.smartregister.util.DateTimeTypeConverter;


@RunWith(RobolectricTestRunner.class)
@PowerMockRunnerDelegate(RobolectricTestRunner.class)
@Config(application = TestEusmApplication.class, shadows = {CustomFontTextViewShadow.class,
        MapViewShadow.class, KujakuMapViewShadow.class, RevealMapViewShadow.class,
        LayerShadow.class, SymbolLayerShadow.class, LineLayerShadow.class,
        GeoJsonSourceShadow.class, SourceShadow.class, OfflineManagerShadow.class,
        AsyncTaskShadow.class, CloudantDataHandlerShadowUtils.class}, sdk = Build.VERSION_CODES.P)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
public abstract class BaseUnitTest {

    protected static final String DUMMY_USERNAME = "myusername";
    protected static final char[] DUMMY_PASSWORD = "mypassword".toCharArray();
    protected static Gson taskGson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeConverter("yyyy-MM-dd'T'HHmm"))
            .serializeNulls().create();
    protected final int ASYNC_TIMEOUT = 2000;
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    protected static String getString(int stringResourceId) {
        return RuntimeEnvironment.application.getResources().getString(stringResourceId);
    }
}
