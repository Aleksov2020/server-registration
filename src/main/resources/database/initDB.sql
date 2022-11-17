DROP TABLE IF EXISTS content_list;
DROP TABLE IF EXISTS deleted_messages;
DROP TABLE IF EXISTS deleted_chat_rooms;

DROP TABLE IF EXISTS liked_posts;
DROP TABLE IF EXISTS subscriber_list;
DROP TABLE IF EXISTS comments;

DROP TABLE IF EXISTS posts;
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS content;
DROP TABLE IF EXISTS participiants;
DROP TABLE IF EXISTS chat_rooms;
DROP TABLE IF EXISTS refresh_token;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS media;
DROP TABLE IF EXISTS avatar;

CREATE TABLE IF NOT EXISTS media
(
    id bigserial primary key,
    name varchar(254) not null,
    type int not null,
    size int,
    path varchar(254)
);

CREATE TABLE IF NOT EXISTS avatar
(
    id bigserial primary key,
    size int,
    path varchar(254)
);
create table if not exists users
(
    id bigserial primary key not null,
    avatar_id bigserial,
    date_birth date,
    user_phone varchar(11) not null,
    user_name varchar(100) not null,
    first_name varchar(100),
    middle_name varchar(100),
    last_name varchar(100),
    subscribers_counter int,
    subscribed_counter int,
    posts_counter int,
    date_last_enter timestamp not null,
    date_last_update timestamp not null,
    date_create timestamp not null,
    is_active boolean,
    is_reported boolean,
    is_banned boolean,

    constraint fk_avatar
    foreign key (avatar_id)
    references avatar(id)
    ON DELETE SET NULL
);

create table if not exists chat_rooms
(
    id bigserial primary key not null,
    chat_name varchar(254),
    media_id bigserial,
    chat_type int not null,
    date_start timestamp not null,
    date_last_update timestamp not null,

    CONSTRAINT fk_img
    FOREIGN KEY(media_id)
    REFERENCES media(id)
    ON DELETE SET NULL
);

create table if not exists participiants
(
    id bigserial primary key,
    chat_room_id bigserial,
    user_id bigserial,

    constraint fk_chat_room
    foreign key (chat_room_id)
    references chat_rooms(id)
    ON DELETE SET NULL,

    constraint fk_users
    foreign key (user_id)
    references users(id)
    ON DELETE SET NULL
);

create table if not exists content
(
    id bigserial primary key,
    content varchar(2540)
);

create table if not exists content_list
(
    id bigserial primary key,
    content_id bigserial,
    media_id bigserial,

    constraint fk_content
    foreign key (content_id)
    references content(id)
    ON DELETE SET NULL,

    constraint fk_media
    foreign key (media_id)
    references media(id)
    ON DELETE SET NULL
);


create table if not exists messages
(
    id bigserial primary key,
    sender_id bigserial,
    chat_id bigserial,
    content_id bigserial,
    media_id int,
    forward_id int,
    date_create timestamp not null,

    CONSTRAINT fk_sender
    FOREIGN KEY(sender_id)
    REFERENCES users(id)
    ON DELETE SET NULL,

    CONSTRAINT fk_chat
    FOREIGN KEY(chat_id)
    REFERENCES chat_rooms(id)
    ON DELETE SET NULL,

    CONSTRAINT fk_content
    FOREIGN KEY(content_id)
    REFERENCES content(id)
    ON DELETE SET NULL,

    CONSTRAINT fk_forward
    FOREIGN KEY(forward_id)
    REFERENCES messages(id)
    ON DELETE SET NULL,

    CONSTRAINT fk_media
    FOREIGN KEY(media_id)
    REFERENCES media(id)
    ON DELETE SET NULL
);



create table if not exists deleted_messages
(
    id bigserial primary key,
    message_id bigserial,
    user_id bigserial,

    constraint fk_message
    foreign key (message_id)
    references messages(id)
    ON DELETE SET NULL,

    constraint fk_users
    foreign key (user_id)
    references users(id)
    ON DELETE SET NULL
);

