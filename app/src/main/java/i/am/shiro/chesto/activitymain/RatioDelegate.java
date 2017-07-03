package i.am.shiro.chesto.activitymain;

import com.fivehundredpx.greedolayout.GreedoLayoutSizeCalculator;

import i.am.shiro.chesto.engine.PostSearch;
import i.am.shiro.chesto.models.Post;

/**
 * Created by Shiro on 5/2/2017.
 */

final class RatioDelegate implements GreedoLayoutSizeCalculator.SizeCalculatorDelegate {

    private static final double RATIO_MIN = 0.5;
    private static final double RATIO_MAX = 5;
    private static final double RATIO_DEFAULT = 1.0;

    private PostSearch searchResults;

    void setData(PostSearch searchResults) {
        this.searchResults = searchResults;
    }

    @Override
    public double aspectRatioForIndex(int i) {
        if (i >= searchResults.size()) {
            return RATIO_DEFAULT;
        } else {
            Post post = searchResults.getPost(i);
            double ratio = (double) post.getWidth() / post.getHeight();

            if (ratio < RATIO_MIN) return RATIO_MIN;
            else if (ratio > RATIO_MAX) return RATIO_MAX;
            else return ratio;
        }
    }
}