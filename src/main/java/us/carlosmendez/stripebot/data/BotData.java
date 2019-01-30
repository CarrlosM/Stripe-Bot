package us.carlosmendez.stripebot.data;

public class BotData {
    private static String prefix = "!";

    public static String getPrefix() {
        return prefix;
    }

    public static void setPrefix(String prefix) {
        BotData.prefix = prefix;
    }
}