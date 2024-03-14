package com.example.farmmate1

import android.os.Parcel
import android.os.Parcelable

data class DiagnosisResult(
    val diseaseUuid: String,
    val plantName: String,
    val diseaseName: String,
    val diseaseCode: Int,
    val diseaseSymptom: String?,
    val diseaseCause: String?,
    val diseaseTreatment: String?,
    val diagnosisCode: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(diseaseUuid)
        parcel.writeString(plantName)
        parcel.writeString(diseaseName)
        parcel.writeInt(diseaseCode)
        parcel.writeString(diseaseSymptom)
        parcel.writeString(diseaseCause)
        parcel.writeString(diseaseTreatment)
        parcel.writeInt(diagnosisCode)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DiagnosisResult> {
        override fun createFromParcel(parcel: Parcel): DiagnosisResult {
            return DiagnosisResult(parcel)
        }

        override fun newArray(size: Int): Array<DiagnosisResult?> {
            return arrayOfNulls(size)
        }
    }
}
