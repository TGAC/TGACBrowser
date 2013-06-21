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
<<<<<<< HEAD
    jQuery(track_list).each(function (index) {
        if (track_list[index].name == trackName) {
            if (track_list[index].label == 1) {
                track_list[index].label = 0;
            }
            else {
                track_list[index].label = 1;
            }
        }

    });
=======
  console.log(window['track_list' + trackName].label)
  if (window['track_list' + trackName].label == 1) {
    window['track_list' + trackName].label = 0
  }
  else {
    window['track_list' + trackName].label = 1

  }
//  jQuery(track_list).each(function (index) {
//    if (track_list[index].name == trackName) {
//      if (track_list[index].label == 1) {
////        track_list[index].label = 0;
//
//      }
//      else {
//        track_list[index].label = 1;
//
//      }
//    }
//
//  });
>>>>>>> d095b59... vars created for tracks so most of the loops on track_list eliminated

    jQuery(".label" + trackName).toggle();
}

function toogleTrackView(trackName) {
<<<<<<< HEAD
    jQuery(track_list).each(function (index) {
        if (track_list[index].name == trackName) {
            if (track_list[index].expand == 1) {
                track_list[index].expand = 0;
            }
            else {
                track_list[index].expand = 1;
            }
        }
=======
  console.log(window['track_list' + trackName].expand)
  if (window['track_list' + trackName].expand == 1) {
    window['track_list' + trackName].expand = 0
    //        track_list[index].expand = 0;
  }
  else {
//          track_list[index].expand = 1;
    window['track_list' + trackName].expand = 1
>>>>>>> d095b59... vars created for tracks so most of the loops on track_list eliminated

  }

<<<<<<< HEAD
    });
=======
//  jQuery(track_list).each(function (index) {
//    if (track_list[index].name == trackName) {
//      if (track_list[index].expand == 1) {
//        window['track_list' + trackName].expand = 0
////        track_list[index].expand = 0;
//      }
//      else {
//        track_list[index].expand = 1;
//        window['track_list' + trackName].expand = 1
//
//      }
//    }
//
//
//  });
>>>>>>> d095b59... vars created for tracks so most of the loops on track_list eliminated

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
//  jQuery("#" + track + "span").remove();
<<<<<<< HEAD
    jQuery("#" + track + "mergedCheckbox").attr("disabled", true);
    jQuery(div).html();
    jQuery(div).fadeOut();
    jQuery("#" + track + "_wrapper").fadeOut();
    jQuery(track_list).each(function (index) {
        if (track_list[index].name == track) {
            track_list[index].disp = 0;
        }
    });
}

function removeMergedTrack() {
    jQuery(track_list).each(function (index) {
        if (jQuery("#" + track_list[index].name + "mergedCheckbox").attr('checked')) {
            this.disp = 0;
            jQuery('input[name=' + track_list[index].name + 'mergedCheckbox]').attr('checked', false);
            trackToggle(track_list[index].name);
        }
    });
    jQuery("#mergedtrack").html("<div id= \"mergelabel\" align='left' class='handle'></div>");
    jQuery("#mergedtrack").fadeOut();
    jQuery("#mergedtrack_wrapper").fadeOut();
=======
  jQuery("#" + track + "mergedCheckbox").attr("disabled", true);
  jQuery(div).html();
  jQuery(div).fadeOut();
  jQuery("#" + track + "_wrapper").fadeOut();
  window['track_list' + track].disp = 0

//  jQuery(track_list).each(function (index) {
//    if (track_list[index].name == track) {
//      track_list[index].disp = 0;
//    }
//  });
}

function removeMergedTrack() {

  jQuery(track_list).each(function (index) {
    if (jQuery("#" + track_list[index].name + "mergedCheckbox").attr('checked')) {
//      this.disp = 0;
      window['track_list' + track_list[index].name].disp = 0

      jQuery('input[name=' + track_list[index].name + 'mergedCheckbox]').attr('checked', false);
      trackToggle(track_list[index].name);
    }
  });
  jQuery("#mergedtrack").html("<div id= \"mergelabel\" align='left' class='handle'></div>");
  jQuery("#mergedtrack").fadeOut();
  jQuery("#mergedtrack_wrapper").fadeOut();
>>>>>>> d095b59... vars created for tracks so most of the loops on track_list eliminated

}

