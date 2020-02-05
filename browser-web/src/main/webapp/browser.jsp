<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>
<%@ page import="java.util.ArrayList" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <script>
        jQuery(document).ready(function () {
            metaData();
            jQuery('input.deletable').wrap('<span class="deleteicon" />').after(jQuery('<span/>').click(function () {
                jQuery(this).prev('input').val('').focus();
                jQuery('#searchText').val('Search DNA');
                jQuery('#searchDiv').html('');
            }));

            initUpload();
        });
    </script>
</head>

<body>
<div id="main1" style="top : 10px ; height: 1050px; ">

    <div id="seqname"></div>
    <div id="currentposition"></div>
    <div id="guideline" style="display: none"></div>
    <div id="seqdrag"><p id="dragLabel"></div>
    <div id="searchresultMap" style="display: none">
    </div>
    <div id="searchResultLegend"></div>
    <div id='map'>
        <div id="refmap"></div>
        <div id="mapmarker">
        </div>
        <div id="unmapped" style="bottom: 0; display: block; max-height: 500px; position: fixed; right: 0;"></div>

    </div>
    <div id="searchresult">
        <div id="searchresultHead"></div>
        <div id="searchnavtabs">
            <ul>
                <li><a href="#tabGenes"><span>Genes</span></a></li>
                <li><a href="#tabTranscripts"><span>Transcripts</span></a></li>
                <li><a href="#tabGO"><span>GO</span></a></li>
            </ul>
            <div id="tabGenes">
            </div>
            <div id="tabGO">
            </div>
            <div id="tabTranscripts">
            </div>
        </div>
    </div>
    <div id="canvas" style="display: none">

        <div id="controller">
            <table width=100%>
                <tr>
                    <td>

                        <div id="coord" align="left">
                            <table>
                                <tr>
                                    <td>
                                        <input type="text" id="begin_scale" size="5" class="jump"> <span
                                            class="unit"></span>
                                    </td>
                                    <td>
                                        <input type="text" id="end_scale" size="5" class="jump"> <span
                                            class="unit"></span>
                                    </td>
                                    <td>
                                        <div onclick="jumpToSeqFromGo()" class="divbutton"> Go</div>
                                    </td>
                                </tr>
                                <tr>
                                    <td>
                                        <input type="text" id="begin" size="5" class="jump" style="visibility: hidden">
                                    </td>
                                    <td>
                                        <input type="text" id="end" size="5" class="jump" style="visibility: hidden">
                                    </td>
                                    <td>

                                    </td>
                                </tr>
                            </table>

                            <div id="notifier" class="rightAlertdiv"></div>
                        </div>
                    </td>
                    <td>
                        <div style="width: 100%; margin: 0px auto; position: fixed; left: 0px;">
                            <center>
                                <img src='images/browser/reset.png' onclick="reset();" class="browserimage" height=50%
                                     alt="Reset" title="Reset"> &nbsp;&nbsp;
                                <img src='images/browser/backward.png' onclick="dragLeft();" id="leftbig"
                                     class="browserimage"
                                     height=70%
                                     alt="Backward" title="Move Left">
                                <img src='images/browser/backward.png' onclick="seqLeft();"
                                     class="browserimage browserimagesmall"
                                     height=40% alt="Left" title="Move Left (1bp)">&nbsp;
                                <img src='images/browser/forward.png' onclick="seqRight();"
                                     class="browserimage browserimagesmall"
                                     height=40% alt="Right" title="Move Right (1bp)">
                                <img src='images/browser/forward.png' onclick="dragRight();" id="rightbig"
                                     class="browserimage"
                                     height=70% alt="Forward" title="Move Right">&nbsp;&nbsp;
                                <img src='images/browser/zoomin.png' id="zoominbig"
                                     onclick="zoomIn(parseInt(getEnd()-getBegin())/10);"
                                     class="browserimage"
                                     height=70% alt="ZoomIn" title="Zoom In">
                                <img src='images/browser/zoomin.png' id="zoominsmall"
                                     onclick="zoomIn(parseInt(getEnd()-getBegin())/20);"
                                     class="browserimage browserimagesmall"
                                     height=40% alt="Zoomin" title="Zoom In">&nbsp;
                                <img src='images/browser/zoomout.png' id="zoomoutsmall"
                                     onclick="zoomOut(parseInt(getEnd()-getBegin())/20);"
                                     class="browserimage browserimagesmall"
                                     height=40% alt="Zoomout" title='Zoom Out'>
                                <img src='images/browser/zoomout.png' id="zoomoutbig"
                                     onclick="zoomOut(parseInt(getEnd()-getBegin())/10);"
                                     class="browserimage"
                                     height=70% alt="Zoomout" title="Zoom Out">&nbsp;&nbsp;
                                <img src='images/browser/selectall.png' onclick="expand();" class="browserimage"
                                     height=60% alt="selectall" title="Select All">
                            </center>

                        </div>
                    </td>
                    <td>
                        <table style="position: fixed; right: 0px; top: 25px;">
                            <tr>
                                <td>
                                    <button id=export type="button"
                                            class="btn btn-default btn-sm ui-btn ui-shadow ui-corner-all"
                                            style="display: none;">
                                    </button>
                                </td>
                                <td>

                                    <button type="button" class="btn btn-default btn-sm ui-btn ui-shadow ui-corner-all"
                                            data-toggle="modal" data-target="#uploadModal">
                                        <i class="fa fa-upload"></i>
                                    </button>
                                </td>
                                <td>
                                    <button type="button" class="btn btn-default btn-sm ui-btn ui-shadow ui-corner-all"
                                            onclick="checkSession();">
                                        <i class="fa">Save Session</i>
                                    </button>
                                </td>

                                <td>
                                    <button type="button" class="btn btn-default btn-sm ui-btn ui-shadow ui-corner-all"
                                            data-toggle="modal" data-target="#controlModal">
                                        <i class="fa">Tracks / Settings</i>
                                    </button>
                                </td>
                            </tr>
                        </table>
                        <div id=sessionid></div>

                    </td>
                </tr>
            </table>


        </div>


        <div id="bar_image">
            <div id="searchDiv"></div>
            <div id="SeqLenStart">&nbsp;</div>
            <div id="SeqLen25">&nbsp;</div>
            <div id="SeqLenMid">&nbsp;</div>
            <div id="SeqLen75">&nbsp;</div>
            <div id="SeqLenEnd">&nbsp;</div>
        </div>
        <div id="nav_panel"> &nbsp;</div>
        <div id="vertical" style="left: 0%; width: 100%; background-color: white; height: 10px; overflow: visible;" class="vertical-line">
            <div id="vertical0" style="left: 0%" class="vertical-line"></div>
            <div id="vertical1" style="left: 10%" class="vertical-line"></div>
            <div id="vertical2" style="left: 20%" class="vertical-line"></div>
            <div id="vertical3" style="left: 30%" class="vertical-line"></div>
            <div id="vertical4" style="left: 40%" class="vertical-line"></div>
            <div id="vertical5" style="left: 50%" class="vertical-line"></div>
            <div id="vertical6" style="left: 60%" class="vertical-line"></div>
            <div id="vertical7" style="left: 70%" class="vertical-line"></div>
            <div id="vertical8" style="left: 80%" class="vertical-line"></div>
            <div id="vertical9" style="left: 90%" class="vertical-line"></div>
            <div id="vertical10" style="right: 0%; margin-left: -20px; border-left: 0" class="vertical-line"></div>
        </div>
        <div id="bg_layer"></div>
        <div id="draggable" onmouseup="auto_drag()" onmouseout="auto_drag()"></div>


        <div id="wrapper">
            <%--<canvas id="myCanvas" width="200" height="100"--%>
            <%--style="border:1px solid #000000;">--%>
            <%--</canvas>--%>
            <div id=tracks>
            </div>
            <div class="fakediv">

            </div>
            <div id="sequence">

                <center>
                    <div id="marker_div"
                         style="position: absolute; top: 0px; left: 0px; height: 20px; width: 100%;"></div>
                    <div id="sequenceString"></div>
                    <div id="translation_div"></div>
                </center>


            </div>
            <span id="ruler"></span>

        </div>
    </div>

