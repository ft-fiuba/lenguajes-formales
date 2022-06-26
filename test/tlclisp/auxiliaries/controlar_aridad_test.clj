(ns tlclisp.auxiliaries.controlar-aridad-test
  (:require [clojure.test :refer :all]
            [tlclisp.interpreter :refer [controlar-aridad]]))

(deftest controlar-aridad-test
  (testing "Devuelve error si la lista tiene mas elementos de lo esperado"
    (let [result (controlar-aridad '(a b c) 2)]
      (is (and (= (nth result 0) '*error*) (= (nth result 1) 'too-many-args)))))

  (testing "Devuelve error si la lista tiene menos elementos de lo esperado"
    (let [result (controlar-aridad '(a b c) 4)]
      (is (and (= (nth result 0) '*error*) (= (nth result 1) 'too-few-args)))))

  (testing "Devueleve correactamente el tamaño de la aridad coincide"
    (let [result (controlar-aridad '(a b c) 3)]
      (is (= 3 result)))))