/*
 *
 * Copyright (c) 2013. The Genome Analysis Centre, Norwich, UK
 * TGAC Browser project contacts: Anil Thanki, Xingdong Bian, Robert Davey, Mario Caccamo @ TGAC
 * **********************************************************************
 *
 * This file is part of TGAC Browser.
 *
 * TGAC Browser is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TGAC Browser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TGAC Browser.  If not, see <http://www.gnu.org/licenses/>.
 *
 * ***********************************************************************
 *
 */


var seqregname = null;
var track_list, minWidth;
var start_global, end_global, hit_global, blastid = 0, blastdb = "", oldTracklist;


function seqregionSearch(query) {
    jQuery(window.location).attr('href', "./index.jsp?query=" + query + "&&blast=").attr("target", "_new");
}

function seqregionSearchPopup(query, from, to, blast) {

    jQuery("#searchresult").fadeOut();
    jQuery("#searchresultMap").fadeOut();
    jQuery('#sessioninput').fadeOut();
    jQuery('#tabGenes').html('');
    jQuery('#tabGO').html('');
    jQuery('#tabTranscripts').html('');
    jQuery("#searchresultHead").html("<img style='position: relative; left: 50%; ' src='./images/browser/loading_big.gif' alt='Loading'>");
    jQuery("#searchresult").fadeIn();

    jQuery("#sessionid").html("");
    minWidth = null;
    removeAllPopup();
    Fluxion.doAjax(
        'dnaSequenceService',
        'seqregionSearchSequence',
        {'query': query, 'url': ajaxurl},
        {'doOnSuccess': function (json) {
            if (json.html == "seqregion") {
                jQuery('#canvas').hide();
                jQuery('#currentposition').hide();
                jQuery("#searchresult").fadeIn();
                var content = "<h1>Search Results: </h1><br>";
                for (var i = 0; i < json.seqregion.length; i++) {
                    if (json.seqregion[i].parent_id) {
                        content += "Seq Regions: " + json.seqregion[i].seq_region_id + ": <a target='_blank' href=\"index.jsp?query=" + json.seqregion[i].parent_name + "&&from=" + json.seqregion[i].start + "&&to=" + json.seqregion[i].end + "\" > " + json.seqregion[i].name + "</a> <br>";
                    }
                    else {
                        content += "Seq Regions: " + json.seqregion[i].seq_region_id + ": <a target='_blank' href=\"index.jsp??query=" + json.seqregion[i].name + "&&blast=\" > " + json.seqregion[i].name + "</a> <br>";
                    }
                }
                jQuery("#searchresult").html(content)
            }
            else {
                jQuery("#searchresult").fadeOut();
                seq = json.html;
                sequencelength = json.seqlength;
                if (!track_list) {
                    track_list = json.tracklists;
                }
                jQuery('#seqnameh1').html(json.seqregname);
                jQuery('#seqname').html("<br/>");
                jQuery('#searchseqregioninput').fadeOut();
                jQuery('#canvas').show();
                jQuery('#currentposition').show();
                jQuery('#openCloseWrap').show();
                jQuery('#displayoptions').show();
                seqregname = json.seqregname;
                tracks = jQuery("#filetrack").html().split(',');
                if (tracks[0].length) {
                    for (var i = 0; i < tracks.length; i++) {
                        var filename = tracks[i].substring(tracks[i].lastIndexOf("/") + 1, tracks[i].lastIndexOf("."));
                        var type = tracks[i].substring(tracks[i].lastIndexOf(".") + 1, tracks[i].length);
                        track_list.push(
                            {name: filename + "_" + type, id: tracks[i], display_label: filename, desc: tracks[i], disp: 1, merge: 0, graph: "false", display_lable: tracks[i], label: 0}
                        );
                    }
                }

                trackList(track_list);

                minWidth = findminwidth();
                if (maxLen > minWidth) {

                    if (from && to) {
                        if (parseInt(from) < parseInt(to)) {
                            setBegin(from);
                            setEnd(parseInt(to));
                        }
                        else {
                            setBegin(to);
                            setEnd(parseInt(from));
                        }
                    }
                    else {
                        setBegin((sequencelength - minWidth) / 2);
                        setEnd(parseInt(getBegin()) + minWidth);
                    }
                    if (blast) {
                        loadPreBlast(blast, query);
                    }
                    jumpToSeq();
                    setNavPanel();

                    jQuery("#controlsbutton").colorbox({width: "90%", inline: true, href: "#controlpanel"});
                }
                else {

                }
                dispSeqCoord();
                displayCursorPosition();
                if (json.coord_sys.indexOf('chromosome') >= 0) {
                    getReferences();
                }
                else {
                    chromosome = false;
                }
                loadDefaultTrack(track_list);
            }
        }
        });

}

