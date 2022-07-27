package ru.webwarehouse.calltracker.ui.fragments.home

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.webwarehouse.calltracker.R
import ru.webwarehouse.calltracker.databinding.FragmentHomeBinding
import ru.webwarehouse.calltracker.extensions.clearError
import ru.webwarehouse.calltracker.ui.fragments.ViewBindingFragment
import ru.webwarehouse.calltracker.util.Validator

@AndroidEntryPoint
class HomeFragment : ViewBindingFragment<FragmentHomeBinding>() {

    private val viewModel by viewModels<HomeViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSave.setOnClickListener {
            onSave()
        }

        binding.urlInputEditText.text = SpannableStringBuilder(viewModel.getApiUrl())
        binding.urlInputEditText.addTextChangedListener {
            binding.urlInputLayout.clearError()
        }

        binding.idInputEditText.text = SpannableStringBuilder(viewModel.getOperatorCode())
        binding.idInputEditText.addTextChangedListener {
            binding.idInputLayout.clearError()
        }
    }

    private fun onSave() {
        val url = binding.urlInputEditText.text.toString()
        if (!Validator.isValidUrl(url)) {
            binding.urlInputLayout.error = getString(R.string.enter_valid_url)
            return
        }

        val id = binding.idInputEditText.text.toString()
        if (!Validator.isValidOperatorId(id)) {
            binding.idInputLayout.error = getString(R.string.enter_valid_operator_id)
            return
        }

        val success = viewModel.onSave(url, id)
        if (success) {
            onSavedSuccessfully()
        }
    }

    private fun onSavedSuccessfully() {
         Snackbar.make(binding.root, R.string.saved_succefully, Snackbar.LENGTH_SHORT).apply {
             setAction(R.string.hide) {
                this.dismiss()
             }
             show()
         }
    }
}
