(ns tlclisp.core (:gen-class)
    (:require [tlclisp.interpreter :as interpreter]))

(interpreter/repl)