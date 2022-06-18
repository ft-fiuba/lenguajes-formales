(ns tlclisp.error-test
  (:require [clojure.test :refer :all]
            [tlclisp.interpreter :refer [error?]]))

(deftest error?-test
  (testing "Es error si se pasa una quote"
    (is (error? '(*error* too-few-args))))

  (testing "Es error cuando la lista tiene error sin importar el case"
    (is (and (error? (list '*error* 'too-few-args))
             (error? (list '*ERROR* 'too-few-args))
             (error? (list '*Error* 'too-few-args)))))

  (testing "Es error si la lista tiene un solo elemento que diga error"
    (is (error? (list '*error*))))

  (testing "No es error si no dice error la lista"
    (is (not (error? (list 'too-few-args)))))

  (testing "No es error si no se pasa una lista"
    (is (not (error? '*error*))))

  (testing "No es error si se pasa una lista vacia"
    (is (not (error? ()))))

  (testing "No es error si se pasa nil"
    (is (not (error? nil)))))
