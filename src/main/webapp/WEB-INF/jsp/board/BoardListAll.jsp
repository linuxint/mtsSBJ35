<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="../inc/header.jsp" %>
    <script>
        function fn_formSubmit() {
            document.form1.submit();
        }

        function showBoardList(ev) {
            if ($('#boardlistDiv').is(':visible')) {
                $("#boardlistDiv").hide();
                return;
            }
            var pos = $("#boardlistBtn").position();
            $("#boardlistDiv").css({
                "top": parseInt(pos.top) + 30 + "px",
                "left": pos.left
            }).show();

            var node = $("#tree").dynatree("getRoot");

            if (node.childList) return;

            $.ajax({
                url: "boardListByAjax",
                type: "post",
                dataType: "json",
                success: function (result) {
                    $("#tree").dynatree({children: result});
                    $("#tree").dynatree("getTree").reload();
                    $("#tree").dynatree("getRoot").visit(function (node) {
                        node.expand(true);
                    });
                }
            })

        }

        $(function () {
            $("#tree").dynatree({
                onActivate: TreenodeActivate
            });
        });

        function TreenodeActivate(node) {
            let isfolder = node.data.isfolder||false;
            if(isfolder) {
                location.href = "boardList";
            } else {
                location.href = "boardList?bgno=" + node.data.key;
            }
        }

    </script>

</head>

<body>

<div id="wrapper">

    <jsp:include page="../common/navigation.jsp"/>

    <div id="page-wrapper">
        <div class="row">
            <div class="col-lg-12">
                <h1 class="page-header"><i class="fa fa-files-o fa-fw"></i> <s:message code="board.boardName"/></h1>
            </div>
            <!-- /.col-lg-12 -->
        </div>

        <!-- /.row -->
        <div class="row">
            <div class="col-lg-12">
                <button id="boardlistBtn" type="button" class="btn btn-default" onclick="showBoardList()"><i
                        class="fa  fa-files-o fa-fw"></i> <s:message code="common.all"/></button>
                <div id="boardlistDiv" style="width: 250px; height:300px; display: none;" class="popover fade bottom in"
                     role="tooltip">
                    <div style="left:15%;" class="arrow"></div>
                    <div class="popover-content">
                        <div id="tree"></div>
                    </div>
                </div>
            </div>
        </div>
        <!-- /.row -->
        <div class="panel panel-default">
            <div class="panel-body">
                <div class="listHead">
                    <div class="listHiddenField pull-left field60"><s:message code="board.no"/></div>
                    <div class="listHiddenField pull-right field100"><s:message code="board.locate"/></div>
                    <div class="listHiddenField pull-right field60"><s:message code="board.attach"/></div>
                    <div class="listHiddenField pull-right field60"><s:message code="board.hitCount"/></div>
                    <div class="listHiddenField pull-right field100"><s:message code="board.date"/></div>
                    <div class="listHiddenField pull-right field100"><s:message code="board.writer"/></div>
                    <div class="listTitle"><s:message code="board.title"/></div>
                </div>
                <c:forEach var="listview" items="${noticelist}" varStatus="status">
                    <c:set var="listitem" value="${listview}" scope="request"/>
                    <c:set var="listitemNo" value=""/>
                    <jsp:include page="BoardListAllSub.jsp">
                        <jsp:param name="listitemNo" value="${listitemNo}"/>
                        <jsp:param name="listitem" value="${listitem}"/>
                    </jsp:include>
                </c:forEach>
                <c:if test="${listview.size()==0}">
                    <div class="listBody height200">
                    </div>
                </c:if>
                <c:forEach var="listview" items="${listview}" varStatus="status">
                    <c:set var="listitem" value="${listview}" scope="request"/>
                    <c:set var="listitemNo"
                           value="${searchVO.totRow-((searchVO.page-1)*searchVO.displayRowCount + status.index)}"
                           scope="request"/>
                    <jsp:include page="BoardListAllSub.jsp">
                        <jsp:param name="listitemNo" value="${listitemNo}"/>
                        <jsp:param name="listitem" value="${listitem}"/>
                    </jsp:include>
                </c:forEach>
                <br/>
                <form role="form" id="form1" name="form1" method="post">
                    <jsp:include page="../common/pagingforSubmit.jsp"/>

                    <div class="form-group">
                        <div class="checkbox col-lg-3 pull-left">
                            <label class="pull-right">
                                <input type="checkbox" name="searchType" value="brdmemo"
                                       <c:if test="${fn:indexOf(searchVO.searchType, 'brdmemo')!=-1}">checked="checked"</c:if>/>
                                <s:message code="board.contents"/>
                            </label>
                            <label class="pull-right">
                                <input type="checkbox" name="searchType" value="brdtitle"
                                       <c:if test="${fn:indexOf(searchVO.searchType, 'brdtitle')!=-1}">checked="checked"</c:if>/>
                                <s:message code="board.title"/>
                            </label>
                            <label class="pull-right">
                                <input type="checkbox" name="searchType" value="usernm"
                                       <c:if test="${fn:indexOf(searchVO.searchType, 'usernm')!=-1}">checked="checked"</c:if>/>
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