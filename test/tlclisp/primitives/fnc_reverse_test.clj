(ns tlclisp.primitives.fnc-reverse-test
  (:require [clojure.test :refer :all]
            [tlclisp.interpreter :refer [fnc-reverse]]))

(deftest fnc-reverse-test
  (testing "No pasar argumentos genera error"
    (is (= '(*error* too-few-args) (fnc-reverse ()))))

  (testing "Pasar un argumento que no es una lista genera error"
    (is (= '(*error* list-expected 1) (fnc-reverse '(1)))))

  (testing "Revierte una lista de un elemento correctamente"
    (is (= '(1) (fnc-reverse '((1))))))

  (testing "Revierte una lista con mas de un elemento correctamente"
    (is (= '(3 2 1) (fnc-reverse '((1 2 3))))))

  (testing "Pasar mas de un argumento genera error"
    (is (= '(*error* too-many-args) (fnc-reverse '((1 2 3) (4)))))))
