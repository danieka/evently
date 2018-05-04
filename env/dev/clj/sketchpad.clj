(use 'evently.db.core)

(get-all-events)

(get-event {:id 23})

(last (last (create-event! {:organizer "Humma" :description "Mumma"})))

((keyword ":scope_identity()") create-event! {:organizer "Humma" :description "Mumma"})