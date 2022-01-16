package com.example.bezpiecznik.ui.lockPatternView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.GridLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.MutableLiveData
import com.example.bezpiecznik.R
import com.example.bezpiecznik.data.AppDatabase_Impl
import org.json.JSONObject
import java.lang.Math.abs
import kotlin.math.floor

class LockPatternView(context: Context, attributeSet: AttributeSet) :
    GridLayout(context, attributeSet) {

    companion object{
        const val DEFAULT_RADIUS_RATIO = 0.25f
        const val DEFAULT_LINE_WIDTH = 2f // dp
        const val DEFAULT_SPACING = 6f // dp
        const val DEFAULT_ROW_AMOUNT = 3
        const val DEFAULT_COLUMN_AMOUNT = 3
        const val DEFAULT_HIT_AREA_PADDING_RATIO = 0.14f

        const val INTERSECTION_POINTS = 3
        const val KNIGHT_TURN_POINTS = 3
        const val NEW_DOT_POINTS = 1
        const val RADIUS_POINTS=0.5
        const val STARTPOINT_POINTS=-5
    }

    private var normalDotColor: Int = 0
    private var normalDotRadiusRatio: Float = 0f

    private var selectedDotColor: Int = 0
    private var selectedDotRadiusRatio: Float = 0f

    private var emptyDotColor: Int = 0

    private var lineWidth: Int = 0
    private var normalLineColor: Int = 0

    private var spacing: Int = 0

    private var rowAmount: Int = 0
    private var colAmount: Int = 0

    private var hitAreaPaddingRatio: Float = 0f

    private var dots: ArrayList<Dot> = ArrayList()
    private var selectedDots: ArrayList<Dot> = ArrayList()

    private var linePaint: Paint = Paint()
    private var linePath: Path = Path()

    private var lastX: Float = 0f
    private var lastY: Float = 0f

    private var onPatternListener: OnPatternListener? = null

    private var _gridPoints: List<List<Int>> = listOf(
        listOf(1,1,1),
        listOf(1,1,1),
        listOf(1,1,1)
    )
    private var allLines: MutableList<PatternLine> = mutableListOf()
    private var isFinished: Boolean = false
    private var isCleared: Boolean = false
    private var _patternString: String = ""

    val score: MutableLiveData<Int> = MutableLiveData()
    private var _score: Int = 0

    var startpoint_tmp=true



    init {
        normalDotColor = ContextCompat.getColor(context, R.color.normalColor)
        normalDotRadiusRatio = DEFAULT_RADIUS_RATIO
        selectedDotColor = ContextCompat.getColor(context, R.color.selectedColor)
        selectedDotRadiusRatio = DEFAULT_RADIUS_RATIO
        emptyDotColor = ContextCompat.getColor(context,R.color.emptyColor)
        lineWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_LINE_WIDTH, context.resources.displayMetrics).toInt()
        normalLineColor = ContextCompat.getColor(context, R.color.selectedColor)
        spacing = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_SPACING, context.resources.displayMetrics).toInt()
        rowAmount =  DEFAULT_ROW_AMOUNT
        colAmount = DEFAULT_COLUMN_AMOUNT
        hitAreaPaddingRatio =  DEFAULT_HIT_AREA_PADDING_RATIO
        rowCount = rowAmount
        columnCount = colAmount
        placeDots()
        initPathPaint()
    }

    private fun placeDots(){
        for(i in 0 until rowCount) {
            for(j in 0 until columnCount){
                if(_gridPoints[i][j] == 0){
                    val dot = Dot(context, i * columnCount + j,
                        emptyDotColor, normalDotRadiusRatio,
                        selectedDotColor, selectedDotRadiusRatio, columnCount, rowCount)
                    val padding = spacing / 2
                    dot.setPadding(padding, padding, padding, padding)
                    addView(dot)
                } else {
                    val dot = Dot(context, i * columnCount + j,
                        normalDotColor, normalDotRadiusRatio,
                        selectedDotColor, selectedDotRadiusRatio, columnCount, rowCount)
                    val padding = spacing / 2
                    dot.setPadding(padding, padding, padding, padding)
                    addView(dot)
                    dots.add(dot)
                }
            }
        }

        // points for grid size
        addPoints((dots.count() * RADIUS_POINTS).toInt())
    }

    private fun initPathPaint() {
        linePaint.isAntiAlias = true
        linePaint.isDither = true
        linePaint.style = Paint.Style.STROKE
        linePaint.strokeJoin = Paint.Join.ROUND
        linePaint.strokeCap = Paint.Cap.ROUND

        linePaint.strokeWidth = lineWidth.toFloat()
        linePaint.color = normalLineColor
    }

    private fun isSelected(view: View, x: Int, y: Int): Boolean{
        val innerPadding = view.width * hitAreaPaddingRatio
        return x >= view.left + innerPadding &&
                x <= view.right - innerPadding &&
                y >= view.top + innerPadding &&
                y <= view.bottom - innerPadding
    }

    private fun getHitDot(x: Int, y: Int): Dot? {
        for(dot in dots) {
            if (isSelected(dot, x, y)) {
                return dot
            }
        }
        return null
    }

    private fun notifyDotSelected(dot: Dot){
        selectedDots.add(dot)
        addPoints(NEW_DOT_POINTS)

        //startpoint points
        if (startpoint_tmp==true)
        {
            startpoint_tmp=false
            if(dot.index%columnCount==0&&dot.index/rowCount==0)
            {

                addPoints(STARTPOINT_POINTS)

            }

        }



        onPatternListener?.onProgress(generateSelectedIds())
        dot.setState(State.SELECTED)
        val center = dot.getCenter()
        if(selectedDots.count() == 1){
            linePath.moveTo(center.x.toFloat(), center.y.toFloat())
        } else {
            linePath.lineTo(center.x.toFloat(),center.y.toFloat())
            val startDot: Dot = selectedDots[selectedDots.count() - 2]
            val startPoint = MatrixPoint(startDot.index % columnCount, startDot.index / columnCount)
            val endPoint = MatrixPoint(dot.index % columnCount, dot.index /columnCount)
            val newLine = PatternLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y)
            var isconeccted=false
            if(allLines.count() > 0){
                if(allLines.last().dy() == 0 && newLine.dy() == 0){
                    // kreska pozioma
                    newLine.startPoint = allLines.last().startPoint
                    allLines.removeLast()
                    isconeccted=true
                } else if (allLines.last().dx() == 0 && newLine.dx() == 0){
                    // kreska pionowa
                    newLine.startPoint = allLines.last().startPoint
                    allLines.removeLast()
                    isconeccted=true
                } else if (allLines.last().dy() != 0 && newLine.dy() != 0){
                    // skosy
                    if(
                        (allLines.last().dx() / allLines.last().dy()).toFloat() == (newLine.dx() / newLine.dy().toFloat())
                    ){
                        newLine.startPoint = allLines.last().startPoint
                        allLines.removeLast()
                        isconeccted=true
                    }
                }
            }
            for(line in allLines){
                if(newLine.startPoint != line.endPoint && newLine.intersects(line)){
                    addPoints(INTERSECTION_POINTS)
                }
            }

            if(!isconeccted) {
                for (line in allLines) {
                    if ((newLine.startPoint == line.endPoint &&
                                newLine.endPoint.x == line.endPoint.x &&
                                abs(line.startPoint.x - line.endPoint.x) >= 2 &&
                                abs(newLine.endPoint.y - line.endPoint.y) >= 1)
                    ) {
                        addPoints(KNIGHT_TURN_POINTS)
                    }
                    if ((newLine.startPoint == line.endPoint &&
                                newLine.endPoint.y == line.endPoint.y &&
                                abs(line.startPoint.y - line.endPoint.y) >= 2 &&
                                abs(newLine.endPoint.x - line.endPoint.x) >= 1)
                    ) {
                        addPoints(KNIGHT_TURN_POINTS)
                    }
                }
            }
            allLines.add(newLine)
        }

        checkGenericPatterns()

        //pattern points value cap
        if(_score>=100){

            _score=100
            score.value=_score

        }

    }

    fun generateSelectedIds(): ArrayList<Int> {
        val ids = ArrayList<Int>()
        for(dot in selectedDots){
            ids.add(dot.index)
        }
        return ids
    }

    fun checkGenericPatterns(){

        //square values
        var square_top:Int=0
        var square_left:Int=0
        var square_bottom:Int=0
        var square_right:Int=0
        var square_check:Boolean=true
        var square_second_check:Int=0
        var _tmp_score=0

        //other-shapes values
        var z_shape_switch:Boolean=true
        var c_shape_switch:Boolean=true
        var m_shape_switch:Boolean=true
        var l_shape_switch:Boolean=true
        var x_shape_switch:Boolean=true
        var s_shape_switch:Boolean=true
        var snake_shape_switchx:Boolean=false
        var snake_shape_switchy:Boolean=false
        var snake_shape_dxlist= mutableListOf<Int>()
        var snake_shape_dylist= mutableListOf<Int>()

        //s_shape_check
        if(rowCount%2==1&&columnCount%2==1&&allLines.count()==5){


            if (abs(allLines.first().dx()) == columnCount-1&&allLines.first().dy()==0) {

                if(abs(allLines[allLines.indexOf(allLines.first())+1].dy())==rowCount/2&&abs(allLines[allLines.indexOf(allLines.first())+1].dx())==0){

                    if(abs(allLines[allLines.indexOf(allLines.first())+2].dy())==0&&abs(allLines[allLines.indexOf(allLines.first())+2].dx())==columnCount-1) {
                        if (abs(allLines[allLines.indexOf(allLines.first())+3].dx()) == 0&&abs(allLines[allLines.indexOf(allLines.first())+3].dy())==rowCount/2) {

                            if(abs(allLines.last().dx())==columnCount-1&&allLines.last().dy()==0)
                            _tmp_score = _score; score.value = 0; s_shape_switch = false
                        }
                    }

                }

            }

            if (abs(allLines.first().dy()) == rowCount-1&&allLines.first().dx()==0) {

                if(abs(allLines[allLines.indexOf(allLines.first())+1].dx())==columnCount/2&&abs(allLines[allLines.indexOf(allLines.first())+1].dy())==0){

                    if(abs(allLines[allLines.indexOf(allLines.first())+2].dx())==0&&abs(allLines[allLines.indexOf(allLines.first())+2].dy())==rowCount-1) {
                        if (abs(allLines[allLines.indexOf(allLines.first())+3].dy()) == 0&&abs(allLines[allLines.indexOf(allLines.first())+3].dx())==columnCount/2) {

                            if(abs(allLines.last().dy())==rowCount-1&&allLines.last().dx()==0) {
                                _tmp_score = _score; score.value =
                                    0; s_shape_switch = false
                            }
                        }
                    }

                }

            }
        }
        else if(allLines.count()>5&&s_shape_switch==false){score.value=_tmp_score}


        //x_shape_check
        if(allLines.count()==3) {

            if (abs(allLines.first().dx()) == columnCount-1&&abs(allLines.first().dy())==rowCount-1) {

                if(allLines[allLines.indexOf(allLines.first())+1].dy()==-allLines.first().dy()&&allLines[allLines.indexOf(allLines.first())+1].dx()==0){

                    if(abs(allLines.last().dx())==columnCount-1){_tmp_score=_score; score.value=0; x_shape_switch=false}

                }

            }

            if (abs(allLines.first().dy()) == rowCount-1&&abs(allLines.first().dx())==columnCount-1) {

                if(allLines[allLines.indexOf(allLines.first())+1].dx()==-allLines.first().dx()&&allLines[allLines.indexOf(allLines.first())+1].dy()==0){

                    if(abs(allLines.last().dy())==rowCount-1){_tmp_score=_score; score.value=0; x_shape_switch=false}

                }

            }
        }
        else if(allLines.count()>3&&x_shape_switch==false){score.value=_tmp_score}

        //l_shape_check
        if(allLines.count()==2) {
            if (abs(allLines.first().dx()) == columnCount - 1 && abs(allLines.first().dy()) == 0) {

                if (abs(allLines.last().dy()) > 0 && abs(allLines.last().dx()) == 0) {

                    _tmp_score = _score; score.value = 0; l_shape_switch = false
                }
            }

            if (abs(allLines.first().dy()) == rowCount - 1 && abs(allLines.first().dx()) == 0) {

                if (abs(allLines.last().dx()) > 0 && abs(allLines.last().dy()) == 0) {

                    _tmp_score = _score; score.value = 0; l_shape_switch = false
                }
            }
        }
        else if(allLines.count()>2&&l_shape_switch==false){score.value=_tmp_score}

        //snake_shape_check
        if(allLines.count()==rowCount*2-1){

            for(i in 0 until allLines.count()-1){

                snake_shape_dxlist.add(i,abs(allLines[i].dx()))
                snake_shape_dylist.add(i,abs(allLines[i].dy()))



            }

            if(snake_shape_dxlist[0]==columnCount-1&&snake_shape_dylist[0]==0) {
                for (i in 0..allLines.count()-2 step 2) {

                    if (snake_shape_dxlist[i] == columnCount - 1&&snake_shape_dylist[i]==0) {
                        snake_shape_switchx = true
                    }

                }

                for(i in 1..allLines.count()-2 step 2){

                    if (snake_shape_dxlist[i] == 0&&snake_shape_dylist[i]==1) {
                        snake_shape_switchy = true
                    }
                }
            }
            if(snake_shape_dylist[0]==rowCount-1&&snake_shape_dxlist[0]==0) {
                for (i in 0..allLines.count()-2 step 2) {

                    if (snake_shape_dylist[i] == rowCount - 1&&snake_shape_dxlist[i]==0) {
                        snake_shape_switchx = true
                    }

                }

                for(i in 1..allLines.count()-2 step 2){

                    if (snake_shape_dylist[i] == 0&&snake_shape_dxlist[i]==1) {
                        snake_shape_switchy = true
                    }
                }
            }

            if(snake_shape_switchx==true&&snake_shape_switchy==true){score.value=0}



        }


        //m_shape_check
        if(rowCount%2==1&&columnCount%2==1&&allLines.count()==4){


            if (abs(allLines.first().dx()) == columnCount-1) {

                if(abs(allLines[allLines.indexOf(allLines.first())+1].dy())==rowCount/2&&abs(allLines[allLines.indexOf(allLines.first())+1].dx())==columnCount/2){

                    if(abs(allLines[allLines.indexOf(allLines.first())+2].dy())==rowCount/2&&abs(allLines[allLines.indexOf(allLines.first())+2].dx())==columnCount/2) {
                        if (abs(allLines.last().dx()) == columnCount - 1) {
                            _tmp_score = _score; score.value = 0; m_shape_switch = false
                        }
                    }

                }

            }

            if (abs(allLines.first().dy()) == rowCount-1) {

                if(abs(allLines[allLines.indexOf(allLines.first())+1].dy())==rowCount/2&&abs(allLines[allLines.indexOf(allLines.first())+1].dx())==columnCount/2){

                    if(abs(allLines[allLines.indexOf(allLines.first())+2].dy())==rowCount/2&&abs(allLines[allLines.indexOf(allLines.first())+2].dx())==columnCount/2) {
                        if (abs(allLines.last().dy()) == rowCount - 1) {
                            _tmp_score = _score; score.value = 0; m_shape_switch = false
                        }
                    }

                }

            }
        }
        else if(allLines.count()>4&&m_shape_switch==false){score.value=_tmp_score}


        //c_shape_check
        if(allLines.count()==3) {

            if (abs(allLines.first().dx()) == columnCount-1) {

                if(abs(allLines[allLines.indexOf(allLines.first())+1].dy())==rowCount-1){

                    if(abs(allLines.last().dx())==columnCount-1){_tmp_score=_score; score.value=0; c_shape_switch=false}

                }

            }

            if (abs(allLines.first().dy()) == rowCount-1) {

                if(abs(allLines[allLines.indexOf(allLines.first())+1].dx())==columnCount-1){

                    if(abs(allLines.last().dy())==rowCount-1){_tmp_score=_score; score.value=0;c_shape_switch=false}

                }

            }
        }
        else if(allLines.count()>3&&c_shape_switch==false){score.value=_tmp_score}


        //z_shape_check
        if(allLines.count()==3) {
            if (abs(allLines.first().dx()) == columnCount-1) {

                if(abs(allLines[allLines.indexOf(allLines.first())+1].dy())==rowCount-1&&abs(allLines[allLines.indexOf(allLines.first())+1].dx())==columnCount-1){

                    if(abs(allLines.last().dx())==columnCount-1){_tmp_score=_score; score.value=0; z_shape_switch=false}

                }

            }

            if (abs(allLines.first().dy()) == rowCount-1) {

                if(abs(allLines[allLines.indexOf(allLines.first())+1].dx())==columnCount-1&&abs(allLines[allLines.indexOf(allLines.first())+1].dy())==rowCount-1){

                    if(abs(allLines.last().dy())==rowCount-1){_tmp_score=_score; score.value=0;z_shape_switch=false}

                }

            }
        }
        else if(allLines.count()>3&&z_shape_switch==false){score.value=_tmp_score}



        for (dot in selectedDots){


            var dotx=dot.index % columnCount
            var doty=dot.index / columnCount





            //square_check
            if(dotx==0){square_left=square_left+1}
            if(dotx==columnCount-1){square_right=square_right+1}
            if(doty==0){square_top=square_top+1}
            if(doty==rowCount-1){square_bottom=square_bottom+1}
            if((dotx!=0&&dotx!=columnCount-1)&&(doty!=0&&doty!=rowCount-1)){square_check=false}

            if(square_second_check==-1){square_second_check=-2}
            if(square_top==columnCount && square_bottom==columnCount && square_left==rowCount && square_right==rowCount&&square_check==true){

                _tmp_score=_score
                square_second_check=-1
                score.value=0
            }
            if(square_second_check==-2&&square_check==false){_score=_tmp_score; score.value=_score; square_second_check=0}





        }






    }

    fun setPattern(patternString: String){
        _patternString = patternString
    }

    private fun drawPattern(patternString: String){
        try {
            val pattern = JSONObject(patternString)
            if(pattern.length() != 0){
                for(i in 1..pattern.length()){
                    for(dot in dots){
                        if(dot.index == pattern["$i"]){
                            if(!selectedDots.contains(dot)){
                                notifyDotSelected(dot)
                            }
                        }
                    }
                }
                onFinish()
            }
        } catch (e: Exception){

            isCleared = true
        }
    }

    fun setSize(height: Int, width: Int){
        rowCount = height
        columnCount = width
    }

    fun addPoints(points: Int){
        _score += points
        score.value = _score
    }

    fun render(){
        isCleared = false
        removeAllViews()
        dots.clear()
        placeDots()
    }

    fun setGridPoints(gridPoints: List<List<Int>>){
        _gridPoints = gridPoints
    }

    private var paddingChanged = false

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(widthSpec, heightSpec)
        if(!paddingChanged){
            paddingChanged = true
            val paddingV = (measuredHeight - (measuredWidth / columnCount) * rowCount) / 2

            updatePadding(0, paddingV, 0, paddingV)
        }
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        canvas?.drawPath(linePath,linePaint)
        if(selectedDots.count() > 0 && lastX > 0f && lastY > 0f){
            val center = selectedDots[selectedDots.count() - 1].getCenter()
            canvas?.drawLine(center.x.toFloat(),center.y.toFloat(),lastX,lastY,linePaint)
        }
        if(!isFinished && !isCleared){
            drawPattern(_patternString)

        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if(!isFinished){
                    val hitDot = getHitDot(event.x.toInt(), event.y.toInt())
                    if (hitDot == null) {
                        return false
                    } else {
                        onPatternListener?.onStarted()
                        notifyDotSelected(hitDot)
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> handleActionMove(event)

            MotionEvent.ACTION_UP -> onFinish()

            else -> return false
        }
        return true
    }

    private fun onFinish() {
        if(!isFinished){
            lastX = 0f
            lastY = 0f
            isFinished = true
            invalidate()
        }
    }
    fun getFinish():Boolean{
        return isFinished
    }

    fun reset() {
        _score = 0
        score.value = 0
        //points for grid radius
        addPoints((dots.count() * RADIUS_POINTS).toInt())

        for(dot in selectedDots){
            dot.reset()
        }
        selectedDots.clear()
        linePaint.color = normalLineColor
        linePath.reset()
        lastX = 0f
        lastY = 0f
        isFinished = false
        isCleared = true
        allLines.clear()
        startpoint_tmp=true
        invalidate()
    }

    private fun handleActionMove(event: MotionEvent) {
        if(!isFinished){
            val hitDot = getHitDot(event.x.toInt(),event.y.toInt())
            if(hitDot != null){
                if(!selectedDots.contains(hitDot)){
                    notifyDotSelected(hitDot)
                }
            }
            lastX = event.x
            lastY = event.y
            invalidate()
        }

    }

    interface OnPatternListener {
        fun onStarted(){}
        fun onProgress(ids: ArrayList<Int>){}
    }
}