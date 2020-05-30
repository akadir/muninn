<h1 align="center">Muninn</h1>

<div align="center">
  Bot that notifies you about the changes your twitter followings made.
</div>

<br />

<div align="center">
  <!-- CI -->
  <a href="https://github.com/akadir/muninn/workflows/CI/badge.svg">
    <img src="https://github.com/akadir/muninn/workflows/CI/badge.svg"
      alt="CI" />
  </a>
  <!-- Quality Gate Status -->
  <a href="https://sonarcloud.io/dashboard?id=akadir_muninn">
    <img src="https://sonarcloud.io/api/project_badges/measure?project=akadir_muninn&metric=alert_status"
      alt="Quality Gate Status" />
  </a>
  <!-- Technical Dept -->
  <a href="https://sonarcloud.io/dashboard?id=akadir_muninn">
    <img src="https://sonarcloud.io/api/project_badges/measure?project=akadir_muninn&metric=sqale_index"
      alt="Technical Dept" />
  </a>
  <!-- License -->
  <a href="https://img.shields.io/badge/License-MIT-blue.svg">
    <img src="https://img.shields.io/badge/License-MIT-blue.svg"
      alt="License" />
  </a>
</div>

## Usage

You can run Muninn as jar file or docker container. In both options, you will need the access tokens for both telegram and twitter bots.

You can create `telegram bot` using [bot father](https://telegram.me/botfather).
You will get an `access token` after you created the bot. This token will be used by `Muninn` to send `notifications.

Another requirement is `Twitter tokens`. You can get your `tokens` from [twitter developers website](https://developer.twitter.com/). 
`Read permissions` would be ok for your `Twitter App` as `Muninn will not need any write operations regarding the authenticated twitter account. 

### Variable definitions

```
# Token that you can get by creating twitter app
TWITTER_CONSUMER_KEY=
# Token that you can get by creating twitter app
TWITTER_CONSUMER_SECRET=
# Token of your telegram bot
TELEGRAM_TOKEN=
# Name of your telegram bot
TELEGRAM_BOT_NAME=
# Delay between checks of changes in hours. 
# 6 means Muninn will check your followings once for every 6 hours.
MUNINN_RECHECK_PERIOD_IN_HOURS=6
# Url of your postgres database
DATA_SOURCE_URL=""
# postgres username
DATA_SOURCE_USERNAME=""
# postgres password
DATA_SOURCE_PASSWORD=""
```

### 1. Run Application as jar file

- Initialise postgresql database using [init-database.sh](.docker/init-database.sh) or [create-tables.sql](.db/create-tables.sql).
- Build jar file and related dependencies by `mvn package`
- Update variables in [fly-muninn.sh](fly-muninn.sh#L3-L10)
- Make this script file executable by `chmod +x fly-muninn.sh`
- Run application in background by `/fly-muninn.sh > /dev/null 2>&1 &`
- Check logs to see if application running correctly: `tail -f log/muninn.log`

### 2. Run Docker Container

- Update variables in [docker-compose.yml](.docker/docker-compose.yml#L7-L11)
- Create directories to mount log and database directories: 
    ```shell script
    mkdir muninn
    mkdir muninn/log
    mkdir muninn/postgres-data
    ``` 
- Run `docker-compose up -d`
- Check log files if application running correctly: `tail -f log/muninn.log`
- ps: application may restarts couple of times in the first time because initialisation of the database takes some time.


### Starting Bot for Your Account

After you make your Muninn up and running, you can start using your Muninn by opening your telegram app and sending your bot to a `\login` command. 

It will generate a link to authenticate your twitter account and send back to you. 

Using that link you can authenticate your account and then start to get messages related to changes your followings made.

You can also use `\logout` command to disable notifications and `\help` command to see help.

## Built With

* [TelegramBots](https://github.com/rubenlagus/TelegramBots) - Java Library for Telegram Bots
* [Twitter4J](https://github.com/Twitter4J/Twitter4J) - Java Library for Twitter API
* [rate-limit-handler](https://github.com/akadir/rate-limit-handler) - Simple utility package to handle Twitter API Rate Limits
* [Spring Framework](https://github.com/spring-projects/spring-framework) - Dependency Injection, Data Access, Task Scheduling 
* [Project Lombok](https://projectlombok.org/) - Java Library for generating of getter, setters
* [diff-match-patch](https://github.com/google/diff-match-patch) - Generate diffs between two text
* [java-diff-utils](https://github.com/java-diff-utils/java-diff-utils) - Another library to generate diffs between two text
* [apache-commons](https://commons.apache.org/) - Generate JSON style toString methods
* [Logback](http://logback.qos.ch/) - Logging frameworks for java
* [Maven](https://maven.apache.org/) - Dependency Management
* [Docker](https://www.docker.com/) - Containerisation of Application

## Authors


<div align="center">
  Developed with ❤︎ by <a href="https://github.com/akadir">akadir</a>
</div>

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details