function search(query, from, to, jsonid, oldtracks) {
    if (track_list) {
        jQuery.cookie('trackslist', track_list.toJSON(), {  path: '/', expires: 10});
        removeTrackslist(track_list);
    }

    jQuery('#sessioninput').fadeOut();
    jQuery("#sessionid").html("");
    minWidth = null;
    removeAllPopup();
    jQuery('#canvas').hide();
    jQuery('#tabGenes').html('');
    jQuery('#tabGO').html('');
    jQuery('#tabTranscripts').html('');

    jQuery("#searchresultHead").html("<img style='position: relative; left: 50%; ' src='./images/browser/loading_big.gif' alt='Loading'>");
    jQuery("#searchresult").fadeIn();

    Fluxion.doAjax(
        'dnaSequenceService',
        'searchSequence',
        {'query': query, 'url': ajaxurl},
        {'doOnSuccess': function (json) {
            jQuery('#canvas').hide();
            jQuery('#currentposition').hide();

            if (json.chromosome == true) {
                jQuery("#searchresult").fadeOut();
                getReferences(json);
                jQuery("#searchresultMap").fadeIn();
            }
            else if (json.html == "seqregion") {
                jQuery("#searchresult").fadeIn();
                var seqregioncontent = "<h1>Search Results: </h1><br>";
                var content = "<h1>Search Results: </h1><br>";
                for (var i = 0; i < json.seqregion.length; i++) {
                    if (i == 0) {
                        seqregioncontent += "<table class='list' id='search_hit' ><thead><tr><th>SeqRegion</th><th>SeqRegion Id</th><th>Reference Name</th><th>Link</th></tr></thead>";
                    }

                    seqregioncontent += "<tr><td>" + json.seqregion[i].Type + "<td> " + json.seqregion[i].seq_region_id + "<td>" + json.seqregion[i].name + " <td><a target='_blank' href='index.jsp?query=" + json.seqregion[i].name + "' > <span title=\"Link\" class=\"ui-button ui-icon ui-icon-link\" </span><a/></td>";
                    if (i == json.seqregion.length - 1) {
                        seqregioncontent += "</table>";
                        jQuery("#searchresult").html(seqregioncontent);
                    }

                    jQuery("#search_hit").tablesorter();
                }

            }
            else if (json.html == "gene" || json.html == "GO" || json.html == "transcript") {

                jQuery('#canvas').hide();
                jQuery('#currentposition').hide();
                jQuery("#searchresult").html(" <div id=\"searchresultHead\"></div><div id=\"searchnavtabs\"><ul> <li><a href=\"#tabGenes\"><span>Genes</span></a></li>  <li><a href=\"#tabTranscripts\"><span>Transcripts</span></a></li><li><a href=\"#tabGO\"><span>GO</span></a></li> </ul> <div id=\"tabGenes\"></div> <div id=\"tabGO\"> </div>      <div id=\"tabTranscripts\"></div> </div>");
                jQuery("#searchresult").fadeIn();
                var genecontent = "";
                var content = "<h1>Search Results: </h1><br>";
                for (var i = 0; i < json.gene.length; i++) {
                    if (i == 0) {
                        genecontent += "<table class='list' id='gene_hit'><thead><tr><th>Track</th><th>Gene</th><th>Reference Name</th><th>Position</th><th>Link</th></tr></thead>";
                    }
                    genecontent += "<tr><td>" + json.gene[i].Type + "<td> " + json.gene[i].name + "<td>" + json.gene[i].parent + "<td>" + json.gene[i].start + "-" + json.gene[i].end + "<td> <a target='_blank' href='index.jsp?query=" + json.gene[i].parent + "&&from=" + json.gene[i].start + "&&to=" + json.gene[i].end + "' > <span title=\"Link\" class=\"ui-button ui-icon ui-icon-link\" </span> </a></td>";
                    if (i == json.gene.length - 1) {
                        genecontent += "</table>";
                        jQuery('#tabGenes').append(genecontent);
                    }

                    jQuery("#gene_hit").tablesorter();

                }
                content += "<hr>";
                var gocontent = "";
                for (var i = 0; i < json.GO.length; i++) {

                    if (i == 0) {
                        gocontent += "<table class='list' id='go_hit'><thead><tr><th>Attribute Type</th><th>Gene/Transcript Name</th><th>Reference Name</th><th>Position</th><th>Link</th></tr></thead>";
                    }

                    gocontent += "<tr><td>" + json.GO[i].Type + "<td>" + json.GO[i].name + "<td>" + json.GO[i].parent + "<td>" + json.GO[i].start + "-" + json.GO[i].end + "</td><td> <a target='_blank' href='index.jsp?query=" + json.GO[i].parent + "&&from=" + json.GO[i].start + "&&to=" + json.GO[i].end + "' ><span title=\"Link\" class=\"ui-button ui-icon ui-icon-link\" </span> </a></tr>";

                    if (i == json.GO.length - 1) {
                        gocontent += "</table>";
                        jQuery('#tabGO').append(gocontent);
                    }

                    jQuery("#go_hit").tablesorter();

                }


                content += "<hr>";
                var transcriptcontent = "";
                for (var i = 0; i < json.transcript.length; i++) {
                    if (i == 0) {
                        transcriptcontent += "<table class='list' id='transcript_hit'><thead><tr><th>Attribute Type</th><th>Transcript Name</th><th>Reference Name</th><th>Position</th><th>Link</th></tr></thead>";
                    }
                    transcriptcontent += "<tr><td> " + json.transcript[i].Type + "<td>" + json.transcript[i].name + "<td> " + json.transcript[i].parent + "<td>" + json.transcript[i].start + "-" + json.transcript[i].end + "<td><a target='_blank' href='index.jsp?query=" + json.transcript[i].parent + "&&from=" + json.transcript[i].start + "&&to=" + json.transcript[i].end + "' ><span title=\"Link\" class=\"ui-button ui-icon ui-icon-link\" </span> </a></td></tr>";

                    if (i == json.transcript.length - 1) {
                        transcriptcontent += "</table>";
                        jQuery('#tabTranscripts').append(transcriptcontent);
                    }
                }

                jQuery("#transcript_hit").tablesorter();
                jQuery("#searchnavtabs").tabs();
                jQuery("#searchresultHead").html("<h2>Search Result</h2>");
            }
            else {
                window.location.replace("index.jsp?query=" + json.seqregname);
            }
        }
        });
}

