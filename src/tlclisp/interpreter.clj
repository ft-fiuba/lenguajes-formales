(ns tlclisp.interpreter)

(require
 '[clojure.string :refer [blank? ends-with? lower-case]]
 '[clojure.java.io :refer [reader]])

(defn spy
  ([x] (do (prn x) (prn) x))
  ([msg x] (do (print msg) (print ": ") (prn x) (prn) x)))

; Funciones principales
(declare repl)
(declare evaluar)
(declare aplicar)

; Funciones secundarias de evaluar
(declare evaluar-de)
(declare evaluar-if)
(declare evaluar-or)
(declare evaluar-cond)
(declare evaluar-eval)
(declare evaluar-exit)
(declare evaluar-load)
(declare evaluar-setq)
(declare evaluar-quote)
(declare evaluar-lambda)
(declare evaluar-escalar)

; Funciones secundarias de aplicar
(declare aplicar-lambda)
(declare aplicar-funcion-primitiva)

; Funciones primitivas
(declare fnc-ge)
(declare fnc-gt)
(declare fnc-lt)
(declare fnc-add)
(declare fnc-env)
(declare fnc-not)
(declare fnc-sub)
(declare fnc-cons)
(declare fnc-list)
(declare fnc-null)
(declare fnc-read)
(declare fnc-rest)
(declare fnc-equal)
(declare fnc-first)
(declare fnc-listp)
(declare fnc-prin3)
(declare fnc-append)
(declare fnc-length)
(declare fnc-terpri)
(declare fnc-reverse)

; Funciones auxiliares
(declare buscar)
(declare error?)
(declare igual?)
(declare imprimir)
(declare cargar-arch)
(declare revisar-fnc)
(declare revisar-lae)
(declare actualizar-amb)
(declare controlar-aridad)
(declare aplicar-lambda-simple)
(declare aplicar-lambda-multiple)
(declare evaluar-clausulas-en-cond)
(declare evaluar-secuencia-en-cond)


; REPL (read–eval–print loop).
; Aridad 0: Muestra mensaje de bienvenida y se llama recursivamente con el ambiente inicial.
; Aridad 1: Muestra >>> y lee una expresion y la evalua. El resultado es una lista con un valor y un ambiente. 
; Si la 2da. posicion del resultado es nil, devuelve true (caso base de la recursividad).
; Si no, imprime la 1ra. pos. del resultado y se llama recursivamente con la 2da. pos. del resultado. 
(defn repl
  "Inicia el REPL de TLC-LISP."
  ([]
   (println "Interprete de TLC-LISP en Clojure")
   (println "Trabajo Practico de 75.14/95.48 - Lenguajes Formales 2022")
   (println "Inspirado en:")
   (println "  TLC-LISP Version 1.51 for the IBM Personal Computer")
   (println "  Copyright (c) 1982, 1983, 1984, 1985 The Lisp Company") (flush)
   (repl '(add add append append cond cond cons cons de de env env equal equal
               eval eval exit exit first first ge ge gt gt if if lambda lambda
               length length list list listp listp load load lt lt nil nil
               not not null null or or prin3 prin3 quote quote read read
               rest rest reverse reverse setq setq sub sub t t terpri terpri
               + add - sub)))
  ([amb]
   (print ">>> ") (flush)
   (try
     (let [res (evaluar (read) amb nil)]  ; READ, EVAL
       (if (nil? (second res))
         true
         (do (imprimir (first res))     ; PRINT
             (repl (second res)))))     ; LOOP
     (catch Exception e
       (println) (print "*error* ")
       (println (get (Throwable->map e) :cause))
       (repl amb)))))


