(ns tlclisp.fnc-sub-test
  (:require [clojure.test :refer :all]
            [tlclisp.interpreter :refer [fnc-sub]]))

(deftest fnc-add-test
  (testing "Llamar a la funcion sin argumentos devuelve error"
    (is (= '(*error* too-few-args) (fnc-sub ()))))

  (testing "Llamar a la funcion con un solo argumento develve ese numero negativo"
    (is (= -3 (fnc-sub '(3)))))

  (testing "Calcula la resta correctamente"
    (is (and (= -1 (fnc-sub '(3 4)))
             (= -6 (fnc-sub '(3 4 5)))
             (= -12 (fnc-sub '(3 4 5 6))))))

  (testing "Devuelve error si se pasa algo que no es un numero"
    (is (and (= '(*error* number-expected A) (fnc-sub '(A 4 5 6)))
             (= '(*error* number-expected A) (fnc-sub '(3 A 5 6)))
             (= '(*error* number-expected A) (fnc-sub '(3 4 A 6)))))))