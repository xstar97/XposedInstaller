package de.robv.android.xposed.installer.core.logic.delegates

interface LogsDelegate
{
    /**
     * Callback function to send email
     */
    fun onSendEmail()

    /**
     * Callback function to send github
     */
    fun onSendGitHub()

    /**
     * Callback function to save log
     */
    fun onSaveLog()

    /**
     * Callback function to clear log
     */
    fun onClearLog()

    /**
     * Callback function to refresh logs
     */
    fun onRefreshLog()

    /**
     * Callback function to pass id
     */
    fun onOtherOption(id: Int)
}