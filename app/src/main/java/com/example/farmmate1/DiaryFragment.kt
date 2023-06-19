package com.example.farmmate1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.farmmate1.network.TodoListInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DiaryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DiaryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var fragment2: WriteDiaryFragment
    private lateinit var calendarView: CalendarView
    private lateinit var writeButton: Button
    private lateinit var listView : ListView
    private lateinit var todoListInterface : TodoListInterface

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_diary, container, false)

        calendarView = view.findViewById(R.id.diary_calview)
        listView = view.findViewById(R.id.diary_todo_list_view)


        // 프래그먼트 인스턴스 생성
        fragment2 = WriteDiaryFragment()
        calendarView = view.findViewById(R.id.diary_calview)
        writeButton = view.findViewById(R.id.diary_regitst_btn)

        //캘린더 날짜 선택 이벤트 처리
        calendarView.setOnDateChangeListener{_, year, month, dayOfMonth ->
            val selectDate = Calendar.getInstance().apply{
                set(year, month, dayOfMonth)
            }
            val todoList = getTodoList(selectDate)
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, todoItems)


            listView.adapter=adapter
        }
    }
    //TodolistInterface 인터페이스 구현
    private fun getTodoList(date:Calendar) {
        //선택 날짜에 해당하는 할일 목록 가져오는 로직 추가 구현
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val selectedDate = formatter.format(date.time)
        val call = todoListInterface.getTodos(selectedDate)
        call.enqueue(object : Callback<List<TodoItem>>) {
            override fun onResponse(
                call: Call<List<TodoItem>>,
                response: Response<List<TodoItem>>
            ) {
                if (response.isSuccessful) {
                    val todoItems = response.body()
                    // todoItems를 사용하여 UI를 업데이트합니다.
                    // 예: 리스트뷰에 할 일(todo) 항목을 표시합니다.
                } else {
                    // 응답이 실패한 경우에 대한 처리를 수행합니다.
                }
            }

        }
        override fun onFailure(call: Call<List<TodoItem>>, t: Throwable) {
        }
    })
        }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        view.findViewById<Button>(R.id.diary_regitst_btn).setOnClickListener {
            val selectedDate = Calendar.getInstance().apply {
                timeInMillis = calendarView.date
            }

            val fragmentManager = requireActivity().supportFragmentManager
            val writeDiaryFragment = WriteDiaryFragment.newInstance(selectedDate)

            fragmentManager.beginTransaction()
                .replace(R.id.main_fl, writeDiaryFragment)
                .addToBackStack(null)
                .commit()
        }
    }
    override fun onDiaryDataReceived(date: Calendar, data: String) {
        TODO("Not yet implemented")
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DiaryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DiaryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }






