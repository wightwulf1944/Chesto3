package i.am.shiro.chesto.util;

import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.function.IntConsumer;


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
