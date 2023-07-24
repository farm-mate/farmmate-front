import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.farmmate1.R
import com.example.farmmate1.WriteDiaryFragment
import java.text.SimpleDateFormat
import java.util.*

class DiaryFragment : Fragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var fragment2: WriteDiaryFragment
    private lateinit var writeButton: Button
    private lateinit var listView: ListView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_diary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendarView = view.findViewById(R.id.diary_calview) // 레이아웃의 캘뷰를 초기화
        listView = view.findViewById(R.id.diary_todo_list_view)//레이아웃 리스트뷰 초기화

        fragment2 = WriteDiaryFragment()
        calendarView = view.findViewById(R.id.diary_calview) // 레이아웃의 CalendarView를 초기화합니다.
        writeButton = view.findViewById(R.id.diary_regitst_btn) // 레이아웃의 버튼을 초기화합니다.


        // 캘린더 날짜 선택 이벤트 처리
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDateMillis = calendarView.date
            val selectDate = Calendar.getInstance().apply {
               timeInMillis = selectedDateMillis
            }


            val todoList = getTodoList(selectDate)
            val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,todoList)
            listView.adapter = adapter//할일 목록 리스트뷰 표시

            showCircleForDate(selectDate) // 선택된 날짜에 동그라미를 표시하는 메서드 호출
        }

        writeButton.setOnClickListener {
            val selectDate = Calendar.getInstance().apply{
                timeInMillis = calendarView.date
            }
            //TODO:선택된 날짜 데이터 저장하고 다이어리로 돌아가기
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

    }

    // TodoInterface 인터페이스 구현
    private fun getTodoList(date: Calendar): List<String> {
        // 선택 날짜에 해당하는 할일 목록 가져오는 로직 추가 구현
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val selectedDate = formatter.format(date.time)

        return when (selectedDate) {
            "2023-06-19" -> listOf("딸기 물주기", "고추 검사", "파프리카 농약")
            "2023-06-20" -> listOf("딸기 농약 사러 가기", "고추 물주기", "가지 검사")
            else -> emptyList()
        }
    }

    private fun showCircleForDate(date: Calendar) {
        // 해당 날짜에 동그라미를 표시하는 로직을 여기에 추가합니다.
        val hasEvent = checkForEvent(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH))

        if (hasEvent) {
            // CircleView를 동적으로 생성하여 추가합니다.
            val circleView = CircleView(requireContext())
            // CircleView의 레이아웃 파라미터 설정 (원하는 크기 등)
            // val layoutParams = ViewGroup.LayoutParams(width, height)
            // circleView.layoutParams = layoutParams

            // CircleView를 CalendarView의 하위 뷰로 추가합니다.
            calendarView.addView(circleView)
        }
    }

    // 일정이 있는지 체크하는 메서드 (임의로 예시를 보여줍니다. 실제로는 데이터를 확인해야 합니다.)
    private fun checkForEvent(year: Int, month: Int, dayOfMonth: Int): Boolean {
        // 여기서 해당 날짜에 일정이 있는지 데이터를 확인하고 결과를 반환합니다.
        // 이 예제에서는 임의로 7월 24일에만 일정이 있다고 가정합니다.
        return year == 2023 && month == Calendar.JULY && dayOfMonth == 24
    }
}
