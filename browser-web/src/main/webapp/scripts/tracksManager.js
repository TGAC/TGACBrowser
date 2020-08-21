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
var newStart, newEnd, mouseX, mouseY, border_left, border_right, selectionStart, selectionEnd, lastStart = -1,
    lastEnd = -1, grouplastid = null, grouptrack, grouptrackclass;
var blastsdata = [];
var grouplist = [];
var tracks = [];
var tracklocation = [];

var single_line_track_list = [];


//toogle side bar codes
function tracklistopenclose() {
    console.log("tracklistopenclose")

    if (jQuery("#openCloseIdentifier").is(":hidden")) {
        jQuery("#slider").animate({
            marginLeft: "-141px"
        }, 500);
        jQuery("#openCloseIdentifier").show();
    } else {
        jQuery("#slider").animate({
            marginLeft: "0px"
        }, 500);
        jQuery("#openCloseIdentifier").hide();
    }
}

// Generate automated tracks divs for each track

function tracks_div(trackname) {
    console.log("tracks_div")

    if (document.getElementById(trackname + "_wrapper") == null) {
        if (trackname) {
            track_div_html(trackname)
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

            for (var i = 0; i < track_list.length; i++) {
                track_div_html(track_list[i].name)
            }
        }

    }
}

function track_div_html(trackname) {
    console.log("track_div_html")

    var track = window['track_list' + trackname]
    var style = "display:block;";
    if (track.disp == false || track.disp == 0) {
        style = "display:none;";
    }
    jQuery("#tracks").append("<div id='" + track.name + "_wrapper' class='feature_tracks' style='" + style + " max-height:110px; overflow: hidden;'>" +
        "</div>");


    if (track.web && track.web.label == false) {
        window['track_list' + track.name].label_show = false;
        jQuery("#" + track.name + "_wrapper").append("<div align='left' class='handle'>" +
            "<table>" +
            "<tr>" +
            "<td><div title=\"Hide Track\" class='closehandle ui-icon ui-icon-close' onclick=removeTrack(\"" + track.name + "_div\",\"" + track.name + "\");></div></td>" +
            "</tr>" +
            "</table>" +
            "</div>" +
            "<div id='" + track.name + "_div' class='feature_tracks' style=\"display:none; top:0px; overflow-y: auto; overflow-x: hidden\" > " + track.name + "</div>"
        );
    } else {
        jQuery("#" + track.name + "_wrapper").append("<div align='left' class='handle'>" +
            "<table>" +
            "<tr>" +
            "<td><b>" + track.display_label + "</b></td>" +
            "<td><div title=\"Track names\"  class=\"closehandle ui-icon ui-icon-comment\" onclick=toogleLabel(\"" + track.name + "\");> </div></td>" + checkGene(track.name) +
            "<td><div title=\"Hide Track\" class='closehandle ui-icon ui-icon-close' onclick=removeTrack(\"" + track.name + "_div\",\"" + track.name + "\");></div></td>" +
            "</tr>" +
            "</table>" +
            "</div>" +
            "<div id='" + track.name + "_div' class='feature_tracks' style=\"display:block; top:10px; overflow-y: auto; overflow-x: hidden\" > </div>"
        );
    }

    jQuery(function () {
        jQuery("#" + track.name + "_wrapper").resizable({
            handles: "s",
            minHeight: "50px",
            borderBottom: '1px solid black',
            alsoResize: "#" + track.name + "_div"
        });
    });

    function checkGene(name) {
        if (name.toLowerCase().indexOf('gene') >= 0 || name.toLowerCase().indexOf("gff") >= 0) {
            return "<td><div title='Expand/Shrink' class=\"closehandle ui-icon ui-icon-carat-2-n-s\" onclick=toogleTrackView(\"" + name + "\");> </div></td>";
        } else {
            return "<td><div class=\"ui-icon ui-icon-extlink\" onclick=exportTrack(\"" + name + "\");> </div></td>";
        }
    }


}


