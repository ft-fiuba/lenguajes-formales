(ns tlclisp.evals.evaluar_setq_test
  (:require [clojure.test :refer :all]
            [tlclisp.interpreter :refer [evaluar-setq]]))





(deftest evaluar-setq-test
  (testing "Evaluar con variable que no existe en ambiente la agrega y devuelve su valor"
    (is (= '(7 (nil nil t t + add w 5 x 4 m 7)) (evaluar-setq '(setq m 7) '(nil nil t t + add w 5 x 4) '(x 1 y nil z 3)))))

  (testing "Evaluar con varibale que existe en ambiente global la remplaza y devuelve su valor"
    (is (= '(7 (nil nil t t + add w 5 x 7)) (evaluar-setq '(setq x 7) '(nil nil t t + add w 5 x 4) '(x 1 y nil z 3)))))

  (testing "Evaluar con expresion en ambiente local remplaza el global"
    (is (= '(2 (nil nil t t + add w 5 x 2)) (evaluar-setq '(setq x (+ x 1)) '(nil nil t t + add w 5 x 4) '(x 1 y nil z 3)))))

  (testing "Evaluar con expresion en ambiente local remplaza el global"
    (is (= '(5 (nil nil t t + add w 5 x 5)) (evaluar-setq '(setq x (+ x 1)) '(nil nil t t + add w 5 x 4) '(y nil z 3)))))

  (testing "Setear mas de una variable"
    (is (= '(8 (nil nil t t + add w 5 x 7 m 8)) (evaluar-setq '(setq x 7 m (+ x 7)) '(nil nil t t + add w 5 x 4) '(x 1 y nil z 3))))
    (is (= '(14 (nil nil t t + add w 5 x 7 m 14)) (evaluar-setq '(setq x 7 m (+ x 7)) '(nil nil t t + add w 5 x 4) '(y nil z 3))))
    (is (= '(9 (nil nil t t + add w 5 x 7 y 8 z 9)) (evaluar-setq '(setq x 7 y 8 z 9) '(nil nil t t + add w 5 x 4) '(y nil z 3)))))

  (testing "Enviar un predicado mal formado devuelve error"
    (is (= '((*error* list-expected nil) (nil nil t t + add w 5 x 4)) (evaluar-setq '(setq) '(nil nil t t + add w 5 x 4) '(x 1 y nil z 3))))
    (is (= '((*error* list-expected nil) (nil nil t t + add w 5 x 4)) (evaluar-setq '(setq m) '(nil nil t t + add w 5 x 4) '(x 1 y nil z 3))))
    (is (= '((*error* list-expected nil) (nil nil t t + add w 5 x 4)) (evaluar-setq '(setq nil) '(nil nil t t + add w 5 x 4) '(x 1 y nil z 3)))))

  (testing "Enviar un parcialmente mal formado setea las variables que estan correctamente asignadas"
    (is (= '((*error* list-expected nil) (nil nil t t + add w 5 x 7)) (evaluar-setq '(setq x 7 y) '(nil nil t t + add w 5 x 4) '(y nil z 3)))))

  (testing "No se puede asignar valor a nil"
    (is (= '((*error* cannot-set nil) (nil nil t t + add w 5 x 4)) (evaluar-setq '(setq nil 7) '(nil nil t t + add w 5 x 4) '(x 1 y nil z 3)))))

  (testing "No se puede asignar valor a algo que no es simbolo"
    (is (= '((*error* symbol-expected 7) (nil nil t t + add w 5 x 4)) (evaluar-setq '(setq 7 8) '(nil nil t t + add w 5 x 4) '(x 1 y nil z 3))))))