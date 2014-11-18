(defproject v3-migration "1.0.0-SNAPSHOT"
  :description "v3-migration"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2156"]]

  :plugins [[lein-cljsbuild "1.0.1"]
            [lein-pprint "1.1.1"]
            [speclj "3.1.0"]]

  :test-path "spec/"

  :profiles {:dev {:dependencies [[speclj "3.1.0"]]}}

  :hooks [leiningen.cljsbuild]

  :cljsbuild {:builds        {:dev  {:source-paths   ["src/cljs" "spec/cljs"]
                                     :compiler       {:output-to "target/main/compiled.js"}
                                     :notify-command ["node_modules/phantomjs/lib/phantom/bin/phantomjs" "bin/speclj" "target/main/compiled.js"]}
                              :prod {:source-paths ["src/cljs"]
                                     :compiler     {:output-to     "target/matin/prod.js"
                                                    :optimizations :simple}}}
              :test-commands {"test" ["node_modules/phantomjs/lib/phantom/bin/phantomjs" "bin/speclj" "target/main/compiled.js"]}}
  )
