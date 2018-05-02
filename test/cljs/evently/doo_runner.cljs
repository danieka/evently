(ns evently.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [evently.core-test]))

(doo-tests 'evently.core-test)

