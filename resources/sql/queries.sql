-- :name create-event! :insert :raw
-- :doc creates a new events record
INSERT INTO events
(organizer, description)
VALUES (:organizer, :description)

-- :name update-event! :! :n
-- :doc updates an existing events record
UPDATE events
SET organizer = :organizer, description = :description
WHERE id = :id

-- :name get-all-events :? :*
-- :doc retrieves a events record given the id
SELECT * FROM events

-- :name get-event :? :1
SELECT * FROM events
WHERE id = :id

-- :name delete-event! :! :n
-- :doc deletes a events record given the id
DELETE FROM events
WHERE id = :id