</div>

<%--<div id="openCloseIdentifier"></div>--%>

<div class="modal fade tgacbrowsermodal" id="controlModal" role="dialog">
    <div class="modal-dialog">

        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Control Panel</h4>
            </div>
            <div class="modal-body">
                <div id="controlpanel" style="font-family: Arial;">

                    <div class="sectionDivider"
                         onclick="toggleLeftInfo(jQuery('#displayoptions_arrowclick'), 'displayoptions');">
                        Settings
                        <div id="displayoptions_arrowclick" class="toggleLeftDown"></div>
                    </div>

                    <div id="displayoptions" style="display: none" align="left">
                        <input id="guidelineswitch" type="checkbox"/>Guideline
                        <br/>
                        <input id="currentpositionswitch" type="checkbox" checked="checked"/>Coordinates
                        <br/>
                        <input id="scrollswitch" type="checkbox" onchange="scrollSwitcher()"/>Scroll Zoom
                        <p></p>
                        Rows of Tracks:
                        <select id='rowoftracks' name='rowoftracks' onchange='trackToggle("all");'>
                            <option value=1> 1</option>
                            <option value=2> 2</option>
                            <option value=3> 3</option>
                            <option value=4 selected=""> 4</option>
                            <option value=5> 5</option>
                        </select>
                    </div>

                    <div class="sectionDivider" onclick="toggleLeftInfo(jQuery('#Tracksdiv_arrowclick'), 'Tracksdiv');">
                        Tracks List
                        <div id="Tracksdiv_arrowclick" class="toggleLeftDown"></div>
                    </div>

                    <div id="Tracksdiv">
                        <table width=100%>
                            <tr>
                                <th>
                                    Track List

                        <span title='selectAll'><input type="checkbox" id='selectAllCheckbox'
                                                       name='selectAllCheckbox'
                                                       onClick=selectAllCheckbox();>  Select All</span>

                        <span title='unSelectAll'><input type="checkbox" id='unSelectAllCheckbox'
                                                         name='unSelectAllCheckbox'
                                                         onClick=unSelectAllCheckbox();>  Deselect All</span>

                                </th>

                            </tr>
                            <tr>
                                <td>
                                    <div id="tracklist" align="left">

                                    </div>
                                </td>

                            </tr>
                        </table>
                    </div>

                    <div class="sectionDivider"
                         onclick="toggleLeftInfo(jQuery('#MergeTracksdiv_arrowclick'), 'MergeTracksdiv');">
                        Merge
                        Tracks List
                        <div id="MergeTracksdiv_arrowclick" class="toggleLeftDown"></div>
                    </div>

                    <div id="MergeTracksdiv">

                        <table width=100%>
                            <tr>
                                <th>
                                    Merge
                                </th>
                            </tr>
                            <tr>

                                <td>
                                    <div id="mergetracklist" align="left"></div>
                                </td>
                            </tr>

                        </table>
                    </div>
                </div>

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>

    </div>