function trackListfromFiles(tracklist) {
    console.log("trackListfromFiles")

    var Tracklist = tracklist;

    for (var i = 0; i < Tracklist.length; i++) {
        prepare_searchable_track_list(Tracklist, i)
    }

    var track_html = "Genomic annotations from files: <br> <select class=\"js-example-basic-multiple\" id=\"track_files\" name=\"sam_files\" multiple='multiple' style=\"width: 75%\">"

    for (var i = 0; i < Tracklist.length; i++) {
        track_html += "<option value='" + Tracklist[i].name + "'>" + Tracklist[i].display_label + "</option>"
    }
    track_html += "</select>"

    jQuery("#filetrackgroup").html(track_html)

    jQuery('#track_files').select2({
        placeholder: "Search here..",
        maximumSelectionLength: 4
    });

    jQuery('#track_files').on('select2:unselecting', function (e) {
        var r = confirm("Do you want to disable " + e["params"]["args"]["data"]["text"])

        if (r == false) {
            e.preventDefault();
        } else {
            var item = e["params"]["args"]["data"]["text"]["element"]["value"]
            jQuery("#" + item + "_wrapper").fadeOut();
            window[item] = []
            return true;
        }
    })
    jQuery('#track_files').on('select2:select', function (evt) {
        var args = JSON.stringify(evt.params, function (key, value) {
            var item = evt["params"]["data"]["element"]["value"]
            //             var item = evt["params"]["data"]["text"]
            tracks_div(item)
            tracks_css(item);
            loadTrackAjax(window['track_list' + item].id, item);
        });
    })

    // jQuery("#filetrackgroup").append("<div><button onclick='loadSelectedTrack()'>Load Selected Track</button></div>")
}


// Generate automated tracks lists for each track

function trackList(tracklist, i) {
    console.log("tracklist")
    var Tracklist = tracklist;

    if (i) {
        prepare_track_list(Tracklist, i)
    } else {
        for (var i = 0; i < Tracklist.length; i++) {
            prepare_track_list(Tracklist, i)
        }
    }

    prepare_single_line()
}


function loadSelectedLine() {
    console.log("loadSelectedLine")
    var someVariable = jQuery("#lines_searchable_list").val();
    if (typeof someVariable !== "undefined") {

        var currentUrl = location.href;
        var url = new URL(currentUrl);


        url.searchParams.set("line", someVariable);
        var newUrl = url.href;
        console.log(newUrl);
        window.history.pushState('TGAC Browser', 'Title', newUrl);


        jQuery(".singleLineCheckbox").each(function () {
            console.log(jQuery("#lines_searchable_list").val())
            loadTrackAjax(jQuery(this).val(), jQuery(this).val());
        });
        var Tracklist = track_list;
        for (var i = 0; i < Tracklist.length; i++) {
            var trackname = Tracklist[i].name;
            var trackid = Tracklist[i].id;
            if (trackid == "ms1") {
                loadTrackAjax(trackid, trackname);
            }
        }


    } else {
        setTimeout(loadSelectedLine, 250);
    }
}


