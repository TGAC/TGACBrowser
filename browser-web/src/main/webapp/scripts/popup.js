/**
 * Created by IntelliJ IDEA.
 * User: thankia
 * Date: 2/7/12
 * Time: 2:46 PM
 * To change this template use File | Settings | File Templates.
 */

var open = false;
// removes popup

function removeMenu() {
  jQuery('#menu').hide();
}

function removePopup() {

  jQuery("#popuptrack").html("");
  jQuery("#position").html("");
  jQuery("#Detail").html("");
  jQuery("#popup").fadeOut("");
  jQuery('#EditTrack').hide();
  jQuery('#blastselector').hide();
  jQuery('#popup_hanging').css('display', 'none');
}

// create a new for each track
function newpopup(track, i, j) {
  var width = jQuery("#popup").width();
  jQuery('#blastselector').hide();
  jQuery("#popuptrack").html(track);
  var position = window[track][i].start + position_func();

  function position_func() {
    if (window[track][i].end) {
      return window[track][i].start
    }
    else {
      return "";
    }
  }

  if (j) {
    jQuery("#makemetop").html('<span title="Make Me Top" class="ui-button ui-icon ui-icon-arrowthick-1-n" onclick=makeMeTop(\"' + track + "\",\"" + i + "\",\"" + j + '\");></span>');
    jQuery("#peptides").html('<span title="peptides" class="ui-button ui-icon ui-icon-comment" onclick=showPeptides(\"' + track + "\",\"" + i + "\",\"" + j + '\");></span>');
    jQuery("#position").html(stringTrim(window[track][i].transcript[j].start + "-" + window[track][i].transcript[j].end, width));
    jQuery("#exdetails").html('<span title="Attributes" class="ui-button ui-icon ui-icon-note" onclick=showDetails(\"' + track + "\",\"" + i + "\",\"" + j + '\");></span>');
    jQuery("#FASTAme").html('<span title="Fasta" class="ui-button ui-fasta" onclick=fetchFasta(' + window[track][i].transcript[j].start + ',' + window[track][i].transcript[j].end + ",\"" + track + "\",\"" + i + "\",\"" + j + '\");></span>');
    jQuery("#BLASTme").html('<span title="Blast" class="ui-button ui-blast" onclick=preBlast(' + window[track][i].transcript[j].start + ',' + window[track][i].transcript[j].end + ',' + '\"#popup\");></span>');
    jQuery("#ZoomHere").html('<span title="Zoom Here" class="ui-button ui-icon ui-icon-zoomin" onclick=zoomHere(' + window[track][i].transcript[j].start + ',' + window[track][i].transcript[j].end + ');></span>');
    jQuery("#EditDescription").html('<span title="Edit" class="ui-button ui-icon ui-icon-pencil" onclick=showEditDesc(\"' + track + "\",\"" + i + "\",\"" + j + '\");></span>');
    jQuery("#deleteTrack").html('<span title="Remove" class="ui-button ui-icon ui-icon-trash" onclick=deleteTrack(\"' + track + "\",\"" + i + "\",\"" + j + '\");></span>');
    jQuery("#flagTrack").html('<span title="Flag" class="ui-button ui-icon ui-icon-flag" onclick=flagTrack(\"' + track + "\",\"" + i + "\",\"" + j + '\");></span>');
    jQuery("#Ensemblme").html("<a target='_blank' href='http://www.ensembl.org/Mus_musculus/Gene/Summary?g=" + window[track][i].desc + "'> <span title=\"Ensembl\" class=\"ui-button ui-ensembl\"></span></a>");
    jQuery("#revertme").html('<span title="Revert_Name" class="ui-button ui-icon ui-icon-arrowreturnthick-1-w" onclick=revertTrack(\"' + track + "\",\"" + i + "\",\"" + j + '\");></span>');
    jQuery("#Detail").html(stringTrim(window[track][i].desc, width));

  }
  else {
    jQuery("#Ensemblme").html("");
    jQuery("#makemetop").html('');
    jQuery("#peptides").html('');
    jQuery("#position").html(position);
    jQuery("#exdetails").html('');
    if (window[track][i].end - window[track][i].start > 1) {
      jQuery("#FASTAme").html('<span title="Fasta" class="ui-button ui-fasta" onclick=fetchFasta(' + window[track][i].start + ',' + window[track][i].end + ');></span>');
      jQuery("#BLASTme").html('<span title="Blast" class="ui-button ui-blast" onclick=preBlast(' + window[track][i].start + ',' + window[track][i].end + ',' + '\"#popup\");></span>');
      jQuery("#revertme").html('<span title="Revert_Name" class="ui-button ui-icon ui-icon-arrowreturnthick-1-w" onclick=revertTrack(\"' + track + "\",\"" + i + '\");></span>');

    }
    else {
      jQuery("#FASTAme").html('');
      jQuery("#BLASTme").html('');
      jQuery("#revertme").html('');

    }
    jQuery("#ZoomHere").html('<span title="Zoom Here" class="ui-button ui-icon ui-icon-zoomin" onclick=zoomHere(' + window[track][i].start + ',' + window[track][i].end + ');></span>');
    jQuery("#EditDescription").html('<span title="Edit" class="ui-button ui-icon ui-icon-pencil" onclick=showEditDesc(\"' + track + '\",\'' + i + '\');></span>');
    jQuery("#deleteTrack").html('<span title="Remove" class="ui-button ui-icon ui-icon-trash" onclick=deleteTrack(\"' + track + '\",\'' + i + '\');></span>');
    jQuery("#flagTrack").html('<span title="Flag" class="ui-button ui-icon ui-icon-flag" onclick=flagTrack(\"' + track + '\",\'' + i + '\');></span>');
    jQuery("#Detail").html(stringTrim(window[track][i].cigarline, width));

  }


// decide side of popup left / right
  if (mouseX + jQuery("#popup").width() > jQuery("#main1").width()) {
    jQuery("#popup").css({"left": mouseX - jQuery("#popup").width() - 5});
    jQuery("#popup").css({"top": (mouseY - jQuery("#popup").height() - 36)});
    jQuery("#popup").attr('class', 'bubbleright')
  }
  else {
    jQuery("#popup").css({"left": (mouseX - 26)});
    jQuery("#popup").css({"top": (mouseY - jQuery("#popup").height() - 36)});
    jQuery("#popup").attr('class', 'bubbleleft')
  }

  jQuery("#popup").fadeIn();
}


