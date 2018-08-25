package de.robv.android.xposed.installer.tv.ui.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.logic.base.activities.utils.InstallationUtils
import de.robv.android.xposed.installer.core.installation.FlashCallback
import de.robv.android.xposed.installer.core.installation.Flashable
import de.robv.android.xposed.installer.core.util.RootUtil
import de.robv.android.xposed.installer.tv.XposedApp
import java.io.File

class InstallationFragment : Fragment(), FlashCallback {
    private var mFlashable: Flashable? = null
    private var mLogText: TextView? = null
    private var mProgress: ProgressBar? = null
    private var mConsoleResult: ImageView? = null
    private var mBtnReboot: Button? = null
    private var mBtnCancel: Button? = null

    //TODO: Add toggle for user to force system installation
    @Suppress("unused")
    private val isOkSystemless: Boolean
        get() {
            val suPartition = File("/su").exists() && File("/data/su.img").exists()
            val m = Build.VERSION.SDK_INT >= 23
            /*
             * toggle here;)
             */
            return m && suPartition
        }

    fun startInstallation(context: Context, flashable: Flashable) {
        mFlashable = flashable
        object : Thread("FlashZip") {
            override fun run() {
                mFlashable!!.flash(context, this@InstallationFragment)
            }
        }.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_installation, container, false)

        mLogText = view.findViewById(R.id.console)
        mProgress = view.findViewById(R.id.progressBar)
        mConsoleResult = view.findViewById(R.id.console_result)
        mBtnReboot = view.findViewById(R.id.reboot)
        mBtnCancel = view.findViewById(R.id.cancel)

