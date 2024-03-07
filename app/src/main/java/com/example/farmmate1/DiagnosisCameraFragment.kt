package com.example.farmmate1

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.content.Intent
import android.provider.MediaStore
import android.app.Activity
import java.io.File
import android.graphics.Bitmap
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.example.farmmate1.databinding.FragmentDiagnosisCameraBinding
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.FileOutputStream
import java.io.IOException
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DiagnosisCameraFragment : Fragment() {

    private val REQUEST_IMAGE_CAPTURE = 101
    private val CAMERA_PERMISSION_CODE = 102
    private val REQUEST_IMAGE_FROM_GALLERY = 103
    private val REQUEST_PERMISSION_CODE = 104

    private var imagePath: String = ""

    private var _binding: FragmentDiagnosisCameraBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDiagnosisCameraBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val selectedCrop = arguments?.getString("selectedCrop")
        binding.diagnosisCameraTvPlant.text = "진단 할 식물: $selectedCrop"

        binding.diagnosisCameraBtnOpencam.setOnClickListener{
            checkCameraPermission()
        }

        binding.diagnosisCameraBtnGallery.setOnClickListener {
            getAlbum()
        }

        // 초기에 진단하기 버튼 비활성화
        binding.diagnosisCameraBtnNext.visibility = View.INVISIBLE

        binding.diagnosisCameraBackIb.setOnClickListener {
            moveToDiagnosisFragment()
        }

        binding.diagnosisCameraBtnNext.setOnClickListener {
            // 서버에 이미지 보내기
            sendDiagnosisToServer(selectedCrop)
            moveToDiagnosisResultFragment()
        }

    }

    private fun checkPermission(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        val storagePermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        return cameraPermission && storagePermission
    }

    private fun checkCameraPermission() {
        // 카메라 권한이 부여되었는지 확인
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 권한이 없다면 권한 요청
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        } else {
            // 권한이 이미 허용되었다면 사진 촬영 진행
            dispatchTakePictureIntent()
        }
    }

    // 권한이 없다면 권한 요청 > 요청 결과 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 허용되면 사진 촬영 진행
                dispatchTakePictureIntent()
            } else {
                // 권한이 거부되었을 때 처리
                Toast.makeText(requireContext(), "권한이 필요합니다", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    // 사진 찍기 후 결과 이미지 처리
                    saveCapturedImage(data)
                }
                REQUEST_IMAGE_FROM_GALLERY -> {
                    // 갤러리에서 이미지 선택 후 처리
                    saveGalleryImage(data)
                }
            }
        }
    }

    private fun dispatchTakePictureIntent() {
//        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
//            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
//                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
//            }
//        }
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
    }

    fun getAlbum() {
        if (checkPermission()) {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_IMAGE_FROM_GALLERY)
        } else {
            requestPermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                REQUEST_PERMISSION_CODE
            )
        }
    }

    private fun saveCapturedImage(data: Intent?) {
        if (data != null && data.extras != null) {
            val imageBitmap = data.extras!!.get("data") as Bitmap

            // 이미지를 파일로 저장
            imagePath = saveImageToFile(imageBitmap)

            // 이미지를 ImageView에 표시
            binding.diagnosisCameraIvImage.setImageBitmap(imageBitmap)
            binding.diagnosisCameraBtnNext.visibility = View.VISIBLE
        }
    }

    private fun saveGalleryImage(data: Intent?) {
        val imageUri = data?.data
        val imageBitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imageUri)

        // 이미지를 파일로 저장
        imagePath = saveImageToFile(imageBitmap)

        // 이미지를 ImageView에 표시
        binding.diagnosisCameraIvImage.setImageBitmap(imageBitmap)
        binding.diagnosisCameraBtnNext.visibility = View.VISIBLE
    }

    private fun saveImageToFile(bitmap: Bitmap): String {
        val filesDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File(filesDir, "captured_image.jpg")

        try {
            FileOutputStream(imageFile).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.flush()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return imageFile.absolutePath
    }

    private fun moveToDiagnosisFragment() {
        val transaction = parentFragmentManager
            .beginTransaction()
            .replace(R.id.main_fl, DiagnosisFragment())
        transaction.commit()
    }

    private fun moveToDiagnosisResultFragment() {
        val transaction = parentFragmentManager
            .beginTransaction()
            .replace(R.id.main_fl, DiagnosisResultFragment())
        transaction.commit()
    }

    fun sendDiagnosisToServer(plantType: String?) {

        val retrofit = RetrofitClient.instance
        val apiService = retrofit.create(ApiService::class.java)

        val imageBase64 = encodeImageToBase64(imagePath)

        try {
            val plantTypeBody = RequestBody.create(MediaType.parse("text/plain"), plantType)

            val imageRequestBody = RequestBody.create(MediaType.parse("image/*"), imageBase64)
            val imageBody = MultipartBody.Part.createFormData("image", "image.jpg", imageRequestBody)



            apiService.postDiagnosis(plantTypeBody, imageBody)
                .enqueue(object : Callback<DiagnosisPost> {
                    override fun onResponse(
                        call: Call<DiagnosisPost>,
                        response: Response<DiagnosisPost>
                    ) {
                        // 요청 성공 시 처리
                        if (response.isSuccessful) {
                            val diagnosisPost = response.body()
                            // 서버로부터의 응답 처리
                        } else {
                            // 요청은 성공했으나 서버에서 오류 응답
                        }
                    }

                    override fun onFailure(call: Call<DiagnosisPost>, t: Throwable) {
                        // 요청 실패 시 처리
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun encodeImageToBase64(imagePath: String): String {
        val imageFile = File(imagePath)
        val byteArrayOutputStream = ByteArrayOutputStream()

        try {
            val fileInputStream = FileInputStream(imageFile)
            val buffer = ByteArray(1024)
            var bytesRead = 0
            while (fileInputStream.read(buffer).also { bytesRead = it } != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead)
            }
            fileInputStream.close()

            val byteArray = byteArrayOutputStream.toByteArray()
            return Base64.encodeToString(byteArray, Base64.DEFAULT)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                byteArrayOutputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return ""
    }

}