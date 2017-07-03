package i.am.shiro.chesto.activitysearch;

import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import i.am.shiro.chesto.listeners.Listener0;

/**
 * Created by Subaru Tashiro on 7/3/2017.
 */

final class EditorSearchListener implements TextView.OnEditorActionListener {

    private Listener0 onEditorSearchListener;

    void setAction(Listener0 listener) {
        onEditorSearchListener = listener;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            onEditorSearchListener.onEvent();
            return true;
        } else {
            return false;
        }
    }
}
