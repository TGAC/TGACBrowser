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
 * Date: 2/28/12
 * Time: 12:18 PM
 * To change this template use File | Settings | File Templates.
 */

var userCDS, userSNPs, userExon;
var cdsTrack = "";
var snpTrack = "";
var exonTrack = "";
function uploadTrack() {
  showTrackData();
  jQuery("#track-upload-form").dialog({
                                        autoOpen: false,
                                        height: 500,
                                        width: 550,
                                        modal: true,
                                        buttons: {
                                          "Update Data": function() {
                                            userCDS = cdsTrack
                                            userSNPs = snpTrack;
                                            userExon = exonTrack;
                                            trackToggle("all");
                                            jQuery("#track-upload-form").dialog("close");


                                          },
                                          Cancel: function() {
                                            jQuery(this).dialog("close");
                                          },
                                          ClearData:function() {
                                            cdsTrack = "";
                                            snpTrack = "";
                                            exonTrack = "";
                                            showTrackData();
                                            jQuery("#filedata").html("<b> File Name: <br> File Type:<br>File Size:</b>");
                                          }

                                        },
                                        close: function() {
                                        }

                                      });
  jQuery("#track-upload-form").dialog("open");
}


function changeField() {
  var track = jQuery('#addfeature').val();

  var snp = "Position <input id='snpposition' onkeypress=\"return isNumberKey(event)\" type='text' size='5'> " +
            "SNP <input id='snpcigar' onkeyup='onlyOne()' onkeypress=\"return isACGT(event)\" type='text' size='1'> " +
            "<input type='button' value='add' onclick=\"snpPassValue();\">";
  var cds = "Start Position <input id='startposition' onkeypress=\"return isNumberKey(event)\" type='text' size='5'> " +
            "End Position <input id='endposition' onkeypress=\"return isNumberKey(event)\" type='text' size='5'> " +
            "<input type='button' value='add' onclick=\"cdsPassValue();\">";
  var Exon = "Start Position <input id='startposition' onkeypress=\"return isNumberKey(event)\" type='text' size='5'> " +
             "End Position <input id='endposition' onkeypress=\"return isNumberKey(event)\" type='text' size='5'> <br>  " +
             "Strand <input name=strand type='radio' value=+> Forward " +
             "<input name=strand type='radio' value=-> Reverse " +
             "<input type='button' value='add' onclick=\"exonPassValue();\">";
//  var add = "<p onclick=\"snpPassValue();\">add</p>";

  if (track == 'SNP') {
    jQuery("#addData").html(snp);
  }
  else if (track == 'CDS') {
    jQuery("#addData").html(cds);
  }
  else if (track == 'Exon') {
    jQuery("#addData").html(Exon);
  }
  else {
    jQuery("#addData").html("");
  }
  jQuery("#addData").show();

}
function snpPassValue() {

  if (jQuery('#snpcigar').val().length > 0 && jQuery('#snpposition').val().length > 0) {
    snpTrack += "," + jQuery("#snpposition").val() + ":" + (parseInt(jQuery("#snpposition").val()) + parseInt(1)) + ":" + jQuery("#snpcigar").val().toUpperCase();
  }
  else {
    alert("Enter Relevant value");
  }

  jQuery("#snpposition").val("");
  jQuery("#snpcigar").val("");

  showTrackData();

}

function cdsPassValue() {

  if (jQuery('#startposition').val().length > 0 && jQuery('#endposition').val().length > 0) {
    if (parseInt(jQuery('#startposition').val()) < parseInt(jQuery('#endposition').val())) {
      cdsTrack += "," + jQuery("#startposition").val() + ":" + jQuery("#endposition").val();
    }
    else {
      alert("End Position can not be smaller than Start");
    }
  }
  else {
    alert("Enter Relevant value");
  }
  jQuery("#startposition").val("");
  jQuery("#endposition").val("");
  showTrackData();
}


function exonPassValue() {

  if (jQuery('#startposition').val().length > 0 && jQuery('#endposition').val().length > 0) {
    if (parseInt(jQuery('#startposition').val()) < parseInt(jQuery('#endposition').val())) {
      exonTrack += "," + jQuery("#startposition").val() + ":" + jQuery("#endposition").val()
    }
    else {
      alert("End Position can not be smaller than Start");
    }
  }
  else {
    alert("Enter Relevant value");
  }
  jQuery("#startposition").val("");
  jQuery("#endposition").val("");
  showTrackData();
}

function showTrackData() {
  jQuery("#addedTracks").html("<b>SNP:</b> " + snpTrack + "<br><b>CDS:</b> " + cdsTrack + "<br><b> Exon; </b> " + exonTrack);
}

