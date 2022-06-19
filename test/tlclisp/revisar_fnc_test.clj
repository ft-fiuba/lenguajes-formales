(ns tlclisp.revisar-fnc-test
  (:require [clojure.test :refer :all]
            [tlclisp.interpreter :refer [revisar-fnc]]))


(deftest revisar-fnc-test
  (testing "Revisar una lista con error como string devudelve la lista"
    (let [L '("*error*" "too-few-args")]
      (is (= L (revisar-fnc L)))))

  (testing "Revisar una lista con error como quote devudelve la lista"
    (let [J ('*error* 'too-few-args)]
      (is (= J (revisar-fnc J)))))

  (testing "Revisar algo que no es una lista devuelve nil"
    (is (nil? (revisar-fnc '*error*))))

  (testing "Revisar una lista sin errores devuelve nil"
    (is (nil? (revisar-fnc '(too-few-args)))))

  (testing "Revisar nil devuelve nil"
    (is (nil? (revisar-fnc nil))))

  (testing "Revisar una lista vacia devuelve nil"
    (is (nil? (revisar-fnc ())))))
