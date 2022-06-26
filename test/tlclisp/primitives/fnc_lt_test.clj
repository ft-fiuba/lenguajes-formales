(ns tlclisp.primitives.fnc-lt-test
  (:require [clojure.test :refer :all]
            [tlclisp.interpreter :refer [fnc-lt]]))

(deftest fnc-lt-test
  (testing "Llamar a la funcion sin argumentos devuelve error"
    (is (= '(*error* too-few-args) (fnc-lt ()))))

  (testing "Llamar a la funcion con un solo argumento devuelve error"
    (is (= '(*error* too-few-args) (fnc-lt '(1)))))

  (testing "Determina el menor correctamente"
    (is (= 't (fnc-lt '(1 2))))
    (is (= nil (fnc-lt '(2 1))))
    (is (= nil (fnc-lt '(1 1)))))

  (testing "Devuelve error si se pasa algo que no es un numero"
    (is (= '(*error* number-expected A) (fnc-lt '(A 1))))
    (is (= '(*error* number-expected A) (fnc-lt '(1 A)))))

  (testing "Devuelve error si se pasan mas de 2 argumentos"
    (is (= '(*error* too-many-args) (fnc-lt '(1 2 3))))))
