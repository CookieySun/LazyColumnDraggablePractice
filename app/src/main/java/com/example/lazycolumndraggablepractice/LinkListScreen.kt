package com.example.lazycolumndraggablepractice

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt

@Composable
fun Main() {
    val items = remember { mutableStateListOf(*Array(20) { "アイテム $it" }) } // アイテムのリスト
    val density = LocalDensity.current
    var draggedItemIndex by remember { mutableStateOf<Int?>(null) }
    var draggedOffset by remember { mutableStateOf(0f) }
    val itemHeightPx = with(density) { 50.dp.toPx() }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        itemsIndexed(items, key = { _, item -> item }) { index, item ->
            val isDragging = remember(draggedItemIndex) { draggedItemIndex == index }
            val offsetY = if (isDragging) draggedOffset else 0f
            val scale = remember(isDragging) { if (isDragging) 1.1f else 1f }
            val elevation =
                remember(isDragging) { with(density) { if (isDragging) 2.dp.toPx() else 0.dp.toPx() } }
            val itemColor =
                remember(isDragging) { if (isDragging) Color.LightGray else Color.White }

            var newIndex: Int
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .zIndex(if (isDragging) 1f else 0f)
                    .offset { IntOffset(0, offsetY.roundToInt()) }
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        shadowElevation = elevation
                    }
                    .background(itemColor),
            ) {
                ListItem(
                    str = item,
                    modifier = Modifier.draggable(
                        orientation = Orientation.Vertical,
                        state = rememberDraggableState { delta ->
                            draggedOffset += delta
                            newIndex = (draggedOffset / itemHeightPx).toInt() + index
                            if (newIndex != index && newIndex in items.indices) {
                                items.move(index, newIndex)
                                draggedItemIndex = newIndex
                                draggedOffset = 0f
                            }
                        },
                        onDragStarted = {
                            draggedItemIndex = index
                        },
                        onDragStopped = {
                            draggedOffset = 0f
                            draggedItemIndex = null
                        },
                    ),
                )
            }
        }
    }
}

fun <T> MutableList<T>.move(from: Int, to: Int) {
    val item = this[from]
    this.removeAt(from)
    this.add(to, item)
}

@Composable
fun ListItem(str: String, modifier: Modifier) {
    Box(
        modifier = Modifier
            .background(Color.White)
            .padding(12.dp)
            .width(512.dp),
    ) {
        Row {
            Spacer(modifier = Modifier.width(8.dp))
            Text(modifier = Modifier.weight(1f), text = str)
            Icon(
                painter = painterResource(id = R.drawable.baseline_drag_indicator_24),
                contentDescription = null,
                modifier
            )
        }
    }
}

@Preview
@Composable
private fun MainPreview() {
    Main()
}

@Preview
@Composable
private fun ItemPreview() {
    ListItem("q", Modifier)
}

