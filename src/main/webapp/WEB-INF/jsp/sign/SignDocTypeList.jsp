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
                <h1 class="page-header"><i class="fa fa-edit fa-fw"></i> <s:message code="electronic.draft"/></h1>
            </div>
            <!-- /.col-lg-12 -->
        </div>

        <!-- /.row -->
        <div class="panel panel-default">
            <div class="panel-body">
                <c:if test="${listview.size()==0}">
                    <div class="listBody height200">등록된 양식이 없습니다.<br/>결재문서양식이 등록되어야 합니다. 관리자에게 연락하세요.</div>
                </c:if>

                <br/>
                <c:forEach var="listview" items="${listview}" varStatus="status">
                    <c:url var="link" value="signDocForm">
                        <c:param name="dtno" value="${listview.dtno}"/>
                    </c:url>

                    <a href="${link}">
                        <div class="panel panel-default" style="width: 150px; height: 200px; display:inline-block; overflow:hidden; ">
                            <div class="panel-heading"><c:out value="${listview.dttitle}"/></div>
                            <div class="panel-body"><img src="images/if_survey_49353.png" style="margin: 30px 25px;"/></div>
                        </div>
                    </a>
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