◎ OLLAMA
set OLLAMA_HOST=0.0.0.0:11434

echo %OLLAMA_HOST%

ollama pull gemma4:e2b

ollama run gemma4:e2b

ollama pull embeddinggemma:latest

◎ MariaDB
MariaDB [(none)]> create database ollama;
Query OK, 1 row affected (0.002 sec)

MariaDB [(none)]> create user 'ollama'@'%' identified by 'ollama';
Query OK, 0 rows affected (0.011 sec)

MariaDB [(none)]> grant all privileges on ollama.* to 'ollama'@'%';
Query OK, 0 rows affected (0.011 sec)

MariaDB [(none)]> show databases;
+--------------------+
| Database           |
+--------------------+
| demo               |
| edmc               |
| hddbscan           |
| hr                 |
| information_schema |
| mysql              |
| ollama             |
| performance_schema |
| sys                |
| timetabling        |
+--------------------+
10 rows in set (0.001 sec)


