package us.carlosmendez.stripebot.data;

import com.stripe.model.Customer;
import com.stripe.model.Subscription;
import net.dv8tion.jda.api.entities.User;
import us.carlosmendez.stripebot.Main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class Queries {

    public static void addCustomer(User user, String customerId, String email) throws SQLException {
        Connection connection = Main.connection;
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO stripeCustomers (discordId, customerId, email) VALUES (?,?,?)");
        preparedStatement.setString(1, user.getId());
        preparedStatement.setString(2, customerId);
        preparedStatement.setString(3, email);

        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    public static String getCustomerByEmail(String email) throws SQLException {
        Connection connection = Main.connection;
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM stripeCustomers WHERE email=?");
        preparedStatement.setString(1, email);

        ResultSet resultSet = preparedStatement.executeQuery();
        String customer = null;

        if (resultSet.next()) customer = resultSet.getString("customerId");

        resultSet.close();
        preparedStatement.close();
        return customer;
    }

    public static String getCustomerIdByDiscord(String discordId) throws SQLException {
        Connection connection = Main.connection;
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM stripeCustomers WHERE discordId=?");
        preparedStatement.setString(1, discordId);

        ResultSet resultSet = preparedStatement.executeQuery();
        String customer = null;

        if (resultSet.next()) customer = resultSet.getString("customer");

        resultSet.close();
        preparedStatement.close();
        return customer;
    }

    public static String getDiscordIdByCustomer(String customer) throws SQLException {
        Connection connection = Main.connection;
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM stripeCustomers WHERE customer=?");
        preparedStatement.setString(1, customer);

        ResultSet resultSet = preparedStatement.executeQuery();
        String discordId = null;

        if (resultSet.next()) discordId = resultSet.getString("discordId");

        resultSet.close();
        preparedStatement.close();
        return discordId;
    }

    public static Collection<String> getSubscriptions() throws SQLException {
        Connection connection = Main.connection;
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM stripeSubscriptions WHERE active=true");

        ResultSet resultSet = preparedStatement.executeQuery();
        Collection<String> subscriptions = new ArrayList<>();

        while (resultSet.next()) {
            subscriptions.add(resultSet.getString("subscriptionId"));
        }

        resultSet.close();
        preparedStatement.close();
        return subscriptions;
    }

    public static void registerSubscription(Subscription subscription, Customer customer) throws SQLException {
        Connection connection = Main.connection;
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO stripeSubscriptions (subscriptionId, customerId, active) VALUES (?, ?, ?)");
        preparedStatement.setString(1, subscription.getId());
        preparedStatement.setString(2, customer.getId());
        preparedStatement.setBoolean(3, true);

        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    public static void setSubscriptionActive(String subscriptionId, boolean active) throws SQLException {
        Connection connection = Main.connection;
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE stripeSubscriptions active=? WHERE subscriptionId=?");
        preparedStatement.setBoolean(1, active);
        preparedStatement.setString(2, subscriptionId);

        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    public static boolean subscriptionRegistered(Subscription subscription) throws SQLException {
        Connection connection = Main.connection;
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM stripeSubscriptions WHERE subscriptionId=?");
        preparedStatement.setString(1, subscription.getId());

        ResultSet resultSet = preparedStatement.executeQuery();
        boolean registered = false;

        if (resultSet.next()) registered = true;

        resultSet.close();
        preparedStatement.close();
        return registered;
    }
}