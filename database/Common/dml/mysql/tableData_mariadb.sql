
INSERT INTO `PRJ_PROJECT` (`PRNO`, `PRSTARTDATE`, `PRENDDATE`, `PRTITLE`, `REGDATE`, `USERNO`, `PRSTATUS`, `DELETEFLAG`) VALUES
	(1, '2016-11-27', '2017-03-03', '시연 전자 과제 관리 시스템 구축', '2016-11-27 11:52:31', 1, '0', 'N'),
	(5, '2016-12-01', '2017-01-31', '시연 화장품 제안 시스템 구축', '2016-11-30 08:44:23', 1, '0', 'N'),
	(6, '2016-10-03', '2017-03-03', '스터디 관리 시스템 (내부 프로젝트)', '2016-11-30 09:02:21', 1, '0', 'N'),
	(7, '2016-10-03', '2017-03-06', '주식 회사 시연 그룹웨어 고도화', '2016-11-30 11:43:12', 1, '0', 'N'),
	(8, '2016-12-01', '2017-02-10', '웹 프로그래밍 과정', '2016-12-01 08:46:20', 1, '0', 'N'),
	(9, '2016-12-01', '2016-12-12', '스프링 웹 개발', '2016-12-01 08:49:18', 1, '0', 'N'),
	(10, '2016-12-01', '2016-12-12', '안드로이드 프로그래밍', '2016-12-01 08:56:47', 1, '0', 'N');