function prepare_single_line() {
    console.log("prepare_single_line")
    Fluxion.doAjax(
        'dnaSequenceService',
        'initMiscFeature', //Using existing function to get all Lines
        {'url': ajaxurl},
        {
            'doOnSuccess': function (json) {
                var lines = json.lines;

                var track_html = "Select a Line: <br> <select class=\"js-example-basic-single\" id=\"lines_searchable_list\" name=\"lines_searchable_list\" style=\"width: 75%\" onchange='loadSelectedLine()'>";

                for (var i = 0; i < lines.length; i++) {
                    if (preset_line == null && i == 0) {
                        track_html += "<option value='" + lines[i].name + "' selected>" + lines[i].name + "</option>"
                    } else  if (preset_line != null && preset_line == lines[i].name) {
                        track_html += "<option value='" + lines[i].name + "' selected>" + lines[i].name + "</option>"
                    } else{
                        track_html += "<option value='" + lines[i].name + "'>" + lines[i].name + "</option>"
                    }
                }
                track_html += "</select>"

                jQuery("#line_list").html(track_html)

                jQuery('#lines_searchable_list').select2({
                    placeholder: "Search here.."
                });
            }
        });

    console.log("prepare_single_line 2")


    Fluxion.doAjax(
        'dnaSequenceService',
        'initMiscSet',
        {'url': ajaxurl},
        {
            'doOnSuccess': function (json) {
                var set = json.set;

                var track_html = " "
                for (var i = 0; i < set.length; i++) {
                    console.log("prepare_single_line set " + i)

                    //Coverage track

                    jQuery("#tracks").append("<div id='" + set[i].name + "_coverage_wrapper' class='feature_tracks singleline' style='position:relative; display:none;max-height:110px; overflow: hidden;'>" +
                        "</div>");


                    jQuery("#" + set[i].name + "_coverage_wrapper").append("<div align='left' class='handle'>" +
                        "<table>" +
                        "<tr>" +
                        "<td><b>" + set[i].name + "_coverage</b></td>" +
                        "</tr>" +
                        "</table>" +
                        "</div>" +
                        "<div id='" + set[i].name + "_coverage_div' class='feature_tracks singleline' style=\"position:relative; display:none; top:0px; overflow-y: auto; overflow-x: hidden\" > " + set[i].name + "</div>")


                    jQuery("#" + set[i].name + "_coverage_wrapper").resizable({
                        handles: "s",
                        minHeight: "50px",
                        borderBottom: '1px solid black',
                        alsoResize: "#" + set[i].name + "_coverage_div"
                    });


                    window['track_list' + set[i].name + "_coverage"] = {
                        name: set[i].name + "_coverage",
                        id: set[i].name + "_coverage",
                        display_label: set[i].name + "_coverage",
                        desc: set[i].name + "_coverage",
                        disp: 1,
                        merge: 0,
                        label: 0,
                        graph: 0,
                        graphtype: null,
                        label_show: true
                    }

                    var slt = {
                        name: set[i].name + "_coverage",
                        id: set[i].name + "_coverage",
                        display_label: set[i].name + "_coverage",
                        desc: set[i].name + "_coverage",
                        disp: 1,
                        merge: 0,
                        label: 0,
                        graph: 0,
                        graphtype: null,
                        label_show: true
                    }
                    single_line_track_list.push(slt);

                    jQuery("#line_tracklist").append(set[i].name + " <br> <input type=\"checkbox\" class=\"singleLineCheckbox\" disabled=\"disabled\" id='" + set[i].name + "_coverageCheckbox' name='" + set[i].name + "_coverageCheckbox'  value=" + set[i].name + "_coverage>" + set[i].name + " coverage <br>")


                    //deletion track

                    jQuery("#tracks").append("<div id='" + set[i].name + "_deletion_wrapper' class='feature_tracks singleline' style='position:relative;  display:none; max-height:110px; overflow: hidden;'>" +
                        "</div>");


                    jQuery("#" + set[i].name + "_deletion_wrapper").append("<div align='left' class='handle'>" +
                        "<table>" +
                        "<tr>" +
                        "<td><b>" + set[i].name + "_deletion</b></td>" +
                        "</tr>" +
                        "</table>" +
                        "</div>" +
                        "<div id='" + set[i].name + "_deletion_div' class='feature_tracks singleline' style=\"position:relative; display:none; top:0px; overflow-y: auto; overflow-x: hidden\" > " + set[i].name + "</div>")


                    jQuery("#" + set[i].name + "_deletion_wrapper").resizable({
                        handles: "s",
                        minHeight: "50px",
                        borderBottom: '1px solid black',
                        alsoResize: "#" + set[i].name + "_deletion_div"
                    });


                    window['track_list' + set[i].name + "_deletion"] = {
                        name: set[i].name + "_deletion",
                        id: set[i].name + "_deletion",
                        display_label: set[i].name + "_deletion",
                        desc: set[i].name + "_deletion",
                        disp: 1,
                        merge: 0,
                        label: 0,
                        graph: 0,
                        graphtype: null,
                        label_show: true
                    }

                    var slt = {
                        name: set[i].name + "_deletion",
                        id: set[i].name + "_deletion",
                        display_label: set[i].name + "_deletion",
                        desc: set[i].name + "_deletion",
                        disp: 1,
                        merge: 0,
                        label: 0,
                        graph: 0,
                        graphtype: null,
                        label_show: true
                    }
                    single_line_track_list.push(slt);

                    jQuery("#line_tracklist").append("<input type=\"checkbox\" class=\"singleLineCheckbox\" disabled=\"disabled\"  id='" + set[i].name + "_deletionCheckbox' name='" + set[i].name + "_deletionCheckbox'  value=" + set[i].name + "_deletion>" + set[i].name + " deletion <br>")

                    //Consensus
                    jQuery("#tracks").append("<div id='" + set[i].name + "_consensus_wrapper' class='feature_tracks singleline' style='position:relative; display:none; max-height:110px; overflow: hidden;'>" +
                        "</div>");


                    jQuery("#" + set[i].name + "_consensus_wrapper").append("<div align='left' class='handle'>" +
                        "<table>" +
                        "<tr>" +
                        "<td><b>" + set[i].name + "_consensus</b></td>" +
                        "</tr>" +
                        "</table>" +
                        "</div>" +
                        "<div id='" + set[i].name + "_consensus_div' class='feature_tracks singleline' style=\"position:relative; display:none; top:0px; overflow-y: auto; overflow-x: hidden\" > " + set[i].name + "</div>")


                    jQuery("#" + set[i].name + "_consensus_wrapper").resizable({
                        handles: "s",
                        minHeight: "50px",
                        borderBottom: '1px solid black',
                        alsoResize: "#" + set[i].name + "_consensus_div"
                    });

                    window['track_list' + set[i].name + "_consensus"] = {
                        name: set[i].name + "_consensus",
                        id: set[i].name + "_consensus",
                        display_label: set[i].name + "_consensus",
                        desc: set[i].name + "_consensus",
                        disp: 1,
                        merge: 0,
                        label: 0,
                        graph: 0,
                        graphtype: null,
                        label_show: true
                    }

                    var slt = {
                        name: set[i].name + "_consensus",
                        id: set[i].name + "_consensus",
                        display_label: set[i].name + "_consensus",
                        desc: set[i].name + "_consensus",
                        disp: 1,
                        merge: 0,
                        label: 0,
                        graph: 0,
                        graphtype: null,
                        label_show: true
                    }
                    single_line_track_list.push(slt);

                    jQuery("#line_tracklist").append("<input type=\"checkbox\" class=\"singleLineCheckbox\" disabled=\"disabled\" id='" + set[i].name + "_consensusCheckbox' name='" + set[i].name + "_consensusCheckbox'  value=" + set[i].name + "_consensus>" + set[i].name + " consensus <br><br>")

                }
                if (preset_line != null) {
                    jQuery("#singleLineRadio").prop("checked", true)
                    jQuery("#singleline_tracks").css("visibility", "visible");
                    jQuery(".singleline").show()
                    track_list = track_list.concat(single_line_track_list);
                    console.log(track_list)
                    jQuery(".singleLineCheckbox").prop("checked", true);
                    jQuery("#lines_searchable_list").val(preset_line)
                    loadSelectedLine()
                }


            }
        })

}

