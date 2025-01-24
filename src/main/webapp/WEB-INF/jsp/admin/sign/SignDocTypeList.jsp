<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="../inc/ad_header.jsp" %>
    <script>
        function fn_formSubmit() {
            document.form1.submit();
        }
    </script>

</head>

<body>

<div id="wrapper">

    <jsp:include page="../../common/navigation.jsp"/>

    <div id="page-wrapper">
        <div class="row">
            <div class="col-lg-12">
                <h1 class="page-header"><i class="fa fa-edit fa-fw"></i> 결재문서양식</h1>
            </div>
            <!-- /.col-lg-12 -->
        </div>

        <!-- /.row -->
        <div class="row">
            <div class="col-lg-12">
                <button type="button" class="btn btn-default pull-right" onclick="fn_moveToURL('adSignDocTypeForm')">
                    <i class="fa fa-edit fa-fw"></i> 양식추가
                </button>
            </div>
        </div>
        <!-- /.row -->
        <div class="panel panel-default">
            <div class="panel-body">
                <div class="listHead">
                    <div class="listHiddenField pull-left field60"><s:message code="board.no"/></div>
                    <div class="listTitle">문서종류명</div>
                </div>

                <c:if test="${listview.size()==0}">
                    <div class="listBody height200">
                    </div>
                </c:if>

                <c:forEach var="listview" items="${listview}" varStatus="status">
                    <c:url var="link" value="adSignDocTypeForm">
                        <c:param name="dtno" value="${listview.dtno}"/>
                    </c:url>

                    <div class="listBody">
                        <div class="listHiddenField pull-left field60 textCenter"><c:out value="${searchVO.totRow-((searchVO.page-1)*searchVO.displayRowCount + status.index)}"/></div>
                        <div class="listTitle" title="<c:out value="${listview.dttitle}"/>">
                            <a href="${link}"><c:out value="${listview.dttitle}"/></a>
                        </div>
                    </div>
                </c:forEach>

                <br/>
                <form role="form" id="form1" name="form1" method="post">
                    <jsp:include page="../../common/pagingforSubmit.jsp"/>

                </form>
            </div>
        </div>
        <!-- /.row -->
    </div>
    <!-- /#page-wrapper -->

</div>
<!-- /#wrapper -->
</body>

<%@include file="../inc/ad_footer.jsp" %>