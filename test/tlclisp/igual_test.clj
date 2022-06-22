(ns tlclisp.igual-test
  (:require [clojure.test :refer :all]
            [tlclisp.interpreter :refer [igual?]]))

(deftest igual?-test
  (testing "El mismo numero devuelve que son iguales"
    (is (igual? 1 1)))

  (testing "Dos numeros diferentes no son iguales"
    (is (not (igual? 1 2))))

  (testing "Igualar quote con string sin importar el case"
    (is (and (igual? "hola" 'hola) (igual? "hola" 'HOLA) (igual? "HOLA" 'hola))))

  (testing "Dos chars del mismo case son iguales"
    (is (and (igual? 'A 'A) (igual? 'a 'a))))

  (testing "Dos chars de distinto case son iguales"
    (is (and (igual? 'A 'a) (igual? 'a 'A))))

  (testing "Dos chars diferentes no son iguales"
    (is (not (igual? 'a 'b))))

  (testing "Dos listas con chars de distinto case son iguales"
    (is (igual? '(a b c) '(A B C))))

  (testing "Dos listas con chars distintos no son iguales"
    (is (not (igual? '(a b c) '(A B D)))))

  (testing "Dos chars upper case son iguales"
    (is (igual? 'A 'A)))

  (testing "Dos nil sos iguales"
    (is (igual? nil nil)))

  (testing "Un nil una quote de nil son iguales"
    (is (and (igual? nil 'NIL) (igual? 'NIL nil))))

  (testing "Dos quotes son iguales"
    (is (igual? 'NIL 'NIL)))

  (testing "Nil y lista vacia son iguales"
    (is (igual? () nil)))

  (testing "Lista vacia y quote NIL son iguales"
    (is (igual? 'NIL ())))

  (testing "Dos listas vacias son iguales"
    (is (igual? () ())))

  (testing "Lista vacia y lista con nil no son iguales"
    (is (not (igual? () '(nil)))))

  (testing "Una quote y un string son iguales sin oimportar el case"
    (is (and (igual? 'a "A") (igual? 'a "a") (igual? 'A "A") (igual? 'A "a")))))

