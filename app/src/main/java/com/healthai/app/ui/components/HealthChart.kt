package com.healthai.app.ui.components

import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.healthai.app.R

@Composable
fun HealthChart(modifier: Modifier = Modifier) {
    val pinkColor = colorResource(id = R.color.purple_200).toArgb()
    val cyanColor = colorResource(id = R.color.teal_200).toArgb()

    AndroidView(
        modifier = modifier.height(200.dp).fillMaxSize(),
        factory = { context ->
            LineChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                description = Description().apply { text = "" }
                legend.isEnabled = false
                axisRight.isEnabled = false
                xAxis.isEnabled = false
                axisLeft.textColor = android.graphics.Color.WHITE
                setTouchEnabled(false)
            }
        },
        update = { chart ->
            // Dummy Data for Preview
            val entries = listOf(
                Entry(1f, 70f), Entry(2f, 72f), Entry(3f, 75f),
                Entry(4f, 80f), Entry(5f, 76f), Entry(6f, 74f)
            )
            val dataSet = LineDataSet(entries, "Heart Rate").apply {
                color = pinkColor
                valueTextColor = cyanColor
                lineWidth = 2f
                setCircleColor(cyanColor)
                mode = LineDataSet.Mode.CUBIC_BEZIER
                setDrawFilled(true)
                fillColor = pinkColor
                fillAlpha = 50
            }
            chart.data = LineData(dataSet)
            chart.invalidate()
        }
    )
}