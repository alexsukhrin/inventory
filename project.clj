(defproject inventory "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :resource-paths ["resources" "target/cljsbuild"]
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [http-kit "2.7.0"]
                 [ring/ring-core "1.12.0"]
                 [ring/ring-json "0.5.1"]
                 [ring/ring-defaults "0.4.0"]
                 [compojure/compojure "1.7.1"]
                 [com.github.seancorfield/next.jdbc "1.3.909"]
                 [hikari-cp "3.0.1"]
                 [conman/conman "0.9.6"]
                 [org.postgresql/postgresql "42.7.2"]
                 [com.xtdb/xtdb-jdbc "1.24.0"]
                 [mount "0.1.18"]
                 [migratus "1.6.3"]
                 [org.clojure/tools.logging "1.3.0"]
                 [com.cognitect.aws/api "0.8.686"]
                 [com.cognitect.aws/endpoints "1.1.12.504"]
                 [com.cognitect.aws/s3 "868.2.1580.0"]
                 [org.clojure/tools.logging "1.3.0"]
                 [ch.qos.logback/logback-classic "1.4.11"]
                 [hiccup "2.0.0-RC4"]
                 [cheshire "5.13.0"]
                 [clojure.java-time "1.4.3"]
                 [org.clojure/data.csv "1.1.0"]]
  :plugins [[migratus-lein "0.7.3"]]
  :main ^:skip-aot inventory.core
  :target-path "target/%s"
  :migratus {:store :database
             :migration-dir "migrations"
             :db {:dbtype "postgresql"
                  :dbname ~(System/getenv "POSTGRES_DB")
                  :host ~(System/getenv "POSTGRES_HOST")
                  :user ~(System/getenv "POSTGRES_USER")
                  :password ~(System/getenv "POSTGRES_PASSWORD")}}
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"
                                  "-Dclojure.tools.logging.factory=clojure.tools.logging.impl/slf4j-factory"]}})
