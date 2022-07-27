package ru.webwarehouse.calltracker.ui.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    abstract val root: View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        root.setOnClickListener {
            clearFocus()
        }
    }

    protected fun clearFocus() {
        if (root is ViewGroup) {
            (root as ViewGroup).children.forEach {
                it.clearFocus()
            }
        } else {
            root.clearFocus()
        }
    }

}
