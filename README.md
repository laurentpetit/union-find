# union-find

Clojure persistent Union-Find Data structure.

not yet Available in Leiningen via [Clojars](https://clojars.org/org.lpetit/union-find):

    [org.lpetit/union-find "0.1.0-SNAPSHOT"]

## Maturity

Not production ready.

This is currently just a toy library.

## Presentation

Implements the weighted union algorithm and appropriate union find datastructure.

It is immutable, and persistent. Thus it is internally using persistent hash maps
instead of arrays.

* Pro: more general than array-based: every set of sites that implement equality
   correctly can be used (e.g. any immutable datastructure).
* Cons: hash-maps have only near constant access time, and the constant is bigger
  than array accesses!

Synonyms: disjoint sets, merge find sets, union-find datastructures

## Terminology

* union-find ds: an union-find datastructure (synonym of disjoint set / merge find set)
* site: an element added to the union-find ds.
* component: the set of joined sites of a union-find ds.


## Usage

Require the library

```clj
=> (require '[org.lpetit/union-find :as uf :refer [union-find union count-sites]])
nil
```

Create an empty union-find ds:

```clj
=> (union-find)
#<UnionFind {}>
```

Initialize a new union-find ds:

```clj
=> (union-find 1 2 3 4)
#<UnionFind {1 nil, 2 nil, 3 nil, 4 nil}>
```

Add a new site as a singleton component with conj:

```clj
=> (conj (union-find) 1)
#<UnionFind {1 nil}>
```

Add several sites as singleton components from a coll:

```clj
=> (into (union-find) [1 2 3 4])
#<UnionFind {1 nil, 2 nil, 3 nil, 4 nil}>
```

Join two components by creating an union between 2 sites:

```clj
=> (-> (union-find 1 2 3 4) (union 1 3))
#<UnionFind {1 nil, 2 nil, 3 1, 4 nil}>
```

Find the component for a site:

```clj
=> (uf/find (union-find 1 2 3 4) 2)
2

=> (uf/find (union-find 1 2 3 4) -1)
nil

=> (uf/find (union-find 1 2 3 4) -1 :not-found)
:not-found
```

The datastructure implements `IAssoc` and `IFn`, so you have shortcuts for finding
the component for a site:

```clj
=> (let [uf (union-find 1 2 3 4)]
     [(uf 2) (uf -1 :not-found)])
[2 :not-found]  
```

Note that with the current implementation, 
the internal tree structure depends on the union args order ...:

```clj
=> (-> (union-find 1 2 3 4) (union 1 3))
#<UnionFind {1 nil, 2 nil, 3 1, 4 nil}>

=> (-> (union-find 1 2 3 4) (union 3 1))
#<UnionFind {1 3, 2 nil, 3 nil, 4 nil}>
```

...thus the 2 previous union-find structures are considered different
(even though they represent the same disjoint sets):

```clj
=> (= (-> (union-find 1 2 3 4) (union 3 1))
      (-> (union-find 1 2 3 4) (union 1 3)))
false
```

You can use `'seq` on the datastructure. You'll get a lazy seq of the components:

```clj
=> (seq (-> (union-find 1 2 3 4) (union 1 3)))
(4 2 1) ;;  (-> (union-find 1 2 3 4) (union 1 3)) == #<UnionFind {1 nil, 2 nil, 3 1, 4 nil}> 
```

You can use `'count` on the datastructure, you'll get the number of components in
constant time:

```clj
=> (-> (union-find 1 2 3 4) (union 1 3) count)
3
```

You can get the number of sites in the datastructure via `'count-sites`:

```clj
=> (-> (union-find 1 2 3 4) (union 1 3) count-sites)
4
```

## Worst-case algorithm guarantees

`N` is the number of sites currently in the datastructure

`union-find`: O( nb initialization sites )

`uf/find`: O( log(N) )

`union`: O( log(N) )

`seq`: worst case O( N ) for accessing the first element if single component at the end of the seqed map

`count`: O( 1 )

`toString`: O( N )

## Todo List

* Implement path compression via optimistic :volatile-synchronized for
UnionFind site-props
* Add methods to return the components (sets of sites)
* Enhance '= to not rely on union / find operations ordering
* Support metadata as other datastructures 


## Credits

Inspired by Jordan Lewis' [data.union-find](https://github.com/jordanlewis/data.union-find) library

Algorithm adapted from Sedgewick's intro to Algorithms in "Algorithms" book & Coursera course


## License

Copyright (c) 2014 Laurent Petit

Distributed under the Eclipse Public License, the same as Clojure.
