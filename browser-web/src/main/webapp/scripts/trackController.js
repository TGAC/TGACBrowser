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
var layers, mergedTracklist;

function trackStatement(trackClass, track, startposition, stopposition, a, top, j) {
    return "<div class='" + trackClass + "'  " +
        "STYLE=\"position:absolute; cursor:pointer; height: 10px; z-index: 100; TOP:" + top + "px; LEFT:" + startposition + "px; " +
        "width:" + (stopposition) + "px \" onclick=trackClick(\"" + track + "\",\"" + a + "\",\"" + j + "\");> </div>";
}

function getStart(track_start) {
    return (track_start - newStart_temp) * parseFloat(maxLen) / (newEnd_temp - newStart_temp) + parseFloat(maxLen) / 2;
}

function toogleLabel(trackName) {
    if (window['track_list' + trackName].label == 1) {
        window['track_list' + trackName].label = 0
    }
    else {
        window['track_list' + trackName].label = 1

    }
    jQuery(".label" + trackName).toggle();
}

function toogleTrackView(trackName) {
    if (window['track_list' + trackName].expand == 1) {
        window['track_list' + trackName].expand = 0
    }
    else {
        window['track_list' + trackName].expand = 1
    }

    trackToggle(trackName);
}

function showObject(start, end, objStart, objEnd) {
    var show = false;
    if (parseInt(start) <= parseInt(objStart) && parseInt(end) >= parseInt(objEnd)) {
        show = true;
    }
    else if (parseInt(start) <= parseInt(objEnd) && parseInt(end) >= parseInt(objEnd)) {
        show = true;
    }

    else if (parseInt(start) <= parseInt(objStart) && parseInt(end) >= parseInt(objStart)) {
        show = true;
    }

    else {
        show = parseInt(start) >= parseInt(objStart) && parseInt(end) <= parseInt(objEnd);
    }
    return show;
}


function trackClick(track, i, j) {

    if (ctrldown) {
        if (window[track][i].id == grouplastid || grouplastid == null) {
            grouplist.push(window[track][i].transcript[j].id);
            grouplastid = window[track][i].id;
            grouptrack = track;
        }
    }
    else {
        newpopup(track, i, j)
    }


}

function trackmouseover(track, i, j) {

    jQuery("#trackmouseoverhead").html("<h2>" + window[track][i].desc + "</h2>");
    jQuery("#trackmouseoverbody").html(window[track][i].transcript.length + "transcript for Gene");

    if (mouseX + jQuery("#trackmouseover").width() > jQuery("#main1").width()) {
        jQuery("#trackmouseover").css({"left": mouseX - jQuery("#trackmouseover").width() - 10});
        jQuery("#trackmouseover").css({"top": (mouseY + 10)});
    }
    else {
        jQuery("#trackmouseover").css({"left": (mouseX + 10)});
        jQuery("#trackmouseover").css({"top": (mouseY + 10)});
    }
    jQuery("#trackmouseover").show();
}
function trackmouseout() {
    jQuery("#trackmouseover").hide();
    jQuery("#trackmouseoverhead").html('');
}

function indelClick(query, hit) {
    newBlastpopup(query, hit);
}

function removeTrack(div, track) {
    jQuery('#' + track + 'Checkbox').attr('checked', false);
    jQuery("#" + track + "mergedCheckbox").attr("disabled", true);
    jQuery(div).html();
    jQuery(div).fadeOut();
    jQuery("#" + track + "_wrapper").fadeOut();
    window['track_list' + track].disp = 0
}

function removeMergedTrack() {

    jQuery(track_list).each(function (index) {
        if (jQuery("#" + track_list[index].name + "mergedCheckbox").attr('checked')) {
            window['track_list' + track_list[index].name].disp = 0
            jQuery('input[name=' + track_list[index].name + 'mergedCheckbox]').attr('checked', false);
            trackToggle(track_list[index].name);
        }
    });
    jQuery("#mergedtrack").html("<div id= \"mergelabel\" align='left' class='handle'></div>");
    jQuery("#mergedtrack").fadeOut();
    jQuery("#mergedtrack_wrapper").fadeOut();

}

function toogleLabelMerged() {
    jQuery(track_list).each(function (index) {
        if (jQuery("#" + track_list[index].name + "mergedCheckbox").attr('checked')) {
            if (this.label == 1) {
                window['track_list' + track_list[index].name].label = 0
            }
            else {
                window['track_list' + track_list[index].name].label = 1
            }
        }
    });


    jQuery(".Merged_Track").toggle();
}


