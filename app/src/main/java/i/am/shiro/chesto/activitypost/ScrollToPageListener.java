package i.am.shiro.chesto.activitypost;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import i.am.shiro.chesto.listeners.Listener1;

import static android.support.v7.widget.RecyclerView.NO_POSITION;

/**
 * Created by Subaru Tashiro on 7/24/2017.
 */

class ScrollToPageListener extends RecyclerView.OnScrollListener {

    private Listener1<Integer> onScrollToPageListener;

    void setOnScrollToPageListener(Listener1<Integer> listener) {
        onScrollToPageListener = listener;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int position = layoutManager.findFirstCompletelyVisibleItemPosition();

        if (position != NO_POSITION) {
            onScrollToPageListener.onEvent(position);
        }
    }
}
