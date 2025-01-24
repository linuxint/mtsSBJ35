SELECT
    COUNT(*)
FROM
    -- 주말 제외한 평일
    (SELECT
         TM.DATES
          ,TM.DAYS
     FROM (SELECT
               TO_CHAR(START_DT + LEVEL - 1, 'YYYY-MM-DD') DATES
                ,TO_CHAR (START_DT + LEVEL - 1, 'D') DAYS
           FROM (	SELECT
                         TRUNC(TO_DATE(SYSDATE),'MM') START_DT
                          ,TO_NUMBER(TO_CHAR(LAST_DAY(SYSDATE), 'DD')) END_DT FROM DUAL
                )
           CONNECT BY LEVEL <= END_DT ) TM
     WHERE TM.DAYS NOT IN ('1', '7')
     ORDER BY TM.DATES ASC) WEEKDAY
        -- DB에 저장된 공휴일
        LEFT JOIN (SELECT
                       EXTRACT(year from sysdate ) || '-' || SHMONTH || '-' || SHDATE AS DATES
                   FROM SCH_HOLIDAY
                   WHERE SHMONTH = TO_CHAR(TO_DATE(SYSDATE), 'MM')) HOLIDAY
                  ON WEEKDAY.DATES = HOLIDAY.DATES
WHERE HOLIDAY.DATES IS NULL

;

SELECT CASE WHEN TO_CHAR(TO_DATE(DT),'D') IN ('1','7')
                THEN '주말'
            ELSE '평일' END AS WEEK_DAY
FROM (
         SELECT /*+ MATERIALIZE */ TO_CHAR(TO_DATE(ST_DT, 'YYYYMMDD') + LEVEL - 1, 'YYYYMMDD') AS DT
         FROM ( SELECT '20210801' AS ST_DT,'20210830' AS END_DT
                FROM DUAL )
         CONNECT BY LEVEL <=  TO_DATE(END_DT, 'YYYYMMDD') - TO_DATE(ST_DT, 'YYYYMMDD') + 1
     );

SELECT
    *
FROM PRJ_TASK