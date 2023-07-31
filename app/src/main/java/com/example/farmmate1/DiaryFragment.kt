package com.example.farmmate1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.ListView
import androidx.fragment.app.Fragment
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
class DiaryFragment : Fragment(), DiaryDataListener {

    private lateinit var fragment2: WriteDiaryFragment
    private lateinit var calendarView: CalendarView
    private lateinit var writeButton: Button
    private lateinit var listView : ListView

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
            val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1, todoList)
            listView.adapter=adapter
        }


        //버튼 클릭시 이벤트 처리
        writeButton.setOnClickListener {
            val selectDate = Calendar.getInstance().apply{
                timeInMillis = calendarView.date
            }
            //TODO: 선택된 날짜에 데이터 저장
        }

        //캘린더 날짜 선택 이벤트 처리
        calendarView.setOnDateChangeListener{_, year, month, dayOfMonth->
            val selectedDate = Calendar.getInstance().apply{
                set(year,month,dayOfMonth)
            }
            val todoList = getTodoList(selectedDate)
            val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1, todoList )
            listView.adapter = adapter

          /*  val hasData = checkDataExist(selectedDate)
            if (hasData){

            }*/
        }



        // 버튼 클릭 시 Fragment2로 전환
        view.findViewById<Button>(R.id.diary_regitst_btn).setOnClickListener {
            // 프래그먼트 전환을 위해 액티비티의 프래그먼트 매니저를 사용
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.main_fl, fragment2)
                .addToBackStack(null) // 이전 프래그먼트로 돌아가기 위해 백 스택에 추가
                .commit()
        }

        return view
    }
    //TodoInterface 인터페이스 구현
    private fun getTodoList(date:Calendar):List<String>{
        //선택 날짜에 해당하는 할일 목록 가져오는 로직 추가 구현
        val formatter = SimpleDateFormat("yyyy-MM-dd",Locale.getDefault())
        val selectedDate = formatter.format(date.time)

        return when(selectedDate){
            "2023-06-19"->listOf("딸기 물주기","고추 검사","파프리카 농약")
            "2023-06-20"->listOf("딸기 농약 사러 가기","고추 물주기","가지 검사")
            else-> emptyList()
        }
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
        private const val ARG_DATE = "arg_date"

        fun newInstance(date: Calendar): WriteDiaryFragment {
            val args = Bundle().apply {
                putSerializable(ARG_DATE, date)
            }
            val fragment = WriteDiaryFragment()
            fragment.arguments = args
            return fragment
        }


    }



}


