mariadb -uroot -p261826

create database hddbscan;
create user 'hddbscan'@'%' identified by 'hddbscan';
grant all privileges on  hddbscan.* to 'hddbscan'@'%';
flush privileges;