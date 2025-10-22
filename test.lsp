(def t (dambda (x) (do (print x) x)))
(def m (macro (a) (+ a a)))
(print (m (t 3)))