package org.smartregister.eusm.adapter;

import android.widget.LinearLayout;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.eusm.BaseUnitTest;
import org.smartregister.eusm.R;
import org.smartregister.eusm.domain.ProductInfoQuestion;
import org.smartregister.eusm.viewholder.ProductInfoQuestionViewHolder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class ProductInfoQuestionsAdapterTest extends BaseUnitTest {

    private ProductInfoQuestionsAdapter productInfoQuestionsAdapter;

    @Before
    public void setUp() {
        productInfoQuestionsAdapter = spy(new ProductInfoQuestionsAdapter());
    }

    @Test
    public void testOnCreateViewHolderShouldInitializeViewHolderCorrectly() {
        LinearLayout linearLayout = new LinearLayout(RuntimeEnvironment.application);
        ProductInfoQuestionViewHolder productInfoQuestionViewHolder = productInfoQuestionsAdapter.onCreateViewHolder(linearLayout, 0);
        assertNotNull(productInfoQuestionViewHolder);
    }

    @Test
    public void testOnBindViewHolderShouldPopulateViews() {
        LinearLayout linearLayout = new LinearLayout(RuntimeEnvironment.application);
        ProductInfoQuestionViewHolder productInfoQuestionViewHolder = spy(productInfoQuestionsAdapter.onCreateViewHolder(linearLayout, 0));
        ProductInfoQuestion productInfoQuestion = new ProductInfoQuestion();
        productInfoQuestion.setImageId(R.drawable.ic_icon_used);
        productInfoQuestion.setAnswer("sample");
        productInfoQuestion.setQuestion("is it there");
        List<ProductInfoQuestion> productInfoQuestions = new ArrayList<>();
        productInfoQuestions.add(productInfoQuestion);
        productInfoQuestionsAdapter.setData(productInfoQuestions);
        productInfoQuestionsAdapter.onBindViewHolder(productInfoQuestionViewHolder, 0);
        productInfoQuestionsAdapter.clearData();
        verify(productInfoQuestionViewHolder).setProductInfoQuestion(productInfoQuestion.getQuestion());
        verify(productInfoQuestionViewHolder).setProductInfoAnswer(productInfoQuestion.getAnswer());
        verify(productInfoQuestionViewHolder).setImageProductInfoQuestion(productInfoQuestion.getImageId());
    }
}