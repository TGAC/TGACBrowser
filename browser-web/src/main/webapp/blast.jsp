<%@ include file="header.jsp" %>

<%--<h1 id="seqnameh1">Blast Search </h1>--%>

<div style="position: absolute; left: 20px; width: 50%; top:50px;" id="blasttextsearch">

    Enter sequence below in FASTA or RAW format (Only one sequence at a time.)
    <br>
    <textarea class="ui-corner-all" id="blastsearch" rows="6" cols="60"
              style="margin: 2px 2px 2px 2px; height: 100px; width: 98%; "></textarea>


    <table width="100%">
        <tbody>
        <tr>
            <td>
                <b> Blast DB </b>
            </td>
            <td>

                <div id="blastdbs" style="position: relative; top:0px; display: none">
                </div>
                <div id="ncbiblastdbs" style="display: none">
                    <select name="blastdb" id="ncbiblastdb">
                        <option value="nr">nr</option>
                        <option value="est_human">est_human</option>
                        <option value="est_mouse">nr</option>
                        <option value="est_others">est_others</option>
                        <option value="htg">htg</option>
                        <option value="gss">gss</option>
                        <option value="pataa">pataa</option>
                        <option value="patnt">patnt</option>
                    </select>
                </div>
                <div id="grassrootblastdbs" style="display: none">
                    <select name="blastdb" id="grassrootblastdb">
                        <option value="ref">Reference</option>
                    </select>
                </div>


            </td>

        </tr>
        <tr>
            <td>
                <b> Type </b>
            </td>
            <td>
                <select name="blast_type" id="blast_type" onchange="setBLASTParams()">
                    <option value="blastn" title="Traditional BLASTN requiring an exact match of 11"> blastn</option>
                    <option value="blastn-short" title="BLASTN program optimized for sequences shorter than 50 bases">blastn short</option>
                    <option value="megablast" title="Traditional megablast used to find very similar (e.g., intraspecies or closely related species) sequences">megablast</option>
                    <option value="dc-megablast" title="Discontiguous megablast used to find more distant (e.g., interspecies) sequences">dc-megablast</option>
                </select>

            </td>

        </tr>

                <tr>
                    <td colspan="3"><b> Advanced Parameters: </b>
                    </td>
                </tr>
<%--                <tr>--%>
<%--                    <td> Repeats</td>--%>
<%--                    <td>--%>
<%--                        <input type="checkbox" id="filter" name="filter" checked=""> Include Repeats--%>
<%--                    </td>--%>
<%--                </tr>--%>
                <tr>
                    <td>
                    </td>
                    <td>


                    </td>
                </tr>
                <tr valign=top>
                    <td><b> Scoring Parameter </b></td>
                    <td>
                        <div id=blastn_para>
                            <table>
                                <tbody>
                                <tr>
                                    <td> Match/Mismatch</td>
                                    <td><select name="match-mismatch" id="match-mismatch">
                                        <option value="1,-2">1, -2</option>
                                        <option value="1,-3">1, -3</option>
                                        <option value="1,-4">1, -4</option>
                                        <option value="2,-3">2, -3</option>
                                        <option value="4,-5">4, -5</option>
                                        <option value="1,-1">1, -1</option>
                                    </select></td>
                                </tr>
<%--                                <tr>--%>
<%--                                    <td> Gap Costs</td>--%>
<%--                                    <td>--%>
<%--                                        <select name="gap_cost" id="gap_cost">--%>
<%--                                            <option value="5,2">Existence: 5 Extension: 2</option>--%>
<%--                                            <option value="2,2">Existence: 2 Extension: 2</option>--%>
<%--                                            <option value="1,2">Existence: 1 Extension: 2</option>--%>
<%--                                            <option value="0,2">Existence: 0 Extension: 2</option>--%>
<%--                                            <option value="3,1">Existence: 3 Extension: 1</option>--%>
<%--                                            <option value="2,1">Existence: 2 Extension: 1</option>--%>
<%--                                            <option value="1,1">Existence: 1 Extension: 1</option>--%>
<%--                                        </select>--%>
<%--                                    </td>--%>
<%--                                </tr>--%>
                                </tbody>
                            </table>
                        </div>
                        <br>

