CREATE TABLE events
(id INTEGER PRIMARY KEY AUTO_INCREMENT,
 organizer text,
 description text,
 access_key text,
 start_date text,
 start_time text,
 end_date text,
 end_time text,
 location text,
 created time,
 title text);

CREATE TABLE PARTICIPATIONS
(id BIGINT PRIMARY KEY AUTO_INCREMENT,
 event INTEGER REFERENCES Events(id),
 status TEXT,
 name TEXT);