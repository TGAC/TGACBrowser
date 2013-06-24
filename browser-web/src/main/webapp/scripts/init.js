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

function setBlast() {
  if (jQuery("#blastType").text().indexOf('local') >= 0) {


    jQuery.getScript("scripts/blast_local.js", function (data, textStatus, jqxhr) {
      console.log('Load was performed.');
    });

    jQuery("#blastdbs").show();
  }
  else if (jQuery("#blastType").text().indexOf('ncbi') >= 0) {
    jQuery.getScript("scripts/blast_ncbi.js", function (data, textStatus, jqxhr) {
      console.log('Load was performed.');
    });
    jQuery("#ncbiblastdbs").show();
  }
  else if (jQuery("#blastType").text().indexOf('server') >= 0) {
    jQuery.getScript("scripts/blast_server.js", function (data, textStatus, jqxhr) {
      console.log('Load was performed.');
    });
    jQuery("#blastdbs").show();
  }
}

function onLoad() {
  setBlast()
  path = jQuery('#title').text();
  jQuery("#notifier").hide()
  jQuery("#sessionid").hide()
  jQuery("#map").hide()


  jQuery(document).ready(function () {
    if (jQuery.browser.msie) {
      jQuery("#alertDiv").html("<img src=\"images/browser/alert.gif\" alt=\"\">Internet Explorer detected. Please use Google Chrome, Safari or Mozilla Firefox");
      jQuery("#alertDiv").show();
    }
    else {
      jQuery("#alertDiv").hide()
    }
  });

  jQuery(function () {
    jQuery(".feature_tracks").resizable({
                                          animate: true,
                                          handles: "s,se,sw"
                                        });
  });

  jQuery(function () {
    var testTextBox = jQuery('#search');
    var code = null;
    testTextBox.keypress(function (e) {
      code = (e.keyCode ? e.keyCode : e.which);
      if (code == 13) {
        search(jQuery('#search').val());
      }
    });

  });

  jQuery(function () {
    var testTextBox = jQuery('#begin');
    var code = null;
    testTextBox.keypress(function (e) {
      code = (e.keyCode ? e.keyCode : e.which);
      if (code == 13) {
        jumpToSeq();
      }
    });
  });

  jQuery(function () {
    var testTextBox = jQuery('#end');
    var code = null;
    testTextBox.keypress(function (e) {
      code = (e.keyCode ? e.keyCode : e.which);
      if (code == 13) {
        jumpToSeq();
      }
    });
  });


  jQuery("#searchBox").hide();

  jQuery("#draggable").bind("contextmenu", function (e) {
    rightClickMenu(e);
    return false;
  });
  jQuery('#wrapper #tracks').mouseup(function (e) {
    if (e.which == 3 && e.button == 2) {
      if (e.which == 3 && e.button == 2 && rightclick) {
        var clicked = jQuery('#currentposition').html();
        setBegin(parseInt(getBegin()) - ( (clicked - parseInt(getBegin())) * 0.4));
        setEnd(parseInt(getEnd()) + ( (parseInt(getEnd()) - clicked) * 0.4));
        jumpToSeq();
        rightclick = false;
      }
      else {
        rightclick = true;
        setTimeout("rightclick = false", 200);
      }
    }
    else {
    }
  });

  jQuery('#wrapper #tracks').dblclick(function (e) {
    var clicked = jQuery('#currentposition').html();
    setBegin(parseInt(getBegin()) + ( (clicked - parseInt(getBegin())) * 0.4));
    setEnd(parseInt(getEnd()) - ( (parseInt(getEnd()) - clicked) * 0.4));
    jumpToSeq();
  });

  jQuery(".track").live('click', function (e) {
    if (e.ctrlKey) {
      removeAllPopup();
      jQuery(this).css("border", "1px solid black");
    }
  });


  jQuery(document).keydown(function (e) {
    if (e.ctrlKey || e.keyCode == 17) {
      ctrldown = true;
    }
  });

  jQuery(document).keyup(function (e) {
    if (e.ctrlKey || e.keyCode == 17) {
      ctrldown = false;
    }
  });

  jQuery(document).keyup(function (e) {
    if ((e.ctrlKey || e.keyCode == 17) && grouplist.length > 0) {
      jQuery("#makegrouplist").html("Group selected tracks on top");
      jQuery("#makegroup").show();
    }
  });

  jQuery(window).unload(function () {
    if (randomnumber) {
      saveSession();
    }
    var track_list_cookie = [];
    jQuery(track_list).each(function (index) {
      track_list_cookie.push(
              {name: track_list[index].name, disp: window["track_list" + track_list[index].name].disp, display_label: window["track_list" + track_list[index].name].display_label}
      );
    });
    jQuery.cookie('trackslist', track_list_cookie.toJSON(), { path: '/', expires: 10});
  });

  jQuery("#draggable").mouseover(function () {
  });

  jQuery("#seqdrag").hide();
  jQuery("#searchresult").hide();

  jQuery(function () {
    jQuery(window).resize(function () {
      minWidth = findminwidth();
      jumpToSeq();
    });
  });


  jQuery('#EditTrack').hide();

  jQuery("#bar_image").click(function (e) {
    dragtohere(e);
  });

  jQuery("#draggable").draggable(
          {
            axis: "x",
            containment: "parent"
          });

  var dragstart = 0;
  jQuery("#wrapper").draggable(
          {
            handle: "#tracks",
            axis: "x",
            start: function () {
            },
            drag: function () {

              jQuery(".handle").each(function (i) {
                jQuery(this).css("left", '1%');
                jQuery(this).css("top", parseInt(jQuery(this).parent().position().top) - parseInt(jQuery(window).scrollTop()) + (parseInt(jQuery("#wrapper").position().top) + parseInt(jQuery("#sequence").css('height'))) + (parseInt(jQuery("#canvas").position().top)) + 5);
                jQuery(this).css("position", 'fixed');
              });
            },
            stop: function () {
              jQuery(".handle").css("position", 'absolute');
              jQuery(".handle").css("left", '25.2%');
              jQuery(".handle").css("top", '5px');
              trackDrag();
            }
          });

  jQuery("#tracks").sortable(
          {
            axis: 'y',
            handle: '.handle',
            cursor: 'move',
            start: function () {
              removeAllPopup()
            }

          });

  jQuery("#wrapper").bind("contextmenu", function (e) {
    return false;
  });

  jQuery(document).keydown(function (e) {
    if (!jQuery(document.activeElement).is('input')) {
      keyControl(e);
    }
  });


  jQuery("#mergedtrack").hide();

  var seqX = 0;

//
//  Drag popup codes
  jQuery('#sequence').mousedown(function (e) {
    e.preventDefault();
    removeDragPopup();
    seqX = mouseX;
    var begin, end
    jQuery("#seqdrag").css("top", jQuery('#sequence').offset().top);
    jQuery("#seqdrag").css("left", seqX);
    jQuery("#seqdrag").css("width", "1px");
    jQuery("#seqdrag").css('height', Math.round(jQuery('#canvas').height() - (jQuery('#sequence').offset().top - jQuery('#canvas').offset().top)));
    jQuery("#seqdrag").show();
    jQuery(window).bind('mousemove', function () {
      if (mouseX > seqX) {
        var seqWidth = parseFloat(mouseX) - parseFloat(seqX);
        jQuery("#seqdrag").css("width", seqWidth);
      }
      else {
        var seqWidth = parseFloat(seqX) - parseFloat(mouseX);
        jQuery("#seqdrag").css("width", seqWidth);
        jQuery("#seqdrag").css("left", mouseX);
      }

      begin = parseInt(getBegin()) - 1 + Math.round((getEnd() - getBegin()) * (seqX ) / parseFloat(maxLen) + getBegin());
      end = parseInt(getBegin()) + Math.round((getEnd() - getBegin()) * (mouseX) / parseFloat(maxLen) + getBegin());

      var diff;
      if (parseInt(begin) < parseInt(end)) {
        diff = parseInt(end) - parseInt(begin);

      }
      else {
        diff = parseInt(begin) - parseInt(end);
      }

      var bp = "bp";
      if (diff > 100000000) {
        diff = (diff / 1000000);
        bp = "Gbp";
      }
      else if (diff > 1000000) {
        diff = (diff / 1000000);
        bp = "Mbp";
      }
      else if (diff > 1000) {
        diff = diff / 1000;
        bp = "Kbp";
      }
      jQuery("#dragLabel").html(diff + bp);

    });

    jQuery(window).bind('mouseup', function () {

      if (parseInt(begin) < parseInt(end) && parseInt(end) - parseInt(begin) > 3) {
        jQuery(window).unbind('mousemove mouseleave');
        newDragpopup(begin, end, "true");
        jQuery(window).unbind('mouseup');
      }
      else if (parseInt(begin) - parseInt(end) > 3) {
        jQuery(window).unbind('mousemove mouseleave');
        newDragpopup(end, begin, "false");
        jQuery(window).unbind('mouseup');
      }
      else {
        jQuery(window).unbind('mousemove mouseleave');
        jQuery("#seqdrag").hide();
        jQuery(window).unbind('mouseup');
      }
    });
  });


  jQuery('#popup').hide();
  jQuery('#blastpopup').hide();
  jQuery('#dragpopup').hide();
  jQuery('#track-upload-form').hide();
}

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

