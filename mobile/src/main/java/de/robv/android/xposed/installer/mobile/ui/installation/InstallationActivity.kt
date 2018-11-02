package de.robv.android.xposed.installer.mobile.ui.installation

import android.os.Bundle
import android.util.Log
import de.robv.android.xposed.installer.R.id.container
import de.robv.android.xposed.installer.R.layout.activity_container
import de.robv.android.xposed.installer.mobile.XposedApp

import de.robv.android.xposed.installer.core.installation.Flashable
import de.robv.android.xposed.installer.mobile.ui.base.XposedBaseActivity
import kotlinx.android.synthetic.main.activity_installation.*
import kotlinx.android.synthetic.main.view_toolbar.*

class InstallationActivity : XposedBaseActivity() {

    override fun onCreate(savedInstanceBundle: Bundle?) {
        super.onCreate(savedInstanceBundle)

        val flashable = intent.getParcelableExtra<Flashable>(Flashable.KEY)
        if (flashable == null) {
            Log.e(XposedApp.TAG, InstallationActivity::class.java.name + ": Flashable is missing")
            finish()
            return
        }

        setContentView(activity_container)

        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener { finish() }

        val ab = supportActionBar
        if (ab != null) {
            ab.setTitle(flashable.type.title)
            ab.subtitle = flashable.title
            ab.setDisplayHomeAsUpEnabled(true)
        }

        setFloating(toolbar, flashable.type.title)

        if (savedInstanceBundle == null) {
            val logFragment = InstallationFragment()
            supportFragmentManager.beginTransaction().replace(container, logFragment).commit()
            logFragment.startInstallation(this, flashable)
        }
    }

    /*
    class InstallationFragment : Fragment(), FlashCallback {
        private var mFlashable: Flashable? = null

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
            return inflater.inflate(R.layout.activity_installation, container, false)
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
                console_result!!.setImageResource(R.drawable.ic_check_circle)
                console_result!!.visibility = View.VISIBLE
                //TODO replace the InstallationUtils method
                val fadeInResult = InstallationUtils.FadeInResult(console_result, "alpha", 0.0f, 0.03f)
                fadeInResult.duration = (MEDIUM_ANIM_TIME * 2).toLong()

                // Collapse the whole bottom bar.
                assert(view != null)
                val buttomBar = view!!.findViewById<View>(R.id.buttonPanel)
                val collapseBottomBar = createExpandCollapseAnimator(buttomBar, false)
                collapseBottomBar.duration = MEDIUM_ANIM_TIME.toLong()
                collapseBottomBar.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        progressBar!!.isIndeterminate = false
                        progressBar!!.rotation = 180f
                        progressBar!!.max = REBOOT_COUNTDOWN
                        progressBar!!.progress = REBOOT_COUNTDOWN

                        reboot!!.visibility = View.VISIBLE
                        cancel!!.visibility = View.VISIBLE
                    }
                })

                val expandBottomBar = createExpandCollapseAnimator(buttomBar, true)
                expandBottomBar.duration = (MEDIUM_ANIM_TIME * 2).toLong()
                expandBottomBar.startDelay = (LONG_ANIM_TIME * 4).toLong()

                //TODO replace the InstallationUtils method
                val countdownProgress = InstallationUtils.CountdownProgress(progressBar, "progress", REBOOT_COUNTDOWN, 0)
                countdownProgress.duration = REBOOT_COUNTDOWN.toLong()
                countdownProgress.interpolator = LinearInterpolator()

                val countdownButton = ValueAnimator.ofInt(REBOOT_COUNTDOWN / 1000, 0)
                countdownButton.duration = REBOOT_COUNTDOWN.toLong()
                countdownButton.interpolator = LinearInterpolator()

                val format = getString(R.string.countdown)
                val rebootMode = mFlashable!!.rebootMode
                val action = getString(rebootMode.titleRes)
                reboot!!.text = String.format(format, action, REBOOT_COUNTDOWN / 1000)

                countdownButton.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
                    private var minWidth = 0

                    @SuppressLint("StringFormatMatches")
                    override fun onAnimationUpdate(animation: ValueAnimator) {
                        reboot!!.text = String.format(format, action, animation.animatedValue)

                        // Make sure that the button width doesn't shrink.
                        if (reboot!!.width > minWidth) {
                            minWidth = reboot!!.width
                            reboot!!.minimumWidth = minWidth
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
                            reboot!!.callOnClick()
                        }
                    }
                })

                reboot!!.setOnClickListener {
                    countdownProgress.cancel()
                    countdownButton.cancel()

                    val rootUtil = RootUtil()
                    if (!rootUtil.startShell(this@InstallationFragment) || !rootUtil.reboot(rebootMode, this@InstallationFragment)) {
                        onError(FlashCallback.ERROR_GENERIC, getString(R.string.reboot_failed))
                    }
                }

                cancel!!.setOnClickListener {
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

                console_result!!.setImageResource(R.drawable.ic_error)
                console_result!!.visibility = View.VISIBLE
                //TODO replace the InstallationUtils method
                val fadeInResult = InstallationUtils.FadeInResult(console_result, "alpha", 0.0f, 0.03f)
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
                    console!!.append(text)
                    console!!.append("\n")
                    return
                }
            }

            val start = console!!.length()
            console!!.append(text)
            val end = console!!.length()
            (console!!.text as Spannable).setSpan(ForegroundColorSpan(color), start, end, 0)
            console!!.append("\n")
        }

        companion object {
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
        }
    }

    companion object {
        private const val REBOOT_COUNTDOWN = 15000

        private val MEDIUM_ANIM_TIME = XposedApp.getInstance().resources
                .getInteger(android.R.integer.config_mediumAnimTime)
        private val LONG_ANIM_TIME = XposedApp.getInstance().resources
                .getInteger(android.R.integer.config_longAnimTime)
    }*/
}