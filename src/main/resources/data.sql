INSERT INTO MEMBER (USER_ID, EMAIL, NAME, PASSWORD, ROLE, ENABLED) VALUES
('user01', 'user01@test.com', '홍길동', '1111', 'ROLE_USER', 1),
('user02', 'user02@test.com', '김영희', '2222', 'ROLE_USER', 1),
('user03', 'user03@test.com', '이철수', '3333', 'ROLE_USER', 1),
('user04', 'user04@test.com', '박민수', '4444', 'ROLE_USER', 1),
('admin', 'admin@test.com', '관리자', 'admin', 'ROLE_ADMIN', 1);

INSERT INTO AI (name, prompt, is_use) VALUES
('다정한 오소리', '', 1),
('친절한 두루미', '', 1),
('용감한 사자', '', 1),
('용맹한 거북이', '', 1);

INSERT INTO CUSTOM (user_id, ai_id, is_formal, is_long) VALUES
('user01', 1, 0, 0),
('user02', 2, 0, 1),
('user03', 3, 1, 0),
('user04', 4, 1, 1);

INSERT INTO KEYWORD (NAME, IS_USE, TYPE) VALUES 
('감탄스러움', 1, 'EMOTION_GOOD'),
('신남', 1, 'EMOTION_GOOD'),
('놀람', 1, 'EMOTION_GOOD'),
('열정적임', 1, 'EMOTION_GOOD'),
('행복함', 1, 'EMOTION_GOOD'),
('기쁨', 1, 'EMOTION_GOOD'),
('대담함', 1, 'EMOTION_GOOD'),
('자랑스러움', 1, 'EMOTION_GOOD'),
('자신 있음', 1, 'EMOTION_GOOD'),
('희망적임', 1, 'EMOTION_GOOD'),
('재미있음', 1, 'EMOTION_GOOD'),
('만족스러움', 1, 'EMOTION_GOOD'),
('안도감', 1, 'EMOTION_GOOD'),
('감사함', 1, 'EMOTION_GOOD'),
('충족감을 느낌', 1, 'EMOTION_GOOD'),
('차분함', 1, 'EMOTION_GOOD'),
('평온함', 1, 'EMOTION_GOOD'),
('화남', 1, 'EMOTION_BAD'),
('불안함', 1, 'EMOTION_BAD'),
('무서움', 1, 'EMOTION_BAD'),
('심한 압박감을 느낌', 1, 'EMOTION_BAD'),
('부끄러움', 1, 'EMOTION_BAD'),
('역겨움', 1, 'EMOTION_BAD'),
('당황스러움', 1, 'EMOTION_BAD'),
('좌절스러움', 1, 'EMOTION_BAD'),
('짜증 남', 1, 'EMOTION_BAD'),
('부러움', 1, 'EMOTION_BAD'),
('스트레스', 1, 'EMOTION_BAD'),
('걱정스러움', 1, 'EMOTION_BAD'),
('죄책감', 1, 'EMOTION_BAD'),
('놀람', 1, 'EMOTION_BAD'),
('절망스러움', 1, 'EMOTION_BAD'),
('거슬림', 1, 'EMOTION_BAD'),
('외로움', 1, 'EMOTION_BAD'),
('낙심함', 1, 'EMOTION_BAD'),
('실망함', 1, 'EMOTION_BAD'),
('지침', 1, 'EMOTION_BAD'),
('슬픔', 1, 'EMOTION_BAD'),
('친구', 1, 'PERSON'),
('동료', 1, 'PERSON'),
('가족', 1, 'PERSON'),
('커뮤니티', 1, 'PERSON'),
('연애', 1, 'PERSON'),
('건강', 1, 'SITUATION'),
('취미', 1, 'SITUATION'),
('종교', 1, 'SITUATION'),
('직장', 1, 'SITUATION'),
('여행', 1, 'SITUATION'),
('날씨', 1, 'SITUATION'),
('돈', 1, 'SITUATION'),
('국내외 이슈', 1, 'SITUATION');

INSERT INTO DIARY (USER_ID, EMOTION, CONTENT, CREATED_AT, MODIFIED_AT, IS_USE) VALUES
('user01', 'VERY_GOOD', '오늘은 정말 완벽한 하루였다. 모든 게 잘 풀렸다.', '2025-05-08 10:15:00', '2025-05-08 10:15:00', 1),
('user02', 'VERY_BAD', '기분이 바닥이다. 모든 게 틀어진 날이었다.', '2025-05-07 14:30:00', '2025-05-07 14:30:00', 1),
('user03', 'BAD', '지하철에서 사람들과 부딪히고 너무 지쳤다.', '2025-05-06 08:50:00', '2025-05-06 08:50:00', 1),
('user04', 'GOOD', '가족과 좋은 시간을 보내서 기분이 좋다.', '2025-05-08 20:10:00', '2025-05-08 20:10:00', 1),
('user04', 'COMMON', '평범한 하루였다. 특별한 일은 없었다.', '2025-05-05 22:45:00', '2025-05-05 22:45:00', 1),
('user01', 'GOOD', '업무에서 좋은 피드백을 받아 기분이 좋다.', '2025-05-04 13:10:00', '2025-05-04 13:10:00', 1),
('user02', 'BAD', '택배 문제로 하루가 망친 느낌이다.', '2025-05-03 11:05:00', '2025-05-03 11:05:00', 1),
('user03', 'VERY_GOOD', '회의 발표가 성공적으로 끝나서 기분 최고다!', '2025-05-02 17:40:00', '2025-05-02 17:40:00', 1),
('user04', 'COMMON', '야근했지만 그냥 그런 하루였다.', '2025-05-01 23:00:00', '2025-05-01 23:00:00', 1),
('user01', 'GOOD', '새 프로젝트 시작! 기대된다.', '2025-04-30 09:20:00', '2025-04-30 09:20:00', 1);

INSERT INTO DIARY_KEYWORD (DIARY_ID, KEYWORD_ID) VALUES
(1, 1), (1, 15), (1, 33),
(2, 22), (2, 25), (2, 43),
(3, 10), (3, 20), (3, 37),
(4, 5), (4, 14), (4, 32),
(5, 30), (5, 34), (5, 41),
(6, 2), (6, 13), (6, 35),
(7, 9), (7, 21), (7, 38),
(8, 3), (8, 6), (8, 31),
(9, 11), (9, 26), (9, 42),
(10, 7), (10, 12), (10, 36);


