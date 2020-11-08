package com.gowtham.calllogapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.gowtham.calllogapp.R
import com.gowtham.calllogapp.databinding.FDeleteSheetBinding
import com.gowtham.calllogapp.utils.DeleteEvent
import com.gowtham.calllogapp.utils.ItemClickListener
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus

class FDeleteDialog : BottomSheetDialogFragment() {

    private lateinit var binding: FDeleteSheetBinding

    companion object{
        fun newInstance(bundle: Bundle) : FDeleteDialog{
            val fragment = FDeleteDialog()
            fragment.arguments=bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FDeleteSheetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnYes.setOnClickListener {
            EventBus.getDefault().post(DeleteEvent(true))
            dismiss()
        }

        binding.btnNo.setOnClickListener {
            EventBus.getDefault().post(DeleteEvent(false))
            dismiss()
        }
    }
}