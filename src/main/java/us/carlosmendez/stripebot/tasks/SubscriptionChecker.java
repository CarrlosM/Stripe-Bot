package us.carlosmendez.stripebot.tasks;

import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;
import us.carlosmendez.stripebot.data.Queries;

import java.sql.SQLException;
import java.util.Collection;

@SuppressWarnings("InfiniteLoopStatement")
public class SubscriptionChecker extends Thread {
    @Override
    public void run() {
        while (true) {
            try {
                Collection<String> subscriptions = Queries.getSubscriptions();

                for (String activeSubscription : subscriptions) {
                    Subscription subscription = Subscription.retrieve(activeSubscription);

                    if (subscription == null) continue;

                    if (subscription.getEndedAt() < System.currentTimeMillis() && !subscription.getStatus().equalsIgnoreCase("active")) {
                        //Subscription is no longer active, do stuff with Discord User
                        String discordUser = Queries.getDiscordIdByCustomer(subscription.getCustomer());

                        Queries.setSubscriptionActive(subscription.getId(), false);
                    }
                }

                Thread.sleep(60000);
            } catch (InterruptedException | SQLException | StripeException e) {
                e.printStackTrace();
            }
        }
    }
}