function generateFileLink(data) {
    var filelink;
    Fluxion.doAjax(
        'fileService',
        'exportFile',
        {'filecontent': data, 'url': ajaxurl, 'location': path},
        {'doOnSuccess': function (json) {
            filelink = json.link;
            jQuery(window.location).attr('href', filelink).attr("target", "_blank");
        }
        });
}

function loadTrackAjax(trackId, trackname) {
    mergeTrackList(trackname);
    var query = jQuery('#search').val();

    jQuery(track_list).each(function (index) {
        //this is the object in the array, index is the index of the object in the array
        if (jQuery("#" + track_list[index].name + "Checkbox").attr('checked')) {//
            window['track_list' + track_list[index].name].disp = 1
            jQuery("#unSelectAllCheckbox").attr('checked', false)
        }
        else {
            window['track_list' + track_list[index].name].disp = 0
            jQuery("#selectAllCheckbox").attr('checked', false)
        }
    });

    if (window[trackname] || window[trackname] == "running" || window[trackname] == "loading") {
        trackToggle(trackname);
//    need to think abt it
    }

    if (jQuery("#" + trackname + "Checkbox").attr('checked') && trackId.indexOf('noid') < 0) {
        var partial = (getEnd() - getBegin()) + ((getEnd() - getBegin()) / 2);
        var start = (getBegin() - partial);
        var end = parseInt(getEnd()) + parseFloat(partial);
        if (start < 0) {
            start = 0;
        }
        if (end > sequencelength) {
            end = sequencelength;
        }
        deltaWidth = parseInt(end - start) * 2 / parseInt(maxLen);
        window[trackname] == "loading";
        trackToggle(trackname);
        Fluxion.doAjax(
            'dnaSequenceService',
            'loadTrack',
            {'query': seqregname, 'name': trackname, 'trackid': trackId, 'start': start, 'end': end, 'delta': deltaWidth, 'url': ajaxurl},
            {'doOnSuccess': function (json) {
                var trackname = json.name;

                if (json.type == "graph") {
                    window['track_list' + json.name].graph = "true";
                }
                else {
                    window['track_list' + json.name].graph = "false";
                }
                window[trackname] = json[trackname];
                trackToggle(trackname);
            }
            });
    }
}

function metaData() {
    ajaxurl = '/' + jQuery('#title').text() + '/' + jQuery('#title').text() + '/fluxion.ajax';

    Fluxion.doAjax(
        'dnaSequenceService',
        'metaInfo',
        {'url': ajaxurl},
        {'doOnSuccess': function (json) {
            jQuery("#dbinfo").html("Species Name: <i>" + json.metainfo[0].name + "</i> Database Version: " + json.metainfo[0].version);
            chromosome = json.chr;
        }
        });
    return chromosome;
}

function saveSession() {
    var tracks = getTracks();
    var edited_tracks = getEditedTracks();
    var removed_tracks = getRemovedTracks();
    var trackslist = getTracklist();
    var blast = jQuery("#notifier").text().indexOf("BLAST");
    Fluxion.doAjax(
        'fileService',
        'saveFile',
        {'location': path, 'reference': seqregname, 'session': randomnumber, 'from': getBegin(), 'to': getEnd(), 'seq': seq, 'seqlen': sequencelength, 'track': trackslist, 'tracks': tracks, 'filename': (randomnumber), 'blast': blast, 'edited_tracks': edited_tracks, 'removed_tracks': removed_tracks, 'url': ajaxurl},
        {'doOnSuccess': function (json) {
            jQuery("#export").html("<a target = '_blank' href='" + json.link + "'>Export</a>")
            jQuery("#export").show();
        }
        });
}

