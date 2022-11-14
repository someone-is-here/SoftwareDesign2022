package com.example.studentregister

import android.annotation.SuppressLint
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studentregister.databinding.ActivityMainBinding
import com.example.studentregister.db.Student
import com.example.studentregister.db.StudentDatabase

class MainActivity : AppCompatActivity() {
    private lateinit var  viewModel: StudentViewModel
    private lateinit var adapter: StudentRecycleViewAdapter

    private lateinit var selectedStudent: Student
    private var isSelected = false

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnSave.setOnClickListener {
                if (isSelected) {
                    updateStudentData()
                    clearInput()
                } else {
                    saveStudentData()
                    clearInput()
                }
            }

            btnClear.setOnClickListener {
                if (isSelected) {
                    deleteStudentData()
                    clearInput()
                } else {
                    clearInput()
                }
            }
        }

        val dao = StudentDatabase.getInstance(application).studentDao()
        val factory = StudentViewModelFactory(dao)

        viewModel = ViewModelProvider(this, factory).get(StudentViewModel::class.java)

        initRecycleView()
    }

    private fun updateStudentData(){
        binding.apply {
            viewModel.updateStudent(
                Student(
                    selectedStudent.id,
                    etName.text.toString(),
                    etEmail.text.toString()
                )
            )
            btnSave.text = "Save"
            btnClear.text = "Clear"
            isSelected = false
        }
    }
    private fun deleteStudentData(){
        binding.apply {
            viewModel.deleteStudent(
                Student(
                    selectedStudent.id,
                    etName.text.toString(),
                    etEmail.text.toString()
                )
            )
            btnSave.text = "Save"
            btnClear.text = "Clear"
            isSelected = false
        }
    }

    private fun initRecycleView(){
        binding.rvStudents.layoutManager = LinearLayoutManager(this)
        adapter =
            StudentRecycleViewAdapter { selectedItem: Student -> listItemClicked(selectedItem) }
        binding.rvStudents.adapter = adapter
        displayStudentsList()
    }
    private fun listItemClicked(student: Student){
        binding.apply {
            selectedStudent = student
            btnSave.text = "Update"
            btnClear.text = "Delete"
            isSelected = true
            etName.setText(selectedStudent.name)
            etEmail.setText(selectedStudent.email)
        }
    }

    private fun displayStudentsList(){
        viewModel.students.observe(this, {
            adapter.setList(it)
            adapter.notifyDataSetChanged()
        })
    }
    private fun saveStudentData(){
        binding.apply {
            viewModel.insertStudent(
                Student(
                    0,
                    etName.text.toString(),
                    etEmail.text.toString()
                )
            )
        }
    }
    private fun clearInput(){
        binding.apply {
            etName.setText("")
            etEmail.setText("")
        }
    }
}