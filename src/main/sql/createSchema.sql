drop table if exists students;
drop table if exists courses;

create table if not exists courses (
  cid serial primary key,
  name varchar(80)
);

create table if not exists students (
  number int primary key,
  name varchar(80),
  course int references courses(cid)
);


