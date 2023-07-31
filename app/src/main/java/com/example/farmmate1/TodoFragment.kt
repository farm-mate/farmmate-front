import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.farmmate1.ApiService
import com.example.farmmate1.R
import com.example.farmmate1.data.WriteDiaryData
import com.example.farmmate1.network.ToDoListInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class TodoFragment : Fragment() {
    private lateinit var checkBoxWater: CheckBox
    private lateinit var checkBoxFertle: CheckBox
    private lateinit var editTextFertle: EditText
    private lateinit var editTextFertleUsage: EditText
    private lateinit var checkBoxPes: CheckBox
    private lateinit var editTextPes: EditText
    private lateinit var editTextPesUsage: EditText
    private lateinit var editTextMemo: EditText
    private lateinit var spinner: Spinner

    val retrofit = Retrofit.Builder()
        .baseUrl("http://localhost:1000/") // 서버의 기본 URL 설정
        .addConverterFactory(GsonConverterFactory.create()) // Gson을 사용해 JSON 데이터를 파싱
        .build()

    companion object {
        private const val ARG_DATE = "arg_date"

        fun newInstance(date: Calendar): TodoFragment {
            val args = Bundle().apply {
                putSerializable(ARG_DATE, date)
            }
            val fragment = TodoFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_todo, container, false)

        // 에딧 텍스트와 스피너 초기화
        checkBoxWater = view.findViewById(R.id.todo_fragment_cb_water)
        checkBoxFertle = view.findViewById(R.id.todo_cb_fertle)
        editTextFertle = view.findViewById(R.id.todo_et_fertle)
        editTextFertleUsage = view.findViewById(R.id.todo_et_fertle_usage)
        checkBoxPes = view.findViewById(R.id.todo_cb_pes)
        editTextPes = view.findViewById(R.id.todo_et_pes)
        editTextPesUsage = view.findViewById(R.id.todo_et_pes_usage)
        editTextMemo = view.findViewById(R.id.todo_et_memo)
        spinner = view.findViewById(R.id.todo_spinner_select)

        // 등록 버튼 클릭 시 데이터 서버로 전송
        view.findViewById<Button>(R.id.todo_register_btn).setOnClickListener {
            sendDataToServer()
        }

        // 전달받은 날짜 가져오기
        val selectedDate = arguments?.getSerializable(ARG_DATE) as? Calendar

        // 선택한 날짜를 TextView에 표시
        selectedDate?.let {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = dateFormat.format(it.time)
            view.findViewById<TextView>(R.id.todo_tv_date).text = formattedDate
        }

        return view
    }

    private fun sendDataToServer() {
        val apiService = ToDoListInterface.create()
        // 에딧 텍스트와 스피너에서 값 가져오기
        val waterCheckedValue = checkBoxWater.isChecked
        val fertleCheckedValue = checkBoxFertle.isChecked
        val fertleValue = editTextFertle.text.toString()
        val fertleUsageValue = editTextFertleUsage.text.toString()
        val pesCheckedValue = checkBoxPes.isChecked
        val pesValue = editTextPes.text.toString()
        val pesUsageValue = editTextPesUsage.text.toString()
        val memoValue = editTextMemo.text.toString()
        val spinnerValue = spinner.selectedItem.toString()


        // TODO: 데이터를 서버로 전송하는 코드 작성
        // 이 부분에서는 데이터를 서버로 전송하는 HTTP 요청을 보내는 로직을 작성해야 합니다.
        // 실제로 API 클라이언트 서비스를 구현하거나 Retrofit, OkHttp 등의 라이브러리를 사용할 수 있습니다.
        // 이 예시에서는 ApiClient와 ApiService라는 가상 클래스를 사용하여 데이터 전송을 가정하고 코드를 작성합니다.

        // Api 클라이언트 및 ApiService 초기화
        //val apiClient = ApiClient()
        //val apiService = apiClient.createService(ApiService::class.java)

        // 전송할 데이터 생성
        val data = HashMap<String, Any>()
        data["waterCheckedValue"] = waterCheckedValue
        data["fertleCheckedValue"] = fertleCheckedValue
        data["fertleValue"] = fertleValue
        data["fertleUsageValue"] = fertleUsageValue
        data["pesCheckedValue"] = pesCheckedValue
        data["pesValue"] = pesValue
        data["pesUsageValue"] = pesUsageValue
        data["memoValue"] = memoValue
        data["spinnerValue"] = spinnerValue

        apiService.sendData(data).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("전송 성공","전송성공")
                    // TODO: 전송 성공 처리 로직 작성
                } else {
                    // 전송 실패
                    // TODO: 전송 실패 처리 로직 작성
                    Log.d("전송 실패","전송실패")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // 전송 실패
                // TODO: 전송 실패 처리 로직 작성
                Log.d("전송 실패","전송실패")
            }
        })
    }

}