INSERT INTO `PRJ_TASK` (`PRNO`, `TSNO`, `TSPARENT`, `TSSORT`, `TSTITLE`, `TSSTARTDATE`, `TSENDDATE`, `TSENDREAL`, `TSRATE`, `DELETEFLAG`, `OLDNO`) VALUES
	(1, 1, NULL, NULL, '분석/설계', '', '', '', 0, 'N', NULL),
	(1, 2, 1, NULL, '시스템 분석', '2016-10-01', '2016-11-23', '', 60, 'N', NULL),
	(1, 3, 1, NULL, '업무 분석', '2016-12-01', '2016-12-16', '', 0, 'N', NULL),
	(1, 4, 1, NULL, '화면구성', '2016-12-19', '2016-12-30', '', 0, 'N', NULL),
	(1, 5, 1, NULL, 'DB설계', '2016-12-19', '2016-12-30', '', 0, 'N', NULL),
	(1, 6, NULL, NULL, '구현', '', '', '', 0, 'N', NULL),
	(1, 7, 6, NULL, '사용자 화면 개발', '2017-01-02', '2017-02-10', '', 0, 'N', NULL),
	(1, 8, 6, NULL, '관리자 화면 개발', '2017-01-16', '2017-02-28', '', 0, 'N', NULL),
	(1, 9, NULL, NULL, '테스트', '', '', '', 0, 'N', NULL),
	(1, 10, 9, NULL, '단위 테스트', '2017-02-20', '2017-02-28', '', 0, 'N', NULL),
	(1, 11, 9, NULL, '통합 테스트', '2017-02-27', '2017-03-03', '', 0, 'N', NULL),
	(1, 12, NULL, NULL, '이행', '', '', '', 0, 'N', NULL),
	(1, 13, 12, NULL, '운영서버 설치', '2017-03-01', '2017-03-03', '', 0, 'N', NULL),
	(1, 14, 12, NULL, '오픈', '2017-03-06', '2017-03-06', '', 0, 'N', NULL),
	(5, 15, NULL, NULL, '분석/설계', '', '', NULL, 0, 'N', 1),
	(5, 16, 15, NULL, '시스템 /업무 분석', '2016-12-01', '2016-12-09', '', 0, 'N', 2),
	(5, 18, 15, NULL, '화면 및 DB 설계', '2016-12-12', '2016-12-16', '', 0, 'N', 4),
	(5, 20, NULL, NULL, '구현', '', '', NULL, 0, 'N', 6),
	(5, 21, 20, NULL, '사용자 화면 개발', '2016-12-19', '2017-01-31', '', 0, 'N', 7),
	(5, 22, 20, NULL, '관리자 화면 개발', '2017-01-16', '2017-01-27', '', 0, 'N', 8),
	(5, 23, NULL, NULL, '테스트', '', '', NULL, 0, 'N', 9),
	(5, 24, 23, NULL, '단위 테스트', '2017-01-30', '2017-01-31', '', 0, 'N', 10),
	(5, 25, 23, NULL, '통합 테스트', '2017-02-01', '2017-02-03', '', 0, 'N', 11),
	(5, 26, NULL, NULL, '이행', '', '', NULL, 0, 'N', 12),
	(5, 27, 26, NULL, '운영서버 설치', '2017-02-02', '2017-02-03', '', 0, 'N', 13),
	(5, 28, 26, NULL, '오픈', '2017-02-06', '2017-02-06', '', 0, 'N', 14),
	(7, 34, NULL, NULL, '분석/설계', '', '', NULL, 0, 'N', 1),
	(7, 35, 34, NULL, '시스템 분석', '2016-10-03', '2016-11-23', '', 0, 'N', 2),
	(7, 36, 34, NULL, '업무 분석', '2016-12-01', '2016-12-16', NULL, 0, 'N', 3),
	(7, 37, 34, NULL, '화면구성', '2016-12-19', '2016-12-30', NULL, 0, 'N', 4),
	(7, 38, 34, NULL, 'DB설계', '2016-12-19', '2016-12-30', NULL, 0, 'N', 5),
	(7, 39, NULL, NULL, '구현', '', '', NULL, 0, 'N', 6),
	(7, 40, 39, NULL, '사용자 화면 개발', '2017-01-02', '2017-02-10', NULL, 0, 'N', 7),
	(7, 41, 39, NULL, '관리자 화면 개발', '2017-01-16', '2017-02-28', NULL, 0, 'N', 8),
	(7, 42, NULL, NULL, '테스트', '', '', NULL, 0, 'N', 9),
	(7, 43, 42, NULL, '단위 테스트', '2017-02-20', '2017-02-28', NULL, 0, 'N', 10),
	(7, 44, 42, NULL, '통합 테스트', '2017-02-27', '2017-03-03', NULL, 0, 'N', 11),
	(7, 45, NULL, NULL, '이행', '', '', NULL, 0, 'N', 12),
	(7, 46, 45, NULL, '운영서버 설치', '2017-03-01', '2017-03-03', NULL, 0, 'N', 13),
	(7, 47, 45, NULL, '오픈', '2017-03-06', '2017-03-06', NULL, 0, 'N', 14),
	(8, 49, NULL, NULL, 'Java', '', '', '', 0, 'N', NULL),
	(8, 50, 49, NULL, 'Java 기본 문법', '2016-12-01', '2016-12-09', '', 0, 'N', NULL),
	(8, 51, 49, NULL, 'I/O, CS 프로그래밍', '2016-12-12', '2016-12-16', '', 0, 'N', NULL),
	(8, 52, 49, NULL, 'Design Pattern', '2016-12-19', '2016-12-23', '', 0, 'N', NULL),
	(8, 53, NULL, NULL, 'Database', '', '', '', 0, 'N', NULL),
	(8, 54, NULL, NULL, 'WEB Programming', '', '', '', 0, 'N', NULL),
	(8, 55, 53, NULL, 'SQL', '2016-12-26', '2017-01-06', '', 0, 'N', NULL),
	(8, 56, 53, NULL, 'DB Modeling', '2017-01-09', '2017-01-13', '', 0, 'N', NULL),
	(8, 57, 53, NULL, 'JDBC', '2017-01-16', '2017-01-16', '', 0, 'N', NULL),
	(8, 58, 54, NULL, 'Servelt / JSP', '2017-01-17', '2017-01-20', '', 0, 'N', NULL),
	(8, 59, 54, NULL, 'MVC', '2017-01-23', '2017-01-23', '', 0, 'N', NULL),
	(8, 60, 54, NULL, 'Spring &amp; Mabatis', '2017-01-24', '2017-02-10', '', 0, 'N', NULL),
	(9, 61, NULL, NULL, '기본', '', '', '', 0, 'N', NULL),
	(9, 62, 61, NULL, '스프링의 개요와 DI', '', '', '', 0, 'N', NULL),
	(9, 64, 62, NULL, 'Framework 개요 및 특징', '2016-12-01', '2016-12-01', '', 0, 'N', NULL),
	(9, 65, 62, NULL, 'Spring Framework 와 디자인 패턴', '2016-12-02', '2016-12-02', '', 0, 'N', NULL),
	(9, 66, 62, NULL, 'DI (Dependency Injection) &amp; AOP(Aspect Oriented Programming) ', '2016-12-05', '2016-12-05', '', 0, 'N', NULL),
	(9, 67, NULL, NULL, '심화', '', '', '', 0, 'N', NULL),
	(9, 68, 67, NULL, '스프링 기반의 웹개발과 데이터베이스', '', '', '', 0, 'N', NULL),
	(9, 69, 68, NULL, '스프링 MVC', '2016-12-06', '2016-12-06', '', 0, 'N', NULL),
	(9, 70, 68, NULL, '어노테이션 기반 설정', '2016-12-07', '2016-12-07', '', 0, 'N', NULL),
	(9, 71, 68, NULL, 'MyBatis', '2016-12-08', '2016-12-08', '', 0, 'N', NULL),
	(9, 72, NULL, NULL, '활용', '', '', '', 0, 'N', NULL),
	(9, 73, 72, NULL, '트렌젝션 관리와 보안', '', '', '', 0, 'N', NULL),
	(9, 74, 73, NULL, '스프링 트렌젝션', '2016-12-09', '2016-12-09', '', 0, 'N', NULL),
	(9, 75, 73, NULL, '스프링 보안', '2016-12-12', '2016-12-12', '', 0, 'N', NULL),
	(10, 76, NULL, NULL, '안드로이드 개요', '', '', '', 0, 'N', NULL),
	(10, 77, 76, NULL, '1. Android 운영체제', '2016-12-01', '2016-12-01', '', 30, 'N', NULL),
	(10, 78, 76, NULL, '2. Android 개발환경 설정', '2016-12-01', '2016-12-01', '', 0, 'N', NULL),
	(10, 79, 76, NULL, '3. Android 애플리케이션 구성요소', '2016-12-05', '2016-12-05', '', 0, 'N', NULL),
	(10, 80, NULL, NULL, '안드로이드 입문', '', '', '', 0, 'N', NULL),
	(10, 81, 80, NULL, '1.Hello, Android ', '2016-12-06', '2016-12-06', '', 0, 'N', NULL),
	(10, 82, 80, NULL, '2.Android 애플리케이션 구성요소', '2016-12-07', '2016-12-07', '', 0, 'N', NULL),
	(10, 83, NULL, NULL, '안드로이드 프로그래밍', '', '', '', 0, 'N', NULL),
	(10, 84, 83, NULL, '1. 안드로이드 기본 UI', '2016-12-08', '2016-12-08', '', 0, 'N', NULL),
	(10, 85, 83, NULL, '2. 안드로이드 고급 UI (액션바/프래그먼트)', '2016-12-09', '2016-12-09', '', 0, 'N', NULL),
	(10, 86, 83, NULL, '3.안드로이드 커스텀 UI (drawable/Theme)', '2016-12-12', '2016-12-12', '', 0, 'N', NULL),
	(6, 87, NULL, NULL, '분석/설계', '', '', NULL, 0, 'N', 15),
	(6, 88, 87, NULL, '시스템 /업무 분석', '2016-10-03', '2016-10-31', '2016-10-31', 100, 'N', 16),
	(6, 89, 87, NULL, '화면 및 DB 설계', '2016-11-01', '2016-11-30', '2016-11-30', 100, 'N', 18),
	(6, 90, NULL, NULL, '구현', '', '', NULL, 0, 'N', 20),
	(6, 91, 90, NULL, '사용자 화면 개발', '2016-12-01', '2017-01-31', '', 0, 'N', 21),
	(6, 92, 90, NULL, '관리자 화면 개발', '2016-12-15', '2017-01-31', '', 0, 'N', 22),
	(6, 93, NULL, NULL, '테스트', '', '', NULL, 0, 'N', 23),
	(6, 94, 93, NULL, '단위 테스트', '2017-01-30', '2017-01-31', '', 0, 'N', 24),
	(6, 95, 93, NULL, '통합 테스트', '2017-02-01', '2017-02-07', '', 0, 'N', 25),
	(6, 96, NULL, NULL, '이행', '', '', NULL, 0, 'N', 26),
	(6, 97, 96, NULL, '완료 보고', '2017-02-08', '2017-02-08', '', 0, 'N', 27);