<%--                        <div id="blastp_para" style="display: none;">--%>
<%--                            <b>Matrix</b>--%>
<%--                            <select name="matrix" id="matrix" onchange="setBLASTPenalty()">--%>
<%--                                <option value="PAM30">PAM-30</option>--%>
<%--                                <option value="PAM70">PAM-70</option>--%>
<%--                                <option value="PAM250">PAM-250</option>--%>
<%--                                <option value="BLOSUM80" selected>BLOSUM-80</option>--%>
<%--                                <option value="BLOSUM62">BLOSUM-62</option>--%>
<%--                                <option value="BLOSUM45">BLOSUM-45</option>--%>
<%--                                <option value="BLOSUM50">BLOSUM-50</option>--%>
<%--                                <option value="BLOSUM90">BLOSUM-90</option>--%>
<%--                            </select>--%>
<%--                            <br>--%>
<%--                            <b>Gap Costs:</b>--%>

<%--                            <div id="penalty_div">--%>

<%--                            </div>--%>
<%--                        </div>--%>
                    </td>
                </tr>
                <tr>
                    <td>
                        Word Size:
                    </td>
                    <td>
                        <div id="word_size_div">

                        </div>
                    </td>
                </tr>
<%--                <tr>--%>
<%--                    <td>--%>
<%--                        Short Queries:--%>
<%--                    </td>--%>
<%--                    <td>--%>
<%--                        <input type="checkbox" id="short_seq" onchange="toogleParams()"> Automatically adjust parameters for--%>
<%--                        short input sequences.--%>
<%--                    </td>--%>
<%--                </tr>--%>
        <tr>
            <td>

            </td>
            <td>

            </td>
            <td>
                <button class="btn btn-primary" onclick="blastFilter()">
                    BLAST
                </button>
                <button class="btn btn-default" onclick="resetBLAST()">
                    Clear
                </button>
            </td>
        </tr>
        </tbody>
    </table>
</div>

<div id="seqresult">

    <span id="ruler"></span>
    <center>
        <div id="main" style="top : 10px ; height: 0px; "></div>
    </center>

</div>

<div id="blastresult" style="display: none">
</div>

<div id="blast_list_div" class="panel panel-primary">
    <div style="position: relative;  font-size: 14px;" class="panel-heading">
        <center>
            <b>BLAST History</b> <span id="blast_no" class="badge "></span>
        </center>
    </div>
    <div id="blast_list" style="overflow: auto; height: 160px;">
    </div>
</div>

<div class="modal fade tgacbrowsermodal" id="alnModal" role="dialog">
    <div class="modal-dialog">

        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">BLAST Alignment</h4>
            </div>
            <div class="modal-body">

                <div id="blast_aln_content"
                     style='display : inline;  font-family: Courier, \"Courier New\", monospace'></div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>

    </div>
</div>

<script type="text/javascript">
    var seq;

    function blastFilter() {


        var dbs = jQuery('#blastdb').val()
        var count = jQuery("#blastsearch").val().match(/>/g);
        var params = {};

        // if(count != null && count.length > 0){
        //     var no_of_seq = parseInt(100/count.length)
        //     params += "-num_alignments "+no_of_seq+" ";
        // }
        //
        var type = jQuery('#blast_type').val();
        //
        // if (jQuery("#short_seq").prop('checked')) {
        //     if (jQuery('#blast_type').val().indexOf('tblastn') >= 0 || jQuery('#blast_type').val().indexOf('blastx') >= 0) {
        //         params += " -seg no -word_size 3";
        //     }
        //     else {
        //         params += " -dust no -word_size 7";
        //     }
        // } else {
        //     if (jQuery("#filter").prop('checked')) {
        //         if (jQuery('#blast_type').val().indexOf('tblastn') >= 0 || jQuery('#blast_type').val().indexOf('blastx') >= 0) {
        //             params += " -seg no";
        //         }
        //         else {
        //             params += " -dust no";
        //         }
        //     }
        //
        //     if (jQuery('#blast_type').val().indexOf('blastn') == 0) {
        //
                var match_mismatch = jQuery("#match-mismatch").val();
        //         var gap = jQuery("#gap_cost").val();
                params['reward'] = parseInt(match_mismatch.split(",")[0])
                params['penalty'] = parseInt(match_mismatch.split(",")[1])
                    // + " -gapopen " + gap.split(",")[0] + " -gapextend " + gap.split(",")[1];
        //     }
        //     else {
        //         var matrix = jQuery("#matrix").val();
        //         var gap = jQuery("#penalty").val();
        //
        //         params += " -matrix " + matrix + " -gapopen " + gap.split(",")[0] + " -gapextend " + gap.split(",")[1];
        //     }
        //
            params['word_size'] = jQuery("#word_size").val();
        // }

        blastSearch(jQuery('#blastsearch').val(), dbs, type, params);
    }


    jQuery(document).ready(function () {
        getUrlVars();
        setBlast();
        setBLASTParams();
        setBLASTDB();
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