function removeBlastPopup() {
  jQuery("#indel").html("");
  jQuery('#blastpopup').hide();
}

// create a new for each track
function newBlastpopup(query, hit) {
  var match = "";
  for (var i = 0; i < query.length; i++) {
    if (query.charAt(i) == hit.charAt(i)) {
      match += "|";
    }
    else {
      match += "&nbsp;";
    }
  }
  jQuery("#query").html(query);
  jQuery("#match").html(match);
  jQuery("#sbjct").html(hit);

//decide side of popup left / right
  if (mouseX + jQuery("#blastpopup").width() > jQuery("#main1").width()) {
    jQuery("#blastpopup").css({"left": mouseX - jQuery("#blastpopup").width() - 5});
    jQuery("#blastpopup").css({"top": mouseY - jQuery("#blastpopup").height() - 36});
    jQuery("#blastpopup").attr('class', 'bubbleright')
  }
  else {
    jQuery("#blastpopup").css({"left": mouseX - 26});
    jQuery("#blastpopup").css({"top": mouseY - jQuery("#blastpopup").height() - 36});
    jQuery("#blastpopup").attr('class', 'bubbleleft')
  }
  jQuery("#blastpopup").fadeIn();
}

// removes drag popup
function removeDragPopup() {
  jQuery("#seqdrag").hide();
  jQuery("#dragpopup").fadeOut("");
  jQuery("#cordinate").html("");
}


