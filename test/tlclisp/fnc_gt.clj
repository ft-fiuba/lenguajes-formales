(ns tlclisp.fnc-gt
  (:require [clojure.test :refer :all]
            [tlclisp.interpreter :refer [fnc-gt]]))

(deftest fnc-gt-test
  (testing "Llamar a la funcion sin argumentos devuelve error∫"
    (is (= '(*error* too-few-args) (fnc-gt ()))))

  (testing "Llamar a la funcion con un solo argumento devuelve error∫"
    (is (= '(*error* too-few-args) (fnc-gt '(1)))))

  (testing "Determina es mayor correctamente"
    (is (and (= 't (fnc-gt '(2 1)))
             (= nil (fnc-gt '(1 2)))
             (= nil (fnc-gt '(1 1))))))

  (testing "Devuelve error si se pasa algo que no es un numero"
    (is (and (= '(*error* number-expected A) (fnc-gt '(A 1)))
             (= '(*error* number-expected A) (fnc-gt '(1 A))))))

  (testing "Devuelve error si se pasan mas de 2 argumentos"
    (is (= '(*error* too-many-args) (fnc-gt '(1 2 3))))))
