package org.smartregister.eusm.presenter;

import androidx.core.util.Pair;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.reflect.Whitebox;
import org.smartregister.eusm.BaseUnitTest;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.contract.BaseDrawerContract;
import org.smartregister.eusm.interactor.BaseDrawerInteractor;
import org.smartregister.eusm.util.PreferencesUtil;
import org.smartregister.location.helper.LocationHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.smartregister.eusm.util.AppConstants.Tags.HEALTH_CENTER;

/**
 * @author Richard Kareko
 */
public class BaseNavigationDrawerViewPresenterTest extends BaseUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private BaseNavigationDrawerPresenter presenter;

    @Mock
    private BaseDrawerContract.View view;

    @Mock
    private PreferencesUtil preferencesUtil;

    @Mock
    private BaseDrawerInteractor interactor;

    @Mock
    private LocationHelper locationHelper;

    @Mock
    private BaseDrawerContract.DrawerActivity drawerActivity;

    @Captor
    private ArgumentCaptor<List<String>> plansCaptor;

    @Captor
    private ArgumentCaptor<String> entireTreeString;

    @Captor
    private ArgumentCaptor<ArrayList<String>> arrayListArgumentCaptor;

    @Captor
    private ArgumentCaptor<Pair<String, ArrayList<String>>> pairArgumentCaptor;

    @Captor
    private ArgumentCaptor<Boolean> synced;

    @Before
    public void setUp() {
        presenter = new BaseNavigationDrawerPresenter(view);
        Whitebox.setInternalState(presenter, "prefsUtil", preferencesUtil);
        Whitebox.setInternalState(presenter, "drawerActivity", drawerActivity);

    }

//    @Test
//    public void testOnPlansFetchedReturnsActivePlans() {
//        PlanDefinition planDefinition = new PlanDefinition();
//        planDefinition.setStatus(PlanDefinition.PlanStatus.ACTIVE);
//        planDefinition.setIdentifier("tlv_1");
//        planDefinition.setTitle("Intervention Plan");
//        PlanDefinition.UseContext useContext = mock(PlanDefinition.UseContext.class);
//        when(useContext.getCode()).thenReturn("focus intervention");
//        when(useContext.getValueCodableConcept()).thenReturn("FI");
//        List<PlanDefinition.UseContext> useContextList = new ArrayList();
//        useContextList.add(useContext);
//        planDefinition.setUseContext(useContextList);
//
//        Set<PlanDefinition> planDefinitionsList = Collections.singleton(planDefinition);
//
//        presenter.onPlansFetched(planDefinitionsList);
//        verify(view).showPlanSelector(plansCaptor.capture(), entireTreeString.capture());
//        assertNotNull(plansCaptor.getValue());
//        assertEquals("tlv_1", plansCaptor.getValue().get(0));
//        assertNotNull(entireTreeString.getValue());
//        assertTrue(entireTreeString.getValue().contains("tlv_1"));
//        assertTrue(entireTreeString.getValue().contains("Intervention Plan"));
//        verifyNoMoreInteractions(view);
//        verifyNoMoreInteractions(interactor);
//
//    }
//
//    @Test
//    public void testOnPlansFetchedDoesNotReturnPlansThatAreNotActive() {
//        PlanDefinition planDefinition = new PlanDefinition();
//        planDefinition.setStatus(PlanDefinition.PlanStatus.COMPLETED);
//        planDefinition.setIdentifier("tlv_1");
//        planDefinition.setTitle("Intervention Plan");
//        PlanDefinition.UseContext useContext = mock(PlanDefinition.UseContext.class);
//        when(useContext.getCode()).thenReturn("focus intervention");
//        when(useContext.getValueCodableConcept()).thenReturn("FI");
//        List<PlanDefinition.UseContext> useContextList = new ArrayList();
//        useContextList.add(useContext);
//        planDefinition.setUseContext(useContextList);
//
//        Set<PlanDefinition> planDefinitionsList = Collections.singleton(planDefinition);
//
//        presenter.onPlansFetched(planDefinitionsList);
//        verify(view).showPlanSelector(plansCaptor.capture(), entireTreeString.capture());
//        assertNotNull(plansCaptor.getValue());
//        assertTrue(plansCaptor.getValue().isEmpty());
//        assertNotNull(entireTreeString.getValue());
//        assertTrue(entireTreeString.getValue().isEmpty());
//        verifyNoMoreInteractions(view);
//        verifyNoMoreInteractions(interactor);
//
//    }

    @Test
    public void testIsPlanAndOperationalAreaSelectedReturnsTrueWHenBothSelected() {

        when(preferencesUtil.getCurrentPlanId()).thenReturn("planid");
        when(preferencesUtil.getCurrentOperationalArea()).thenReturn("OA");

        assertTrue(presenter.isPlanAndOperationalAreaSelected());

    }

    @Test
    public void testIsPlanAndOperationalAreaSelectedReturnsFalseWhenPlanNotSelected() {
        when(preferencesUtil.getCurrentPlanId()).thenReturn(null);
        when(preferencesUtil.getCurrentOperationalArea()).thenReturn("OA");

        assertFalse(presenter.isPlanAndOperationalAreaSelected());

    }

    @Test
    public void testIsPlanAndOperationalAreaSelectedReturnsFalseWhenJurisdictionNotSelected() {

        when(preferencesUtil.getCurrentPlanId()).thenReturn("planid");
        when(preferencesUtil.getCurrentOperationalArea()).thenReturn(null);

        assertFalse(presenter.isPlanAndOperationalAreaSelected());

    }

    @Test
    public void testIsPlanAndOperationalAreaSelectedReturnsFalseWhenNonSelected() {

        when(preferencesUtil.getCurrentPlanId()).thenReturn(null);
        when(preferencesUtil.getCurrentOperationalArea()).thenReturn(null);

        assertFalse(presenter.isPlanAndOperationalAreaSelected());

    }


    @Test
    public void testOnOperationalAreaSelectedValidatesPlan() {
        String planId = UUID.randomUUID().toString();
        String operationArea = UUID.randomUUID().toString();
        when(preferencesUtil.getCurrentPlanId()).thenReturn(planId);
        when(preferencesUtil.getCurrentOperationalArea()).thenReturn(operationArea);
        ArrayList<String> list = new ArrayList<>(Arrays.asList("Eastern", "Chadiza", "Chadiza RHC", operationArea));
        Whitebox.setInternalState(presenter, "locationHelper", locationHelper);
        Whitebox.setInternalState(presenter, "interactor", interactor);
        presenter.onOperationalAreaSelectorClicked(list);
        verify(interactor).validateCurrentPlan(operationArea, planId);

    }

    @Test
    public void testOnPlanValidatedFailsClearsPlan() {
        presenter.onPlanValidated(false);
        verify(preferencesUtil).setCurrentPlanId("");
        verify(preferencesUtil).setCurrentPlan("");
        verify(view).setPlan("");
        verify(view).lockNavigationDrawerForSelection();
    }

