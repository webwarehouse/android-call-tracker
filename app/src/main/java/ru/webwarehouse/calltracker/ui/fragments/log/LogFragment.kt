package ru.webwarehouse.calltracker.ui.fragments.log

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.webwarehouse.calltracker.databinding.FragmentLogBinding
import ru.webwarehouse.calltracker.ui.fragments.ViewBindingFragment

@AndroidEntryPoint
class LogFragment : ViewBindingFragment<FragmentLogBinding>() {

    private val viewModel by viewModels<LogViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLog.setOnClickListener {
            val log = viewModel.getLog().toTypedArray()
            binding.listView.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, log)
        }

        binding.btnNumbers.setOnClickListener {
            val numbers = viewModel.getNumbers().toTypedArray()
            binding.listView.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, numbers)
        }

    }
}
