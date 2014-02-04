<%@ include file="header.jsp" %>

<%--<h1 id="seqnameh1">Blast Search </h1>--%>

<div style="position: absolute; top: 50px;left: 20px;" id="blasttextsearch">

    Enter sequence below in FASTA or RAW format
    <br>
    <textarea class="ui-corner-all" id="blastsearch" rows="6" cols="60"
              style="margin: 2px 2px 2px 2px; height: 100px; width: 98%; "></textarea>

    </select> Blast DB
    <div id="blastdbs" position: style="position: absolute; display: none">
        <select name="blastdb" id="blastdb">

            <c:set var="databases">${initParam.blastdblink} </c:set>

            <c:set var="dateParts" value="${fn:split(databases, ',')}"/>

            <c:set var="databasesloc">${initParam.blastdblocation} </c:set>

            <c:set var="datePartsloc" value="${fn:split(databasesloc, ',')}"/>


            <c:forEach var="i" begin="1" end='${fn:length(dateParts)}' step="1">
                <%--splitting by /--%>
                <c:set var="text" value="${fn:split(datePartsloc[i-1],'/')}"/>
                <%--considering last entry--%>
                <c:set var="text" value="${text[fn:length(text)-1]}"/>
                <%--index of . --%>
                <c:set var="to" value="${fn:indexOf(text,'.' )}"/>
                <%--substring to . --%>
                <c:set var="filename" value="${fn:substring(text,0,to) }"/>

                <option id=${dateParts[i-1]} value=
                "${datePartsloc[i-1]}:${dateParts[i-1]}">${filename}</option>
            </c:forEach>


        </select>
        Type
        <select name="blast_type" id="blast_type">
            <option value="blastn"> blastn</option>
            <option value="tblastn">tblastn</option>
            <option value="blastx">blastx</option>
        </select>

        <input type=checkbox id='filter' name='filter' checked> Include Repeats
        <button class="ui-state-default ui-corner-all"
                onclick=blastFilter()>
            BLAST
        </button>

        <button class="ui-state-default ui-corner-all"
                onclick="resetBLAST()">
            Clear
        </button>

    </div>


    <br>

    <div id="ncbiblastdbs" style="position: absolute; display: none">
        NCBI BLAST
        <select name="blastdb" id="ncbiblastdb">
            <option value=nr>nr</option>
            <option value=est_human>est_human</option>
            <option value=est_mouse>nr</option>
            <option value=est_others>est_others</option>
            <option value=htg>htg</option>
            <option value=gss>gss</option>
            <option value=pataa>pataa</option>
            <option value=patnt>patnt</option>
        </select>

        Type
        <select name="blast_type" id="ncbi_blast_type">
            <option value="blastn"> blastn</option>
            <option value="tblastn">tblastn</option>

        </select>
        <button class="ui-state-default ui-corner-all"
                onclick="blastSearch(jQuery('#blastsearch').val(), jQuery('#ncbiblastdb').val(),jQuery('#ncbi_blast_type').val());">
            NCBI BLAST
        </button>

        <button class="ui-state-default ui-corner-all"
                onclick="blastTrackSearch(jQuery('#blastsearch').val(), 0, 100, 10, jQuery('#ncbiblastdb').val(),jQuery('#ncbi_blast_type').val());">
            NCBI BLAST Track
            test
        </button>
    </div>


</div>
<div id="seqresult">

    <span id="ruler"></span>
    <center>
        <div id="main" style="top : 10px ; height: 0px; ">
    </center>

</div>

<div id="blastresult" style="display: none">
</div>

<div id="blast_list">
    <div style="position: relative; background: none repeat scroll 0% 0% gray; font-size: 14px; margin-left: -5px; margin-right: -5px; margin-top: -5px;">
    <center>
    <b>BLAST History</b>
    </center></div>
</div>

<script type="text/javascript">
    var seq;
    function blastFilter(){

        var dbs = jQuery('#blastdb').val()

        var type = jQuery('#blast_type').val();
                var params = "-num_threads  4 ";
                if(jQuery("#filter").attr('checked'))
                {
                    if(jQuery('#blast_type').val().indexOf('tblastn') >= 0 || jQuery('#blast_type').val().indexOf('blastx') >= 0){
                        params += " -seg no";
                    }
                    else {
                        params += " -dust no";
                    }
                }
                blastSearch(jQuery('#blastsearch').val(),dbs,type,params);
    }
    jQuery(document).ready(function () {
        getUrlVars();
        setBlast();
        var testTextBox = jQuery('#search');
        var code = null;
        testTextBox.keypress(function (e) {
            code = (e.keyCode ? e.keyCode : e.which);
            if (code == 13) {
                search(jQuery('#search').val());
            }
        });

        <%--jQuery(".blasttab").html('<a href="<c:url value="index.jsp"/>"><span>Browser</span></a>');--%>
        <%--jQuery("#seqnameh1").html('<a href="<c:url value="/blast.jsp"/>"> Blast Search</a>');--%>
    });

    function getUrlVars() {
        var parts = window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi, function (m, key, value) {
            jQuery("#blastsearch").val(value);
            blastSearch(value);
        });

    }


</script>
<%@ include file="footer.jsp" %>