function dispBLAST(div, track) {
    jQuery(div).html("<img src=\"./images/browser/dna_helix_md_wm.gif\" alt=\"loading\">");
    var blasts = window[track];
    if (!window[track] || window[track] == "running") {
        jQuery(div).html("<img style='position: relative; left: 50%; ' src='./images/browser/loading_big.gif' alt='Loading'>")
        jQuery(div).fadeIn();
    }
    else if (blasts[0] == "No hits found.") {
        alert(blasts);
        jQuery('#' + track + 'Checkbox').attr('checked', false);
        jQuery(div).html();
        jQuery(div).fadeOut();
        delete window['blasttrack'];
        delete track_list.splice(track_list.length, 1);
        jQuery("#blasttrack_div").remove();
        jQuery("#blastcheck").remove();
        jQuery("#blastcheckmerge").remove();
    }

    else {

        if (blasts.length > 0) {
            var track_html = "<div align='left' class='handle'><table><tr><td><b>" + track + "</b>(" + blasts.length + ")</td><td><div title='Label Toggle' class=\"closehandle ui-icon ui-icon-comment\" onclick=toogleLabel(\"" + track + "\");> </div></td><td><div title='Close' class='closehandle ui-icon ui-icon-close' onclick=removeTrack(\"" + div + "\",\"" + track + "\");></div></td></tr></table></div>";


            var layers = blasts.length + 1;
            var maxLen_temp = jQuery("#canvas").css("width");
            for (var i = 0; i < blasts.length; i++) {
                var blast_start = blasts[i].start;
                var blast_stop = blasts[i].end;
                var blast_desc = blasts[i].desc.replace(">", "");
                var score = blasts[i].score;

                if (blast_start > blast_stop) {
                    blast_start = blasts[i].end;
                    blast_stop = blasts[i].start;
                }
                var partial = (getEnd() - getBegin()) / 2;
                var start = getBegin() - partial;

                var end = parseInt(getEnd()) + parseInt(partial);
                var show = showObject(start, end, blast_start, blast_stop);
                var border = 0;
                if (blasts[i].flag) {
                    border = 1
                }
                var image = "./images/browser/blastred.jpg";
                if (parseInt(score) < 40) {
                    image = "./images/browser/blastblack.jpg";
                }
                else if (parseInt(score) < 50) {
                    image = "./images/browser/blastblue.jpg";
                }
                else if (parseInt(score) < 80) {
                    image = "./images/browser/blastgreen.jpg";
                }
                else if (parseInt(score) < 200) {
                    image = "./images/browser/blastpink.jpg";
                }
                else {
                    image = "./images/browser/blastred.jpg";
                }

                if (show) {
                    var top = ((i + 1) % layers) * 15 + 10;
                    jQuery(div).fadeIn();
                    var startposition = (blast_start - getBegin()) * parseFloat(maxLen_temp) / (getEnd() - getBegin()) + parseFloat(maxLen_temp) / 2;
                    var stopposition = (blast_stop - blast_start) * parseFloat(maxLen_temp) / (getEnd() - getBegin());
                    if (stopposition < 2) {
                        stopposition = 2;
                    }
                    track_html += "<div class='tracks_image' STYLE=\"position:absolute;  TOP:" + top + "px; " +
                        "LEFT:" + startposition + "px \" " +
                        "onclick=trackClick(\"" + track + "\",\"" + i + "\"); > " +
                        "<img class='tracks_image' id=\"" + track + blast_start + ":" + blast_stop + "\" " +
                        "STYLE=\"WIDTH:" + stopposition + "px; height: 10px; border:" + border + "px solid black; cursor: pointer  \" " +
                        "src=\"" + image + "\" alt=" + startposition + "-" + stopposition + "  " +
                        "title=" + track + ":" + blast_start + "-" + blast_stop + ",Score:" + score + " > <br> </div>";
                    track_html = track_html.replace("undefined", "");
                    track_html += dispBLASTindel(i, blast_start);
                }
            }
            if (jQuery('input[name=' + track + 'mergedCheckbox]').is(':checked')) {
                jQuery(div).fadeOut();
                jQuery(div).html();
                jQuery("#mergedtrack").css('height', (parseInt(layers * 15) + parseInt(50)));
                jQuery("#mergedtrack").append(track_html);
                jQuery("#mergedtrack").fadeIn();
            }
            else {
                jQuery(div).css('height', (parseInt(layers * 15) + parseInt(50)));
                jQuery(div).fadeIn();
                jQuery("#blasttrack_wrapper").fadeIn();
                if (layers == 1) {
                    track_html = track_html.replace(/tracks_image/g, 'merged_tracks_image')
                }
                jQuery(div).html(track_html);
            }
        }
        else {
            alert(track + " not Found");
            jQuery('#' + track + 'Checkbox').attr('checked', false);
            jQuery(div).html();
            jQuery(div).fadeOut();
        }
    }
}

function dispBLASTindel(j, blast_start) {

    var blastindel = window["blasttrack"][j].indels;
    if (blastindel.length > 0) {
        var track_html = "";
        var layers = window["blasttrack"].length + 1;
        var maxLen_temp = jQuery("#canvas").css("width");

        for (var i = 0; i < blastindel.length; i++) {
            var indel_start = parseInt(blastindel[i].position) + parseInt(blast_start) - 1;
            var indel_stop = (parseInt(indel_start) + 1);
            var partial = (getEnd() - getBegin()) / 2;
            var start = getBegin() - partial;

            var end = parseInt(getEnd()) + parseInt(partial);
            var show = showObject(start, end, indel_start, indel_stop);

            var image = "./images/browser/indel.png";

            if (show) {
                var top = ((j + 1) % layers) * 15 + 10;
                var startposition = (indel_start - getBegin()) * parseFloat(maxLen_temp) / (getEnd() - getBegin()) + parseFloat(maxLen_temp) / 2;
                var stopposition = (indel_stop - indel_start) * parseFloat(maxLen_temp) / (getEnd() - getBegin());
                track_html += "<div class='tracks_image' onclick=indelClick(\"" + blastindel[i].query + "\",\"" + blastindel[i].hit + "\"); " +
                    "STYLE=\"position:absolute; z-index: 999; TOP:" + top + "px; LEFT:" + startposition + "px \"> " +
                    "<img class='tracks_image' \" STYLE=\"WIDTH:" + stopposition + "px; height: 10px; cursor: pointer \" " +
                    "src=\"" + image + "\" alt=" + startposition + "-" + stopposition + " title= \"indel at " + indel_start + " \" ></div>";
                track_html = track_html.replace("undefined", "");
            }
        }
        return track_html;
    }
    else {
    }
}


