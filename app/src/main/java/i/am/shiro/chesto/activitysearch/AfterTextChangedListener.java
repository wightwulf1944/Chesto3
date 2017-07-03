package i.am.shiro.chesto.activitysearch;

import android.text.Editable;
import android.text.TextWatcher;

import i.am.shiro.chesto.listeners.Listener1;

/**
 * Created by Subaru Tashiro on 7/3/2017.
 */

final class AfterTextChangedListener implements TextWatcher {

    private Listener1<String> onEditorTextChangedListener;

    void setAction(Listener1<String> listener) {
        onEditorTextChangedListener = listener;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Do nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // Do nothing
    }

    @Override
    public void afterTextChanged(Editable s) {
        onEditorTextChangedListener.onEvent(s.toString());
    }
}
