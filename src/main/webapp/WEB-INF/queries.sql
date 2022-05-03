INSERT INTO meetings(title, meeting_date, meeting_time, duration, max_participants) VALUES("Riunione delli servitori", current_date(), current_time(), current_time(), 20);
SELECT * FROM meetings;
SELECT* FROM users;
INSERT INTO hostings(host_user_id, meeting_id) VALUES(1, 1);
SELECT* FROM hostings;
INSERT INTO attendances(attendee, meeting_id) VALUES(7, 1);
INSERT INTO attendances(attendee, meeting_id) VALUES(8, 1);
SELECT* FROM attendances;