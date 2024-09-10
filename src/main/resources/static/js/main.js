$(document).ready(function() {
  // 채팅방 목록 예시 데이터
  var chatRooms = [
    { id: 1, name: '채팅방 1' },
    { id: 2, name: '채팅방 2' },
    { id: 3, name: '채팅방 3' }
  ];

  // 채팅방 목록 표시
  chatRooms.forEach(function(room) {
    $('#roomList').append('<li data-id="' + room.id + '">' + room.name + '</li>');
  });

  // 채팅방 클릭 이벤트
  $('#roomList').on('click', 'li', function() {
    var roomId = $(this).data('id');
    var roomName = $(this).text();

    // 채팅방 제목 변경
    $('#chatRoomTitle').text(roomName + ' 채팅방');

    // 서버에서 해당 채팅방 메시지를 불러올 수 있는 AJAX 요청을 추가
    // 예시로 서버 없이 내용을 변경
    $('#chatContent').html('<p>' + roomName + '의 대화 내용입니다.</p>');
  });

  // 채팅 메시지 전송 이벤트
  $('#chatForm').submit(function(event) {
    event.preventDefault();
    var message = $('#messageInput').val();

    if (message.trim() !== '') {
      // 입력된 메시지를 채팅 내용에 추가
      $('#chatContent').append('<p>' + message + '</p>');
      $('#messageInput').val('');

      // 서버로 메시지 전송하는 AJAX 요청을 추가 (생략 가능)
      // $.post('/chat/send', { message: message, roomId: currentRoomId });
    }
  });
});