</div>


<div id="menu">
    <table id="dpop1" width="100%">
        <tr>
            <td>
                ${initParam.fasta == "true" ? "<span title=\"Fasta\" class=\"ui-button  ui-widget  ui-corner-all ui-fasta\" onclick=fetchFasta(getBegin(),getEnd());></span>" : ""}
            </td>
            <td>
                <c:set var="databases">${initParam.blastdblink} </c:set>

                <c:set var="dateParts" value="${fn:split(databases, ',')}"/>

                <c:set var="length">${fn:length(databases)}</c:set>

                ${initParam.fasta == "true" && length > 1 ?  "<span title=\"Blast\" class=\"ui-button  ui-widget  ui-corner-all ui-blast\"  onclick=\"preBlast(getBegin(),getEnd(),'#menu');\"></span>" : ""}
            </td>
            <td align="right">
                <span class="ui-button ui-icon ui-icon-close" onclick="removeAllPopup();"></span>
            </td>
        </tr>
    </table>
</div>

<div id="popup" class="bubbleleft">


    <table id="dpop" width="100%">

        <tr>
            <td>
                <div id="popuptrack" class="popuptrack"></div>
            </td>
            <td align="right">
                <span class="ui-button ui-icon ui-icon-close" onclick=removePopup();></span>
            </td>

        </tr>
        <tr>
            <td colspan="2">
                <div id="position"></div>
            </td>
        </tr>

        <tr>
            <td colspan="2">
                <div id="Detail"></div>
            </td>
        </tr>
    </table>
    <hr style="color: #d3d3d3;">
    <div id="widget"></div>
    <table width="100%">
        <tr>

            <td align="right">

                <div class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only" id="exdetails">
                </div>

                ${initParam.fasta == "true" ? "<div class=\"ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only\" id=\"FASTAme\"> </div>" : ""}

                <c:set var="databases">${initParam.blastdblink} </c:set>

                <c:set var="dateParts" value="${fn:split(databases, ',')}"/>

                <c:set var="length">${fn:length(databases)}</c:set>

                ${initParam.fasta == "true" && length > 1 ? "<div class=\"ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only\" id=\"BLASTme\"> </div>" : ""}

                <div class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only" id="ZoomHere">
                </div>

                <div class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only" id="flagTrack">
                </div>
                <div class="ui-widget ui-state-default ui-corner-all ui-button ui-icon ui-icon-arrow-1-s" id="openmenu"
                     onclick="tooglehangingmenu()" title="More Option">
                </div>
            </td>
        </tr>
    </table>


