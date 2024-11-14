$(document).ready(function() {
    var username = window.localStorage.getItem('member');
    var chatRooms = [];
    var currentRoomId = null;
    var stompClient = null;
    var subscription = null;

   function connect(){

        const token = localStorage.getItem("token");
        if(!token){
            console.log("로그인 필요");
            return;
        }

        console.log("하이 : "+JSON.stringify(window.localStorage));

        var socket = new SockJS("http://localhost:8090/ws");
        stompClient = Stomp.over(socket);

        stompClient.connect({}, function(frame){
            console.log("Connected : "+frame);

            stompClient.subscribe("/topic/public/"+currentRoomId, function(messageOutput){
                showMessage(JSON.parse(messageOutput.body));
            });
        });
   }


   function subscribeToRoom(roomId) {
       // 기존 구독을 해제합니다.
       if (subscription) {
           subscription.unsubscribe();
       }

       // 새로운 구독을 설정합니다.
       if (stompClient) {
           subscription = stompClient.subscribe(`/topic/public/${roomId}`, function(messageOutput) {
               showMessage(JSON.parse(messageOutput.body));
           });
       }
   }

   function sendMessage(){
        var messageContent = document.getElementById("messageInput").value.trim();
        var currentTime = new Date();
        if(messageContent && stompClient){
            var chatDto = {
                chatroomId: currentRoomId,
                memberId : window.localStorage.email,
                sendTime: currentTime,
                senderName : window.localStorage.nickname,
                chatContent: messageContent
            };
            stompClient.send("/app/chat.sendMessage",{}, JSON.stringify(chatDto));
            document.getElementById("messageInput").value="";
        }
   }

    function showMessage(message){
        var messageElement = document.createElement("li");
        messageElement.classList.add("list-group-item");

        messageElement.textContent = message.senderName +": "+message.chatContent;
        document.getElementById("messageArea").appendChild(messageElement);
    }

    $('#chatForm').submit(function(event) {
        event.preventDefault();
        sendMessage();
     });



    // '확인' 버튼 클릭 시 채팅방 생성 로직
    $('#createRoomButton').on('click', function() {
        var roomName = $('#roomName').val();
        if(roomName) {
            $.ajax({
                url:"http://localhost:8090/chat/startChat",
                type:"POST",
                contentType : "application/json",
                data: JSON.stringify({
                    name: roomName
                }),
                success: function(response){
                    alert("채팅방 생성 성공!");
                    history.go(0);
                },
                 error: function(jqXHR, textStatus, errorThrown) {
                   alert("채팅방 생성 실패: " + jqXHR.responseText);
                 }
            });
            console.log('채팅방 이름:', roomName);
            // 모달 닫기
            $('#createRoomModal').modal('hide');
        }
    });



    function selectRoomList() {
        $.ajax({
            url: "http://localhost:8090/chat/selectChatRoomList",
            type: "GET",
            data: {
                email: window.localStorage.email  // URL 파라미터로 이메일을 전달
            },
            success: function(response) {
                // response에서 chatRoomList를 추출
                let chatRooms = response.chatRoomList;
                $('#roomList').empty(); // 기존 목록을 비워줍니다.
                chatRooms.forEach(function(room) {
                    // 각 채팅방의 ID를 리스트 항목으로 추가
                    $('#roomList').append('<li data-id="' + room.chatroomId + '">' + room.chatroomId + '</li>');
                });
            },
            error: function(jqXHR, textStatus, errorThrown) {
                console.log("채팅방 조회 실패: " + jqXHR.responseText);
            }
        });
    }


    selectRoomList();

  // 채팅방 클릭 이벤트
  $('#roomList').on('click', 'li', function() {
    currentRoomId = $(this).data('id');
    var roomName = $(this).text();

    // 채팅방 제목 변경
    $('#chatRoomTitle').text(roomName + ' 채팅방');
    $('#chatRoom').show();
    $('#messageArea').empty();

     $.ajax({
        url: `http://localhost:8090/chat/startChat`,
        type: "POST",
        contentType: "application/json",
        data:JSON.stringify(
        {
            "productSeq" : 1,
            "masterEmail" : "anszl789@naver.com",
            "participantEmail" : "test"
        }),
        success: function(response) {
            console.log(response);
            response.chatList.forEach(function(message) {
                var messageElement = document.createElement("li");
                messageElement.classList.add("list-group-item");
                messageElement.textContent = `${message.senderName}: ${message.chatContent}`;
                document.getElementById("messageArea").appendChild(messageElement);
            });
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.log("채팅 메시지 조회 실패: " + jqXHR.responseText);
        }
    });

    if(stompClient){
        subscribeToRoom(currentRoomId);
    }else{
        connect();
    }

  });

    connect();
});
