package org.smartregister.eusm.adapter;

import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.eusm.BaseUnitTest;
import org.smartregister.eusm.domain.StructureDetail;
import org.smartregister.eusm.viewholder.GenericTitleViewHolder;
import org.smartregister.eusm.viewholder.StructureRegisterViewHolder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class StructureRegisterAdapterTest extends BaseUnitTest {

    private StructureRegisterAdapter structureRegisterAdapter;

    @Mock
    private View.OnClickListener onClickListener;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        structureRegisterAdapter = spy(new StructureRegisterAdapter(RuntimeEnvironment.application, onClickListener));
    }

    @Test
    public void testOnCreateViewHolderShouldReturnCorrectViewHolder() {
        LinearLayout linearLayout = new LinearLayout(RuntimeEnvironment.application);

        RecyclerView.ViewHolder viewHolder = structureRegisterAdapter.onCreateViewHolder(linearLayout, 0);
        assertTrue(viewHolder instanceof GenericTitleViewHolder);

        viewHolder = structureRegisterAdapter.onCreateViewHolder(linearLayout, 1);
        assertTrue(viewHolder instanceof StructureRegisterViewHolder);
    }

    @Test
    public void testOnBindViewHolderShouldPopulateInformationViews() {
        LinearLayout linearLayout = new LinearLayout(RuntimeEnvironment.application);
        List<StructureDetail> structureDetailList = new ArrayList<>();

        StructureDetail structureDetail = new StructureDetail();
        structureDetail.setHeader(true);
        structureDetail.setEntityName("Nearby");
        structureDetailList.add(structureDetail);
        structureRegisterAdapter.setData(structureDetailList);
        GenericTitleViewHolder viewHolder = (GenericTitleViewHolder) spy(structureRegisterAdapter.onCreateViewHolder(linearLayout, 0));
        structureRegisterAdapter.onBindViewHolder(viewHolder, 0);
        verify(viewHolder).setTitle(structureDetail.getEntityName());

    }

    @Test
    public void testOnBindViewHolderShouldPopulateDataViews() {
        LinearLayout linearLayout = new LinearLayout(RuntimeEnvironment.application);
        List<StructureDetail> structureDetailList = new ArrayList<>();

        StructureDetail structureDetail = new StructureDetail();
        structureDetail.setEntityName("AAA ");
        structureDetail.setCommune("commune A");
        structureDetail.setTaskStatus("in_progress");
        structureDetail.setStructureType("Water Point");
        structureDetailList.add(structureDetail);
        structureRegisterAdapter.setData(structureDetailList);
        StructureRegisterViewHolder viewHolder = (StructureRegisterViewHolder) spy(structureRegisterAdapter.onCreateViewHolder(linearLayout, 1));
        structureRegisterAdapter.onBindViewHolder(viewHolder, 0);
        structureRegisterAdapter.clearData();
        verify(viewHolder).setCommune(eq(structureDetail.getCommune()));
        verify(viewHolder).setTaskStatus(eq(structureDetail));
        verify(viewHolder).setServicePointName(eq(structureDetail.getEntityName()));
        verify(viewHolder).setServicePointType(eq(structureDetail));
        verify(viewHolder).setServicePointIcon(eq(structureDetail.getTaskStatus()), eq(structureDetail.getStructureType()));
    }
}