function prepare_searchable_track_list(Tracklist, i) {
    console.log("prepare_searchable_track_list");

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
        ensembl: Tracklist[i].ensembl ? Tracklist[i].ensembl : null
    }

    track_list.push(Tracklist[i]);

    if (document.getElementById("filetrackgroup") == null) {
        jQuery("#tracklist").append("<div style='padding: 5px; margin: 10px; position: relative; border: 1px solid lightgray; top: 10px' id='filetrackgroup'> </div>")

    }
}

function prepare_track_list(Tracklist, i) {
    console.log("prepare track list");

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
        ensembl: Tracklist[i].ensembl ? Tracklist[i].ensembl : null
    }

    tracks_div(Tracklist[i].name);
    tracks_css(Tracklist[i].name);

    var checked = "checked"
    if (Tracklist[i].disp == false || Tracklist[i].disp == 0) {
        checked = "";
    }

    if (Tracklist[i].web && Tracklist[i].web.trackgroup) {

        if (document.getElementById("group" + Tracklist[i].web.trackgroup) == null) {
            jQuery("#tracklist").append("<div style='padding: 5px; margin: 10px; position: relative; border: 1px solid lightgray; top: 10px' id='group" + Tracklist[i].web.trackgroup + "'> <b>" + Tracklist[i].web.trackgroup + "</b> <p></div>")
            jQuery("#mergetracklist").append("<div style='padding: 5px;  margin: 10px; position: relative; border: 1px solid lightgray; top: 10px' id='mergegroup" + Tracklist[i].web.trackgroup + "'><b>" + Tracklist[i].web.trackgroup + "</b> <p></div>")
            // jQuery('#group'+Tracklist[i].web.trackgroup).html("<input id=\"SearchBar"+Tracklist[i].web.trackgroup+"\" placeholder=\"Search for options..\" type=\"text\" oninput=\"updateCheckboxes('SearchBar"+Tracklist[i].web.trackgroup+',','"+Tracklist[i].web.trackgroup+"')\" autocomplete=\"off\">")

        }

        jQuery("<div>").attr({
            'style': "position: relative; width: 70%; word-wrap: break-word;",
            'id': Tracklist[i].name + "span",
            'title': Tracklist[i].desc
        }).html("<input type=\"checkbox\" " + checked + " id='" + Tracklist[i].name + "Checkbox' name='" + Tracklist[i].name + "Checkbox' onClick=loadTrackAjax(\"" + Tracklist[i].id + "\",\"" + Tracklist[i].name + "\"); value=" + Tracklist[i].name + " >" + Tracklist[i].display_label)
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
        }).html("<input type=\"checkbox\" " + checked + " id='" + Tracklist[i].name + "Checkbox' name='" + Tracklist[i].name + "Checkbox' onClick=loadTrackAjax(\"" + Tracklist[i].id + "\",\"" + Tracklist[i].name + "\"); value=" + Tracklist[i].name + " >" + Tracklist[i].display_label)
            .appendTo("#nogroup-table");

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
    // else {
    //     if (document.getElementById("group" + Tracklist[i].web.trackgroup) == null) {
    //         jQuery("#tracklist").append(
    //             "<div style='padding: 5px; margin: 10px; position: relative; border: 1px solid lightgray; top: 10px' id='group" + Tracklist[i].web.trackgroup + "'> " +
    //             "<b>" + Tracklist[i].web.trackgroup + "</b> <p> <select class=\"js-example-basic-multiple\" multiple='multiple' id='searchable_tracks'></select>" +
    //             "</div>")
    //     }
    //     var option = document.createElement("option");
    //     option.text = Tracklist[i].name;
    //     option.value = Tracklist[i].name;
    //     var select = document.getElementById("searchable_tracks");
    //     select.appendChild(option);
    //
    // }
    //
    // if (last == true && jQuery('#searchable_tracks')) {
    //     jQuery('#searchable_tracks').select2({
    //         placeholder: "Search here..",
    //         maximumSelectionLength: 4
    //     });
    //     jQuery('#searchable_tracks').on('select2:unselecting', function (e) {
    //         var r = confirm("Do you want to disable " + e["params"]["args"]["data"]["text"])
    //
    //         if (r == false) {
    //             e.preventDefault();
    //         } else {
    //             var item = e["params"]["args"]["data"]["text"]
    //             jQuery("#" + item + "_wrapper").fadeOut();
    //             window[item] = []
    //             return true;
    //         }
    //     })
    //     jQuery('#searchable_tracks').on('select2:select', function (evt) {
    //         var args = JSON.stringify(evt.params, function (key, value) {
    //             var item = evt["params"]["data"]["element"]["text"]
    //             tracks_div(item)
    //             tracks_css(item);
    //             loadTrackAjax(window['track_list' + item].id, item);
    //         });
    //     })
    //     console.log("last 3")
    // }
}


