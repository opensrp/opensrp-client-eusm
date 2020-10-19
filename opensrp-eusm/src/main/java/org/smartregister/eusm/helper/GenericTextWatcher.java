package org.smartregister.eusm.helper;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

public abstract class GenericTextWatcher implements TextWatcher {

    private final TextView textView;

    public GenericTextWatcher(TextView textView) {
        this.textView = textView;
    }

    public abstract void handleAfterTextChanged(TextView textView, String value);

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable != null) {
            String text = editable.toString();
            handleAfterTextChanged(textView, text);
        }
    }
}