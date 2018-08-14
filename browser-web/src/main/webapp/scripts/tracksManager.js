/*
 *
 * Copyright (c) 2013. Earlham Institute, Norwich, UK
 * TGAC Browser project contacts: Anil Thanki, Xingdong Bian, Robert Davey @ Earlham Institute
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
var showCDS = false, showSNP = false, ctrldown = false;
var rightclick = false, path;
//var cds, SNPs, Exon, minWidth;
var newStart, newEnd, mouseX, mouseY, border_left, border_right, selectionStart, selectionEnd, lastStart = -1, lastEnd = -1, grouplastid = null, grouptrack, grouptrackclass;
var blastsdata = [];
var grouplist = [];
var tracks = [];
var tracklocation = [];


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

// Generate automated tracks divs for each track

function tracks_div(Tracklist, i) {



    if (i) {

        track_div_html(Tracklist, i)

    } else {

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

        for (var i = 0; i < Tracklist.length; i++) {

            track_div_html(Tracklist, i)



        }
    }




}

function track_div_html(Tracklist, i){

    var style = "display:block;";
    if(Tracklist[i].disp == false || Tracklist[i].disp == 0){
        style = "display:none;";
    }
    jQuery("#tracks").append("<div id='" + Tracklist[i].name + "_wrapper' class='feature_tracks' style='"+style+" max-height:110px; overflow-x: hidden;'>" +
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
            "<div id='" + Tracklist[i].name + "_div' class='feature_tracks' style=\"display:block; top:10px;\" > </div>"
        );
    }

    jQuery(function () {
        jQuery("#" + Tracklist[i].name + "_wrapper").resizable({
            handles: "s",
            minHeight: "50px",
            borderBottom: '1px solid black'
        });
    });

    function checkGene(track) {
        if (track.toLowerCase().indexOf('gene') >= 0 || track.toLowerCase().indexOf("gff") >= 0) {
            return "<td><div title='Expand/Shrink' class=\"closehandle ui-icon ui-icon-carat-2-n-s\" onclick=toogleTrackView(\"" + track + "\");> </div></td>"
        }
        else {
            return "";
        }
    }



}

// Generate automated tracks lists for each track

function trackList(tracklist, i) {
    var Tracklist = tracklist;

    if (i) {
        prepare_track_list(Tracklist, i)
    }
    else {
        for (var i = 0; i < Tracklist.length; i++) {
            prepare_track_list(Tracklist, i)


        }

        tracks_div(tracklist);
        tracks_css(tracklist);

    }


}

function prepare_track_list(Tracklist, i){
    window['track_list' + Tracklist[i].name] = {
        name: Tracklist[i].name,
        id: Tracklist[i].id,
        display_label: Tracklist[i].display_label,
        desc: Tracklist[i].desc,
        disp: Tracklist[i].disp,
        merge: Tracklist[i].merge,
        label: Tracklist[i].label,
        graph: Tracklist[i].graph,
        graphtype: null,
        label_show: true,
        ensembl: Tracklist[i].ensembl ?  Tracklist[i].ensembl : null
    }

    tracks_div(Tracklist, i);
    tracks_css(Tracklist, i);

    var checked = "checked"
    if(Tracklist[i].disp == false || Tracklist[i].disp == 0){
        checked = "";
    }

    if (Tracklist[i].web && Tracklist[i].web.trackgroup) {

        if (document.getElementById("group" + Tracklist[i].web.trackgroup) == null) {
            jQuery("#tracklist").append("<div style='padding: 5px; margin: 10px; position: relative; border: 1px solid lightgray; top: 10px' id='group" + Tracklist[i].web.trackgroup + "'> <b>" + Tracklist[i].web.trackgroup + "</b> <p></div>")
            jQuery("#mergetracklist").append("<div style='padding: 5px;  margin: 10px; position: relative; border: 1px solid lightgray; top: 10px' id='mergegroup" + Tracklist[i].web.trackgroup + "'><b>" + Tracklist[i].web.trackgroup + "</b> <p></div>")
        }

        jQuery("<div>").attr({
            'style': "position: relative; width: 70%; word-wrap: break-word;",
            'id': Tracklist[i].name + "span",
            'title': Tracklist[i].desc
        }).html("<input type=\"checkbox\" "+checked+" id='" + Tracklist[i].name + "Checkbox' name='" + Tracklist[i].name + "Checkbox' onClick=loadTrackAjax(\"" + Tracklist[i].id + "\",\"" + Tracklist[i].name + "\"); value=" + Tracklist[i].name + " >" + Tracklist[i].display_label)
            .appendTo("#group" + Tracklist[i].web.trackgroup);

        jQuery("<div>").attr({
            'style': "position: relative; width: 70%; word-wrap: break-word;",
            'id': Tracklist[i].name + "span",
            'title': Tracklist[i].desc
        }).html("<input type=\"checkbox\" disabled id='" + Tracklist[i].name + "mergeCheckbox' name='" + Tracklist[i].name + "mergeCheckbox' onClick=mergeTrack(\"" + Tracklist[i].name + "\"); value=" + Tracklist[i].name + " >" + Tracklist[i].display_label)
            .appendTo("#mergegroup" + Tracklist[i].web.trackgroup);

    } else {
        if (document.getElementById("nogroup") == null) {

            jQuery("#tracklist").append("<div style='padding: 5px; margin: 10px; position: relative; border: 1px solid lightgray; top: 10px' id='nogroup'> </div>")
            jQuery("#mergetracklist").append("<div style='padding: 5px;  margin: 10px; position: relative; border: 1px solid lightgray; top: 10px' id='nomergegroup'></div>")
            jQuery("#nogroup").append("<table id='nogroup-table' width=100%> <tr>");
            jQuery("#nomergegroup").append("<table id='nomergegroup-table' width=100%> <tr>");

        }

        jQuery("<div>").attr({
            'style': "position: relative; width: 70%; word-wrap: break-word;",
            'id': Tracklist[i].name + "span",
            'title': Tracklist[i].desc
        }).html("<input type=\"checkbox\" "+checked+" id='" + Tracklist[i].name + "Checkbox' name='" + Tracklist[i].name + "Checkbox' onClick=loadTrackAjax(\"" + Tracklist[i].id + "\",\"" + Tracklist[i].name + "\"); value=" + Tracklist[i].name + " >" + Tracklist[i].display_label).appendTo("#nogroup-table");

        jQuery("<div>").attr({
            'style': "position: relative; width: 70%; word-wrap: break-word;",
            'id': Tracklist[i].name + "span",
            'title': Tracklist[i].desc
        }).html("<input type=\"checkbox\" disabled id='" + Tracklist[i].name + "mergeCheckbox' name='" + Tracklist[i].name + "mergeCheckbox' onClick=mergeTrack(\"" + Tracklist[i].name + "\"); value=" + Tracklist[i].name + " >" + Tracklist[i].display_label)
            .appendTo("#nomergegroup-table");

        if ((i + 1) % 3 == 0) {
            jQuery("#nogroup-table").append("</tr> <tr>");
            jQuery("#nomergegroup-table").append("</tr> <tr>");
        }
    }

}

// Generate automated css classes for tracks

function tracks_css(Tracklist, i) {
    if(i){
        prepare_track_css (Tracklist, i)
    }else{
        for (var i = 0; i < Tracklist.length; i++) {
            prepare_track_css (Tracklist, i)
        }
    }
}

function prepare_track_css (Tracklist, i){
    if (Tracklist[i].web && Tracklist[i].web.colour) {

        if (Tracklist[i].name.toLowerCase().indexOf("snp") >= 0) {

        }
        else if (Tracklist[i].web.source == "file" && (Tracklist[i].name.toLowerCase().indexOf("gene") >= 0 || Tracklist[i].name.toLowerCase().indexOf("gff") >= 0)) {

            jQuery("<style type='text/css'> ." + Tracklist[i].display_label + "_exon" + "{ background:" + Tracklist[i].web.colour + ";} </style>").appendTo("head");
            jQuery("<style type='text/css'> ." + Tracklist[i].display_label + "_utr" + "{  background:" + Tracklist[i].web.colour + ";} </style>").appendTo("head");
            jQuery("<style type='text/css'> ." + Tracklist[i].display_label + "_graph{ border:1px solid black; background:" + Tracklist[i].web.colour + ";} </style>").appendTo("head");
            jQuery("<style type='text/css'> ." + Tracklist[i].display_label + "_heatgraph{  background:" + Tracklist[i].web.colour + ";} </style>").appendTo("head");

        }
        else if (Tracklist[i].web.source == "file") {
            jQuery("<style type='text/css'> ." + Tracklist[i].display_label + "" + "{ fill:" + Tracklist[i].web.colour + "; stroke: " + Tracklist[i].web.colour + "; background: " + Tracklist[i].web.colour + ";} </style>").appendTo("head");
        }
        else if (Tracklist[i].name.toLowerCase().indexOf("gene") >= 0 || Tracklist[i].name.toLowerCase().indexOf("gff") >= 0) {

            jQuery("<style type='text/css'> ." + Tracklist[i].display_label + "_exon" + "{ background:" + Tracklist[i].web.colour + "; } </style>").appendTo("head");
            jQuery("<style type='text/css'> ." + Tracklist[i].display_label + "_utr" + "{  background:" + Tracklist[i].web.colour + "; opacity: 0.5;} </style>").appendTo("head");
            jQuery("<style type='text/css'> ." + Tracklist[i].display_label + "_graph{ border:1px solid black; background:" + Tracklist[i].web.colour + ";} </style>").appendTo("head");
            jQuery("<style type='text/css'> ." + Tracklist[i].display_label + "_heatgraph{ background:" + Tracklist[i].web.colour + ";} </style>").appendTo("head");

        }
        else {

            jQuery("<style type='text/css'> ." + Tracklist[i].display_label + "{ background:" + Tracklist[i].web.colour + ";} </style>").appendTo("head");
            jQuery("<style type='text/css'> ." + Tracklist[i].display_label + "_graph { border:1px solid black; background:" + Tracklist[i].web.colour + ";} </style>").appendTo("head");
            jQuery("<style type='text/css'> ." + Tracklist[i].display_label + "_heatgraph {  background:" + Tracklist[i].web.colour + ";} </style>").appendTo("head");

        }
    }
    else {


        if (Tracklist[i].name.toLowerCase().indexOf("snp") >= 0) {

        }
        else if (Tracklist[i].name.toLowerCase().indexOf("gene") >= 0 || Tracklist[i].name.toLowerCase().indexOf("gff") >= 0) {
            jQuery("<style type='text/css'> ." + Tracklist[i].display_label + "_exon" + "{ background: green; } </style>").appendTo("head");
            jQuery("<style type='text/css'> ." + Tracklist[i].display_label + "_utr" + "{ background: steelblue; opacity: 0.5;} </style>").appendTo("head");
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
    if (JSON.parse(jQuery.cookie('trackslist')).length > 1) {
        cookietest = JSON.parse(jQuery.cookie('trackslist'));
    }
    //else {
        for (var i = 0; i < Tracklist.length; i++) {
            if (Tracklist[i].disp == "1" && tracklist[i].id.toString().indexOf("noid") < 0) {
                jQuery('#' + Tracklist[i].name + 'Checkbox').attr('checked', true);
                mergeTrackList(Tracklist[i].name);

                var partial = ((getEnd() - getBegin()) / 2);
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
                    {
                        'query': seqregname,
                        'coord': coord,
                        'name': Tracklist[i].name,
                        'trackid': Tracklist[i].id,
                        'start': start,
                        'end': end,
                        'delta': deltaWidth,
                        'url': ajaxurl
                    },
                    {
                        'doOnSuccess': function (json) {
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
    //}


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
                    {
                        'query': seqregname,
                        'coord': coord,
                        'name': Tracklist[i].name,
                        'trackid': Tracklist[i].id,
                        'start': start,
                        'end': end,
                        'delta': deltaWidth,
                        'url': ajaxurl
                    },
                    {
                        'doOnSuccess': function (json) {
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
        eachTrack = {"trackId": "running", "child": blastsdata}
        tracks.push(eachTrack);
    }
    if ((window['blasttrack']))// && !jQuery("#blasttrackCheckbox").is(':checked'))
    {
        var track = window['blasttrack'];
        eachTrack = {"trackId": 0, "child": track}
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
            eachTrack = {"trackName": track_list[i].name + "_edited", "child": window[track_list[i].name + "_edited"]};
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
            eachTrack = {
                "trackName": track_list[i].name + "_removed",
                "child": window[track_list[i].name + "_removed"]
            };
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


