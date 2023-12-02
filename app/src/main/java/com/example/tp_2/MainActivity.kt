package com.example.tp_2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

data class Student(
    val firstName: String,
    val lastName: String,
    val gender: String,
    val subject: String
)

class StudentAdapter(private val data: List<Student>) :
    RecyclerView.Adapter<StudentAdapter.StudentViewHolder>(), Filterable {
    private var dataFilterList = ArrayList<Student>()

    init {
        dataFilterList.addAll(data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.student_item, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = dataFilterList[position]
        holder.bind(student)
    }

    override fun getItemCount(): Int {
        return dataFilterList.size
    }

    // Implement the Filterable interface
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                val filterResults = FilterResults()

                if (charSearch.isEmpty()) {
                    dataFilterList.clear()
                    dataFilterList.addAll(data) // Restore the original data
                } else {
                    val resultList = ArrayList<Student>()
                    for (student in data) {
                        // Filter based on the selected subject
                        if (student.subject.equals(charSearch, ignoreCase = true)) {
                            resultList.add(student)
                        }
                    }
                    dataFilterList.clear()
                    dataFilterList.addAll(resultList)
                }

                filterResults.values = dataFilterList
                filterResults.count = dataFilterList.size
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                dataFilterList = results?.values as ArrayList<Student>
                notifyDataSetChanged()
            }
        }
    }

    inner class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val studentImage: ImageView = itemView.findViewById(R.id.studentImage)
        private val studentName: TextView = itemView.findViewById(R.id.studentName)

        fun bind(student: Student) {
            studentName.text = "${student.firstName} ${student.lastName}"
            // Set the image based on the student's gender
            if (student.subject.equals("Cours", ignoreCase = true)) {
                studentImage.setImageResource(R.drawable.male_image)
            } else {
                studentImage.setImageResource(R.drawable.woman_image)
            }
        }
    }
}


class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var studentAdapter: StudentAdapter
    private val students = listOf(
        Student("John", "Doe", "male", "TP"),
        Student("Jane", "Smith", "female", "Cours"),
        Student("Rima", "Smith", "female", "Cours"),
        Student("Tom", "Khelif", "female", "TP"),
        Student("Nada", "Smith", "female", "TP")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val spinner: Spinner by lazy { findViewById(R.id.spinner) }
        val matieres = listOf("Cours", "TP")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, matieres)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the student adapter
        studentAdapter = StudentAdapter(students)
        recyclerView.adapter = studentAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedSubject = spinner.selectedItem.toString()
                studentAdapter.filter.filter(selectedSubject)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                // Handle when nothing is selected (if needed)
            }
        }
    }
}