create table if not exists deleted_chat_rooms
(
    id bigserial primary key,
    chat_room_id bigserial,
    user_id bigserial,

    constraint fk_chat_room
    foreign key (chat_room_id)
    references chat_rooms(id)
    ON DELETE NO ACTION,

    constraint fk_users
    foreign key (user_id)
    references users(id)
    ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS refresh_token
(
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGSERIAL,
    token           VARCHAR(254)    NOT NULL,
    expiry_date     VARCHAR(254)    NOT NULL,

    CONSTRAINT fk_user_id
        FOREIGN KEY(user_id)
            REFERENCES users(id)
            ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS posts
(
    id              BIGSERIAL       PRIMARY KEY,
    owner_id        BIGSERIAL,
    content_id      bigint,
    media_id        bigint,
    like_counter    int,
    comment_counter int,
    date_create     timestamp            NOT NULL,

    CONSTRAINT fk_user_id
    FOREIGN KEY(owner_id)
    REFERENCES users(id)
    ON DELETE SET NULL,

    CONSTRAINT fk_content_id
    FOREIGN KEY(content_id)
    REFERENCES content(id)
    ON DELETE SET NULL,

    CONSTRAINT fk_media_id
    FOREIGN KEY(media_id)
    REFERENCES media(id)
    ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS liked_posts
(
    id          bigserial  PRIMARY KEY,
    post_id     bigint,
    user_id     bigint,

    CONSTRAINT fk_posts
    FOREIGN KEY(post_id)
    REFERENCES posts(id)
    ON DELETE SET NULL,

    CONSTRAINT fk_users
    FOREIGN KEY(user_id)
    REFERENCES users(id)
    ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS subscriber_list
(
    id          bigserial  PRIMARY KEY,
    subscriber_id     bigint,
    user_id     bigint,

    CONSTRAINT fk_subscriber
    FOREIGN KEY(subscriber_id)
    REFERENCES users(id)
    ON DELETE SET NULL,

    CONSTRAINT fk_users
    FOREIGN KEY(user_id)
    REFERENCES users(id)
    ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS comments
(
    id          bigserial  PRIMARY KEY,
    owner_id    bigint,
    post_id     bigint,
    content_id  bigint,
    forward_id  bigint,
    like_count  bigint,
    date_create timestamp,
    date_update timestamp,

    CONSTRAINT fk_users
    FOREIGN KEY(owner_id)
    REFERENCES users(id)
    ON DELETE SET NULL,

    CONSTRAINT fk_posts
    FOREIGN KEY(post_id)
    REFERENCES posts(id)
    ON DELETE SET NULL,

    CONSTRAINT fk_content
    FOREIGN KEY(content_id)
    REFERENCES content(id)
    ON DELETE SET NULL,

    CONSTRAINT fk_forward
    FOREIGN KEY(forward_id)
    REFERENCES comments(id)
    ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS likedComments
(
    id          bigserial  PRIMARY KEY,
    comment_id     bigint,
    user_id     bigint,

    CONSTRAINT fk_comment
    FOREIGN KEY(comment_id)
    REFERENCES comments(id)
    ON DELETE SET NULL,

    CONSTRAINT fk_user
    FOREIGN KEY(user_id)
    REFERENCES users(id)
    ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS posts
(
    id              BIGSERIAL       PRIMARY KEY,
    owner_id        BIGSERIAL,
    content_id      int,
    media_id        int,
    like_counter    int,
    date_create     timestamp            NOT NULL,

    CONSTRAINT fk_user_id
    FOREIGN KEY(owner_id)
    REFERENCES users(id)
    ON DELETE SET NULL,

    CONSTRAINT fk_content_id
    FOREIGN KEY(content_id)
    REFERENCES content(id)
    ON DELETE SET NULL,

    CONSTRAINT fk_media_id
    FOREIGN KEY(media_id)
    REFERENCES media(id)
    ON DELETE SET NULL
);