// Generate automated css classes for tracks

function tracks_css(trackname) {
    console.log("tracks_css");

    var track = window["track_list" + trackname]

    if (jQuery('.' + track.display_label).length <= 0) {
        if (trackname) {
            prepare_track_css(trackname)
        } else {
            for (var i = 0; i < track_list.length; i++) {
                prepare_track_css(track_list[i])
            }
        }
    }
}

function prepare_track_css(trackname) {
    console.log("prepare_track_css");

    var track = window["track_list" + trackname]
    if (track.web && track.web.colour) {

        if (track.name.toLowerCase().indexOf("snp") >= 0) {

        } else if (track.web.source == "file" && (track.name.toLowerCase().indexOf("gene") >= 0 || track.name.toLowerCase().indexOf("gff") >= 0)) {

            jQuery("<style type='text/css'> ." + track.display_label + "_exon" + "{ background:" + track.web.colour + ";} </style>").appendTo("head");
            jQuery("<style type='text/css'> ." + track.display_label + "_utr" + "{  background:" + track.web.colour + ";} </style>").appendTo("head");
            jQuery("<style type='text/css'> ." + track.display_label + "_graph{ border:1px solid black; background:" + track.web.colour + ";} </style>").appendTo("head");
            jQuery("<style type='text/css'> ." + track.display_label + "_heatgraph{  background:" + track.web.colour + ";} </style>").appendTo("head");

        } else if (Tracklist[i].web.source == "file") {
            jQuery("<style type='text/css'> ." + Tracklist[i].display_label + "" + "{ fill:" + Tracklist[i].web.colour + "; stroke: " + Tracklist[i].web.colour + "; background: " + Tracklist[i].web.colour + ";} </style>").appendTo("head");
        } else if (Tracklist[i].name.toLowerCase().indexOf("gene") >= 0 || Tracklist[i].name.toLowerCase().indexOf("gff") >= 0) {

            jQuery("<style type='text/css'> ." + track.display_label + "_exon" + "{ background:" + track.web.colour + "; } </style>").appendTo("head");
            jQuery("<style type='text/css'> ." + track.display_label + "_utr" + "{  background:" + track.web.colour + "; opacity: 0.5;} </style>").appendTo("head");
            jQuery("<style type='text/css'> ." + track.display_label + "_graph{ border:1px solid black; background:" + track.web.colour + ";} </style>").appendTo("head");
            jQuery("<style type='text/css'> ." + track.display_label + "_heatgraph{ background:" + track.web.colour + ";} </style>").appendTo("head");

        } else {

            jQuery("<style type='text/css'> ." + track.display_label + "{ background:" + track.web.colour + ";} </style>").appendTo("head");
            jQuery("<style type='text/css'> ." + track.display_label + "_graph { border:1px solid black; background:" + track.web.colour + ";} </style>").appendTo("head");
            jQuery("<style type='text/css'> ." + track.display_label + "_heatgraph {  background:" + track.web.colour + ";} </style>").appendTo("head");

        }
    } else {

        if (track.name.toLowerCase().indexOf("snp") >= 0) {

        } else if (track.name.toLowerCase().indexOf("gene") >= 0 || track.name.toLowerCase().indexOf("gff") >= 0) {
            jQuery("<style type='text/css'> ." + track.display_label + "_exon" + "{ background: green; } </style>").appendTo("head");
            jQuery("<style type='text/css'> ." + track.display_label + "_utr" + "{ background: steelblue; opacity: 0.5;} </style>").appendTo("head");
            jQuery("<style type='text/css'> ." + track.display_label + "_graph { border:1px solid black; background:green;} </style>").appendTo("head");
            jQuery("<style type='text/css'> ." + track.display_label + "_heatgraph {  background:green;} </style>").appendTo("head");

        } else {
            var colour = "";

            if (track.name.toLowerCase().indexOf("contig") >= 0) {
                colour = "#ff8c00";
            } else if (track.name.toLowerCase().indexOf("est") >= 0) {
                colour = "#556b2f";
            } else if (track.name.toLowerCase().indexOf("clone") >= 0) {
                colour = "#90ee90";
            } else if (track.name.toLowerCase().indexOf("align") >= 0) {
                colour = "#a52a2a";
            } else if (track.name.toLowerCase().indexOf("sam") >= 0 || track.name.toLowerCase().indexOf("bam") >= 0) {
                colour = "blue";
            } else if (track.name.toLowerCase().indexOf("repeat") >= 0) {
                colour = "gray";
            } else {
                colour = "green";
            }

            jQuery("<style type='text/css'> ." + track.display_label + "{ background:" + colour + ";} </style>").appendTo("head");
            jQuery("<style type='text/css'> ." + track.display_label + "_graph{ border:1px solid black; background:" + colour + ";} </style>").appendTo("head");
            jQuery("<style type='text/css'> ." + track.display_label + "_heatgraph{ background:" + colour + ";} </style>").appendTo("head");
        }
    }
}


