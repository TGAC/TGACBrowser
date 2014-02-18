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

/**
 * Created by IntelliJ IDEA.
 * User: thankia
 * Date: 2/17/12
 * Time: 4:08 PM
 * To change this template use File | Settings | File Templates.
 */

var seq = null, seqLen, sequencelength, randomnumber, merged_track_list, deltaWidth = 0, refheight;
var maxLen, showCDS = false, showSNP = false, ctrldown = false;
var rightclick = false, path;
//var cds, SNPs, Exon, minWidth;
var newStart, newEnd, mouseX, mouseY, border_left, border_right, selectionStart, selectionEnd, lastStart = -1, lastEnd = -1, grouplastid = null, grouptrack, grouptrackclass;
var blastsdata = [];
var grouplist = [];
var tracks = [];
var tracklocation = [];
var chromosome = false;


//toogle side bar codes
function tracklistopenclose() {
    if (jQuery("#openCloseIdentifier").is(":hidden")) {
        jQuery("#slider").animate({
            marginLeft: "-141px"
        }, 500);
        jQuery("#openCloseIdentifier").show();
    }
    else {
        jQuery("#slider").animate({
            marginLeft: "0px"
        }, 500);
        jQuery("#openCloseIdentifier").hide();
    }
}


// Generate automated tracks lists and divs for each track