function loadSession(query) {
    Fluxion.doAjax(
        'fileService',
        'loadSession',
        {'location': path, 'query': query, 'url': ajaxurl},
        {'doOnSuccess': function (json) {
            var now = new Date();

            seq = json.seq;
            sequencelength = json.seqlen;
            track_list = json.tracklist;
            randomnumber = json.session;
            jQuery("#sessionid").html("<b>Session Id: </b><a  href='./session.jsp?query=" + randomnumber + "' target='_blank'>" + randomnumber + "</a> Saved at " + now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds());
            jQuery("#sessionid").show();

            jQuery('#seqnameh1').html('<a href="/"+path+"/">' + json.reference + '</a>');
            jQuery('#seqname').html("<br/>");
            jQuery('#canvas').show();
            jQuery('#displayoptions').show();
            jQuery('#sessioninput').fadeOut();
            seqregname = json.reference;
            trackList(track_list);
            minWidth = findminwidth();
            setBegin(json.from);
            setEnd(json.to)
            jumpToSeq();
            dispSeqCoord();
            displayCursorPosition();
            setNavPanel();
            getReferences();
            loadEditedTracks(json.edited_tracks)
            loadRemovedTracks(json.removed_tracks)
            reloadTracks(json.tracks, track_list, json.blast);
            jQuery("#controlsbutton").colorbox({width: "90%", inline: true, href: "#controlpanel"});
            checkSession();
        }
        });
}

function loadSeq(query, from, to) {
    Fluxion.doAjax(
        'dnaSequenceService',
        'loadSequence',
        {'query': query, 'from': getBegin(), 'to': getEnd(), 'url': ajaxurl},
        {'doOnSuccess': function (json) {
            seq = json.seq;
            return json.seq;
        }
        });
}

function reloadTracks(tracks, tracklist, blast) {
    for (var i = 0; i < tracklist.length; i++) {
        if (tracklist[i].name.indexOf('blasttrack') >= 0) {
            jQuery('#' + tracklist[i].name + 'Checkbox').attr('checked', true);
            window['blasttrack'] = tracks[tracks.length - 1].child;

            trackToggle('blasttrack');
        }
        else if (tracklist[i].disp == 1 && tracklist[i].id.indexOf('noid') < 0) {
            jQuery('#' + tracklist[i].name + 'Checkbox').attr('checked', true);
            if (tracklist[i].merge == "1") {
                mergeTrackList(tracklist[i].name);
                jQuery('input[name=' + tracklist[i].name + 'mergedCheckbox]').attr('checked', true);
            }
            var partial = (getEnd() - getBegin()) / 2;
            var start = (getBegin() - partial);
            var end = parseInt(getEnd()) + parseFloat(partial);
            if (start < 0) {
                start = 0;
            }
            if (end > sequencelength) {
                end = sequencelength;
            }
            var deltaWidth = parseInt(end - start) * 2 / parseInt(maxLen);
            window[tracklist[i].name] == "loading";
            trackToggle(tracklist[i].name);
            Fluxion.doAjax(
                'dnaSequenceService',
                'loadTrack',
                {'query': seqregname, 'name': tracklist[i].name, 'trackid': tracklist[i].id, 'start': start, 'end': end, 'delta': deltaWidth, 'url': ajaxurl},
                {'doOnSuccess': function (json) {
                    var trackname = json.name;

                    if (json.type == "graph") {
                        window['track_list' + json.name].graph = "true";
                    }
                    else {
                        window['track_list' + json.name].graph = "false";
                    }
                    window[trackname] = json[trackname];
                    trackToggle(trackname);
                }
                });
        }
        else {
        }
    }
    if (blast == "true" || blast == 1) {
        setBlast();
        for (var j = 0; j < tracks.length - 1; j++) {
            if (tracks[j].trackId == "running") {
                if (!window['blasttrack']) {
                    window['blasttrack'] = "running";
                    jQuery("#blasttrack_div").html("<img style='position: relative; left: 50%; ' src='./images/browser/loading_big.gif' alt='Loading'>")
                    jQuery("#blasttrack_wrapper").fadeIn();
                    jQuery('input[name=blasttrack-0]').attr('checked', true);
                }
                jQuery("#notifier").html("<img src='images/browser/loading2.gif' height='10px'> BLAST running ");
                jQuery("#notifier").show();

                var blasts = tracks[j].child;
                blastsdata = blasts;
                jQuery.each(blasts, function (index) {

                    checkTask(blasts[index].id, blasts[index].db, blasts[index].format, blasts[index].start, blasts[index].end, blasts[index].hit, blasts[index].link);
                });
                jQuery('input[name=blasttrackCheckbox]').attr('checked', true);
                trackToggle('blasttrack');
            }
        }
    }
}


