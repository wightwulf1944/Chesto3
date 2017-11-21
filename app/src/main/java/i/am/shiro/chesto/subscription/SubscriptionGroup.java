package i.am.shiro.chesto.subscription;

import java.util.LinkedList;
import java.util.List;

import i.am.shiro.chesto.listener.Listener;
import i.am.shiro.chesto.notifier.AbstractNotifier;

/**
 * Created by Shiro on 11/5/2017.
 */

public class SubscriptionGroup {

    private final List<Subscription> subscriptions = new LinkedList<>();

    public void add(AbstractNotifier notifier, Listener listener) {
        subscriptions.add(new Subscription(notifier, listener));
    }

    public void unsubscribe() {
        for (Subscription subscription : subscriptions) {
            subscription.unsubscribe();
        }
        subscriptions.clear();
    }
}
