(ns tlclisp.evals.evaluar-escalar-test
  (:require [clojure.test :refer :all]
            [tlclisp.interpreter :refer [evaluar-escalar]]))

(deftest evaluar-escalar-test
  (testing "Evaluar int devuelve a si mismo y el primer ambiente"
    (is (= '(32 (v 1 w 3 x 6)) (evaluar-escalar 32 '(v 1 w 3 x 6) '(x 5 y 11 z "hola")))))

  (testing "Evaluar string devuelve a si mismo y el primer ambierte"
    (is (= '("chau" (v 1 w 3 x 6)) (evaluar-escalar "chau" '(v 1 w 3 x 6) '(x 5 y 11 z "hola")))))

  (testing "Evaluar simbolo encuentra correctamente el ambiente"
    (is (= '("hola" (v 1 w 3 x 6)) (evaluar-escalar 'z '(v 1 w 3 x 6) '(x 5 y 11 z "hola"))))
    (is (= '("hola" (v 1 w 3 x 6)) (evaluar-escalar 'Z '(v 1 w 3 x 6) '(x 5 y 11 z "hola"))))
    (is (= '(3 (v 1 w 3 x 6)) (evaluar-escalar 'w '(v 1 w 3 x 6) '(x 5 y 11 z "hola"))))
    (is (= '(5 (v 1 w 3 x 6)) (evaluar-escalar 'x '(v 1 w 3 x 6) '(x 5 y 11 z "hola")))))

  (testing "Devuelve error el simbolo no se encuentra"
    (is (= '((*error* unbound-symbol n) (v 1 w 3 x 6)) (evaluar-escalar 'n '(v 1 w 3 x 6) '(x 5 y 11 z "hola"))))))