// Scroll control on off
function scrollSwitcher() {

  if (jQuery.browser.mozilla == true) {
    if (jQuery('#scrollswitch').is(':checked')) {
      document.getElementById("wrapper").addEventListener('DOMMouseScroll', scrollZoom, false);
    }
    else {
      document.getElementById("wrapper").removeEventListener('DOMMouseScroll', scrollZoom, false);
    }

  }
  else {
    if (jQuery('#scrollswitch').is(':checked')) {
      document.getElementById("wrapper").addEventListener('mousewheel', scrollZoom, false);
    }
    else {
      document.getElementById("wrapper").removeEventListener('mousewheel', scrollZoom, false);
    }
  }


}

// Calculate guideline and current position
function displayCursorPosition() {

  jQuery("#wrapper").mouseenter(function () {
    if (jQuery('#currentpositionswitch').is(':checked')) {
      jQuery('#currentposition').show();
    }
    if (jQuery('#guidelineswitch').is(':checked')) {
      jQuery('#guideline').show();
    }
  });

  jQuery("#wrapper .vertical-line").mouseleave(function () {
    jQuery('#currentposition').hide();
  });

  jQuery(document).mousemove(function (e) {
    mouseX = e.pageX;
    mouseY = e.pageY;
    var pos = parseInt(getBegin()) + Math.round((( (e.pageX) * (getEnd() - getBegin())) / jQuery(window).width()) + getBegin());
    jQuery('#currentposition').html(pos);

    jQuery('#currentposition').css({
                                     left: Math.round(e.pageX + 15),
                                     top: e.pageY
                                   });
    if (jQuery('#guidelineswitch').is(':checked')) {
      jQuery('#guideline').css({
                                 height: Math.round(jQuery('#canvas').height() - (jQuery('#sequence').offset().top - jQuery('#canvas').offset().top)),
                                 left: Math.round(e.pageX + 0),
                                 top: jQuery('#sequence').offset().top
                               });
    }
  });
  jQuery('#canvas').mouseleave(function () {
    jQuery('#currentposition').hide();
    jQuery('#guideline').hide();
  });
}

