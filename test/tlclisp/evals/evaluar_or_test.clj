(ns tlclisp.evals.evaluar_or_test
  (:require [clojure.test :refer :all]
            [tlclisp.interpreter :refer [evaluar-or]]))



(deftest evaluar-escalar-test
  (testing "Evaluar or sin arguementos devuelve nil"
    (is (= '(nil (nil nil t t w 5 x 4)) (evaluar-or '(or) '(nil nil t t w 5 x 4) '(x 1 y nil z 3)))))

  (testing "Evaluar con un solo argumento devuelve correctamente"
    (is (= '(nil (nil nil t t w 5 x 4)) (evaluar-or '(or nil) '(nil nil t t w 5 x 4) '(x 1 y nil z 3))))
    (is (= '(t (nil nil t t w 5 x 4)) (evaluar-or '(or t) '(nil nil t t w 5 x 4) '(x 1 y nil z 3))))
    (is (= '(5 (nil nil t t w 5 x 4)) (evaluar-or '(or w) '(nil nil t t w 5 x 4) '(x 1 y nil z 3))))
    (is (= '(nil (nil nil t t w 5 x 4)) (evaluar-or '(or y) '(nil nil t t w 5 x 4) '(x 1 y nil z 3))))
    (is (= '(6 (nil nil t t w 5 x 4)) (evaluar-or '(or 6) '(nil nil t t w 5 x 4) '(x 1 y nil z 3)))))

  (testing "Evaluar con mas de un argumento"
    (is (= '(6 (nil nil t t w 5 x 4)) (evaluar-or '(or nil 6) '(nil nil t t w 5 x 4) '(x 1 y nil z 3))))
    (is (= '(6 (nil nil t t w 5 x 4)) (evaluar-or '(or nil 6 nil) '(nil nil t t w 5 x 4) '(x 1 y nil z 3))))
    (is (= '(6 (nil nil t t w 5 x 4)) (evaluar-or '(or nil 6 r nil) '(nil nil t t w 5 x 4) '(x 1 y nil z 3))))
    (is (= '(t (nil nil t t w 5 x 4)) (evaluar-or '(or nil t r nil) '(nil nil t t w 5 x 4) '(x 1 y nil z 3))))
    (is (= '(nil (nil nil t t w 5 x 4)) (evaluar-or '(or nil nil nil nil) '(nil nil t t w 5 x 4) '(x 1 y nil z 3)))))

; user=> (evaluar-or '(or (setq b 8) nil) '(nil nil t t w 5 x 4) '(x 1 y nil z 3))
; (8 (nil nil t t w 5 x 4 b 8))

  (testing "Evaluar cuando la clave no existe en el ambiente devuelve error"
    (is (= '((*error* unbound-symbol r) (nil nil t t w 5 x 4)) (evaluar-or '(or r) '(nil nil t t w 5 x 4) '(x 1 y nil z 3))))))