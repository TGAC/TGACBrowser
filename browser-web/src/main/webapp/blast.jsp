<%@ include file="header.jsp" %>

<%--<h1 id="seqnameh1">Blast Search </h1>--%>

<div id="blasttextsearch">
    Enter sequence below in FASTA or RAW format
    <br>
    <textarea class="ui-corner-all" id="blastsearch" rows="6" cols="60"
              style="margin: 2px 2px 2px 2px; height: 100px; width: 98%; "></textarea>

    </select> Blast DB
    <select name="blastdb" id="blastdb">

        <c:set var="databases">${initParam.blastdblink} </c:set>

        <c:set var="dateParts" value="${fn:split(databases, ',')}"/>

        <c:set var="databasesloc">${initParam.blastdblocation} </c:set>

        <c:set var="datePartsloc" value="${fn:split(databasesloc, ',')}"/>


        <c:forEach var="i" begin="1" end='${fn:length(dateParts)}' step="1">
           <%--splitting by /--%>
                       <c:set var="text" value="${fn:split(datePartsloc[i-1],'/')}" />
                       <%--considering last entry--%>
                       <c:set var="text" value="${text[fn:length(text)-1]}"/>
                       <%--index of . --%>
                       <c:set var="to" value="${fn:indexOf(text,'.' )}"/>
                       <%--substring to . --%>
                       <c:set var="filename" value="${fn:substring(text,0,to) }" />

                       <option id=${dateParts[i-1]} value="${datePartsloc[i-1]}:${dateParts[i-1]}">${filename}</option>
        </c:forEach>


    </select>
    <button class="ui-state-default ui-corner-all"
            onclick="blastSearch(jQuery('#blastsearch').val(), jQuery('#blastdb').val());">BLAST
    </button>
</div>
<div id="seqresult">

    <span id="ruler"></span>
    <center>
        <div id="main" style="top : 10px ; height: 0px; ">
    </center>

</div>

<div id="blastresult"></div>

<script type="text/javascript">
    var seq;
    jQuery(document).ready(function() {
        getUrlVars();
        jQuery(".blasttab").html('<a href="<c:url value="index.jsp"/>"><span>Browser</span></a>');
        jQuery("#seqnameh1").html('<a href="<c:url value="/blast.jsp"/>"> Blast Search</a>');
    });

    function getUrlVars() {
        var parts = window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi, function(m, key, value) {
            jQuery("#blastsearch").val(value);
            blastSearch(value);
        });

    }


</script>
<%@ include file="footer.jsp" %>