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
function onLoad() {

  path = jQuery('#title').text();


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
//    jQuery.cookie('trackslist', track_list.toJSON(), { path: '/' + path, expires: 10});
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

            axis: "x",
            start: function () {
            },
            drag: function () {

              jQuery(".handle").each(function (i) {
                jQuery(this).css("left", '1%');
                jQuery(this).css("top", (jQuery(this).parent().position().top) - parseInt(jQuery(window).scrollTop()) + (parseInt(jQuery("#wrapper").position().top)) + (parseInt(jQuery("#canvas").position().top)));
                jQuery(this).css("position", 'fixed');
              });
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

      begin = parseInt(getBegin()) - 1 + Math.round((getEnd() - getBegin()) * (seqX - jQuery('#canvas').offset().left) / parseFloat(maxLen) + getBegin());
      end = parseInt(getBegin()) + Math.round((getEnd() - getBegin()) * (mouseX - jQuery('#canvas').offset().left) / parseFloat(maxLen) + getBegin());

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

  jQuery("#wrapper").mouseleave(function () {
    jQuery('#currentposition').hide();
  });

  jQuery(document).mousemove(function (e) {
    mouseX = e.pageX;
    mouseY = e.pageY;
    var pos = parseInt(getBegin()) + Math.round((( (e.pageX - jQuery('#canvas').offset().left) * (getEnd() - getBegin())) / jQuery('#canvas').width()) + getBegin());
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
  setMapMarkerTop(getBegin());
}


// Generate automated tracks lists and divs for each track

function trackList(tracklist) {

  var tracks = "<table> <tr>";
  var mergeTrack = "<table> <tr>";

  var Tracklist = tracklist;

  for (var i = 0; i < Tracklist.length; i++) {
    tracks += "<td> <span title='" + Tracklist[i].desc + "'><input type=\"checkbox\" id='" + Tracklist[i].name + "Checkbox' name='" + Tracklist[i].name + "-" + Tracklist[i].id + "'  onClick=loadTrackAjax(\"" + Tracklist[i].id + "\",\"" + Tracklist[i].name + "\"); />  " + Tracklist[i].display_label + " </span> </td>";
    if ((i + 1) % 3 == 0) {
      tracks += "</tr> <tr>";
      mergeTrack += "</tr> <tr>";
    }
    mergeTrack += "<td><span id='" + Tracklist[i].name + "span'> <input type=\"checkbox\" disabled id='" + Tracklist[i].name + "mergedCheckbox' name='" + Tracklist[i].name + "mergedCheckbox' onClick=mergeTrack(\"" + Tracklist[i].name + "\"); value=" + Tracklist[i].name + " >" + Tracklist[i].display_label + "  </span> </td>";
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
                             "<div align='left' class='handle'><table><tr><td><b>" + Tracklist[i].display_label + "</b></td><td><div class=\"ui-icon ui-icon-comment\" onclick=toogleLabel(\"" + Tracklist[i].name + "\");> </div></td>" + checkGene(Tracklist[i].name) +
                             "<td><div class='closehandle ui-icon ui-icon-close' onclick=removeTrack(\"" + Tracklist[i].name + "_div\",\"" + Tracklist[i].name + "\");></div></td></tr></table></div> <div id='" + Tracklist[i].name + "_div' class='feature_tracks' style=\"display:none\" > " + Tracklist[i].name + "</div></div>");
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
function loadDefaultTrack(tracklist) {

  var Tracklist = tracklist;
//  var cookietest = []
//  if (jQuery.cookie('trackslist')) {
//    cookietest = JSON.parse(jQuery.cookie('trackslist'));
//  }

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
                  for (var j = 0; j < track_list.length; j++) {
                    if (track_list[j].name == trackname) {
                      track_list[j].graph = "true";
                    }
                  }
                }
                else {
                  for (var j = 0; j < track_list.length; j++) {
                    if (track_list[j].name == trackname) {
                      track_list[j].graph = "false";
                    }
                  }
                }
                window[trackname] = json[trackname];
                trackToggle(trackname);
              }
              });
    }
//    else {
//      jQuery.each(cookietest, function (j, v) {
//        if (v.name == Tracklist[i].name && v.disp == 1) {
//          jQuery('#' + Tracklist[i].name + 'Checkbox').attr('checked', true);
//          loadTrackAjax(Tracklist[i].id, Tracklist[i].name);
//          return false; // stops the loop
//        }
//      });
//
//      continue;
//
//    }
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
  var now = new Date();
  if (randomnumber == null) {
    randomnumber = seqregname + now.getDate() + "-" + now.getMonth() + 1 + "" + now.getFullYear() + "" + now.getHours() + "" + now.getMinutes() + "" + Math.ceil(Math.random() * 5);
  }
  jQuery("#sessionid").html("<b>Session Id: </b><a  href='./session.jsp?query=" + randomnumber + "' target='_blank'>" + randomnumber + "</a> Saved at " + now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds());
  saveSession();
}

function getTracks() {

  var tracks = [];
  var eachTrack = {};
  for (var i = 0; i < track_list.length; i++) {
    if (jQuery("#" + track_list[i].name + "Checkbox").is(':checked')) {
      var track = window[track_list[i].name];
      var trackId = track_list[i].id;
      if (window[track_list[i].name] && window[track_list[i].name] != 'running') {
        eachTrack = { "trackId": trackId, "child": track}
      }
      tracks.push(eachTrack);
    }
  }
  if (jQuery("#alertDiv").text().contains("BLAST")) {
    eachTrack = { "trackId": "running", "child": blastsdata}
    tracks.push(eachTrack);
  }
  if ((window['blasttrack']) && !jQuery("#blasttrackCheckbox").is(':checked')) {
    var track = window['blasttrack'];
    eachTrack = { "trackId": 0, "child": track}
    tracks.push(eachTrack);
  }

  return tracks;
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

        begin = parseInt(getBegin()) - 1 + Math.round((getEnd() - getBegin()) * (seqX - jQuery('#canvas').offset().left) / parseFloat(maxLen) + getBegin());
        end = parseInt(getBegin()) + Math.round((getEnd() - getBegin()) * (mouseX - jQuery('#canvas').offset().left) / parseFloat(maxLen) + getBegin());
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
//        eval(jQuery(this).attr('onClick'));
      }
      else {
        //    do nothing
      }
    })
  }
  trackToggle("all")

}