#!/bin/bash
classpath="muninn.jar:lib/*"
mainclass="io.github.akadir.muninn.TelegramBot"

echo "telegram.bot.name=$TELEGRAM_BOT_NAME
telegram.token=$TELEGRAM_TOKEN
twitter.consumer.key=$TWITTER_CONSUMER_KEY
twitter.consumer.secret=$TWITTER_CONSUMER_SECRET
muninn.recheck.period=$MUNINN_RECHECK_PERIOD_IN_HOURS
data.source.url=$DATA_SOURCE_URL
data.source.username=$DATA_SOURCE_USERNAME
data.source.password=$DATA_SOURCE_PASSWORD
following.count.limit=$FOLLOWING_COUNT_LIMIT" >> muninn.properties

java -Duser.timezone=$USER_TIMEZONE -cp $classpath $mainclass
