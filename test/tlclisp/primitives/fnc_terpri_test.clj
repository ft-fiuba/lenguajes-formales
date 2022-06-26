(ns tlclisp.primitives.fnc-terpri-test
  (:require [clojure.test :refer :all]
            [tlclisp.interpreter :refer [fnc-terpri]]))

(deftest fnc-read-test
  (testing "Devuelve nil"
    (is (nil? (fnc-terpri ()))))

  (testing "No pasar una lista vacia como parametro devuelve error"
    (is (= '(*error* not-implemented) (fnc-terpri '(1))))))