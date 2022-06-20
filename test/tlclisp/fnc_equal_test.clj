(ns tlclisp.fnc-equal-test
  (:require [clojure.test :refer :all]
            [tlclisp.interpreter :refer [fnc-equal]]))

; user=> (fnc-equal '(1 1))
; t
; user=> (fnc-equal '(A a))
; t
; user=> (fnc-equal '("1" "1"))
; t
; user=> (fnc-equal '(nil NIL))
; t
; user=> (fnc-equal '(1 2))
; nil
; user=> (fnc-equal '(A B))
; nil
; user=> (fnc-equal '("1" 1))
; nil

; user=> (fnc-equal ())
; (*error* too-few-args)
; user=> (fnc-equal '(A))
; (*error* too-few-args)
; user=> (fnc-equal '(A a A))
; (*error* too-many-args)

(deftest fnc-equal-test
  (testing "El mismo numero devuelve que son iguales"
    (is (= 't (fnc-equal '(1 1)))))

  (testing "Dos caracteres son iguales sin importar el case"
    (is (and (= 't (fnc-equal '(A a)))
             (= 't (fnc-equal '(A A)))
             (= 't (fnc-equal '(a a))))))

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
