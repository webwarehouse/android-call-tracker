package ru.webwarehouse.calltracker.ui.fragments.log

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.webwarehouse.calltracker.databinding.FragmentHomeBinding
import ru.webwarehouse.calltracker.databinding.FragmentLogBinding

@AndroidEntryPoint
class LogFragment : Fragment() {

    private var _binding: FragmentLogBinding? = null
    private val binding: FragmentLogBinding get() = _binding!!

    private val viewModel by viewModels<LogViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

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
