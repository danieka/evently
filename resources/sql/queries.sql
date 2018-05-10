-- :name create-event! :insert :raw
-- :doc creates a new events record
INSERT INTO events
(organizer, description, access_key, start_date, start_time, location, title)
VALUES (:organizer, :description, :access-key, :start-date, :start-time, :location, :title)

-- :name update-event! :! :n
-- :doc updates an existing events record
UPDATE events
SET organizer = :organizer, description = :description
WHERE id = :id

-- :name get-all-events :? :*
-- :doc retrieves a events record given the id
SELECT id, organizer, description FROM events

-- :name get-event :? :1
SELECT id, organizer, description, start_date as "start-date", start_time as "start-time", end_date, end_time, location, title
FROM events
WHERE id = :id

-- :name get-event-access-key :? :1
SELECT access_key as "access-key"
FROM events
WHERE id = :id

-- :name delete-event! :! :n
-- :doc deletes a events record given the id
DELETE FROM events
WHERE id = :id

