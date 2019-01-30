package us.carlosmendez.stripebot;

import com.stripe.Stripe;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import us.carlosmendez.stripebot.commands.CommandCreatePlan;
import us.carlosmendez.stripebot.commands.CommandCreateProduct;

import javax.security.auth.login.LoginException;

public class Main {
    private static JDA jda;

    public static void main(String[] args) throws LoginException {
        jda = new JDABuilder(AccountType.BOT)
                .setToken("NTQwMDc4MDM4OTM1OTI4ODMy.DzLqzA.omUEcKxVl2jViK6Y2LglTm0qAMc")
                .addEventListeners(new CommandCreateProduct())
                .addEventListeners(new CommandCreatePlan())
                .build();

        Stripe.apiKey = "sk_test_my0xhwLYvtZy7di06F2cwOL0";
    }
}