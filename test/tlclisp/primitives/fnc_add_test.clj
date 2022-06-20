(ns tlclisp.primitives.fnc-add-test
  (:require [clojure.test :refer :all]
            [tlclisp.interpreter :refer [fnc-add]]))

(deftest fnc-add-test
  (testing "Llamar a la funcion sin argumentos devuelve error"
    (is (= '(*error* too-few-args) (fnc-add ()))))

  (testing "Llamar a la funcion con un solo argumento devuelve error"
    (is (= '(*error* too-few-args) (fnc-add '(1)))))

  (testing "Calcula la suma correctamente"
    (is (and (= 7 (fnc-add '(3 4)))
             (= 12 (fnc-add '(3 4 5)))
             (= 18 (fnc-add '(3 4 5 6))))))

  (testing "Devuelve error si se pasa algo que no es un numero"
    (is (and (= '(*error* number-expected A) (fnc-add '(A 4 5 6)))
             (= '(*error* number-expected A) (fnc-add '(3 A 5 6)))
             (= '(*error* number-expected A) (fnc-add '(3 4 A 6)))))))