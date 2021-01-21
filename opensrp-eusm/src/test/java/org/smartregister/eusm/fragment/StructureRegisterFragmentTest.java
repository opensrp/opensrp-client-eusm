package org.smartregister.eusm.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.eusm.BaseUnitTest;
import org.smartregister.eusm.R;
import org.smartregister.eusm.activity.EusmTaskRegisterActivity;
import org.smartregister.eusm.presenter.StructureRegisterFragmentPresenter;
import org.smartregister.eusm.view.NavigationDrawerView;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class StructureRegisterFragmentTest extends BaseUnitTest {

    private FragmentScenario<TestStructureRegisterFragment> fragmentScenario;

    @Mock
    private Context openspContext;

    @Mock
    private CoreLibrary coreLibrary;

    @Mock
    private SyncStatusBroadcastReceiver syncStatusBroadcastReceiver;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        doReturn(openspContext).when(coreLibrary).context();
        doReturn(openspContext).when(openspContext).updateApplicationContext(any(android.content.Context.class));
        doReturn(false).when(openspContext).IsUserLoggedOut();

        ReflectionHelpers.setStaticField(SyncStatusBroadcastReceiver.class, "singleton", syncStatusBroadcastReceiver);

        fragmentScenario = FragmentScenario
                .launch(TestStructureRegisterFragment.class)
                .moveToState(Lifecycle.State.CREATED);
    }

    @Test
    public void testOnViewClickShouldOpenMapActivity() {
        fragmentScenario.onFragment(fragment -> {
            View view = new View(RuntimeEnvironment.application);
            view.setId(R.id.task_register);

            StructureRegisterFragment fragmentSpy = spy(fragment);

            fragmentSpy.onClick(view);

            verify(fragmentSpy).startMapActivity();
        });
    }

    @Test
    public void testOnViewClickShouldOpenTaskRegisterActivity() {
        fragmentScenario.onFragment(fragment -> {
            View view = new View(RuntimeEnvironment.application);
            view.setId(R.id.table_layout);

            StructureRegisterFragment fragmentSpy = spy(fragment);

            FragmentActivity fragmentActivity = mock(FragmentActivity.class);

            doReturn(fragmentActivity).when(fragmentSpy).getActivity();

            fragmentSpy.onClick(view);

            ArgumentCaptor<Intent> argumentCaptor = ArgumentCaptor.forClass(Intent.class);
            verify(fragmentActivity).startActivity(argumentCaptor.capture());

            Intent intent = argumentCaptor.getValue();

            assertNotNull(intent);

            assertEquals(EusmTaskRegisterActivity.class.getCanonicalName(), intent.getComponent().getClassName());
        });
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", null);
    }

    public static class TestStructureRegisterFragment extends StructureRegisterFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            drawerView = mock(NavigationDrawerView.class);
        }

        @Override
        protected void initializePresenter() {
            presenter = mock(StructureRegisterFragmentPresenter.class);
        }

        @Override
        protected void renderView() {
            //Do nothing
        }
    }
}