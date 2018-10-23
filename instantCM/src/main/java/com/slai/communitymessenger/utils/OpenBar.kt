package com.slai.communitymessenger.utils

import android.content.Context
import android.view.View
import com.google.android.material.snackbar.Snackbar

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
    lateinit var actionText : String
    lateinit var actionListener : View.OnClickListener

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
        val snack : Snackbar = Snackbar.make(view, message, duration)
        if(actionText != null && actionListener != null)
            snack.setAction(actionText, actionListener)
        snack.show()
    }

}

fun on(view : View) : OpenBar{
    return OpenBar(view)
}