(defn evaluar
  "Evalua una expresion 'expre' en los ambientes global y local. Devuelve un lista con un valor resultante y un ambiente."
  [expre amb-global amb-local]
  (if (or (igual? expre nil)
          (and (seq? expre)
               (or (empty? expre) (error? expre)))) ; si 'expre' es nil, () o error, devolverla intacta
    (list expre amb-global)                         ; de lo contrario, evaluarla
    (cond
      (not (seq? expre))              (evaluar-escalar expre amb-global amb-local)
      (igual? (first expre) 'cond)    (evaluar-cond expre amb-global amb-local)
      (igual? (first expre) 'de)      (evaluar-de expre amb-global)
      (igual? (first expre) 'eval)    (evaluar-eval expre amb-global amb-local)
      (igual? (first expre) 'exit)    (evaluar-exit expre amb-global amb-local)
      (igual? (first expre) 'if)      (evaluar-if expre amb-global amb-local)
      (igual? (first expre) 'lambda)  (evaluar-lambda expre amb-global amb-local)
      (igual? (first expre) 'load)    (evaluar-load expre amb-global amb-local)
      (igual? (first expre) 'or)      (evaluar-or expre amb-global amb-local)
      (igual? (first expre) 'quote)   (evaluar-quote expre amb-global amb-local)
      (igual? (first expre) 'setq)    (evaluar-setq expre amb-global amb-local)

      ; Si la expresion no es la aplicacion de una funcion (es una forma especial, una macro...) debe ser evaluada aqui
      ; por una funcion de Clojure especifica debido a que puede ser necesario evitar la evaluacion de los argumentos 
      :else (let [res-eval-1 (evaluar (first expre) amb-global amb-local),
                  res-eval-2 (reduce (fn [x y] (let [res-eval-3 (evaluar y (first x) amb-local)] (cons (second res-eval-3) (concat (next x) (list (first res-eval-3)))))) (cons (list (second res-eval-1)) (next expre)))]
              (aplicar (first res-eval-1) (next res-eval-2) (first res-eval-2) amb-local)))))


; Evalua una macro COND. Siempre devuelve una lista con un resultado y un ambiente.
(defn evaluar-cond [expre amb-global amb-local]
  "Evalua una forma 'cond' en TLC-LISP."
  (evaluar-clausulas-en-cond (next expre) amb-global amb-local))


(defn evaluar-clausulas-en-cond [expre amb-global amb-local]
  "Une 'evaluar-cond' con 'evaluar-secuencia-en-cond'."
  (if (nil? expre)
    (list nil amb-global)
    (let [res-eval (evaluar (ffirst expre) amb-global amb-local)]
      (cond
        (error? (first res-eval)) res-eval
        (not (igual? (first res-eval) nil)) (evaluar-secuencia-en-cond (nfirst expre) (second res-eval) amb-local)
        :else (recur (next expre) (second res-eval) amb-local)))))


; Evalua (con evaluar) secuencialmente las sublistas de una lista y devuelve el valor de la ultima evaluacion.
; Si alguna evaluacion devuelve un error, sera la ultima que se evalue. 
(defn evaluar-secuencia-en-cond [lis amb-global amb-local]
  (if (nil? (next lis))
    (evaluar (first lis) amb-global amb-local)
    (let [res-eval (evaluar (first lis) amb-global amb-local)]
      (if (error? (first res-eval))
        res-eval
        (recur (next lis) (second res-eval) amb-local)))))


(defn evaluar-eval
  "Evalua una forma 'eval' en TLC-LISP."
  [expre amb-global amb-local]
  (let [ari (controlar-aridad (next expre) 1)]
    (cond
      (seq? ari) ari
      (and (seq? (second expre)) (igual? (first (second expre)) 'quote)) (evaluar (second (second expre)) amb-global amb-local)
      :else (evaluar (second expre) amb-global amb-local))))


(defn evaluar-exit
  "Sale del interprete de TLC-LISP."
  [expre amb-global _]
  (cond
    (< (count (next expre)) 1) (list nil nil)
    :else (list (list '*error* 'too-many-args) amb-global)))


(defn evaluar-lambda
  "Evalua una forma 'lambda' en TLC-LISP."
  [expre amb-global _]
  (cond
    (< (count (next expre)) 1) (list (list '*error* 'list 'expected nil) amb-global)
    (and (not (igual? (second expre) nil)) (not (seq? (second expre))))
    (list (list '*error* 'list 'expected (second expre)) amb-global)
    :else (list expre amb-global)))


(defn evaluar-load
  "Evalua una forma 'load' en TLC-LISP. Carga en el ambiente un archivo 'expre' con código en TLC-LISP."
  [expre amb-global amb-local]
  (cond
    (< (count (next expre)) 1) (list (list '*error* 'too-few-args) amb-global)
    (> (count (next expre)) 1) (list (list '*error* 'not-implemented) amb-global)
    :else (list \space (cargar-arch amb-global amb-local (second expre)))))


(defn cargar-arch
  ([amb-global amb-local arch]
   (let [nomb (first (evaluar arch amb-global amb-local))]
     (if (error? nomb)
       (do (imprimir nomb) amb-global)
       (let [nm (clojure.string/lower-case (str nomb)),
             nom (if (and (> (count nm) 4) (clojure.string/ends-with? nm ".lsp")) nm (str nm ".lsp")),
             ret (try (with-open [in (java.io.PushbackReader. (clojure.java.io/reader nom))]
                        (binding [*read-eval* false] (try (let [res (evaluar (read in) amb-global nil)]
                                                            (cargar-arch (second res) nil in res))
                                                          (catch Exception e (imprimir nil) amb-global))))
                      (catch java.io.FileNotFoundException e (imprimir (list '*error* 'file-open-error 'file-not-found nom '1 'READ)) amb-global))]
         ret))))
  ([amb-global amb-local in res]
   (try (let [res (evaluar (read in) amb-global nil)] (cargar-arch (second res) nil in res))
        (catch Exception e (imprimir (first res)) amb-global))))


(defn evaluar-quote
  "Evalua una forma 'quote' de TLC-LISP."
  [expre amb-global _]
  (if (igual? (second expre) nil)
    (list nil amb-global)
    (list (second expre) amb-global)))


(defn aplicar
  "Aplica a la lista de argumentos 'lae' la función 'fnc' en los ambientes dados."
  ([fnc lae amb-global amb-local]
   (aplicar (revisar-fnc fnc) (revisar-lae lae) fnc lae amb-global amb-local))
  ([resu1 resu2 fnc lae amb-global amb-local]
   (cond
     (error? resu1) (list resu1 amb-global)
     (error? resu2) (list resu2 amb-global)
     (not (seq? fnc)) (list (aplicar-funcion-primitiva fnc lae amb-global amb-local) amb-global)
     :else (aplicar-lambda fnc lae amb-global amb-local))))


(defn aplicar-lambda
  "Aplica la forma lambda 'fnc' a la lista de argumentos 'lae'."
  [fnc lae amb-global amb-local]
  (cond
    (< (count lae) (count (second fnc))) (list '(*error* too-few-args) amb-global)
    (> (count lae) (count (second fnc))) (list '(*error* too-many-args) amb-global)
    (nil? (next (nnext fnc))) (aplicar-lambda-simple fnc lae amb-global amb-local)
    :else (aplicar-lambda-multiple fnc lae amb-global amb-local)))


(defn aplicar-lambda-simple
  "Evalua una forma lambda 'fnc' con un cuerpo simple."
  [fnc lae amb-global amb-local]
  (let [lista-params-args (reduce concat (map list (second fnc) lae)),
        nuevo-amb-local (cond
                          (empty? amb-local) lista-params-args
                          (empty? lista-params-args) amb-local
                          :else (apply concat (apply assoc (apply assoc {} amb-local) lista-params-args)))]
    (evaluar (first (nnext fnc)) amb-global nuevo-amb-local)))



(defn aplicar-lambda-multiple
  "Evalua una forma lambda 'fnc' cuyo cuerpo contiene varias expresiones."
  [fnc lae amb-global amb-local]
  (aplicar (cons 'lambda (cons (second fnc) (next (nnext fnc))))
           lae
           (second (aplicar-lambda-simple fnc lae amb-global amb-local))  ; Nuevo ambiente global
           amb-local))


(defn aplicar-funcion-primitiva
  "Aplica una funcion primitiva a una 'lae' (lista de argumentos evaluados)."
  [fnc lae amb-global amb-local]
  (cond
    (igual? fnc 'add)     (fnc-add lae)
    (igual? fnc 'append)  (fnc-append lae)
    (igual? fnc 'cons)    (fnc-cons lae)
    (igual? fnc 'equal)   (fnc-equal lae)
    (igual? fnc 'env)     (fnc-env lae amb-global amb-local)
    (igual? fnc 'first)   (fnc-first lae)
    (igual? fnc 'ge)      (fnc-ge lae)
    (igual? fnc 'gt)      (fnc-gt lae)
    (igual? fnc 'length)  (fnc-length lae)
    (igual? fnc 'list)    (fnc-list lae)
    (igual? fnc 'listp)   (fnc-listp lae)
    (igual? fnc 'lt)      (fnc-lt lae)
    (igual? fnc 'not)     (fnc-not lae)
    (igual? fnc 'null)    (fnc-null lae)
    (igual? fnc 'prin3)   (fnc-prin3 lae)
    (igual? fnc 'read)    (fnc-read lae)
    (igual? fnc 'rest)    (fnc-rest lae)
    (igual? fnc 'reverse) (fnc-reverse lae)
    (igual? fnc 'sub)     (fnc-sub lae)
    (igual? fnc 'terpri)  (fnc-terpri lae)
    ; Las funciones primitivas reciben argumentos y retornan un valor (son puras)

    :else (list '*error* 'non-applicable-type fnc)))


(defn fnc-cons
  "Devuelve la inserción de un elem en la cabeza de una lista."
  [lae]
  (let [ari (controlar-aridad lae 2)]
    (cond
      (seq? ari) ari
      (or (seq? (second lae)) (igual? (second lae) nil)) (cons (first lae) (second lae))
      :else (list '*error* 'not-implemented))))


(defn fnc-first
  "Devuelve el primer elemento de una lista."
  [lae]
  (let [ari (controlar-aridad lae 1)]
    (cond
      (seq? ari) ari
      (igual? (first lae) nil) nil
      (not (seq? (first lae))) (list '*error* 'list 'expected (first lae))
      :else (ffirst lae))))


(defn fnc-length
  "Devuelve la longitud de una lista."
  [lae]
  (let [ari (controlar-aridad lae 1)]
    (cond
      (seq? ari) ari
      (or (seq? (first lae)) (igual? (first lae) nil)) (count (first lae))
      :else (list '*error* 'arg-wrong-type (first lae)))))


(defn fnc-list
  "Devuelve una lista formada por los args."
  [lae]
  (if (< (count lae) 1) nil lae))


(defn fnc-listp
  "Devuelve 't' si un elemento es una lista."
  [lae]
  (let [ari (controlar-aridad lae 1)]
    (cond
      (seq? ari) ari
      (seq? (first lae)) 't
      :else nil)))


(defn fnc-not
  "Niega el argumento."
  [lae]
  (let [ari (controlar-aridad lae 1)]
    (cond
      (seq? ari) ari
      (igual? (first lae) nil) 't
      :else nil)))


(defn fnc-null
  "Devuelve 't' si un elemento es 'nil' en TLC-Lisp."
  [lae]
  (fnc-not lae))


(defn fnc-prin3
  "Imprime un elemento y lo devuelve."
  [lae]
  (cond
    (< (count lae) 1) (list '*error* 'too-few-args)
    (> (count lae) 1) (list '*error* 'not-implemented)
    (not (seq? (first lae))) (do (print (first lae)) (flush) (first lae))
    :else (do (print (map #(if (igual? % nil) nil %) (first lae))) (flush) (first lae))))


(defn fnc-rest
  "Devuelve una lista sin su 1ra. posición."
  [lae]
  (let [ari (controlar-aridad lae 1)]
    (cond
      (seq? ari) ari
      (igual? (first lae) nil) nil
      (not (seq? (first lae))) (list '*error* 'list 'expected (first lae))
      :else (nfirst lae))))


(defn imprimir
  "Imprime, con un salto de linea al final, lo recibido devolviendo 
    el mismo valor. Tambien muestra los errores."
  ([elem]
   (cond
     (not (seq? elem)) (if (igual? elem \space)
                         (do (flush) elem)
                         (do (prn (if (igual? elem nil) nil elem)) (flush) elem))
     (error? elem) (imprimir elem elem)
     :else (do (prn (map #(if (igual? % nil) nil %) elem)) (flush) elem)))
  ([lis orig]
   (if (nil? lis)
     (do (prn) (flush) orig)
     (do (pr (first lis)) (print " ") (imprimir (next lis) orig)))))


; FUNCIONES QUE DEBEN SER IMPLEMENTADAS PARA COMPLETAR EL INTERPRETE DE TLC-LISP
; (ADEMAS DE COMPLETAR 'EVALUAR' Y 'APLICAR-FUNCION-PRIMITIVA'):


;; ------------------------------------------------------------------------------------------------ 
;; -----------------------------------------AXULIAR------------------------------------------------ 
;; ------------------------------------------------------------------------------------------------
(defn -build-error
  ([msg] (list '*error* msg))
  ([msg, value] (list '*error* msg value)))



(defn controlar-aridad
  "Si la longitud de una lista dada es la esperada, devuelve esa longitud.
   Si no, devuelve una lista con un mensaje de error (una lista con *error* como primer elemento)."
  [L, n]
  (let [real-length (count L)]
    (cond
      (> real-length n) (list "*error*" "too-many-args")
      (< real-length n) (list "*error*" "too-few-args")
      :else n)))



(defn -is-lisp-nil?
  "Devuelte true si el valor es equlivalente a nil en TLC-LISP"
  [n]
  (or (nil? n) (= n '()) (= n 'NIL)))

(defn igual?
  "Verifica la igualdad entre dos elementos al estilo de TLC-LISP (case-insensitive)."
  [a, b]
  (cond
    (-is-lisp-nil? a) (-is-lisp-nil? b)
    (and (list? a) (list? b)) (reduce (fn [i,j] (and i j)) (map igual? a b))
    (and (symbol? a) (symbol? b)) (= (lower-case (str a)) (lower-case (str b)))
    (and (string? a) (symbol? b)) (= (lower-case (str a)) (lower-case (str b)))
    (and (symbol? a) (string? b)) (= (lower-case (str a)) (lower-case (str b)))
    :else (= a b)))



(defn -first-or-nil
  "Devuelve el primer elemento de la lista. Si esta vacia, devuelve nil"
  [L]
  (cond (empty? L) nil :else (first L)))

(defn error?
  "Devuelve true o false, segun sea o no el arg. un mensaje de error 
  (una lista con *error* como primer elemento)."
  [L]
  (cond
    (not (list? L)) false
    (igual? (-first-or-nil L) '*error*) true))



(defn revisar-fnc
  "Si la lista es un mensaje de error, lo devuelve; si no, devuelve nil."
  [L]
  (cond (error? L) L :else nil))



(defn revisar-lae
  "Devuelve el primer elemento que es un mensaje de error. Si no hay ninguno, devuelve nil."
  [L]
  (-first-or-nil (filter error? L)))




(defn --env-keys [A]
  (map (fn [t] (nth t 1))
       (filter (fn [t] (odd? (nth t 0))) (map list (range 1 (inc (count A))) A))))

(defn --env-vals [A]
  (map (fn [t] (nth t 1))
       (filter (fn [t] (even? (nth t 0))) (map list (range 1 (inc (count A))) A))))

(defn --keyval-has-key [T, key]
  (igual? key (nth T 0)))

(defn -env-to-keyval-tuples [A]
  (map list (--env-keys A) (--env-vals A)))

(defn -get-keyval-from-env-with-key [A, key]
  (-first-or-nil
   (filter (fn [t] (--keyval-has-key t key)) (-env-to-keyval-tuples A))))

(defn buscar
  "Busca una clave en un ambiente (una lista con claves en las posiciones
   impares [1, 3, 5...] y valores en las pares [2, 4, 6...] y devuelve el 
   valor asociado. Devuelve un mensaje de error si no la encuentra."
  [key, A]
  (let [encontrado (-get-keyval-from-env-with-key A key)]
    (cond (some? encontrado) (nth encontrado 1)
          :else (-build-error 'unbound-symbol key))))



(defn -key-exists-in-env [A key]
  (some? (-get-keyval-from-env-with-key A key)))

(defn -replace-if-keyval-is-from-key [T key new-value]
  (cond (--keyval-has-key T key) (list key new-value) :else T))

(defn actualizar-amb
  "Devuelve un ambiente actualizado con una clave (nombre de la variable o funcion) y su valor. 
  Si el valor es un error, el ambiente no se modifica. De lo contrario, se le carga o reemplaza el valor."
  [A key value]
  (cond
    (error? value) A
    (not (-key-exists-in-env A key)) (concat A (list key value))
    :else (flatten (map (fn [t] (-replace-if-keyval-is-from-key t key value)) (-env-to-keyval-tuples A)))))
;; ------------------------------------------------------------------------------------------------
;; ------------------------------------------------------------------------------------------------
;; ------------------------------------------------------------------------------------------------


;; ------------------------------------------------------------------------------------------------ 
;; -----------------------------------------PRIMITIVAS--------------------------------------------- 
;; ------------------------------------------------------------------------------------------------
(defn -check-min-args [args min-expected]
  (cond
    (> min-expected (count args)) (-build-error 'too-few-args)
    :else nil))

(defn -check-num-args [args expected]
  (cond
    (> (count args) expected) (-build-error 'too-many-args)
    (< (count args) expected) (-build-error 'too-few-args)
    :else nil))

(defn -check-is-empty-arg [arg]
  (cond
    (seq arg) (-build-error 'not-facu) :else nil))



(defn fnc-append
  "Devuelve el resultado de fusionar 2 sublistas."
  [args]
  (let [args-error (-check-num-args args 2)]
    (cond
      (some? args-error) args-error
      ; Returns a seq on the collection. 
      ; If the collection is  empty, returns nil.
      :else (seq (concat (nth args 0) (nth args 1))))))



(defn fnc-equal
  "Compara 2 elementos. Si son iguales, devuelve t. Si no, nil."
  [args]
  (let [args-error (-check-num-args args 2)]
    (cond
      (some? args-error) args-error
      :else (cond (igual? (nth args 0) (nth args 1)) 't :else nil))))



(defn fnc-read
  "Devuelve la lectura de un elemento de TLC-LISP desde la terminal/consola."
  [args]
  (let [args-error (-check-is-empty-arg args)]
    (cond
      (some? args-error) args-error
      :else (let [val (read)] (cond (-is-lisp-nil? val) nil :else val)))))



(defn fnc-env
  "Devuelve la fusion de los ambientes global y local."
  [arg local-env global-env]
  (let [args-error (-check-is-empty-arg arg)]
    (cond
      ; return another error instead of default
      (some? args-error) (-build-error 'too-many-args)
      :else (fnc-append (list local-env global-env)))))



(defn fnc-terpri
  "Imprime un salto de línea y devuelve nil."
  [args]
  (let [args-error (-check-is-empty-arg args)]
    (cond
      (some? args-error) args-error :else (println))))



(defn -first-not-number [L]
  (first (filter (fn [e] (not (number? e))) L)))

(defn fnc-add
  "Suma los elementos de una lista. Minimo 2 elementos."
  [args]
  (let [args-error (-check-min-args args 2)]
    (cond
      (some? args-error) args-error
      (not-every? number? args) (-build-error 'number-expected (-first-not-number args))
      :else (reduce + args))))




(defn fnc-sub
  "Resta los elementos de un lista. Minimo 1 elemento."
  [args]
  (let [args-error (-check-min-args args 1)]
    (cond
      (some? args-error) args-error
      (not-every? number? args) (-build-error 'number-expected (-first-not-number args))
      (= 1 (count args)) (- (nth args 0))
      :else (reduce - args))))



(defn fnc-lt
  "Devuelve t si el primer numero es menor que el segundo; si no, nil."
  [args]
  (let [args-error (-check-num-args args 2)]
    (cond
      (some? args-error) args-error
      (not (number? (nth args 0))) (-build-error 'number-expected (nth args 0))
      (not (number? (nth args 1))) (-build-error 'number-expected (nth args 1))
      :else (cond (< (nth args 0) (nth args 1)) 't :else nil))))



(defn fnc-gt
  "Devuelve t si el primer numero es mayor que el segundo; si no, nil."
  [args]
  (let [args-error (-check-num-args args 2)]
    (cond
      (some? args-error) args-error
      (not (number? (nth args 0))) (-build-error 'number-expected (nth args 0))
      (not (number? (nth args 1))) (-build-error 'number-expected (nth args 1))
      :else (cond (> (nth args 0) (nth args 1)) 't :else nil))))



(defn fnc-ge
  "Devuelve t si el primer numero es mayor o igual que el segundo; si no, nil."
  [args]
  (let [args-error (-check-num-args args 2)]
    (cond
      (some? args-error) args-error
      (not (number? (nth args 0))) (-build-error 'number-expected (nth args 0))
      (not (number? (nth args 1))) (-build-error 'number-expected (nth args 1))
      :else (cond (>= (nth args 0) (nth args 1)) 't :else nil))))



(defn fnc-reverse
  "Devuelve una lista con sus elementos en orden inverso."
  [args]
  (let [args-error (-check-num-args args 1)]
    (cond
      (some? args-error) args-error
      (not (list? (nth args 0))) (-build-error 'list-expected (nth args 0))
      :else (reverse (nth args 0)))))
;; ------------------------------------------------------------------------------------------------
;; ------------------------------------------------------------------------------------------------
;; ------------------------------------------------------------------------------------------------





;; ------------------------------------------------------------------------------------------------
;; ---------------------------------------EVALS----------------------------------------------------
;; ------------------------------------------------------------------------------------------------
(defn evaluar-escalar
  "Evalua una expresion escalar consultando, si corresponde, 
   los ambientes local y global. Devuelve una lista con el resultado 
   y un ambiente."
  [e, global-env, local-env]
  (cond
    (symbol? e) (let [val-in-local (buscar e local-env)
                      val-in-global (buscar e global-env)]
                  (cond
                    ; local-env has priority ver global-env
                    (not (error? val-in-local)) (list val-in-local global-env)
                    (not (error? val-in-global)) (list val-in-global global-env)
                    :else (list (-build-error 'unbound-symbol e) global-env)))

    :else (list e global-env)))



(defn -add-lambda-to-func-body [func-body]
  (conj func-body 'lambda))

(defn -check-func-def [func]
  (let [func-name (cond (> (count func) 1) (nth func 1) :else nil)
        func-params (cond (> (count func) 2) (nth func 2) :else nil)]
    (cond
      (and (nil? func-name) (> (count func) 1)) (-build-error 'cannot-set nil)
      (nil? func-name) (-build-error 'list-expected nil)
      (nil? func-params) (-build-error 'list-expected nil)
      (not (list? func-params)) (-build-error 'list-expected func-params)
      (not (symbol? func-name)) (-build-error 'symbol-expected func-name)
      :else nil)))

(defn evaluar-de
  "Evalua una forma 'de'. Devuelve una lista con el resultado y un ambiente actualizado con la definicion."
  [func env]
  (let [func-def-error (-check-func-def func)]
    (cond
      (some? func-def-error) (list func-def-error env)
      :else (let [func-name (nth func 1),
                  func-body (rest (rest func))]
              (list func-name (actualizar-amb env func-name (-add-lambda-to-func-body func-body)))))))



(defn evaluar-if
  "Evalua una forma 'if'. Devuelve una lista con el resultado y un ambiente eventualmente modificado."
  [pred global-env local-env]
  (let [aux-pred   (rest pred) ; remove 'if from pred 
        condition  (first aux-pred)
        true-path  (second aux-pred)
        ; si al mentos tiene la forma de if (cond) a b
        false-path (cond (> (count aux-pred) 2) (last aux-pred) :else nil)
        result-condition (first (evaluar condition global-env local-env))]

    (cond
      (igual? nil result-condition) (evaluar false-path global-env local-env)
      :else (evaluar true-path global-env local-env))))



(defn -evaluar-or-rec
  [pred amb-global local-env]
  (let [res (evaluar (first pred) amb-global local-env)]
    (cond
      (empty? pred) (list nil (second res))
      (first res) res
      :else (-evaluar-or-rec (rest pred) (second res) local-env))))

(defn evaluar-or
  "Evalua una forma 'or'. Devuelve una lista con el resultado y un ambiente."
  [pred global-env local-env]
  (-evaluar-or-rec (rest pred) global-env local-env))



(defn _evaluar-setq-rec [pred global-env local-env]
  (cond
    (> 2 (count pred)) (list (-build-error 'list-expected nil) global-env)
    :else (let [var-name  (nth pred 0)
                var-value (first (evaluar (nth pred 1) global-env local-env))]
            (cond
              (nil? var-name) (list (-build-error 'cannot-set nil) global-env)
              (not (symbol? var-name)) (list (-build-error 'symbol-expected var-name) global-env)
              (= 2 (count pred)) (list var-value (actualizar-amb global-env var-name var-value))
              :else (_evaluar-setq-rec (nthnext pred 2) (actualizar-amb global-env var-name var-value) local-env)))))

(defn evaluar-setq
  "Evalua una forma 'setq'. Devuelve una lista con el resultado y un ambiente actualizado."
  [pred global-env local-env]
  (_evaluar-setq-rec (rest pred) global-env local-env))
; Al terminar de cargar el archivo en el REPL de Clojure (con load-file), se debe devolver true.
;; ------------------------------------------------------------------------------------------------
;; ------------------------------------------------------------------------------------------------
;; ------------------------------------------------------------------------------------------------