</div>

<div id=popup_hanging style="display : none; z-index: 1999;">
    <div class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only" id="revertme">
    </div>
    <div class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only"
         id="EditDescription">
    </div>
    <div class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only" id="deleteTrack">
    </div>
    <p>

    <div class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only" id="Linkme">
    </div>
    <div class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only" id="makemetop">
    </div>

    ${initParam.fasta == "true" ? "<div class=\"ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only\" id=\"peptides\"> </div>" : ""}

</div>

<div id="dragpopup" class="bubbleleft">

    <div align="right" onclick="removeDragPopup();"><span class="ui-button ui-icon ui-icon-close"></span>
    </div>
    <table id="dragpop" width="100%">

        <tr>
            <td colspan="3" align="left">
                <div id="cordinate"></div>
            </td>
        </tr>
        <tr align="right">
            <td>
                ${initParam.fasta == "true" ? "<div class=\"ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only\" id=\"fetchFASTA\"> </div>" : ""}
                <div class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only" id="CenterHere">
                </div>

                <c:set var="databases">${initParam.blastdblink} </c:set>

                <c:set var="dateParts" value="${fn:split(databases, ',')}"/>

                <c:set var="length">${fn:length(databases)}</c:set>

                ${initParam.fasta == "true" && length > 1  ? "<div class=\"ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only\" id=\"fetchBLAST\"> </div>" : ""}

            </td>
        </tr>
    </table>


</div>


<div id="blastpopup" class="bubbleleft">

    <div align="right" onclick="removeBlastPopup();"><span class="ui-button ui-icon ui-icon-close">   </span>

    </div>
    <table>
        <tr>
            <td>
                Query:
            </td>
            <td>
                <div id=query>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </td>
            <td>
                <div id=match>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                Sbjct:
            </td>
            <td>
                <div id=sbjct>
                </div>

            </td>
        </tr>
    </table>


</div>

<div id="track-upload-form" title="Upload Your Track Data">
    <p class="validateTips">Select Your Data Type.</p>

    <form>
        <fieldset>
            <input type="radio" name="datatype" value="file"
                   onclick="jQuery('#file').show(); jQuery('#field').hide(); jQuery('#addData').hide()">
            File
            <input type="radio" name="datatype" value="textbox" disabled=""> textbox
            <input type="radio" name="datatype" value="field"
                   onclick="jQuery('#field').show(); jQuery('#file').hide(); changeField();" checked=""> Field
            <p></p>

            <div id="file" style="display: none;">
                <form action="/" method="post">
                    Upload GFF2/GFF3/GTF <sup><a style="color: red; cursor: help"
                                                 onclick="exportSessionHelp();">?</a></sup><input type="file"
                                                                                                  name="files"
                                                                                                  id="files" size="10">
                    <input type='button'
                           value='upload'
                           onclick="fileupload();">

                </form>

                <br> <br> p.s. Working with cds and Exons, SNPs, others will be included in future.
                <p></p>
                <fieldset>
                    <div id=filedata><b> File Name: <br> File Type: <br> File Size: </b></div>
                </fieldset>
            </div>

            <div id="field">
                Feature Type:
                <select name="addfeature" id="addfeature" onchange="changeField()">
                    <option selected value="">Select Track Type</option>
                    <option value="SNP">SNP</option>
                    <option value="CDS">CDS</option>
                    <option value="Exon">Exon</option>
                </select>
            </div>
            <p>

            </p>

            <div id="addData"></div>
        </fieldset>
        <fieldset>
            <div id="addedTracks"></div>
        </fieldset>
    </form>
</div>

<div id="searchBox" style="position: fixed; background: white; border: 1px dotted #000000; top: 45%; left: 45%">
    <div align="right" onclick="searchbox();" class="textbutton"><font color="red"> <u> [X] </u> </font></div>
    <b> Search String: </b>
    <%--<input type="text" name="searchText" id="searchText" size="0" value="" onkeypress="return isACGT(event)"--%>
    <%--onkeyup="searchSeq();">--%>

    <p onclick="jQuery('#searchDiv').html('');">Clear Search Hits</p>

