package xyz.sachil.staggeredgrid.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import kotlin.math.max

/**
 * 通过使用Compose的Layout来学习如何在Compose中自定义view，这里我们自定义一个StaggeredGridLayout。
 * 在compose中自定义view的过程和传统的自定义view差不多，都是先依次测量子view的尺寸，然后确定自身的尺寸，
 * 最后将各个子view摆放到对应的位置。
 */
@Composable
fun StaggeredGridLayout(
    modifier: Modifier = Modifier,
    orientation: Orientation = Orientation.Default,
    spanCount: Int = 3,
    content: @Composable () -> Unit
) {
    check(spanCount > 1) {
        "Please set the span count larger than 1."
    }
    //measurables是一个List类型，可以理解为子view列表。constraints的类型为Constraints，表示约束条件
    Layout(modifier = modifier, content = content) { measurables, constraints ->

        val widthArrays = IntArray(spanCount) { 0 }
        val heightArrays = IntArray(spanCount) { 0 }

        //依次测量所包含的子view
        val placeables = measurables.mapIndexed { index, measurable ->
            val spanIndex = index % spanCount
            val placeable = measurable.measure(constraints)

            if (orientation == Orientation.HORIZONTAL) {
                widthArrays[spanIndex] += placeable.width
                heightArrays[spanIndex] = max(heightArrays[spanIndex], placeable.height)
            } else {
                widthArrays[spanIndex] = max(widthArrays[spanIndex], placeable.width)
                heightArrays[spanIndex] += placeable.height
            }
            placeable
        }
        //计算StaggeredGridLayout的宽度,该宽度不能超过constraints的约束条件。
        // 当没有子view时，取constraints.minWidth，否则取rowWidthArrays中的最大值
        val width = if (orientation == Orientation.HORIZONTAL) {
            widthArrays.maxOrNull()?.coerceIn(constraints.minWidth, constraints.maxWidth)
                ?: constraints.minWidth
        } else {
            widthArrays.sum().coerceIn(constraints.minWidth, constraints.maxWidth)
        }

        //计算StaggeredGridLayout的高度,该高度不能超过constraints的约束条件
        val height = if (orientation == Orientation.HORIZONTAL) {
            heightArrays.sum().coerceIn(constraints.minHeight, constraints.maxHeight)
        } else {
            heightArrays.maxOrNull()?.coerceIn(constraints.minHeight, constraints.maxHeight)
                ?: constraints.minHeight
        }

        //子view的布局
        layout(width, height) {
            //保存每一行开始布局时子view的X坐标
            val xArrays = IntArray(spanCount) { 0 }
            //计算并保存每一行起始的Y坐标
            val yArrays = IntArray(spanCount) { 0 }

            if (orientation == Orientation.HORIZONTAL) {
                for (rowIndex in 1 until spanCount) {
                    yArrays[rowIndex] = heightArrays[rowIndex] + yArrays[rowIndex - 1]
                }
            } else {
                for (columnIndex in 1 until spanCount) {
                    xArrays[columnIndex] = widthArrays[columnIndex] + xArrays[columnIndex - 1]
                }
            }

            placeables.forEachIndexed { index, placeable ->
                val spanIndex = index % spanCount
                placeable.placeRelative(xArrays[spanIndex], yArrays[spanIndex])
                if (orientation == Orientation.HORIZONTAL) {
                    xArrays[spanIndex] += placeable.width
                } else {
                    yArrays[spanIndex] += placeable.height
                }
            }
        }
    }
}

enum class Orientation {
    HORIZONTAL, VERTICAL;

    companion object {
        val Default = HORIZONTAL
    }
}