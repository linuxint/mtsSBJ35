<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="../inc/header.jsp" %>
    <style>

        #calendar-container {
            position: fixed;
            top: 250px;
            left: 270px;
            right: 20px;
            bottom: 20px;
        }
        /* 일요일 날짜: 빨간색 */
        .fc-day-sun a {
            color: red;
        }

        /* 토요일 날짜: 파란색 */
        .fc-day-sat a {
            color: blue;
        }
    </style>
    <script>

        document.addEventListener('DOMContentLoaded', function () {
            var calendarEl = document.getElementById('calendar');
            var data = [
                <c:forEach var="listview" items="${listview}" varStatus="status">
                <c:if test="${listview.tsstartdate != ''}">
                {
                    "id": '<c:out value="${listview.tsno}" />'
                    , "title": '<c:out value="${listview.tstitle}" />'
                    , "start": "<c:out value="${listview.tsstartdate}" />"
                    <c:if test="${listview.tsenddate != ''}">
                    , "end": "<c:out value="${listview.tsenddate}" />"
                    </c:if>
                    , "color": "<c:out value="${listview.statuscolor}" />"
                } <c:if test="${!status.last}">, </c:if>
                </c:if>
                </c:forEach>
            ];
            var calendar = new FullCalendar.Calendar(calendarEl, {
                height: '100%',
                expandRows: true,
                slotMinTime: '08:00',
                slotMaxTime: '20:00',
                headerToolbar: {
                    left: 'prevYear,prev,next,nextYear today',
                    center: 'title',
                    right: 'dayGridMonth,timeGridWeek,timeGridDay,listWeek'
                },
                initialView: 'dayGridMonth',
                initialDate: new Date(),
                locale: 'ko',
                navLinks: true, // can click day/week names to navigate views
                editable: true,
                selectable: true,
                nowIndicator: true,
                dayMaxEvents: true, // allow "more" link when too many events
                events: data,
                eventTextColor: 'black',
                eventClick: function (calEvent, jsEvent, view) {
                    calEvent.jsEvent.preventDefault(); // don't let the browser navigate

                    $.ajax({
                        url: "taskCalenPopup",
                        type: "post",
                        data: {tsno: calEvent.event.id}
                    }).success(function (result) {
                            $("#calendarItem").html(result);
                        }
                    );
                    $("#calendarItem").modal("show");
                }
            });
            calendar.render();
        });
    </script>
</head>

<body>

<div id="wrapper">

    <jsp:include page="../common/navigation.jsp"/>

    <div id="page-wrapper">
        <div class="row">
            <div class="col-lg-12">
                <h1 class="page-header"><i class="fa fa-tasks fa-fw"></i> <s:message code="project.title"/></h1>
            </div>
            <!-- /.col-lg-12 -->
        </div>
        <!-- /.row -->
        <div class="row">
            <div class="col-lg-12">
                <c:out value="${projectInfo.prtitle}"/> ( <c:out value="${projectInfo.prstartdate}"/> ~ <c:out value="${projectInfo.prenddate}"/>)
                <c:if test="${projectInfo.userno==sessionScope.userno}">
                    <a href="projectForm?prno=<c:out value="${projectInfo.prno}"/>"><i class="fa fa-edit fa-fw" title="<s:message code="common.btnUpdate"/>"></i></a>
                </c:if>
            </div>
        </div>
        <p>&nbsp;</p>
        <!-- /.row -->
        <div class="row">
            <ul class="nav nav-pills">
                <li><a href='task?prno=<c:out value="${prno}" />'><i class="fa fa-tasks fa-fw"></i><s:message code="project.taskMgr"/></a></li>
                <li class="active"><a href="#profile" data-toggle="tab"><i class="fa fa-calendar  fa-fw"></i><s:message code="project.calendar"/></a></li>
                <li><a href="taskWorker?prno=<c:out value="${prno}" />"><i class="fa fa-user fa-fw"></i><s:message code="project.taskWorker"/></a></li>
                <li><a href="taskMine?prno=<c:out value="${prno}" />"><s:message code="project.taskMine"/></a></li>
            </ul>
        </div>
        <p>&nbsp;</p>
        <!-- /.row -->
        <div class="row">
            <div id='calendar-container'>
                <div id='calendar'></div>
            </div>
        </div>
        <!-- /.row -->
    </div>
    <!-- /#page-wrapper -->

</div>
<!-- /#wrapper -->

<div id="calendarItem" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
</div>

</body>

<%@include file="../inc/footer.jsp" %>