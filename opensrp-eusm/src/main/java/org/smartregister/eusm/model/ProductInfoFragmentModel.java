package org.smartregister.eusm.model;

import android.content.Context;

import org.smartregister.eusm.R;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.model.ProductInfoQuestion;
import org.smartregister.eusm.model.TaskDetail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProductInfoFragmentModel {
    private Context context;

    public List<ProductInfoQuestion> getProductInfoQuestions(TaskDetail taskDetail) {
        ProductInfoQuestion p1 = new ProductInfoQuestion();
        p1.setAnswer(taskDetail.getAvailability());
        p1.setQuestion(getContext().getString(R.string.is_it_there));

        ProductInfoQuestion p2 = new ProductInfoQuestion();
        p2.setAnswer(taskDetail.getCondition());
        p2.setQuestion(getContext().getString(R.string.is_it_good_condition));

        ProductInfoQuestion p3 = new ProductInfoQuestion();
        p3.setAnswer(taskDetail.getAppropriateUsage());
        p3.setQuestion(getContext().getString(R.string.is_it_being_used_appropriately));
        return new ArrayList<>(Arrays.asList(p1, p2, p3));
    }

    public Context getContext() {
        if (context == null) {
            context = EusmApplication.getInstance().getBaseContext();
        }
        return context;
    }
}
