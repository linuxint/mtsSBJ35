<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="../inc/header.jsp" %>
    <script>

        function fn_formSubmit() {
            document.form1.submit();
        }

    </script>

</head>

<body>

<div id="wrapper">

    <jsp:include page="../common/navigation.jsp"/>

    <div id="page-wrapper">
        <div class="row">
            <div class="col-lg-12">
                <h1 class="page-header"><i class="fa fa-files-o fa-fw"></i> <s:message code="project.title"/></h1>
            </div>
            <!-- /.col-lg-12 -->
        </div>

        <!-- /.row -->
        <div class="row">
            <div class="col-lg-12">
                <button type="button" class="btn btn-default pull-right" onclick="fn_moveToURL('projectForm')">
                    <i class="fa fa-edit fa-fw"></i> <s:message code="project.new"/></button>
            </div>
        </div>
        <!-- /.row -->
        <div class="panel panel-default">
            <div class="panel-body">
                <div class="listHead">
                    <div class="listHiddenField pull-left field60"><s:message code="board.no"/></div>
                    <div class="listHiddenField pull-right field100"><s:message code="board.writer"/></div>
                    <div class="listHiddenField pull-right field100"><s:message code="project.status"/></div>
                    <div class="listHiddenField pull-right field100"><s:message code="project.enddate"/></div>
                    <div class="listHiddenField pull-right field100"><s:message code="project.startdate"/></div>
                    <div class="listTitle"><s:message code="project.name"/></div>
                </div>

                <c:forEach var="listview" items="${listview}" varStatus="status">
                    <c:url var="link" value="task">
                        <c:param name="prno" value="${listview.prno}"/>
                    </c:url>

                    <div class="listBody">
                        <div class="listHiddenField pull-left field60">
                            <c:out value="${searchVO.totRow-((searchVO.page-1)*searchVO.displayRowCount + status.index)}"/>
                        </div>

                        <div class="listHiddenField pull-right field100 textCenter"><c:out value="${listview.usernm}"/></div>
                        <div class="listHiddenField pull-right field100 textCenter"><c:out value="${listview.prstatus}"/></div>
                        <div class="listHiddenField pull-right field100 textCenter"><c:out value="${listview.prenddate}"/></div>
                        <div class="listHiddenField pull-right field100 textCenter"><c:out value="${listview.prstartdate}"/></div>
                        <div class="listTitle" title="<c:out value="${listview.prtitle}"/>">
                            <a href="${link}"><c:out value="${listview.prtitle}"/></a>
                        </div>

                        <div class="showField text-muted small">
                            <c:out value="${listview.usernm}"/>
                            <c:out value="${listview.prstartdate}"/>~<c:out value="${listview.prenddate}"/>
                            <c:out value="${listview.prstatus}"/>
                        </div>
                    </div>

                </c:forEach>

                <br/>
                <form role="form" id="form1" name="form1" method="post">
                    <jsp:include page="../common/pagingforSubmit.jsp"/>

                    <div class="form-group">
                        <div class="checkbox col-lg-3 pull-left">
                            <label class="pull-right">
                                <input type="checkbox" name="searchType" value="prtitle" <c:if test="${fn:indexOf(searchVO.searchType, 'prtitle')!=-1}">checked="checked"</c:if>/>
                                <s:message code="project.title"/>
                            </label>
                            <label class="pull-right">
                                <input type="checkbox" name="searchType" value="usernm" <c:if test="${fn:indexOf(searchVO.searchType, 'usernm')!=-1}">checked="checked"</c:if>/>
                                <s:message code="board.writer"/>
                            </label>
                        </div>
                        <div class="input-group custom-search-form col-lg-3">
                            <input class="form-control" placeholder="Search..." type="text" name="searchKeyword"
                                   value='<c:out value="${searchVO.searchKeyword}"/>'>
                            <span class="input-group-btn">
                                    <button class="btn btn-default" onclick="fn_formSubmit()">
                                        <i class="fa fa-search"></i>
                                    </button>
                                </span>
                        </div>
                    </div>
                </form>
            </div>
        </div>
        <!-- /.row -->
    </div>
    <!-- /#page-wrapper -->

</div>
<!-- /#wrapper -->
</body>

<%@include file="../inc/footer.jsp" %>
