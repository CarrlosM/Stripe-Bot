package us.carlosmendez.stripebot.commands;

import com.stripe.exception.StripeException;
import com.stripe.model.Product;
import com.sun.deploy.util.StringUtils;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import us.carlosmendez.stripebot.data.BotData;

import java.util.*;

public class CommandCreateProduct extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        String[] args = e.getMessage().getContentRaw().split(" ");

        if (e.getAuthor().isBot() || !args[0].equalsIgnoreCase(BotData.getPrefix() + "createproduct")) return;

        if (args.length < 2) {
            e.getChannel().sendMessage("The proper command usage is `" + BotData.getPrefix() + "createproduct <name> [description]`").queue();
            return;
        }
        String name = args[1];
        String[] description = null;
        Map<String, Object> productParams = new HashMap<>();

        if (args.length > 2) {
            description = Arrays.copyOfRange(args, 2, args.length);
        }

        productParams.put("name", name);
        productParams.put("type", "service");

        if (description != null) {
            productParams.put("description", StringUtils.join(new ArrayList<>(Arrays.asList(description)), " "));
        }

        try {
            Product product = Product.create(productParams);

            e.getChannel().sendMessage("You have created the product " + product.getName() + "!").queue();
        } catch (StripeException ex) {
            System.out.println(ex.toString());
            e.getChannel().sendMessage("Stripe exception has occurred. Is your key correct?").queue();
        }
    }
}