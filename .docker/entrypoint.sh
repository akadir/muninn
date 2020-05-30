#!/bin/bash
classpath="muninn.jar:lib/*"
mainclass="io.github.akadir.muninn.TelegramBot"

java -Dtwitter.consumer.key=$TWITTER_CONSUMER_KEY -Dtwitter.consumer.secret=$TWITTER_CONSUMER_SECRET \
     -Dtelegram.token=$TELEGRAM_TOKEN -Dtelegram.bot.name=$TELEGRAM_BOT_NAME \
     -Dmuninn.recheck.period=$MUNINN_RECHECK_PERIOD_IN_HOURS \
     -Ddata.source.url=$DATA_SOURCE_URL -Ddata.source.username=$DATA_SOURCE_USERNAME -Ddata.source.password=$DATA_SOURCE_PASSWORD \
     -Duser.timezone=$USER_TIMEZONE \
     -cp $classpath $mainclass