function trackList(tracklist) {
    var Tracklist = tracklist;
    for (var i = 0; i < Tracklist.length; i++) {

        window['track_list' + Tracklist[i].name] = {
            name: Tracklist[i].name,
            id: Tracklist[i].id,
            display_label: Tracklist[i].display_label,
            desc: Tracklist[i].desc,
            disp: Tracklist[i].disp,
            merge: Tracklist[i].merge,
            label: Tracklist[i].label,
            graph: Tracklist[i].graph,
            graphtype : null,

        label_show: true
        }
    }

    var tracks = "<table> <tr>";
    var mergeTrack = "<table> <tr>";

    for (var i = 0; i < Tracklist.length; i++) {

        if (Tracklist[i].web && Tracklist[i].web.trackgroup) {

            console.log(Tracklist[i].name)
            if (document.getElementById("group" + Tracklist[i].web.trackgroup) == null) {

                jQuery("#tracklist").append("<div style='padding: 5px; margin: 10px; position: relative; border: 1px solid lightgray; top: 10px' id='group" + Tracklist[i].web.trackgroup + "'> <b>" + Tracklist[i].web.trackgroup + "</b> <p></div>")
                jQuery("#mergetracklist").append("<div style='padding: 5px;  margin: 10px; position: relative; border: 1px solid lightgray; top: 10px' id='mergegroup" + Tracklist[i].web.trackgroup + "'><b>" + Tracklist[i].web.trackgroup + "</b> <p></div>")

            }

            jQuery("#group" + Tracklist[i].web.trackgroup).append("<span title='" + Tracklist[i].desc + "'><input type=\"checkbox\" id='" + Tracklist[i].name + "Checkbox' name='" + Tracklist[i].name + "-" + Tracklist[i].id + "'  onClick=loadTrackAjax(\"" + Tracklist[i].id + "\",\"" + Tracklist[i].name + "\"); />  " + Tracklist[i].display_label + " </span> <p> ");
            jQuery("#mergegroup" + Tracklist[i].web.trackgroup).append("<span id='" + Tracklist[i].name + "span'> <input type=\"checkbox\" disabled id='" + Tracklist[i].name + "mergedCheckbox' name='" + Tracklist[i].name + "mergedCheckbox' onClick=mergeTrack(\"" + Tracklist[i].name + "\"); value=" + Tracklist[i].name + " >" + Tracklist[i].display_label + "  </span> <p>");

        } else {
            if (document.getElementById("nogroup") == null) {

                jQuery("#tracklist").append("<div style='padding: 5px; margin: 10px; position: relative; border: 1px solid lightgray; top: 10px' id='nogroup'> </div>")
                jQuery("#mergetracklist").append("<div style='padding: 5px;  margin: 10px; position: relative; border: 1px solid lightgray; top: 10px' id='nomergegroup'></div>")
                jQuery("#nogroup").append("<table> <tr>");
                jQuery("#nomergemgroup").append("<table> <tr>");

            }

            jQuery("#nogroup").append(" <td><span title='" + Tracklist[i].desc + "'><input type=\"checkbox\" id='" + Tracklist[i].name + "Checkbox' name='" + Tracklist[i].name + "-" + Tracklist[i].id + "'  onClick=loadTrackAjax(\"" + Tracklist[i].id + "\",\"" + Tracklist[i].name + "\"); />  " + Tracklist[i].display_label + " </span></td>");
            jQuery("#nomergegroup").append("<td><span id='" + Tracklist[i].name + "span'> <input type=\"checkbox\" disabled id='" + Tracklist[i].name + "mergedCheckbox' name='" + Tracklist[i].name + "mergedCheckbox' onClick=mergeTrack(\"" + Tracklist[i].name + "\"); value=" + Tracklist[i].name + " >" + Tracklist[i].display_label + "  </span> </td>");

            if ((i + 1) % 3 == 0) {
                jQuery("#nogroup").append("</tr> <tr>");
                jQuery("#nomergegroup").append("</tr> <tr>");
            }
        }

        if (Tracklist[i].web && Tracklist[i].web.colour) {
            console.log(Tracklist[i].name)

            if (Tracklist[i].name.toLowerCase().indexOf("snp") >= 0) {

            }
            else if (Tracklist[i].web.source == "file" && (Tracklist[i].name.toLowerCase().indexOf("gene") >= 0 || Tracklist[i].name.toLowerCase().indexOf("gff") >= 0)) {
                console.log("gene " +Tracklist[i].name)

                jQuery("<style type='text/css'> ." + Tracklist[i].display_label + "_exon" + "{ background:" + Tracklist[i].web.colour + "; border: 1px solid " + Tracklist[i].web.colour + ";} </style>").appendTo("head");
                jQuery("<style type='text/css'> ." + Tracklist[i].display_label + "_utr" + "{ border: 1px solid " + Tracklist[i].web.colour + "; background:none repeat scroll 0 0 white;} </style>").appendTo("head");
                jQuery("<style type='text/css'> ." + Tracklist[i].display_label + "_graph{ border:1px solid black; background:" + Tracklist[i].web.colour + ";} </style>").appendTo("head");
                jQuery("<style type='text/css'> ." + Tracklist[i].display_label + "_heatgraph{  background:" + Tracklist[i].web.colour + ";} </style>").appendTo("head");

            }
            else if (Tracklist[i].web.source == "file") {
                jQuery("<style type='text/css'> ." + Tracklist[i].display_label + "" + "{ fill:" + Tracklist[i].web.colour + "; stroke: " + Tracklist[i].web.colour + "; background: " + Tracklist[i].web.colour + ";} </style>").appendTo("head");
            }
            else if (Tracklist[i].name.toLowerCase().indexOf("gene") >= 0 || Tracklist[i].name.toLowerCase().indexOf("gff") >= 0) {
                console.log("gene " +Tracklist[i].name)

                jQuery("<style type='text/css'> ." + Tracklist[i].display_label + "_exon" + "{ background:" + Tracklist[i].web.colour + "; border: 1px solid " + Tracklist[i].web.colour + ";} </style>").appendTo("head");
                jQuery("<style type='text/css'> ." + Tracklist[i].display_label + "_utr" + "{ border: 1px solid " + Tracklist[i].web.colour + "; background:none repeat scroll 0 0 white;} </style>").appendTo("head");
                jQuery("<style type='text/css'> ." + Tracklist[i].display_label + "_graph{ border:1px solid black; background:" + Tracklist[i].web.colour + ";} </style>").appendTo("head");
                jQuery("<style type='text/css'> ." + Tracklist[i].display_label + "_heatgraph{ background:" + Tracklist[i].web.colour + ";} </style>").appendTo("head");

            }
            else {
                console.log("gene "+Tracklist[i].name)

                jQuery("<style type='text/css'> ." + Tracklist[i].display_label + "{ background:" + Tracklist[i].web.colour + ";} </style>").appendTo("head");
                jQuery("<style type='text/css'> ." + Tracklist[i].display_label + "_graph { border:1px solid black; background:" + Tracklist[i].web.colour + ";} </style>").appendTo("head");
                jQuery("<style type='text/css'> ." + Tracklist[i].display_label + "_heatgraph {  background:" + Tracklist[i].web.colour + ";} </style>").appendTo("head");

            }
        }
        else {

            console.log(Tracklist[i].name)

            if (Tracklist[i].name.toLowerCase().indexOf("snp") >= 0) {

            }
            else if (Tracklist[i].name.toLowerCase().indexOf("gene") >= 0 || Tracklist[i].name.toLowerCase().indexOf("gff") >= 0) {
                jQuery("<style type='text/css'> ." + Tracklist[i].display_label + "_exon" + "{ background: green; border: 1px solid green;} </style>").appendTo("head");
                jQuery("<style type='text/css'> ." + Tracklist[i].display_label + "_utr" + "{ border: 1px solid green; background:none repeat scroll 0 0 white;} </style>").appendTo("head");
                jQuery("<style type='text/css'> ." + Tracklist[i].display_label + "_graph { border:1px solid black; background:green;} </style>").appendTo("head");
                jQuery("<style type='text/css'> ." + Tracklist[i].display_label + "_heatgraph {  background:green;} </style>").appendTo("head");

            }
            else {
                var colour = "";

                if (Tracklist[i].name.toLowerCase().indexOf("contig") >= 0) {
                    colour = "#ff8c00";
                }
                else if (Tracklist[i].name.toLowerCase().indexOf("est") >= 0) {
                    colour = "#556b2f";
                }
                else if (Tracklist[i].name.toLowerCase().indexOf("clone") >= 0) {
                    colour = "#90ee90";
                }
                else if (Tracklist[i].name.toLowerCase().indexOf("align") >= 0) {
                    colour = "#a52a2a";
                }
                else if (Tracklist[i].name.toLowerCase().indexOf("sam") >= 0 || Tracklist[i].name.toLowerCase().indexOf("bam") >= 0) {
                    colour = "blue";
                }
                else if (Tracklist[i].name.toLowerCase().indexOf("repeat") >= 0) {
                    colour = "gray";
                }
                else {
                    colour = "green";
                }

                jQuery("<style type='text/css'> ." + Tracklist[i].display_label + "{ background:" + colour + ";} </style>").appendTo("head");
                jQuery("<style type='text/css'> ." + Tracklist[i].display_label + "_graph{ border:1px solid black; background:" + colour + ";} </style>").appendTo("head");
                jQuery("<style type='text/css'> ." + Tracklist[i].display_label + "_heatgraph{ background:" + colour + ";} </style>").appendTo("head");
            }
        }
    }

    jQuery("#tracks").html("<div id='mergedtrack_wrapper' class='feature_tracks' style=\"display:none\">  " +
        "<div align='left' class='handle'>" +
        "<table>" +
        "<tr>" +
        "<td><b>Merged Track  </b></td>" +
        "<td><div class=\"ui-icon ui-icon-comment\" onclick=toogleLabelMerged();> </div></td>" +
        "<td><div class='closehandle ui-icon ui-icon-close' onclick=removeMergedTrack()></div></td>" +
        "<td><div id= \"mergelabel\" align='left'></div></td>" +
        "</tr>" +
        "</table>" +
        "</div>" +
        "<div id=\"mergedtrack\" style=\"display:none\" > </div>" +
        "</div>");

    jQuery("#mergedtrack_wrapper").resizable({
        handles: "s",
        alsoResize: "#mergedtrack",
        minHeight: "50px",
        borderBottom: '1px solid black'
    });

    for (i = 0; i < Tracklist.length; i++) {

        jQuery("#tracks").append("<div id='" + Tracklist[i].name + "_wrapper' class='feature_tracks' style=\"display:none; max-height:110px; overflow-x: hidden;\">" +
            "</div>");

        if (Tracklist[i].web && Tracklist[i].web.label == false) {
            window['track_list' + Tracklist[i].name].label_show = false;
            jQuery("#" + Tracklist[i].name + "_wrapper").append("<div align='left' class='handle'>" +
                "<table>" +
                "<tr>" +
                "<td><div class='closehandle ui-icon ui-icon-close' onclick=removeTrack(\"" + Tracklist[i].name + "_div\",\"" + Tracklist[i].name + "\");></div></td>" +
                "</tr>" +
                "</table>" +
                "</div>" +
                "<div id='" + Tracklist[i].name + "_div' class='feature_tracks' style=\"display:none; top:0px;\" > " + Tracklist[i].name + "</div>"
            );
        } else {
            jQuery("#" + Tracklist[i].name + "_wrapper").append("<div align='left' class='handle'>" +
                "<table>" +
                "<tr>" +
                "<td><b>" + Tracklist[i].display_label + "</b></td>" +
                "<td><div class=\"ui-icon ui-icon-comment\" onclick=toogleLabel(\"" + Tracklist[i].name + "\");> </div></td>" + checkGene(Tracklist[i].name) +
                "<td><div class='closehandle ui-icon ui-icon-close' onclick=removeTrack(\"" + Tracklist[i].name + "_div\",\"" + Tracklist[i].name + "\");></div></td>" +
                "</tr>" +
                "</table>" +
                "</div>" +
                "<div id='" + Tracklist[i].name + "_div' class='feature_tracks' style=\"display:none; top:10px;\" > " + Tracklist[i].name + "</div>"
            );
        }

        jQuery(function () {
            jQuery("#" + Tracklist[i].name + "_wrapper").resizable({
                handles: "s",
                minHeight: "50px",
                borderBottom: '1px solid black'
            });
        });
    }


    function checkGene(track) {
        if (track.toLowerCase().indexOf('gene') >= 0 || track.toLowerCase().indexOf("gff") >= 0) {
            return "<td><div title='Expand/Shrink' class=\"closehandle ui-icon ui-icon-carat-2-n-s\" onclick=toogleTrackView(\"" + track + "\");> </div></td>"
        }
        else {
            return "";
        }
    }
}