function toogleLabelMerged() {
<<<<<<< HEAD
    jQuery(track_list).each(function (index) {
        if (jQuery("#" + track_list[index].name + "mergedCheckbox").attr('checked')) {
            this.disp = 0;
            if (this.label == 1) {
                this.label = 0;
            }
            else {
                this.label = 1;
            }
=======
  jQuery(track_list).each(function (index) {
    if (jQuery("#" + track_list[index].name + "mergedCheckbox").attr('checked')) {
//      this.disp = 0;
      if (this.label == 1) {
        window['track_list' + track_list[index].name].label = 0
//        this.label = 0;
      }
      else {
        window['track_list' + track_list[index].name].label = 1
//        this.label = 1;
      }
>>>>>>> d095b59... vars created for tracks so most of the loops on track_list eliminated

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
<<<<<<< HEAD
    var labeltoogle = "display : in-line;";
    var labelclass = "label" + track;

    if (track.indexOf("RNA") >= 0) {
        trackClass = "rnaseq_exon";
    }
    else if (track.toLowerCase().indexOf("cufflink") >= 0) {
        trackClass = "cufflink_exon";
    }
    else {
        trackClass = "exon";
    }

    jQuery(track_list).each(function (index) {
        if (track_list[index].name == track) {
            if (track_list[index].label == 0) {
                labeltoogle = "display : none;";
            }
        }
    });
=======
  var labeltoogle = "display : in-line;";
  var labelclass = "label" + track;

  if (track.indexOf("RNA") >= 0) {
    trackClass = "rnaseq_exon";
  }
  else if (track.toLowerCase().indexOf("cufflink") >= 0) {
    trackClass = "cufflink_exon";
  }
  else {
    trackClass = "exon";
  }
  console.log(window['track_list' + track].label)
  if (window['track_list' + track].label == 0) {
    labeltoogle = "display : none;";
  }

//  jQuery(track_list).each(function (index) {
//    if (track_list[index].name == track) {
//      if (track_list[index].label == 0) {
//        labeltoogle = "display : none;";
//      }
//    }
//  });
>>>>>>> d095b59... vars created for tracks so most of the loops on track_list eliminated

//  var genes_old = window[track];
    var genes = window[track];

    var newStart_temp = getBegin();
    var newEnd_temp = getEnd();
    var maxLentemp = jQuery("#canvas").css("width");

    var partial = (newEnd_temp - newStart_temp) / 2;
    var start = newStart_temp - partial;
    var label = "";
    var end = parseInt(getEnd()) + partial;
//  jQuery(genes_old).each(function (i) {
//    //   console.log(this.start+"_"+this.end+"-"+start+"_"+end)
//    if (this.start >= start && this.end <= end || this.start <= start && this.end >= end || this.start <= start && this.end >= start || this.start <= start && this.end >= end) {
//      genes.push(this);
//    }
//  })

    var trackClass;
    if (!window[track] || window[track] == "loading") {
        jQuery(div).html("<img style='position: relative; left: 50%; ' src='./images/browser/loading_big.gif' alt='Loading'>")
        jQuery(div).fadeIn();
        jQuery("#" + track + "_wrapper").fadeIn();

    }
    else if (genes[0] == "getGene no result found") {
        jQuery('#' + track + 'Checkbox').attr('checked', false);
        jQuery(div).html();
        jQuery(div).fadeOut();
        jQuery("#" + track + "_wrapper").fadeOut();

        jQuery("#" + track + "span").remove();
    }

    else {

        var track_html = [];

        if (jQuery('input[name=' + track + 'mergedCheckbox]').is(':checked')) {
            jQuery(div).fadeOut();
            jQuery("#" + track + "_wrapper").fadeOut();

            jQuery(div).html("");
            div = "#mergedtrack";
            track_html.push("(" + merged_track_list + ")");
            jQuery("#mergelabel").html(track_html.join(""));
            trackClass += " mergedtrack"
            labelclass = "Merged_Track";
        }
        else {
            jQuery(div).html(track_html.join(''));
        }
        track_html = [];
        var j = 0;
        var len = genes.length;


        var now = new Date();
//    console.log(now.getMinutes() + ":" + now.getSeconds() + ":" + now.getMilliseconds());
        if (genes[0] == null) {
            track_html = [];
            track_html.push("<font size=4><center>No data available for selected region</center></font>");

            jQuery(div).html(track_html.join(""));
//      jQuery(div).css('height', 50)

            jQuery(div).fadeIn();
            jQuery("#" + track + "_wrapper").fadeIn();

        }
        else if (expand == 0) {
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


                //    var show = showObject(start, end, gene_start, gene_stop);
                //  if (show) {
                var gene_desc = genes[len].desc;
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

                var startposition = (gene_start - newStart_temp) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp) + parseFloat(maxLentemp) / 2;
                var stopposition = (gene_stop - gene_start + 1) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp);
                track_html.push("<div class='" + trackClass + " " + className + "_exon' STYLE=\"position:absolute;  cursor:pointer; TOP:" + top + "px; " +
                    "LEFT:" + startposition + "px; width :" + stopposition + "px; \" " +
                    "onclick=trackClick(\"" + track + "\",\"" + len + "\"); onmouseover=trackmouseover(\"" + track + "\",\"" + len + "\"); onmouseout=trackmouseout(); > " +
                    "<div style='" + labeltoogle + " z-index: 999; overflow: hidden;text-overflow: ellipsis;' class = \"tracklabel " + labelclass + "\"> <p class='track_label'>" + label + "</p></div></div>");

//            console.log(track_html);
                //}
            }
        }
        else {
            var len = genes.length;
            var template = "<div STYLE=\"position:absolute;  cursor:pointer; height: 10px;  \">";
            var template_label = "<div style=' z-index: 999;' class = \"label\"> <p></p></div></div>";
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
                    track_html.push("<div class='" + trackClass + " " + className + "' STYLE=\"height: 10px; TOP:" + top + "px; " +
                        "LEFT:" + startposition + "px; width :" + stopposition + "px; \" " +
                        " > <div style='overflow: hidden;text-overflow: ellipsis; " + labeltoogle + "' class = \"tracklabel " + labelclass + "\"> <p>" + label + "</p></div></div>");

                    if (stopposition > 10) {
                        track_html.push(dispGeneExon(genes[len].transcript[transcript_len], genes[len].strand, className));
                    }
                    else {
                        track_html.push("<div class='exon' STYLE=\"TOP:" + top + "px; " +
                            "LEFT:" + startposition + "px; width :" + stopposition + "px; \" " +
                            "> " +
                            "</div>");
                    }

                    track_html.push("<div class='track' STYLE=\"z-index: 999; height: 10px; TOP:" + top + "px; " +
                        "LEFT:" + startposition + "px; width :" + stopposition + "px; \" " +
                        "onclick=trackClick(\"" + track + "\",\"" + len + "\",\"" + transcript_len + "\"); > " +
                        "</div>");

                }
            }
        }
        var now = new Date();
//    console.log(now.getMinutes() + ":" + now.getSeconds() + ":" + now.getMilliseconds());

        if (jQuery('input[name=' + track + 'mergedCheckbox]').is(':checked')) {
            jQuery(div).fadeOut();
            jQuery("#" + track + "_wrapper").fadeOut();

            jQuery(div).html();

            //  track_html = track_html.replace(/tracks_image/g, 'merged_tracks_image')
            jQuery("#mergedtrack").css('height', (j * 20) + parseInt(50));
            jQuery("#mergedtrack").append(track_html.join(''));
            jQuery("#mergedtrack").fadeIn();
            jQuery("#mergedtrack_wrapper").fadeIn();


        }
        else {
//      jQuery(div).css('height', (parseInt(j * 20) + parseInt(50)));

            jQuery(div).fadeIn();
            jQuery("#" + track + "_wrapper").fadeIn();
            if (layers == 1) {
                track_html = track_html.replace(/tracks_image/g, 'merged_tracks_image')
            }
            jQuery(div).html(track_html.join(''));
//      }
        }

    }
}

function dispGeneExon(track, genestrand, className) {
    var trackClass = "exon "+className + "_exon";
    var utrtrackClass = "utr "+className + "_utr";


    var geneexons = track.Exons;
    var genetranscript = track;


    if (geneexons.length > 0) {
        var track_html = "";
        var last = null, current = null;
        var strand = genestrand;

        var spanclass = "ui-icon ui-icon-carat-1-e";

        if (strand == -1 || strand == "-1") {
            spanclass = "ui-icon ui-icon-carat-1-w";
        }

        var newStart_temp = getBegin();
        var newEnd_temp = getEnd();
        var maxLentemp = jQuery("#canvas").css("width");

        var partial = (newEnd_temp - newStart_temp) / 2;
        var start = newStart_temp - partial;
        var end = newEnd_temp + partial;
        var exon_len = geneexons.length;
        var last_exon = 0;
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

            var show = showObject(start, end, exon_start, exon_stop);
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

            if (exon_len - 1 >= 0 && geneexons[exon_len - 1].end < newStart_temp && exon_len + 1 < geneexons.length && geneexons[exon_len + 1].start > newEnd_temp) {

                startposition = (newStart_temp - newStart_temp) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp) + parseFloat(maxLentemp) / 2;
                stopposition = (getEnd() - newStart_temp) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp) + parseFloat(maxLentemp) / 2;
                track_html += "<div style=\"position:absolute; z-index; 999; TOP:" + (top - 3) + "px; left:" + (startposition + 20) + "px; \" class=\"" + spanclass + "\"></div>";
                track_html += "<div style=\"position:absolute; z-index; 999; TOP:" + (top - 3) + "px; left:" + (stopposition - 20) + "px; \" class=\"" + spanclass + "\"></div>";
            }
            if (show) {

                if (transcript_start && transcript_end) {
                    if (exon_start > transcript_end && exon_stop > transcript_end) {
                        startposition = (exon_start - newStart_temp) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp) + parseFloat(maxLentemp) / 2;
                        stopposition = (exon_stop - exon_start + 1) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp);
                        if (last != null) {
                            track_html += "<span style=\"cursor:pointer; position:absolute; z-index; 999; TOP:" + (top - 3) + "px; left:" + (startposition - 20) + "px; \" class= \"" + spanclass + "\"></span>";
                        }


                        track_html += "<div class='" + utrtrackClass + "'  " +
                            "STYLE=\"position:absolute; cursor:pointer; height: 10px; z-index: 100; TOP:" + top + "px; LEFT:" + startposition + "px; " +
                            "width:" + (stopposition) + "px \"> </div>";
                        //trackStatement(utrtrackClass, track, startposition, stopposition, a, top, j);

                        last = current;
                    }
                    else if (exon_start < transcript_start && exon_stop < transcript_start) {
                        startposition = (exon_start - newStart_temp) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp) + parseFloat(maxLentemp) / 2;
                        stopposition = (exon_stop - exon_start + 1) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp);

                        track_html += "<div class='" + utrtrackClass + "'  " +
                            "STYLE=\"position:absolute; cursor:pointer; height: 10px; z-index: 100; TOP:" + top + "px; LEFT:" + startposition + "px; " +
                            "width:" + (stopposition) + "px \" > </div>";
                        //trackStatement(utrtrackClass, track, startposition, stopposition, a, top, j);

                        last = current;
                    }
                    else if (exon_start < transcript_start && exon_stop > transcript_end) {

                        startposition = (exon_start - newStart_temp) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp) + parseFloat(maxLentemp) / 2;
                        stopposition = (transcript_start - exon_start + 1) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp);


                        track_html += "<div class='" + utrtrackClass + "'  " +
                            "STYLE=\"position:absolute; cursor:pointer; height: 10px; z-index: 100; TOP:" + top + "px; LEFT:" + startposition + "px; " +
                            "width:" + (stopposition) + "px \" > </div>";
                        //trackStatement(utrtrackClass, track, startposition, stopposition, a, top, j);

                        startposition = ( transcript_end - newStart_temp) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp) + parseFloat(maxLentemp) / 2;
                        stopposition = (exon_stop - transcript_end + 1) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp);


                        track_html += "<div class='" + utrtrackClass + "'  " +
                            "STYLE=\"position:absolute; cursor:pointer; height: 10px; z-index: 100; TOP:" + top + "px; LEFT:" + startposition + "px; " +
                            "width:" + (stopposition) + "px \" > </div>";
                        //trackStatement(utrtrackClass, track, startposition, stopposition, a, top, j);

                        startposition = (transcript_start - newStart_temp) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp) + parseFloat(maxLentemp) / 2;
                        stopposition = (transcript_end - transcript_start + 1) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp);


                        track_html += "<div class='" + trackClass + "'  " +
                            "STYLE=\"position:absolute; cursor:pointer; height: 10px; z-index: 100; TOP:" + top + "px; LEFT:" + startposition + "px; " +
                            "width:" + (stopposition) + "px \" > </div>";
                        ///trackStatement(trackClass, track, startposition, stopposition, a, top, j);

                        last = current;
                    }
                    else if (exon_stop > transcript_start && exon_start < transcript_start) {
                        startposition = ( exon_start - newStart_temp) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp) + parseFloat(maxLentemp) / 2;
                        stopposition = (transcript_start - exon_start + 1) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp);


                        track_html += "<div class='" + utrtrackClass + "'  " +
                            "STYLE=\"position:absolute; cursor:pointer; height: 10px; z-index: 100; TOP:" + top + "px; LEFT:" + startposition + "px; " +
                            "width:" + (stopposition) + "px \" > </div>";
                        //trackStatement(utrtrackClass, track, startposition, stopposition, a, top, j);


                        startposition = (transcript_start - newStart_temp) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp) + parseFloat(maxLentemp) / 2;
                        stopposition = (exon_stop - transcript_start + 1) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp);


                        track_html += "<div class='" + trackClass + "'  " +
                            "STYLE=\"position:absolute; cursor:pointer; height: 10px; z-index: 100; TOP:" + top + "px; LEFT:" + startposition + "px; " +
                            "width:" + (stopposition) + "px \" > </div>";

                        //trackStatement(trackClass, track, startposition, stopposition, a, top, j);


                        last = current;
                    }
                    else if (exon_stop > transcript_end && exon_start < transcript_end) {

                        startposition = ( transcript_end - newStart_temp) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp) + parseFloat(maxLentemp) / 2;
                        stopposition = (exon_stop - transcript_end + 1) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp);


                        track_html += "<div class='" + utrtrackClass + "'  " +
                            "STYLE=\"position:absolute; cursor:pointer; height: 10px; z-index: 100; TOP:" + top + "px; LEFT:" + startposition + "px; " +
                            "width:" + (stopposition) + "px \"> </div>";

                        //trackStatement(utrtrackClass, track, startposition, stopposition, a, top, j);

                        startposition = (exon_start - newStart_temp) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp) + parseFloat(maxLentemp) / 2;
                        stopposition = (transcript_end - exon_start + 1) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp);

                        track_html += "<div class='" + trackClass + "'  " +
                            "STYLE=\"position:absolute; cursor:pointer; height: 10px; z-index: 100; TOP:" + top + "px; LEFT:" + startposition + "px; " +
                            "width:" + (stopposition) + "px \" > </div>";
                        //trackStatement(trackClass, track, startposition, stopposition, a, top, j);

                        last = current;
                    }
                    else {

                        startposition = (exon_start - newStart_temp) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp) + parseFloat(maxLentemp) / 2;
                        stopposition = (exon_stop - exon_start + 1) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp);


                        track_html += "<div class='" + trackClass + "'  " +
                            "STYLE=\"position:absolute; cursor:pointer; height: 10px; z-index: 100; TOP:" + top + "px; LEFT:" + startposition + "px; " +
                            "width:" + (stopposition) + "px \" > </div>";

                        //trackStatement(trackClass, track, startposition, stopposition, a, top, j);

                        last = current;
                    }
                    if (last != null || geneexons.length == 1) {
                        if ((startposition - 20) > (transcript_start - newStart_temp) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp) + parseFloat(maxLentemp) / 2) {
                            track_html += "<span style=\"cursor:pointer; position:absolute; z-index; 999; TOP:" + (top - 3) + "px; left:" + (startposition - 20) + "px; \" class= \"" + spanclass + "\"></span>";
                        }
                    }
                }
                else {
                    startposition = (exon_start - newStart_temp) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp) + parseFloat(maxLentemp) / 2;
                    stopposition = (exon_stop - exon_start + 1) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp);


                    track_html += "<div class='" + trackClass + "'  " +
                        "STYLE=\"height: 10px; z-index: 100; TOP:" + top + "px; LEFT:" + startposition + "px; " +
                        "width:" + (stopposition) + "px \" > </div>";

                    if (last_exon > 0 && exon_len > 1) {
                        var marker_pos = (startposition - last_exon) / 2;
                        if (startposition < last_exon) {
                            marker_pos = (last_exon - startposition) / 2;
                            marker_pos = parseInt(startposition) - parseInt(marker_pos);
                        }
                        else {
                            marker_pos = parseInt(marker_pos) + parseInt(startposition) - 8;
                        }
                        track_html += "<span style=\"cursor:pointer; position:absolute; TOP:" + (top - 3) + "px; left:" + (marker_pos) + "px; opacity:0.6;  \" class= \"" + spanclass + "\"></span>";
                    }
                }
                last_exon = parseInt(startposition) + parseInt(stopposition);
            }


        }
        return track_html;
    }
    else {
    }
}