// create drag popup
function newDragpopup(begin, end, binary) {

  jQuery("#cordinate").html("<b>" + (begin) + "-" + (end) + "</b>");
  jQuery("#fetchFASTA").html('<span title="Fasta" class="ui-button ui-fasta" onclick=fetchFasta(' + begin + ',' + end + ');></span>');
  jQuery("#fetchBLAST").html('<span title="Blast" class="ui-button ui-blast" onclick=preBlast(' + begin + ',' + end + ',' + '\"#popup\");></span>');
  jQuery("#CenterHere").html('<span title="Zoom Here" class="ui-button ui-icon ui-icon-zoomin" onclick=zoomHere(' + begin + ',' + end + ');></span>');

// decide side left - right
  if (mouseX + jQuery("#dragpopup").width() > jQuery("#main1").width()) {
    if (binary == "false") {
      jQuery("#dragpopup").css({"left": mouseX - jQuery("#dragpopup").width() / 2 - 100 + jQuery("#seqdrag").width() / 2});
    }
    else {
      jQuery("#dragpopup").css({"left": mouseX - jQuery("#dragpopup").width() - jQuery("#seqdrag").width() / 2});
    }
    jQuery("#dragpopup").css({"top": jQuery('#sequence').offset().top - jQuery("#dragpopup").height() - 36});
    jQuery("#dragpopup").attr('class', 'bubbleright')
  }
  else {
    if (binary == "false") {
      jQuery("#dragpopup").css({"left": mouseX + jQuery("#seqdrag").width() / 2 - 26});
    }
    else {
      jQuery("#dragpopup").css({"left": mouseX - jQuery("#seqdrag").width() / 2 - 26});
    }
    jQuery("#dragpopup").css({"top": jQuery('#sequence').offset().top - jQuery("#dragpopup").height() - 36});
    jQuery("#dragpopup").attr('class', 'bubbleleft')
  }
  jQuery("#dragpopup").fadeIn();
}


// centaralised view to track
function zoomHere(begin, end) {
//  setBegin(begin - 1);
//  setEnd(end + 1);

  var tempBegin = (begin - 1);
  var tempEnd = (end + 1);

  if ((tempEnd - tempBegin) <= minWidth) {
    var diff = minWidth - (tempEnd - tempBegin);

    setBegin(parseInt(tempBegin - parseInt((diff / 2))));
    setEnd(parseInt(tempEnd + parseInt((diff / 2))));

  }
  else {
    setBegin(tempBegin);
    setEnd(tempEnd);
  }

  jumpToSeq();

}

// set blast parameters and call blast
function preBlast(begin, end, popup) {
  jQuery('#EditTrack').hide();
  var blast_selector = "<table width='100%'>" +
                       "<tr><td>Number of Hits<select name=\"blasthit\" id=\"blasthit\">  " +
                       "<option value=\"1\">1</option>  " +
                       "<option value=\"2\">2</option> " +
                       "<option value=\"3\">3</option>  " +
                       "<option value=\"4\">4</option>  " +
                       "<option value=\"5\">5</option>            " +
                       "<option value=\"6\">6</option>  " +
                       "<option value=\"7\">7</option>  " +
                       "<option value=\"8\">8</option> " +
                       "<option value=\"9\">9</option>   " +
                       "<option value=\"10\">10</option>  " +
                       "</select> </td>" +
//                       "<tr><td>Blast DB<select name=\"blastdb\" id=\"blastdb\">  " +
////                       "<option value=\"/net/tgac-cfs3/ifs/TGAC/browser/jobs/choblastdb/assembly_POOLED_non-redundant_contigs_over_1000.fa\">Version 1.0 Contigs</option> " +
////                       "<option value=\"/net/tgac-cfs3/ifs/TGAC/browser/jobs/choblastdb/assembly_abyss-q30-80_sspace-all_OVER_1kb.fasta\">Version 1.1 Contigs</option> " +
////                       "<option  value=\"/net/tgac-cfs3/ifs/TGAC/browser/jobs/choblastdb/TGAC_CHO_v1.2_COMPLETE.fa\">Version 1.2 Contigs</option> " +
//                       "<option selected value=\"/net/tgac-cfs3/ifs/TGAC/browser/jobs/choblastdb/TGAC_CHO_v2.0_COMPLETE.fa\">Version 2.0 Scaffolds</option> " +
//                       "<option VALUE =\"/net/tgac-cfs3/ifs/TGAC/browser/jobs/choblastdb/unplaced.scaf.fa\">BGI CHO scaffolds </option>" +
//                       "</select> </td>" +
                       "<td><span class=\"fg-button ui-icon ui-widget ui-state-default ui-corner-all ui-icon-close\" id=\"dontblast\"></span>" +
                       "<span class=\"fg-button ui-icon ui-widget ui-state-default ui-corner-all ui-icon-check\" id=\"doblast\"></span> </td></tr></table>";


  jQuery('#blastselectorpanel').html(blast_selector);
  jQuery('#blastselector').show();
  if (parseInt(mouseX) + parseInt(jQuery("#blastselector").width()) > jQuery("#main1").width()) {
    jQuery('#blastselector').css({"left": mouseX - jQuery("#blastselector").width()});
    jQuery("#blastselector").css({"top": mouseY + 10});
  }
  else {
    jQuery('#blastselector').css({"left": mouseX});
    jQuery("#blastselector").css({"top": mouseY + 10});
  }

  jQuery("#doblast").click(function () {
    var hit = jQuery('#blasthit').val();
    var blastdb = jQuery('#blastdb').val();

    blast(begin, end, hit, blastdb);
    removePopup();

    jQuery('#blastselector').hide();
  });

  jQuery("#dontblast").click(function () {
    jQuery('#blastselector').hide();
  });

}


