<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="../inc/header.jsp" %>
    <style>
        .title {
            color: blue;
            font-size: 12pt;
        }

        .row {
            margin-top: 5px
        }

        .field1 {
            margin-left: 10px
        }

        em {
            color: red;
        }

        #page_div div {
            width: 30px;
            height: 30px;
            margin: 5px;
            /* border: 1px solid; */
            font-size: 20px;
            cursor: pointer;
        }
    </style>
    <script>
        // HTML 엔티티를 디코딩하는 함수
        function decodeHtmlEntities(text) {
            var textArea = document.createElement('textarea');
            textArea.innerHTML = text;
            return textArea.value;
        }

        window.onload = function () {
            $('#searchTerm1').datepicker().on('changeDate', function (ev) {
                if (ev.viewMode == "days") {
                    $('#searchTerm1').datepicker('hide');
                }
            });
            $('#searchTerm2').datepicker().on('changeDate', function (ev) {
                if (ev.viewMode == "days") {
                    $('#searchTerm2').datepicker('hide');
                }
            });
        }
        var searchKeyword = "", currentPage = 1, searchType = "";

        function fn_fullTextSearch(page) {
            var searchRange = "";

            searchType = "";
            searchKeyword = $("#searchKeyword").val();
            if(searchKeyword.length < 1) {
                alert("검색어를 입력하세요.")
                return;
            }
            $("#searchRange:checked").each(function () {
                searchRange += $(this).val() + ",";
            });
            if (searchRange.length < 1) {
                alert("검색 범위를 지정하세요.");
                return;
            }

            currentPage = page;
            $.ajax({
                url: "search4Ajax",
                cache: false,
                data: {
                    searchKeyword: searchKeyword,
                    page: page,
                    searchType: searchType,
                    searchRange: searchRange.substr(0, searchRange.length - 1),
                    searchTerm: $("#searchTerm:checked").val(),
                    searchTerm1: $("#searchTerm1").val(),
                    searchTerm2: $("#searchTerm2").val()
                }
            }).done(receiveData);
        }

        function fn_formSubmit() {

            fn_fullTextSearch(1);
        }

        function fn_page(page) {
            fn_fullTextSearch(page);
        }

        function receiveData(data) {
            if(!data) {
                return;
            }

            var hits = data.hits.hits;
            var $list_div = $("#list_div");
            $list_div.empty();

            hits.forEach(function (row) {
                var brdwriter = row.highlight && row.highlight.brdwriter ? row.highlight.brdwriter : row._source.brdwriter;
                var brdtitle = row.highlight && row.highlight.brdtitle ? row.highlight.brdtitle : row._source.brdtitle;
                var brdmemo = row.highlight && row.highlight.brdmemo ? row.highlight.brdmemo[0] : row._source.brdmemo;

                var childNode = $('<div  class="panel panel-default"/>');
                // HTML 엔티티 디코딩
                var decodedTitle = decodeHtmlEntities(brdtitle);
                var decodedMemo = decodeHtmlEntities(brdmemo);
                var decodedWriter = decodeHtmlEntities(brdwriter);

                var html = '<div class="panel-body">' +
                    '        <div class="col-lg-12 title"><a href="boardRead?brdno=' + row._source.brdno + '" target="_blank">' + decodedTitle + '</a></div>' +
                    '        <div class="col-lg-12 row">...' + decodedMemo.substring(0, 200) + '...</div>' +
                    '        <div class="col-lg-12 row">' +
                    '            <div class="pull-left">등록자: ' + decodedWriter + '</div>' +
                    '             <div class="pull-left field1">등록일: ' + row._source.regdate + ' ' + row._source.regtime + '</div>' +
                    '             <div class="pull-left field1">조회수: ' + row._source.brdhit + '</div>' +
                    '        </div>' +
                    '</div>';
                childNode.html(html)
                $list_div.append(childNode);
            });
            /////////////////////////////////
            if (searchType === "") {        // 그룹 선택시 초기화 되는 문제로 임시처리
                "2,3".split(",").forEach(function (row) {
                    $("#cnt" + row).html(0);
                });

                var agg = data.aggregations;
                if (agg && Object.keys(agg).length > 0) {
                    var buckets = agg[Object.keys(agg)[0]].buckets;     // "lterms#gujc"로 "lterms#"가 자동으로 생겨서 사용한 편넙;
                    console.log(buckets);

                    if (buckets) {
                        buckets.forEach(function (row) {
                            $("#cnt" + row.key).html(row.doc_count);
                        });
                    }
                }

                $("#cnt").html(data.hits.total.value);
            }

            /////////////////////////////////
            var $page_div = $("#page_div");
            $page_div.empty();

            var totalPages = Math.ceil(data.hits.total.value / 5);
            if (totalPages === 1) return;

            for (var i = 1; i <= totalPages; i++) {
                var $node = $('<div class="pull-left"/>');
                $node.click(function () {
                    fn_page($(this).text());
                });

                $node.html(i);
                $page_div.append($node);
            }
        }

        function fn_fullTextSearchType(type) {
            searchType = type;
            fn_fullTextSearch(0);
        }

        function ev_checkAllField() {
            var chk = $("#searchRangeAll")[0].checked;
            $("input[id=searchRange]:checkbox").each(function () {
                $(this).prop("checked", chk);
            });
        }
    </script>

