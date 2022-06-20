(ns tlclisp.evals.evaluar-de-test
  (:require [clojure.test :refer :all]
            [tlclisp.interpreter :refer [evaluar-de]]))

(deftest evaluar-escalar-test
  (testing "Actualiza el ambiente con la funcion correctamente"
    (is (= '(f (x 1 f (lambda (x)))) (evaluar-de '(de f (x)) '(x 1))))
    (is (= '(f (x 1 f (lambda (x) (+ x 1)))) (evaluar-de '(de f (x) (+ x 1)) '(x 1))))
    (is (= '(f (x 1 f (lambda (x y) (+ x y)))) (evaluar-de '(de f (x y) (+ x y)) '(x 1))))
    (is (= '(f (x 1 f (lambda (x y) (prin3 x) (terpri) y))) (evaluar-de '(de f (x y) (prin3 x) (terpri) y) '(x 1)))))

  (testing "Error funcion sin nombre"
    (is (= '((*error* list-expected nil) (x 1)) (evaluar-de '(de) '(x 1)))))

  (testing "Error funcion sin parametos"
    (is (= '((*error* list-expected nil) (x 1)) (evaluar-de '(de f) '(x 1))))
    (is (= '((*error* list-expected nil) (x 1)) (evaluar-de '(de (f)) '(x 1)))))

  (testing "Error funcion con parametros que no son lista"
    (is (= '((*error* list-expected 2) (x 1)) (evaluar-de '(de f 2) '(x 1))))
    (is (= '((*error* list-expected 2) (x 1)) (evaluar-de '(de f 2 3) '(x 1))))
    (is (= '((*error* list-expected x) (x 1)) (evaluar-de '(de 2 x) '(x 1)))))

  (testing "Error funcion con nombre que no es simbolo"
    (is (= '((*error* symbol-expected 2) (x 1)) (evaluar-de '(de 2 (x)) '(x 1)))))

  (testing "Error funcion con nombre nil"
    (is (= '((*error* cannot-set nil) (x 1)) (evaluar-de '(de nil (x) 2) '(x 1))))))