function isNumberKey(evt) {
  var charCode = (evt.which) ? evt.which : event.keyCode
  if (charCode > 31 && (charCode < 48 || charCode > 57))
    return false;

  return true;
}


function isACGT(evt) {

  var charCode = (evt.which) ? evt.which : event.keyCode
  if (charCode == 97 || charCode == 99 || charCode == 103 || charCode == 116 || charCode == 65 || charCode == 67 || charCode == 71 || charCode == 84 || charCode == 8) {
    return true;
  }
  else {
    return false;
  }

}

function onlyOne() {
  jQuery('#snpcigar').val(jQuery('#snpcigar').val().substring(0, 1));
}

function fileupload() {

  jQuery('body').css('cursor', 'wait');
  var photo = document.getElementById("sessionfile");
  // the file is the first element in the files property
  var file = photo.files[0];
  var f = file;

  var r = new FileReader();
  if (photo.files.length < 1) {
    alert('Please select a file!');
    jQuery('body').css('cursor', 'auto');
    return;
  }

  r.onload = (function() {
    var extension = f.name.split(".");
    jQuery("#filedata").html("<b> File Name:</b> " + f.name + "" +
                             "<br> <b> File Type:</b> " + extension.last() + "" +
                             "<br> <b>File Size:</b>" + f.size + " bytes");
    var response = true;
    if (f.size > 5242880) {
      response = confirm("Large file Detected It may slower down system \nDo you wish to continue?")
    }
    if (response == true) {
      if (extension.last() == "json" || extension.last() == "txt") {
        return function(e) {
          readFile(e.target.result.toString());

          jQuery('body').css('cursor', 'auto');
        };
      }
      else {
        alert("Wrong File Type " + extension.last() + " \nIt must be GFF/GFF2/GFF3/GTF/GTF2");
      }
    }
  })(f);
  r.readAsText(f);

  jQuery('body').css('cursor', 'auto');

}

function readFile(uploadedFile) {
  var json = {};
  json = jQuery.parseJSON(uploadedFile);
  seq = json.seq;
  sequencelength = json.seqlen;
  track_list = json.tracklist;
  jQuery("")
  randomnumber = json.session;
  var now = new Date();
  jQuery("#sessionid").html("<b>Session Id: </b><a  href='./session.jsp?query=" + randomnumber + "' target='_blank'>" + randomnumber + "</a> Saved at " + now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds());
  jQuery("#sessionid").show();
  jQuery('#seqnameh1').html(json.reference);
  jQuery('#seqname').html("<br/>");
  jQuery('#canvas').show();
  jQuery('#displayoptions').show();
  jQuery('#sessioninput').fadeOut();
  seqregname = json.reference;
  trackList(track_list);
  minWidth = findminwidth();

  setBegin((sequencelength - minWidth) / 2);
  setEnd(parseInt(getBegin()) + minWidth)
  jumpToSeq();
  dispSeqCoord();
  displayCursorPosition();
  setNavPanel();
//            checkSession();
  reloadTracks(json.tracks, track_list);
//            loadDefaultTrack(track_list);
  jQuery("#controlsbutton").colorbox({width:"90%", inline:true, href:"#controlpanel"});
}


function exportSession() {

  var now = new Date();
  if (randomnumber == null) {
    randomnumber = seqregname + now.getDate() + "-" + now.getMonth() + 1 + "" + now.getFullYear() + "" + now.getHours() + "" + now.getMinutes() + "" + Math.ceil(Math.random() * 5);
    jQuery("#sessionid").html("<b>Session Id: </b><a  href='./session.jsp?query=" + randomnumber + "' target='_blank'>" + randomnumber + "</a> Saved at " + now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds());
    jQuery("#sessionid").show();
  }
  saveSession();
  }

function exportSessionHelp() {

  var Tracklist = track_list;
  var data = "# TGAC-Browser Generated GFF\n# Exported at: " + new Date() + "\n# " + seqregname + "\n# can be loaded back to TGAC-Browser for restarting the same session\n# Dumped from  database.\n#";
  for (i = 0; i < Tracklist.length; i++) {
    if (jQuery('#' + Tracklist[i].name + 'Checkbox').is(':checked')) {
      data += "\n" + seqregname + "\t.\t" + Tracklist[i].name + "\t" + window[Tracklist[i].name][j].start + "\t" + window[Tracklist[i].name][j].end + "\t.\t.\t.\t" + window[Tracklist[i].name][j].desc;
    }
  }
  generateFileLink(data);
}

function textControl(evt) {
  var charCode = (evt.which) ? evt.which : event.keyCode
  if (charCode == 97 || charCode == 99 || charCode == 103 || charCode == 116 || charCode == 110 || charCode == 8 || (charCode < 123 && charCode > 112 ))
    return true;

  return false;
}