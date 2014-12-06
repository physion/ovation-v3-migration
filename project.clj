(defproject v3-migration "1.0.0-SNAPSHOT"
            :description "v3-migration"
            :dependencies [[org.clojure/clojure "1.5.1"]
                           [org.clojure/clojurescript "0.0-2197"]]

            :plugins [[lein-cljsbuild "1.0.3"]
                      [lein-pprint "1.1.1"]
                      [speclj "3.1.0"]]

            :test-path "spec/"

            :profiles {:dev {:dependencies [[speclj "3.1.0"]]}}

            :hooks [leiningen.cljsbuild]

            :cljsbuild {:builds        {:dev      {:source-paths   ["src/cljs" "spec/cljs"]
                                                   :compiler       {:output-to    "target/main/migration-dev.js"
                                                                    :pretty-print true}
                                                   :notify-command ["node_modules/phantomjs/lib/phantom/bin/phantomjs" "bin/speclj" "target/main/migration-dev.js"]}
                                        :prod     {:source-paths ["src/cljs"]
                                                   :compiler     {:output-to     "target/main/migration.js"
                                                                  :optimizations :simple
                                                                  :pretty-print  true}}
                                        :node-dev {:source-paths   ["src/cljs" "spec/cljs" "nodejs/cljs"]
                                                   :compiler       {:output-to     "target/main/migration-node-dev.js"
                                                                    :optimizations :simple
                                                                    :target        :nodejs}
                                                   :notify-command ["node_modules/phantomjs/lib/phantom/bin/phantomjs" "bin/speclj" "target/main/migration-node-dev.js"]}
                                        :node     {:source-paths ["src/cljs" "nodejs/cljs"]
                                                   :compiler     {:output-to     "target/main/migration-node.js"
                                                                  :output-dir       "target/main"
                                                                  :optimizations :simple
                                                                  :target        :nodejs
                                                                  :pretty-print  true
                                                                  :source-map    "target/main/migration-node.js.map"}}}

                        :test-commands {"test" ["node_modules/phantomjs/lib/phantom/bin/phantomjs" "bin/speclj" "target/main/compiled.js"]}}
            )



