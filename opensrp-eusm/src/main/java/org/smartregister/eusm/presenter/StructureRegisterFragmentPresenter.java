package org.smartregister.eusm.presenter;

import android.view.View;

import androidx.annotation.StringRes;

import org.smartregister.eusm.contract.StructureRegisterFragmentContract;
import org.smartregister.eusm.domain.StructureDetail;
import org.smartregister.eusm.fragment.StructureRegisterFragment;
import org.smartregister.eusm.interactor.StructureRegisterInteractor;
import org.smartregister.eusm.util.AppConstants;

import java.lang.ref.WeakReference;
import java.util.List;

public class StructureRegisterFragmentPresenter extends BaseRegisterFragmentPresenter implements StructureRegisterFragmentContract.Presenter, StructureRegisterFragmentContract.InteractorCallback {

    private final WeakReference<StructureRegisterFragmentContract.View> viewWeakReference;
    private final int pageSize = AppConstants.STRUCTURE_REGISTER_PAGE_SIZE;
    private final StructureRegisterInteractor structureRegisterInteractor;
    private int currentPageNo = 0;
    private int totalCount = 0;
    private int totalPageCount = 0;
    private String nameFilter;

    public StructureRegisterFragmentPresenter(StructureRegisterFragmentContract.View view) {
        this.viewWeakReference = new WeakReference<>(view);
        structureRegisterInteractor = new StructureRegisterInteractor();
    }

    public String getString(@StringRes int resId) {
        return getView().getContext().getString(resId);
    }

    @Override
    public void initializeQueries(String s) {
        if (getView().getAdapter() == null) {
            getView().initializeAdapter();
        }
        countOfStructures();
        fetchStructures();
    }

    public void countOfStructures() {
        structureRegisterInteractor.countOfStructures(this, nameFilter);
    }

    public void fetchStructures() {
        if (getView() != null) {
            getView().showProgressView();
        }
        structureRegisterInteractor.fetchStructures(this, currentPageNo, nameFilter);
    }


    public StructureRegisterFragmentContract.View getView() {
        if (viewWeakReference != null) {
            return viewWeakReference.get();
        } else {
            return null;
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onDrawerClosed() {

    }

    public void filterByName(String nameFilter) {
        this.nameFilter = nameFilter;
        currentPageNo = 0;

        getView().getAdapter().clearData();

        countOfStructures();
        fetchStructures();
    }

    @Override
    public void onNextButtonClick() {
        getView().getAdapter().clearData();

        ++currentPageNo;

        fetchStructures();

        if ((totalCount - ((currentPageNo) * pageSize)) > pageSize) {
            getFragment().clientsView.scrollToPosition(0);
            getFragment().getNextButton().setVisibility(View.VISIBLE);
        } else {
            getFragment().getNextButton().setVisibility(View.INVISIBLE);
        }
    }

    private StructureRegisterFragment getFragment() {
        return (StructureRegisterFragment) getView();
    }

    @Override
    public void onPreviousButtonClick() {
        getView().getAdapter().clearData();

        getFragment().getNextButton().setVisibility(View.VISIBLE);

        --currentPageNo;

        fetchStructures();

        int pageNo = currentPageNo - 1;
        getFragment().clientsView.scrollToPosition(0);

        if (pageNo < 0) getFragment().getPreviousButton().setVisibility(View.INVISIBLE);

    }

    public int getCurrentPageNo() {
        return currentPageNo;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getTotalPageCount() {
        return totalPageCount;
    }

    public String getNameFilter() {
        return nameFilter;
    }

    public void setNameFilter(String nameFilter) {
        this.nameFilter = nameFilter;
    }

    @Override
    public void onFetchedStructures(List<StructureDetail> structureDetails) {
        if (getView() != null) {
            getView().hideProgressView();
        }

        getFragment().updatePageInfo();
        getView().setStructureDetails(structureDetails);
    }

    @Override
    public void onCountOfStructuresFetched(int count) {
        totalCount = count;
        totalPageCount = (int) Math.ceil((double) totalCount == 0 ? 1 : totalCount / (double) pageSize);
        getFragment().getNextButton().setVisibility((totalPageCount > 1) ? View.VISIBLE : View.INVISIBLE);
        getFragment().updatePageInfo();
    }
}
