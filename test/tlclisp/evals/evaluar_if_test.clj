(ns tlclisp.evals.evaluar_if_test
  (:require [clojure.test :refer :all]
            [tlclisp.interpreter :refer [evaluar-if]]))



(deftest evaluar-if-test
  (testing "Evaluar condicion sin definir resultados deuvelve nil"
    (is (= '(nil (nil nil t t v 1 w 3 x 6)) (evaluar-if '(if t) '(nil nil t t v 1 w 3 x 6) '(x 5 y 11 z "hola"))))
    (is (= '(nil (nil nil t t v 1 w 3 x 6)) (evaluar-if '(if 7) '(nil nil t t v 1 w 3 x 6) '(x 5 y 11 z "hola"))))
    (is (= '(nil (nil nil t t v 1 w 3 x 6)) (evaluar-if '(if nil) '(nil nil t t v 1 w 3 x 6) '(x 5 y 11 z "hola"))))
    (is (= '(nil (nil nil t t v 1 w 3 x 6)) (evaluar-if '(if x) '(nil nil t t v 1 w 3 x 6) '(x 5 y 11 z "hola")))))

  (testing "Evaluar condicion con el camino de 'true' definido"
    (is (= '(9 (nil nil t t v 1 w 3 x 6)) (evaluar-if '(if t 9) '(nil nil t t v 1 w 3 x 6) '(x 5 y 11 z "hola"))))
    (is (= '(9 (nil nil t t v 1 w 3 x 6)) (evaluar-if '(if z 9) '(nil nil t t v 1 w 3 x 6) '(x 5 y 11 z "hola"))))
    (is (= '(9 (nil nil t t v 1 w 3 x 6)) (evaluar-if '(if w 9) '(nil nil t t v 1 w 3 x 6) '(x 5 y 11 z "hola"))))
    (is (= '(nil (nil nil t t v 1 w 3 x 6)) (evaluar-if '(if nil 9) '(nil nil t t v 1 w 3 x 6) '(x 5 y 11 z "hola")))))

  (testing "Evaluar condicion con el ambos caminos definidos"
    (is (= '("hola" (nil nil t t v 1 w 3 x 6)) (evaluar-if '(if nil 9 z) '(nil nil t t v 1 w 3 x 6) '(x 5 y 11 z "hola"))))
    ; para este caso se asme que el ultimo elemento es el valor de false
    (is (= '("hola" (nil nil t t v 1 w 3 x 6)) (evaluar-if '(if nil 9 1 2 3 z) '(nil nil t t v 1 w 3 x 6) '(x 5 y 11 z "hola"))))
    (is (= '(3 (nil nil t t v 1 w 3 x 6)) (evaluar-if '(if nil 9 w) '(nil nil t t v 1 w 3 x 6) '(x 5 y 11 z "hola"))))
    (is (= '(8 (nil nil t t v 1 w 3 x 6)) (evaluar-if '(if nil 9 8) '(nil nil t t v 1 w 3 x 6) '(x 5 y 11 z "hola"))))
    (is (= '(8 (nil nil t t v 1 w 3 x 6)) (evaluar-if '(if nil a 8) '(nil nil t t v 1 w 3 x 6) '(x 5 y 11 z "hola")))))

   ; user=> (evaluar-if '(if (gt 0 2) a (setq m 8)) '(gt gt nil nil t t v 1 w 3 x 6) '(x 5 y 11 z "hola"))
   ; (8 (gt gt nil nil t t v 1 w 3 x 6 m 8)))
  (testing "Evaluar con condicion compleja"
    (is (= '((*error* unbound-symbol a) (gt gt nil nil t t v 1 w 3 x 6)) (evaluar-if '(if (gt 2 0) a 8) '(gt gt nil nil t t v 1 w 3 x 6) '(x 5 y 11 z "hola"))))
    (is (= '(8 (gt gt nil nil t t v 1 w 3 x 6)) (evaluar-if '(if (gt 0 2) a 8) '(gt gt nil nil t t v 1 w 3 x 6) '(x 5 y 11 z "hola"))))))