        return view
    }

    override fun onStarted() {
        try {
            Thread.sleep((LONG_ANIM_TIME * 3).toLong())
        } catch (ignored: InterruptedException) {
        }

    }

    override fun onLine(line: String) {
        try {
            Thread.sleep(60)
        } catch (ignored: InterruptedException) {
        }

        XposedApp.postOnUiThread { appendText(line, TYPE_NONE) }
    }

    override fun onErrorLine(line: String) {
        try {
            Thread.sleep(60)
        } catch (ignored: InterruptedException) {
        }

        XposedApp.postOnUiThread { appendText(line, TYPE_ERROR) }
    }

    override fun onDone() {
        XposedApp.getInstance().reloadXposedProp()
        try {
            Thread.sleep(LONG_ANIM_TIME.toLong())
        } catch (ignored: InterruptedException) {
        }

        XposedApp.postOnUiThread {
            appendText("\n" + getString(R.string.file_done), TYPE_OK)

            // Fade in the result image.
            mConsoleResult!!.setImageResource(R.drawable.ic_check_circle)
            mConsoleResult!!.visibility = View.VISIBLE
            //TODO replace the InstallationUtils method
            val fadeInResult = InstallationUtils.FadeInResult(mConsoleResult, "alpha", 0.0f, 0.03f)
            fadeInResult.duration = (MEDIUM_ANIM_TIME * 2).toLong()

            // Collapse the whole bottom bar.
            assert(view != null)
            val buttomBar = view!!.findViewById<View>(R.id.buttonPanel)
            val collapseBottomBar = createExpandCollapseAnimator(buttomBar, false)
            collapseBottomBar.duration = MEDIUM_ANIM_TIME.toLong()
            collapseBottomBar.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    mProgress!!.isIndeterminate = false
                    mProgress!!.rotation = 180f
                    mProgress!!.max = REBOOT_COUNTDOWN
                    mProgress!!.progress = REBOOT_COUNTDOWN

                    mBtnReboot!!.visibility = View.VISIBLE
                    mBtnCancel!!.visibility = View.VISIBLE
                }
            })

            val expandBottomBar = createExpandCollapseAnimator(buttomBar, true)
            expandBottomBar.duration = (MEDIUM_ANIM_TIME * 2).toLong()
            expandBottomBar.startDelay = (LONG_ANIM_TIME * 4).toLong()

            //TODO replace the InstallationUtils method
            val countdownProgress = InstallationUtils.CountdownProgress(mProgress, "progress", REBOOT_COUNTDOWN, 0)
            countdownProgress.duration = REBOOT_COUNTDOWN.toLong()
            countdownProgress.interpolator = LinearInterpolator()

            val countdownButton = ValueAnimator.ofInt(REBOOT_COUNTDOWN / 1000, 0)
            countdownButton.duration = REBOOT_COUNTDOWN.toLong()
            countdownButton.interpolator = LinearInterpolator()

            val format = getString(R.string.countdown)
            val rebootMode = mFlashable!!.rebootMode
            val action = getString(rebootMode.titleRes)
            mBtnReboot!!.text = String.format(format, action, REBOOT_COUNTDOWN / 1000)

            countdownButton.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
                private var minWidth = 0

                @SuppressLint("StringFormatMatches")
                override fun onAnimationUpdate(animation: ValueAnimator) {
                    mBtnReboot!!.text = String.format(format, action, animation.animatedValue)

                    // Make sure that the button width doesn't shrink.
                    if (mBtnReboot!!.width > minWidth) {
                        minWidth = mBtnReboot!!.width
                        mBtnReboot!!.minimumWidth = minWidth
                    }
                }
            })

            countdownButton.addListener(object : AnimatorListenerAdapter() {
                private var canceled = false

                override fun onAnimationCancel(animation: Animator) {
                    canceled = true
                }

                override fun onAnimationEnd(animation: Animator) {
                    if (!canceled) {
                        mBtnReboot!!.callOnClick()
                    }
                }
            })

            mBtnReboot!!.setOnClickListener {
                countdownProgress.cancel()
                countdownButton.cancel()

                val rootUtil = RootUtil()
                if (!rootUtil.startShell(this@InstallationFragment) || !rootUtil.reboot(rebootMode, this@InstallationFragment)) {
                    onError(FlashCallback.ERROR_GENERIC, getString(R.string.reboot_failed))
                }
            }

            mBtnCancel!!.setOnClickListener {
                countdownProgress.cancel()
                countdownButton.cancel()

                assert(activity != null)
                activity!!.finish()
            }

            val `as` = AnimatorSet()
            `as`.play(fadeInResult)
            `as`.play(collapseBottomBar).with(fadeInResult)
            `as`.play(expandBottomBar).after(collapseBottomBar)
            `as`.play(countdownProgress).after(expandBottomBar)
            `as`.play(countdownButton).after(expandBottomBar)
            `as`.start()
        }
    }

    override fun onError(exitCode: Int, error: String) {
        XposedApp.postOnUiThread {
            appendText(error, TYPE_ERROR)

            mConsoleResult!!.setImageResource(R.drawable.ic_error)
            mConsoleResult!!.visibility = View.VISIBLE
            //TODO replace the InstallationUtils method
            val fadeInResult = InstallationUtils.FadeInResult(mConsoleResult, "alpha", 0.0f, 0.03f)
            fadeInResult.duration = (MEDIUM_ANIM_TIME * 2).toLong()

            assert(view != null)
            val buttomBar = view!!.findViewById<View>(R.id.buttonPanel)
            val collapseBottomBar = createExpandCollapseAnimator(buttomBar, false)
            collapseBottomBar.duration = MEDIUM_ANIM_TIME.toLong()

            val `as` = AnimatorSet()
            `as`.play(fadeInResult)
            `as`.play(collapseBottomBar).with(fadeInResult)
            `as`.start()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun appendText(text: String, type: Int) {
        val color: Int = when (type) {
            TYPE_ERROR -> {
                assert(activity != null)
                ContextCompat.getColor(activity!!, R.color.red_500)
            }
            TYPE_OK -> {
                assert(activity != null)
                ContextCompat.getColor(activity!!, R.color.darker_green)
            }
            else -> {
                mLogText!!.append(text)
                mLogText!!.append("\n")
                return
            }
        }

        val start = mLogText!!.length()
        mLogText!!.append(text)
        val end = mLogText!!.length()
        (mLogText!!.text as Spannable).setSpan(ForegroundColorSpan(color), start, end, 0)
        mLogText!!.append("\n")
    }

    companion object {
        val TAG: String = InstallationFragment::class.java.simpleName
        fun newInstance() = InstallationFragment()
        private const val TYPE_NONE = 0
        private const val TYPE_ERROR = -1
        private const val TYPE_OK = 1

        private fun createExpandCollapseAnimator(view: View, expand: Boolean): ValueAnimator {
            val animator = object : ValueAnimator() {
                override fun start() {
                    view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    val height = view.measuredHeight

                    var start = 0
                    var end = 0
                    if (expand) {
                        start = -height
                    } else {
                        end = -height
                    }

                    setIntValues(start, end)

                    super.start()
                }
            }

            animator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
                private val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams

                override fun onAnimationUpdate(animation: ValueAnimator) {
                    layoutParams.bottomMargin = animation.animatedValue as Int
                    view.requestLayout()
                }
            })

            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    view.visibility = View.VISIBLE
                }

                override fun onAnimationEnd(animation: Animator) {
                    if (!expand) {
                        view.visibility = View.GONE
                    }
                }
            })

            return animator
        }

        private const val REBOOT_COUNTDOWN = 15000

        private val MEDIUM_ANIM_TIME = XposedApp.getInstance().resources
                .getInteger(android.R.integer.config_mediumAnimTime)
        private val LONG_ANIM_TIME = XposedApp.getInstance().resources
                .getInteger(android.R.integer.config_longAnimTime)
    }
}