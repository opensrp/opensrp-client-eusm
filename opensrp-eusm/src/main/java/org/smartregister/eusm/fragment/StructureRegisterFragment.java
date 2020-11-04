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
import org.smartregister.eusm.activity.HomeActivity;
import org.smartregister.eusm.activity.TaskRegisterActivity;
import org.smartregister.eusm.adapter.StructureRegisterAdapter;
import org.smartregister.eusm.contract.BaseDrawerContract;
import org.smartregister.eusm.contract.StructureRegisterFragmentContract;
import org.smartregister.eusm.model.StructureDetail;
import org.smartregister.eusm.presenter.StructureRegisterFragmentPresenter;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.eusm.util.LocationUtils;
import org.smartregister.eusm.view.NavigationDrawerView;

import java.text.MessageFormat;
import java.util.List;

public class StructureRegisterFragment extends BaseDrawerRegisterFragment implements StructureRegisterFragmentContract.View, BaseDrawerContract.DrawerActivity, View.OnClickListener {

    private StructureRegisterAdapter structureRegisterAdapter;

    private Button nextButton;

    private Button previousButton;

    private TextView strPageInfoView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        drawerView = new NavigationDrawerView(this);
    }

    @Override
    protected void initializePresenter() {
        presenter = new StructureRegisterFragmentPresenter(this);
    }

    @Override
    protected void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.btn_structure_register) {
            startMapActivity();
        } else if (id == R.id.drawerMenu) {
            drawerView.openDrawerLayout();
        } else if (id == R.id.next_button) {
            presenter().onNextButtonClick();
        } else if (id == R.id.previous_button) {
            presenter().onPreviousButtonClick();
        } else if (id == R.id.table_layout) {
            StructureDetail structureDetail = (StructureDetail) view.getTag(R.id.structure_detail);
            Intent intent = new Intent(getActivity(), TaskRegisterActivity.class);
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

    }

    @UiThread
    @Override
    public void setStructureDetails(List<StructureDetail> structureDetails) {
        structureRegisterAdapter.setData(structureDetails);
    }

    @Override
    public void displayNotification(int title, int message, Object... formatArgs) {

    }

    @Override
    public void showProgressDialog(int title, int message) {

    }

    @Override
    public void hideProgressDialog() {

    }

    @Override
    public LocationUtils getLocationUtils() {
        return null;
    }

    @Override
    public void displayIndexCaseDetails(JSONObject indexCase) {

    }

    @Override
    public void setNumberOfFilters(int numberOfFilters) {

    }

    @Override
    public void clearFilter() {

    }

    @Override
    public StructureRegisterAdapter getAdapter() {
        return structureRegisterAdapter;
    }


    @Override
    public void setSearchPhrase(String searchPhrase) {

    }

    @Override
    public void startMapActivity() {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
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

        TextView servicePointBtnRegisterTextView = view.findViewById(R.id.btn_structure_register);
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