function toggleLeftInfo(div, id) {
    if (jQuery(div).hasClass("toggleRight")) {
        jQuery(div).removeClass("toggleRight").addClass("toggleRightDown");
    }
    else {
        jQuery(div).removeClass("toggleRightDown").addClass("toggleRight");
    }
    jQuery("#" + id).toggle("blind", {}, 500);
}

function loadDefaultTrack(tracklist) {
    var Tracklist = tracklist;
    var cookietest = []
    if (jQuery.cookie('trackslist')) {
        cookietest = JSON.parse(jQuery.cookie('trackslist'));
    }
    else {
        for (var i = 0; i < Tracklist.length; i++) {
            if (Tracklist[i].disp == "1" && tracklist[i].id.toString().indexOf("noid") < 0) {
                jQuery('#' + Tracklist[i].name + 'Checkbox').attr('checked', true);
                mergeTrackList(Tracklist[i].name);

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
                window[Tracklist[i].name] == "loading";
                trackToggle(Tracklist[i].name);
                Fluxion.doAjax(
                    'dnaSequenceService',
                    'loadTrack',
                    {'query': seqregname, 'name': Tracklist[i].name, 'trackid': Tracklist[i].id, 'start': start, 'end': end, 'delta': deltaWidth, 'url': ajaxurl},
                    {'doOnSuccess': function (json) {
                        var trackname = json.name;

                        if (json.type == "graph") {
                            window['track_list' + json.name].graph = "true";
                            window['track_list' + json.name].graphtype = json.graphtype;
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
    }

    for (var i = 0; i < Tracklist.length; i++) {

        jQuery.each(cookietest, function (j, v) {
            if (v.name == Tracklist[i].name && v.disp == 1 && Tracklist[i].id.toString().indexOf('noid') < 0) {
                jQuery('#' + Tracklist[i].name + 'Checkbox').attr('checked', true);
                mergeTrackList(Tracklist[i].name);
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

                window[Tracklist[i].name] == "loading";

                trackToggle(Tracklist[i].name);
                Fluxion.doAjax(
                    'dnaSequenceService',
                    'loadTrack',
                    {'query': seqregname, 'name': Tracklist[i].name, 'trackid': Tracklist[i].id, 'start': start, 'end': end, 'delta': deltaWidth, 'url': ajaxurl},
                    {'doOnSuccess': function (json) {
                        var trackname = json.name;

                        if (json.type == "graph") {
                            window['track_list' + json.name].graph = "true";
                            window['track_list' + json.name].graphtype = json.graphtype;
                        }
                        else {
                            window['track_list' + json.name].graph = "false";
                        }
                        window[trackname] = json[trackname];
                        trackToggle(trackname);
                    }
                    });
                jQuery('#' + Tracklist[i].name + 'Checkbox').attr('checked', true);
                window['track_list' + track_list[i].name].disp = 1

                return false; // stops the loop
            }
            else if (v.name == Tracklist[i].name && v.disp == 0) {
                window["track_list" + Tracklist[i].name].disp = 0;
            }
        });
        continue;
    }
}

function mergeTrackList(trackName) {

    if (jQuery("#" + trackName + "Checkbox").is(':checked')) {
        jQuery("#" + trackName + "mergedCheckbox").removeAttr("disabled");
    }
    else {
        jQuery("#" + trackName + "mergedCheckbox").attr("disabled", true);

        mergeTrack(trackName);
    }
}

function getTracks() {

    var tracks = [];
    var eachTrack = {};
    if (jQuery("#notifier").text().indexOf("BLAST") >= 0) {
        eachTrack = { "trackId": "running", "child": blastsdata}
        tracks.push(eachTrack);
    }
    if ((window['blasttrack']))// && !jQuery("#blasttrackCheckbox").is(':checked'))
    {
        var track = window['blasttrack'];
        eachTrack = { "trackId": 0, "child": track}
        tracks.push(eachTrack);
    }

    return tracks;
}

function getTracklist() {

    var tracks = [];
    for (var i = 0; i < track_list.length; i++) {

        tracks.push(window["track_list" + track_list[i].name]);
    }

    return tracks;
}

function getEditedTracks() {
    var tracks = [];
    var eachTrack = {};
    for (var i = 0; i < track_list.length; i++) {
        if (window[track_list[i].name + "_edited"]) {
            eachTrack = { "trackName": track_list[i].name + "_edited", "child": window[track_list[i].name + "_edited"]};
            tracks.push(eachTrack);
        }
    }
    return tracks;
}

function getRemovedTracks() {
    var tracks = [];
    var eachTrack = {};
    for (var i = 0; i < track_list.length; i++) {
        if (window[track_list[i].name + "_removed"]) {
            eachTrack = { "trackName": track_list[i].name + "_removed", "child": window[track_list[i].name + "_removed"]};
            tracks.push(eachTrack);
        }
    }
    return tracks;
}

function loadEditedTracks(tracks) {
    for (var i = 0; i < tracks.length; i++) {
        window[tracks[i].trackName] = tracks[i].child;
    }
}

function loadRemovedTracks(tracks) {
    for (var i = 0; i < tracks.length; i++) {
        window[tracks[i].trackName] = tracks[i].child;
    }
}

function selectAllCheckbox() {
    if (jQuery("#selectAllCheckbox").is(':checked')) {
        jQuery("#tracklist input").each(function () {
            if (jQuery(this).is(':checked')) {
                //    do nothing
            }
            else {
                jQuery(this).attr('checked', 'checked');
                eval(jQuery(this).attr('onClick'));
            }
        })
    }
    else {
    }
}

function unSelectAllCheckbox() {
    if (jQuery("#unSelectAllCheckbox").is(':checked')) {
        jQuery("#tracklist input").each(function () {
            if (jQuery(this).is(':checked')) {
                jQuery(this).attr('checked', false);
                window['track_list' + this.id.replace("Checkbox", "")].disp = 0
            }
        })
    }
    trackToggle("all")
}