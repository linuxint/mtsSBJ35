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
                <h1 class="page-header"><i class="fa fa-envelope-o fa-fw"></i> 메일정보관리</h1>
            </div>
            <!-- /.col-lg-12 -->
        </div>

        <!-- /.row -->
        <div class="row">
            <div class="col-lg-12">
                <button type="button" class="btn btn-default pull-right" onclick="fn_moveToURL('mailInfoForm')">
                    <i class="fa fa-edit fa-fw"></i> 서버정보추가
                </button>
            </div>
        </div>
        <!-- /.row -->
        <div class="row">
            <div class="col-lg-8">
                <div class="table-responsive table-bordered">
                    <table class="table">
                        <thead>
                        <tr>
                            <th>No.</th>
                            <th>IMAP</th>
                            <th>SMTP</th>
                            <th>계정</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="listview" items="${listview}" varStatus="status">
                            <c:url var="link" value="mailInfoForm">
                                <c:param name="emino" value="${listview.emino}"/>
                            </c:url>

                            <tr style="text-align:center">
                                <td><c:out value="${status.index+1}"/></td>
                                <td><a href="${link}"><c:out value="${listview.emiimap}"/></a></td>
                                <td><a href="${link}"><c:out value="${listview.emismtp}"/></a></td>
                                <td><a href="${link}"><c:out value="${listview.emiuser}"/></a></td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
        <!-- /.row -->
    </div>
    <!-- /#page-wrapper -->

</div>
<!-- /#wrapper -->
</body>

<%@include file="../inc/footer.jsp" %>