function dispTrack(div, trackName, className) {
    var labelclass = "label" + trackName;
    var modi_style;
    var now = new Date();
//  console.log(now.getMinutes() + ":" + now.getSeconds() + ":" + now.getMilliseconds());
<<<<<<< HEAD
    var labeltoogle = "display : none;";
    var trackId;
    jQuery(track_list).each(function (index) {
        if (track_list[index].name == trackName) {
            trackId = track_list[index].id;
            if (track_list[index].label == 1) {
                labeltoogle = "display : in-line;";
            }
        }
    });
=======
  var labeltoogle = "display : none;";
  var trackId;

  if (window['track_list' + trackName].label == 0) {
    labeltoogle = "display : none;";
  }

//  jQuery(track_list).each(function (index) {
//    if (track_list[index].name == trackName) {
//      trackId = track_list[index].id;
//      if (track_list[index].label == 1) {
//        labeltoogle = "display : in-line;";
//      }
//    }
//  });
>>>>>>> d095b59... vars created for tracks so most of the loops on track_list eliminated

    if (!window[trackName] || window[trackName] == "loading") {
        jQuery(div).html("<img style='position: relative; left: 50%; ' src='./images/browser/loading_big.gif' alt='Loading'>")
        jQuery(div).fadeIn();
        jQuery("#" + trackName + "_wrapper").fadeIn();

    }
    else if (window[trackName][0] == "getHit no result found") {
        jQuery('#' + trackName + 'Checkbox').attr('checked', false);
        jQuery(div).html();
        jQuery(div).fadeOut();
        jQuery("#" + trackName + "_wrapper").fadeOut();


    }
    else {
        var partial = (getEnd() - getBegin()) / 2;
        var start = getBegin() - partial;
        var end = parseInt(getEnd()) + parseInt(partial);
        var diff = getEnd() - getBegin();
        var newStart_temp = getBegin();
        var newEnd_temp = getEnd();
        var maxLen_temp = jQuery("#canvas").css("width");

//    var track_old = window[trackName];
        var track = window[trackName];
        var now = new Date();

//    if (track_old[0] && track_old[0].end) {
//      track = jQuery.grep(track_old, function (element, index) {
//        return element.start >= start && element.start <= end || element.start <= start && element.end >= end || element.start <= start && element.end >= start || element.start <= start && element.end >= end; // retain appropriate elements
//      });
//    }
//    else if (track_old[0]) {
//      track = jQuery.grep(track_old, function (element, index) {
//        return element.start >= start && element.start <= end; // retain appropriate elements
//      });
//    }
        if (track[0] == null) {
            track_html = [];
//      track_html.push("<span class='handle' style='left: 0%; width:100%'><center>No data available for selected region</center></span>");
            track_html.push("<font size=4><center>No data available for selected region</center></font>");


            jQuery(div).html(track_html.join(""));
//      jQuery(div).css('height', 50)

            jQuery(div).fadeIn();
            jQuery("#" + trackName + "_wrapper").fadeIn();

        }
        else if (track.length > 0 && (track.length < 10000 || diff <= minWidth)) {
            var track_html = [];
            var coord;
            if (jQuery('input[name=' + trackName + 'mergedCheckbox]').is(':checked')) {
                jQuery(div).fadeOut();
                jQuery(div).html("");
//        div = "#mergedtrack";
                track_html.push("(" + merged_track_list + ")");
                jQuery("#mergelabel").html(track_html.join(""));
                trackClass += " mergedtrack";
                labelclass = "Merged_Track";
            }
            else {
                jQuery(div).html(track_html.join(""));
//        track_html = "";
<<<<<<< HEAD
            }
=======
      }

      if (window['track_list' + trackName].id.toString().indexOf('cs') > -1) {
        coord = true;
      }
      else {
        coord = false;
      }

//      jQuery(track_list).each(function (index) {
//        if (this.name == trackName && this.id.toString().indexOf('cs') > -1) {
//          coord = true;
//        }
//        else {
//          coord = false;
//        }
//      });
      track_html = [];

      var j = 0;
      if (trackName.toLowerCase().indexOf("cds") >= 0) {
        trackClass = "cds";
      }
      else if (trackName.toLowerCase().indexOf("exon") >= 0) {
        trackClass = "exon track";
      }
      else if (trackName.toLowerCase().indexOf("contig") >= 0) {
        trackClass = "contigs track";
      }
      else if (trackName.toLowerCase().indexOf("est") >= 0) {
        trackClass = "est track";
      }
      else if (trackName.toLowerCase().indexOf("clone") >= 0) {
        trackClass = "clone track";
      }
      else if (trackName.toLowerCase().indexOf("align") >= 0) {
        trackClass = "align track";
      }
      else if (trackName.toLowerCase().indexOf("sam") >= 0 || trackName.toLowerCase().indexOf("bam") >= 0) {
        trackClass = "sam track";
      }
      else if (trackName.toLowerCase().indexOf("repeat") >= 0) {
        trackClass = "repeat track";
      }
      else {
        trackClass = "unknown track";
      }


      var track_len = track.length;
      while (track_len--) {

        var strand = track[track_len].strand;

        var spanclass = "ui-icon ui-icon-carat-1-e";
        if (strand == -1 || strand == false) {
          spanclass = "ui-icon ui-icon-carat-1-w";
        }


        var track_start = track[track_len].start;
        var track_stop = track[track_len].end ? track[track_len].end : parseInt(track[track_len].start) + 1;

        var border = "";
        if (track[track_len].flag) {
          border = "border: 1px solid black;";
        }
        var track_desc = track[track_len].desc;
        var top;
        if (coord || track[track_len].layer) {
          top = (track[track_len].layer) * 10 + 10;
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
        var trackClass, label;

        if (trackName.toLowerCase().indexOf("snp") >= 0) {
          spanclass = "";
          if (stopposition < 2) {
            stopposition = 2;
          }
          trackClass = 'snp' + track[track_len].cigarline + ' track';
>>>>>>> d095b59... vars created for tracks so most of the loops on track_list eliminated

            jQuery(track_list).each(function (index) {
                if (this.name == trackName && this.id.toString().indexOf('cs') > -1) {
                    coord = true;
                }
                else {
                    coord = false;
                }
            });
            track_html = [];

            var j = 0;
            if (trackName.toLowerCase().indexOf("cds") >= 0) {
                trackClass = "cds";
            }
            else if (trackName.toLowerCase().indexOf("exon") >= 0) {
                trackClass = "exon track";
            }
            else if (trackName.toLowerCase().indexOf("contig") >= 0) {
                trackClass = "contigs track";
            }
            else if (trackName.toLowerCase().indexOf("est") >= 0) {
                trackClass = "est track";
            }
            else if (trackName.toLowerCase().indexOf("clone") >= 0) {
                trackClass = "clone track";
            }
            else if (trackName.toLowerCase().indexOf("align") >= 0) {
                trackClass = "align track";
            }
            else if (trackName.toLowerCase().indexOf("sam") >= 0 || trackName.toLowerCase().indexOf("bam") >= 0) {
                trackClass = "sam track";
            }
            else if (trackName.toLowerCase().indexOf("repeat") >= 0) {
                trackClass = "repeat track";
            }
            else {
                trackClass = "unknown track";
            }


            var track_len = track.length;
            while (track_len--) {

                var strand = track[track_len].strand;

                var spanclass = "ui-icon ui-icon-carat-1-e";
                if (strand == -1 || strand == false) {
                    spanclass = "ui-icon ui-icon-carat-1-w";
                }


                var track_start = track[track_len].start;
                var track_stop = track[track_len].end ? track[track_len].end : parseInt(track[track_len].start) + 1;

                var border = "";
                if (track[track_len].flag) {
                    border = "border: 1px solid black;";
                }
                var track_desc = track[track_len].desc;
                var top;
                if (coord || track[track_len].layer) {

                    top = (track[track_len].layer) * 10 + 10;
                    if (track[track_len].layer > j) {
                        j = track[track_len].layer;
                    }

                    if (track[track_len].colour) {
                        modi_style = 'background:' + track[track_len].colour + "; ";
                    } else {
                        modi_style = '';
                    }
                }
                else {
                    top = ((track_len) % (layers) + 1) * 20 + 15;
                }
                var startposition = (track_start - newStart_temp) * parseFloat(maxLen_temp) / (newEnd_temp - newStart_temp) + parseFloat(maxLen_temp) / 2;
                var stopposition = (track_stop - track_start + 1) * parseFloat(maxLen_temp) / (newEnd_temp - newStart_temp);
                if (stopposition < 2) {
                    stopposition = 2;
                }
                var trackClass, label;

                if (trackName.toLowerCase().indexOf("snp") >= 0) {
                    spanclass = "";
                    if (stopposition < 2) {
                        stopposition = 2;
                    }
                    trackClass = 'snp' + track[track_len].cigarline + ' track';

                    label = track[track_len].cigarline;
                }
                else {
                    label = track_desc + ":" + track_start + "-" + track_stop;

                }

                track_html.push("<div class='" + trackClass + " " + className + "' STYLE=\"" + border + "" + modi_style + "TOP:" + top + "px; LEFT:" + (startposition) + "px; " +
                    "width:" + (stopposition) + "px \" " +
                    "onclick=trackClick(\"" + trackName + "\",\"" + track_len + "\"); " +
                    "title=" + label + ">" +
                    "<div style='" + labeltoogle + " z-index: 999; overflow: hidden;text-overflow: ellipsis;' class = \"tracklabel " + labelclass + "\">" + label + "</div></div>")
                if (stopposition > 10) {
                    track_html.push("<span style=\"cursor:pointer; position:absolute; TOP:" + (top - 6) + "px; left:" + (parseInt(startposition) + parseInt(stopposition / 2) - 8) + "px; opacity:0.6; \" class= \"" + spanclass + "\"></span>");
                }
                if (track[track_len].cigars && stopposition > 50) {
                    track_html.push(dispCigar(track[track_len].cigars, track[track_len].start, top));
                }
                else if (track[track_len].cigarline && stopposition > 50) {
                    track_html.push(dispCigarLine(track[track_len].cigarline, track[track_len].start, top));
                }
            }

            if (jQuery('input[name=' + trackName + 'mergedCheckbox]').is(':checked')) {
                jQuery("#mergedtrack").append(track_html.join(""));
                jQuery(div).fadeOut();
                jQuery("#" + trackName + "_wrapper").fadeOut();


                //  track_html = track_html.replace(/class='cds'/g, "class='mergedcds'");
                if (coord || track[0].layer) {
//          jQuery(div).css('height', (parseInt(j * 10) + parseInt(50)))
                }
                else {
//          jQuery(div).css('height', (parseInt(layers * 20) + parseInt(50)))
                }
                jQuery("#mergedtrack").fadeIn();
                jQuery("#mergedtrack_wrapper").fadeIn();
            }
            else {
                jQuery(div).html(track_html.join(""));

                if (coord || track[0].layer) {
//          jQuery(div).css('height', (parseInt(j * 10) + parseInt(50)))
                }
                else {
//          jQuery(div).css('height', (parseInt(layers * 15) + parseInt(50)))
                }
                jQuery(div).fadeIn();
                jQuery("#" + trackName + "_wrapper").fadeIn();

                if (layers == 1) {
                    //   track_html = track_html.replace(/class='cds'/g, "class='mergedcds'");
                }
            }
        }
        else if (track.length >= 10000) {
            dispGraph(div, trackName, trackId)
        }
    }
    var now = new Date();
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

//        trackClass = "match";
//        startposition = (cigar_pos - newStart_temp) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp) + parseFloat(maxLen) / 2;
//        stopposition = (length) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp);
//        track_html += trackHTML(startposition, stopposition, top, trackClass);
//        console.log(cigar_pos + "," + length)
                cigar_pos = parseInt(cigar_pos) + parseInt(length);

//        console.log(startposition + "," + stopposition + "," + top + "," + trackClass)
            }
            else if (key == "I") {
                trackClass = "insert";
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
//        trackClass = "match";
//        startposition = (cigar_pos - newStart_temp) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp) + parseFloat(maxLen) / 2;
//        stopposition = (length) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp);
//        track_html += trackHTML(startposition, stopposition, top, trackClass);
//        console.log(cigar_pos + "," + length)
                cigar_pos = parseInt(cigar_pos) + parseInt(length)
//        console.log(startposition + "," + stopposition + "," + top + "," + trackClass)
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
//  console.log("cigars")
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
        else if (key == "D") {
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
//(track_start - newStart_temp) * parseFloat(maxLen) / (newEnd_temp - newStart_temp) + parseFloat(maxLen) / 2;
            var cigar_start = parseInt(cigar[i].split(":")[0]) + parseInt(start);
            var cigar_stop = cigar[i].split(":")[1];
            var startposition = (cigar_start - newStart_temp) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp) + parseFloat(maxLentemp) / 2;


            //  var startposition =parseInt( (parseInt(parseInt(start)+ parseInt(cigar[i].split(":")[0]) - newStart_temp) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp) + parseFloat(maxLentemp) / 2));
            var stopposition;
            if (key == "M" || key == "I" || key == "X" || key == "=") {
                //  stopposition = (parseInt(cigar[i].split(":")[1])) * parseFloat(maxLentemp) / (newEnd_temp - newStart_temp);
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


function dispGraph(div, trackName,  className) {
    var track_html = "";

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

            track_html += "<div class= \"graph "+className+"_graph\" onclick=\"setBegin(" + track[track_len].start + ");setEnd(" + track[track_len].end + ");jumpToSeq();\"STYLE=\"bottom:0px; height: " + (track[track_len].graph * 45 / max) + "px;" +
                "LEFT:" + startposition + "px;" +
                "width:" + (stopposition - 1) + "px \" title=\"" + track_start + ":" + track_stop + "->" + track[track_len].graph + "\" ></div>";

        }
//  jQuery(div).css('height', '70px');
        jQuery(div).fadeIn();
        jQuery("#" + trackName + "_wrapper").fadeIn();

        jQuery(div).html(track_html);
    }
}

function dispGraphBed(div, trackName, trackId, className) {
    var track_html = "";

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
            return o.value;
        }));

        var track_len = track.length;

        while (track_len--) {
            var track_start = track[track_len].start;
            var track_stop = track[track_len].end;

            var startposition = (track_start - newStart_temp) * parseFloat(maxLen_temp) / (newEnd_temp - newStart_temp) + parseFloat(maxLen_temp) / 2;
            var stopposition = (track_stop - track_start ) * parseFloat(maxLen_temp) / (newEnd_temp - newStart_temp);

            track_html += "<div class= \"graph "+className+"graph\" onclick=\"setBegin(" + track[track_len].start + ");setEnd(" + track[track_len].end + ");jumpToSeq();\"STYLE=\"bottom:0px; height: " + (track[track_len].value * 45 / max) + "px;" +
                "LEFT:" + startposition + "px;" +
                "width:" + (stopposition - 1) + "px \" title=\"" + track_start + ":" + track_stop + "->" + track[track_len].value + "\" ></div>";

        }