function randomString(length) {
    var chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz'.split('');

    if (!length) {
        length = Math.floor(Math.random() * chars.length);
    }

    var str = '';
    for (var i = 0; i < length; i++) {
        str += chars[Math.floor(Math.random() * chars.length)];
    }
    return str;
}

function fastaFile(seq, start, end) {
    var fastaseq = seq;
    Fluxion.doAjax(
        'fileService',
        'saveFasta',
        {'sequence': fastaseq, 'reference': seqregname, 'from': start, 'to': end, 'url': ajaxurl, 'location': path},
        {'doOnSuccess': function (json) {
            jQuery("#fastadownload").html("<a href=" + json.link + " target = '_blank'>Download</a>");
        }
        });

}

function loadPreBlast(jsonid, refid) {

    var refseq = refid;
    if (!window['blasttrack']) {
        window['track_listblasttrack'] = {
            name: "blasttrack",
            id: "noid",
            display_label: "blasttrack",
            desc: "blast from browser",
            disp: 1,
            merge: 0,
            label: 0,
            graph: false
        }

        jQuery("#tracklist").append("<p title='blast' id=blastcheck><input type=\"checkbox\" checked id='blasttrackCheckbox' name='blasttrackCheckbox' onClick=loadTrackAjax(\"blasttrack\",\"blasttrack\");\>  Blasttrack\  </p>");

        jQuery("#mergetracklist").append("<span id=blasttrackspan> <input type=\"checkbox\" id='blasttrackmergedCheckbox' name='blasttrackmergedCheckbox' onClick=mergeTrack(\"blasttrack\"); value=blasttrack >Blast Track</span>");

        jQuery("#tracks").append("<div id='blasttrack_div' class='feature_tracks'> Blast Track </div>");

        jQuery("#blasttrack_div").html("<img style='position: relative; left: 50%; ' src='./images/browser/loading_big.gif' alt='Loading'>")
        jQuery("#blasttrack_div").fadeIn();

        track_list.push(
            {name: "blasttrack", display_label: "blasttrack", id: "noid", desc: "blast from browser", disp: 1, merge: 0}
        );
        window['blasttrack'] = "running";

    }

    jQuery("#blasttrack_div").fadeIn();
    Fluxion.doAjax(
        'blastservice',
        'blastEntry',
        {'accession': jsonid, 'seqregion': refid, 'url': ajaxurl},
        {'doOnSuccess': function (json) {
            track_list.push(
                {name: "blasttrack", id: "noid", desc: "blast from browser", disp: 1, merge: 0}
            );

            window['blasttrack'] = json.entries;//(decodeURIComponent(json.blast.replace(/\s+/g, ""))).replace(/>/g, "");
            jQuery('input[name=blasttrackCheckbox]').attr('checked', true);
            jQuery("#mergetracklist").append("<span id=blastcheckmerge> <input type=\"checkbox\" id='blasttrackmergedCheckbox' name='blasttrackmergedCheckbox' onClick=mergeTrack(\"blasttrack\"); value=blasttrack >Blast Track</span>");
            trackToggle("blasttrack");

        }
        });
}

function fileUploadProgress(formname, divname, successfunc) {
    var self = this;
    Fluxion.doAjaxUpload(
        formname,
        'fileUploadProgressBean',
        'checkUploadStatus',
        {'url': ajaxurl},
        {'statusElement': divname, 'progressElement': 'trash', 'doOnSuccess': successfunc},
        {'validationBeanId': 'fileValidationBean'}
    );
}

function processingOverlay() {
    jQuery.colorbox({width: "30%", html: "Processing..."});
}

function fileUploadSuccess() {
    alert("upload done");
}

