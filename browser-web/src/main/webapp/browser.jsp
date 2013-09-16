<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>
<%@ page import="java.util.ArrayList" language="java" %>
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
                                        <input type="text" id="begin" size="5" class="jump">
                                    </td>
                                    <td>
                                        <input type="text" id="end" size="5" class="jump">
                                    </td>
                                    <td>
                                        <div onclick="jumpToSeq()" class="divbutton"> Go</div>
                                    </td>
                                    <td>
                                        <div class="divbutton" style="padding: 0px 5px;" onClick="parent.location='mailto:tgac.browser@tgac.ac.uk?Subject=TGAC Browser - Feedback';">
                                            <span  class="ui-button ui-icon ui-icon-mail-closed"></span>
                                        </div>

                                    </td>
                                </tr>
                            </table>

                            <div id=alertDiv></div>
                            <div id="notifier" class="rightAlertdiv"></div>
                        </div>
                    </td>
                    <td>
                        <div style="width: 100%; margin: 0px auto; position: fixed; left: 0px;">
                            <center>
                                <img src='images/browser/reset.png' onclick="reset();" class="browserimage" height=50%
                                     alt="Reset" title="Reset"> &nbsp;&nbsp;
                                <img src='images/browser/backward.png' onclick="dragLeft();" id="leftbig" class="browserimage"
                                     height=70%
                                     alt="Backward" title="Move Left">
                                <img src='images/browser/backward.png' onclick="seqLeft();"
                                     class="browserimage browserimagesmall"
                                     height=40% alt="Left" title="Move Left (1bp)">&nbsp;
                                <img src='images/browser/forward.png' onclick="seqRight();"
                                     class="browserimage browserimagesmall"
                                     height=40% alt="Right" title="Move Right (1bp)">
                                <img src='images/browser/forward.png' onclick="dragRight();" id="rightbig" class="browserimage"
                                     height=70% alt="Forward" title="Move Right">&nbsp;&nbsp;
                                <img src='images/browser/zoomin.png' id="zoominbig"
                                     onclick="zoomIn(parseInt(sequencelength/20));"
                                     class="browserimage"
                                     height=70% alt="ZoomIn" title="Zoom In">
                                <img src='images/browser/zoomin.png' id="zoominsmall"
                                     onclick="zoomIn(parseInt(sequencelength/40));"
                                     class="browserimage browserimagesmall"
                                     height=40% alt="Zoomin" title="Zoom In">&nbsp;
                                <img src='images/browser/zoomout.png' id="zoomoutsmall"
                                     onclick="zoomOut(parseInt(sequencelength/40));"
                                     class="browserimage browserimagesmall"
                                     height=40% alt="Zoomout" title='Zoom Out'>
                                <img src='images/browser/zoomout.png' id="zoomoutbig"
                                     onclick="zoomOut(parseInt(sequencelength/20));"
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
                                    <div id=export style=" display: none;"
                                         class="divbutton"> Export
                                    </div>
                                </td>
                                <td>
                                    <div onclick="checkSession();"
                                         class="divbutton">
                                        Save Session
                                    </div>
                                </td>

                                <td>
                                    <div id="controlsbutton" class="divbutton">
                                        Tracks / Settings
                                    </div>
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
        <div id="bg_layer"></div>
        <div id="draggable" onmouseup="auto_drag()" onmouseout="auto_drag()"></div>


        <div id="wrapper">

            <div id=tracks>
            </div>
            <div class="fakediv">

            </div>
            <div id="sequence">

                <center>
                    <div id="sequenceString"></div>
                    <div id="translation_div"></div>
                </center>


            </div>
        </div>
    </div>

</div>

<%--<div id="openCloseIdentifier"></div>--%>

<div style='display:none'>
    <div id="controlpanel">

        <h1>Control Panel</h1>

        <div class="sectionDivider" onclick="toggleLeftInfo(jQuery('#displayoptions_arrowclick'), 'displayoptions');">
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

        <div class="sectionDivider" onclick="toggleLeftInfo(jQuery('#Tracksdiv_arrowclick'), 'Tracksdiv');">Tracks List
            <div id="Tracksdiv_arrowclick" class="toggleLeftDown"></div>
        </div>

        <div id="Tracksdiv">
            <table>
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

        <div class="sectionDivider" onclick="toggleLeftInfo(jQuery('#MergeTracksdiv_arrowclick'), 'MergeTracksdiv');">
            Merge
            Tracks List
            <div id="MergeTracksdiv_arrowclick" class="toggleLeftDown"></div>
        </div>

        <div id="MergeTracksdiv">

            <table>
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
                <%--<tr>--%>
                <%--<td>--%>
                <%--<form method='post'--%>
                <%--id='ajax_upload_form'--%>
                <%--action="<c:url value="/vietnamese_rice/upload/file"/>"--%>
                <%--enctype="multipart/form-data"--%>
                <%--target="target_upload"--%>
                <%--onsubmit="fileUploadProgress('ajax_upload_form', 'statusdiv', fileUploadSuccess);">--%>
                <%--<input type="file" name="file"/>--%>
                <%--<button type="submit" class="br-button ui-state-default ui-corner-all">Upload</button>--%>
                <%--</form>--%>
                <%--<iframe id='target_upload' name='target_upload' src='' style='display: none'></iframe>--%>
                <%--<div id="statusdiv"></div>--%>
                <%--</td>--%>
                <%--<td>--%>

                <%--</td>--%>
                <%--</tr>--%>
            </table>
        </div>
        <%--<div id="openCloseWrap" style="display: none; cursor: pointer" onclick="tracklistopenclose();">--%>
        <%--<font color="white"> Contrasdfols </font>--%>
        <%--</div>--%>
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
                <div id="popuptrack" class="popuptrack" ></div>
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

<div id=popup_hanging style="display : none; z-index: 999;">
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

<div id=blastselector class="popupmenu" style="position: absolute; display: none">

    <div id=blastdbs style="position: absolute; display: none">
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

<span id="ruler"></span>

<script>
    jQuery(document).ready(function () {
        metaData();
        jQuery('input.deletable').wrap('<span class="deleteicon" />').after(jQuery('<span/>').click(function () {
            jQuery(this).prev('input').val('').focus();
            jQuery('#searchText').val('Search DNA');
            jQuery('#searchDiv').html('');
        }));
    });
</script>