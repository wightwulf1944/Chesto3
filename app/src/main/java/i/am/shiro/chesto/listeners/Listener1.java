package i.am.shiro.chesto.listeners;

/**
 * Created by Subaru Tashiro on 5/18/2017.
 *
 * Generic listener with 1 parameter
 */

public interface Listener1<T> {
    void onEvent(T t);
}
