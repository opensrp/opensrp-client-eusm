package org.smartregister.eusm.presenter;

import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.robolectric.annotation.LooperMode;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.eusm.BaseUnitTest;
import org.smartregister.eusm.adapter.StructureRegisterAdapter;
import org.smartregister.eusm.fragment.StructureRegisterFragment;
import org.smartregister.eusm.interactor.StructureRegisterInteractor;

import static android.os.Looper.getMainLooper;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;
import static org.robolectric.annotation.LooperMode.Mode.PAUSED;

@LooperMode(PAUSED)
public class StructureRegisterFragmentPresenterTest extends BaseUnitTest {

    private StructureRegisterFragmentPresenter structureRegisterFragmentPresenter;

    @Mock
    private StructureRegisterFragment view;

    @Before
    public void setUp() {
        structureRegisterFragmentPresenter = spy(new StructureRegisterFragmentPresenter(view));
    }

    @Test
    public void testInitializeQueriesShouldInvokeFetchQueries() throws InterruptedException {
        structureRegisterFragmentPresenter.initializeQueries("not used");
        shadowOf(getMainLooper()).idle();
        Thread.sleep(ASYNC_TIMEOUT);
        verify(structureRegisterFragmentPresenter).countOfStructures();
        verify(structureRegisterFragmentPresenter).fetchStructures();
        verify(structureRegisterFragmentPresenter).onCountOfStructuresFetched(anyInt());
        verify(structureRegisterFragmentPresenter).onFetchedStructures(anyList());
    }

    @Test
    public void testFilterByNameShouldAddNameToSearch() throws InterruptedException {
        StructureRegisterInteractor structureRegisterInteractorSpy = spy(new StructureRegisterInteractor());
        ReflectionHelpers.setField(structureRegisterFragmentPresenter, "structureRegisterInteractor", structureRegisterInteractorSpy);
        String nameToFilter = "test";
        doReturn(mock(StructureRegisterAdapter.class)).when(view).getAdapter();
        structureRegisterFragmentPresenter.filterByName(nameToFilter);
        shadowOf(getMainLooper()).idle();
        Thread.sleep(ASYNC_TIMEOUT);
        verify(structureRegisterFragmentPresenter).countOfStructures();
        verify(structureRegisterFragmentPresenter).fetchStructures();
        verify(structureRegisterInteractorSpy).fetchStructures(eq(structureRegisterFragmentPresenter),
                eq(0), eq(nameToFilter));
        verify(structureRegisterInteractorSpy).countOfStructures(eq(structureRegisterFragmentPresenter),
                eq(nameToFilter));
        verify(structureRegisterFragmentPresenter).onCountOfStructuresFetched(anyInt());
        verify(structureRegisterFragmentPresenter).onFetchedStructures(anyList());
    }

    @Test
    public void testOnNextButtonClickShouldInvokeFetch() {
        Button mockNextButton = mock(Button.class);
        StructureRegisterFragment mockStructureRegisterFragment = mock(StructureRegisterFragment.class);
        StructureRegisterAdapter mockStructureRegisterAdapter = mock(StructureRegisterAdapter.class);
        doReturn(mockStructureRegisterAdapter).when(view).getAdapter();
        doReturn(mockStructureRegisterFragment).when(structureRegisterFragmentPresenter).getFragment();
        doReturn(mockNextButton).when(mockStructureRegisterFragment).getNextButton();
        structureRegisterFragmentPresenter.onNextButtonClick();

        verify(structureRegisterFragmentPresenter).fetchStructures();
    }

    @Test
    public void testOnPreviousButtonClickShouldInvokeFetch() {
        Button mockNextButton = mock(Button.class);
        StructureRegisterFragment mockStructureRegisterFragment = mock(StructureRegisterFragment.class);
        StructureRegisterAdapter mockStructureRegisterAdapter = mock(StructureRegisterAdapter.class);
        RecyclerView mockRecyclerView = mock(RecyclerView.class);
        doReturn(mockStructureRegisterAdapter).when(view).getAdapter();
        doReturn(mockStructureRegisterFragment).when(structureRegisterFragmentPresenter).getFragment();
        doReturn(mockNextButton).when(mockStructureRegisterFragment).getNextButton();
        doReturn(mockNextButton).when(mockStructureRegisterFragment).getPreviousButton();

        WhiteboxImpl.setInternalState(mockStructureRegisterFragment, "clientsView", mockRecyclerView);

        structureRegisterFragmentPresenter.onPreviousButtonClick();

        verify(structureRegisterFragmentPresenter).fetchStructures();
    }
}