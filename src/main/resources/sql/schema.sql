CREATE TABLE IF NOT EXISTS MPA_RATINGS
(
    MPA_ID       BIGINT auto_increment PRIMARY KEY,
    MPA_NAME VARCHAR(16) NOT NULL
);

CREATE TABLE IF NOT EXISTS FILMS
(
    FILM_ID BIGINT auto_increment PRIMARY KEY,
    FILM_NAME         VARCHAR(100) NOT NULL,
    DESCRIPTION  VARCHAR(200),
    RELEASE_DATE TIMESTAMP,
    DURATION     INTEGER,
    MPA_ID       BIGINT REFERENCES MPA_RATINGS (MPA_ID) ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS GENRES
(
    GENRE_ID BIGINT auto_increment PRIMARY KEY,
    GENRE_NAME VARCHAR(64) NOT NULL
);

create table if not exists GENRES_FILMS
(
    FILM_ID   BIGINT,
    GENRE_ID  BIGINT,
    constraint PK_GENRE_FILMS
        primary key (FILM_ID, GENRE_ID),
    constraint FK_GENRE_FILMS_FILM
        foreign key (FILM_ID) references FILMS,
    constraint FK_GENRE_FILMS_GENRE
        foreign key (GENRE_ID) references GENRES
);

CREATE TABLE IF NOT EXISTS USERS
(
    USER_ID       BIGINT auto_increment PRIMARY KEY,
    EMAIL    VARCHAR(64) UNIQUE NOT NULL,
    LOGIN    VARCHAR(32) UNIQUE NOT NULL,
    USER_NAME     VARCHAR(255),
    BIRTHDAY TIMESTAMP
);

create table if not exists FRIENDSHIPS
(
    USER_ID   BIGINT not null,
    FRIEND_ID BIGINT not null,
    constraint PK_FRIENDSHIP
        primary key (USER_ID, FRIEND_ID),
    constraint FK_FRIENDSHIP_USER_ID
        foreign key (USER_ID) references USERS,
    constraint FK_FRIENDSHIP_FRIEND_ID
        foreign key (FRIEND_ID) references USERS
);

create table if not exists LIKES_FILMS
(
    FILM_ID BIGINT not null,
    USER_ID BIGINT not null,
    constraint PK_LIKES_FILMS
        primary key (FILM_ID, USER_ID),
    constraint FK_LIKES_FILMS_FILM_ID
        foreign key (FILM_ID) references FILMS,
    constraint FK_LIKES_FILMS_USER_ID
        foreign key (USER_ID) references USERS
);



