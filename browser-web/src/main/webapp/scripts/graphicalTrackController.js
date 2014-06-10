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

function dispGraph(div, trackName, className) {
    var track_html = "";
    jQuery(div).html("");

    if (jQuery('input[name=' + trackName + 'mergedCheckbox]').is(':checked')) {
        jQuery(div).fadeOut();
        jQuery("#" + trackName + "_wrapper").fadeOut();
        div = "#mergedtrack";
        jQuery("#mergedtrack").fadeIn();
        jQuery("#mergedtrack_wrapper").fadeIn();

        track_html.push("(" + merged_track_list + ")");
        jQuery("#mergelabel").html(track_html.join(""));

        className = " mergedtrack " + className;
        labelclass = "Merged_Track";
    }
    else {
        jQuery(div).fadeIn();
        jQuery("#" + track + "_wrapper").fadeIn();
    }

    if (!window[trackName] || window[trackName] == "loading") {
        jQuery(div).html("<img style='position: relative; left: 50%; ' src='./images/browser/loading_big.gif' alt='Loading'>")
        jQuery(div).fadeIn();
        jQuery("#" + trackName + "_wrapper").fadeIn();

    }
    else {
        var track = window[trackName];
        var partial = (parseInt(getEnd()) - parseInt(getBegin())) / 2;
        var start = parseInt(getBegin()) - parseInt(partial)
        var end = parseInt(getEnd()) + parseInt(partial);
        var maxLen_temp = jQuery("#canvas").css("width");

        var newStart_temp = getBegin();
        var newEnd_temp = getEnd();

        if (track[0]) {
            track = jQuery.grep(track, function (element, index) {
                return element.start >= start && element.start <= end; // retain appropriate elements
            });
        }


        var total = 0;
        var max = Math.max.apply(Math, track.map(function (o) {
            return o.graph;
        }));

        var track_len = track.length;

        while (track_len--) {
            var track_start = track[track_len].start;
            var track_stop = track[track_len].end;

            var startposition = ((track_start - newStart_temp)) * parseFloat(maxLen_temp) / (newEnd_temp - newStart_temp) + parseFloat(maxLen_temp) / 2;
            var stopposition = (track_stop - track_start ) * parseFloat(maxLen_temp) / (newEnd_temp - newStart_temp);

            jQuery("<div>").attr({
                'id': trackName + "" + track_len,
                'class': "graph " + className + "_graph",
                'style': "bottom:0px; height: " + (track[track_len].graph * 45 / max) + "px; LEFT:" + startposition + "px; width:" + (stopposition - 1) + "px",
                'title': track_start + ":" + track_stop + "->" + track[track_len].graph,
                'onClick': "setBegin(" + track[track_len].start + ");setEnd(" + track[track_len].end + ");jumpToSeq();"
            }).appendTo(div);

        }
    }
}
function dispGraphHeat(div, trackName, className) {
    var track_html = "";
    jQuery(div).html("");

    if (jQuery('input[name=' + trackName + 'mergedCheckbox]').is(':checked')) {
        jQuery(div).fadeOut();
        jQuery("#" + trackName + "_wrapper").fadeOut();
        div = "#mergedtrack";
        jQuery("#mergedtrack").fadeIn();
        jQuery("#mergedtrack_wrapper").fadeIn();

        track_html.push("(" + merged_track_list + ")");
        jQuery("#mergelabel").html(track_html.join(""));

        className = " mergedtrack " + className;
        labelclass = "Merged_Track";
    }
    else {
        jQuery(div).fadeIn();
        jQuery("#" + track + "_wrapper").fadeIn();
    }

    if (!window[trackName] || window[trackName] == "loading") {
        jQuery(div).html("<img style='position: relative; left: 50%; ' src='./images/browser/loading_big.gif' alt='Loading'>")
        jQuery(div).fadeIn();
        jQuery("#" + trackName + "_wrapper").fadeIn();

    }
    else {
        var track = window[trackName];
        var partial = (parseInt(getEnd()) - parseInt(getBegin())) / 2;
        var start = parseInt(getBegin()) - parseInt(partial)
        var end = parseInt(getEnd()) + parseInt(partial);
        var maxLen_temp = jQuery("#canvas").css("width");

        var newStart_temp = getBegin();
        var newEnd_temp = getEnd();

        if (track[0]) {
            track = jQuery.grep(track, function (element, index) {
                return element.start >= start && element.start <= end; // retain appropriate elements
            });
        }


        var total = 0;
        var max = Math.max.apply(Math, track.map(function (o) {
            return o.graph;
        }));

        var track_len = track.length;

        while (track_len--) {
            var track_start = track[track_len].start;
            var track_stop = track[track_len].end;

            var startposition = (track_start - newStart_temp) * parseFloat(maxLen_temp) / (newEnd_temp - newStart_temp) + parseFloat(maxLen_temp) / 2;
            var stopposition = (track_stop - track_start ) * parseFloat(maxLen_temp) / (newEnd_temp - newStart_temp);

            jQuery("<div>").attr({
                'id': trackName + "" + track_len,
                'class': "heatgraph " + className + "_heatgraph",
                'style': "bottom:0px; height: 70px; opacity: " + (track[track_len].graph / max).toFixed(1) + "; LEFT:" + startposition + "px; width:" + (stopposition - 1) + "px",
                'title': track_start + ":" + track_stop + "->" + track[track_len].graph
            }).appendTo(div);

        }
    }
}

function sortResults(prop, asc, array) {
    array = array.sort(function (a, b) {
        if (asc) return (a[prop] > b[prop]);
        else return (b[prop] > a[prop]);
    });
    return array;
}

