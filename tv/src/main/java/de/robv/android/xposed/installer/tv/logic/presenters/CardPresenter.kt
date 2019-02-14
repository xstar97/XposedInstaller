package de.robv.android.xposed.installer.tv.logic.presenters

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import androidx.core.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.logic.models.InfoModel
import de.robv.android.xposed.installer.core.logic.models.StatusModel
import de.robv.android.xposed.installer.core.logic.models.ZipModel
import de.robv.android.xposed.installer.core.util.FrameworkZips
import de.robv.android.xposed.installer.tv.logic.Navigation

class CardPresenter(private val context: Context?): Presenter()
{
    private var mSelectedBackgroundColor = -1
    private var mDefaultBackgroundColor = -1
    private var mDefaultCardImage: Drawable? = null

    override fun onCreateViewHolder(parent: ViewGroup): Presenter.ViewHolder {
        mDefaultBackgroundColor = ContextCompat.getColor(parent.context, R.color.default_background)
        mDefaultCardImage = parent.resources.getDrawable(R.mipmap.ic_launcher, null)

        val cardView = ImageCardView(parent.context)
        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true
        return Presenter.ViewHolder(cardView)
        }

        override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any) {

            val cardView = viewHolder.view as ImageCardView
            val res = cardView.resources
            val width = res.getDimensionPixelSize(R.dimen.grid_item_width)
            val height = res.getDimensionPixelSize(R.dimen.grid_item_height)
            cardView.setMainImageDimensions(width, height)
            when(item){
                is Navigation -> {
                    cardView.titleText = context!!.getString(item.title)
                    cardView.mainImage = ContextCompat.getDrawable(context, item.icon)
                }
                is StatusModel -> {
                    val infoField = cardView.findViewById<View>(R.id.info_field)
                    val title = cardView.findViewById<TextView>(R.id.title_text)
                    title.maxLines = 2
                    cardView.layoutParams.width = 400
                    infoField.layoutParams.width = 400
                    val msg = item.statusMessage!!
                    val mid = msg.length / 2
                    val initMsg = arrayOf(msg.substring(0, mid), msg.substring(mid))
                    cardView.titleText = msg
                    //cardView.titleText = initMsg[0]
                    //cardView.contentText = initMsg[1]
                    cardView.mainImage = item.statusIcon
                    cardView.setBackgroundColor(item.statusContainerColor!!)
                }
                is ZipModel -> {
                    //if (item.type == FrameworkZips.Type.INSTALLER){
                    //cardView.titleText = item.key
                    //} else {
                        val title = cardView.findViewById<TextView>(R.id.title_text)
                        title.maxLines = 2
                        val key = item.key!!
                        val mid = key.length / 2
                        val initKey = arrayOf(key.substring(0, mid), key.substring(mid))
                        cardView.titleText = initKey[0]
                        cardView.contentText = initKey[1]
                        //cardView.titleText = key
                    //}
                    cardView.mainImage = item.icon
                }
                is InfoModel -> {
                    cardView.titleText = item.key
                    cardView.contentText = item.desciption
                    cardView.mainImage = item.icon!!//ContextCompat.getDrawable(context, item.icon)
                }
            }
        }

        override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder) {
            val cardView = viewHolder.view as ImageCardView

            // Remove references to images so that the garbage collector can free up memory.
            cardView.badgeImage = null
            cardView.mainImage = null
        }

    }