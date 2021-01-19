package org.smartregister.eusm;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.eusm.activity.LoginActivity;
import org.smartregister.view.contract.BaseLoginContract;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class LoginActivityTest extends BaseActivityUnitTest {

    private static final String STRING_SETTINGS = "Settings";
    private LoginActivity loginActivity;
    private ActivityController<LoginActivity> controller;
    @Mock
    private Menu menu;

    @Mock
    private BaseLoginContract.Presenter presenter;

    @Mock
    private ProgressDialog progressDialog;

    @Mock
    private Button loginButton;

    @Captor
    private ArgumentCaptor<Intent> intentArgumentCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        controller = Robolectric.buildActivity(LoginActivity.class).create().start();
        loginActivity = spy(controller.get());
        doNothing().when(presenter).setLanguage();
    }

    @After
    public void tearDown() {
        destroyController();
    }

    @Test
    public void testUserNameEditTextIsInitialized() {

        EditText userNameEditText = Whitebox.getInternalState(loginActivity, "userNameEditText");
        assertNotNull(userNameEditText);
    }

    @Test
    public void testPasswordEditTextIsInitialized() {

        EditText userPasswordEditText = Whitebox.getInternalState(loginActivity, "passwordEditText");
        assertNotNull(userPasswordEditText);
    }


    @Test
    public void testShowPasswordCheckBoxIsInitialized() {

        CheckBox showPasswordCheckBox = Whitebox.getInternalState(loginActivity, "showPasswordCheckBox");
        assertNotNull(showPasswordCheckBox);
    }

    @Test
    public void testProgressDialogIsInitialized() {

        ProgressDialog progressDialog = Whitebox.getInternalState(loginActivity, "progressDialog");
        assertNotNull(progressDialog);
    }


    @Test
    public void testOnCreateOptionsMenuShouldAddSettingsItem() {
        loginActivity.onCreateOptionsMenu(menu);
        verify(menu).add(STRING_SETTINGS);
    }


    @Test
    public void testOnDestroyShouldCallOnDestroyPresenterMethod() {

        Whitebox.setInternalState(loginActivity, "mLoginPresenter", presenter);
        loginActivity.onDestroy();
        verify(presenter).onDestroy(anyBoolean());
    }

    @Test
    public void testShowProgressShouldShowProgressDialogWhenParamIsTrue() {
        Whitebox.setInternalState(loginActivity, "progressDialog", progressDialog);
        loginActivity.showProgress(true);
        verify(progressDialog).show();
    }

    @Test
    public void testShowProgressShouldDismissProgressDialogWhenParamIsFalse() {
        Whitebox.setInternalState(loginActivity, "progressDialog", progressDialog);
        loginActivity.showProgress(false);
        verify(progressDialog).dismiss();
    }

    @Test
    public void testEnableLoginShouldCallLoginButtonSetClickableMethodWithCorrectParameter() {
        Whitebox.setInternalState(loginActivity, "loginButton", loginButton);
        loginActivity.enableLoginButton(false);
        verify(loginButton).setClickable(anyBoolean());
    }

    @Test
    public void testOnEditorActionShouldCallAttemptLoginMethodFromPresenterIfActionIsEnter() {
        Whitebox.setInternalState(loginActivity, "mLoginPresenter", presenter);

        verify(presenter, times(0)).attemptLogin(DUMMY_USERNAME, DUMMY_PASSWORD);

        EditText userNameEditText = spy(new EditText(RuntimeEnvironment.application));
        userNameEditText.setText(DUMMY_USERNAME);

        EditText passwordEditText = spy(new EditText(RuntimeEnvironment.application));
        passwordEditText.setText(String.valueOf(DUMMY_PASSWORD));

        Whitebox.setInternalState(loginActivity, "userNameEditText", userNameEditText);
        Whitebox.setInternalState(loginActivity, "passwordEditText", passwordEditText);
    }

    @Test
    public void testOnClickShouldInvokeAttemptLoginPresenterMethodIfLoginButtonClicked() {
        loginActivity.onClick(new View(RuntimeEnvironment.application));//default

        Whitebox.setInternalState(loginActivity, "mLoginPresenter", presenter);

        EditText editTextUsername = loginActivity.findViewById(R.id.login_user_name_edit_text);
        editTextUsername.setText(DUMMY_USERNAME);
        EditText editTextPassword = loginActivity.findViewById(R.id.login_password_edit_text);
        editTextPassword.setText(String.valueOf(DUMMY_PASSWORD));

        Button loginButton = loginActivity.findViewById(R.id.login_login_btn);
        loginActivity.onClick(loginButton);

        verify(presenter, times(1)).attemptLogin(DUMMY_USERNAME, DUMMY_PASSWORD);
    }

    @Test
    public void testResetPasswordErrorShouldInvokeSetUsernameErrorWithNull() {
        Whitebox.setInternalState(loginActivity, "mLoginPresenter", presenter);

        EditText passwordEditText = spy(new EditText(RuntimeEnvironment.application));

        Whitebox.setInternalState(loginActivity, "passwordEditText", passwordEditText);

        loginActivity.resetPaswordError();

        verify(passwordEditText).setError(null);
    }

    @Test
    public void testSetPasswordErrorShouldShowErrorDialogWithCorrectMessage() {
        Whitebox.setInternalState(loginActivity, "mLoginPresenter", presenter);

        EditText passwordEditText = spy(new EditText(RuntimeEnvironment.application));

        Whitebox.setInternalState(loginActivity, "passwordEditText", passwordEditText);

        doNothing().when(loginActivity).showErrorDialog(RuntimeEnvironment.application.getString(R.string.unauthorized));

        loginActivity.setUsernameError(R.string.unauthorized);

        verify(loginActivity).showErrorDialog(RuntimeEnvironment.application.getString(R.string.unauthorized));
    }

    @Test
    public void testSetUsernameErrorShouldShowErrorDialogWithCorrectMessage() {
        Whitebox.setInternalState(loginActivity, "mLoginPresenter", presenter);

        EditText userNameEditText = spy(new EditText(RuntimeEnvironment.application));

        Whitebox.setInternalState(loginActivity, "userNameEditText", userNameEditText);

        doNothing().when(loginActivity).showErrorDialog(RuntimeEnvironment.application.getString(R.string.unauthorized));

        loginActivity.setPasswordError(R.string.unauthorized);

        verify(loginActivity).showErrorDialog(RuntimeEnvironment.application.getString(R.string.unauthorized));
    }

    @Test
    public void testResetUsernameErrorShouldInvokeSetUsernameErrorWithNull() {
        Whitebox.setInternalState(loginActivity, "mLoginPresenter", presenter);

        EditText userNameEditText = spy(new EditText(RuntimeEnvironment.application));

        Whitebox.setInternalState(loginActivity, "userNameEditText", userNameEditText);

        loginActivity.resetUsernameError();

        verify(userNameEditText).setError(null);
    }

    @Test
    public void testGetActivityContextReturnsCorrectInstance() {
        assertEquals(loginActivity, loginActivity.getActivityContext());
    }

    @Test
    public void testGoToHome() {
        Whitebox.setInternalState(loginActivity, "mLoginPresenter", presenter);

        loginActivity.goToHome(false);

        verify(loginActivity).startActivity(intentArgumentCaptor.capture());
        assertNotNull(intentArgumentCaptor.getValue());
        assertEquals(".activity.EusmTaskingMapActivity", intentArgumentCaptor.getValue().getComponent().getShortClassName());
        verify(loginActivity).finish();
    }

    @Override
    protected Activity getActivity() {
        return loginActivity;
    }

    @Override
    protected ActivityController getActivityController() {
        return controller;
    }
}
