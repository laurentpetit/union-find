(ns org.lpetit.union-find
  (:refer-clojure :exclude [find]))

;; First implementation: only what the algorithms text books generally provide:
;; - no path compression yet
;; - equality is defined in terms of site-props map (so it's really strict,
;;   e.g. it will not consider equal 2 union-find ds created differently, even
;;   if they represent the same disjoint sets)

(defprotocol IUnionFind
  (union [this site1 site2])
  (find  [this site])
  (count-sites [this]))

(defrecord SiteProps [parent-site size])

;; count is a derived value, so that count-sites is a constant time operation 
(deftype UnionFind [site-props count]

  IUnionFind
  (find [this site]
    (when (contains? site-props site)
      (loop [site site]
       (if-let [ps (-> site site-props :parent-site)]
         (recur ps)
         site))))
  
  ;; pre-requisite: site1 and site2 are already in the ds
  (union [this site1 site2]
    (assert (contains? site-props site1) (format "argument site1 not found: %s" site1))
    (assert (contains? site-props site2) (format "argument site2 not found: %s" site2))
    (let [root1 (find this site1)
          root2 (find this site2)]
      (if (= root1 root2)
        this
        (let [smaller-root (if (< (-> root1 site-props :size)
                                 (-> root2 site-props :size))
                             root1 root2)
              larger-root (if (= smaller-root root1) root2 root1)
              union-site-props (-> site-props
                                 (assoc-in [smaller-root :parent-site] larger-root)
                                 (update-in [larger-root :size] + (-> smaller-root site-props :size)))]
          (UnionFind. union-site-props (dec count))))))
  
  (count-sites [this] (clojure.core/count site-props))
  
  java.lang.Object
  ;; in this version, toString just dumps the map-reified tree of site->parent-site entries
  (toString [this] (.toString (reduce-kv #(assoc %1 %2 (:parent-site %3)) {} site-props)))
  
  ;; use only site-props: count is a derived value
  (hashCode [this] (.hashCode site-props))
  
  ;; equality is too strict right now: same disjoint sets may not be equal if they haven't been
  ;; created with the same sequence of operations
  (equals [this that] (or (identical? this that) (.equals site-props (.site-props that))))  
  
  clojure.lang.Seqable
  ;; seq returns each of the canonical elements, not all of the elements
  (seq [this] (keep #(when-not (:parent-site (val %)) (key %)) site-props))

  ;; this adds support for cons, conj, into  
  clojure.lang.IPersistentCollection
  ;; cons adds the input to a new singleton set
  (cons [this site]
    (if (contains? site-props site)
      this
      (UnionFind. (assoc site-props site (->SiteProps nil 1)) (inc count))))
  (empty [this] (UnionFind. {} 0))
  (equiv [this that] (.equals this that))
  
  ;; count returns the number of components, not the number of sites
  clojure.lang.Counted
  (count [this] count)
  
  clojure.lang.ILookup
  (valAt [this k] (.valAt this k nil))
  (valAt [this k not-found] (or (find this k) not-found))

  clojure.lang.IFn
  ;; invoking as function behaves like valAt.
  (invoke [this k] (.valAt this k))
  (invoke [this k not-found] (.valAt this k not-found)))

(defn union-find 
  [& sites]
  (let [count (count sites)
        site-props (reduce #(assoc %1 %2 (->SiteProps nil 1)) {} sites)]
    (->UnionFind site-props count)))

(comment
  
=> (union-find)
;; #<UnionFind {}>

=> (union-find 1 2 3 4)
;; #<UnionFind {1 nil, 2 nil, 3 nil, 4 nil}>

=> (into (union-find) [1 2 3 4])
;; #<UnionFind {1 nil, 2 nil, 3 nil, 4 nil}>

=> (conj (union-find) 1)
;; #<UnionFind {1 nil}>

=> (find (union-find 1 2 3 4) 2)
;; 2

=> (find (union-find 1 2 3 4) -1)
;; nil

=> (let [uf (union-find 1 2 3 4)]
     [(uf 2) (uf -1 :not-found)])
;; [2 :not-found]  

=> (-> (union-find 1 2 3 4) (union 1 3))
;; #<UnionFind {1 nil, 2 nil, 3 1, 4 nil}>

;; note that the internal tree structure depends on the union args order
=> (-> (union-find 1 2 3 4) (union 3 1))
;; #<UnionFind {1 3, 2 nil, 3 nil, 4 nil}>

;; with this version, equality check relies on internal tree structure, thus
;; will surpringly tell the 2 previous union-find structures are different
;; (even though they aren't since they represent the same disjoint sets):
=> (= (-> (union-find 1 2 3 4) (union 3 1))
      (-> (union-find 1 2 3 4) (union 1 3)))
;; false

)