INSERT INTO `PRJ_TASKUSER` (`TSNO`, `USERNO`) VALUES
	(2, 6),
	(3, 11),
	(4, 11),
	(5, 6),
	(7, 4),
	(7, 9),
	(7, 14),
	(8, 24),
	(10, 4),
	(10, 9),
	(11, 14),
	(11, 24),
	(13, 4),
	(16, 6),
	(18, 26),
	(21, 29),
	(21, 34),
	(22, 54),
	(24, 54),
	(25, 15),
	(27, 34),
	(88, 20),
	(88, 25),
	(89, 20),
	(89, 25),
	(91, 20),
	(91, 25),
	(92, 20),
	(92, 25),
	(94, 20),
	(94, 25),
	(95, 20),
	(95, 25),
	(97, 20),
	(97, 25);


INSERT INTO `COM_CODE` (`CLASSNO`, `CODECD`, `CODENM`) VALUES
	(1, 'A', '관리자'),
	(1, 'U', '사용자');

INSERT INTO `COM_CODE` (`CLASSNO`, `CODECD`, `CODENM`) VALUES
	(2, '0', '임시저장'),
	(2, '1', '대기중'),
	(2, '2', '심사중'),
	(2, '3', '반려'),
	(2, '4', '결재 완료');

INSERT INTO `COM_CODE` (`CLASSNO`, `CODECD`, `CODENM`) VALUES
	(3, '01', '선임'),
	(3, '02', '주임'),
	(3, '03', '대리'),
	(3, '04', '과장'),
	(3, '05', '차장'),
	(3, '06', '부장'),
	(3, '07', '이사'),
	(3, '08', '상무'),
	(3, '09', '전무'),
	(3, '10', '부사장'),
	(3, '11', '사장'),
	(3, '12', '부회장'),
	(3, '13', '회장');


INSERT INTO `COM_CODE` (`CLASSNO`, `CODECD`, `CODENM`) VALUES
	(4, '1', '업무'),
	(4, '2', '회의'),
	(4, '3', '외근'),
	(4, '4', '출장'),
	(4, '5', '교육'),
	(4, '6', '휴가'),
	(4, '7', '기타');

INSERT INTO `COM_CODE` (`CLASSNO`, `CODECD`, `CODENM`) VALUES
	(5, '1', '반복없음'),
	(5, '2', '주단위'),
	(5, '3', '월단위');


INSERT INTO `COM_CODE` (`CLASSNO`, `CODECD`, `CODENM`) VALUES
	(6, 'Y', '공개'),
	(6, 'N', '비공개');

INSERT INTO COM_CODE_V2 VALUES ('0','','공통코드','','N');
INSERT INTO COM_CODE_V2 VALUES ('1','0','사용자롤','','N');
INSERT INTO COM_CODE_V2 VALUES ('2','0','문서결제상태','','N');
INSERT INTO COM_CODE_V2 VALUES ('3','0','직책구분','','N');
INSERT INTO COM_CODE_V2 VALUES ('4','0','근무상태','','N');
INSERT INTO COM_CODE_V2 VALUES ('5','0','반복단위','','N');
INSERT INTO COM_CODE_V2 VALUES ('6','0','공개여부','','N');

