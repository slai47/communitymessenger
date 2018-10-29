package com.slai.communitymessenger.utils

import android.os.Build
import android.text.Html
import android.text.Spanned
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.slai.communitymessenger.utils.OpenBar.Companion.on

/**
 * Snackbar creator that uses the [on] method to set the view to attach to.
 *
 *
 *
 * @param view - View that the snackbar will attach to
 *
 */
class OpenBar(val view : View) {

    companion object {
        fun on(view: View) : OpenBar = OpenBar(view)

        val SHORT : Int = Snackbar.LENGTH_SHORT
        val LONG : Int = Snackbar.LENGTH_LONG
        val INDEFINITELY : Int = Snackbar.LENGTH_INDEFINITE

    }

    var duration : Int = Snackbar.LENGTH_SHORT
    lateinit var message : String
    var actionText : String? = null
    var actionListener : View.OnClickListener? = null

    fun with(message : String) : OpenBar{
        this.message = message
        return this
    }

    fun with(resId : Int) : OpenBar{
        with(view.context.getString(resId))
        return this
    }

    fun duration(duration : Int) : OpenBar{
        this.duration = duration
        return this
    }

    fun withAction(text : String, listener : View.OnClickListener) : OpenBar{
        actionText = text
        actionListener = listener
        return this
    }

    fun withAction(resId : Int, listener: View.OnClickListener) : OpenBar {
        return withAction(view.context.getString(resId), listener)
    }

    fun show(){
        Html.fromHtml("<font color=\"#ffffff\">Tap to open</font>")
        val snack : Snackbar = Snackbar.make(view, createText(message), duration)
        if(actionText != null && actionListener != null)
            snack.setAction(actionText, actionListener)

        snack.show()
    }

    private fun createText(text: String): Spanned {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml("<font color=\"#ffffff\">$text</font>", Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(text);
        }
    }

}

fun on(view : View) : OpenBar{
    return OpenBar(view)
}