function toggleLeftInfo(div, id) {
    console.log(arguments.callee.name);

    if (jQuery(div).hasClass("toggleRight")) {
        jQuery(div).removeClass("toggleRight").addClass("toggleRightDown");
    } else {
        jQuery(div).removeClass("toggleRightDown").addClass("toggleRight");
    }
    jQuery("#" + id).toggle("blind", {}, 500);
}

function loadDefaultTrack(tracklist) {
    console.log("loadDefaultTrack");
    var Tracklist = tracklist;
    var cookietest = []
    if (JSON.parse(jQuery.cookie('trackslist')).length > 1) {
        cookietest = JSON.parse(jQuery.cookie('trackslist'));
    }

    var group = false;
    var line = ""
    if (jQuery(("input[name='lineRadioBox']:checked")).val() == "singlelineRadiobox") {
        group = true;
        line = jQuery("#lines_searchable_list").val()
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
            deltaWidth = parseInt(end - start) / parseInt(maxLen);
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
                    'url': ajaxurl,
                    'group': group,
                    'line': line
                },
                {
                    'doOnSuccess': function (json) {
                        var trackname = json.name;

                        if (json.type == "graph") {
                            window['track_list' + json.name].graph = "true";
                            window['track_list' + json.name].graphtype = json.graphtype;
                        } else {
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

                deltaWidth = parseInt(end - start) / parseInt(maxLen);

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
                        'url': ajaxurl,
                        'group': group,
                        'line': line
                    },
                    {
                        'doOnSuccess': function (json) {
                            var trackname = json.name;

                            if (json.type == "graph") {
                                window['track_list' + json.name].graph = "true";
                                window['track_list' + json.name].graphtype = json.graphtype;
                            } else {
                                window['track_list' + json.name].graph = "false";
                            }
                            window[trackname] = json[trackname];
                            trackToggle(trackname);
                        }
                    });
                jQuery('#' + Tracklist[i].name + 'Checkbox').attr('checked', true);
                window['track_list' + track_list[i].name].disp = 1

                return false; // stops the loop
            } else if (v.name == Tracklist[i].name && v.disp == 0) {
                window["track_list" + Tracklist[i].name].disp = 0;
            }
        });
        continue;
    }
}

