package com.example.bezpiecznik.ui.lockPatternView

class PatternLine(
    startX: Int,
    startY: Int,
    endX: Int,
    endY: Int
) {
    var startPoint = MatrixPoint(startX,startY)
    var endPoint = MatrixPoint(endX,endY)

    private fun ccw(A: MatrixPoint, B: MatrixPoint, C: MatrixPoint): Boolean{
        return (C.y-A.y) * (B.x-A.x) > (B.y-A.y) * (C.x-A.x)
    }

    fun intersects(line: PatternLine): Boolean{
        return (ccw(startPoint, line.startPoint, line.endPoint) != ccw(endPoint, line.startPoint, line.endPoint)
                && ccw(startPoint, endPoint, line.startPoint) != ccw(startPoint, endPoint, line.endPoint))
    }

    override fun toString(): String {
        return "(${startPoint.x}, ${startPoint.y}) -- (${endPoint.x}, ${endPoint.y})"
    }
}