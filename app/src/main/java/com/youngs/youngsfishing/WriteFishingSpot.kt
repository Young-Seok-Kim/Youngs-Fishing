//package com.youngs.youngsfishing
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.DialogFragment
//import com.youngs.youngsfishing.databinding.WriteFishingSpotBinding
//
//class WriteFishingSpot : DialogFragment() {
//
//    lateinit var binding: WriteFishingSpotBinding
//
//
//
//
//    interface OnDialogDismissListener
//    {
//        fun whenDismiss()
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = WriteFishingSpotBinding.inflate(layoutInflater,null,false)
//    }
//
//    override fun onResume() {
//        super.onResume()
//
//    }
//
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
//
//        initButton()
//
//        return binding.root
//    }
//
//
//    private fun initButton() {
//        binding.buttonSave.setOnClickListener(object : View.OnClickListener {
//            override fun onClick(p0: View?) {
//
//            }
//        })
//    }
//}