function dispGenes(div, track, expand, className) {
    var d = new Date();
    console.log(d.getMinutes() + ":" + d.getSeconds() + ":" + d.getMilliseconds())

    var track_div = document.getElementById(track + "_div")

    var new_div = document.createElement("div");
    new_div.style.height = "10px";
    new_div.style.position = "absolute";


    var label_div = document.createElement("div");
    label_div.style.overflow = "hidden"
    label_div.style.textOverflow = "ellipsis";
    label_div.style.zindex = 999;


    var labeltoogle = "in-line;";
    var labelclass = "label" + track;
    var track_html = [];
    var max = 110;

    trackClass = "exon";

    if (window['track_list' + track].label == 0) {
        labeltoogle = "none"
    }

    if (jQuery('input[name=' + track + 'mergedCheckbox]').is(':checked')) {
        jQuery(div).fadeOut();
        jQuery("#" + track + "_wrapper").fadeOut();
        div = "#mergedtrack";
        jQuery("#mergedtrack").fadeIn();
        jQuery("#mergedtrack_wrapper").fadeIn();

        track_html.push("(" + merged_track_list + ")");
        jQuery("#mergelabel").html(track_html.join(""));

        trackClass += " mergedtrack";
        labelclass = "Merged_Track";
    }
    else {
        jQuery(div).fadeIn();
        jQuery("#" + track + "_wrapper").fadeIn();
    }


    var genes = window[track];


    var trackClass;
    if (!window[track] || window[track] == "loading") {
        if (div.indexOf("mergedtrack") <= 0) {
            jQuery(div).html("<img style='position: relative; left: 50%; ' src='./images/browser/loading_big.gif' alt='Loading'>")
            jQuery(div).fadeIn();
            jQuery("#" + track + "_wrapper").fadeIn();
        }
    }
    else if (genes[0] == "getGene no result found") {
        if (div.indexOf("mergedtrack") <= 0) {
            jQuery('#' + track + 'Checkbox').attr('checked', false);
            jQuery(div).html();
            jQuery(div).fadeOut();
            jQuery("#" + track + "_wrapper").fadeOut();
            jQuery("#" + track + "span").remove();
        }
    }

    else {

        var newStart_temp = getBegin();
        var newEnd_temp = getEnd();
        var maxLentemp = jQuery("#canvas").css("width");

        var partial = (newEnd_temp - newStart_temp) / 2;
        var start = newStart_temp - partial;
        var label = "";
        var end = parseInt(getEnd()) + partial;

        track_html = [];
        var j = 0;
        var len = genes.length;

        if (genes[0] == null) {
            if (div.indexOf("mergedtrack") <= 0) {
                track_html = [];
                track_html.push("<font size=4><center>No data available for selected region</center></font>");
                jQuery(div).html(track_html.join(""));
            }
        }
        else if (expand == 0) {
            trackClass = "exon track"
            if (div.indexOf("mergedtrack") <= 0) {
                track_html = [];
                jQuery(div).html(track_html.join(""));
            }
            while (len--) {
                var gene_start;
                var gene_stop;
                if (genes[len].start < genes[len].end) {
                    gene_start = genes[len].start;
                    gene_stop = genes[len].end;
                }
                else {
                    gene_start = genes[len].end;
                    gene_stop = genes[len].start;
                }

                var border = " border-left: 1px solid #000000; border-right: 1px solid #000000;";
                if (genes[len].flag) {
                    if (trackClass.indexOf("geneflag") < 0) {
                        trackClass += " geneflag";
                    }
                }
                else {
                    trackClass = trackClass.replace(" geneflag", "");
                }
                label = genes[len].desc;

                if (genes[len].layer > j) {
                    j = genes[len].layer;
                }
                var top = genes[len].layer * 20 + 15;

                if (max < top) {
                    max = top;
                }
                var startposition = (gene_start - newStart_temp) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp) + parseFloat(maxLentemp) / 2;
                var stopposition = (gene_stop - gene_start + 1) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp);


                var temp = jQuery("<div>").attr({
                    'id': track + "" + len,
                    'class': trackClass + " " + className + "_exon",
                    'style': "TOP:" + top + "px; LEFT:" + startposition + "px; width :" + stopposition + "px;",
                    'title': label,
                    'onClick': "trackClick(\"" + track + "\",\"" + len + "\")",
                    'onmouseOver': "trackmouseover(\"" + track + "\",\"" + len + "\")",
                    'onmouseOut': 'trackmouseout()'
                }).appendTo(div);


                jQuery("<div>").attr({
                    'class': "tracklabel " + labelclass,
                    'style': labeltoogle + " z-index: 999; overflow: hidden;text-overflow: ellipsis;",
                    'title': label
                }).html("<p class='track_label'>" + label + "</p>").appendTo(temp);
            }
        }
        else {
            if (div.indexOf("mergedtrack") <= 0) {
                track_html = [];
                jQuery(div).html(track_html.join(""));
            }
            var len = genes.length;
            while (len--) {
                trackClass = "gene track";
                var transcript_len = genes[len].transcript.length;
                while (transcript_len--) {

                    var gene_start;
                    var gene_stop;
                    if (genes[len].transcript[transcript_len].start < genes[len].transcript[transcript_len].end) {
                        gene_start = genes[len].transcript[transcript_len].start;
                        gene_stop = genes[len].transcript[transcript_len].end;
                    }
                    else {
                        gene_start = genes[len].transcript[transcript_len].end;
                        gene_stop = genes[len].transcript[transcript_len].start;
                    }

                    var gene_desc = genes[len].transcript[transcript_len].desc;
                    var border = " border-left: 1px solid #000000; border-right: 1px solid #000000;";
                    if (genes[len].transcript[transcript_len].flag) {
                        if (trackClass.indexOf("geneflag") < 0) {
                            trackClass += " geneflag";
                        }
                    }
                    else {
                        trackClass = trackClass.replace(" geneflag", "");
                    }
                    label = genes[len].transcript[transcript_len].desc;
                    if (genes[len].transcript[transcript_len].layer > j) {
                        j = genes[len].transcript[transcript_len].layer;
                    }
                    var top = genes[len].transcript[transcript_len].layer * 20 + 15;


                    var startposition = (gene_start - newStart_temp) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp) + parseFloat(maxLentemp) / 2;
                    var stopposition = (gene_stop - gene_start + 1) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp);

                    var clone_new_div= new_div.cloneNode(true)

                    clone_new_div.className = trackClass + " " + className;
                    clone_new_div.id = track + "" + len;
                    clone_new_div.style.width = stopposition + "px";
                    clone_new_div.style.top = top + "px";

                    clone_new_div.style.left = startposition + "px";
                    clone_new_div.title = label;

                    clone_new_div.onclick = (function () {
                        var current_len = len;
                        var current_transcript_len = transcript_len;
                        return function () {
                            trackClick(track, current_len, current_transcript_len);
                        }
                    })();

                    track_div.appendChild(clone_new_div)
                    var clone_label_div= label_div.cloneNode(true)

                    clone_label_div.className = "tracklabel " + labelclass;

                    clone_label_div.style.display = labeltoogle

                    clone_label_div.title = label;
                    clone_label_div.textContent = label

                    new_div.appendChild(clone_label_div);

                    if (stopposition > 10) {
                        dispGeneExon(genes[len].transcript[transcript_len], genes[len].strand, className, div, track);
                    }
                    else {

                        var new_div = document.createElement("div");

                        new_div.style.height = "10px";
                        new_div.style.position = "absolute";

                        new_div.className = "exon " + className + "_exon";
                        new_div.style.width = stopposition + "px";
                        new_div.style.top = top + "px";

                        new_div.style.left = startposition + "px";
                        new_div.title = label;

                        track_div.appendChild(new_div)
                    }

                    var clone_new_div= new_div.cloneNode(true)



                    clone_new_div.className = "track";
                    clone_new_div.style.width = stopposition + "px";
                    clone_new_div.style.top = top + "px";

                    clone_new_div.style.left = startposition + "px";
                    clone_new_div.style.zIndex = "999"

                    clone_new_div.title = label;

                    clone_new_div.onclick = (function () {
                        var current_len = len;
                        var current_transcript_len = transcript_len;

                        return function () {
                            trackClick(track, current_len, current_transcript_len);
                        }
                    })();
                    track_div.appendChild(clone_new_div)


                }
            }
        }
    }
    max = parseInt(jQuery(div)[0].scrollHeight) + 50;

    jQuery("#" + track + "_wrapper").css("max-height", max);


    if (max > parseInt(jQuery("#" + track + "_wrapper").css("height"))) {
        jQuery("#" + track + "_wrapper").children(".ui-resizable-handle").addClass("resize-arrow")
    } else {
        jQuery("#" + track + "_wrapper").children(".ui-resizable-handle").removeClass("resize-arrow")

    }

    var d = new Date();
    console.log(d.getMinutes() + ":" + d.getSeconds() + ":" + d.getMilliseconds())

}

