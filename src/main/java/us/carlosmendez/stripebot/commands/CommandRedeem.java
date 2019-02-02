package us.carlosmendez.stripebot.commands;

import com.stripe.exception.StripeException;
import com.stripe.model.*;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import us.carlosmendez.stripebot.data.BotData;
import us.carlosmendez.stripebot.data.Queries;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CommandRedeem extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        String[] args = e.getMessage().getContentRaw().split(" ");
        User author = e.getAuthor();

        if (author.isBot() || !args[0].equalsIgnoreCase(BotData.getPrefix() + "redeem")) return;

        if (args.length < 2) {
            e.getChannel().sendMessage("The proper command usage is `" + BotData.getPrefix() + "redeem <email>`").queue();
            return;
        }
        String email = args[1];
        Map<String, Object> subscriptionParams = new HashMap<>();
        subscriptionParams.put("limit", 100);

        try {
            for (Subscription subscription : Subscription.list(subscriptionParams).autoPagingIterable()) {
                Customer customer = Customer.retrieve(subscription.getCustomer());
                String receiptEmail = customer.getEmail();

                if (!receiptEmail.equalsIgnoreCase(email)) continue;
                String customerId = Queries.getCustomerByEmail(email);

                if (customerId == null) {
                    Queries.addCustomer(e.getAuthor(), customer.getId(), receiptEmail);
                } else {
                    String discordId = Queries.getDiscordIdByCustomer(customer.getId());

                    if (!discordId.equalsIgnoreCase(author.getId())) {
                        e.getChannel().sendMessage("Your Discord account is not tied to this email").queue();
                        return;
                    }
                }

                if (Queries.subscriptionRegistered(subscription)) return;
                //New subscription, hasn't been activated yet. Do stuff to user.

                Queries.registerSubscription(subscription, customer);
            }
        } catch (StripeException ex) {
            System.out.println(ex.toString());
            e.getChannel().sendMessage("Stripe exception has occurred. Is your key correct?").queue();
        } catch (SQLException ex) {
            System.out.println(ex.toString());
            e.getChannel().sendMessage("SQL exception has occurred. Is your database information correct?").queue();
        }
    }
}