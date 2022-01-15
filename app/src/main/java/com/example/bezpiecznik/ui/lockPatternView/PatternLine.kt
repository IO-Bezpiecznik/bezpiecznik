package com.example.bezpiecznik.ui.lockPatternView

import kotlin.math.max
import kotlin.math.min

class PatternLine(
    startX: Int,
    startY: Int,
    endX: Int,
    endY: Int
) {
    var startPoint = MatrixPoint(startX,startY)
    var endPoint = MatrixPoint(endX,endY)

    fun onSegment(p: MatrixPoint, q:MatrixPoint, r: MatrixPoint): Boolean{
        if (r.x <= max(p.x, q.x) && r.x >= min(p.x, q.x) && r.y <= max(p.y, q.y) && r.y >= min(p.y, q.y)){
            return true
        }
        return false
    }

    private fun orientation(p: MatrixPoint, q:MatrixPoint, r: MatrixPoint): Int{
        val v = (((q.y - p.y) * (r.x - q.x)) - ((q.x - p.x) * (r.y - q.y)))
        return when {
            v == 0 -> {
                0
            }
            v > 0 -> {
                1
            }
            else -> {
                -1
            }
        }
    }

    fun dx(): Int{
        return endPoint.x - startPoint.x
    }

    fun dy(): Int{
        return endPoint.y - startPoint.y
    }

    fun intersects(line: PatternLine): Boolean{
        val o1 = orientation(startPoint, endPoint, line.startPoint)
        val o2 = orientation(startPoint, endPoint, line.endPoint)
        val o3 = orientation(line.startPoint, line.endPoint, startPoint)
        val o4 = orientation(line.startPoint, line.endPoint, endPoint)
        if (o1 != o2 && o3 != o4){
            return true
        }
        if(o1 == 0 && onSegment(startPoint, endPoint, line.startPoint)){
            return true
        }
        if(o2 == 0 && onSegment(startPoint, endPoint, line.endPoint)){
            return true
        }
        if(o3 == 0 && onSegment(line.startPoint, line.endPoint, startPoint)){
            return true
        }
        if(o4 == 0 && onSegment(line.startPoint, line.endPoint, endPoint)){
            return true
        }
        return false
    }

    override fun toString(): String {
        return "(${startPoint.x}, ${startPoint.y}) -- (${endPoint.x}, ${endPoint.y})"
    }
}