//    @Test
//    public void testOnPlanValidatedDoesNotClearPlan() {
//        presenter.onPlanValidated(true);
//        verifyZeroInteractions(preferencesUtil);
//        verifyZeroInteractions(interactor);
//        verifyZeroInteractions(view);
//
//    }

    @Test
    public void testOnShowOfflineMaps() {
        presenter.onShowOfflineMaps();
        verify(view).openOfflineMapsView();
    }

//    @Test
//    public void testOnOperationalAreaSelectedValidatesPlanWhenOAHasNoNodes() {
//        String planId = UUID.randomUUID().toString();
//        String operationArea = UUID.randomUUID().toString();
//        when(preferencesUtil.getCurrentPlanId()).thenReturn(planId);
//        when(preferencesUtil.getCurrentOperationalArea()).thenReturn(operationArea);
//        ArrayList<String> list = new ArrayList<>(Arrays.asList("Eastern", "Chadiza", "Chadiza RHC", operationArea));
//        Whitebox.setInternalState(presenter, "locationHelper", locationHelper);
//        Whitebox.setInternalState(presenter, "interactor", interactor);
//
//        FormLocation locationHierarchy = TestingUtils.generateLocationHierarchy();
//        when(locationHelper.generateLocationHierarchyTree(anyBoolean(), any())).thenReturn(Collections.singletonList(locationHierarchy));
//        presenter.onOperationalAreaSelectorClicked(list);
//        verify(interactor).validateCurrentPlan(operationArea, planId);
//
//    }

    @Test
    public void testOnViewResumedWithViewNotInitialized() {

        when(preferencesUtil.getCurrentPlan()).thenReturn("IRS Lusaka");
        presenter = spy(presenter);
        doNothing().doNothing().when(presenter).updateSyncStatusDisplay(synced.capture());
        Whitebox.setInternalState(presenter, "locationHelper", locationHelper);
        List<String> defaultLocations = new ArrayList<>();
        defaultLocations.add("Lusaka");
        defaultLocations.add("Mtendere");
        when(locationHelper.generateDefaultLocationHierarchy(any())).thenReturn(defaultLocations);

        assertFalse(Whitebox.getInternalState(presenter, "viewInitialized"));

        presenter.onViewResumed();

        assertTrue(Whitebox.getInternalState(presenter, "viewInitialized"));
        verify(view).setOperator();
        verify(view).setDistrict("Lusaka");
        verify(view).setFacility("Mtendere", HEALTH_CENTER);
        verify(view).setPlan("IRS Lusaka");

    }

    @Test
    public void testOnViewResumedWithViewInitialized() {

        Whitebox.setInternalState(presenter, "viewInitialized", true);
        when(preferencesUtil.getCurrentPlan()).thenReturn("IRS Lusaka");

        assertTrue(Whitebox.getInternalState(presenter, "viewInitialized"));
        assertFalse(Whitebox.getInternalState(presenter, "changedCurrentSelection"));

        presenter = spy(presenter);
        doNothing().doNothing().when(presenter).updateSyncStatusDisplay(synced.capture());
        presenter.onViewResumed();

        assertTrue(Whitebox.getInternalState(presenter, "changedCurrentSelection"));
        verify(presenter).onDrawerClosed();

    }

    @Test
    public void testIsChangedCurrentSelectio() {
        Whitebox.setInternalState(presenter, "changedCurrentSelection", true);

        boolean actualIsChangedCurrentSelection = presenter.isChangedCurrentSelection();

        assertTrue(actualIsChangedCurrentSelection);
    }

    @Test
    public void testSetChangedCurrentSelectio() {
        assertFalse(Whitebox.getInternalState(presenter, "changedCurrentSelection"));

        presenter.setChangedCurrentSelection(true);

        assertTrue(Whitebox.getInternalState(presenter, "changedCurrentSelection"));
    }

    @Test
    public void testOnDraweClosed() {
        presenter.onDrawerClosed();
        verify(drawerActivity).onDrawerClosed();
    }

