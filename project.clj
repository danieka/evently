(defproject evently "0.1.0-SNAPSHOT"

  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :dependencies [[clj-time "0.14.3"]
                 [com.h2database/h2 "1.4.196"]
                 [compojure "1.6.1"]
                 [conman "0.7.8"]
                 [cprop "0.1.11"]
                 [funcool/struct "1.2.0"]
                 [luminus-immutant "0.2.4"]
                 [luminus-migrations "0.5.0"]
                 [luminus-nrepl "0.1.4"]
                 [luminus/ring-ttl-session "0.3.2"]
                 [markdown-clj "1.0.2"]
                 [metosin/muuntaja "0.5.0"]
                 [metosin/ring-http-response "0.9.0"]
                 [mount "0.1.12"]
                 [org.clojure/clojure "1.9.0"]
                 [org.clojure/tools.cli "0.3.6"]
                 [org.clojure/tools.logging "0.4.0"]
                 [org.webjars.bower/tether "1.4.3"]
                 [org.webjars/bootstrap "4.1.0"]
                 [org.webjars/font-awesome "5.0.10"]
                 [org.webjars/jquery "3.2.1"]
                 [ring-webjars "0.2.0"]
                 [ring/ring-core "1.6.3"]
                 [ring/ring-defaults "0.3.1"]
                 [selmer "1.11.7"]
                 [amalloy/ring-gzip-middleware "0.1.3"]
                 [lein-cljsbuild "1.1.3"]]

  :min-lein-version "2.0.0"
  
  :source-paths ["src/clj"]
  :test-paths ["test/clj"]
  :resource-paths ["resources" "target/cljsbuild"]
  :target-path "target/%s/"
  :main ^:skip-aot evently.core

  :plugins [[lein-immutant "2.1.0"]
            [lein-cljsbuild "1.1.1"]]

  :profiles
  {:uberjar {:omit-source true
             :aot :all
             :uberjar-name "evently.jar"
             :source-paths ["env/prod/clj"]
             :resource-paths ["env/prod/resources"]}

   :dev           [:project/dev :profiles/dev]
   :test          [:project/dev :project/test :profiles/test]

   :project/dev  {:jvm-opts ["-Dconf=dev-config.edn"]
                  :dependencies [[expound "0.5.0"]
                                 [pjstadig/humane-test-output "0.8.3"]
                                 [prone "1.5.2"]
                                 [ring/ring-devel "1.6.3"]
                                 [ring/ring-mock "0.3.2"]]
                  :plugins      [[com.jakemccrary/lein-test-refresh "0.19.0"]]
                  
                  :source-paths ["env/dev/clj"]
                  :resource-paths ["env/dev/resources"]
                  :repl-options {:init-ns user}
                  :injections [(require 'pjstadig.humane-test-output)
                               (pjstadig.humane-test-output/activate!)]}
   :project/test {:jvm-opts ["-Dconf=test-config.edn"]
                  :resource-paths ["env/test/resources"]}
   :profiles/dev {}
   :profiles/test {}}

  :cljsbuild
    {:builds
      {:app
        {:source-paths ["src/cljs"]
        :compiler
                      {:main          (str project-ns ".app")
                        :asset-path    "/js/out"
                        :output-to     "target/cljsbuild/public/js/app.js"
                        :output-dir    "target/cljsbuild/public/js/out"
                        :optimizations :none
                        :source-map    true
                        :pretty-print  true}}
        :min
        {:source-paths ["src/cljs"]
        :compiler
                      {:output-to     "target/cljsbuild/public/js/app.js"
                        :output-dir    "target/uberjar"
                        :externs       ["react/externs/react.js"]
                        :optimizations :advanced
                        :pretty-print  false}}}})