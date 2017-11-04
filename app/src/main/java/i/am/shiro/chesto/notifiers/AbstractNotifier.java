package i.am.shiro.chesto.notifiers;

import java.util.LinkedList;
import java.util.List;

import i.am.shiro.chesto.listeners.Listener;
import i.am.shiro.chesto.listeners.Subscription;

/**
 * Created by Shiro on 11/5/2017.
 */

public abstract class AbstractNotifier<T extends Listener> {

    protected List<T> listeners = new LinkedList<>();

    public Subscription addListener(T listener) {
        listeners.add(listener);
        return new Subscription(this, listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }
}