function dispGeneExon(track, genestrand, className, div, trackName) {

    var track_div = document.getElementById(trackName + "_div")


    var trackClass = "exon " + className + "_exon";
    var utrtrackClass = "utr " + className + "_utr";


    var geneexons = sortResults("start", "asc", track.Exons);
    var genetranscript = track;


    if (geneexons.length > 0) {
        var track_html = "";
        var last = null, current = null;
        var strand = genestrand;

        var spanclass = "forward";

        if (strand == -1 || strand == "-1") {
            spanclass = "reverse";
        }

        var newStart_temp = getBegin();
        var newEnd_temp = getEnd();
        var maxLentemp = parseFloat(jQuery("#canvas").css("width"));

        var partial = (newEnd_temp - newStart_temp) / 2;
        var start = newStart_temp - partial;
        var end = newEnd_temp + partial;
        var exon_len = geneexons.length;
        var last_exon = null;
        var startposition = 0;
        var stopposition = 0;
        while (exon_len--) {

            var exon_start;
            var exon_stop;
            if (geneexons[exon_len].start < geneexons[exon_len].end) {
                exon_start = geneexons[exon_len].start;
                exon_stop = geneexons[exon_len].end;
            }
            else {
                exon_start = geneexons[exon_len].end;
                exon_stop = geneexons[exon_len].start;
            }

            var top = genetranscript.layer * 20 + 15;
            current = exon_start;
            var transcript_start;
            var transcript_end;

            if (genetranscript.transcript_start < genetranscript.transcript_end) {
                transcript_start = genetranscript.transcript_start;
                transcript_end = genetranscript.transcript_end;
            }
            else {
                transcript_start = genetranscript.transcript_start;
                transcript_end = genetranscript.transcript_end;
            }


            if (transcript_start && transcript_end) {
                if (exon_start > transcript_end && exon_stop > transcript_end) {
                    startposition = ((exon_start - newStart_temp)) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp) + parseFloat(maxLentemp) / 2;
                    stopposition = (exon_stop - exon_start ) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp);


                    var new_div = document.createElement("div");
                    new_div.className = utrtrackClass;
                    new_div.style.width = stopposition + "px";
                    new_div.style.top = top + "px";
                    new_div.style.left = startposition + "px";
                    track_div.appendChild(new_div)

                    if (stopposition > 10) {
                        if (spanclass == "forward") {
                            startposition = startposition + (stopposition - 8)

                        }


                        var new_div = document.createElement("div");
                        new_div.className = spanclass;
                        new_div.style.top = top + "px";
                        new_div.style.left = startposition + "px";
                        track_div.appendChild(new_div)
                    }


                    last = current;
                }
                else if (exon_start < transcript_start && exon_stop < transcript_start) {
                    startposition = ((exon_start - newStart_temp)) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp) + parseFloat(maxLentemp) / 2;
                    stopposition = (exon_stop - exon_start) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp);

                    var new_div = document.createElement("div");
                    new_div.className = utrtrackClass;
                    new_div.style.width = stopposition + "px";
                    new_div.style.top = top + "px";
                    new_div.style.left = startposition + "px";
                    track_div.appendChild(new_div)


                    if (stopposition > 10) {
                        if (spanclass == "forward") {
                            startposition = startposition + (stopposition - 8)

                        }

                        var new_div = document.createElement("div");
                        new_div.className = spanclass;
                        new_div.style.top = top + "px";
                        new_div.style.left = startposition + "px";
                        track_div.appendChild(new_div)
                    }

                    last = current;
                }
                else if (exon_start < transcript_start && exon_stop > transcript_end) {
                    startposition = ((exon_start - newStart_temp)) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp) + parseFloat(maxLentemp) / 2;
                    stopposition = (transcript_start - exon_start) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp);

                    var new_div = document.createElement("div");
                    new_div.className = utrtrackClass;
                    new_div.style.width = stopposition + "px";
                    new_div.style.top = top + "px";
                    new_div.style.left = startposition + "px";
                    track_div.appendChild(new_div)

                    if (stopposition > 10) {
                        if (spanclass == "forward") {
                            startposition = startposition + (stopposition - 8)

                        }

                        var new_div = document.createElement("div");
                        new_div.className = spanclass;
                        new_div.style.top = top + "px";
                        new_div.style.left = startposition + "px";
                        track_div.appendChild(new_div)
                    }


                    startposition = ((transcript_end - newStart_temp) - 1) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp) + parseFloat(maxLentemp) / 2;
                    stopposition = (exon_stop - transcript_end + 1) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp);

                    var new_div = document.createElement("div");
                    new_div.className = utrtrackClass;
                    new_div.style.width = stopposition + "px";
                    new_div.style.top = top + "px";
                    new_div.style.left = startposition + "px";
                    track_div.appendChild(new_div)

                    if (stopposition > 10) {
                        if (spanclass == "forward") {
                            startposition = startposition + (stopposition - 8)

                        }

                        var new_div = document.createElement("div");
                        new_div.className = spanclass;
                        new_div.style.top = top + "px";
                        new_div.style.left = startposition + "px";
                        track_div.appendChild(new_div)
                    }


                    startposition = ((transcript_start - newStart_temp)) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp) + parseFloat(maxLentemp) / 2;
                    stopposition = (transcript_end - transcript_start ) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp);
                    jQuery("<div>").attr({
                        'class': trackClass,
                        'style': "TOP:" + top + "px; LEFT:" + startposition + "px; width:" + (stopposition) + "px"
                    }).appendTo(div);

                    if (stopposition > 10) {
                        if (spanclass == "forward") {
                            startposition = startposition + (stopposition - 8)

                        }

                        var new_div = document.createElement("div");
                        new_div.className = spanclass;
                        new_div.style.top = top + "px";
                        new_div.style.left = startposition + "px";
                        track_div.appendChild(new_div)
                    }

                    last = current;
                }
                else if (exon_stop > transcript_start && exon_start < transcript_start) {
                    startposition = ((exon_start - newStart_temp)) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp) + parseFloat(maxLentemp) / 2;
                    stopposition = (transcript_start - exon_start) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp);

                    var new_div = document.createElement("div");
                    new_div.className = utrtrackClass;
                    new_div.style.width = stopposition + "px";
                    new_div.style.top = top + "px";
                    new_div.style.left = startposition + "px";
                    track_div.appendChild(new_div)


                    startposition = ((transcript_start - newStart_temp)) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp) + parseFloat(maxLentemp) / 2;
                    stopposition = (exon_stop - transcript_start ) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp);


                    var new_div = document.createElement("div");
                    new_div.className = trackClass;
                    new_div.style.width = stopposition + "px";
                    new_div.style.top = top + "px";
                    new_div.style.left = startposition + "px";
                    track_div.appendChild(new_div)

                    if (stopposition > 10) {
                        if (spanclass == "forward") {
                            startposition = startposition + (stopposition - 8)
                        }
                        var new_div = document.createElement("div");
                        new_div.className = spanclass;
                        new_div.style.top = top + "px";
                        new_div.style.left = startposition + "px";
                        track_div.appendChild(new_div)
                    }


                    last = current;
                }
                else if (exon_stop > transcript_end && exon_start < transcript_end) {
                    startposition = ((transcript_end - newStart_temp)) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp) + parseFloat(maxLentemp) / 2;
                    stopposition = (exon_stop - transcript_end) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp);


                    var new_div = document.createElement("div");
                    new_div.className = utrtrackClass;
                    new_div.style.width = stopposition + "px";
                    new_div.style.top = top + "px";
                    new_div.style.left = startposition + "px";
                    track_div.appendChild(new_div)

                    if (stopposition > 10) {
                        if (spanclass == "forward") {
                            startposition = startposition + (stopposition - 8)

                        }

                        var new_div = document.createElement("div");
                        new_div.className = spanclass;
                        new_div.style.top = top + "px";
                        new_div.style.left = startposition + "px";
                        track_div.appendChild(new_div)
                    }


                    startposition = ((exon_start - newStart_temp)) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp) + parseFloat(maxLentemp) / 2;
                    stopposition = (transcript_end - exon_start) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp);


                    var new_div = document.createElement("div");
                    new_div.className = trackClass;
                    new_div.style.width = stopposition + "px";
                    new_div.style.top = top + "px";
                    new_div.style.left = startposition + "px";
                    track_div.appendChild(new_div)

                    if (stopposition > 10) {
                        if (spanclass == "forward") {
                            startposition = startposition + (stopposition - 8)

                        }

                        var new_div = document.createElement("div");
                        new_div.className = spanclass;
                        new_div.style.top = top + "px";
                        new_div.style.left = startposition + "px";
                        track_div.appendChild(new_div)
                    }


                    last = current;
                }
                else {
                    startposition = ((exon_start - newStart_temp)) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp) + parseFloat(maxLentemp) / 2;
                    stopposition = (exon_stop - exon_start) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp);


                    var new_div = document.createElement("div");
                    new_div.className = trackClass;
                    new_div.style.width = stopposition + "px";
                    new_div.style.top = top + "px";
                    new_div.style.left = startposition + "px";
                    track_div.appendChild(new_div)

                    if (stopposition > 10) {
                        if (spanclass == "forward") {
                            startposition = startposition + (stopposition - 8)

                        }

                        var new_div = document.createElement("div");
                        new_div.className = spanclass;
                        new_div.style.top = top + "px";
                        new_div.style.left = startposition + "px";
                        track_div.appendChild(new_div)
                    }


                    last = current;
                }
