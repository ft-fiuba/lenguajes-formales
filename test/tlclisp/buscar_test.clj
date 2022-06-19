(ns tlclisp.buscar-test
  (:require [clojure.test :refer :all]
            [tlclisp.interpreter :refer [buscar]]))

; user=> (buscar 'c '(a 1 b 2 c 3 d 4 e 5))
; 3
; user=> (buscar 'f '(a 1 b 2 c 3 d 4 e 5))
; (*error* unbound-symbol f)

(deftest buscar-test
  (testing "Devuelve correctamente el valor si la encuentra"
    (is (= 3 (buscar 'c '(a 1 b 2 c 3 d 4 e 5)))))

  (testing "Devuelve error si no se encuentra la clave"
    (is (= '("*error*" "unbound-symbol" f) (buscar 'f '(a 1 b 2 c 3 d 4 e 5))))))