//    @Test
//    public void testOnShowPlanSelectorWhenCurrentPlanIsBlank() {
//        presenter.onShowPlanSelector();
//        verify(view).displayNotification(R.string.operational_area, R.string.operational_area_not_selected);
//    }

    @Test
    public void testOnShowPlanSelector() {
        Whitebox.setInternalState(presenter, "interactor", interactor);
        when(preferencesUtil.getCurrentOperationalArea()).thenReturn("Lusaka");
        presenter.onShowPlanSelector();
        verify(interactor).fetchPlans("Lusaka");
    }

    @Test
    public void testOnPlanSelectorClicked() {
        ArrayList<String> name = new ArrayList<>();
        name.add("IRS Lusaka");
        ArrayList<String> value = new ArrayList<>();
        value.add("plan_1");
        assertFalse(Whitebox.getInternalState(presenter, "changedCurrentSelection"));

        presenter.onPlanSelectorClicked(value, name);
        verify(preferencesUtil).setCurrentPlan("IRS Lusaka");
        verify(preferencesUtil).setCurrentPlanId("plan_1");
        verify(view).setPlan("IRS Lusaka");
        assertTrue(Whitebox.getInternalState(presenter, "changedCurrentSelection"));
    }

//    @Test
//    public void testOnShowOperationalAreaSelector() {
//        when(preferencesUtil.getPreferenceValue(anyString())).thenReturn("akros_1");
//        when(preferencesUtil.getCurrentPlan()).thenReturn("IRS Lusaka");
//        Whitebox.setInternalState(presenter, "locationHelper", locationHelper);
//        List<String> defaultLocations = new ArrayList<>();
//        defaultLocations.add("Lusaka");
//        defaultLocations.add("Mtendere");
//        when(locationHelper.generateDefaultLocationHierarchy(any())).thenReturn(defaultLocations);
//
//        FormLocation locationHierarchy = TestingUtils.generateLocationHierarchy();
//        when(locationHelper.generateLocationHierarchyTree(anyBoolean(), any())).thenReturn(Collections.singletonList(locationHierarchy));
//
//        presenter.onShowOperationalAreaSelector();
//
//        verify(locationHelper, times(2)).generateDefaultLocationHierarchy(arrayListArgumentCaptor.capture());
//        assertTrue(arrayListArgumentCaptor.getValue().contains(COUNTRY));
//        assertTrue(arrayListArgumentCaptor.getValue().contains(PROVINCE));
//        assertTrue(arrayListArgumentCaptor.getValue().contains(REGION));
//        assertTrue(arrayListArgumentCaptor.getValue().contains(DISTRICT));
//        assertTrue(arrayListArgumentCaptor.getValue().contains(SUB_DISTRICT));
//        assertTrue(arrayListArgumentCaptor.getValue().contains(OPERATIONAL_AREA));
//
//        verify(view).showOperationalAreaSelector(pairArgumentCaptor.capture());
//        assertEquals("[{\"name\":\"Zambia\",\"nodes\":[{\"name\":\"Chadiza 1\"}]}]", pairArgumentCaptor.getValue().first);
//        assertTrue(pairArgumentCaptor.getValue().second.contains("Lusaka"));
//        assertTrue(pairArgumentCaptor.getValue().second.contains("Mtendere"));
//    }

    @Test
    public void testOnViewResumedCallsCheckSyncedIfAlreadySyncedAndRefreshMapOnEventSaved() {

        Whitebox.setInternalState(EusmApplication.getInstance(), "refreshMapOnEventSaved", true);
        Whitebox.setInternalState(EusmApplication.getInstance(), "synced", true);
        Whitebox.setInternalState(presenter, "viewInitialized", true);
        when(preferencesUtil.getCurrentPlan()).thenReturn("IRS Lusaka");

        presenter = spy(presenter);
        doNothing().doNothing().when(presenter).updateSyncStatusDisplay(synced.capture());
        presenter.onViewResumed();

        verify(view).checkSynced();

    }

    @Test
    public void testOnViewResumedUpdateSyncStatusDisplayIfNotRefreshMapOnEventSavedAndSynced() {

        Whitebox.setInternalState(EusmApplication.getInstance(), "refreshMapOnEventSaved", false);
        Whitebox.setInternalState(EusmApplication.getInstance(), "synced", true);
        Whitebox.setInternalState(presenter, "viewInitialized", true);
        when(preferencesUtil.getCurrentPlan()).thenReturn("IRS Lusaka");

        presenter = spy(presenter);
        doNothing().doNothing().when(presenter).updateSyncStatusDisplay(synced.capture());
        presenter.onViewResumed();

        verify(presenter).updateSyncStatusDisplay(true);

    }

    @Test
    public void testOnViewResumedUpdateSyncStatusDisplayIfNotRefreshMapOnEventSavedAndNotSynced() {

        Whitebox.setInternalState(EusmApplication.getInstance(), "refreshMapOnEventSaved", false);
        Whitebox.setInternalState(EusmApplication.getInstance(), "synced", false);
        Whitebox.setInternalState(presenter, "viewInitialized", true);
        when(preferencesUtil.getCurrentPlan()).thenReturn("IRS Lusaka");

        presenter = spy(presenter);
        doNothing().doNothing().when(presenter).updateSyncStatusDisplay(synced.capture());
        presenter.onViewResumed();

        verify(presenter).updateSyncStatusDisplay(false);

    }

//    @Test
//    public void testOnViewResumedShouldSetPlanAndOperationalAreaAndLockDrawer() {
//
//        Whitebox.setInternalState(EusmApplication.getInstance(), "refreshMapOnEventSaved", false);
//        Whitebox.setInternalState(EusmApplication.getInstance(), "synced", false);
//        Whitebox.setInternalState(presenter, "viewInitialized", true);
//        when(preferencesUtil.getCurrentPlan()).thenReturn("plan_1");
//        when(preferencesUtil.getCurrentOperationalArea()).thenReturn(null);
//        when(view.getOperationalArea()).thenReturn("oa_1");
//        when(view.getPlan()).thenReturn("plan_1");
//
//
//        presenter = spy(presenter);
//        doNothing().doNothing().when(presenter).updateSyncStatusDisplay(anyBoolean());
//
//        presenter.onViewResumed();
//        verify(view).setOperationalArea(null);
//        verify(view).setPlan("plan_1");
//        verify(view).lockNavigationDrawerForSelection(R.string.select_mission_operational_area_title, R.string.revoked_plan_operational_area);
//
//    }

}
