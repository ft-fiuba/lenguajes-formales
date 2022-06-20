(ns tlclisp.fnc-read-test
  (:require [clojure.test :refer :all]
            [tlclisp.interpreter :refer [fnc-read]]))

(deftest fnc-read-test
  (testing "Leer de pantalla"
    (is 1 (with-in-str "1" (fnc-read ())))
    (is "hola" (with-in-str "(hola)" (fnc-read ())))
    (is '(hola mundo) (with-in-str "(hola mundo)" (fnc-read ()))))

  (testing "Leer de pantalla () es nil"
    (is (nil? (with-in-str "()" (fnc-read ())))))

  (testing "Leer de pantalla 'nil' es nil"
    (is (nil? (with-in-str "nil" (fnc-read ())))))

  (testing "No pasar una lista vacia como parametro"
    (is '(*error* not-implemented) (fnc-read '(1)))
    (is '(*error* not-implemented) (fnc-read '(1 2)))))