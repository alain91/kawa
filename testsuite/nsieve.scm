(define (nsieve (m :: int) (is-prime :: boolean[])) :: int
  (do ((i :: int 2 (+ i 1)))
      ((> i m) #!void)
    (set! (is-prime i) #t))
  (define count :: int 0)
  (do ((i :: int 2 (+ i 1)))
      ((> i m) count)
    (cond ((is-prime i)
	   (do ((k :: int (+ i i) (+ k i)))
	       ((> k m) count)
	       (set! (is-prime k) #f))
	   (set! count (+ count 1))))))

(define (test (n :: int))
  (if (< n 2) (set! n 2))
  (define m :: int (* (bitwise-arithmetic-shift 1 n) 10000))
  (define flags (make boolean[] length: (+ m 1)))
  (format #t "Primes up to ~8d~9d~%" m (nsieve m flags))
  (set! m (* (bitwise-arithmetic-shift 1 (- n 1)) 10000))
  (format #t "Primes up to ~8d~9d~%" m (nsieve m flags))
  (set! m (* (bitwise-arithmetic-shift 1 (- n 2)) 10000))
  (format #t "Primes up to ~8d~9d~%" m (nsieve m flags)))

(define args (cdr (command-line)))
(test (if (null? args) 0
	  (java.lang.Integer:parseInt (args 0))))