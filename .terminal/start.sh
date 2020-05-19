#!/bin/bash
PROGRAM_TYPE="muninnTelegramBot"
DEPENDENCIES="muninn-1.0.jar:lib/*"
MAIN_CLASS="io.github.akadir.muninn.TelegramBot"
PIDS=`ps aux | grep -v grep | grep $PROGRAM_TYPE | awk '{print $2}'`
if [ -z "$PIDS" ]; then
  cd "${0%/*}"
  java -DtwitterConsumerKey="" -DtwitterConsumerSecret="" \
       -DtelegramToken="" -DtelegramBotName="" \
       -DreCheckPeriod=1 \
       -DDATA_SOURCE_URL="" -DDATA_SOURCE_USERNAME="" -DDATA_SOURCE_PASSWORD="" \
       -Duser.timezone=GMT+3 -DprogramType=$PROGRAM_TYPE -cp $DEPENDENCIES $MAIN_CLASS
else
  echo "process running with pid:$PIDS"
  exit 1
fi
exit 0