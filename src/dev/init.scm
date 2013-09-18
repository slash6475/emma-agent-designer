;;;;;;;;;;;;;;;;;;;;;;;
;;;
;;; simple pattern matching, tested with Kawa 1.9.1
;;; Mariusz Nowostawski <mariusz@nowostawski.org>
;;;
;;; 2007-06-18
;;;
;;; use: 
;;; ;;  define some data
;;; (define data '(person (name Marcos) (surname Oliveira) (age 33)))
;;; ;;  perform the matching-unification, and keep the binding in b
;;; (define b
;;;    (match '(person (name ?name) (surname ?surname) (age ?age)) data)) 
;;; ;;  get appropriate binding values
;;; (var '?age b)
;;; (var '?name b)
;;; (var '?surname b)
;;; 
;;; for multiple datum and multiple bindings use mmatch and mvar accordingly.
;;; 
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;



;; lets define boolean constants

(define false '#f)
(define true '#t)



;;; query syntax extensions

(define (tagged-list? exp tag)
  (if (pair? exp)
      (eq? (car exp) tag)
      false))

(define (var? exp) (tagged-list? exp '?))

(define (query-syntax-process exp)
  (map-over-symbols expand-question-mark exp))

(define (map-over-symbols proc exp)
  (cond ((pair? exp)
         (cons (map-over-symbols proc (car exp))
               (map-over-symbols proc (cdr exp))))
        ((symbol? exp) (proc exp))
        (else exp)))

(define (expand-question-mark symbol)
  (let ((chars (symbol->string symbol)))
    (if (string=? (substring chars 0 1) "?")
        (list '?
              (string->symbol
               (substring chars 1 (string-length chars))))
        symbol)))


;;; bindings 

(define (make-binding variable value)
  (cons variable value))

(define (binding-variable binding)
  (car binding))

(define (binding-value binding)
  (cdr binding))


(define (binding-in-frame variable frame)
  (assoc variable frame))

(define (extend variable value frame)
  (cons (make-binding variable value) frame))



;;; Unification and patterns

(define (unify-match p1 p2 frame)
  (cond ((eq? frame false) false)
        ((equal? p1 p2) frame)
        ((var? p1) (extend-if-possible p1 p2 frame))
        ((var? p2) (extend-if-possible p2 p1 frame))
        ((and (pair? p1) (pair? p2))
         (unify-match (cdr p1)
                      (cdr p2)
                      (unify-match (car p1)
                                   (car p2)
                                   frame)))
        (else false)))

(define (extend-if-possible var val frame)
  (let ((binding (binding-in-frame var frame)))
    (cond (binding
           (unify-match
            (binding-value binding) val frame))
          ((var? val)
           (let ((binding (binding-in-frame val frame)))
             (if binding
                 (unify-match
                  var (binding-value binding) frame)
                 (extend var val frame))))
          ((depends-on? val var frame)    ; {\em ; ***}
           'failed)
          (else (extend var val frame)))))

(define (depends-on? exp var frame)
  (define (tree-walk e)
    (cond ((var? e)
           (if (equal? var e)
               true
               (let ((b (binding-in-frame e frame)))
                 (if b
                     (tree-walk (binding-value b))
                     false))))
          ((pair? e)
           (or (tree-walk (car e))
               (tree-walk (cdr e))))
          (else false)))
  (tree-walk exp))

(define (pattern-match pat dat frame)
  (cond ((eq? frame 'failed) 'failed)
        ((equal? pat dat) frame)
        ((var? pat) (extend-if-consistent pat dat frame))
        ((and (pair? pat) (pair? dat))
         (pattern-match (cdr pat)
                        (cdr dat)
                        (pattern-match (car pat)
                                       (car dat)
                                       frame)))
        (else 'failed)))

(define (extend-if-consistent var dat frame)
  (let ((binding (binding-in-frame var frame)))
    (if binding
        (pattern-match (binding-value binding) dat frame)
        (extend var dat frame))))



;; single pattern single data matching

(define match
  (lambda (pat dat)
    (let ((f '()))
      (unify-match (query-syntax-process pat) dat f))))


(define var
  (lambda (variable f)
    (cdr (assoc (expand-question-mark variable) f))))


;; single pattern, multiple data matching

(define mmatch
  (lambda (pat dats)
    (if (null? dats) 
	'()
	(let ((f (match pat (car dats))))
	  (if (eq? f false)
	      (mmatch pat (cdr dats))
	      (let ((g (mmatch pat (cdr dats))))
		(if (null? g)
		    f
		    (list f g))))))))

(define mvar
  (lambda (variable mf)
    (if (null? mf)
	'()
	(let ((b (var variable (car mf))))
	  (if (eq? b false)
	      (mvar variable (cdr mf))
	      (let ((g (mvar variable (cdr mf))))
		(if (null? g)
		    b
		    (list b g))))))))