function mergeTrackList(trackName) {
    console.log("mergeTrackList");

    if (jQuery("#" + trackName + "Checkbox").is(':checked')) {
        jQuery("#" + trackName + "mergedCheckbox").removeAttr("disabled");
    } else {
        jQuery("#" + trackName + "mergedCheckbox").attr("disabled", true);

        mergeTrack(trackName);
    }
}

function getTracks() {
    console.log("getTracks");

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
    console.log("getTracklist");

    var tracks = [];
    for (var i = 0; i < track_list.length; i++) {

        tracks.push(window["track_list" + track_list[i].name]);
    }

    return tracks;
}

function getEditedTracks() {
    console.log(arguments.callee.name);

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
    console.log(arguments.callee.name);

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
    console.log(arguments.callee.name);

    for (var i = 0; i < tracks.length; i++) {
        window[tracks[i].trackName] = tracks[i].child;
    }
}

function loadRemovedTracks(tracks) {
    console.log(arguments.callee.name);

    for (var i = 0; i < tracks.length; i++) {
        window[tracks[i].trackName] = tracks[i].child;
    }
}

function selectAllCheckbox() {
    console.log(arguments.callee.name);

    if (jQuery("#selectAllCheckbox").is(':checked')) {
        jQuery("#tracklist input").each(function () {
            if (jQuery(this).is(':checked')) {
                //    do nothing
            } else {
                jQuery(this).attr('checked', 'checked');
                eval(jQuery(this).attr('onClick'));
            }
        })
    } else {
    }
}

function unSelectAllCheckbox() {
    console.log(arguments.callee.name);

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


