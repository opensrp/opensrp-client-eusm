package org.smartregister.eusm.fragment;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.UiThread;

import org.json.JSONObject;
import org.smartregister.eusm.R;
import org.smartregister.eusm.activity.EusmTaskRegisterActivity;
import org.smartregister.eusm.activity.EusmTaskingMapActivity;
import org.smartregister.eusm.adapter.StructureRegisterAdapter;
import org.smartregister.eusm.contract.StructureRegisterFragmentContract;
import org.smartregister.eusm.domain.StructureDetail;
import org.smartregister.eusm.presenter.StructureRegisterFragmentPresenter;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.tasking.TaskingLibrary;
import org.smartregister.tasking.contract.BaseDrawerContract;
import org.smartregister.tasking.util.LocationUtils;
import org.smartregister.tasking.util.TaskingLibraryConfiguration;

import java.text.MessageFormat;
import java.util.List;

public class StructureRegisterFragment extends BaseDrawerRegisterFragment implements StructureRegisterFragmentContract.View, BaseDrawerContract.DrawerActivity, View.OnClickListener {

    private StructureRegisterAdapter structureRegisterAdapter;

    private Button nextButton;

    private Button previousButton;

    private TextView strPageInfoView;

    private TaskingLibraryConfiguration taskingLibraryConfiguration;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskingLibraryConfiguration = TaskingLibrary.getInstance().getTaskingLibraryConfiguration();
        drawerView = taskingLibraryConfiguration.getDrawerMenuView(this);
    }

    @Override
    protected void initializePresenter() {
        presenter = new StructureRegisterFragmentPresenter(this);
    }

    @Override
    protected void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.task_register) {
            startMapActivity();
        } else if (id == R.id.drawerMenu) {
            drawerView.openDrawerLayout();
        } else if (id == R.id.next_button) {
            presenter().onNextButtonClick();
        } else if (id == R.id.previous_button) {
            presenter().onPreviousButtonClick();
        } else if (id == R.id.table_layout) {
            StructureDetail structureDetail = (StructureDetail) view.getTag(R.id.structure_detail);
            Intent intent = new Intent(getActivity(), EusmTaskRegisterActivity.class);
            intent.putExtra(AppConstants.IntentData.STRUCTURE_DETAIL, structureDetail);
            getActivity().startActivity(intent);
        }
    }

    public StructureRegisterFragmentPresenter presenter() {
        return (StructureRegisterFragmentPresenter) presenter;
    }

    @Override
    public void onDrawerClosed() {
        presenter().onDrawerClosed();
    }

    @Override
    public Location getLastLocation() {
        return null;
    }

    @Override
    public void initializeAdapter() {
        structureRegisterAdapter = new StructureRegisterAdapter(getContext(), registerActionHandler);
        clientsView.setAdapter(structureRegisterAdapter);
    }

    @Override
    public void setTotalServicePoints(int structuresWithinBuffer) {
        //do nothing
    }

    @UiThread
    @Override
    public void setStructureDetails(List<StructureDetail> structureDetails) {
        structureRegisterAdapter.setData(structureDetails);
    }

    @Override
    public void displayNotification(int title, int message, Object... formatArgs) {
        //do nothing
    }

    @Override
    public void showProgressDialog(int title, int message) {
        //do nothing
    }

    @Override
    public void hideProgressDialog() {
        //do nothing
    }

    @Override
    public LocationUtils getLocationUtils() {
        return null;
    }

    @Override
    public void displayIndexCaseDetails(JSONObject indexCase) {
        //do nothing
    }

    @Override
    public void setNumberOfFilters(int numberOfFilters) {
        //do nothing
    }

    @Override
    public void clearFilter() {
        //do nothing
    }

    @Override
    public StructureRegisterAdapter getAdapter() {
        return structureRegisterAdapter;
    }


    @Override
    public void setSearchPhrase(String searchPhrase) {
        //do nothing
    }

    @Override
    public void startMapActivity() {
        Intent intent = new Intent(getActivity(), EusmTaskingMapActivity.class);
        getActivity().startActivity(intent);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_structure_register;
    }

    @Override
    public void filter(String filterString, String joinTableString, String mainConditionString, boolean qrCode) {
        presenter().filterByName(filterString);
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);

        TextView servicePointBtnRegisterTextView = view.findViewById(R.id.task_register);
        servicePointBtnRegisterTextView.setText(getString(R.string.map));
        servicePointBtnRegisterTextView.setOnClickListener(this);

        strPageInfoView = view.findViewById(R.id.page_info_textView);

        nextButton = view.findViewById(R.id.next_button);
        nextButton.setOnClickListener(this);

        previousButton = view.findViewById(R.id.previous_button);
        previousButton.setOnClickListener(this);

        drawerView.initializeDrawerLayout();

        drawerView.onResume();

        view.findViewById(R.id.drawerMenu)
                .setOnClickListener(this);
    }

    public void updatePageInfo() {
        strPageInfoView.setText(MessageFormat.format(getString(R.string.str_page_info), (presenter().getCurrentPageNo() + 1), presenter().getTotalPageCount()));
    }

    public Button getNextButton() {
        return nextButton;
    }

    public Button getPreviousButton() {
        return previousButton;
    }

    @Override
    public void onClick(View v) {
        onViewClicked(v);
    }

}