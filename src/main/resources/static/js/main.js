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

        var socket = new SockJS("/ws");
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
        if(messageContent && stompClient){
            var chatMessage = {
                sender: username,
                content: messageContent,
                roomId: currentRoomId,
                type: "CHAT"
            };
            stompClient.send("/app/chat.sendMessage",{}, JSON.stringify(chatMessage));
            document.getElementById("messageInput").value="";
        }
   }

    function showMessage(message){
        var messageElement = document.createElement("li");
        messageElement.classList.add("list-group-item");

        messageElement.textContent = message.sender +": "+message.content;
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
                url:"/chat/makeChatRoom",
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



    function selectRoomList()
    {
        $.ajax({
            url:"/chat/selectChatRoom",
            type:"GET",
            contentType : "application/json",
            success: function(response){
                chatRooms = response;
                console.log("채팅방 : "+JSON.stringify(chatRooms));

                $('#roomList').empty(); // 기존 목록을 비워줍니다.
                chatRooms.forEach(function(room) {
                    $('#roomList').append('<li data-id="' + room.id + '">' + room.name + '</li>');
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
        url: `/chat/selectChatContent/${currentRoomId}`,
        type: "GET",
        success: function(response) {
            response.forEach(function(message) {
                var messageElement = document.createElement("li");
                messageElement.classList.add("list-group-item");
                messageElement.textContent = `${message.sender}: ${message.message}`;
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
