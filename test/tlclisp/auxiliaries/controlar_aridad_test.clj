(ns tlclisp.auxiliaries.controlar-aridad-test
  (:require [clojure.test :refer :all]
            [tlclisp.interpreter :refer [controlar-aridad]]))

(deftest controlar-aridad-test
  (testing "Devuelve error si la lista tiene mas elementos de lo esperado"
    (is (= '(*error* too-many-args) (controlar-aridad '(a b c) 2))))

  (testing "Devuelve error si la lista tiene menos elementos de lo esperado"
    (is (= '(*error* too-few-args) (controlar-aridad '(a b c) 4))))

  (testing "Devueleve correactamente el tamaño de la aridad coincide"
    (is (= 3 (controlar-aridad '(a b c) 3)))))