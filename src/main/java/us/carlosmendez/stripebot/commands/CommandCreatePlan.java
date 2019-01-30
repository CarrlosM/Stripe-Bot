package us.carlosmendez.stripebot.commands;

import com.stripe.exception.StripeException;
import com.stripe.model.Plan;
import com.stripe.model.Product;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import us.carlosmendez.stripebot.data.BotData;

import java.util.HashMap;
import java.util.Map;

public class CommandCreatePlan extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        String[] args = e.getMessage().getContentRaw().split(" ");

        if (e.getAuthor().isBot() || !args[0].equalsIgnoreCase(BotData.getPrefix() + "createplan")) return;

        if (args.length < 7) {
            e.getChannel().sendMessage("The proper command usage is `" + BotData.getPrefix() + "createplan <name> <product> <currency code> <interval> <interval count> <price>`").queue();
            return;
        }

        try {
            String planName = args[1];
            String productName = args[2];
            String currencyCode = args[3];
            String interval = args[4];
            int intervalCount = Integer.parseInt(args[5]);
            int price = Integer.parseInt(args[6]);
            Map<String, Object> productParams = new HashMap<>();

            productParams.put("type", "service");
            productParams.put("limit", "20");

            for (Product product : Product.list(productParams).autoPagingIterable()) {
                if (!product.getName().equalsIgnoreCase(productName)) continue;
                Map<String, Object> planParams = new HashMap<>();

                planParams.put("product", product.getId());
                planParams.put("amount", price);
                planParams.put("interval", interval);
                planParams.put("currency", currencyCode);
                planParams.put("interval_count", intervalCount);
                planParams.put("nickname", planName);

                Plan.create(planParams);
                e.getChannel().sendMessage("Successfully created plan `" + planName + "`!").queue();
                return;
            }

            e.getChannel().sendMessage("Unable to find a product named `" + productName + "`").queue();
        } catch (IllegalArgumentException ex) {
            e.getChannel().sendMessage("The proper command usage is `" + BotData.getPrefix() + "createplan <name> <product> <currency code> <interval> <interval count> <price>`").queue();
        } catch (StripeException ex) {
            e.getChannel().sendMessage("Stripe exception has occurred! Is your key correct?").queue();
        }
    }
}