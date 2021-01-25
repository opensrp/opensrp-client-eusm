package org.smartregister.eusm.model;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.eusm.R;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.domain.ProductInfoQuestion;
import org.smartregister.eusm.domain.TaskDetail;

import java.util.ArrayList;
import java.util.List;

public class ProductInfoFragmentModel {
    private Context context;

    public List<ProductInfoQuestion> getProductInfoQuestions(TaskDetail taskDetail) {
        List<ProductInfoQuestion> productInfoQuestions = new ArrayList<>();

        ProductInfoQuestion p1 = new ProductInfoQuestion();
        p1.setAnswer(taskDetail.getAvailability());
        p1.setQuestion(getContext().getString(R.string.is_it_there));
        p1.setImageId(R.drawable.ic_icon_there);

        productInfoQuestions.add(p1);

        if (StringUtils.isNotBlank(taskDetail.getCondition())) {
            ProductInfoQuestion p2 = new ProductInfoQuestion();
            p2.setAnswer(taskDetail.getCondition());
            p2.setQuestion(getContext().getString(R.string.is_it_good_condition));
            p2.setImageId(R.drawable.ic_icon_condition);
            productInfoQuestions.add(p2);
        }

        if (StringUtils.isNotBlank(taskDetail.getAppropriateUsage())) {
            ProductInfoQuestion p3 = new ProductInfoQuestion();
            p3.setAnswer(taskDetail.getAppropriateUsage());
            p3.setQuestion(getContext().getString(R.string.is_it_being_used_appropriately));
            p3.setImageId(R.drawable.ic_icon_used);
            productInfoQuestions.add(p3);
        }

        return productInfoQuestions;
    }

    public Context getContext() {
        if (context == null) {
            context = EusmApplication.getInstance().getBaseContext();
        }
        return context;
    }
}
