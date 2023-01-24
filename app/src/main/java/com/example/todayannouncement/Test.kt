package com.example.todayannouncement

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket

// server 역할
fun main() {

    // socket(로우 레벨) 을 사용하면 복잡하기 때문에(예외 처리등의 고려해야할 것이 많기 때문에),
    // 내부적으로 OkHttp 를 사용하면 소켓 프로그래밍을 직접해주지 않아도 되서 편함 (결국 내부적으로는 socket 을 사용)
    // 알아야할 것은 네트워크 프로그래밍은 socket 을 통해 이뤄지고 이를 쉽게 사용할 수 있게 해주는 것이 OkHttp

    // UiThread 가 아닌 새로운 스레드에서 동작하도록
    Thread {
        // 소켓 서버 구현
        // 브라우저에서 IP 주소 + :8080 입력시 전송된 데이터를 확인할 수 있음
        // 단, 같은 와이파이로 연결된 상태여야 함(내부 네트워크에서만 동작하는 서버)
        val port = 8080
        val server = ServerSocket(port)
        while(true) {
            // blocking 동기로 넘어가지 않음

            // socket 이 튀어 나왔다는 것은 클라이언트에서 서버에게 요청을 했다는 의미
            // accept 이 돠면 클라이언트에도 socket 이 생기고 서버에도 socket 이 생겼다는 의미
            val socket = server.accept()
            // 이제 데이터를 주고 받을 수 있음

            // stream 을 통한 데이터 통신 (stream 은 단뱡향(일방통행), inputStream, outputStream)
            // 데이터가 흐르는 길(파이프)
            // socket.getInputStream() // 클라이언트로부터 들어오는 스토리 == 클라이언트의 socket.outputStream
            // socket.getOutputStream() // 클라이언트에게 데이터를 주는 스트림  == 클라이언트의 socket.inputStream

            // 클라이언트에서 데이터를 언제 넣어줄지 모름
            // reader 를 통해 데이터를 읽을 수 있음

            // inputStream 을 reader 로 변환하고 buffer 를 씌워줌, 데이터를 읽음
            // 입구 (서버입장에서 클라이언트가 보내는 요청)
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

            // 출구 (나가는 요청)
            val printer = PrintWriter(socket.getOutputStream())

            var input: String? = "-1"
            // 데이터를 line 단위로 읽음

            // eof 처리
            while(input != null && input != "") {
                input = reader.readLine()
            }

            // Log.e("SERVER", "READ DATA $input")
            println("READ DATA $input")

            // HTTP 통신 규격을 사용
            // HTTP 1.1 버전을 사용,
            // 데이터를 정상적으로 수신했고, 반환을 해주는 정상 응답의 의미
            // 헤더 부분 (서버와 클라이언트간의 통신할때 데이터의 대한 정보를 제공)
            printer.println("HTTP/1.1 200 OK")
            printer.println("Content-Type: text/html\r\n")

            // 바디 부분
            // html type
            // printer.println("<h1>Hello World</h1>")

            // json
            printer.println("{\"message\": \"Hello World\"}")

            // 데이터 전송이 끝났음을 의미
            printer.println("\r\n")
            // 배출
            printer.flush()
            // 연결 종료
            printer.close()
            reader.close()
            socket.close()
        }
    }.start()
}