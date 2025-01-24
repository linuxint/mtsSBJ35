
create user 'mts'@'localhost' identified by 'mts';
create user 'mts'@'%' identified by 'mts';
create schema mts default character set utf8;

grant all privileges on mts.* to mts@'%';
grant all privileges on mts.* to mts@'localhost';


flush privileges;


use mysql;
show tables;
select * from user;