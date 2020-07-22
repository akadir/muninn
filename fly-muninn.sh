#!/bin/bash

TWITTER_CONSUMER_KEY=""
TWITTER_CONSUMER_SECRET=""
TELEGRAM_TOKEN=""
TELEGRAM_BOT_NAME=""
MUNINN_RECHECK_PERIOD_IN_HOURS=6
DATA_SOURCE_URL=""
DATA_SOURCE_USERNAME=""
DATA_SOURCE_PASSWORD=""

PROGRAM_TYPE="muninnTelegramBot"
DEPENDENCIES="target/muninn-1.0.jar:target/lib/*"
MAIN_CLASS="io.github.akadir.muninn.TelegramBot"
PID=`ps aux | grep -v grep | grep $PROGRAM_TYPE | awk '{print $2}'`

if [ -z "$PID" ]; then
  cd "${0%/*}"

  echo "telegram.bot.name=$TELEGRAM_BOT_NAME
  telegram.token=$TELEGRAM_TOKEN
  twitter.consumer.key=$TWITTER_CONSUMER_KEY
  twitter.consumer.secret=$TWITTER_CONSUMER_SECRET
  muninn.recheck.period=$MUNINN_RECHECK_PERIOD_IN_HOURS
  data.source.url=$DATA_SOURCE_URL
  data.source.username=$DATA_SOURCE_USERNAME
  data.source.password=$DATA_SOURCE_PASSWORD
  following.count.limit=$FOLLOWING_COUNT_LIMIT" >> muninn.properties

  java -DprogramType=$PROGRAM_TYPE -cp $DEPENDENCIES $MAIN_CLASS
else
  echo "process running with pid:$PID"
  exit 1
fi
exit 0