// edit desc div
function showEditDesc(track, i, j) {
  jQuery('#blastselector').hide();
  var edit_desc = "<input type=\"text\" name=\"editTrackValue\" id=\"editTrackValue\"><span class=\"fg-button ui-icon ui-widget ui-state-default ui-corner-all ui-icon-close\" id=\"editTrackRemove\"></span><span class=\"fg-button ui-icon ui-widget ui-state-default ui-corner-all ui-icon-check\" id=\"editTrackAdd\"></span>";
  jQuery('#EditTrack').html(edit_desc);


  if (mouseX + jQuery("#EditTrack").width() > jQuery("#main1").width()) {
    jQuery('#EditTrack').css({"left": mouseX - jQuery("#EditTrack").width()});
    jQuery("#EditTrack").css({"top": mouseY + 10});
  }
  else {
    jQuery('#EditTrack').css({"left": mouseX});
    jQuery("#EditTrack").css({"top": mouseY + 10});
  }


  jQuery('#EditTrack').show();
  if (j) {
    jQuery("#editTrackValue").val(window[track][i].transcript[j].desc);
  }
  else {
    jQuery("#editTrackValue").val(window[track][i].desc);
  }

  jQuery("#editTrackAdd").click(function () {
    editDesc(track, i, j)
  });

  jQuery("#editTrackRemove").click(function () {
    jQuery('#EditTrack').hide();
    jQuery('#editTrackValue').val('');
  });

}

//make top
function makeMeTop(track, i, j) {

  for (var a = 0; a < window[track][i].transcript.length; a++) {
    if (window[track][i].transcript[a].layer < window[track][i].transcript[j].layer) {
      window[track][i].transcript[a].layer += 1;
    }
  }
  window[track][i].transcript[j].layer = 1;
  removePopup();
  trackToggle(track);
}

// edit desc main function
function editDesc(track, i, j) {

  var edited = jQuery("#editTrackValue").val().replace(/:/g, '=');
  edited = edited.replace(/\s*/g, '');
  if (j) {
    window[track][i].transcript[j].desc = edited;
  }
  else {
    window[track][i].desc = edited;
  }
  trackToggle(track);
  removePopup();
}

// removes data from variables and display again
function deleteTrack(track, i, j) {
  var response = confirm("Tracks Removed Can not be undone\nDo you wish to continue?")
  if (response == true) {
    if (j) {
      delete window[track][i].transcript.splice(j, 1);
    }
    else {
      delete window[track].splice(i, 1);
    }
    trackToggle(track);
    removePopup();
  }
}

