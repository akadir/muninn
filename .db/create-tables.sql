create database muninn;

CREATE TABLE "authenticated_user" (
    "id" SERIAL PRIMARY KEY,
    "twitter_user_id" bigint,
    "twitter_token" varchar,
    "twitter_token_secret" varchar,
    "twitter_request_token" varchar,
    "twitter_request_token_secret" varchar,
    "telegram_user_id" bigint,
    "telegram_chat_id" varchar,
    "last_notified_time" timestamp,
    "last_checked_time" timestamp,
    "bot_status" int,
    "created_at" timestamp,
    "updated_at" timestamp,
    "version" int
);

CREATE TABLE "user_friend" (
    "id" SERIAL PRIMARY KEY,
    "follower_id" bigint,
    "friend_id" bigint,
    "created_at" timestamp,
    "updated_at" timestamp,
    "version" int
);

CREATE TABLE "friend" (
    "id" SERIAL PRIMARY KEY,
    "twitter_user_id" bigint,
    "username" varchar,
    "name" varchar,
    "bio" varchar,
    "profile_pic_url" varchar,
    "account_state" int,
    "last_checked" timestamp,
    "thread_availability" int,
    "thread_id" varchar,
    "check_start_time" timestamp,
    "created_at" timestamp,
    "updated_at" timestamp,
    "version" int
);

CREATE TABLE "change_set" (
    "id" SERIAL PRIMARY KEY,
    "friend_id" bigint,
    "change_type" int,
    "old_data" varchar,
    "new_data" varchar,
    "created_at" timestamp,
    "updated_at" timestamp,
    "version" int
);

ALTER TABLE "user_friend" ADD FOREIGN KEY ("follower_id") REFERENCES "authenticated_user" ("id");

ALTER TABLE "user_friend" ADD FOREIGN KEY ("friend_id") REFERENCES "friend" ("id");

ALTER TABLE "change_set" ADD FOREIGN KEY ("friend_id") REFERENCES "friend" ("id");

CREATE UNIQUE INDEX ON "authenticated_user" ("id");

CREATE INDEX ON "authenticated_user" ("twitter_user_id");

CREATE UNIQUE INDEX ON "user_friend" ("id");

CREATE UNIQUE INDEX ON "user_friend" ("follower_id", "friend_id");

CREATE UNIQUE INDEX ON "friend" ("id");

CREATE UNIQUE INDEX ON "friend" ("twitter_user_id");

CREATE INDEX "ix_friend_id" ON "change_set" ("friend_id");

CREATE UNIQUE INDEX ON "change_set" ("id");