//Display cordinates as percentage
function dispSeqCoord() {
  var diff = parseInt(parseInt(sequencelength) / 20);
  var bp = "bp";
  if (diff > 100000000) {
    diff = (diff / 1000000);
    bp = "Gbp";
  }
  else if (diff > 1000000) {
    diff = (diff / 1000000);
    bp = "Mbp";
  }
  else if (diff > 1000) {
    diff = diff / 1000;
    bp = "Kbp";
  }
  jQuery("#zoomoutbig").attr('title', "Zoom Out(" + diff + "" + bp + ")");
  jQuery("#zoominbig").attr('title', "Zoom In(" + +diff + "" + bp + ")");
  var diff = parseInt(parseInt(sequencelength) / 40);
  var bp = "bp";
  if (diff > 100000000) {
    diff = (diff / 1000000);
    bp = "Gbp";
  }
  else if (diff > 1000000) {
    diff = (diff / 1000000);
    bp = "Mbp";
  }
  else if (diff > 1000) {
    diff = diff / 1000;
    bp = "Kbp";
  }
  jQuery("#zoomoutsmall").attr('title', "Zoom Out(" + diff + "" + bp + ")");
  jQuery("#zoominsmall").attr('title', "Zoom In(" + +diff + "" + bp + ")");
  var len = sequencelength;
  jQuery('#SeqLenStart').html(0);
  jQuery('#SeqLen25').html(parseInt(parseInt(len) / 4));

  jQuery('#SeqLenMid').html(parseInt(parseInt(len) / 2));
  jQuery('#SeqLen75').html(parseInt(parseInt(len) / 4 * 3));

  jQuery('#SeqLenEnd').html(len);

}

