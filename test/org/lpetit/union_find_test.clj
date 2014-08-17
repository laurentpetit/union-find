(ns org.lpetit.union-find-test
  (:refer-clojure :exclude [find])
  (:use org.lpetit.union-find)
  (:require [clojure.java.io :as io])
  (:import [edu.princeton.cs.introcs StdIn StdOut]))

(defn connected? [uf p q]
  (= (find uf p) (find uf q)))

(defn -main []
  (let [N (StdIn/readLong)
        uf (into (union-find) (range N))]
    (loop [uf uf]
      (if (StdIn/isEmpty)
        (StdOut/println (str (count uf) " components"))
        (let [p (StdIn/readLong)
              q (StdIn/readLong)]
          (if (connected? uf p q)
            (recur uf)
            (do 
              (StdOut/println (str p " " q))
              (recur (union uf p q)))))))))

