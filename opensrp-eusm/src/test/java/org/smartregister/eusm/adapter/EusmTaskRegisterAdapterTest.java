package org.smartregister.eusm.adapter;

import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.eusm.BaseUnitTest;
import org.smartregister.eusm.domain.TaskDetail;
import org.smartregister.eusm.viewholder.GenericEmptyViewHolder;
import org.smartregister.eusm.viewholder.GenericTitleViewHolder;
import org.smartregister.eusm.viewholder.TaskRegisterViewHolder;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class EusmTaskRegisterAdapterTest extends BaseUnitTest {

    private EusmTaskRegisterAdapter eusmTaskRegisterAdapter;

    @Mock
    private View.OnClickListener onClickListener;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        eusmTaskRegisterAdapter = spy(new EusmTaskRegisterAdapter(RuntimeEnvironment.application, onClickListener));
    }

    @Test
    public void testOnCreateViewHolderShouldReturnRequiredView() {
        LinearLayout viewGroup = new LinearLayout(RuntimeEnvironment.application);

        RecyclerView.ViewHolder viewHolder = eusmTaskRegisterAdapter.onCreateViewHolder(viewGroup, 0);
        Assert.assertTrue(viewHolder instanceof GenericTitleViewHolder);

        viewHolder = eusmTaskRegisterAdapter.onCreateViewHolder(viewGroup, 1);
        Assert.assertTrue(viewHolder instanceof GenericEmptyViewHolder);

        viewHolder = eusmTaskRegisterAdapter.onCreateViewHolder(viewGroup, 2);
        Assert.assertTrue(viewHolder instanceof TaskRegisterViewHolder);
    }

    @Test
    public void testOnBindViewHolderShouldUpdateInformationViews() {
        List<TaskDetail> taskDetailList = new ArrayList<>();
        TaskDetail taskDetailHeader = new TaskDetail();
        taskDetailHeader.setHeader(true);
        taskDetailHeader.setEntityName("header");
        taskDetailList.add(taskDetailHeader);

        TaskDetail taskDetailEmptyView = new TaskDetail();
        taskDetailEmptyView.setEmptyView(true);
        taskDetailEmptyView.setEntityName("no item");
        taskDetailList.add(taskDetailEmptyView);
        eusmTaskRegisterAdapter.setData(taskDetailList);
        LinearLayout viewGroup = new LinearLayout(RuntimeEnvironment.application);
        GenericTitleViewHolder viewHolder = (GenericTitleViewHolder) spy(eusmTaskRegisterAdapter.onCreateViewHolder(viewGroup, 0));
        eusmTaskRegisterAdapter.onBindViewHolder(viewHolder, 0);
        verify(viewHolder).setTitle(eq(taskDetailHeader.getEntityName()));

        GenericEmptyViewHolder genericEmptyViewHolder = (GenericEmptyViewHolder) spy(eusmTaskRegisterAdapter.onCreateViewHolder(viewGroup, 1));
        eusmTaskRegisterAdapter.onBindViewHolder(genericEmptyViewHolder, 1);
        eusmTaskRegisterAdapter.clearData();
        verify(genericEmptyViewHolder).setTitle(eq(taskDetailEmptyView.getEntityName()));
    }

    @Test
    public void testOnBindViewHolderShouldUpdateDataViews() {
        List<TaskDetail> taskDetailList = new ArrayList<>();
        TaskDetail taskDetail = new TaskDetail();
        taskDetail.setEntityName("Product A");
        taskDetail.setProductSerial("234-23");
        taskDetailList.add(taskDetail);
        eusmTaskRegisterAdapter.setData(taskDetailList);
        LinearLayout viewGroup = new LinearLayout(RuntimeEnvironment.application);

        TaskRegisterViewHolder viewHolder = (TaskRegisterViewHolder) spy(eusmTaskRegisterAdapter.onCreateViewHolder(viewGroup, 2));
        eusmTaskRegisterAdapter.onBindViewHolder(viewHolder, 0);
        eusmTaskRegisterAdapter.clearData();

        verify(viewHolder).setProductName(eq(taskDetail.getEntityName()), eq(false));
        verify(viewHolder).setProductSerial(eq(taskDetail));
        verify(viewHolder).setProductImage(eq(taskDetail));
    }

}