(ns tlclisp.auxiliaries.revisar-lae-test
  (:require [clojure.test :refer :all]
            [tlclisp.interpreter :refer [revisar-lae]]))

(deftest revisar-lae-test
  (testing "Si la lista no contiene errores devuelve nil"
    (is (nil? (revisar-lae '(1 2 3)))))

  (testing "Si le pasamos nil devuelve nil"
    (is (nil? (revisar-lae nil))))

  (testing "Si la lista esta vacia devuelve nil"
    (is (nil? (revisar-lae ()))))

  (testing "Devuelve correctamente el error si hay un solo error"
    (is (= '(*error* too-few-args) (revisar-lae '(1 (*error* too-few-args) 3)))))

  (testing "Devuelve correctamente el primer error si hay mas de un error"
    (is (= '(*error* too-few-args) (revisar-lae '(1 (*error* too-few-args) (*error* too-many-args) 3))))))

