package com.example.todayannouncement

import com.google.gson.annotations.SerializedName

// 데이터 저장에 특화된 클래스
// 데이터 클래스가 지원하는 함수들이 존재, 자동으로 제공 (copy(), hashCode()...))
data class Message (
    // 앱을 출시할 때를 대비하여, 난독화 방지
    @SerializedName("message")
    val message: String
)

