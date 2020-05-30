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
DEPENDENCIES="muninn-1.0.jar:lib/*"
MAIN_CLASS="io.github.akadir.muninn.TelegramBot"
PID=`ps aux | grep -v grep | grep $PROGRAM_TYPE | awk '{print $2}'`

if [ -z "$PID" ]; then
  cd "${0%/*}"
  java -Dtwitter.consumer.key=$TWITTER_CONSUMER_KEY -Dtwitter.consumer.secret=$TWITTER_CONSUMER_SECRET \
       -Dtelegram.token=$TELEGRAM_TOKEN -Dtelegram.bot.name=$TELEGRAM_BOT_NAME \
       -Dmuninn.recheck.period=$MUNINN_RECHECK_PERIOD_IN_HOURS \
       -Ddata.source.url=$DATA_SOURCE_URL -Ddata.source.username=$DATA_SOURCE_USERNAME -Ddata.source.password=$DATA_SOURCE_PASSWORD \
       -DprogramType=$PROGRAM_TYPE -cp $DEPENDENCIES $MAIN_CLASS
else
  echo "process running with pid:$PID"
  exit 1
fi
exit 0