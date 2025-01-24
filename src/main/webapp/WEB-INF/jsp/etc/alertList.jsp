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
                <h1 class="page-header"><i class="fa fa-bell fa-fw"></i> <s:message code="common.alert"/></h1>
            </div>
            <!-- /.col-lg-12 -->
        </div>

        <!-- /.row -->
        <div class="panel panel-default">
            <div class="panel-body">
                <div class="listHead">
                    <div class="listHiddenField pull-right field60"><s:message code="common.like"/></div>
                    <div class="listHiddenField pull-right field60"><s:message code="board.hitCount"/></div>
                    <div class="listHiddenField pull-right field130"><s:message code="board.date"/></div>
                    <div class="listHiddenField pull-right field100"><s:message code="board.writer"/></div>
                    <div class="listTitle"><s:message code="board.title"/></div>
                </div>

                <c:forEach var="listview" items="${listview}" varStatus="status">
                    <c:url var="link" value="boardRead">
                        <c:param name="brdno" value="${listview.brdno}"/>
                    </c:url>

                    <div class="listBody">
                        <div class="listHiddenField pull-right field60"><c:out value="${listview.brdlike}"/></div>
                        <div class="listHiddenField pull-right field60 textCenter"><c:out value="${listview.brdhit}"/></div>
                        <div class="listHiddenField pull-right field130 textCenter"><c:out value="${listview.regdate}"/></div>
                        <div class="listHiddenField pull-right field100 textCenter"><a href="boardList?bgno=<c:out value="${searchVO.bgno}"/>&searchExt1=<c:out value="${listview.userno}"/>"><c:out value="${listview.brdwriter}"/></a></div>
                        <div class="listTitle" title="<c:out value="${listview.brdtitle}"/>">
                            <div class="pull-left field30">
                                <i class="fa fa-<c:out value="${listview.extfield1}"/> fa-fw"></i>
                            </div>

                            <a href="${link}" <c:if test="${listview.brdnotice=='Y'}">class="notice"</c:if>><c:out value="${listview.brdtitle}"/></a>
                            <c:if test="${listview.replycnt>0}">
                                (<c:out value="${listview.replycnt}"/>)
                            </c:if>
                        </div>
                        <div class="showField text-muted small col-lg-12">
                            <c:out value="${listview.brdwriter}"/>
                            <c:out value="${listview.regdate}"/>
                            <i class="fa fa-eye fa-fw"></i> <c:out value="${listview.brdhit}"/>
                            <i class="fa fa-thumbs-o-up fa-fw"> <c:out value="${listview.brdlike}"/></i>
                        </div>
                    </div>

                </c:forEach>
                <br/>

            </div>
        </div>
        <!-- /.row -->
    </div>
    <!-- /#page-wrapper -->

</div>
<!-- /#wrapper -->
</body>

<%@include file="../inc/footer.jsp" %>