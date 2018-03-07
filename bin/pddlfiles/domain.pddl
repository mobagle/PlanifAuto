;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(define (domain test)
    (:requirements :strips :typing)
    (:types posX posY)
    (:predicates
        (aPalet)
        (palet-is-on ?x - posX ?y - posY)
        (is-on ?x - posX ?y - posY)
        (depX ?x - posX ?y - posX)
        (depY ?x - posY ?y - posY))

(:action prendrePalet
    :parameters (?x - posX ?y - posY)
    :precondition (and (palet-is-on ?x ?y) (is-on ?x ?y))
    :effect 
    (and (aPalet) 
        (not (palet-is-on ?x ?y))))

(:action lacherPalet
    :parameters (?x - posX ?y - posY)
    :precondition  (and (aPalet) (is-on ?x ?y))
    :effect (palet-is-on ?x ?y))

(:action deplacementX1
    :parameters (?x - posX ?y - posX ?z - posY)
    :precondition (and (is-on ?x ?z) (depX ?x ?y))
    :effect
    (and(is-on ?y ?z)
        (not (is-on ?x ?z))))

(:action deplacementX2
    :parameters (?x - posX ?y - posX ?z - posY)
    :precondition (and (is-on ?x ?z) (depX ?y ?x))
    :effect
    (and(is-on ?y ?z)
        (not (is-on ?x ?z))))


(:action deplacementY1
    :parameters (?x - posY ?y - posY ?z - posX)
    :precondition (and (is-on ?z ?x) (depY ?x ?y))
    :effect
    (and(is-on ?z ?y)
        (not (is-on ?z ?x))))

(:action deplacementY2
    :parameters (?x - posY ?y - posY ?z - posX)
    :precondition (and (is-on ?z ?x) (depY ?y ?x))
    :effect
    (and(is-on ?z ?y)
        (not (is-on ?z ?x))))
        
)
