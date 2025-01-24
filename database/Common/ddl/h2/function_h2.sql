CREATE
OR REPLACE FUNCTION uf_datetime2string(dt_ IN DATE) RETURN VARCHAR2 IS
    ti NUMBER;
    ret_
VARCHAR2(10);
BEGIN
    ti
:= (SYSDATE - dt_) * 24 * 60;

    IF ti < 1 THEN
        ret_ := '방금';
    ELSIF ti < 60 THEN
        ret_ := TO_CHAR(FLOOR(ti)) || '분전';
    ELSIF ti < 60 * 24 THEN
        IF TRUNC(SYSDATE) - TRUNC(dt_) = 1 THEN
            ret_ := '어제';
        ELSE
            ret_ := TO_CHAR(FLOOR(ti / 60)) || '시간전';
        END IF;
    ELSIF ti < 60 * 24 * 7 THEN
        ret_ := TO_CHAR(FLOOR(ti / 60 / 24)) || '일전';
    ELSIF ti < 60 * 24 * 7 * 4 THEN
        ret_ := TO_CHAR(FLOOR(ti / 60 / 24 / 7)) || '주전';
    ELSIF MONTHS_BETWEEN(SYSDATE, dt_) < 1 THEN
        ret_ := '지난달';
    ELSIF MONTHS_BETWEEN(SYSDATE, dt_) < 13 THEN
        IF FLOOR(MONTHS_BETWEEN(SYSDATE, dt_) / 12) = 1 THEN
            ret_ := '작년';
        ELSE
            ret_ := TO_CHAR(FLOOR(MONTHS_BETWEEN(SYSDATE, dt_))) || '달전';
        END IF;
    ELSE
        ret_ := TO_CHAR(FLOOR(MONTHS_BETWEEN(SYSDATE, dt_) / 12)) || '년전';
    END IF;

RETURN ret_;
END;
/
CREATE
OR REPLACE FUNCTION makeCalendar(startdate VARCHAR2, enddate VARCHAR2) IS
    sdate DATE := TO_DATE(startdate, 'YYYY-MM-DD');
    edate
DATE := TO_DATE(enddate, 'YYYY-MM-DD');
BEGIN
    DBMS_OUTPUT.PUT_LINE ('sdate=' || sdate);
    DBMS_OUTPUT.PUT_LINE ('edate=' || edate);

    IF sdate IS NULL THEN
        SELECT MAX(CDDATE) + 1,
               MAX(CDDATE) + 300
        INTO sdate, edate
        FROM COM_DATE;
    END IF;

    DBMS_OUTPUT.PUT_LINE ('sdate=' || TO_CHAR(sdate, 'YYYY-MM-DD'));
    DBMS_OUTPUT.PUT_LINE ('edate=' || TO_CHAR(edate, 'YYYY-MM-DD'));

    IF edate <= sdate THEN
        DBMS_OUTPUT.PUT_LINE('edate <= sdate RET');
        RETURN;
    END IF;

    WHILE sdate <= edate LOOP
        DBMS_OUTPUT.PUT_LINE('LOOP ' || TO_CHAR(sdate, 'YYYY-MM-DD'));

        INSERT INTO COM_DATE (CDDATE, CDYEAR, CDMM, CDDD, CDWEEKOFYEAR, CDWEEKOFMONTH, CDWEEK, CDDAYOFWEEK)
        SELECT sdate,
               EXTRACT(YEAR FROM sdate),
               EXTRACT(MONTH FROM sdate),
               EXTRACT(DAY FROM sdate),
               TO_NUMBER(TO_CHAR(sdate, 'IW')),
               FLOOR((EXTRACT(DAY FROM sdate) - 1) / 7) + 1,
               TO_NUMBER(TO_CHAR(sdate, 'WW')),
               TO_NUMBER(TO_CHAR(sdate, 'D'))
        FROM dual;
        sdate := sdate + 1;
    END LOOP;

    DBMS_OUTPUT.PUT_LINE ('LOOP END');
END;
/
---- end makeCalendar

-- DROP FUNCTION getColor4Alert;
CREATE
OR REPLACE FUNCTION getColor4Alert(
    tsstartdate_ IN VARCHAR2,
    tsenddate_ IN VARCHAR2,
    tsendreal_ IN VARCHAR2,
    tsrate_ IN INTEGER
) RETURN VARCHAR2 IS
    bgcolor_ VARCHAR2(10);
    today_
DATE := SYSDATE;
    tsstartdate
DATE := TO_DATE(tsstartdate_, 'YYYY-MM-DD');
    tsenddate
DATE := TO_DATE(tsenddate_, 'YYYY-MM-DD');
    tsendreal
DATE := CASE WHEN tsendreal_ IS NOT NULL AND tsendreal_ != '' THEN TO_DATE(tsendreal_, 'YYYY-MM-DD') ELSE NULL
END;
BEGIN
    -- 초기 백그라운드 색상 설정
    bgcolor_
:= 'gray';

    IF tsrate_ < 100 THEN
        IF tsstartdate > today_ THEN
            bgcolor_ := 'gray';
        ELSIF tsenddate < today_ THEN
            bgcolor_ := 'red';
        ELSIF (today_ - tsstartdate) < (tsenddate - today_) THEN
            bgcolor_ := 'green';
        ELSIF (today_ - tsstartdate) >= (tsenddate - today_) THEN
            bgcolor_ := 'yellow';
        END IF;
    ELSE
        IF tsendreal_ IS NOT NULL AND tsendreal <= tsenddate THEN
            bgcolor_ := 'green';
        ELSE
            bgcolor_ := 'orange';
        END IF;
    END IF;

RETURN bgcolor_;
END;