package com.gowtham.calllogapp.fragments.calls

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.gowtham.calllogapp.addRestorePolicy
import com.gowtham.calllogapp.databinding.FCallsBinding
import com.gowtham.calllogapp.db.data.Call
import com.gowtham.calllogapp.hide
import com.gowtham.calllogapp.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FCalls : Fragment() {

    private var calls = mutableListOf<Call>()

    private val viewModel: FCallsViewModel by viewModels()

    private lateinit var binding: FCallsBinding

    private val adCallLog by lazy {
        AdCallLog()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FCallsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDataInView()
        subscribeObservers()
    }

    private fun setDataInView() {
        binding.listCalls.adapter = adCallLog
        adCallLog.addRestorePolicy()
    }

    private fun subscribeObservers() {
        lifecycleScope.launch {
               viewModel.getCalls().collect { list->
                   calls=list.toMutableList().asReversed()
                   AdCallLog.allCalls=calls
                   adCallLog.submitList(calls)
                   adCallLog.notifyDataSetChanged()
                   if(calls.isEmpty())
                       binding.viewEmpty.show()
                   else
                       binding.viewEmpty.hide()
               }
        }
    }

}