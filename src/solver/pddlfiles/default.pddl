(define (problem hanoi-tower)
    (:domain hanoi)
    (:objects
        x0 x1 x2 x3 x4 x5 x6 x7 x8 x9 x10 x11 x12 - posX
        y0 y1 y2 y3 y4 y5 y6 y7 y8 y9 y10 y11 y12 - posY)
    (:init         
        (depX x0 x1) (depY y0 y1)
        (depX x1 x2) (depY y1 y2)
        (depX x2 x3) (depY y2 y3)
        (depX x3 x4) (depY y3 y4)
        (depX x4 x5) (depY y4 y5)
        (depX x5 x6) (depY y5 y6)
        (depX x6 x7) (depY y6 y7)
        (depX x7 x8) (depY y7 y8)
        (depX x8 x9) (depY y8 y9)
        (depX x9 x10) (depY y9 y10)
        (depX x10 x11) (depY y10 y11)
        (depX x11 x12) (depY y11 y12)
        (peut-lacher x0 y12)
        (peut-lacher x1 y12)
        (peut-lacher x2 y12)
        (peut-lacher x3 y12)
        (peut-lacher x4 y12)
        (peut-lacher x5 y12)
        (peut-lacher x6 y12)
        (peut-lacher x7 y12)
        (peut-lacher x8 y12)
        (peut-lacher x9 y12)
        (peut-lacher x10 y12)
        (peut-lacher x11 y12)
        (peut-lacher x12 y12)
        (is-on x0 y0)
        (palet-is-on x3 y3)
        (palet-is-on x3 y6)
        (palet-is-on x3 y9)
        (palet-is-on x6 y3)
        (palet-is-on x6 y6)
        (palet-is-on x6 y9)
        (palet-is-on x9 y3)
        (palet-is-on x9 y6)
        (palet-is-on x9 y9)
        )
    (:goal (aPosePalet)
    ))
