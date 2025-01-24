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
                <h1 class="page-header"><i class="fa fa-gear fa-fw"></i> <s:message code="crud.title"/></h1>
            </div>
            <!-- /.col-lg-12 -->
        </div>

        <!-- /.row -->
        <div class="row">
            <div class="col-lg-12">
                <button type="button" class="btn btn-default pull-right" onclick="fn_moveToURL('crudForm')">
                    <i class="fa fa-edit fa-fw"></i> <s:message code="board.new"/></button>
            </div>
        </div>
        <!-- /.row -->
        <div class="panel panel-default">
            <div class="panel-body">
                <div class="listHead">
                    <div class="listHiddenField pull-left field60"><s:message code="board.no"/></div>
                    <div class="listHiddenField pull-right field100"><s:message code="crud.regdate"/></div>
                    <div class="listHiddenField pull-right field100"><s:message code="crud.usernm"/></div>
                    <div class="listTitle"><s:message code="crud.crtitle"/></div>
                </div>

                <c:if test="${listview.size()==0}">
                    <div class="listBody height200">
                    </div>
                </c:if>

                <c:forEach var="listview" items="${listview}" varStatus="status">
                    <c:url var="link" value="crudRead">
                        <c:param name="crno" value="${listview.crno}"/>
                    </c:url>

                    <div class="listBody">
                        <div class="listHiddenField pull-left field60 textCenter"><c:out value="${searchVO.totRow-((searchVO.page-1)*searchVO.displayRowCount + status.index)}"/></div>
                        <div class="listHiddenField pull-right field100 textCenter"><c:out value="${listview.regdate}"/></div>
                        <div class="listHiddenField pull-right field100 textCenter"><c:out value="${listview.usernm}"/></div>
                        <div class="listTitle" title="<c:out value="${listview.crtitle}"/>">
                            <a href="${link}"><c:out value="${listview.crtitle}"/></a>
                        </div>
                    </div>
                </c:forEach>

                <br/>
                <form role="form" id="form1" name="form1" method="post">
                    <jsp:include page="../common/pagingforSubmit.jsp"/>

                    <div class="form-group">
                        <div class="checkbox col-lg-3 pull-left">
                            <label class="pull-right">
                                <input type="checkbox" name="searchType" value="crtitle" <c:if test="${fn:indexOf(searchVO.searchType, 'crtitle')!=-1}">checked="checked"</c:if>/>
                                <s:message code="crud.crtitle"/>
                            </label>
                            <label class="pull-right">
                                <input type="checkbox" name="searchType" value="crmemo" <c:if test="${fn:indexOf(searchVO.searchType, 'crmemo')!=-1}">checked="checked"</c:if>/>
                                <s:message code="crud.crmemo"/>
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