</div>

<div id=blastselector class="popupmenu" style="display: none">

    <div id=blastdbs style="display: none">
        Blast DB <select name="blastdb" id="blastdb">
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

            <option value="${datePartsloc[i-1]}:${dateParts[i-1]}">${filename}</option>

            <%--<option value=${datePartsloc[i-1]}>${dateParts[i-1]}</option>--%>
        </c:forEach>
    </select>

    </div>
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

    </div>
    <p> &nbsp; </p>

    <div id=blastselectorpanel></div>

</div>

<div id=EditTrack class="popupmenu" style="position: absolute; display: none">

</div>

<div id="makegroup" class="popupmenu" style="position: absolute; display: none; padding: 10px">
    <div id=makegrouplist>

    </div>
    <div>
        <span class="fg-button ui-icon ui-widget ui-state-default ui-corner-all ui-icon-close" id="dontblast"
              onclick="groupCancel();"></span>
        <span class="fg-button ui-icon ui-widget ui-state-default ui-corner-all ui-icon-check" id="doblast\"
              onclick="groupTogether();"></span>
    </div>

</div>

<div id="trackmouseover" class="popupmenu" style="position: absolute; display: none">
    <div id="trackmouseoverhead">
    </div>
    <hr>
    <div id="trackmouseoverbody">
    </div>


</div>

