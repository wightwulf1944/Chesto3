package i.am.shiro.chesto.notifiers;

import i.am.shiro.chesto.listeners.Listener1;

/**
 * Created by Shiro on 11/5/2017.
 */

public class Notifier1<T> extends AbstractNotifier<Listener1<T>> {

    public void notifyListeners(T x) {
        for (Listener1<T> listener : listeners) {
            listener.onEvent(x);
        }
    }
}