function getReferences(show) {
    Fluxion.doAjax(
        'dnaSequenceService',
        'searchSeqRegionforMap',
        {'url': ajaxurl},
        {'doOnSuccess': function (json) {
            var maximumLengthname, maximumsequencelength;
            var max = Math.max.apply(Math, json.seqregion.map(function (o) {
                return o.length;
            }));

            var referenceLength = json.seqregion.length;
            if (!maxLen) {
                maxLen = jQuery(window).width();
            }
            var width = 15;
            var distance = (parseInt(maxLen) - (width * referenceLength)) / (referenceLength + 1);
            jQuery("#mapmarker").animate({"width": width}, 100);
            jQuery("#refmap").html("");
            if (referenceLength > 0 && referenceLength < 50) {
                changeCSS();
                while (referenceLength--) {
                    if (json.seqregion[referenceLength].length == max) {
                        maximumLengthname = json.seqregion[referenceLength].name;
                        maximumsequencelength = json.seqregion[referenceLength].length;
                    }
                    var left = parseInt(referenceLength * (width)) + parseInt(distance * referenceLength) + parseInt(distance);
                    var height = (json.seqregion[referenceLength].length * 125 / max);
                    var length = json.seqregion[referenceLength].length;
                    if (seqregname == json.seqregion[referenceLength].name) {
                        refheight = height;
                    }
                    var top = parseInt(jQuery("#map").css('top')) + parseInt(jQuery("#map").css('height')) - (height + 20);
                    if (seqregname == json.seqregion[referenceLength].name) {
                        jQuery("#refmap").append("<div onclick='jumpToHere(event);' class='refmap' id='" + json.seqregion[referenceLength].name + "' style='left: " + left + "px; width:" + width + "px; height:" + height + "px;'></div>");
                    }
                    else {
                        jQuery("#refmap").append("<div onclick='jumpToOther(event, " + length + ",\"" + json.seqregion[referenceLength].name + "\");' class='refmap' id='" + json.seqregion[referenceLength].name + "' style='left: " + left + "px; width:" + width + "px; height:" + height + "px;'></div>");
                    }
                    jQuery("#refmap").append("<div style='position:absolute; bottom: 0px; left: " + (left) + "px; '>" + stringTrim(json.seqregion[referenceLength].name, width * 2) + "</div>");
                    jQuery("#map").fadeIn();
                }
                if (show) {
                    jQuery("#searchresultMap").show;

                    if (show.html) {
                        dispOnMap(show, maximumLengthname, maximumsequencelength);
                    }

                    jQuery("#searchresultMap").html("<center><h1>References</h1><br>Click to jump to reference</center>");
                }
                else {
                    getMarkers();
                }
                setMapMarkerLeft();
                setMapMarkerTop(getBegin());
                setMapMarkerHeight(getEnd() - getBegin())
            }
        }
        });
}

