package us.carlosmendez.stripebot;

import com.stripe.Stripe;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import us.carlosmendez.stripebot.commands.CommandCreatePlan;
import us.carlosmendez.stripebot.commands.CommandCreateProduct;
import us.carlosmendez.stripebot.commands.CommandRedeem;
import us.carlosmendez.stripebot.tasks.SubscriptionChecker;

import javax.security.auth.login.LoginException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Main {
    private static JDA jda;
    public static Connection connection;

    public static void main(String[] args) throws LoginException {
        jda = new JDABuilder(AccountType.BOT)
                .setToken("NTQwMDc4MDM4OTM1OTI4ODMy.DzLqzA.omUEcKxVl2jViK6Y2LglTm0qAMc")
                .addEventListeners(new CommandCreateProduct())
                .addEventListeners(new CommandCreatePlan())
                .addEventListeners(new CommandRedeem())
                .build();

        Stripe.apiKey = "sk_test_my0xhwLYvtZy7di06F2cwOL0";

        try {
            openConnection();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        startChecker();
    }

    private static void openConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager
                .getConnection("jdbc:mysql://localhost:3306/stripe-bot", "root", "");
        PreparedStatement createCustomerTable = connection.prepareStatement("CREATE TABLE IF NOT EXISTS stripeCustomers(discordId VARCHAR(100) PRIMARY KEY, customerId VARCHAR(100), email VARCHAR(100))");
        PreparedStatement createSubscriptionTable = connection.prepareStatement("CREATE TABLE IF NOT EXISTS stripeSubscriptions(subscriptionId VARCHAR(100) PRIMARY KEY, customerId VARCHAR(100), active VARCHAR(100))");

        createCustomerTable.executeUpdate();
        createSubscriptionTable.executeUpdate();
        createCustomerTable.close();
        createSubscriptionTable.close();
    }

    private static void startChecker() {
        Thread checker = new SubscriptionChecker();

        checker.start();
    }
}