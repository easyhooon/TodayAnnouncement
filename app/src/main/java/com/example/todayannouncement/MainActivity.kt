package com.example.todayannouncement

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.example.todayannouncement.databinding.ActivityMainBinding
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.lang.Exception
import java.net.ServerSocket
import java.net.Socket

// client 역할
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Okhttp 를 통한 데이터를 받아오는 방법
        // 하나의 클라이언트
        val client = OkHttpClient()
        var serverHost = ""

        // 10.0.0.2 를 입력
        // https://developer.android.com/studio/run/emulator-networking?hl=ko
        binding.serverHostEditText.addTextChangedListener {
            serverHost = it.toString()
        }

        binding.confirmButton.setOnClickListener {
            // request 를 만들어둔 다음 client 에 request 의 call 을 요청하는 방식으로 구현
            // request 는 builder 패턴으로 구현(alertDialog 를 구현하는 방식과 같음)
            val request: Request = Request.Builder()
                // 실제기기 (pc 와 핸드폰이 같은 와이파이로 연결 되어있어야 함, 같은 네트워크를 공유)
                // .url("192.168.0.50")
                .url("http://$serverHost:8080")
                .build()

            val callback = object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    // enqueue 를 통해 새로운 스레드를 열어서 작업했으므로 ui thread 가 아님 -> toast 를 사용할 수 없음
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "수신에 실패했습니다", Toast.LENGTH_SHORT).show()
                    }
                    Log.e("Client", e.toString())
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val result = response.body?.string()

                        // message data class type 으로 변환
                        val message = Gson().fromJson(result, Message::class.java)

                        runOnUiThread {
                            // html 형식으로 데이터를 받아옴 <h1>Hello World</h1> (gson 을 사용하지 않았을 경우)
                            // JsonObject 를 지원하지만, google 에서 만든 gson 라이브러리를 사용 (강력한 장점)
                            // json 형태의 데이터를 kotlin 파일의 data class 형태로 변환
                            binding.informationTextView.visibility = View.VISIBLE
                            binding.informationTextView.text = message.message
                            // Log.e("Client", "${response.body?.string()}")

                            binding.serverHostEditText.isVisible = false
                            binding.confirmButton.isVisible = false
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "수신에 실패했습니다", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            client.newCall(request).enqueue(callback)
        }

        //        try {
        //            //excute 는 동기 함수
        //            // val response = client.newCall(request).execute()
        //            // 블락 (ANR의 위험성)-> 콜백의 방식으로 해결
        //            val response = client.newCall(request).enqueue()
        //            // 큐에 넣어놨다가 비동기로 수행되는 형식
        //
        //            Log.e("Client", "${response.body?.string()}")
        //        } catch (e: Exception) {
        //            Log.e("Client", e.toString())
        //        }

        /*
        // socket 을 통한 데이터를 받아오는 방법

        Thread {
            try {
                // 에뮬레이터에서 localhost(127.0.0.1) 을 사용하기 위한 주소 (에뮬레이터는 외부의 ip주소를 사용하지 못하도록 lock 되어 있음)
                // https://developer.android.com/studio/run/emulator-networking?hl=ko
                val socket = Socket("10.0.2.2", 8080)
                val printer = PrintWriter(socket.getOutputStream())
                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

                // 서버와 순서 반대로
                printer.println("GET / HTTP/1.1")
                printer.println("Host: 127.0.0.1:8080")
                printer.println("User-Agent: android")
                printer.println("\r\n")
                printer.flush()

                var input: String? = "-1"
                while (input != null) {
                    input = reader.readLine()
                    Log.e("Client", input)
                }
                reader.close()
                printer.close()
                socket.close()
            } catch (e: Exception) {
                Log.e("Client", e.toString())
            }
        }.start()
         */
    }
}