//  jQuery(div).css('height', '70px');
        jQuery(div).fadeIn();
        jQuery("#" + trackName + "_wrapper").fadeIn();

        jQuery(div).html(track_html);
    }
}

function dispGraphWig(div, trackName, trackId, className) {

    var track_html = "";

    jQuery(div).html("");//<img style='position: relative; left: 50%; ' src='./images/browser/loading_big.gif' alt='Loading'>")
    jQuery(div).fadeIn();
    jQuery(trackName + "_wrapper").fadeIn();

    var track = window[trackName];
    var partial = (parseInt(getEnd()) - parseInt(getBegin())) / 2;
    var start = parseInt(getBegin()) - parseInt(partial)
    var end = parseInt(getEnd()) + parseInt(partial);

    if (track) {
        if (track[0] == null) {
            track_html = [];
            track_html.push("<font size=4><center>No data available for selected region</center></font>");
        }
        else if (track[0]) {
            track = jQuery.grep(track, function (element, index) {
                return element.start >= start && element.start <= end; // retain appropriate elements
            });
        }


        var total = 0;
        var max = Math.max.apply(Math, track.map(function (o) {
            return o.value;
        }));

        var width = jQuery("#wrapper").width(),
            height = 80;


        var left = 0;
        if (start < 0) {
            left = (1 - start) * parseInt(width) / end * 3 / 4;
        }

        var top = 20;

        var svg = d3.select(div).append("svg")
            .attr("width", width)
            .attr("height", height + 20)
            .append("g")
            .attr("transform", "translate(" + left + "," + top + ")");

        var d3line2 = d3.svg.line()
            .x(function (d) {
                return d.x;
            })
            .y(function (d) {
                return d.y;
            })
            .interpolate("linear");

        d3.json(track, function () {
            data = track;
            var length = data.length - 1;
            var end_val = parseInt(data[length].start) + parseInt(data[1].start - data[0].start);
            var start_val = parseInt(data[0].start) - (parseInt(data[1].start - data[0].start));

            data.splice(0, 0, {start: start_val, value: '0'});

            data.splice(data.length, 0, {start: end_val, value: '0'});
            var space = parseInt(width) / (end - start);

            var pathinfo = [];

            var last_start = 0;
            var diff = parseInt(data[1].start - data[0].start);

            for (var i = 0; i < data.length - 1;) {
                var tempx;
                if (start > 0) {
                    tempx = (data[i].start - start) * space;
                }
                else {
                    tempx = (data[i].start) * space;
                }
                var tempy = height - (data[i].value * height / max); //(parseInt(max)*(41-parseInt(patharray[i]))/41);
                pathinfo.push({ x: tempx, y: tempy});


//        if (data.length < 400) {
                i++;
//        }
//        else {
//          i = parseInt(i + (data.length / 400));
//        }

                if (last_start < data[i].start - diff) {
                    if (start > 0) {
                        tempx = ((parseInt(last_start) + parseInt(diff)) - start) * space;
                    }
                    else {
                        tempx = ((parseInt(last_start) + parseInt(diff))) * space;
                    }
                    var tempy = height; //(parseInt(max)*(41-parseInt(patharray[i]))/41);
                    pathinfo.push({ x: tempx, y: tempy});

                    if (start > 0) {
                        tempx = ((parseInt(data[i].start) - parseInt(diff)) - start) * space;
                    }
                    else {
                        tempx = ((parseInt(data[i].start) - parseInt(diff))) * space;
                    }
                    var tempy = height; //(parseInt(max)*(41-parseInt(patharray[i]))/41);
                    pathinfo.push({ x: tempx, y: tempy});

                }

                last_start = data[i].start;
            }


            if (start > 0) {
                tempx = (data[data.length - 1].start - start) * space;
            }
            else {
                tempx = (data[data.length - 1].start) * space;
            }
            var tempy = height - (data[data.length - 1].value * height / max); //(parseInt(max)*(41-parseInt(patharray[i]))/41);
            pathinfo.push({ x: tempx, y: tempy});
            var path = svg.selectAll("path")
                .data([1]);

            path.enter().append("svg:path")
                .attr("width", 200)
                .attr("height", 200)
                .attr("class", "path")

                .attr('stroke', function () {
                    return "blue";
                })
                .attr('stroke-width', function () {
                    return "2px";
                })
                .attr("fill", function () {
                    return "lightblue";
                })
                .attr("d", d3line2(pathinfo));
        });

    }
    else {
        jQuery(div).html("<img style='position: relative; left: 50%; ' src='./images/browser/loading_big.gif' alt='Loading'>")
    }

    jQuery(div).css('height', '70px');
    jQuery(div).fadeIn();
    jQuery("#" + trackName + "_wrapper").fadeIn();
//
    // jQuery(div).html(track_html);
}

function sortResults(prop, asc, array) {
    array = array.sort(function (a, b) {
        if (asc) return (a[prop] > b[prop]);
        else return (b[prop] > a[prop]);
    });
    return array;
}