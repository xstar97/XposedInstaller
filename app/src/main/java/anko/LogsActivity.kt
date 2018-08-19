package anko

import android.app.Activity
import android.os.Bundle
import org.jetbrains.anko.*

import com.company.project.R

/**
 * Generate with Plugin
 * @plugin Kotlin Anko Converter For Xml
 * @version 1.2.1
 */
class LogsActivity : Activity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		relativeLayout {
			id = R.id.container
			scrollView {
				id = R.id.svLog
				horizontalScrollView {
					id = R.id.hsvLog
					textView {
						id = R.id.txtLog
						//tools:text = logging the good stuff //not support attribute
						padding = dip(8)
						//android:textIsSelectable = true //not support attribute
					}
				}.lparams(width = matchParent)
			}.lparams(width = matchParent)
			floatingActionButton {
				id = R.id.fabLogActions
				//android:layout_alignParentEnd = true //not support attribute
				//android:clickable = true //not support attribute
				//android:focusable = true //not support attribute
				//app:srcCompat = @drawable/ic_info //not support attribute
			}.lparams {
				alignParentBottom()
				alignParentRight()
				margin = @dimen/fab_marg //in
			}
		}
	}
}
