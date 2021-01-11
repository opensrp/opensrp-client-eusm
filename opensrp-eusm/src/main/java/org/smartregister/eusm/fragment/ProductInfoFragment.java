package org.smartregister.eusm.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.eusm.R;
import org.smartregister.eusm.adapter.ProductInfoQuestionsAdapter;
import org.smartregister.eusm.contract.ProductInfoFragmentContract;
import org.smartregister.eusm.domain.TaskDetail;
import org.smartregister.eusm.presenter.ProductInfoFragmentPresenter;
import org.smartregister.eusm.util.AppConstants;

public class ProductInfoFragment extends Fragment implements ProductInfoFragmentContract.View {

    private TaskDetail taskDetail;

    private ProductInfoQuestionsAdapter productInfoQuestionsAdapter;

    private ProductInfoFragmentContract.Presenter presenter;

    public static ProductInfoFragment newInstance(Bundle bundle) {
        ProductInfoFragment fragment = new ProductInfoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskDetail = (TaskDetail) getArguments().getSerializable(AppConstants.IntentData.TASK_DETAIL);
//        StructureDetail structureDetail = (StructureDetail) getArguments().getSerializable(AppConstants.IntentData.STRUCTURE_DETAIL);
        initializeAdapter();
        initializePresenter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(productInfoQuestionsAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.fetchProductQuestions(taskDetail);
    }

    protected int getLayoutId() {
        return R.layout.fragment_product_info;
    }

    @Override
    public void initializeAdapter() {
        productInfoQuestionsAdapter = new ProductInfoQuestionsAdapter();
    }

    @Override
    public ProductInfoQuestionsAdapter getAdapter() {
        return productInfoQuestionsAdapter;
    }

    @Override
    public void initializePresenter() {
        presenter = new ProductInfoFragmentPresenter(this);
    }

}