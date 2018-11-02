package de.robv.android.xposed.installer.mobile.logic.widgets

import android.content.Context
import android.graphics.Rect
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View


class AutoCloseBottomSheetBehavior<V : View>(context: Context, attrs: AttributeSet) : BottomSheetBehavior<V>(context, attrs) {

    override fun onInterceptTouchEvent(parent: CoordinatorLayout, child: V, event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN && state == BottomSheetBehavior.STATE_EXPANDED) {

            val outRect = Rect()
            child.getGlobalVisibleRect(outRect)

            if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
        return super.onInterceptTouchEvent(parent, child, event)
    }
}