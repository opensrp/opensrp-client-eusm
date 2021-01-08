package org.smartregister.eusm.presenter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.annotation.LooperMode;
import org.smartregister.eusm.BaseUnitTest;
import org.smartregister.eusm.contract.ProductInfoFragmentContract;
import org.smartregister.eusm.domain.TaskDetail;

import java.util.UUID;

import static android.os.Looper.getMainLooper;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;
import static org.robolectric.annotation.LooperMode.Mode.PAUSED;

@LooperMode(PAUSED)
public class ProductInfoFragmentPresenterTest extends BaseUnitTest {

    private ProductInfoFragmentPresenter productInfoFragmentPresenter;

    @Mock
    private ProductInfoFragmentContract.View view;

    @Before
    public void setUp() {
        productInfoFragmentPresenter = spy(new ProductInfoFragmentPresenter(view));
    }

    @Test
    public void testFetchProductQuestionsShouldInvokeCallback() throws InterruptedException {
        TaskDetail taskDetail = new TaskDetail();
        taskDetail.setChecked(true);
        taskDetail.setEntityName("Freezer");
        taskDetail.setTaskId(UUID.randomUUID().toString());
        productInfoFragmentPresenter.fetchProductQuestions(taskDetail);
        shadowOf(getMainLooper()).idle();
        Thread.sleep(ASYNC_TIMEOUT);
        verify(productInfoFragmentPresenter).onQuestionsFetched(anyList());
    }
}