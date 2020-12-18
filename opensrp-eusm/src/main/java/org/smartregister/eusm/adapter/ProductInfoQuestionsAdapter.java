package org.smartregister.eusm.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.eusm.R;
import org.smartregister.eusm.domain.ProductInfoQuestion;
import org.smartregister.eusm.viewholder.ProductInfoQuestionViewHolder;

import java.util.ArrayList;
import java.util.List;

public class ProductInfoQuestionsAdapter extends RecyclerView.Adapter<ProductInfoQuestionViewHolder> {

    private List<ProductInfoQuestion> productInfoQuestions = new ArrayList<>();

    @NonNull
    @Override
    public ProductInfoQuestionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.product_info_question_row, viewGroup, false);
        return new ProductInfoQuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductInfoQuestionViewHolder viewHolder, int position) {
        ProductInfoQuestion productInfoQuestion = productInfoQuestions.get(position);
        viewHolder.setProductInfoAnswer(productInfoQuestion.getAnswer());
        viewHolder.setProductInfoQuestion(productInfoQuestion.getQuestion());
        viewHolder.setImageProductInfoQuestion(productInfoQuestion.getImageId());
    }

    @Override
    public int getItemCount() {
        return productInfoQuestions.size();
    }

    public void setData(List<ProductInfoQuestion> structureTaskDetails) {
        this.productInfoQuestions = structureTaskDetails;
        notifyDataSetChanged();
    }

    public void clearData() {
        if (productInfoQuestions != null) {
            productInfoQuestions.clear();
        }
    }
}
