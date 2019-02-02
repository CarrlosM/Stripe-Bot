# Stripe-Bot
This bot is NOT intended to be used by itself.
I simply created this to show how Stripe would be implemented in Java with a Discord Bot using JDA and MySQL.

This relies on the Stripe API without hooking into it directly and having to authorize something on your Stripe account. 
It uses emails as a sort of "token" for people to redeem their purchased subscriptions. It then ties that Discord user to the email to prevent other people from using their email if it gets leaked.