//
            }
            else {
                startposition = ((exon_start - newStart_temp)) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp) + parseFloat(maxLentemp) / 2;
                stopposition = (exon_stop - exon_start) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp);


                var new_div = document.createElement("div");
                new_div.className = trackClass;
                new_div.style.width = stopposition + "px";
                new_div.style.top = top + "px";
                new_div.style.left = startposition + "px";
                track_div.appendChild(new_div)

                if (stopposition > 10) {
                    if (spanclass == "forward") {
                        startposition = startposition + (stopposition - 8)

                    }

                    var new_div = document.createElement("div");
                    new_div.className = spanclass;
                    new_div.style.top = top + "px";
                    new_div.style.left = startposition + "px";
                    track_div.appendChild(new_div)
                }


            }

            last_exon = parseInt(exon_start);

        }

        return track_html;
    }
    else {
    }
}

function dispTrack(div, trackName, className) {

    var labelclass = "label" + trackName;
    var modi_style;
    var labeltoogle = "display : in-line;";
    var trackId;
    var trackClass, label;
    var track_html = [];
    var max = 110;

    if (window['track_list' + trackName].id.toString().indexOf('cs') > -1) {
        coord = true;
        trackClass = "track";
    }
    else {
        coord = false;
        trackClass = "track";
    }

    var j = 0;

    if (window['track_list' + trackName].label == 0) {
        labeltoogle = "display : none;";
    }

    if (jQuery('input[name=' + trackName + 'mergedCheckbox]').is(':checked')) {
        jQuery(div).fadeOut();
        jQuery("#" + trackName + "_wrapper").fadeOut();
        div = "#mergedtrack";
        jQuery("#mergedtrack").fadeIn();
        jQuery("#mergedtrack_wrapper").fadeIn();

        track_html.push("(" + merged_track_list + ")");
        jQuery("#mergelabel").html(track_html.join(""));

        trackClass += " mergedtrack";
        labelclass = "Merged_Track";
    }
    else {
        jQuery(div).fadeIn();
        jQuery("#" + trackName + "_wrapper").fadeIn();
    }


    if (!window[trackName] || window[trackName] == "loading") {
        if (div.indexOf("mergedtrack") <= 0) {
            jQuery(div).html("<img style='position: relative; left: 50%; ' src='./images/browser/loading_big.gif' alt='Loading'>")
            jQuery(div).fadeIn();
            jQuery("#" + trackName + "_wrapper").fadeIn();
        }
    }
    else if (window[trackName][0] == "getHit no result found") {
        if (div.indexOf("mergedtrack") <= 0) {
            jQuery('#' + trackName + 'Checkbox').attr('checked', false);
            jQuery(div).html();
            jQuery(div).fadeOut();
            jQuery("#" + trackName + "_wrapper").fadeOut();
        }
    }
    else {
        var partial = (getEnd() - getBegin()) / 2;
        var start = getBegin() - partial;
        var end = parseInt(getEnd()) + parseInt(partial);
        var diff = getEnd() - getBegin();
        var newStart_temp = getBegin();
        var newEnd_temp = getEnd();
        var maxLen_temp = jQuery("#canvas").css("width");

        var track = window[trackName];

        if (track[0] == null) {
            if (div.indexOf("mergedtrack") <= 0) {
                track_html = [];
                track_html.push("<font size=4><center>No data available for selected region</center></font>");
                jQuery(div).html(track_html.join(""));
            }
        }
        else if (track.length > 0 && (track.length < 10000 || diff <= minWidth)) {
            if (div.indexOf("mergedtrack") <= 0) {
                track_html = [];
                jQuery(div).html(track_html.join(""));
            }
            var coord;

            var track_len = track.length;
            var spanclass = "ui-icon ui-icon-carat-1-e";
            var border = "";

            while (track_len--) {

                var strand = track[track_len].strand;


                if (strand == -1 || strand == false) {
                    spanclass = "ui-icon ui-icon-carat-1-w";
                }

                var track_start = track[track_len].start;
                var track_stop = track[track_len].end ? track[track_len].end : parseInt(track[track_len].start) + 1;

                if (track[track_len].flag) {
                    border = "border: 1px solid black;";
                }
                var track_desc = track[track_len].desc;
                var top;
                if (coord || track[track_len].layer) {
                    top = (track[track_len].layer) * 10 + 15;
                    if (track[track_len].layer > j) {
                        j = track[track_len].layer;
                    }
                }
                else {
                    top = ((track_len) % (layers) + 1) * 20 + 15;
                }
                if (track[track_len].colour) {
                    modi_style = 'background:' + track[track_len].colour + "; ";
                }
                else {
                    modi_style = '';
                }
                var startposition = (track_start - newStart_temp) * parseFloat(maxLen_temp) / (newEnd_temp - newStart_temp) + parseFloat(maxLen_temp) / 2;
                var stopposition = (track_stop - track_start + 1) * parseFloat(maxLen_temp) / (newEnd_temp - newStart_temp);

                if (stopposition < 2) {
                    stopposition = 2;
                }

                if (trackName.toLowerCase().indexOf("snp") >= 0) {
                    spanclass = "";
                    if (stopposition < 2) {
                        stopposition = 2;
                    }
                    trackClass = 'snp' + track[track_len].cigarline;

                    label = track[track_len].cigarline;
                }
                else if (track_desc) {
                    label = track_desc + ":" + track_start + "-" + track_stop;

                } else {
                    label = track_start + "-" + track_stop;
                }

                var temp = jQuery("<div>").attr({
                    'id': trackName + "" + track_len,
                    'class': trackClass + " " + className,
                    'style': border + "" + modi_style + "TOP:" + top + "px; LEFT:" + (startposition) + "px; width:" + (stopposition) + "px;",
                    'title': label,
                    'onClick': "trackClick(\"" + trackName + "\",\"" + track_len + "\")"
                }).appendTo(div);

                jQuery("<div>").attr({
                    'class': "tracklabel " + labelclass,
                    'style': labeltoogle + " z-index: 999; overflow: hidden;text-overflow: ellipsis;",
                    'title': label
                }).html(label).appendTo(temp);


                if (track[track_len].cigars && stopposition > 50) {
                    jQuery(dispCigarLine(track[track_len].cigars, track[track_len].start, top)).appendTo(div);
                }
                else if (track[track_len].cigarline && stopposition > 50) {
                    jQuery(dispCigarLine(track[track_len].cigarline, track[track_len].start, top)).appendTo(div);
                }
            }
        }
        else if (track.length >= 10000) {
            dispGraph(div, trackName, trackId)
        }
    }

    max = parseInt(jQuery(div)[0].scrollHeight) + 50;

    jQuery("#" + trackName + "_wrapper").css("max-height", max);

    if (max > parseInt(jQuery("#" + trackName + "_wrapper").css("height"))) {
        jQuery("#" + trackName + "_wrapper").children(".ui-resizable-handle").addClass("resize-arrow")
    } else {
        jQuery("#" + trackName + "_wrapper").children(".ui-resizable-handle").removeClass("resize-arrow")

    }
}