// flag track
function flagTrack(track, i, j) {
  if (j) {
    if (window[track][i].transcript[j].flag == true) {
//      jQuery.each(window[track], function(index, result) {
//      if(result.desc == 'flag' && result.id == window[track][i].id) {
//          //Remove from array
//          window[track].splice(index, 1);
//      }
//   });
      window[track][i].transcript[j].flag = false;
    }
    else {
//      window[track].push({id: window[track][i].id, start: window[track][i].start, end: window[track][i].end, desc: 'flag', transcript:[{transcript_start:window[track][i].transcript[j].transcript_start,transcript_end:window[track][i].transcript[j].transcript_end}]});
      window[track][i].transcript[j].flag = true;
    }
  }
  else {
    if (window[track][i].flag == true) {
      //      jQuery.each(window[track], function(index, result) {
//      if(result.desc == 'flag' && result.id == window[track][i].id) {
//          //Remove from array
//          window[track].splice(index, 1);
//      }
//   });
      window[track][i].flag = false;
    }
    else {
//      window[track].push({id: window[track][i].id, start: window[track][i].start, end: window[track][i].end, desc: 'flag'});
      window[track][i].flag = true;
    }
  }


  trackToggle(track);
  removePopup();
}

function revertTrack(track, i, j) {
  if (j) {
    var id = window[track][i].transcript[j].id;
    Fluxion.doAjax(
            'dnaSequenceService',
            'loadTranscriptName',
            {'id': id, 'url': ajaxurl, 'track': track},
            {'doOnSuccess': function (json) {

              window[track][i].transcript[j].desc = json.name;
              trackToggle(track);

            }
            });


  }
  else {
    var id = window[track][i].id;
    Fluxion.doAjax(
            'dnaSequenceService',
            'loadTrackName',
            {'id': id, 'url': ajaxurl, 'track': track},
            {'doOnSuccess': function (json) {
              window[track][i].desc = json.name;
              trackToggle(track);
            }
            });
  }


  removePopup();
}


// search seq in ref seq
function searchSeq() {

  if (jQuery("#searchText").val().length > 2) {

    jQuery("#searchDiv").html("");
    var re = new RegExp(jQuery("#searchText").val().toUpperCase(), "g");
    var match, matches = [];
    var match_found = "";
    while ((match = re.exec(seq)) != null) {
      matches.push(match.index);
    }

    var top = parseFloat(jQuery("#draggable").position().top) - parseFloat(jQuery("#bar_image").position().top);
    var height = jQuery("#draggable").css('height');
    jQuery("#searchDiv").css('height', height);
    jQuery("#searchDiv").css('top', top);

    for (var i = 0; i < matches.length; i++) {
      var left = parseFloat(matches[i]) * parseFloat(maxLen) / parseFloat(sequencelength);
      var width = jQuery("#searchText").val().length * parseFloat(maxLen) / parseFloat(sequencelength);
      var height = jQuery("#draggable").css('height');
      match_found += ("<div \" style=\" z-index:999; cursor:pointer; position: relative; float:left; left: " + left + "px; width:" + width + "px; top:0px; height: +100%; border: 1px solid red; \"></div>");
    }

    match_found += "</div>";
    match_found = match_found.replace("undefined", "");
    jQuery("#searchDiv").html(match_found);
  }
  else {
    jQuery("#searchDiv").html("");
  }
  /**/

}

// display search box
function searchbox() {

  if (open == false) {
    jQuery("#searchBox").css({"left": mouseX});
    jQuery("#searchBox").css({"top": mouseY});
    jQuery("#searchBox").fadeIn();
    open = true;
    jQuery('#searchText').focus();
  }
  else {
    jQuery("#searchBox").fadeOut();
    jQuery("#searchText").val("");
    open = false;
  }
}

function rightClickMenu(e) {
  jQuery('#menu').css({
                        top: e.pageY + 'px',
                        left: e.pageX + 'px'
                      }).show();
}

function showDetails(track, i, j) {
  if (window[track][i].transcript[j].domain.length > 1) {
    var details = window[track][i].transcript[j].domain.replace(/,/g, "<br>");

    jQuery.colorbox({
                      width: "90%",
                      html: "<b>Attributes</b><hr><span id=domain>" + window[track][i].transcript[j].domain.replace(/(GO:[0-9]+)/g, "<a href='http://www.ebi.ac.uk/QuickGO/GTerm?id=$1' target='_blank'>$1</a>").replace(/,/g, "<br>") + "</span>" });
  }
}

