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
                <h1 class="page-header"><i class="fa fa-gear fa-fw"></i> <s:message code="common.codecd"/></h1>
            </div>
            <!-- /.col-lg-12 -->
        </div>

        <!-- /.row -->
        <div class="row">
            <div class="col-lg-12">
                <button type="button" class="btn btn-default pull-right" onclick="fn_moveToURL('adCodeForm')">
                    <i class="fa fa-edit fa-fw"></i> <s:message code="common.codecd"/></button>
            </div>
        </div>
        <!-- /.row -->
        <div class="panel panel-default">
            <div class="panel-body">
                <div class="listHead">
                    <div class="listHiddenField pull-left field60"><s:message code="common.classno"/></div>
                    <div class="listHiddenField pull-left field100"><s:message code="common.codecd"/></div>
                    <div class="listTitle"><s:message code="common.codenm"/></div>
                </div>

                <c:if test="${listview.size()==0}">
                    <div class="listBody height200">
                    </div>
                </c:if>
                <c:forEach var="listview" items="${listview}" varStatus="status">
                    <c:url var="link" value="adCodeRead">
                        <c:param name="classno" value="${listview.classno}"/>
                        <c:param name="codecd" value="${listview.codecd}"/>
                    </c:url>

                    <div class="listBody">
                        <div class="listHiddenField pull-left field60 textCenter"><c:out value="${listview.classno}"/></div>
                        <div class="listHiddenField pull-left field100 textCenter"><c:out value="${listview.codecd}"/></div>
                        <div class="listTitle" title="<c:out value="${listview.codenm}"/>">
                            <a href="${link}"><c:out value="${listview.codenm}"/></a>
                        </div>
                    </div>
                </c:forEach>

                <br/>
                <form role="form" id="form1" name="form1" method="post">
                    <jsp:include page="../../common/pagingforSubmit.jsp"/>

                    <div class="form-group">
                        <div class="checkbox col-lg-3 pull-left">
                            <label class="pull-right">
                                <input type="checkbox" name="searchType" value="codenm" <c:if test="${fn:indexOf(searchVO.searchType, 'codenm')!=-1}">checked="checked"</c:if>/>
                                <s:message code="common.codenm"/>
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

<%@include file="../inc/ad_footer.jsp" %>