</head>

<body>

<div id="wrapper">
    <jsp:include page="../common/navigation.jsp"/>

    <div id="page-wrapper">
        <div class="row">
            <div class="col-lg-12">
                <h1 class="page-header"><i class="fa fa-gear fa-fw"></i> Elasticsearch 테스트</h1>
            </div>
            <!-- /.col-lg-12 -->
        </div>

        <div class="form-group">
            <div class="col-lg-1 pull-left"></div>
            <div class="input-group custom-search-form col-lg-3">
                <input class="form-control" placeholder="Search..." type="text" id="searchKeyword" name="searchKeyword"
                       onkeydown="if(event.keyCode == 13) { fn_formSubmit();}">
                <span class="input-group-btn">
                              <button class="btn btn-default" onclick="fn_formSubmit()">
                                  <i class="fa fa-search"></i>
                              </button>
                        </span>
            </div>
        </div>
        <div class="row">
            <div class="col-lg-1"><s:message code="search.scope"/></div>
            <div class="col-lg-1"><label><input type="checkbox" id="searchTerm" value="a" checked><s:message
                    code="common.all"/></label></div>
            <div class="col-lg-2">
                <input class="form-control" size="16" id="searchTerm1" type="text" value="<c:out value="${today}"/>"
                       readonly>
            </div>
            <div class="col-lg-2">
                <input class="form-control" size="16" id="searchTerm2" type="text" value="<c:out value="${today}"/>"
                       readonly>
            </div>
        </div>
        <div class="row">
            <div class="col-lg-1"><s:message code="search.period"/></div>
            <div class="col-lg-1">
                <label><input type="checkbox" id="searchRangeAll" value="" checked
                              onchange="ev_checkAllField()">전체</label>
            </div>
            <div class="col-lg-10">
                <label><input type="checkbox" id="searchRange" value="brdwriter" checked><s:message
                        code="board.writer"/></label>
                <label><input type="checkbox" id="searchRange" value="brdtitle" checked><s:message
                        code="board.title"/></label>
                <label><input type="checkbox" id="searchRange" value="brdmemo" checked><s:message
                        code="board.contents"/></label>
                <label><input type="checkbox" id="searchRange" value="brdreply" checked><s:message
                        code="common.btnReply"/></label>
                <label><input type="checkbox" id="searchRange" value="brdfiles" checked><s:message
                        code="board.attach"/></label>
            </div>
        </div>

        <div class="row">
            <div class="panel panel-default">
                <div class="panel-body">
                    <div class="pull-left" onclick="fn_fullTextSearchType('')" style="cursor: pointer;"><s:message
                            code="search.total"/> <span
                            id="cnt" style="color:red">0</span></div>
                    <div class="pull-left" onclick="fn_fullTextSearchType('3')"
                         style="cursor: pointer;margin-left: 30px">일반 게시판 <span id="cnt3" style="color:red">0</span>
                    </div>
                    <div class="pull-left" onclick="fn_fullTextSearchType('2')"
                         style="cursor: pointer;margin-left: 30px">공지사항 <span id="cnt2" style="color:red">0</span></div>
                </div>
            </div>
        </div>

        <div id="list_div"></div>
        <div id="page_div" class="col-lg-12">
        </div>
    </div>
    <!-- /#page-wrapper -->

</div>
<!-- /#wrapper -->
</body>

<%@include file="../inc/footer.jsp" %>