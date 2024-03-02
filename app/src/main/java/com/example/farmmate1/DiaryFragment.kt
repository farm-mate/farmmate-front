package com.example.farmmate1


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.farmmate1.data.*
import com.example.farmmate1.databinding.FragmentDiaryBinding
import java.text.SimpleDateFormat
import java.util.*
import com.prolificinteractive.materialcalendarview.*
import com.prolificinteractive.materialcalendarview.format.TitleFormatter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DiaryFragment : Fragment(), DiaryDataListener {

    private var _binding: FragmentDiaryBinding? = null
    private val binding get() = _binding!!

    private lateinit var calendarView: MaterialCalendarView

    private var selectedDate: CalendarDay? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDiaryBinding.inflate(inflater, container, false)
        val view = binding.root

        calendarView = binding.diaryFragmentCal

        val today = CalendarDay.today()
        val oneDayDecorator = OneDayDecorator()

        calendarView.selectedDate = today
        calendarView.addDecorators(
            SundayDecorator(),
            SaturdayDecorator(),
            oneDayDecorator
        )

        calendarView.setTitleFormatter(object : TitleFormatter {
            override fun format(day: CalendarDay): CharSequence {
                // 원하는 형식으로 제목을 지정
                return "${day.year}년 ${day.month + 1}월" // month는 0부터 시작하므로 +1 해줍니다.
            }
        })

        // 캘린더 뷰에 날짜 선택 리스너 등록
        calendarView.setOnDateChangedListener { widget, date, selected ->
            // 선택된 날짜 정보를 다른 곳에서 활용할 수 있도록 저장
            selectedDate = date
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.diaryRegitstBtn.setOnClickListener {
            // selectedDate가 null이 아닌 경우에만 DiaryAddFragment를 생성하고 전환
            selectedDate?.let { date ->
                moveToAddDiaryFragment(date)
            }
        }
        //fetchDiaryListFromServer()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // 메모리 누수를 방지하기 위해 Fragment View에 대한 참조를 제거하여 가비지 컬렉터가 수거
        _binding = null
    }

    private fun moveToAddDiaryFragment(selectedDate: CalendarDay?) {
        selectedDate?.let { date ->
            val diaryAddFragment = DiaryAddFragment.newInstance(date.calendar)
            val transaction = parentFragmentManager
                .beginTransaction()
                .replace(R.id.main_fl, diaryAddFragment)
            transaction.commit()
        }
    }

    private fun fetchDiaryListFromServer() {

        val retrofit = RetrofitClient.instance
        val apiService = retrofit.create(ApiService::class.java)

        apiService.getDiaryList().enqueue(object : Callback<List<Diary>> {
            override fun onResponse(call: Call<List<Diary>>, response: Response<List<Diary>>) {
                if (response.isSuccessful) {
                    val diaries = response.body()
                    diaries?.let {
                        val (waterCheckedDates, fertilizerCheckedDates, pesticideCheckedDates) = checkDiariesForCheckboxes(diaries)
                        // 달력에 점 찍는 코드
                        val waterHashSet = HashSet(waterCheckedDates)
                        val fertilizerHashSet = HashSet(fertilizerCheckedDates)
                        val pesticideHashSet = HashSet(pesticideCheckedDates)

                        val decorators = mutableListOf<EventDecorator>()
                        decorators.add(EventDecorator(requireContext(), setOf("water"), waterHashSet))
                        decorators.add(EventDecorator(requireContext(), setOf("fertilizer"), fertilizerHashSet))
                        decorators.add(EventDecorator(requireContext(), setOf("pesticide"), pesticideHashSet))

                        // MaterialCalendarView에 EventDecorator 추가
                        calendarView.addDecorators(*decorators.toTypedArray())
                    }
                } else {
                    // 응답이 실패한 경우
                    Log.e("GetAllDiaries", "Error fetching diary info: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Diary>>, t: Throwable) {
                // 네트워크 요청 자체가 실패한 경우
                Log.e("GetAllDiaries", "Network error: ${t.message}")
            }
        })
    }

    private fun checkDiariesForCheckboxes(diaries: List<Diary>): Triple<MutableList<CalendarDay>, MutableList<CalendarDay>, MutableList<CalendarDay>> {
        val waterCheckedDates = mutableListOf<CalendarDay>()
        val fertilizerCheckedDates = mutableListOf<CalendarDay>()
        val pesticideCheckedDates = mutableListOf<CalendarDay>()

        for (diary in diaries) {
            val dateParts = diary.diaryDate.split("-").map { it.toInt() }
            val calendar = Calendar.getInstance()
            calendar.set(dateParts[0], dateParts[1] - 1, dateParts[2])
            val calendarDay = CalendarDay.from(calendar)

            if (diary.waterFlag) {
                waterCheckedDates.add(calendarDay)
            }
            if (diary.fertilizeFlag) {
                fertilizerCheckedDates.add(calendarDay)
            }
            if (diary.pesticideFlag) {
                pesticideCheckedDates.add(calendarDay)
            }
        }

        return Triple(waterCheckedDates, fertilizerCheckedDates, pesticideCheckedDates)
    }
//
//    private fun handleDiaryInfoResponse(response: Diary) {
//        requireActivity().runOnUiThread {
//            binding.diaryAddCbWater.isChecked = response.waterFlag
//            binding.diaryAddCbFert.isChecked = response.fertilizeFlag
//            binding.diaryAddCbPes.isChecked = response.pesticideFlag
//        }
//    }

}


