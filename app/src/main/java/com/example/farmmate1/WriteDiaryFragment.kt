//writeDiary 서버 테스트

package com.example.farmmate1


import TodoFragment
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContentProviderCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import com.example.farmmate1.data.WriteDiaryData
import com.example.farmmate1.network.WriteDiaryInterface
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Use the [WriteDiaryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WriteDiaryFragment : Fragment() {

    private lateinit var editTextFertle: EditText
    private lateinit var editTextFertleUsage: EditText
    private lateinit var editTextPes: EditText
    private lateinit var editTextPesUsage: EditText
    private lateinit var spinner: Spinner
    private lateinit var saveButton: Button
    private lateinit var imageView: ImageView
    private lateinit var writeDiaryInterface: WriteDiaryInterface
    private lateinit var writeDiaryTvDate: TextView

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

    // TODO: Rename and change types of parameters
    private lateinit var diaryDataListener: DiaryDataListener
    private lateinit var WriteDiaryTvDate: TextView
    private lateinit var data: String


    private var param1: String? = null
    private var param2: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_write_diary, container, false)

        editTextFertle = view.findViewById(R.id.write_diary_et_fertle)
        editTextFertleUsage = view.findViewById(R.id.write_diary_et_fertle_usage)
        editTextPes = view.findViewById(R.id.write_diary_et_pes)
        editTextPesUsage = view.findViewById(R.id.write_diary_et_pes_usage)
        spinner = view.findViewById(R.id.write_diary_spinner_select)
        saveButton = view.findViewById(R.id.write_diary_register_btn)


//        saveButton.setOnClickListener {
//            sendDataToServer()
//        }

        // Retrofit 인스턴스 초기화
        val gson: Gson = GsonBuilder().setLenient().create()
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://bd301c46-a122-477e-ace3-1d7b47fa9610.mock.pstmn.io") // 서버 URL을 여기에 입력하세요
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        writeDiaryInterface = retrofit.create(WriteDiaryInterface::class.java)

        // Retrofit을 사용하여 ApiService 인터페이스 구현체 생성

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        writeDiaryTvDate = view.findViewById(R.id.write_diary_tv_date)
        writeDiaryTvDate.text = getCurrentDate()

        //spinner
        val spinner: Spinner = view.findViewById(R.id.write_diary_spinner_select)
        val items = listOf("모두","포도", "딸기", "오이","파프리카","토마토", "고추") // 스피너에 표시될 데이터 리스트
        val spinnerAdapter = WriteDiayrSpinnerAdapter(requireContext(), items)

        spinner.adapter = spinnerAdapter

        // 스피너의 아이템 선택 리스너 설정
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position) as String
                // 선택된 항목에 대한 동작 수행
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // 아무 항목도 선택되지 않았을 때 동작 수행
            }
        }

        val selectedDate = arguments?.getSerializable(ARG_DATE) as? Calendar
        selectedDate?.let {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = dateFormat.format(it.time)
            writeDiaryTvDate.text = formattedDate
        } ?: run {
            writeDiaryTvDate.text = "에러 발" // 날짜가 없을 경우에 대한 처리 (빈 문자열로 표시)
        }

        view.findViewById<Button>(R.id.write_diary_title_todobtn).setOnClickListener {
            val fragmentManger = requireActivity().supportFragmentManager
            val date = Calendar.getInstance()
            val fragment2 = WriteDiaryFragment.newInstance(date)

            //메인 액티비티가 DiaryDataListener를 구현했을 경우 WriteDiaryFragment에 mainActivity 인스턴스 전달
            if (requireActivity() is DiaryDataListener) {
                fragment2.diaryDataListener = requireActivity() as DiaryDataListener
            }
            fragmentManger.beginTransaction()
                .replace(R.id.main_fl,fragment2)
                .addToBackStack(null)
                .commit()


        }


        // DiaryFragment의 버튼 클릭 이벤트에서 WriteDiaryFragment로 전환하는 코드
        view.findViewById<Button>(R.id.write_diary_title_todobtn).setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            val fragment2 = WriteDiaryFragment()



            // MainActivity가 DiaryDataListener를 구현했을 경우, WriteDiaryFragment에 MainActivity 인스턴스를 전달
            if (requireActivity() is DiaryDataListener) {
                fragment2.diaryDataListener = requireActivity() as DiaryDataListener
            }

            fragmentManager.beginTransaction()
                .replace(R.id.main_fl, fragment2)
                .addToBackStack(null)
                .commit()
        }

        // WriteDiaryFragment 내부에서 사용하고자 하는 버튼의 ID를 확인하고 수정
        val button: Button = view.findViewById(R.id.write_diary_title_todobtn)
        button.setOnClickListener {
            // 전환할 프래그먼트 생성
            val newFragment = TodoFragment()

            // 선택한 날짜 가져오기
            val selectedDate = arguments?.getSerializable(ARG_DATE) as? Calendar

            // 선택한 날짜를 TodoFragment에 전달
            selectedDate?.let { date ->
                val args = Bundle().apply {
                    putSerializable("selected_date", date)
                }
                newFragment.arguments = args
            }

            // 프래그먼트 전환을 위해 FragmentManager 사용
            val fragmentManager = requireActivity().supportFragmentManager

            // 트랜잭션 시작
            val transaction = fragmentManager.beginTransaction()

            // 프래그먼트 전환
            transaction.replace(R.id.main_fl, newFragment)

            // 트랜잭션 커밋
            transaction.commit()
        }
        // WriteDiaryFragment 내부에서 사용하고자 하는 버튼의 ID를 확인하고 수정

        //back button
        val backButton: ImageButton = view.findViewById(R.id.write_diary_fragment_back_ib)
        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }
    //가장 최근의 날짜 가져오는 함수
    private fun getCurrentDate(): String {
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(currentDate)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            diaryDataListener = context as DiaryDataListener
        } catch (e: ClassCastException) {
            throw java.lang.ClassCastException("$context must implement DiaryDatalistener")
        }
    }

//    private fun sendDataToServer() {
//        val fertleValue = editTextFertle.text.toString()
//        val fertleUsageValue = editTextFertleUsage.text.toString()
//        val pesValue = editTextPes.text.toString()
//        val pesUsageValue = editTextPesUsage.text.toString()
//        val spinnerSelectedItem = spinner.selectedItem.toString()
//
//        // 데이터 객체 생성
//        val data = WriteDiaryData(spinnerSelectedItem,fertleValue,fertleUsageValue,pesValue,pesUsageValue)
//
//        // 데이터 전송 요청
//        val call: Call<Void> = writeDiaryInterface.sendData(data)
//        call.enqueue(object : Callback<Void> {
//            override fun onResponse(call: Call<Void>, response: Response<Void>) {
//                if (response.isSuccessful) {
//                    Toast.makeText(getActivity(),"Registered",Toast.LENGTH_SHORT).show();
//                    // 서버 응답이 성공적으로 받아졌을 때의 처리
//                } else {
//                    // 서버 응답이 실패일 때의 처리
//                }
//            }
//
//            override fun onFailure(call: Call<Void>, t: Throwable) {
//                // 네트워크 오류 처리
//            }
//        })
//    }
}













