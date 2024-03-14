package com.example.farmmate1

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.farmmate1.databinding.FragmentDiaryBinding
import java.util.*
import com.prolificinteractive.materialcalendarview.*
import com.prolificinteractive.materialcalendarview.format.TitleFormatter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DiaryFragment : Fragment() {

    private var _binding: FragmentDiaryBinding? = null
    private val binding get() = _binding!!

    private var diaryList: ArrayList<DiaryGet>? = null
    private lateinit var calendarView: MaterialCalendarView
    private var selectedDate: CalendarDay? = null
    private var selectedPlantName: String? = null
    //private var filteredDiaryList: List<DiaryGet>? = null

    private lateinit var diaryUuid: String

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
        calendarView.setDateSelected(today, true)
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

//        // 캘린더 뷰에 날짜 선택 리스너 등록
//        calendarView.setOnDateChangedListener { widget, date, selected ->
//            // 선택된 날짜 정보를 다른 곳에서 활용할 수 있도록 저장
//            selectedDate = date
//        }

        // 상단바 get
        val retrofit = RetrofitClient.instance
        val apiService = retrofit.create(ApiService::class.java)

        // SharedPreferences에서 디바이스 ID 가져오기
        val sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val deviceId: String = sharedPreferences.getString(DEVICE_ID_KEY, "") ?: ""

        apiService.getPlantList(deviceId).enqueue(object : Callback<List<PlantGet>> {
            override fun onResponse(call: Call<List<PlantGet>>, response: Response<List<PlantGet>>) {
                val plantList = response.body() as? ArrayList<PlantGet>
                if (plantList != null) {
                    Log.d("DiaryFragment", "$plantList")

                    setButtonTextForLinearLayout(plantList)
                    // 동적으로 버튼 추가
                    for (plant in plantList) {
                        Log.d("DiaryFragment", "${plant.plant_name}")
                    }

                    for (i in 0 until 5) {
                        val button = binding.diaryLinearlayout.getChildAt(i) as? Button
                        button?.setOnClickListener {
                            // 클릭된 버튼을 강조하기 위해 색상 변경
                            onButtonClicked(button)
                            selectedPlantName = button?.text.toString()
                            Log.d("DiaryPlantName","$selectedPlantName")
                            fetchDiaryListFromServer()
                        }
                    }

                    // 동적으로 생성된 버튼에 클릭 이벤트 처리기 추가
                    for (i in 5 until binding.diaryLinearlayout.childCount) {
                        val button = binding.diaryLinearlayout.getChildAt(i) as? Button
                        button?.setOnClickListener {
                            // 클릭된 버튼을 강조하기 위해 색상 변경
                            onButtonClicked(button)
                            selectedPlantName = button?.text.toString()
                            Log.d("DiaryPlantName","$selectedPlantName")
                            fetchDiaryListFromServer()
                        }
                    }

                    // 맨 처음 버튼을 선택 상태로 지정
                    val firstButton = binding.diaryLinearlayout.getChildAt(0) as? Button
                    firstButton?.isSelected = true
                    selectedPlantName = firstButton?.text.toString()
                    Log.d("DiaryPlantName","$selectedPlantName")
                    fetchDiaryListFromServer()

                } else {
                    // API 요청 실패 처리
                    Log.e("DiaryFragment", "Failed to fetch plant list: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<PlantGet>>, t: Throwable) {
                // 통신 오류 처리
                Log.e("DiaryFragment", "Network error: ${t.message}")
            }
        })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.diaryRegitstBtn.setOnClickListener {
            // selectedDate가 null이 아닌 경우에만 DiaryAddFragment를 생성하고 전환
            Log.d("-------","$selectedDate")
            selectedDate?.let { date ->
                moveToAddDiaryFragment(date)
            }
        }

        binding.diaryContentLayout.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("diaryUuid", diaryUuid)
            bundle.putString("selectedPlantName", selectedPlantName)

            val diaryInfoFragment = DiaryInfoFragment()
            diaryInfoFragment.arguments = bundle

            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.main_fl, diaryInfoFragment)
                .commit()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()

        // 메모리 누수를 방지하기 위해 Fragment View에 대한 참조를 제거하여 가비지 컬렉터가 수거
        _binding = null
    }

    // LinearLayout에 버튼을 추가하는 함수
    fun setButtonTextForLinearLayout(plantList: List<PlantGet>) {
        for (i in 0 until plantList.size) {
            val plantName = plantList[i].plant_name
            if (i < 5) {
                // 처음 다섯 개의 버튼에 텍스트만 할당
                val button = binding.diaryLinearlayout.getChildAt(i) as? Button
                button?.text = plantName ?: ""
            } else {
                // 여섯 번째 요소부터 동적으로 버튼을 추가하며 텍스트 할당
                val button = Button(requireContext())
                button.text = plantName ?: ""
                val layoutParams = LinearLayout.LayoutParams(
                    resources.getDimensionPixelSize(R.dimen.button_width),
                    resources.getDimensionPixelSize(R.dimen.button_height)
                )
                val marginHorizontalPx = resources.getDimensionPixelSize(R.dimen.button_margin_horizontal)
                layoutParams.setMargins(marginHorizontalPx, 0, marginHorizontalPx, 0) // 좌우 마진과 상단 마진을 dimens.xml에서 가져온 값으로 설정
                button.layoutParams = layoutParams
                button.setTextColor(Color.BLACK) // 텍스트 색상 설정
                button.setBackgroundResource(R.drawable.rounded_btn)
                button.gravity = Gravity.CENTER

                button.setPadding(0, -3, 0, 0)
                layoutParams.weight = 1f

                button.setOnClickListener {
                    // 버튼이 클릭되었을 때 수행할 작업 추가
                }
                binding.diaryLinearlayout.addView(button)
            }
        }
    }

    // 모든 버튼을 클릭했을 때 호출되는 메서드
    fun onButtonClicked(clickedButton: Button) {
        // 모든 버튼을 탐색하면서 클릭된 버튼인지 확인하고 상태를 변경합니다.
        for (i in 0 until binding.diaryLinearlayout.childCount) {
            val button = binding.diaryLinearlayout.getChildAt(i) as? Button
            button?.isSelected = (button == clickedButton)
        }

        // 일지 요약 초기화
        binding.diaryContentDate.text = ""
        binding.diaryContentWeather.text = "일지를 등록하세요"
        binding.diaryContentMemo.text = ""
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

        apiService.getDiaryList().enqueue(object : Callback<List<DiaryGet>> {
            override fun onResponse(call: Call<List<DiaryGet>>, response: Response<List<DiaryGet>>) {
                if (response.isSuccessful) {
                    val diaryList = response.body()
                    Log.d("DiaryFragment---", "${diaryList?.joinToString()}")

                    diaryList?.let {
                        list -> val filteredDiaryList = mutableListOf<DiaryGet>()

                        for (diary in diaryList) {
                            val plantName = diary.plant?.plant_name

                            if (plantName == selectedPlantName) {
                                //Log.d("Diary---",  diary.toString())
                                Log.d("Diary---", "Diary UUID: ${diary.diary_uuid}, Diary Date: ${diary.diary_date}, Plant Name: ${diary.plant?.plant_name}")
                                filteredDiaryList.add(diary)
                                Log.d("Diary---", "$filteredDiaryList")
                            }

                        }

                        val (waterCheckedDates, fertilizerCheckedDates, pesticideCheckedDates) = checkDiariesForCheckboxes(filteredDiaryList).let {
                            Triple(it.first.toSet(), it.second.toSet(), it.third.toSet())
                        }

                        updateEventDecorators(waterCheckedDates, fertilizerCheckedDates, pesticideCheckedDates)

                        calendarView.setOnDateChangedListener { widget, date, selected ->
                            // 선택된 날짜를 이용하여 해당 다이어리 정보를 가져옴
                            selectedDate = date
                            displayDiaryInfo(date, filteredDiaryList)
                        }
                    }
                } else {
                    // 응답이 실패한 경우
                    Log.e("GetAllDiaries", "Error fetching diary info: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<DiaryGet>>, t: Throwable) {
                // 네트워크 요청 자체가 실패한 경우
                Log.e("GetAllDiaries", "Network error: ${t.message}")
            }
        })
    }

    private fun checkDiariesForCheckboxes(diaryList: List<DiaryGet>): Triple<MutableList<CalendarDay>, MutableList<CalendarDay>, MutableList<CalendarDay>> {
        val waterCheckedDates = mutableListOf<CalendarDay>()
        val fertilizerCheckedDates = mutableListOf<CalendarDay>()
        val pesticideCheckedDates = mutableListOf<CalendarDay>()

        for (diary in diaryList) {
            if (diary.diary_date != null) {
                val dateParts = diary.diary_date.split("-").map { it.toInt() }
                val calendar = Calendar.getInstance()
                calendar.set(dateParts[0], dateParts[1] - 1, dateParts[2])
                val calendarDay = CalendarDay.from(calendar)

                if (diary.water_flag) {
                    waterCheckedDates.add(calendarDay)
                    Log.d("CheckDiary", "Water checked on date: $calendarDay")
                }
                if (diary.fertilizer_flag) {
                    fertilizerCheckedDates.add(calendarDay)
                    Log.d("CheckDiary", "Fertilizer checked on date: $calendarDay")
                }
                if (diary.pesticide_flag) {
                    pesticideCheckedDates.add(calendarDay)
                    Log.d("CheckDiary", "Pesticide checked on date: $calendarDay")
                }
            }
        }
        Log.d("CheckDiary","Triple: $waterCheckedDates, $fertilizerCheckedDates, $pesticideCheckedDates")

        return Triple(waterCheckedDates, fertilizerCheckedDates, pesticideCheckedDates)
    }

    private var eventDecorators: MutableList<EventDecorator> = mutableListOf()

    private fun updateEventDecorators(waterCheckedDates: Set<CalendarDay>, fertilizerCheckedDates: Set<CalendarDay>, pesticideCheckedDates: Set<CalendarDay>) {
        // 기존에 추가된 데코레이터 제거
        for (decorator in eventDecorators) {
            calendarView.removeDecorator(decorator)
        }

        // 각 날짜에 대한 항목들을 모으기
        val dateToItems = mutableMapOf<CalendarDay, MutableSet<String>>()

        // 물 체크된 날짜 추가
        for (date in waterCheckedDates) {
            val items = dateToItems.getOrPut(date) { mutableSetOf() }
            items.add("water")
        }

        // 비료 체크된 날짜 추가
        for (date in fertilizerCheckedDates) {
            val items = dateToItems.getOrPut(date) { mutableSetOf() }
            items.add("fertilizer")
        }

        // 농약 체크된 날짜 추가
        for (date in pesticideCheckedDates) {
            val items = dateToItems.getOrPut(date) { mutableSetOf() }
            items.add("pesticide")
        }

        // 새로운 이벤트 데코레이터 생성 및 추가
        val newDecorators = dateToItems.map { (date, items) ->
            EventDecorator(requireContext(), items, date)
        }

        // 캘린더에 새로운 데코레이터 추가
        for (decorator in newDecorators) {
            calendarView.addDecorator(decorator)
        }

        // 업데이트된 데코레이터 목록 저장
        eventDecorators = newDecorators.toMutableList()
    }

    private fun displayDiaryInfo(selectedDate: CalendarDay, filteredDiaryList: List<DiaryGet>) {
        val month = selectedDate.month + 1
        val day = selectedDate.day
        val formattedDate = String.format("%d-%02d-%02d", selectedDate.year, month, day)

        filteredDiaryList?.let { diaryList ->
            val matchingDiary = diaryList.find { diary ->
                diary.diary_date == formattedDate
            }
            if (matchingDiary != null) {
                // 다이어리 정보를 텍스트 뷰에 표시
                binding.diaryContentDate.text = matchingDiary.diary_date
                binding.diaryContentWeather.text = matchingDiary.plant_weather
                binding.diaryContentMemo.text = matchingDiary.memo
                diaryUuid = matchingDiary.diary_uuid

            } else {
                // 매칭되는 다이어리가 없는 경우
                Log.d("DiaryInfo", "No matching diary found for selected date: $formattedDate")

                binding.diaryContentDate.text = ""
                binding.diaryContentWeather.text = "일지를 등록하세요"
                binding.diaryContentMemo.text = ""
            }
        }
    }
}