function dispVCF(div, trackName, className) {

    var labelclass = "label" + trackName;
    var modi_style;
    var labeltoogle = "display : in-line;";
    var trackId;
    var trackClass, label;
    var track_html = [];
    var max = 110;


    coord = false;
    trackClass = "track";


    var j = 0;


    if (jQuery('input[name=' + trackName + 'mergedCheckbox]').is(':checked')) {
        jQuery(div).fadeOut();
        jQuery("#" + trackName + "_wrapper").fadeOut();
        div = "#mergedtrack";
        jQuery("#mergedtrack").fadeIn();
        jQuery("#mergedtrack_wrapper").fadeIn();

        track_html.push("(" + merged_track_list + ")");
        jQuery("#mergelabel").html(track_html.join(""));

        trackClass += " mergedtrack";
        labelclass = "Merged_Track";
    }
    else {
        jQuery(div).fadeIn();
        jQuery("#" + trackName + "_wrapper").fadeIn();
    }


    if (!window[trackName] || window[trackName] == "loading") {
        if (div.indexOf("mergedtrack") <= 0) {
            jQuery(div).html("<img style='position: relative; left: 50%; ' src='./images/browser/loading_big.gif' alt='Loading'>")
            jQuery(div).fadeIn();
            jQuery("#" + trackName + "_wrapper").fadeIn();
        }
    }
    else if (window[trackName][0] == "getHit no result found") {
        if (div.indexOf("mergedtrack") <= 0) {
            jQuery('#' + trackName + 'Checkbox').attr('checked', false);
            jQuery(div).html();
            jQuery(div).fadeOut();
            jQuery("#" + trackName + "_wrapper").fadeOut();
        }
    }
    else {
        var partial = (getEnd() - getBegin()) / 2;
        var start = getBegin() - partial;
        var end = parseInt(getEnd()) + parseInt(partial);
        var diff = getEnd() - getBegin();
        var newStart_temp = getBegin();
        var newEnd_temp = getEnd();
        var maxLen_temp = jQuery("#canvas").css("width");

        var track = window[trackName];

        if (track[0] == null) {
            if (div.indexOf("mergedtrack") <= 0) {
                track_html = [];
                track_html.push("<font size=4><center>No data available for selected region</center></font>");
                jQuery(div).html(track_html.join(""));
            }
        }
        else if (track.length > 0 && (track.length < 10000 || diff <= minWidth)) {
            if (div.indexOf("mergedtrack") <= 0) {
                track_html = [];
                jQuery(div).html(track_html.join(""));
            }
            var coord;

            var track_len = track.length;
            var spanclass = "ui-icon ui-icon-carat-1-e";
            var border = "";

            var temp = newEnd_temp - newStart_temp;
            var seqLen = visualLength(temp);
            if (parseFloat(seqLen) <= (parseFloat(maxLen))) {
                while (track_len--) {

                    var strand = track[track_len].strand;


                    var track_start = track[track_len].start;
                    var track_stop = track[track_len].end ? track[track_len].end : parseInt(track[track_len].start) + 1;

                    if (track[track_len].flag) {
                        border = "border: 1px solid black;";
                    }
                    var track_desc = track[track_len].desc;
                    var top;
                    if (coord || track[track_len].layer) {
                        top = (track[track_len].layer) * 10;
                        if (track[track_len].layer > j) {
                            j = track[track_len].layer;
                        }
                    }
                    else {
                        top = ((track_len) % (layers) + 1) * 20 + 15;
                    }
                    if (track[track_len].colour) {
                        modi_style = 'background:' + track[track_len].colour + "; ";
                    }
                    else {
                        modi_style = '';
                    }
                    var startposition = (track_start - newStart_temp) * parseFloat(maxLen_temp) / (newEnd_temp - newStart_temp) + parseFloat(maxLen_temp) / 2;
                    var stopposition = (track_stop - track_start + 1) * parseFloat(maxLen_temp) / (newEnd_temp - newStart_temp);

                    if (stopposition < 2) {
                        stopposition = 2;
                    }

                    label = stringColour(track[track_len].ref.toString()) + "<br>" + stringColour(track[track_len].alt.toString());

                    jQuery("<div>").attr({
                        'id': trackName + "" + track_len,
                        'class': trackClass,
                        'style': "font-family: 'Courier New',Courier,monospace; font-size: 13px; " + border + " " + modi_style + " TOP:" + top + "px; LEFT:" + (startposition) + "px; width:" + (stopposition) + "px;",
                        'onClick': "trackClick(\"" + trackName + "\",\"" + track_len + "\")"
                    }).html(label).appendTo(div);


                }

            } else {
                while (track_len--) {

                    var strand = track[track_len].strand;


                    var track_start = track[track_len].start;
                    var track_stop = track[track_len].end ? track[track_len].end : parseInt(track[track_len].start) + 1;

                    if (track[track_len].flag) {
                        border = "border: 1px solid black;";
                    }
                    var track_desc = track[track_len].desc;
                    var top;
                    if (coord || track[track_len].layer) {
                        top = (track[track_len].layer) * 10;
                        if (track[track_len].layer > j) {
                            j = track[track_len].layer;
                        }
                    }
                    else {
                        top = ((track_len) % (layers) + 1) * 20 + 15;
                    }
                    if (track[track_len].colour) {
                        modi_style = 'background:' + track[track_len].colour + "; ";
                    }
                    else {
                        modi_style = '';
                    }
                    var startposition = (track_start - newStart_temp) * parseFloat(maxLen_temp) / (newEnd_temp - newStart_temp) + parseFloat(maxLen_temp) / 2;
                    var stopposition = (track_stop - track_start + 1) * parseFloat(maxLen_temp) / (newEnd_temp - newStart_temp);

                    if (stopposition < 2) {
                        stopposition = 2;
                    }

                    label = stringColour(track[track_len].ref.toString()) + "<br>" + stringColour(track[track_len].alt.toString());
                    console.log(label)

                    jQuery("<div>").attr({
                        'id': trackName + "" + track_len,
                        'class': trackClass + " " + className,
                        'style': "font-family: 'Courier New',Courier,monospace; font-size: 13px; " + border + " " + modi_style + " TOP:" + top + "px; LEFT:" + (startposition) + "px; width:" + (stopposition) + "px;",
                        'onClick': "trackClick(\"" + trackName + "\",\"" + track_len + "\")"
                    }).appendTo(div);


                }
            }
        }
        else if (track.length >= 10000) {
            dispGraph(div, trackName, trackId)
        }
    }

    max = parseInt(jQuery(div)[0].scrollHeight) + 50;

    jQuery("#" + trackName + "_wrapper").css("max-height", max);

    if (max > parseInt(jQuery("#" + trackName + "_wrapper").css("height"))) {
        jQuery("#" + trackName + "_wrapper").children(".ui-resizable-handle").addClass("resize-arrow")
    } else {
        jQuery("#" + trackName + "_wrapper").children(".ui-resizable-handle").removeClass("resize-arrow")

    }
}

