package com.example.tabatatimer


import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.tabatatimer.databinding.FragmentAddBinding
import com.example.tabatatimer.db.Training
import com.example.tabatatimer.db.TrainingDatabase
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener


class AddFragment : Fragment() {
    private lateinit var binding: FragmentAddBinding
    private lateinit var viewModel: TrainingViewModel
    private var updatingId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddBinding.inflate(inflater, container, false)

        val dao = TrainingDatabase.getInstance(container!!.context).trainingDao()
        val factory = TrainingViewModelFactory(dao)

        viewModel = ViewModelProvider(this, factory).get(TrainingViewModel::class.java)

        initButtons()
        initColorPicker()

        return binding.root
    }

    private fun initColorPicker() {
        binding.btnPickColor.setOnClickListener{
            ColorPickerDialog.Builder(context)
                .setTitle("ColorPicker Dialog")
                .setPreferenceName("MyColorPickerDialog")
                .setPositiveButton(getString(R.string.confirm),
                    ColorEnvelopeListener { envelope, fromUser ->
                        run {
                            binding.vPreviewSelectedColor.setBackgroundColor(envelope.color)
                        }
                    })
                .setNegativeButton(
                    getString(R.string.cancel)
                ) { dialogInterface, i -> dialogInterface.dismiss() }
                .attachAlphaSlideBar(true) // the default value is true.
                .attachBrightnessSlideBar(true) // the default value is true.
                .setBottomSpace(12) // set a bottom space between the last slidebar and buttons.
                .show()
        }
    }

    private fun initButtons(){
        binding.apply {
            try {
                updatingId = requireArguments().getInt("id_for_update")
                val name = requireArguments().getString("name_for_update")
                val color = requireArguments().getInt("color_for_update")
                etTrainingName.setText(name)
                vPreviewSelectedColor.setBackgroundColor(color)
            } catch (ex: IllegalStateException) {

            }
            btnSubmitTraining.setOnClickListener {
                val name = etTrainingName.text.toString()
                val color = (vPreviewSelectedColor!!.background as ColorDrawable).color

                if(updatingId != -1) viewModel.updateTraining(Training(updatingId, name, color))
                else viewModel.insertTraining(Training(0, name, color))
                findNavController().navigate(R.id.action_addFragment_to_mainFragment)
            }
        }
    }

}