function dispOnMap(json, maximumLengthname, maximumsequencelength) {
    var width = 15;
    jQuery("#searchResultLegend").html("")
    jQuery("#searchResultLegend").fadeIn();

    if (json.html == "seqregion") {
        jQuery("#searchResultLegend").html("<div class='searchResultLegend'><input checked type=checkbox name='refmapsearchmarkerseqregion' onClick=jQuery('.refmapsearchmarkerseqregion').toggle()> Seq Region </div> ")

        var markers = json.seqregion;
        for (var i = 0; i < markers.length; i++) {

            var length = maximumsequencelength * parseFloat(jQuery("#" + markers[i].parent).css('height')) / parseFloat(jQuery("#" + maximumLengthname).css('height'));
            var maptop = parseFloat(jQuery("#" + markers[i].parent).css('height')) + parseInt(jQuery("#" + markers[i].parent).css('bottom')) - (parseInt(markers[i].end) * parseFloat(jQuery("#" + markers[i].parent).css('height')) / length);
            var left = parseInt(jQuery("#" + markers[i].parent).css('left')) + parseInt(20);
            var mapheight = ((markers[i].end - markers[i].start) * parseFloat(jQuery("#" + markers[i].parent).css('height'))) / length;
            if (mapheight < 1) {
                mapheight = 1;
            }
            jQuery("#refmap").append("<div name='" + markers[i].name + "' parent=" + markers[i].parent + " start=" + markers[i].start + " end=" + markers[i].end + " id='" + markers[i].name + "' title='" + markers[i].name + ":" + markers[i].start + "-" + markers[i].end + "' class='refmapsearchmarkerseqregion'  style='left:" + left + "px; bottom:" + maptop + "px;  width:" + width + "px; height:" + mapheight + "px;' onclick=clicked_func('" + markers[i].name + "'); ></div>");
        }

    }

    if (json.html == "gene" || json.html == "GO" || json.html == "transcript") {
        jQuery("#searchResultLegend").append("<br><div class='searchResultLegend'><input checked type=checkbox name='refmapsearchmarkergene' onClick=jQuery('.refmapsearchmarkergene').toggle()> Gene </div> ")
        jQuery("#searchResultLegend").append("<br><div class='searchResultLegend'><input checked type=checkbox name='refmapsearchmarkertranscript' onClick=jQuery('.refmapsearchmarkertranscript').toggle()> Transcript </div> ")
        jQuery("#searchResultLegend").append("<br><div class='searchResultLegend'><input checked  type=checkbox name='refmapsearchmarkergo' onClick=jQuery('.refmapsearchmarkergo').toggle()> GO </div>")
        var markers = json.gene;

        for (var i = 0; i < markers.length; i++) {
            var length = maximumsequencelength * parseFloat(jQuery("#" + markers[i].parent).css('height')) / parseFloat(jQuery("#" + maximumLengthname).css('height'));
            var maptop = parseFloat(jQuery("#" + markers[i].parent).css('height')) + parseInt(jQuery("#" + markers[i].parent).css('bottom')) - (parseInt(markers[i].end) * parseFloat(jQuery("#" + markers[i].parent).css('height')) / length);
            var left = parseInt(jQuery("#" + markers[i].parent).css('left')) + parseInt(20);
            var mapheight = ((markers[i].end - markers[i].start) * parseFloat(jQuery("#" + markers[i].parent).css('height'))) / length;
            if (mapheight < 1) {
                mapheight = 1;
            }
            jQuery("#refmap").append("<div name='" + markers[i].name + "' parent=" + markers[i].parent + " start=" + markers[i].start + " end=" + markers[i].end + " id='" + markers[i].name + "' title='" + markers[i].name + ":" + markers[i].start + "-" + markers[i].end + "' class='refmapsearchmarkergene'  style='left:" + left + "px; bottom:" + maptop + "px;  width:" + width + "px; height:" + mapheight + "px;' onclick=clicked_func('" + markers[i].name + "'); ></div>");

        }

        var markers = json.transcript;

        for (var i = 0; i < markers.length; i++) {
            var length = maximumsequencelength * parseFloat(jQuery("#" + markers[i].parent).css('height')) / parseFloat(jQuery("#" + maximumLengthname).css('height'));
            var maptop = parseFloat(jQuery("#" + markers[i].parent).css('height')) + parseInt(jQuery("#" + markers[i].parent).css('bottom')) - (parseInt(markers[i].end) * parseFloat(jQuery("#" + markers[i].parent).css('height')) / length);
            var left = parseInt(jQuery("#" + markers[i].parent).css('left')) + parseInt(20);
            var mapheight = ((markers[i].end - markers[i].start) * parseFloat(jQuery("#" + markers[i].parent).css('height'))) / length;
            if (mapheight < 1) {
                mapheight = 1;
            }
            jQuery("#refmap").append("<div name='" + markers[i].name + "' parent=" + markers[i].parent + " start=" + markers[i].start + " end=" + markers[i].end + " id='" + markers[i].name + "' title='" + markers[i].name + ":" + markers[i].start + "-" + markers[i].end + "' class='refmapsearchmarkertranscript'  style='left:" + left + "px; bottom:" + maptop + "px;  width:" + width + "px; height:" + mapheight + "px;' onclick=clicked_func('" + markers[i].name + "'); ></div>");
        }

        var markers = json.GO;

        for (var i = 0; i < markers.length; i++) {
            var length = maximumsequencelength * parseFloat(jQuery("#" + markers[i].parent).css('height')) / parseFloat(jQuery("#" + maximumLengthname).css('height'));
            var maptop = parseFloat(jQuery("#" + markers[i].parent).css('height')) + parseInt(jQuery("#" + markers[i].parent).css('bottom')) - (parseInt(markers[i].end) * parseFloat(jQuery("#" + markers[i].parent).css('height')) / length);
            var left = parseInt(jQuery("#" + markers[i].parent).css('left')) + parseInt(20);
            var mapheight = ((markers[i].end - markers[i].start) * parseFloat(jQuery("#" + markers[i].parent).css('height'))) / length;
            if (mapheight < 1) {
                mapheight = 1;
            }
            jQuery("#refmap").append("<div name='" + markers[i].name + "' parent=" + markers[i].parent + " start=" + markers[i].start + " end=" + markers[i].end + " id='" + markers[i].name + "' title='" + markers[i].name + ":" + markers[i].start + "-" + markers[i].end + "' class='refmapsearchmarkergo'  style='left:" + left + "px; bottom:" + maptop + "px;  width:" + width + "px; height:" + mapheight + "px;' onclick=clicked_func('" + markers[i].name + "'); ></div>");
        }
    }

}

