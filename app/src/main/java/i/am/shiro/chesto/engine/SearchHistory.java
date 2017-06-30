package i.am.shiro.chesto.engine;

import java.util.Deque;
import java.util.LinkedList;

/**
 * Created by Subaru Tashiro on 6/14/2017.
 */

public class SearchHistory {

    private static final Deque<PostSearch> stack = new LinkedList<>();

    public static void goForward(PostSearch postSearch) {
        stack.push(postSearch);
    }

    public static PostSearch current() {
        return stack.peek();
    }

    public static void goBack() {
        stack.pop();
    }

    public static boolean canGoBack() {
        return stack.size() > 1;
    }
}
