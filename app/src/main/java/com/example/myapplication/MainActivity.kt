package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
                ){
                circularProgressBar(0.8f,100)
            }
        }
    }
}

// Animated circular
@Composable
fun circularProgressBar(
    percentage : Float,
    number : Int,
    fontSize : TextUnit = 25.sp,
    radius : Dp = 50.dp,
    color : Color = Color.Green,
    strokeWidth : Dp = 8.dp,
    animDuration : Int = 1000,
    delayDuration : Int = 0
){
    var isLoading by remember {
        mutableStateOf(false)
    }
    var currentPercentage = animateFloatAsState(
        targetValue = if (isLoading) percentage else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = delayDuration
        )
    )
    LaunchedEffect(key1 = true){
        isLoading = true
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(radius * 2f)
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ){
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360 * currentPercentage.value ,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(),cap = StrokeCap.Round)
            )
        }
        Text(
            text = (currentPercentage.value * number).toInt().toString(),
            color = Color.Black,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold

        )
    }
}


// Part 11
@Composable
fun boxLargerWithAnimation(){
    val scaffoldState = rememberScaffoldState()
    var sizeBoxState by remember {
        mutableStateOf(200.dp)
    }
    val animateDp by animateDpAsState(
        targetValue = sizeBoxState,
        spring(dampingRatio = Spring.DampingRatioHighBouncy)
        /*tween(
            durationMillis = 1000,
            delayMillis = 100,
            easing = FastOutLinearInEasing
        )*/
    )

    val infiniteAnimate = rememberInfiniteTransition()
    val colorTransInfinite by infiniteAnimate.animateColor(
        initialValue = Color.Red,
        targetValue = Color.Yellow,
        animationSpec = infiniteRepeatable(
            animation =
                tween(2000,100,easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    Scaffold(scaffoldState = scaffoldState) {
        Box(modifier = Modifier
            .size(animateDp)
            .background(colorTransInfinite),
            contentAlignment = Alignment.Center){
            Button(onClick = {
                sizeBoxState += 50.dp
            }) {
                Text(text = "Click me")
            }
        }
    }
}

// Part 10
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun composableWithSideEffect(backPressedDispatcher: OnBackPressedDispatcher){
    val scaffoldState = rememberScaffoldState()
    val snackBarCoroutinesScope = rememberCoroutineScope()
    var numberState by remember {
        mutableStateOf(0)
    }

    val numberProduceState = produceState(initialValue = 0 ){
        delay(2000L)
        value = 3
    }

    val callBack = remember {
        object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {

            }
        }
    }
    DisposableEffect(key1 = backPressedDispatcher){
        backPressedDispatcher.addCallback(callBack)
        onDispose {
            callBack.remove()
        }
    }
    SideEffect {
    }
    if (numberState % 5 == 0 && numberState > 0)
    {
        LaunchedEffect(key1 = scaffoldState.snackbarHostState){
            scaffoldState.snackbarHostState.showSnackbar("This number is divisible by five")
        }
    }
    Scaffold(scaffoldState = scaffoldState) {
        Button(onClick = { numberState++ }) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Click me : $numberState")
                Text(text = "This number from produce state : ${numberProduceState.value}")
            }

        }
        snackBarCoroutinesScope.launch {
            delay(2100L)
            scaffoldState.snackbarHostState.showSnackbar("Pop up after 1s with value : ${numberProduceState.value}",duration = SnackbarDuration.Short)
        }
    }

}

@Composable
fun constraintBoxes(){
    val constraintSet = ConstraintSet {
        val greenBox = createRefFor("greenbox")
        val redBox = createRefFor("redbox")

        constrain(ref = greenBox){
            top.linkTo(anchor = parent.top)
            start.linkTo(anchor = parent.start)
            width = Dimension.value(100.dp)
            height = Dimension.value(100.dp)
        }
        constrain(ref = redBox){
            top.linkTo(anchor = greenBox.top)
            start.linkTo(anchor = greenBox.end)
            end.linkTo(anchor = parent.end)
            bottom.linkTo(anchor = greenBox.bottom)
            width = Dimension.value(100.dp)
            height = Dimension.fillToConstraints
        }

        createHorizontalChain(greenBox,redBox)

    }
    ConstraintLayout(constraintSet = constraintSet, modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier
            .background(Color.Green)
            .layoutId("greenbox"))
        Box(modifier = Modifier
            .background(Color.Red)
            .layoutId("redbox"))
    }
}


@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        Greeting("Android")
    }
}