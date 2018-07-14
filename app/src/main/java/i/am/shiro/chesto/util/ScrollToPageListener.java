package i.am.shiro.chesto.util;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.annimon.stream.function.IntConsumer;

import static android.support.v7.widget.RecyclerView.NO_POSITION;

/**
 * Created by Subaru Tashiro on 7/24/2017.
 */

public class ScrollToPageListener extends RecyclerView.OnScrollListener {

    private IntConsumer onScrollToPageListener;

    public void setOnScrollToPageListener(IntConsumer listener) {
        onScrollToPageListener = listener;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int position = layoutManager.findFirstCompletelyVisibleItemPosition();

        if (position != NO_POSITION) {
            onScrollToPageListener.accept(position);
        }
    }
}
