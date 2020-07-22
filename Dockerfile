FROM openjdk:8-jre-alpine3.9

LABEL repository="https://github.com/akadir/muninn" maintainer="https://github.com/akadir"

WORKDIR /muninn

COPY ["./target", ".docker/entrypoint.sh", "./"]

RUN chmod +x entrypoint.sh

ENV TWITTER_CONSUMER_KEY=""  \
    TWITTER_CONSUMER_SECRET="" \
    TELEGRAM_TOKEN=""  \
    TELEGRAM_BOT_NAME="" \
    MUNINN_RECHECK_PERIOD_IN_HOURS=6 \
    DATA_SOURCE_URL=""  \
    DATA_SOURCE_USERNAME=""  \
    DATA_SOURCE_PASSWORD="" \
    FOLLOWING_COUNT_LIMIT=0 \
    USER_TIMEZONE=UTC

ENTRYPOINT ["sh", "/muninn/entrypoint.sh"]
