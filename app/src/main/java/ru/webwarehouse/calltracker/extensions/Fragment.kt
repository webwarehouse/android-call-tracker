package ru.webwarehouse.calltracker.extensions

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.core.view.allViews
import androidx.fragment.app.Fragment

fun Fragment.clearFocus() {
    view?.allViews?.forEach {
        it.clearFocus()
    }
}

fun Fragment.hideKeyboard() {
    val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view!!.windowToken, 0)
}
