package i.am.shiro.chesto.listeners;

import i.am.shiro.chesto.notifiers.AbstractNotifier;

/**
 * Created by Shiro on 11/5/2017.
 */

public class Subscription {

    private AbstractNotifier notifier;

    private Listener listener;

    public Subscription(AbstractNotifier notifier, Listener listener) {
        this.notifier = notifier;
        this.listener = listener;
    }

    public void unsubscribe() {
        notifier.removeListener(listener);
    }
}
