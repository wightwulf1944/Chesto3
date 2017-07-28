package i.am.shiro.chesto.activitysearch;

import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import i.am.shiro.chesto.listeners.Listener1;

/**
 * Created by Subaru Tashiro on 7/3/2017.
 */

final class EditorSearchListener implements TextView.OnEditorActionListener {

    private Listener1<String> onEditorSearchListener;

    void setAction(Listener1<String> listener) {
        onEditorSearchListener = listener;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            String searchString = v.getText().toString();
            onEditorSearchListener.onEvent(searchString);
            return true;
        } else {
            return false;
        }
    }
}
