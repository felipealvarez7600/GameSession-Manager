insert into players(name, email, details, image, password) values('Antonio', 'antonio@gmail.com', null, null, 'ADMIN');
insert into players(name, email, details, image, password) values('Player1', 'Player1@gmail.com', null, null, '1234');
insert into players(name, email, details, image, password) values('Player2', 'Player2@gmail.com', null, null, '1234');
insert into players(name, email, details, image, password) values('Felipe', 'felipe@gmail.com', null, null, 'Destiny');
insert into players(name, email, details, image, password) values('Nuno', 'nuno@gmail.com', null, null, 'CS');
insert into players(name, email, details, image, password) values('Tomas', 'tomas@gmail.com', null, null, 'Destiny');


insert into genres(name) values('Shooter');
insert into genres(name) values('RPG');
insert into genres(name) values('Adventure');
insert into genres(name) values('Action');
insert into genres(name) values('Strategy');
insert into genres(name) values('Fist Person');
insert into genres(name) values('Third Person');

insert into games(name, developer) values('Call of Duty', 'Infinity Ward');
insert into games(name, developer) values('The Witcher 3', 'CD Projekt Red');
insert into games(name, developer) values('The Legend of Zelda: Breath of the Wild', 'Nintendo');
insert into games(name, developer) values('Game2', 'CD Projekt Red');
insert into games(name, developer) values('Game3', 'CD Projekt Red');


insert into games_genres(game_id, genre_id) values(1, 1);
insert into games_genres(game_id, genre_id) values(1, 4);
insert into games_genres(game_id, genre_id) values(1, 6);
insert into games_genres(game_id, genre_id) values(2, 2);
insert into games_genres(game_id, genre_id) values(4, 1);
insert into games_genres(game_id, genre_id) values(5, 1);
insert into games_genres(game_id, genre_id) values(2, 3);
insert into games_genres(game_id, genre_id) values(4, 3);
insert into games_genres(game_id, genre_id) values(3, 3);

insert into sessions(id, gameId, capacity, date, state) values(1, 1, 10, TIMESTAMP'2024-07-16 10:00:00', 'OPEN');



