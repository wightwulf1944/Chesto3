package i.am.shiro.chesto.util;

import android.text.Editable;
import android.text.TextWatcher;

import com.annimon.stream.function.Consumer;


public class SimpleTextWatcher implements TextWatcher {

    private final Consumer<String> onTextChangedListener;

    public SimpleTextWatcher(Consumer<String> onTextChangedListener) {
        this.onTextChangedListener = onTextChangedListener;
    }

    @Override
    public void afterTextChanged(Editable s) {
        onTextChangedListener.accept(s.toString());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // do nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // do nothing
    }
}
