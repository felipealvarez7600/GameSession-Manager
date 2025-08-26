drop table if exists player_invites;
drop table if exists games_genres;
drop table if exists genres;
drop table if exists players_tokens;
drop table if exists players_sessions;
drop table if exists players;
drop table if exists sessions;
drop table if exists games;


create table if not exists games (
    id serial primary key,
    name varchar(255) unique not null ,
    developer varchar(255) not null
);

create table if not exists sessions (
    id serial primary key,
    gameId int not null,
    capacity integer not null,
    date timestamp not null,
    state varchar(255) not null,
    foreign key (gameId) references games(id)
);

create table if not exists players (
    id serial primary key,
    name varchar(255) not null unique,
    email varchar(255) not null unique,
    details varchar(255) null,
    image bytea null,
    password varchar(255) not null
);

create table if not exists players_tokens(
    token_validation VARCHAR(256) primary key,
    player_id int not null,
    last_used_at timestamp not null,
    foreign key (player_id) references players(id)
);

create table if not exists players_sessions (
    player_id integer not null,
    session_id integer not null,
    foreign key (player_id) references players(id),
    foreign key (session_id) references sessions(id)
);

create table if not exists genres (
    id serial primary key,
    name varchar(255) not null unique
);

create table if not exists games_genres (
    game_id integer not null,
    genre_id integer not null,
    foreign key (game_id) references games(id),
    foreign key (genre_id) references genres(id)
);

create table if not exists player_invites (
    from_player_id integer not null,
    to_player_id integer not null,
    session_id integer not null,
    invite_expiration timestamp not null,
    foreign key (from_player_id) references players(id),
    foreign key (to_player_id) references players(id),
    foreign key (session_id) references sessions(id)
);

