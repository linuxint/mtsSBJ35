<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="../inc/header.jsp" %>
    <style>
        .listPeople {
            background-color: #d8e3ea;
        }
        .listHead {
            background-color: #eaedee;
        }
    </style>
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
                <c:if test="${projectInfo.userno == sessionScope.userno}">
                    <a href="projectForm?prno=<c:out value="${projectInfo.prno}"/>"><i class="fa fa-edit fa-fw" title="<s:message code="common.btnUpdate"/>"></i></a>
                </c:if>
            </div>
        </div>
        <p>&nbsp;</p>
        <!-- /.row -->
        <div class="row">
            <ul class="nav nav-pills">
                <li><a href='task?prno=<c:out value="${prno}" />'><i class="fa fa-tasks fa-fw"></i><s:message code="project.taskMgr"/></a></li>
                <li><a href="taskCalendar?prno=<c:out value="${prno}" />"><i class="fa fa-calendar  fa-fw"></i><s:message code="project.calendar"/></a></li>
                <li class="active"><a href="taskWorker?prno=<c:out value="${prno}" />"><i class="fa fa-user fa-fw"></i><s:message code="project.taskWorker"/></a></li>
                <li><a href="taskMine?prno=<c:out value="${prno}" />"><s:message code="project.taskMine"/></a></li>
            </ul>
        </div>

        <!-- /.row -->
        <div class="panel panel-default">
            <div class="panel-body">
                <c:if test="${listview.size()==0}">
                    <div class="listBody height200">데이터가 없습니다.
                    </div>
                </c:if>
                <c:forEach var="listview" items="${listview}" varStatus="status">
                    <c:if test="${taskCurUserno != listview.userno}">
                        <p class="listPeople"><i class="fa fa-user fa-fw"></i> <strong><c:out value="${listview.usernm}"/></strong></p>
                        <div class="listHead">
                            <div class="listHiddenField pull-left field60"><s:message code="board.no"/></div>
                            <div class="listHiddenField pull-right field100"><s:message code="project.rate"/></div>
                            <div class="listHiddenField pull-right field130"><s:message code="project.endreal"/></div>
                            <div class="listHiddenField pull-right field100"><s:message code="project.enddate"/></div>
                            <div class="listHiddenField pull-right field100"><s:message code="project.startdate"/></div>
                            <div class="listTitle"><s:message code="project.task"/></div>
                        </div>
                    </c:if>
                    <c:set var="taskCurUserno" value="${listview.userno}"/>
                    <c:url var="link" value="taskMineForm">
                        <c:param name="tsno" value="${listview.tsno}"/>
                    </c:url>
                    <div class="listBody">
                        <div class="listHiddenField pull-left field60 textCenter"><c:out value="${status.index+1}"/></div>
                        <div class="listHiddenField pull-right field100 textCenter">
                            <div style="width:100%;border:1px solid <c:out value="${listview.statuscolor}"/>">
                                <div style="width:<c:out value="${listview.tsrate}"/>%;background:<c:out value="${listview.statuscolor}"/>; color:#000000"><c:out value="${listview.tsrate}"/>%</div>
                            </div>
                        </div>
                        <div class="listHiddenField pull-right field130 textCenter"><c:out value="${listview.tsendreal}"/></div>
                        <div class="listHiddenField pull-right field100 textCenter"><c:out value="${listview.tsenddate}"/></div>
                        <div class="listHiddenField pull-right field100 textCenter"><c:out value="${listview.tsstartdate}"/></div>
                        <div class="listTitle" title="<c:out value="${listview.tstitle}"/>">
                            <c:choose>
                                <c:when test="${projectInfo.userno == sessionScope.userno}">
                                    <a href="${link}"><c:out value="${listview.tstitle}"/></a>
                                </c:when>
                                <c:otherwise>
                                    <c:out value="${listview.tstitle}"/>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="showField text-muted small">
                            <c:out value="${listview.tsstartdate}"/> ~
                            <c:out value="${listview.tsenddate}"/>
                            <c:out value="${listview.tsrate}"/>%
                            <c:out value="${listview.usernm}"/>
                        </div>
                    </div>

                </c:forEach>

            </div>
        </div>
        <!-- /.row -->
    </div>
    <!-- /#page-wrapper -->

</div>
<!-- /#wrapper -->
</body>

<%@include file="../inc/footer.jsp" %>