function showPeptides(track, i, k) {
  var seq = "";
  Fluxion.doAjax(
          'dnaSequenceService',
          'loadSequence',
          {'query': seqregname, 'from': window[track][i].start, 'to': window[track][i].end, 'url': ajaxurl},
          {'doOnSuccess': function (json) {

            seq = json.seq;
            var cdnaseq = "";
            var noofexons = window[track][i].transcript[k].Exons.length;
            if (noofexons > 0) {
              for (var j = 0; j < noofexons; j++) {
                var exon_start = window[track][i].transcript[k].Exons[j].start;
                var exon_end = window[track][i].transcript[k].Exons[j].end;
                var transcript_start = window[track][i].transcript[k].transcript_start;
                var transcript_end = window[track][i].transcript[k].transcript_end;
                var track_start = window[track][i].transcript[k].start;
                var track_end = window[track][i].transcript[k].end;

                if (exon_start <= transcript_start && exon_end >= transcript_end) {
                  cdnaseq = seq.substring(parseInt(transcript_start) - parseInt(track_start) - 1, parseInt(transcript_end) - parseInt(track_start));
                }
                else if (exon_start <= transcript_start) {
                  cdnaseq = seq.substring(parseInt(transcript_start) - parseInt(track_start) - 1, parseInt(exon_end) - parseInt(track_start));
                }
                else if (exon_end >= transcript_end) {
                  cdnaseq += seq.substring(parseInt(exon_start) - parseInt(track_start) - 1, parseInt(transcript_end) - parseInt(track_start));
                  break;
                }
                else if (exon_start > transcript_start) {
                  cdnaseq += seq.substring(parseInt(exon_start) - parseInt(track_start) - 1, parseInt(exon_end) - parseInt(track_start));
                }
                else {
                }

              }
              if (window[track][i].strand == -1) {
                var temp = "";
                var temp2 = cdnaseq.split("").reverse();
                for (var j = 0; j < temp2.length; j++) {
                  if (temp2[j] == "A") {
                    temp += "T";
                  }
                  else if (temp2[j] == "C") {
                    temp += "G";
                  }
                  else if (temp2[j] == "G") {
                    temp += "C";
                  }
                  else if (temp2[j] == "T") {
                    temp += "A";
                  }
                  else {
                    temp += "N";
                  }
                }
                cdnaseq = temp;
              }
            }
            var oldcdnaseq = cdnaseq;
            var oldPeptideSeq = convertPeptide(oldcdnaseq);
            cdnaseq = convertFasta(cdnaseq)
            var peptideseq = convertFasta(oldPeptideSeq);
            jQuery.colorbox({
                              width: "90%",
                              html: "<table><tr><td><button id=\"peptidebutton\" class='ui-state-default ui-corner-all' " +
                                    "onclick=\"sequenceToogle();\">Peptide Sequence</button><br/>" +
                                    "</td></td></tr></table><br/>" +
                                    "<div id='cdnasequence' style='display : inline; font-family: Courier, \"Courier New\", monospace'><b>cDNA Seq</b><hr>" + cdnaseq + "</div><div id='peptidesequence' style='display : none; font-family: Courier, \"Courier New\", monospace'><b>Peptide Seq</b><hr>" + peptideseq + "</div>" });


          }
          });

}