function clicked_func(element) {
    console.log(element)
    element = element.replace(/\./g, '\\.')
    var seqregioncontent = "";


    element = jQuery("[name='" + element + "']");
    var parent_main = element.attr("parent");
    console.log(element)

    var temp_element = element;
    var class_clicked = "." + element.attr('class');
    console.log(class_clicked)
    var temp = element.prevAll(class_clicked);
    console.log(temp)
    for (var i = 0; i < temp.length; i++) {
        console.log(temp[i])
        var temp_id = temp[i].id.replace(/\./g, '\\.');
        var temp_element = jQuery("[name='" + temp_id + "']");
        console.log(temp_element)
        var parent = temp_element.attr("parent");
        var start = temp_element.attr("start");
        var end = temp_element.attr("end");
        var name = temp_element.attr("title").split(":")[0];
        var link = "<a target='_blank' href='index.jsp?query=" + parent + "&&from=" + start + "&&to=" + end + "' > <span title=\"Link\" class=\"ui-button ui-icon ui-icon-link\" </span><a/>"

        seqregioncontent = "<tr><td>" + parent + "</td><td>" + name + "</td><td>" + start + ":" + end + "</td><td>" + link + "</td>" + seqregioncontent;

        if (i >= 4) {
            break;
        }

    }

    seqregioncontent = "<table class='list' id='search_hit' ><thead><tr><th>Parent</th><th>Name</th><th>Position</th><th>Link</th></tr> </thead>" + seqregioncontent;
    temp_element = element;

    var temp_id = temp_element.attr('id').replace(/\./g, '\\.');
    var temp_element = jQuery("[name='" + temp_id + "']");
    console.log(temp_element)
    var parent = temp_element.attr("parent");
    var start = temp_element.attr("start");
    var end = temp_element.attr("end");
    var name = temp_element.attr("title").split(":")[0];


    var link = "<a target='_blank' href='index.jsp?query=" + parent + "&&from=" + start + "&&to=" + end + "' > <span title=\"Link\" class=\"ui-button ui-icon ui-icon-link\" </span><a/>"
    seqregioncontent += "<tr><td><b><u>" + parent + "</b></u></td><td><b><u>" + name + "</b></u></td><td><u><b>" + start + ":" + end + "</u></b></td><td>" + link + "</td>";

    var temp = element.nextAll(class_clicked);

    for (var i = 0; i < temp.length; i++) {
        console.log(temp[i])
        var temp_id = temp[i].id.replace(/\./g, '\\.');
        var temp_element = jQuery("[name='" + temp_id + "']");
        console.log(temp_element)
        var parent = temp_element.attr("parent");
        var start = temp_element.attr("start");
        var end = temp_element.attr("end");
        var name = temp_element.attr("title").split(":")[0];
        var link = "<a target='_blank' href='index.jsp?query=" + parent + "&&from=" + start + "&&to=" + end + "' > <span title=\"Link\" class=\"ui-button ui-icon ui-icon-link\" </span><a/>"

        seqregioncontent += "<tr><td>" + parent + "</td><td>" + name + "</td><td>" + start + ":" + end + "</td><td>" + link + "</td>";

        if (i >= 4) {
            break;
        }

    }

    jQuery("#searchresult").html(seqregioncontent)
    jQuery("#searchresult").fadeIn()
    jQuery("#searchresult").css('top', '225px')
    console.log(parent_main)
    jQuery("#searchresult").css('left', jQuery("#" + parent_main).css('left'))


}

function getMarkers() {
    Fluxion.doAjax(
        'dnaSequenceService',
        'loadMarker',
        {'url': ajaxurl},
        {'doOnSuccess': function (json) {
            var markers = json.marker;
            var height = jQuery("#" + seqregname).css('height');
            var width = 15;
            for (var i = 0; i < markers.length; i++) {
                var length = sequencelength * parseFloat(jQuery("#" + markers[i].reference).css('height')) / parseFloat(jQuery("#" + seqregname).css('height'));
                var maptop = parseFloat(jQuery("#" + markers[i].reference).css('height')) + parseInt(jQuery("#" + markers[i].reference).css('bottom')) - (parseInt(markers[i].end) * parseFloat(jQuery("#" + markers[i].reference).css('height')) / length);
                var left = parseInt(jQuery("#" + markers[i].reference).position().left) + parseInt(20);
                var mapheight = parseFloat(jQuery("#" + markers[i].reference).css('height')) / length;
                if (mapheight < 1) {
                    mapheight = 1;
                }
                if (seqregname == markers[i].reference) {
                    jQuery("#refmap").append("<div title='" + markers[i].reference + ":" + markers[i].start + "' class='refmapmarker'  style='left:" + left + "px; bottom:" + maptop + "px;  width:" + width + "px; height:" + mapheight + "px;' onclick='setBegin(" + markers[i].start + "); setEnd(" + parseInt(parseInt(markers[i].start) + parseInt(1)) + "); jumpToSeq();'></div>");
                }
                else {
                    jQuery("#refmap").append("<div  title='" + markers[i].reference + ":" + markers[i].start + "' class='refmapmarker'  style='left:" + left + "px; bottom:" + maptop + "px;  width:" + width + "px; height:" + mapheight + "px;' onclick='window.location.replace(\"index.jsp?query=" + markers[i].reference + "&from=" + markers[i].start + "&to=" + parseInt(parseInt(markers[i].start) + parseInt(1)) + "\");' ></div>");
                }
            }
        }
        });
}

function changeCSS() {
    jQuery("#bar_image").css('top', '210px');
    jQuery("#nav_panel").css('top', '225px');
    jQuery(".vertical-line").css('top', '264px');
    jQuery("#bg_layer").css('top', '230px');
    jQuery("#draggable").css('top', '229px');
    jQuery("#wrapper").css('top', '265px');
    jQuery("#sequence").css('top', '280px');
    jQuery(".fakediv").css('top', '260px');

}