--user_friend table
select concat('insert into user_friend(id, follower_id, friend_id, created_at, updated_at, version) values(', id, ', ',
    follower_id, ', ', friend_id, ', ''', created_at, ''', null, 0);') from user_friend order by id asc;
--friend table
select concat('insert into friend(id, twitter_user_id, username, name, bio, profile_pic_url, account_state, last_checked, thread_availability, thread_id, check_start_time, created_at, updated_at, version) values(',
    id, ', ', twitter_user_id, ', ''', replace(username, '''', ''''''), ''', ''', replace(name, '''', ''''''), ''', ''',
    replace(bio,'''', ''''''), ''', ''', profile_pic_url, ''', ', account_state, ', ''', last_checked, ''', ',
    thread_availability, ', ''', thread_id, ''', ''', check_start_time, ''', ''', created_at, ''', ''', updated_at, ''', ', version,');')
from friend order by id asc;
--change_set table
select concat('insert into change_set(id, friend_id, change_type, old_data, new_data, created_at, updated_at, version) values(',
    id, ', ', friend_id, ', ', change_type, ', ''', replace(old_data, '''', ''''''), ''', ''', replace(new_data, '''', ''''''),
    ''', ''', created_at, ''', null, 0);') from change_set;