INSERT INTO COM_CODE_V2 VALUES ('A',  1, '관리자',   '사용자롤', 'N');
INSERT INTO COM_CODE_V2 VALUES ('U',  1, '사용자',   '사용자롤', 'N');
INSERT INTO COM_CODE_V2 VALUES ('20', 2, '임시저장',  '문서결제상태', 'N'); -- 0-20
INSERT INTO COM_CODE_V2 VALUES ('21', 2, '대기중',   '문서결제상태', 'N');
INSERT INTO COM_CODE_V2 VALUES ('22', 2, '심사중',   '문서결제상태', 'N');
INSERT INTO COM_CODE_V2 VALUES ('23', 2, '반려',    '문서결제상태', 'N');
INSERT INTO COM_CODE_V2 VALUES ('24', 2, '결재 완료','문서결제상태', 'N');
INSERT INTO COM_CODE_V2 VALUES ('01', 3, '선임',    '직책구분', 'N');
INSERT INTO COM_CODE_V2 VALUES ('02', 3, '주임',    '직책구분', 'N');
INSERT INTO COM_CODE_V2 VALUES ('03', 3, '대리',    '직책구분', 'N');
INSERT INTO COM_CODE_V2 VALUES ('04', 3, '과장',    '직책구분', 'N');
INSERT INTO COM_CODE_V2 VALUES ('05', 3, '차장',    '직책구분', 'N');
INSERT INTO COM_CODE_V2 VALUES ('06', 3, '부장',    '직책구분', 'N');
INSERT INTO COM_CODE_V2 VALUES ('07', 3, '이사',    '직책구분', 'N');
INSERT INTO COM_CODE_V2 VALUES ('08', 3, '상무',    '직책구분', 'N');
INSERT INTO COM_CODE_V2 VALUES ('09', 3, '전무',    '직책구분', 'N');
INSERT INTO COM_CODE_V2 VALUES ('10', 3, '부사장',   '직책구분', 'N');
INSERT INTO COM_CODE_V2 VALUES ('11', 3, '사장',    '직책구분', 'N');
INSERT INTO COM_CODE_V2 VALUES ('12', 3, '부회장',   '직책구분', 'N');
INSERT INTO COM_CODE_V2 VALUES ('13', 3, '회장',    '직책구분', 'N');
INSERT INTO COM_CODE_V2 VALUES ('41', 4, '업무',    '근무상태', 'N'); -- 1 -41
INSERT INTO COM_CODE_V2 VALUES ('42', 4, '회의',    '근무상태', 'N');
INSERT INTO COM_CODE_V2 VALUES ('43', 4, '외근',    '근무상태', 'N');
INSERT INTO COM_CODE_V2 VALUES ('44', 4, '출장',    '근무상태', 'N');
INSERT INTO COM_CODE_V2 VALUES ('45', 4, '교육',    '근무상태', 'N');
INSERT INTO COM_CODE_V2 VALUES ('46', 4, '휴가',    '근무상태', 'N');
INSERT INTO COM_CODE_V2 VALUES ('47', 4, '기타',    '근무상태', 'N');
INSERT INTO COM_CODE_V2 VALUES ('51', 5, '반복없음', '반복단위', 'N'); -- 1-51
INSERT INTO COM_CODE_V2 VALUES ('52', 5, '주단위',  '반복단위', 'N');
INSERT INTO COM_CODE_V2 VALUES ('53', 5, '월단위',  '반복단위', 'N');
INSERT INTO COM_CODE_V2 VALUES ('Y',  6, '공개',   '공개여부', 'N');
INSERT INTO COM_CODE_V2 VALUES ('N',  6, '비공개',  '공개여부', 'N');


INSERT INTO `SCH_HOLIDAY` (`SHNO`, `SHTITLE`, `SHMONTH`, `SHDATE`, `SHCOLOR`, `DELETEFLAG`) VALUES
	(1, '신정', 1, 1, 'RED', 'N'),
	(2, '삼일절', 3, 1, 'RED', 'N'),
	(3, '식목일', 4, 5, 'GREEN', 'N'),
	(4, '근로자의날', 5, 1, 'GREEN', 'N'),
	(5, '어린이날', 5, 5, 'RED', 'N'),
	(6, '어버이날', 5, 8, 'GREEN', 'N'),
	(7, '스승의날', 5, 15, 'GREEN', 'N'),
	(8, '현충일', 6, 6, 'RED', 'N'),
	(9, '제헌절', 7, 17, 'GREEN', 'N'),
	(10, '광복절', 8, 15, 'RED', 'N'),
	(11, '개천절', 10, 3, 'RED', 'N'),
	(12, '한글날', 10, 9, 'RED', 'N'),
	(13, '성탄절', 12, 25, 'RED', 'N');    

-- select * from com_code;

INSERT INTO `COM_DEPT` (`DEPTNO`, `DEPTNM`, `PARENTNO`, `DELETEFLAG`) VALUES
	(1, '주식회사', NULL, 'N'),
	(2, '기획실', 1, 'N'),
	(3, '경리팀', 1, 'N'),
	(4, '개발본부', 1, 'N'),
	(5, '개발 1팀', 4, 'N'),
	(6, '개발 2팀', 4, 'N'),
	(7, '디자인팀', 1, 'N');

INSERT INTO `TBL_BOARDGROUP` (`BGNO`, `BGNAME`, `BGPARENT`, `DELETEFLAG`, `BGUSED`, `BGREPLY`, `BGREADONLY`, `BGNOTICE`, `regdate`) VALUES
	(1, '게시판', NULL, 'N', 'N', 'N', 'N', 'N',NOW()),
	(2, '공지사항', 1, 'N', 'Y', 'N', 'Y', 'Y', NOW()),
	(3, '일반게시판', 1, 'N', 'Y', 'Y', 'N', 'N', NOW());

