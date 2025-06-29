-- DROP FUNCTION uf_datetime2string;
CREATE OR REPLACE FUNCTION uf_datetime2string(dt_ IN DATE)
    RETURN VARCHAR2
    IS
    ti NUMBER;
    ret_ varchar2(10);
BEGIN
    ti := to_date(SYSDATE,'YYYY-MM-DD HH24:MI') - to_date(dt_,'YYYY-MM-DD HH24:MI');
    IF ti < 1 THEN
        ret_:='방금';
    ELSIF ti < 60 THEN
        ret_:= CONCAT(TRUNC(ti ,0), '분전');
    ELSIF ti < 60*24 THEN
        IF (abs(trunc(sysdate) - to_date(dt_,'YYYY-MM-DD')) =1) THEN
            ret_:= '어제';
        ELSE
            ret_:= CONCAT(TRUNC(ti/60 ,0), '시간전');
        END IF;
    ELSIF ti < 60*24*7 THEN
        ret_:= CONCAT(TRUNC(ti/60/24 ,0), '일전');
    ELSIF ti < 60*24*7*4 THEN
        ret_:= CONCAT(TRUNC(ti/60/24/7 ,0), '주전');
    ELSIF (MONTHS_BETWEEN(dt_, SYSDATE)=1) THEN
        ret_:= '지난달';
    ELSIF (MONTHS_BETWEEN(dt_, SYSDATE)<13) THEN
        IF (MONTHS_BETWEEN(dt_, SYSDATE)/12 =1) THEN
            ret_:= '작년';
        ELSE
            ret_:= TRUNC(MONTHS_BETWEEN(dt_, SYSDATE) ,0)||'달전';
        END IF;
    ELSE
        ret_:= TRUNC(MONTHS_BETWEEN(dt_, SYSDATE)/12) || '년전';
    END IF;

RETURN ret_;

END uf_datetime2string;
/

CREATE OR REPLACE FUNCTION makeCalendar(startdate IN VARCHAR2, enddate IN VARCHAR2)
    RETURN VARCHAR2
    IS
    PRAGMA AUTONOMOUS_TRANSACTION;
    ret_ varchar2(10) := null;
    sdate date := to_date(startdate,'YYYY-MM-DD');
    edate date := to_date(enddate,'YYYY-MM-DD');
BEGIN

    DBMS_OUTPUT.PUT_LINE('sdate=' || sdate);
    DBMS_OUTPUT.PUT_LINE('edate=' || edate);

    IF sdate IS NULL THEN
        SELECT MAX(TO_DATE(CDDATE,'YYYY-MM-DD'))+1, MAX(TO_DATE(CDDATE,'YYYY-MM-DD'))+300
        INTO sdate, edate
        FROM COM_DATE;
    END IF;

    DBMS_OUTPUT.PUT_LINE('sdate=' || TO_CHAR(sdate,'YYYY-MM-DD'));
    DBMS_OUTPUT.PUT_LINE('edate=' || TO_CHAR(edate,'YYYY-MM-DD'));

    IF edate <= sdate THEN
        DBMS_OUTPUT.PUT_LINE('edate <= sdate RET');
        RETURN ret_;
    END IF;

    WHILE (sdate <= edate)
        LOOP
            DBMS_OUTPUT.PUT_LINE('LOOP ' || TO_CHAR(sdate,'YYYY-MM-DD'));

            INSERT INTO COM_DATE (CDNO, CDDATE, CDYEAR, CDMM, CDDD, CDWEEKOFYEAR, CDWEEKOFMONTH, CDWEEK, CDDAYOFWEEK)
            SELECT CDNO_SEQ.NEXTVAL,
                   TO_CHAR(sdate, 'YYYY-MM-DD'),
                   EXTRACT(year from TO_DATE(TO_DATE(sdate))),
                   EXTRACT(month from TO_DATE(TO_DATE(sdate))),
                   EXTRACT(day from TO_DATE(TO_DATE(sdate))),
                   TO_CHAR(TO_DATE(sdate), 'IW'),
                   TO_CHAR(sdate, 'WW'),
                   TO_CHAR(sdate + 1, 'IW') - 1,
                   TO_CHAR(sdate, 'D')
            FROM DUAL;

            SELECT sdate + 1 INTO sdate FROM DUAL;

        END LOOP;
        DBMS_OUTPUT.PUT_LINE('LOOP END');

    COMMIT;

    RETURN ret_;

END makeCalendar;
---- end makeCalendar

-- DROP FUNCTION getColor4Alert;

CREATE OR REPLACE FUNCTION getColor4Alert(TSSTARTDATE_ IN VARCHAR2
, TSENDDATE_ IN VARCHAR2
, TSENDREAL_ IN VARCHAR2
, TSRATE_ IN NUMBER)
    RETURN VARCHAR2
    IS
    BGCOLOR_ varchar2(10)  := 'gray'; -- tsstartdate < today
    TODAY_   TIMESTAMP(6) := SYSDATE;
BEGIN
    /*
      오늘 날짜가 시작일자보다 작으면 회색
      오늘 날짜가 종료일자를 지났으면 빨강
      오늘 날짜가 주어진 기간의 50% 미만이면 녹색
      오늘 날짜가 주어진 기간의 50% 이상이면 노랑
      진행율이 기간내에 100이 되었으면 녹색, 지난뒤에 100이면 검정색
    */
    IF TSRATE_ < 100 THEN
        IF TSSTARTDATE_ > TODAY_ THEN
            BGCOLOR_ := 'gray';
        ELSIF TSENDDATE_ < TODAY_ THEN
            BGCOLOR_ := 'red';
        ELSIF TO_DATE(TSSTARTDATE_, 'YYYY-MM-DD') - TRUNC(SYSDATE) < TRUNC(SYSDATE) - TO_DATE(TSENDDATE_, 'YYYY-MM-DD') THEN
            BGCOLOR_ := 'green';
        ELSIF TO_DATE(TSSTARTDATE_, 'YYYY-MM-DD') - TRUNC(SYSDATE) >= TRUNC(SYSDATE) - TO_DATE(TSENDDATE_, 'YYYY-MM-DD') THEN
            BGCOLOR_ := 'yellow';
        END IF;
    ELSE
        IF TSENDREAL_ IS NOT NULL AND TSENDREAL_ <= TSENDDATE_ THEN
            BGCOLOR_ := 'green';
        ELSE
            BGCOLOR_ := 'orange';
        END IF;
    END IF;

RETURN BGCOLOR_;

END;
/