// set begin and end value
function dispCoord(seqStart, seqEnd) {

  var begin = parseInt(seqStart);
  var end = parseInt(seqEnd);

  setBegin(begin);
  setEnd(end);
  if (chromosome) {
    setMapMarkerTop(getBegin());
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
      graph: Tracklist[i].graph
    }
  }
  var tracks = "<table> <tr>";
  var mergeTrack = "<table> <tr>";


  for (var i = 0; i < Tracklist.length; i++) {

    tracks += "<td> <span title='" + Tracklist[i].desc + "'><input type=\"checkbox\" id='" + Tracklist[i].name + "Checkbox' name='" + Tracklist[i].name + "-" + Tracklist[i].id + "'  onClick=loadTrackAjax(\"" + Tracklist[i].id + "\",\"" + Tracklist[i].name + "\"); />  " + Tracklist[i].display_label + " </span> </td>";
    mergeTrack += "<td><span id='" + Tracklist[i].name + "span'> <input type=\"checkbox\" disabled id='" + Tracklist[i].name + "mergedCheckbox' name='" + Tracklist[i].name + "mergedCheckbox' onClick=mergeTrack(\"" + Tracklist[i].name + "\"); value=" + Tracklist[i].name + " >" + Tracklist[i].display_label + "  </span> </td>";

    if ((i + 1) % 3 == 0) {
      tracks += "</tr> <tr>";
      mergeTrack += "</tr> <tr>";
    }

    if (Tracklist[i].name.toLowerCase().indexOf("snp") >= 0) {

    }
    else if (Tracklist[i].name.toLowerCase().indexOf("gene") >= 0) {
      jQuery("<style type='text/css'> ." + Tracklist[i].display_label + "_exon" + "{ background:" + Tracklist[i].colour + ";border: 1px solid " + Tracklist[i].colour + ";} </style>").appendTo("head");
      jQuery("<style type='text/css'> ." + Tracklist[i].display_label + "_utr" + "{ border: 1px solid " + Tracklist[i].colour + "; background:none repeat scroll 0 0 white;} </style>").appendTo("head");
      jQuery("<style type='text/css'> ." + Tracklist[i].display_label + "_graph{ border:1px solid black; background:" + Tracklist[i].colour + ";} </style>").appendTo("head");

    }
    else {
      jQuery("<style type='text/css'> ." + Tracklist[i].display_label + "{ background:" + Tracklist[i].colour + ";} </style>").appendTo("head");
      jQuery("<style type='text/css'> ." + Tracklist[i].display_label + "_graph{ border:1px solid black; background:" + Tracklist[i].colour + ";} </style>").appendTo("head");

    }
  }
  jQuery("#mergetracklist").html(mergeTrack);
  jQuery("#tracklist").html(tracks);
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
    jQuery("#tracks").append("<div id='" + Tracklist[i].name + "_wrapper' class='feature_tracks' style=\"display:none\">" +
                             "<div align='left' class='handle'>" +
                             "<table>" +
                             "<tr>" +
//                             "<td><div onclick=\"toggleLeftInfo(jQuery('" + Tracklist[i].display_label + "_arrowclick'), '" + Tracklist[i].display_label + "_div');\"> " +
//                             "<div id='" + Tracklist[i].display_label + "_arrowclick' class=\"toggleRight\"></div> " +
//                             "</div></td>" +
                             "<td><b>" + Tracklist[i].display_label + "</b></td>" +
                             "<td><div class=\"ui-icon ui-icon-comment\" onclick=toogleLabel(\"" + Tracklist[i].name + "\");> </div></td>" + checkGene(Tracklist[i].name) +
                             "<td><div class='closehandle ui-icon ui-icon-close' onclick=removeTrack(\"" + Tracklist[i].name + "_div\",\"" + Tracklist[i].name + "\");></div></td>" +
                             "</tr>" +
                             "</table>" +
                             "</div>" +
                             "<div id='" + Tracklist[i].name + "_div' class='feature_tracks' style=\"display:none; top:10px;\" > " + Tracklist[i].name + "</div>" +
                             "</div>");
    jQuery(function () {
      jQuery("#" + Tracklist[i].name + "_wrapper").resizable({
                                                               handles: "s",
                                                               alsoResize: "#" + Tracklist[i].name + "_div",
                                                               minHeight: "50px",
                                                               borderBottom: '1px solid black'
                                                             });
    });
  }
  function checkGene(track) {
    if (track.toLowerCase().indexOf('gene') >= 0) {
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
  console.log("loadDefaultTrack")
  var Tracklist = tracklist;
  var cookietest = []
  if (jQuery.cookie('trackslist')) {
    cookietest = JSON.parse(jQuery.cookie('trackslist'));
  }
  else {
    for (var i = 0; i < Tracklist.length; i++) {
      if (Tracklist[i].disp == "1") {
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
      if (v.name == Tracklist[i].name && v.disp == 1) {
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
                  }
                  else {
                    window['track_list' + json.name].graph = "false";
                  }
                  window[trackname] = json[trackname];
                  trackToggle(trackname);
                }
                });


        jQuery('#' + Tracklist[i].name + 'Checkbox').attr('checked', true);
        loadTrackAjax(Tracklist[i].id, Tracklist[i].name);
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

function checkSession() {
  var track_list_cookie = [];
  jQuery(track_list).each(function (index) {
    track_list_cookie.push(
            {name: track_list[index].name, disp: window["track_list" + track_list[index].name].disp}
    );
  });
  var now = new Date();
  if (randomnumber == null) {
    randomnumber = seqregname + now.getDate() + "-" + now.getMonth() + 1 + "" + now.getFullYear() + "" + now.getHours() + "" + now.getMinutes() + "" + Math.ceil(Math.random() * 5);
  }
  jQuery("#sessionid").html("<b>Session Id: </b><a  href='./session.jsp?query=" + randomnumber + "' target='_blank'>" + randomnumber + "</a> Saved at " + now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds());
  jQuery("#sessionid").show("blind", {}, 500);
  saveSession();
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

function dragToogle() {
  if (jQuery("#dragRadio").is(':checked')) {
    jQuery('#wrapper').css('cursor', 'default');
    jQuery('#wrapper').unbind('mousedown');
    var dragstart = 0;
    jQuery("#wrapper").draggable(
            {

              axis: "x",
              start: function () {
              },
              drag: function () {

                jQuery(".handle").each(function (i) {
                  jQuery(this).css("left", '1%');
                  jQuery(this).css("top", (jQuery(this).parent().position().top) - parseInt(jQuery(window).scrollTop()) + (parseInt(jQuery("#wrapper").position().top)) + (parseInt(jQuery("#canvas").position().top)));
                  jQuery(this).css("position", 'fixed');
                });
//              var temp = parseFloat(jQuery('#canvas').css("left")) - parseFloat(jQuery('#wrapper').css("left"));
//
//              console.log(temp+" "+parseFloat(jQuery('#wrapper').css("left")));
//              var beginnew = parseFloat(getBegin()) + parseFloat((getEnd() - getBegin()) * temp / parseFloat(maxLen));
//              var endnew = parseFloat(getEnd()) + parseFloat((getEnd() - getBegin()) * temp / parseFloat(maxLen));
//
//              if (beginnew <= 0) {
//                beginnew = 1;
//              } else  if (endnew >= parseFloat(sequencelength)) {
//                endnew = sequencelength;
//              }
//              else{
//
//                 var seqStart = parseInt(beginnew) * parseInt(maxLen) / sequencelength;
//              removeAllPopup();
//              setDragableLeft(seqStart);
//              setbglayerLeft(seqStart, false);
//                setBegin(beginnew);
//              setEnd(endnew);
//              }
//
//
              },
              stop: function () {
                jQuery(".handle").css("position", 'absolute');
                jQuery(".handle").css("left", '25%');
                jQuery(".handle").css("top", '0px');
                trackDrag();
              }
            });

    jQuery("#tracks").sortable(
            {
              axis: 'y',
              handle: '.handle',
              cursor: 'move',
              start: function () {
                removeAllPopup()
              }

            });

  }
  else {
    var seqX = 0;
    jQuery('#wrapper').draggable("destroy");
    jQuery('#wrapper').css('cursor', 'crosshair');
//
//  Drag popup codes
    jQuery('#wrapper').mousedown(function (e) {
      e.preventDefault();
      removeDragPopup();
      seqX = mouseX;
      var begin, end
      jQuery("#seqdrag").css("top", jQuery('#sequence').offset().top);
      jQuery("#seqdrag").css("left", seqX);
      jQuery("#seqdrag").css("width", "1px");
      jQuery("#seqdrag").css('height', Math.round(jQuery('#canvas').height() - (jQuery('#sequence').offset().top - jQuery('#canvas').offset().top)));
      jQuery("#seqdrag").show();
      jQuery(window).bind('mousemove', function () {
        if (mouseX > seqX) {
          var seqWidth = parseFloat(mouseX) - parseFloat(seqX);
          jQuery("#seqdrag").css("width", seqWidth);
        }
        else {
          var seqWidth = parseFloat(seqX) - parseFloat(mouseX);
          jQuery("#seqdrag").css("width", seqWidth);
          jQuery("#seqdrag").css("left", mouseX);
        }

        begin = parseInt(getBegin()) - 1 + Math.round((getEnd() - getBegin()) * (seqX) / parseFloat(maxLen) + getBegin());
        end = parseInt(getBegin()) + Math.round((getEnd() - getBegin()) * (mouseX) / parseFloat(maxLen) + getBegin());
        var bp = "bp";
        var diff;
        if (parseInt(begin) < parseInt(end)) {
          diff = parseInt(end) - parseInt(begin);

        }
        else {
          diff = parseInt(begin) - parseInt(end);
        }
        if (diff > 100000000) {
          diff = (diff / 1000000);
          bp = "Gbp";
        }
        else if (diff > 1000000) {
          diff = (diff / 1000000);
          bp = "Mbp";
        }
        else if (diff > 1000) {
          diff = diff / 1000;
          bp = "Kbp";
        }

        jQuery("#dragLabel").html(diff + bp);

      });

      jQuery(window).bind('mouseup', function () {

        if (parseInt(begin) < parseInt(end) && parseInt(end) - parseInt(begin) > 3) {
          jQuery(window).unbind('mousemove mouseleave');
          newDragpopup(begin, end, "true");
          jQuery(window).unbind('mouseup');
        }
        else if (parseInt(begin) - parseInt(end) > 3) {
          jQuery(window).unbind('mousemove mouseleave');
          newDragpopup(end, begin, "false");
          jQuery(window).unbind('mouseup');
        }
        else {
          jQuery(window).unbind('mousemove mouseleave');
          jQuery("#seqdrag").hide();
          jQuery(window).unbind('mouseup');
        }
      });
    });


  }
}

function tooglehangingmenu() {

  jQuery("#popup_hanging").css('left', mouseX + 5);
  jQuery("#popup_hanging").css('top', mouseY + 5);

  jQuery("#popup_hanging").toggle();
}

function selectAllCheckbox() {
  if (jQuery("#selectAllCheckbox").is(':checked')) {
    jQuery("#tracklist input").each(function () {
      if (jQuery(this).is(':checked')) {
        //    do nothing
      }
      else {
        var name_splitter = jQuery(this).attr('name');
        jQuery(this).attr('checked', 'checked');
        eval(jQuery(this).attr('onClick'));
      }
    })
//    trackToggle("all")
  }
  else {
//     jQuery("#tracklist input").each(function () {
//     if (jQuery(this).is(':checked')) {
//       jQuery(this).attr('checked', false);
//       eval(jQuery(this).attr('onClick'));
//     }
//     else {
//       //    do nothing
//     }
//   })
  }

}

function unSelectAllCheckbox() {
  if (jQuery("#unSelectAllCheckbox").is(':checked')) {
    jQuery("#tracklist input").each(function () {
      if (jQuery(this).is(':checked')) {
        jQuery(this).attr('checked', false);
        window['track_list' + this.id.replace("Checkbox", "")].disp = 0
      }
      else {
        //    do nothing
      }
    })
  }
  trackToggle("all")

}