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
var maxLen, showCDS = false, showSNP = false, ctrldown = false;
var rightclick = false, path;
//var cds, SNPs, Exon, minWidth;
var newStart, newEnd, mouseX, mouseY, border_left, border_right, selectionStart, selectionEnd, lastStart = -1, lastEnd = -1, grouplastid = null, grouptrack, grouptrackclass;
var blastsdata = [];
var grouplist = [];
var tracks = [];
var tracklocation = [];
var chromosome = false;
var scale = "";
var unit = "bp";

var link = null;

function setBlast() {
    if (jQuery("#blastType").text().indexOf('local') >= 0) {
        jQuery.getScript("scripts/blast_local.js", function (data, textStatus, jqxhr) {
        });
        jQuery("#blastdbs").show();
    }
    else if (jQuery("#blastType").text().indexOf('ncbi') >= 0) {
        jQuery.getScript("scripts/blast_ncbi.js", function (data, textStatus, jqxhr) {
        });
        jQuery("#ncbiblastdbs").show();
    }
    else if (jQuery("#blastType").text().indexOf('server') >= 0) {
        jQuery.getScript("scripts/blast_server.js", function (data, textStatus, jqxhr) {
        });
        jQuery("#blastdbs").show();
    }
    else if (jQuery("#blastType").text().indexOf('slurm') >= 0) {
        jQuery.getScript("scripts/blast_slurm.js", function (data, textStatus, jqxhr) {
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

                jQuery(".marker_class").each(function (i) {
                    jQuery(this).css("left", '-50%');
                    jQuery(this).css("top", parseInt(jQuery(this).parent().parent().position().top) - parseInt(jQuery(window).scrollTop()) + (parseInt(jQuery("#wrapper").position().top) + parseInt(jQuery("#sequence").css('height'))) + (parseInt(jQuery("#canvas").position().top)) + 10);
                    jQuery(this).css("position", 'fixed');
                });
                jQuery(".marker_class_neg").each(function (i) {
                    jQuery(this).css("left", '-50%');
                    jQuery(this).css("top", parseInt(jQuery(this).parent().parent().position().top) - parseInt(jQuery(window).scrollTop()) + (parseInt(jQuery("#wrapper").position().top) + parseInt(jQuery("#sequence").css('height'))) + (parseInt(jQuery("#canvas").position().top)));
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
            if (diff > 1000000000) {
                diff = (diff / 1000000000);
                bp = "Gb";
            }
            else if (diff > 1000000) {
                diff = (diff / 1000000);
                bp = "Mb";
            }
            else if (diff > 1000) {
                diff = diff / 1000;
                bp = "Kb";
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

//    jQuery(window).bind('orientationchange', function(e) {
//           getMaxLen();
//        switch ( window.orientation ) {
//
//            case 0:
//                maxLen = jQuery(window).width();
//                alert('portrait mode');
//                break;
//
//            case 90:
//                maxLen = jQuery(window).height();
//                alert('landscape mode screen turned to the left');
//                break;
//
//            case -90:
//                maxLen = jQuery(window).height();
//                alert('landscape mode screen turned to the right');
//                break;
//
//        }
//
//    });


    jQuery("#uploadFormats").tabs();

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
        var pos = ((getBegin() * scale) +
            (Math.round(
                ((e.pageX * (getEnd() - getBegin())) / jQuery(window).width()) + getBegin()) * scale)).toFixed(2)
            + "" + unit;
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
//Display cordinates as percentage
function dispSeqCoord() {
    if (scale == 1) {
        var diff = parseInt(parseInt(sequencelength) / 20);
        var bp = "";
        if (diff > 1000000000) {
            diff = (diff / 1000000000);
            bp = "G";
        }
        else if (diff > 1000000) {
            diff = (diff / 1000000);
            bp = "M";
        }
        else if (diff > 1000) {
            diff = diff / 1000;
            bp = "K";
        }

        diff = parseInt(diff);
        jQuery("#zoomoutbig").attr('title', "Zoom Out(" + diff + "" + bp + ")");
        jQuery("#zoominbig").attr('title', "Zoom In(" + +diff + "" + bp + ")");
        var diff = parseInt(parseInt(sequencelength) / 40);
        var bp = "";
        if (diff > 1000000000) {
            diff = (diff / 1000000000);
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
        jQuery('#SeqLenStart').html(0 +""+bp);

        var diff = parseInt(parseInt(len) / 4);
        var bp = "";
        if (diff > 1000000000) {
            diff = (diff / 1000000000);
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
        jQuery('#SeqLen25').html(parseFloat(diff).toFixed(2) + "" + bp);

        var diff = parseInt(parseInt(len) / 2);
        var bp = "";
        if (diff > 1000000000) {
            diff = (diff / 1000000000);
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

        jQuery('#SeqLenMid').html(parseFloat(diff).toFixed(2) + "" + bp);
        var diff = parseInt(parseInt(len) / 4 * 3);
        var bp = "";
        if (diff > 1000000000) {
            diff = (diff / 1000000000);
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

        jQuery('#SeqLen75').html(parseFloat(diff).toFixed(2) + "" + bp);

        var diff = parseInt(parseInt(len));
        var bp = "";
        if (diff > 1000000000) {
            diff = (diff / 1000000000);

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
        jQuery('#SeqLenEnd').html(parseFloat(diff).toFixed(2) + "" + bp);
    } else {

        var diff = parseInt(parseInt(sequencelength) / 20);

        jQuery("#zoomoutbig").attr('title', "Zoom Out(" + (diff*scale).toFixed(2) + "" + unit + ")");
        jQuery("#zoominbig").attr('title', "Zoom In(" + +(diff*scale).toFixed(2) + "" + unit + ")");

        diff = parseInt(parseInt(sequencelength) / 40);
        jQuery("#zoomoutsmall").attr('title', "Zoom Out(" + (diff*scale).toFixed(2) + "" + unit + ")");
        jQuery("#zoominsmall").attr('title', "Zoom In(" + +(diff*scale).toFixed(2) + "" + unit + ")");

        var len = sequencelength;
        jQuery('#SeqLenStart').html(0);

        var diff = parseInt(parseInt(len) / 4);

        jQuery('#SeqLen25').html(parseFloat(diff*scale).toFixed(2) + "" + unit);


        var diff = parseInt(parseInt(len) / 2);

        jQuery('#SeqLenMid').html(parseFloat(diff*scale).toFixed(2) + "" + unit);
        var diff = parseInt(parseInt(len) / 4 * 3);

        jQuery('#SeqLen75').html(parseFloat(diff*scale).toFixed(2) + "" + unit);

        var diff = parseInt(parseInt(len));

        jQuery('#SeqLenEnd').html(parseFloat(diff*scale).toFixed(2) + "" + unit);

    }

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


function tooglehangingmenu() {

    jQuery("#popup_hanging").css('left', mouseX + 5);
    jQuery("#popup_hanging").css('top', mouseY + 5);

    jQuery("#popup_hanging").toggle();
}

