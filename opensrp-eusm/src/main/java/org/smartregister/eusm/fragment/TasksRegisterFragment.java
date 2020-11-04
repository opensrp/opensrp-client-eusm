package org.smartregister.eusm.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.eusm.R;
import org.smartregister.eusm.activity.ProductInfoActivity;
import org.smartregister.eusm.adapter.TaskRegisterAdapter;
import org.smartregister.eusm.contract.TaskRegisterFragmentContract;
import org.smartregister.eusm.model.StructureTaskDetail;
import org.smartregister.eusm.presenter.TaskRegisterFragmentPresenter;
import org.smartregister.eusm.util.AppConstants;

public class TasksRegisterFragment extends Fragment implements TaskRegisterFragmentContract.View, View.OnClickListener {

    private TaskRegisterAdapter taskRegisterAdapter;

    private TaskRegisterFragmentContract.Presenter presenter;

    public static TasksRegisterFragment newInstance(Bundle bundle) {
        Bundle args = bundle;
        TasksRegisterFragment fragment = new TasksRegisterFragment();
        if (args == null) {
            args = new Bundle();
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializePresenter();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_task_register, container, false);
        RecyclerView recyclerView = fragmentView.findViewById(R.id.task_recyclerview);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        taskRegisterAdapter = new TaskRegisterAdapter(this);

        recyclerView.setAdapter(taskRegisterAdapter);
        return fragmentView;
    }

    @Override
    public void initializePresenter() {
        presenter = new TaskRegisterFragmentPresenter(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        onResumption();
    }

    @Override
    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.task_register_row) {
            StructureTaskDetail structureTaskDetail = (StructureTaskDetail) view.getTag(R.id.task_detail);
            if (structureTaskDetail.isChecked()) {
                openUndoDialog(structureTaskDetail);
            } else {
                if (!structureTaskDetail.isNonProductTask()) {
                    Intent intent = new Intent(getActivity(), ProductInfoActivity.class);
                    intent.putExtra(AppConstants.IntentData.STRUCTURE_TASK_DETAIL, structureTaskDetail);
                    getActivity().startActivity(intent);
                } else {
                    //TODO open form
                }

            }
        }
    }

    private void openUndoDialog(StructureTaskDetail structureTaskDetail) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(String.format(getString(R.string.undo_looks_good_title), structureTaskDetail.getProductName()));
        builder.setMessage(R.string.undo_dialog_message);
        builder.setPositiveButton(R.string.undo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    @Override
    public void onResumption() {
        presenter().fetchData();
    }

    protected TaskRegisterFragmentPresenter presenter() {
        return (TaskRegisterFragmentPresenter) presenter;
    }

    @Override
    public TaskRegisterAdapter getAdapter() {
        return taskRegisterAdapter;
    }

    @Override
    public void onClick(View v) {
        onViewClicked(v);
    }
}
