(ns tlclisp.fnc-ge
  (:require [clojure.test :refer :all]
            [tlclisp.interpreter :refer [fnc-ge]]))

(deftest fnc-ge-test
  (testing "Llamar a la funcion sin argumentos devuelve error∫"
    (is (= '(*error* too-few-args) (fnc-ge ()))))

  (testing "Llamar a la funcion con un solo argumento devuelve error∫"
    (is (= '(*error* too-few-args) (fnc-ge '(1)))))

  (testing "Determina es mayor correctamente"
    (is (and (= 't (fnc-ge '(2 1)))
             (= nil (fnc-ge '(1 2)))
             (= 't (fnc-ge '(1 1))))))

  (testing "Devuelve error si se pasa algo que no es un numero"
    (is (and (= '(*error* number-expected A) (fnc-ge '(A 1)))
             (= '(*error* number-expected A) (fnc-ge '(1 A))))))

  (testing "Devuelve error si se pasan mas de 2 argumentos"
    (is (= '(*error* too-many-args) (fnc-ge '(1 2 3))))))