function sequenceToogle() {
  jQuery("#cdnasequence").toggle();
  jQuery("#peptidesequence").toggle();
  if (jQuery("#peptidebutton").text() == "cDNA Sequence") {
    jQuery("#peptidebutton").text("Peptide Sequence");
  }
  else {
    jQuery("#peptidebutton").text("cDNA Sequence");
  }

}
function convertPeptide(cdnaseq) {

  var ptn_seq = "";
  var seq = cdnaseq;


  var i = 0;
  for (i; i <= seq.length - 3; i = i + 3) {
    var chunk = seq.substring(i, i + 3);
    if (chunk.indexOf("N") > -1) {
      ptn_seq += "X";
    }
    else if (chunk == "GCT" || chunk == "GCC" || chunk == "GCA" || chunk == "GCG") {
      ptn_seq += "A";
    }
//    CGU, CGC, CGA, CGG, AGA, AGG
    else if (chunk == "CGT" || chunk == "CGC" || chunk == "CGA" || chunk == "CGG" || chunk == "AGA" || chunk == "AGG") {
      ptn_seq += "R";
    }
//    AAU, AAC
    else if (chunk == "AAT" || chunk == "AAC") {
      ptn_seq += "N";
    }
//    GAU, GAC
    else if (chunk == "GAT" || chunk == "GAC") {
      ptn_seq += "D";
    }
//    UGU, UGC
    else if (chunk == "TGT" || chunk == "TGC") {
      ptn_seq += "C";
    }
//    CAA, CAG
    else if (chunk == "CAA" || chunk == "CAG") {
      ptn_seq += "Q";
    }
//    GAA, GAG
    else if (chunk == "GAA" || chunk == "GAG") {
      ptn_seq += "E";
    }
//      GGU, GGC, GGA, GGG
    else if (chunk == "GGT" || chunk == "GGC" || chunk == "GGA" || chunk == "GGG") {
      ptn_seq += "G";
    }
//    CAU, CAC
    else if (chunk == "CAT" || chunk == "CAC") {
      ptn_seq += "H";
    }
//      AUU, AUC, AUA
    else if (chunk == "ATT" || chunk == "ATC" || chunk == "ATA") {
      ptn_seq += "I";
    }
//     AUG
    else if (chunk == "ATG") {
      ptn_seq += "M";
    }
//     UUA, UUG, CUU, CUC, CUA, CUG
    else if (chunk == "TTA" || chunk == "TTG" || chunk == "CTT" || chunk == "CTC" || chunk == "CTA" || chunk == "CTG") {
      ptn_seq += "L";
    }
//         AAA, AAG
    else if (chunk == "AAA" || chunk == "AAG") {
      ptn_seq += "K";
    }
//    UUU, UUC
    else if (chunk == "TTT" || chunk == "TTC") {
      ptn_seq += "F";
    }
    //    CCU, CCC, CCA, CCG
    else if (chunk == "CCT" || chunk == "CCC" || chunk == "CCA" || chunk == "CCG") {
      ptn_seq += "P";
    }
    //  UCU, UCC, UCA, UCG, AGU, AGC
    else if (chunk == "TCT" || chunk == "TCC" || chunk == "TCA" || chunk == "TCG" || chunk == "AGT" || chunk == "AGC") {
      ptn_seq += "S";
    }
    //      ACU, ACC, ACA, ACG
    else if (chunk == "ACT" || chunk == "ACC" || chunk == "ACA" || chunk == "ACG") {
      ptn_seq += "T";
    }
    //      UGG
    else if (chunk == "TGG") {
      ptn_seq += "W";
    }
//    UAU, UAC
    else if (chunk == "TAT" || chunk == "TAC") {
      ptn_seq += "Y";
    }
    //   GUU, GUC, GUA, GUG
    else if (chunk == "GTT" || chunk == "GTC" || chunk == "GTA" || chunk == "GTG") {
      ptn_seq += "V";
    }
    //  	UAA, UGA, UAG
    else if (chunk == "TAA" || chunk == "TGA" || chunk == "TAG") {
      ptn_seq += "*";
    }

    else {
      ptn_seq += "-";
    }
  }

  return ptn_seq;
  alert("here");
}

