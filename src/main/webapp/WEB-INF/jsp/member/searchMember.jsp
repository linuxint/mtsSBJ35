<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="../inc/header.jsp" %>
    <script>
        function fn_formSubmit() {
            if (!chkInputValue("#searchKeyword", "<s:message code="common.keyword"/>")) return false;

            $("#form1").submit();
        }
    </script>
</head>

<body>

<div id="wrapper">

    <jsp:include page="../common/navigation.jsp"/>

    <div id="page-wrapper">
        <div class="row">
            <div class="col-lg-12">
                <h1 class="page-header"><i class="fa fa-users fa-fw"></i> <s:message code="menu.users"/></h1>
            </div>
        </div>

        <!-- /.row -->
        <div class="row">
            <form role="form" id="form1" name="form1" method="post">
                <div class="form-group">
                    <div class="checkbox col-lg-4">
                        <s:message code="msg.page.inputName"/>
                    </div>
                    <div class="input-group custom-search-form col-lg-3">
                        <input class="form-control" placeholder="Search..." type="text" id="searchKeyword" name="searchKeyword"
                               value='<c:out value="${searchVO.searchKeyword}"/>'>
                        <span class="input-group-btn">
                            <button class="btn btn-default" onclick="fn_formSubmit()"><i class="fa fa-search"></i></button>
                            </span>
                    </div>
                </div>
            </form>
        </div>

        <!-- /.row -->
        <div class="row">
            <table class="table table-striped table-bordered table-hover">
                <colgroup>
                    <col width='10%'/>
                    <col width='25%'/>
                    <col width='25%'/>
                    <col width='20%'/>
                </colgroup>
                <thead>
                <tr>
                    <th><s:message code="board.no"/></th>
                    <th><s:message code="common.id"/></th>
                    <th><s:message code="common.name"/></th>
                    <th><s:message code="common.deptName"/></th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="listview" items="${listview}" varStatus="status">
                    <tr>
                        <td><c:out value="${status.index+1}"/></td>
                        <td><c:out value="${listview.userid}"/></td>
                        <td><c:out value="${listview.usernm}"/></td>
                        <td><c:out value="${listview.deptno}"/></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
        <!-- /.row -->
    </div>
    <!-- /#page-wrapper -->

</div>
<!-- /#wrapper -->
</body>

<%@include file="../inc/footer.jsp" %>