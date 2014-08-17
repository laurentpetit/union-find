(defproject org.lpetit/union-find "0.1.0-SNAPSHOT"
  :description "Implementation of weighted quick-union algorithm with path compression"
  ;:url ""
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [edu.princeton.cs.introcs/stdlib-package "1.0"]
                 #_[criterium "0.4.3"]]
  ;; maven repository https://github.com/slok/algs4-mvn-repo
  :repositories [["org.coursera.algs4"
                    {:url "https://raw.github.com/slok/algs4-mvn-repo/master"}]]  
  :main org.lpetit.union-find-test)