function fetchFasta(begin, end, track, i, j) {
  var reverseText = "";

  if (window[track][i].transcript[j].start > window[track][i].transcript[j].end) {

    reverseText = "<br/><b>(Minus strand, you will need to reverse-complement)</b><br/><br/>";
  }

  jQuery.colorbox({
                    width: "90%",
                    height: "100%",
                    html: "<table><tr><td><button class='ui-state-default ui-corner-all' " +
                          "onclick=\"selectText('fastaoutput');\"')\">Select Sequence</button><br/>" +
                          "<td><div id=fastadownload></div></td></td></tr></table><br/>" +
                            // "<b>Position: </b>" + begin + " - " + end+
                          "<br/><b>Fasta:</b> <br/>" + reverseText +
                          "<div id=\"fastaoutput\" style=' font-family: Courier, \"Courier New\", monospace'><img style='position: relative; left: 50%; ' src='./images/browser/loading_big.gif' alt='Loading'></div>"});

  Fluxion.doAjax(
          'dnaSequenceService',
          'loadSequence',
          {'query': seqregname, 'from': begin, 'to': end, 'url': ajaxurl},
          {'doOnSuccess': function (json) {
            var seq = (json.seq).toLowerCase();
            if (i) {
              var start, stop;

              if (window[track][i].transcript[j].start > window[track][i].transcript[j].end) {
                start = window[track][i].transcript[j].end;
                stop = window[track][i].transcript[j].start;
              }
              else {
                start = window[track][i].transcript[j].start;
                stop = window[track][i].transcript[j].end;
              }
              var exons = window[track][i].transcript[j].Exons.length;
              for (var k = 0; k < exons; k++) {
                var substart, subend;
                if (window[track][i].transcript[j].start > window[track][i].transcript[j].end) {
                  substart = window[track][i].transcript[j].Exons[k].end - start;
                  subend = window[track][i].transcript[j].Exons[k].start - start;
                }
                else {
                  substart = window[track][i].transcript[j].Exons[k].start - start;
                  subend = window[track][i].transcript[j].Exons[k].end - start;
                }
                var exonSeq = seq.substring(substart,subend);
                seq = seq.substring(0,substart)+exonSeq.toUpperCase()+seq.substring(subend+1,seq.length);
              }
              jQuery('#fastaoutput').html(">" + seqregname + ": " + begin + " - " + end + " <font color='green'> " + convertFasta(seq) + "</font>");
              jQuery('#fastaoutput').each(function () {
                var pattern = /([ATCG]+)/g;
                var before = '<span style="color: red;">';
                var after = '</span>';
                jQuery(this).html(jQuery('#fastaoutput').html().replace(pattern, before + "$1" + after));
              });

            }
            else {
              jQuery('#fastaoutput').html(">" + seqregname + ": " + begin + " - " + end + convertFasta(seq));
            }
            jQuery('#fastadownload').html("<button class='ui-state-default ui-corner-all' " +
                                          "onclick=fastaFile('" + seq + "'," + begin + "," + end + ") \">Prepare Download Sequence File</button>");
          }
          });

}


function blast(begin, end, hit, blastdb) {
//  if (end - begin < 10000) {
  Fluxion.doAjax(
          'dnaSequenceService',
          'loadSequence',
          {'query': seqregname, 'from': begin, 'to': end, 'url': ajaxurl},
          {'doOnSuccess': function (json) {
            var seq = json.seq;
            blastTrackSearch(seq, begin, end, hit, blastdb);
          }
          });


//    blastTrackSearch(seq.substring(begin, end), begin, end, hit, blastdb);
//  }
//  else {
//    alert("BLAST limit applies less than 10kb");
//  }

  removeMenu();
}

function convertFasta(string) {
  var start = 0;
  var end = 69;
  var oldString = string;
  var newString = "";

  while (oldString.length > 70) {
    newString = newString + '<br/>' + oldString.substring(start, end);
    oldString = oldString.substring(end, oldString.length);
//    start = start + 70;
//    end = end + 70;
  }
  newString += "<br />" + oldString;


  return newString;
}

function selectText(element) {
  var doc = document;
  var text = doc.getElementById(element);

  if (doc.body.createTextRange) { // ms
    var range = doc.body.createTextRange();
    range.moveToElementText(text);
    range.select();
  }
  else if (window.getSelection) { // moz, opera, webkit
    var selection = window.getSelection();
    var range = doc.createRange();
    range.selectNodeContents(text);
    selection.removeAllRanges();
    selection.addRange(range);
  }
}

function removeMenu() {
  jQuery('#menu').hide();
}


function removeAllPopup() {
  removeMenu();
  removePopup();
  removeDragPopup();
  removeBlastPopup();
}