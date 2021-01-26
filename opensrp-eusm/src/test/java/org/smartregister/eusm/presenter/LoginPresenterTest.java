package org.smartregister.eusm.presenter;

import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.configurableviews.helper.JsonSpecHelper;
import org.smartregister.configurableviews.model.LoginConfiguration;
import org.smartregister.configurableviews.model.ViewConfiguration;
import org.smartregister.eusm.BaseUnitTest;
import org.smartregister.eusm.R;
import org.smartregister.eusm.activity.LoginActivity;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.eusm.util.ImageLoaderRequest;
import org.smartregister.view.activity.DrishtiApplication;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class LoginPresenterTest extends BaseUnitTest {

    private LoginPresenter loginPresenter;

    private LoginActivity loginView;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        loginView = spy(Robolectric.buildActivity(LoginActivity.class).create().get());
        loginPresenter = spy(new LoginPresenter(loginView));
    }

    @Test
    public void testProcessViewCustomizationsShouldUpdateBackgroundAndHideSomeViews() {
        EusmApplication spyEusmApplication = spy(EusmApplication.getInstance());
        JsonSpecHelper jsonSpecHelper = mock(JsonSpecHelper.class);
        ReflectionHelpers.setField(spyEusmApplication, "jsonSpecHelper", jsonSpecHelper);
        ViewConfiguration mockViewConfiguration = mock(ViewConfiguration.class);
        ImageLoaderRequest mockImageLoaderRequest = mock(ImageLoaderRequest.class);
        ImageLoader mockImageLoader = mock(ImageLoader.class);
        doReturn(mockImageLoader).when(mockImageLoaderRequest).getImageLoader();

        doReturn(mockViewConfiguration).when(jsonSpecHelper).getConfigurableView(anyString());

        LoginConfiguration.Background background = mock(LoginConfiguration.Background.class);
        doReturn("TOP_BOTTOM").when(background).getOrientation();
        doReturn("#000000").when(background).getStartColor();
        doReturn("#000000").when(background).getEndColor();

        LoginConfiguration loginConfiguration = new LoginConfiguration();
        loginConfiguration.setLogoUrl("http://opensrpurl/login_image");
        loginConfiguration.setBackground(background);

        ReflectionHelpers.setStaticField(ImageLoaderRequest.class, "imageLoaderRequest", mockImageLoaderRequest);

        doReturn(loginConfiguration).when(mockViewConfiguration).getMetadata();

        ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", spyEusmApplication);

        doReturn("{}").when(loginPresenter).getJsonViewFromPreference(eq(AppConstants.VIEW_CONFIGURATION_PREFIX + AppConstants.CONFIGURATION.LOGIN));

        View mockView = mock(View.class);
        CheckBox mockCheckBox = mock(CheckBox.class);
        TextView mockTextView = mock(TextView.class);

        doReturn(mockCheckBox).when(loginView).findViewById(R.id.login_show_password_checkbox);

        doReturn(mockTextView).when(loginView).findViewById(R.id.login_show_password_text_view);

        doReturn(mockView).when(loginView).findViewById(R.id.login_layout);

        loginPresenter.processViewCustomizations();

        verify(mockView).setBackground(any(GradientDrawable.class));

        verify(mockCheckBox).setVisibility(View.GONE);

        verify(mockTextView).setVisibility(View.GONE);
    }

    @After
    public void tearDown() {
        loginView.finish();
    }
}