(ns tlclisp.fnc-env-test
  (:require [clojure.test :refer :all]
            [tlclisp.interpreter :refer [fnc-env]]))

; user=> (fnc-env () '(a 1 b 2) '(c 3 d 4))
; (a 1 b 2 c 3 d 4)
; user=> (fnc-env '(5) '(a 1 b 2) '(c 3 d 4))
; (*error* too-many-args)
(deftest fnc-env-test
  (testing "Unir dos ambientes devuelve una sequencia con ambos"
    (is (= '(a 1 b 2 c 3 d 4) (fnc-env () '(a 1 b 2) '(c 3 d 4)))))

  (testing "Unir dos ambientes devuelve una sequencia con ambos"
    (is (= '(*error* too-many-args) (fnc-env '(5) '(a 1 b 2) '(c 3 d 4))))))