<div class="modal fade tgacbrowsermodal" id="uploadModal" role="dialog">
    <div class="modal-dialog modal-lg">

        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button data-dismiss="modal" class="close" type="button">X</button>
                <h4 class="modal-title">Upload data</h4>
            </div>
            <div class="modal-body">
                Current supported formats are GFF, VCF, GAPIT and GEM.
                <br>
                example: File should have extension matching type. <br>

                <div class="sectionDivider" onclick="toggleLeftInfo(jQuery('#uploadFormats_arrowclick'), 'uploadFormats');">
                    Formats Description:
                    <div id="uploadFormats_arrowclick" class="toggleLeft"></div>
                </div>

                <div id="uploadFormats" style="display: none;" class="ui-tabs ui-widget ui-widget-content ui-corner-all">
                    <ul class="ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all">
                        <li class="ui-state-default ui-corner-top ui-tabs-selected ui-state-active">
                            <a href="#formatGFF"><span>GFF</span></a>
                        </li>
                        <li class="ui-state-default ui-corner-top">
                            <a href="#formatVCF"><span>VCF</span></a>
                        </li>
                        <li class="ui-state-default ui-corner-top">
                            <a href="#formatWig"><span>Wig</span></a>
                        </li>
                        <li class="ui-state-default ui-corner-top">
                            <a href="#formatGAPIT"><span>GAPIT</span></a>
                        </li>
                        <li class="ui-state-default ui-corner-top">
                            <a href="#formatGEM"><span>GEM</span></a>
                        </li>
                    </ul>
                    <div id="formatGFF" class="ui-tabs-panel ui-widget-content ui-corner-bottom">
                        GFF
                        <table cellspacing="10" cellpadding="20" border="0" style="border-collapse: separate;">
                            <tr>
                                <td>chr1	<td>EVM	<td>gene	<td>8287	<td>16179	<td>.	<td>-	<td>.	<td>ID=geneid;Name=genename
                            <tr>
                            <tr><td>chr1	<td>EVM	<td>mRNA	<td>8287	<td>16179	<td>.	<td>-	<td>.	<td>ID=transcriptid;Parent=geneid;Name=transcriptname
                            <tr><td>chr1	<td>EVM	<td>exon	<td>15864	<td>16179	<td>.	<td>-	<td>.	<td>ID=exonid;Parent=transcriptid
                            <tr><td>chr1	<td>EVM	<td>CDS	<td>15864	<td>16179	<td>.	<td>-	<td>0	<td>ID=cdsid;Parent=transcriptid
                                </td>
                                </tr>
                            </table>
                        <br>
                        File should match column labels shown in
                        <a target="_blank" href="files/test.gff3"> example file </a>

                    </div>
                    <div id="formatVCF" class="ui-tabs-panel ui-widget-content ui-corner-bottom ">
                        VCF
                        <br>
                        <table cellspacing="10" cellpadding="20" border="0" style="border-collapse: separate;">
                            <tr><td>#chrOM<td>POS<td>ID<td>REF<td>ALT<td>QUAL<td>FILTER<td>INFO<td>FORMAT
                            <tr><td>chr1<td>14370<td>rs6054257<td>G<td>A<td>29<td>PASS<td>NS=3;DP=14;AF=0.5;DB;H2<td>GT:GQ:DP:HQ
                            <tr><td>chr1<td>17330<td>.<td>T<td>A<td>3<td>q10<td>NS=3;DP=11;AF=0.017<td>GT:GQ:DP:HQ
                            <tr><td>chr1<td>1110696<td>rs6040355<td>A<td>G,T<td>67<td>PASS<td>NS=2;DP=10;AF=0.333,0.667;AA=T;DB<td>GT:GQ:DP:HQ
                            </td>
                            </tr>
                            </table>
                        <br>
                        File should match column labels shown in
                        <a target="_blank" href="files/test.vcf"> example file </a>

                    </div>
                    <div id="formatWig" class="ui-tabs-panel ui-widget-content ui-corner-bottom">
                        Wig
                        <table cellspacing="10" cellpadding="20" border="0">
                            <tr><td>variableStep<td>chrom=chr1
                            <tr><td>10<td>34
                            <tr><td>20<td>41
                            <tr><td>30<td>46
                            <tr><td>40<td>49
                            <tr><td>50<td>52
                        </table>
                        <br>
                        File should match column labels shown in
                        <a target="_blank" href="files/test.wig"> example file </a>

                    </div>
                    <div id="formatGAPIT" class="ui-tabs-panel ui-widget-content ui-corner-bottom ">
                        GAPIT
                    <br>
                        GAPIT and GEM files must match column labels shown below, but not order of column. <br>
                        <table class="table table-bordered">
                            <thead>
                            <tr>
                                <th>

                                </th>
                                <th>
                                    P.value
                                </th>
                                <th>
                                    Loc
                                </th>
                                <th>
                                    Chr
                                </th>
                                <th>TAIR_id</th>
                                <th>
                                    ...
                                </th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr>
                                <td>
                                    ref:pos:base
                                </td>
                                <td>
                                    log10P
                                </td>
                                <td>
                                    Chr_start_pos(ref)_end-pos(ref)
                                </td>
                                <td>
                                    Chr
                                </td>
                                <td>TAIR_gene_id</td>
                                <td>
                                    ...
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    cds1:105:A
                                </td>
                                <td>
                                    1.62E
                                </td>
                                <td>
                                    A01-11658_11788
                                </td>
                                <td>
                                    A01
                                </td>
                                <td>AT4G30996.1</td>
                                <td>
                                    ...
                                </td>
                            </tr>
                            </tbody>
                        </table>
                        <br>
                        File should match column labels shown in
                        <a target="_blank" href="files/test_gapit.csv"> example file </a>
                    </div>
                    <div id="formatGEM" class="ui-tabs-panel ui-widget-content ui-corner-bottom ">
                        GEM
                    <br>
                        GAPIT and GEM files must match column labels shown below, but not order of column. <br>
                        <table class="table table-bordered">
                            <thead>
                            <tr>
                                <th>

                                </th>
                                <th>
                                    P.value
                                </th>
                                <th>
                                    Loc
                                </th>
                                <th>
                                    Chr
                                </th>
                                <th>TAIR_id</th>
                                <th>
                                    ...
                                </th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr>
                                <td>
                                    ref:pos:base
                                </td>
                                <td>
                                    log10P
                                </td>
                                <td>
                                    Chr_start_pos(ref)_end-pos(ref)
                                </td>
                                <td>
                                    Chr
                                </td>
                                <td>TAIR_gene_id</td>
                                <td>
                                    ...
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    cds1:105:A
                                </td>
                                <td>
                                    1.62E
                                </td>
                                <td>
                                    A01-11658_11788
                                </td>
                                <td>
                                    A01
                                </td>
                                <td>AT4G30996.1</td>
                                <td>
                                    ...
                                </td>
                            </tr>
                            </tbody>
                        </table></div>
                </div>

                <article>
                    <div id="holder" class=""> Drop files here

                    </div>
                    <p id="upload" class="hidden">
                        <label>Drag &amp; drop not supported, but you can still upload via this input field:<br>
                            <input type="file"></label>
                    </p>
                </article>
            </div>
            <div class="modal-footer">
                <button data-dismiss="modal" class="btn btn-default" type="button">Close</button>
            </div>
        </div>
    </div>
</div>

</body>
</html>