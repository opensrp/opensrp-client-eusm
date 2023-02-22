package org.smartregister.eusm.viewholder;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.eusm.BaseUnitTest;
import org.smartregister.eusm.R;
import org.smartregister.eusm.domain.TaskDetail;
import org.smartregister.eusm.util.AppConstants;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class TaskRegisterViewHolderTest extends BaseUnitTest {

    private TaskRegisterViewHolder taskRegisterViewHolder;

    @Mock
    private ImageView checkedOverlayImageView;

    @Mock
    private ImageView rectangleOverlayImageView;

    @Mock
    private ImageView statusOverlayImageView;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        View view = LayoutInflater.from(RuntimeEnvironment.application)
                .inflate(R.layout.task_register_row, null);
        taskRegisterViewHolder = spy(new TaskRegisterViewHolder(view));
    }

    @Test
    public void testSetProductImageIfTaskNotCheckedShouldLoadWithoutOverlay() {
        TaskDetail taskDetail = new TaskDetail();
        taskDetail.setProductImage("/home/pseudo/location");

        ReflectionHelpers.setField(taskRegisterViewHolder, "checkedOverlayImageView", checkedOverlayImageView);
        ReflectionHelpers.setField(taskRegisterViewHolder, "rectangleOverlayImageView", rectangleOverlayImageView);
        ReflectionHelpers.setField(taskRegisterViewHolder, "statusOverlayImageView", statusOverlayImageView);

        taskRegisterViewHolder.setProductImage(taskDetail);

        verify(checkedOverlayImageView).setVisibility(eq(View.GONE));
        verify(rectangleOverlayImageView).setVisibility(eq(View.GONE));
        verify(statusOverlayImageView).setVisibility(eq(View.GONE));
    }

    @Test
    public void testSetProductImageIfTaskISCheckedShouldLoadWithOverlay() {
        TaskDetail taskDetail = new TaskDetail();
        taskDetail.setProductImage("/home/pseudo/location");
        taskDetail.setBusinessStatus(AppConstants.BusinessStatus.HAS_PROBLEM);
        taskDetail.setChecked(true);

        ReflectionHelpers.setField(taskRegisterViewHolder, "checkedOverlayImageView", checkedOverlayImageView);
        ReflectionHelpers.setField(taskRegisterViewHolder, "rectangleOverlayImageView", rectangleOverlayImageView);
        ReflectionHelpers.setField(taskRegisterViewHolder, "statusOverlayImageView", statusOverlayImageView);

        taskRegisterViewHolder.setProductImage(taskDetail);

        verify(checkedOverlayImageView).setVisibility(eq(View.VISIBLE));
        verify(rectangleOverlayImageView).setVisibility(eq(View.VISIBLE));
        verify(statusOverlayImageView).setVisibility(eq(View.GONE));
        verify(statusOverlayImageView).setVisibility(eq(View.VISIBLE));
    }

    @Test
    public void testSetProductImageShouldCallSetImageDrawableForFixProblemForCOnsultBeneficiaries() {
        TaskDetail taskDetail = new TaskDetail();
        taskDetail.setNonProductTask(true);
        taskDetail.setChecked(false);
        taskDetail.setTaskCode(AppConstants.TaskCode.FIX_PROBLEM_CONSULT_BENEFICIARIES);

        ImageView productImageView = mock(ImageView.class);
        ReflectionHelpers.setField(taskRegisterViewHolder, "checkedOverlayImageView", checkedOverlayImageView);
        ReflectionHelpers.setField(taskRegisterViewHolder, "rectangleOverlayImageView", rectangleOverlayImageView);
        ReflectionHelpers.setField(taskRegisterViewHolder, "statusOverlayImageView", statusOverlayImageView);
        ReflectionHelpers.setField(taskRegisterViewHolder, "productImageView", productImageView);

        taskRegisterViewHolder.setProductImage(taskDetail);

        verify(productImageView, times(2)).setImageDrawable(any(Drawable.class));
    }
}