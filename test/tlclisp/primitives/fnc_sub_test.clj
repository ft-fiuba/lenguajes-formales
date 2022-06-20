(ns tlclisp.primitives.fnc-sub-test
  (:require [clojure.test :refer :all]
            [tlclisp.interpreter :refer [fnc-sub]]))

(deftest fnc-add-test
  (testing "Llamar a la funcion sin argumentos devuelve error"
    (is (= '(*error* too-few-args) (fnc-sub ()))))

  (testing "Llamar a la funcion con un solo argumento develve ese numero negativo"
    (is (= -3 (fnc-sub '(3)))))

  (testing "Calcula la resta correctamente"
    (is  (= -1 (fnc-sub '(3 4))))
    (is (= -6 (fnc-sub '(3 4 5))))
    (is (= -12 (fnc-sub '(3 4 5 6)))))

  (testing "Devuelve error si se pasa algo que no es un numero"
    (is (= '(*error* number-expected A) (fnc-sub '(A 4 5 6))))
    (is (= '(*error* number-expected A) (fnc-sub '(3 A 5 6))))
    (is (= '(*error* number-expected A) (fnc-sub '(3 4 A 6))))))