INSERT INTO `COM_USER` (`USERNO`, `USERID`, `USERNM`, `USERPW`, `USERROLE`, `PHOTO`, `DEPTNO`, `DELETEFLAG`) VALUES
	(1, 'admin', '관리자', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'A', NULL, 1, 'N'),
	(2, 'user1', '이사부', '0a041b9462caa4a31bac3567e0b6e6fd9100787db2ab433d96f6d178cabfce90', 'U', NULL, 3, 'N'),
	(3, 'user2', '계백', '6025d18fe48abd45168528f18a82e265dd98d421a7084aa09f61b341703901a3', 'U', NULL, 4, 'N'),
	(4, 'user3', '관창', '5860faf02b6bc6222ba5aca523560f0e364ccd8b67bee486fe8bf7c01d492ccb', 'U', NULL, 5, 'N'),
	(5, 'user4', '김유신', '5269ef980de47819ba3d14340f4665262c41e933dc92c1a27dd5d01b047ac80e', 'U', NULL, 6, 'N'),
	(6, 'user5', '원효', '5a39bead318f306939acb1d016647be2e38c6501c58367fdb3e9f52542aa2442', 'U', NULL, 2, 'N'),
	(7, 'user6', '혜초', 'ecb48a1cc94f951252ec462fe9ecc55c3ef123fadfe935661396c26a45a5809d', 'U', NULL, 3, 'N'),
	(8, 'user7', '장보고', '3268151e52d97b4cacf97f5b46a5c76c8416e928e137e3b3dc447696a29afbaa', 'U', NULL, 4, 'N'),
	(9, 'user8', '대조영', 'f60afa4989a7db13314a2ab9881372634b5402c30ba7257448b13fa388de1b78', 'U', NULL, 5, 'N'),
	(10, 'user9', '강감찬', '0fb8d3c5dfaf81a387bf0ba439ab40e6343d2155fb4ddf6978a52d9b9ea8d0f8', 'U', NULL, 6, 'N'),
	(11, 'user10', '서희', '5bbf1a9e0de062225a1bb7df8d8b3719591527b74950810f16b1a6bc6d7bd29b', 'U', NULL, 2, 'N'),
	(12, 'user11', '정중부', '81115e31e22a5801b197750ec12d7a51ad693aa017ecc8bca033cbd500a928b6', 'U', NULL, 3, 'N'),
	(13, 'user12', '최무선', 'bd35283fe8fcfd77d7c05a8bf2adb85c773281927e12c9829c72a9462092f7c4', 'U', NULL, 4, 'N'),
	(14, 'user13', '임춘', '1834e148b518a43a37e04a4e4fbcee1eb845de6ee5a3f04fe9fb749f9695be42', 'U', NULL, 5, 'N'),
	(15, 'user14', '오세재', 'daf7996f88742675acb3d0f85a8069d02fdf1c4dc2026de7f01a0ba7e65922fb', 'U', NULL, 6, 'N'),
	(16, 'user15', '이인로', '2b8b66f64b605318593982b059a08dae101c0bdf5d8cb882a0891241a704f46c', 'U', NULL, 2, 'N'),
	(17, 'user16', '조통', '4de4153595c0977d2389d0880547bd3aa60871e906ce52a26648d8ca0a157e5c', 'U', NULL, 3, 'N'),
	(18, 'user17', '황보항', '2a60ff641c890283b1d070f827cf9c0cce004769c2a2dc7136bc6d290477275c', 'U', NULL, 4, 'N'),
	(19, 'user18', '이담지', 'ebc835d1b43e63d1ba35af810da3a23e4f8a04cf680f1718c2fefb1ee77fcecf', 'U', NULL, 5, 'N'),
	(20, 'user19', '함순', '0b6ecb3aa9b23589fb9e314b46c832d977e597228c1e358fcc564bd8ba733195', 'U', NULL, 6, 'N'),
	(21, 'user20', '김부식', '7febe54e79096749ac43dc6c2e3e5d4dc768993d1f3102889257c9cab7934ec7', 'U', NULL, 2, 'N'),
	(22, 'user21', '지눌', '8fab3a60577befd765cde83f2737cd1a9f25a72356c94052c2194e816829b331', 'U', NULL, 3, 'N'),
	(23, 'user22', '의천', 'b999205cdacd2c4516598d99b420d29786443e9908556a65f583a6fd4765ee4a', 'U', NULL, 4, 'N'),
	(24, 'user23', '이종무', '2a0fff6e36fbc6a21f7b065f24a3ffb40de209ea8cfc15d76cf786f74dd6f115', 'U', NULL, 5, 'N'),
	(25, 'user24', '정몽주', '65303a0ec5984ab62d102031a5c9be89ca21812816af34a8e13c8104be240ab2', 'U', NULL, 6, 'N'),
	(26, 'user25', '문익점', 'dab53605c85e2eff2f2192e08b772cbc06af317d217d09e161215901339300e0', 'U', NULL, 2, 'N'),
	(27, 'user26', '최충', '6356b9f65e6e775b3feb7996bd3e1ba3f20c83366e4aec56f4f3979a0cfd75f5', 'U', NULL, 3, 'N'),
	(28, 'user27', '일연', '590b1a8287729e75223a77d4f64fb13dd1bc1819f4e185fe37f81b97e436edbc', 'U', NULL, 4, 'N'),
	(29, 'user28', '최영', 'fdc86230e908634cb03015f1a016ffa16f1cd9eac9003b51fcd1c13abb799c06', 'U', NULL, 5, 'N'),
	(30, 'user29', '황희', 'df3c4035da126a48f484133358bedf2306c403af75347c8420a96e461d1709ad', 'U', NULL, 6, 'N'),
	(31, 'user30', '맹사성', '601fbd496628a2a8ad6186af757273b116d9f2a7d666145827c68e0bef1ccd13', 'U', NULL, 2, 'N'),
	(32, 'user31', '장영실', '4ccb2f3b57e6de1caea8475b13fb5c968a766f78c3004034f07704ae5fd3e5dd', 'U', NULL, 3, 'N'),
	(33, 'user32', '신숙주', '54a2979a455bec056285887a8137d1bfe6cbfd229e558ea4d28cd67cf81ed38e', 'U', NULL, 4, 'N'),
	(34, 'user33', '한명회', 'f81e0105e6dcdf3f2e2041d119d1c87b9e66a73f8d0007d2dc505595b20d9809', 'U', NULL, 5, 'N'),
	(35, 'user34', '이율곡', '4d4ec9499ab2bce1c5c8a565004ac27d90a593a360e79bc237b917b43d34c44d', 'U', NULL, 6, 'N'),
	(36, 'user35', '이퇴계', '3dd5329b7396fda1de5878a24ab7ca98cd47351bcbfb37138565a30771da1590', 'U', NULL, 2, 'N'),
	(37, 'user36', '신사임당', '06f01542cd526fcfd7624cf9fca3e0e3899e25a9e84da65d3b8cb5ac200dda1f', 'U', NULL, 3, 'N'),
	(38, 'user37', '곽재우', '9c10f9996d42387555f8008224c0b8ffcedd501108b9f6ac1159ecd87bb9f6c3', 'U', NULL, 4, 'N'),
	(39, 'user38', '조헌', '85a20bcfad4e8153d5fd624c87ed0ae685d3f70595aaeb561b5228dfbda71f51', 'U', NULL, 5, 'N'),
	(40, 'user39', '김시민', 'dd69a400ddf28026fe94595eafadd0d1d7163301162a17e3cf7b36115d02e693', 'U', NULL, 6, 'N'),
	(41, 'user40', '이순신', '39a6bd68eee930e937ba05904fd4379444bb6f33c45232d93844788da336c4ee', 'U', NULL, 2, 'N'),
	(42, 'user41', '성삼문', 'ce063ef25241f49ef0adef10d7c2ba9108df3aca630b7f03f160d41a7e198c8e', 'U', NULL, 3, 'N'),
	(43, 'user42', '박팽년', 'fb44d98b9d56bbe49028eacc8574f5715178e6d3470d276a1697de3df68e7579', 'U', NULL, 4, 'N'),
	(44, 'user43', '하위지', 'f02916fe22baf673a04bc322efd5efc188fa45a378d47c2fd9effaa2952b33ae', 'U', NULL, 5, 'N'),
	(45, 'user44', '이개', '0d28f6929860381621e417f808a811e85d60442aa7d29b69a6302d8d2321c57b', 'U', NULL, 6, 'N'),
	(46, 'user45', '유응부', '435fa81d2e3e34b76e40154d878de9f7df8bdc18429d7321f14cd3cb906766a1', 'U', NULL, 2, 'N'),
	(47, 'user46', '유성원', '47a268f40a503fc100a1b3ab3413d241c1861f37228761dd2198a94fc6d06811', 'U', NULL, 3, 'N'),
	(48, 'user47', '김시습', '8a43bbd15dc9d40eb8556bc68608a3beb80a3f6903a1c55c1c1686151ea7c50c', 'U', NULL, 4, 'N'),
	(49, 'user48', '원호', '70df126f7888b99fd00f45feae219ac1b75c09ae67f84dcf9ecc2d45d54e9f37', 'U', NULL, 5, 'N'),
	(50, 'user49', '이맹전', '62752906d749b398b283bb8faa750cc4f0c40e69bef872c80580fa4ff71d65fe', 'U', NULL, 6, 'N'),
	(51, 'user50', '조려', '59ea123c0d70b150939706c1ed9822e76f87a73d159a4a67792924a85c6f92bb', 'U', NULL, 2, 'N'),
	(52, 'user51', '성담수', '120ab541bb461227b08dbca747bf864427dd25db286e920834f1428d89847617', 'U', NULL, 3, 'N'),
	(53, 'user52', '남효온', '24a1b3b47863af96bc133341991d6bdbc21ab12557facbd02fb9c56ec6ec1dcc', 'U', NULL, 4, 'N'),
	(54, 'user53', '논개', '98e98b7a251f18aac9d33b2b5281b561381d478afc594a7d0d4a793fc40b7ced', 'U', NULL, 5, 'N'),
	(55, 'user54', '권율', 'f1240fdecd4f7290aaf18e01de8b5818f83d40581295d97ea6864c215797b160', 'U', NULL, 6, 'N'),
	(56, 'user55', '홍길동', '8d74ee260a93cb1f5a340b66b8fca9a7650de10a3771965705804634c8e0c857', 'U', NULL, 2, 'N'),
	(57, 'user56', '임꺽정', '3d0273794c791b208eb8f2a1172f8052e476570f79db889c0c19cd79f17a73ab', 'U', NULL, 3, 'N'),
	(58, 'user57', '홍익한', 'a36931dd2a776f1ad77068a410260c6d7e9eca6640fd47e25a6cfca1423a9926', 'U', NULL, 4, 'N'),
	(59, 'user58', '윤집', 'ce364c858b36c7b91dd83d8664055e7cb5eae7cacacba674e66f208488c0af6e', 'U', NULL, 5, 'N'),
	(60, 'user59', '오달제', '02dbbc8a7483ed7ef37e34cb6bf3c41c0851721b7a4cc90eb84164baf81d9399', 'U', NULL, 6, 'N'),
	(61, 'user60', '박문수', 'f4b7e0d1f9fa0594a13aedbaee5599f65e69e1e7562537ddd443404c791bd927', 'U', NULL, 2, 'N'),
	(62, 'user61', '한석봉', '714556c2e812784d374c4a64fe487de33d637e393f29672e8cb96d6152c016f4', 'U', NULL, 3, 'N'),
	(63, 'user62', '김홍도', '56dfcd67f783fe26b80e6a58a3f5afc7aaad511adfd967bee23afb378e9a0472', 'U', NULL, 4, 'N'),
	(64, 'user63', '김병연', 'd4e146fb59c16dccc05a72f977b98f9040c4886592c17cda83a428c61486d216', 'U', NULL, 5, 'N'),
	(65, 'user64', '김정호', 'dd6d3ece19671acfc0d4b1e9541a33659bfdb59a3126f6bf483fda76eb81e864', 'U', NULL, 6, 'N'),
	(66, 'user65', '정약용', '55014d30edc4478f9ea6b5c56baf0a0da1d3c98003ae8be9fa3d0c3529461300', 'U', NULL, 2, 'N'),
	(67, 'user66', '전봉준', '611f51d44fd7c795ea1ff0bee188e0f4039e7f17b8b42378a07c10d3064923b0', 'U', NULL, 3, 'N'),
	(68, 'user67', '김대건', '97cdd97cc8798a1a7377141eda51c21af0016be9d594729c8ee708672ce607b0', 'U', NULL, 4, 'N'),
	(69, 'user68', '황진이', '6e5acacca08fe171c57529438f3f14e6c67d5aa4cd4d3a024e30701653ae8ff0', 'U', NULL, 5, 'N'),
	(70, 'user69', '홍경래', 'a36b4f42b54219a5dbb5c98680f23adb135b72cd48d34beed6b915ad3632494c', 'U', NULL, 6, 'N'),
	(71, 'user70', '김옥균', 'da08eac8478e4c85f16285f8e6acae04c4b1b337b60f0aea18b3607596611df0', 'U', NULL, 2, 'N'),
	(72, 'user71', '안중근', '407d3c1df7b982a9feef9b5c907371342b218502a191254241d5b573bed0fa6c', 'U', NULL, 3, 'N'),
	(73, 'user72', '윤동주', '2bc6ebdf1967fa1d0dd6f19e6812d0a2ad54eac41cc9b863711aafa4e1180278', 'U', NULL, 4, 'N'),
	(74, 'user73', '지석영', '746465eb17f97008c05b4e0fdfb962c0808a901be5e9aca47426d48d0245b13a', 'U', NULL, 5, 'N'),
	(75, 'user74', '손병희', '9de6a56e7c76c073625f401cc25dad0320bdbb5249f5c53bf6a8f5c8974a9ab0', 'U', NULL, 6, 'N'),
	(76, 'user75', '유관순', 'cd8cbd30586fb98f2901e0b702b6e7c2ef4278fac2dc304fa36394ab68101a5b', 'U', NULL, 2, 'N'),
	(77, 'user76', '안창호', '023736a413a16146837bb207159639c81b0c680e22e0d0c123c06f81241ef214', 'U', NULL, 3, 'N'),
	(78, 'user77', '방정환', '361c39697ce48d431423460a3854b229f41550a97db935f21a84f4c5f3dce61c', 'U', NULL, 4, 'N'),
	(79, 'user78', '김두한', '706be5e3c19ac35208c6ca79238f197cd0df048bb07ecb6aacb1e5008481baaa', 'U', NULL, 5, 'N'),
	(80, 'user79', '이상', 'bbee94b7e5bc646a4030556612871e939251a7fc5eaef785eafdae12c2ecc939', 'U', NULL, 6, 'N'),
	(81, 'user80', '이중섭', 'eb9c5e78498aa74b123eddf29c801ae7e9d636e39a3cc34d3c46abad28342e2c', 'U', NULL, 2, 'N');