function dispCigarLine(cigars, start, top) {
    var track_html = "";
    var trackClass = "";
    var newStart_temp = getBegin();
    var newEnd_temp = getEnd();
    var maxLentemp = jQuery("#canvas").css("width");


    var cigar_pos = start;
    var startposition;
    var stopposition;
    if (cigars != '*') {
        cigars = cigars.replace(/([SIXMND])/g, ":$1,");
        var cigars_array = cigars.split(',');
        for (var i = 0; i < cigars_array.length - 1; i++) {
            var cigar = cigars_array[i].split(":");

            var key = cigar[1];
            var length = cigar[0];
            if (key == "M") {
                cigar_pos = parseInt(cigar_pos) + parseInt(length);
            }
            else if (key == "I") {
                trackClass = "insert";
                startposition = (cigar_pos - newStart_temp) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp) + parseFloat(maxLentemp) / 2;
                stopposition = (length) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp);
                track_html += trackHTML(startposition, stopposition, top, trackClass);
                cigar_pos = parseInt(cigar_pos) + parseInt(length)
            } else if (key == "N") {
                trackClass = "skip";
                startposition = (cigar_pos - newStart_temp) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp) + parseFloat(maxLentemp) / 2;
                stopposition = (length) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp);
                track_html += trackHTML(startposition, stopposition, top, trackClass);
                cigar_pos = parseInt(cigar_pos) + parseInt(length)
            }
            else if (key == "D") {
                trackClass = "delete";
                startposition = (cigar_pos - newStart_temp) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp) + parseFloat(maxLentemp) / 2;
                stopposition = 1
                track_html += trackHTML(startposition, stopposition, top, trackClass);
            }

            else if (key == "X") {
                trackClass = "mismatch";
                startposition = (cigar_pos - newStart_temp) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp) + parseFloat(maxLentemp) / 2;
                stopposition = (length) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp);
                track_html += trackHTML(startposition, stopposition, top, trackClass);
                cigar_pos = parseInt(cigar_pos) + parseInt(length)
            }
            else if (key == "=") {
                cigar_pos = parseInt(cigar_pos) + parseInt(length)
            }
        }
    }

    function trackHTML(startposition, stopposition, top, trackClass) {
        var track_html_local;

        track_html_local = "<div class='" + trackClass + "'  " +
            "STYLE=\"height: 5px; z-index: 100; TOP:" + top + "px; LEFT:" + startposition + "px; " +
            "width:" + (stopposition) + "px \" > </div>";

        return track_html_local;
    }

    return track_html;
}
function dispCigar(cigars, start, top) {
    var track_html = "";
    var trackClass = "";
    var newStart_temp = getBegin();
    var newEnd_temp = getEnd();
    var maxLentemp = jQuery("#canvas").css("width");

    for (var key in cigars) {
        if (key == "M") {
            trackClass = "match";
        }
        else if (key == "I") {
            trackClass = "insert";
        }
        else if (key == "D") {
            trackClass = "delete";
        }
        else if (key == "D" || key == "N") {
            trackClass = "skip";
        }

        else if (key == "X") {
            trackClass = "mismatch";
        }
        else if (key == "=") {
            trackClass = "match";
        }
        var cigar = cigars[key].split(",");
        for (var i = 0; i < cigar.length; i++) {
            var cigar_start = parseInt(cigar[i].split(":")[0]) + parseInt(start);
            var cigar_stop = cigar[i].split(":")[1];
            var startposition = (cigar_start - newStart_temp) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp) + parseFloat(maxLentemp) / 2;


            var stopposition;
            if (key == "M" || key == "I" || key == "X" || key == "=") {
                stopposition = (cigar_stop) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp);
            }
            else {
                stopposition = 1;
            }
            track_html += "<div class='" + trackClass + "'  " +
                "STYLE=\"height: 5px; z-index: 100; TOP:" + top + "px; LEFT:" + startposition + "px; " +
                "width:" + (stopposition) + "px \" > </div>";
        }

    }
    return track_html;
}


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

