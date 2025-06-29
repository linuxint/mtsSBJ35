-- SQLite 함수 정의 (H2, MySQL, Oracle 기준 동기화)

-- SQLite는 사용자 정의 함수를 지원하지만, 다른 데이터베이스와 문법이 다름
-- 여기서는 SQLite에서 사용할 수 있는 기본 함수들을 정의

-- 1. 현재 날짜/시간 함수 (SQLite 기본 제공)
-- datetime('now') - 현재 날짜와 시간
-- date('now') - 현재 날짜
-- time('now') - 현재 시간

-- 2. 문자열 함수 (SQLite 기본 제공)
-- length() - 문자열 길이
-- substr() - 부분 문자열
-- upper() - 대문자 변환
-- lower() - 소문자 변환
-- trim() - 공백 제거

-- 3. 숫자 함수 (SQLite 기본 제공)
-- abs() - 절대값
-- round() - 반올림
-- random() - 난수 생성

-- 4. 집계 함수 (SQLite 기본 제공)
-- count() - 개수
-- sum() - 합계
-- avg() - 평균
-- max() - 최대값
-- min() - 최소값

-- 참고: SQLite는 CREATE FUNCTION 문법을 지원하지 않으므로
-- 기본 제공 함수들을 사용하거나, 애플리케이션 레벨에서 함수를 구현해야 함

-- SQLite에서 사용할 수 있는 유용한 함수 예시:
-- SELECT datetime('now', '+1 day');  -- 내일
-- SELECT datetime('now', '-1 day');  -- 어제
-- SELECT date('now', 'start of month');  -- 이번 달 시작일
-- SELECT date('now', 'start of year');   -- 올해 시작일 