UPDATE COM_USER SET USERPOS='01';
UPDATE COM_USER SET USERPOS='02' WHERE USERNO BETWEEN 60 AND 59;
UPDATE COM_USER SET USERPOS='03' WHERE USERNO BETWEEN 40 AND 49;
UPDATE COM_USER SET USERPOS='04' WHERE USERNO BETWEEN 30 AND 39;
UPDATE COM_USER SET USERPOS='05' WHERE USERNO BETWEEN 20 AND 29;
UPDATE COM_USER SET USERPOS='06' WHERE USERNO BETWEEN 10 AND 19;
UPDATE COM_USER SET USERPOS='07' WHERE USERNO < 10;


INSERT INTO `TBL_BOARD` (`BGNO`, `BRDNO`, `BRDTITLE`, `USERNO`, `BRDMEMO`, `REGDATE`, `CHGDATE`, `CHGUSERNO`, `DELETEFLAG`) VALUES
	(3, 2, '모바일 웹 사이트 제작 및 프로그래밍', 11, '모바일 웹 사이트 제작 및 프로그래밍', now(), now(), 11, 'N'),
	(3, 3, '세상의 속도를 따라잡고 싶다면 DO IT(HTML5 CSS3)', 15, '세상의 속도를 따라잡고 싶다면 DO IT(HTML5 CSS3)', now(), now(), 15, 'N'),
	(3, 4, '마음을 움직이는 콘텐츠디자인', 44, '마음을 움직이는 콘텐츠디자인', now(), now(), 44, 'N'),
	(3, 5, '필요할 때마다 골라쓰는 프레젠테이션 디자인 100', 13, '필요할 때마다 골라쓰는 프레젠테이션 디자인 100', now(), now(), 13, 'N'),
	(3, 6, '안드로이드 앱 개발 완벽 가이드', 15, '안드로이드 앱 개발 완벽 가이드', now(), now(), 15, 'N'),
	(3, 7, '가치관 경영', 36, '가치관 경영', now(), now(), 36, 'N'),
	(3, 8, 'Ext JS 웹 애플리케이션 개발', 55, 'Ext JS 웹 애플리케이션 개발', now(), now(), 55, 'N'),
	(3, 9, '힐링 코드', 6, '힐링 코드', now(), now(), 6, 'N'),
	(3, 10, '고급 웹 표준 사이트제작을 위한 CSS 마스터 전략', 29, '고급 웹 표준 사이트제작을 위한 CSS 마스터 전략', now(), now(), 29, 'N'),
	(3, 11, 'DB나라-나는 DB다', 44, 'DB나라-나는 DB다', now(), now(), 44, 'N'),
	(3, 12, 'HTML5 Canvas', 53, 'HTML5 Canvas', now(), now(), 53, 'N'),
	(3, 13, 'Javascript Graphics', 53, 'Javascript Graphics', now(), now(), 53, 'N'),
	(3, 14, '루씬 인 액션 - 오픈소스 자바 검색엔진', 24, '루씬 인 액션 - 오픈소스 자바 검색엔진', now(), now(), 24, 'N'),
	(3, 15, 'Jquery in action', 44, 'Jquery in action', now(), now(), 44, 'N'),
	(3, 16, 'Jquery 1.7 작고 강력한 자바스크립트', 66, 'Jquery 1.7 작고 강력한 자바스크립트', now(), now(), 66, 'N'),
	(3, 17, '프로 스프링3', 37, '프로 스프링3', now(), now(), 37, 'N'),
	(3, 18, '스트럿츠 2 프로그래밍', 68, '스트럿츠 2 프로그래밍', now(), now(), 68, 'N'),
	(3, 19, '스트럿츠2 실무 프로그래밍', 69, '스트럿츠2 실무 프로그래밍', now(), now(), 69, 'N'),
	(3, 20, 'CSS 비밀 매뉴얼(감추고 싶은 나만의 비밀 노트) ', 62, 'CSS 비밀 매뉴얼(감추고 싶은 나만의 비밀 노트) ', now(), now(), 62, 'N'),
	(3, 21, 'Ext JS 4 First look 한국어판/화려한 웹 애플리케이션을 위한 Ext JS 입문', 20, 'Ext JS 4 First look 한국어판/화려한 웹 애플리케이션을 위한 Ext JS 입문', now(), now(), 20, 'N'),
	(3, 22, 'SQL 전문가 가이드', 77, 'SQL 전문가 가이드', now(), now(), 77, 'N'),
	(3, 23, '성장의 정석 CRS 기업의 지속 성장을 위한 핵심 경영 전략', 3, '성장의 정석 CRS 기업의 지속 성장을 위한 핵심 경영 전략', now(), now(), 3, 'N'),
	(3, 24, '실전 프로젝트 관리를 위한 MS Project 2010 ', 27, '실전 프로젝트 관리를 위한 MS Project 2010 ', now(), now(), 27, 'N'),
	(3, 25, '워드프레스 제대로 파기', 45, '워드프레스 제대로 파기', now(), now(), 45, 'N'),
	(3, 26, '클라우드 컴퓨팅 구현 기술: 구글, 페이스북, 야후, 아마존이 채택한 핵심 기술 파헤치기', 64, '클라우드 컴퓨팅 구현 기술: 구글, 페이스북, 야후, 아마존이 채택한 핵심 기술 파헤치기', now(), now(), 64, 'N'),
	(3, 27, 'Real MySQL: 개발자와 DBA를 위한', 25, 'Real MySQL: 개발자와 DBA를 위한', now(), now(), 25, 'N'),
	(3, 28, '스프링4 프로그래밍(Getting started with Spring Framework)', 15, '스프링4 프로그래밍(Getting started with Spring Framework)', now(), now(), 15, 'N'),
	(3, 29, '웹 퍼포먼스 모니터링과 디버깅', 80, '웹 퍼포먼스 모니터링과 디버깅', now(), now(), 80, 'N'),
	(3, 30, '아파치 Solr 4 구축과 관리(루씬 검색 플랫폼)', 31, '아파치 Solr 4 구축과 관리(루씬 검색 플랫폼)', now(), now(), 31, 'N'),
	(3, 31, '2015 알기쉬운 정보보안 기사 필기', 78, '2015 알기쉬운 정보보안 기사 필기', now(), now(), 78, 'N'),
	(3, 32, '2016 알기쉬운 정보보안 기사 실기', 54, '2016 알기쉬운 정보보안 기사 실기', now(), now(), 54, 'N'),
	(3, 33, 'Apache Jmeter', 37, 'Apache Jmeter', now(), now(), 37, 'N'),
	(3, 34, '코드 품질 시각화의 정석', 24, '코드 품질 시각화의 정석', now(), now(), 24, 'N'),
	(3, 35, 'RabbitMQ 따라잡기 : AMQP 기반의 오픈소스 메시지 브로커 ', 10, 'RabbitMQ 따라잡기 : AMQP 기반의 오픈소스 메시지 브로커 ', now(), now(), 10, 'N'),
	(3, 36, 'Hyper-V를 다루는 기술', 59, 'Hyper-V를 다루는 기술', now(), now(), 59, 'N'),
	(3, 37, '윈도우 서버 2012 R2', 24, '윈도우 서버 2012 R2', now(), now(), 24, 'N'),
	(3, 38, '거침없이 배우는 Jboss', 24, '거침없이 배우는 Jboss', now(), now(), 24, 'N');
    
    
CALL MAKECALENDAR();
