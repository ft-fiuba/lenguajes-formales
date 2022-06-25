(ns tlclisp.auxiliaries.actualizar-amb-test
  (:require [clojure.test :refer :all]
            [tlclisp.interpreter :refer [actualizar-amb]]))

(deftest actualizar-amb-test
  (testing "Si el elemento no existe lo agrega"
    (is (= '(a 1 b 2 c 3 d 4) (actualizar-amb '(a 1 b 2 c 3) 'd 4))))

  (testing "Si el elemento existe lo actualiza"
    (is (= '(a 1 b 4 c 3) (actualizar-amb '(a 1 b 2 c 3) 'b 4))))

  (testing "Si el valor a actualizar es un error, devuelve el mismo sin modificarlo"
    (is (= '(a 1 b 2 c 3) (actualizar-amb '(a 1 b 2 c 3) 'b (list '*error* 'mal 'hecho)))))

  (testing "Si el ambiente esta vacio se agrega el nuevo elemento"
    (is (= '(b 7) (actualizar-amb () 'b 7)))))

