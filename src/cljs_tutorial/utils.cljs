(ns cljs-tutorial.utils)

(defn v- [[px py] [qx qy]]
  [(- qx px) (- qy py)])
(defn v+ [[px py] [qx qy]]
  [(+ qx px) (+ qy py)])
(defn v* [[px py] s]
  [(* s px) (* s py)])
(defn distance [[px py] [qx qy]]
  (let [dx (- px qx)
        dy (- py qy)]
    (Math/sqrt (+ (* dx dx) (* dy dy)))))

(defn outer-product
  ([[px py] [qx qy]] 
   (- (* px qy) (* py qx))
   )
  ([O A B] 
   (assert (every? #(== 2 %) (map count [O A B])) "expect O, A, B, each of which is a vector of length 2")
   (outer-product (v- O A) (v- O B))))

(defn ccw? [O A B] (pos? (outer-product O A B)))
(defn cw?  [O A B] (neg? (outer-product O A B)))
(defn colinear? [O A B] (= 0 (outer-product O A B)))

(defn convex-quadrilateral?
  {:test #(let [[A B C D] [[1 0] [-1 0] [0 1] [0 -1]]]
            (assert (convex-quadrilateral? A C B D)))}
  [P Q R S]
  (let [angles (map #(apply outer-product %) [[P Q R] [Q R S] [R S P] [S P Q]])
        non-colinears (filter #(not= 0 %) angles)]
    (or (every? pos? non-colinears)
        (every? neg? non-colinears))))

(defn intersect?
  {:test #(let [[A B C D] [[1 0] [-1 0] [0 1] [0 -1]]]
            (assert (intersect? [A B] [C D])))}
  [[A B] [C D]]
  (convex-quadrilateral? A C B D))

(defn intersection
  {:test #(do (assert (= [0.5 0.5] (intersection [[0 0] [1 1]] [[0 1] [1 0]]))))}
  [[A B] [C D]]
  (let [n (outer-product (v- C D) (v- A C))
        d (outer-product (v- C D) (v- A B))
        t (/ n d)]
    (v+ (v* B t) (v* A (- 1 t)))))

