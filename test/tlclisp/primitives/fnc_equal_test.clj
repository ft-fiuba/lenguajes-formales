(ns tlclisp.primitives.fnc-equal-test
  (:require [clojure.test :refer :all]
            [tlclisp.interpreter :refer [fnc-equal]]))

(deftest fnc-equal-test
  (testing "El mismo numero devuelve que son iguales"
    (is (= 't (fnc-equal '(1 1)))))

  (testing "Dos caracteres son iguales sin importar el case"
    (is (= 't (fnc-equal '(A a))))
    (is (= 't (fnc-equal '(A A))))
    (is (= 't (fnc-equal '(a a)))))

  (testing "Dos strings son iguales"
    (is (= 't (fnc-equal '("1" "1")))))

  (testing "NIL y nil son iguales"
    (is (= 't (fnc-equal '(nil NIL)))))

  (testing "Dos numeros distintos no son iguales"
    (is (nil? (fnc-equal '(1 2)))))

  (testing "Dos caracteres distintos no son iguales"
    (is (nil? (fnc-equal '(A B)))))

  (testing "Un string y un numero no son iguales"
    (is (nil? (fnc-equal '(1 "1"))))))
