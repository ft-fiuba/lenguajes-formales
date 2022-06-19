(ns tlclisp.fnc-append-test
  (:require [clojure.test :refer :all]
            [tlclisp.interpreter :refer [fnc-append]]))

; user=> 
; 
; user=> (fnc-append '( (1 2) 3 ))
; (*error* list expected 3)
; user=> (fnc-append '( (1 2) A ))
; (*error* list expected A)


; user=> (fnc-append '(nil nil))
; nil
; user=> (fnc-append '(() ()))
; nil

(deftest fnc-append-test
  (testing "Appendear una sola lista devuelve error"
    (is (= '(*error* too-few-args) (fnc-append '((1 2))))))

  (testing "Appendear mas de dos listas devuelve error"
    (is (= '(*error* too-many-args) (fnc-append '((1 2) (3) (4 5) (6 7))))))

  (testing "Appendear dos listas devuelve correctamente"
    (is (= '(1 2 3) (fnc-append '((1 2) (3))))))

  (testing "Appendeaar una lista con nil devuelve ella misma"
    (is (and (= '(1 2) (fnc-append '((1 2) nil)))
             (= '(1 2) (fnc-append '(nil (1 2)))))))

  (testing "Appendeaar una lista con lista vacia devuelve ella misma"
    (is (and (= '(1 2) (fnc-append '((1 2) ())))
             (= '(1 2) (fnc-append '(() (1 2)))))))

  (testing "Appendear nil con nil develve nil"
    (is (nil? (fnc-append '(nil nil)))))

  (testing "Appendear dos listas vacias devuelve nil"
    (is (nil? (fnc-append '(() ()))))))
