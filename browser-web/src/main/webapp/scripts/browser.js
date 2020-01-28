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
 * Date: 10/11/11
 * Time: 13:42
 * To change this template use File | Settings | File Templates.
 */
var ajaxurl = '/' + jQuery('#title').text() + '/' + jQuery('#title').text() + '/fluxion.ajax';


function scrollZoom(event) {
    event.preventDefault();
    var delta = 0;

    // normalize the delta
    if (event.wheelDelta) {

        // IE and Opera
        delta = event.wheelDelta / 60;
    }
    else if (event.detail) {

        // W3C
        delta = -event.detail / 2;
    }
    var clicked = jQuery('#currentposition').html();
    if (delta > 0) {
        setBegin(parseInt(getBegin()) + ( (clicked - parseInt(getBegin())) * 0.2));
        setEnd(parseInt(getEnd()) - ( (parseInt(getEnd()) - clicked) * 0.2));
    }
    else {
        setBegin(parseInt(getBegin()) - ( (clicked - parseInt(getBegin())) * 0.2));
        setEnd(parseInt(getEnd()) + ( (parseInt(getEnd()) - clicked) * 0.2));
    }
    jumpToSeq();

}

function zoomIn(zoom_len) {
    var tempBegin = (parseInt(getBegin()) + parseInt(zoom_len));
    var tempEnd = (parseInt(getEnd()) - parseInt(zoom_len));


//    if ((tempEnd - tempBegin) <= minWidth) {
//        var diff = minWidth - (tempEnd - tempBegin);
//
//        setBegin(parseInt(tempBegin - parseInt((diff / 2))));
//        setEnd(parseInt(tempEnd + parseInt((diff / 2))));
//
//    }
//    else {
    setBegin(tempBegin);
    setEnd(tempEnd);
//    }
    jumpToSeq();
}

function jumpToHere(e) {
    var top = parseFloat(e.pageY - jQuery('#' + seqregname).offset().top);
    //if (top > parseFloat(getMapMarkerTop())) {
    top = top - parseFloat(getMapMarkerHeight()) / 2;
    //}
    var begin = (top * sequencelength / parseFloat(jQuery("#" + seqregname).css('height')) );
    var end = parseInt(begin) + parseInt(getEnd() - getBegin());
    setBegin(begin);
    setEnd(end)
    setMapMarkerTop(getBegin());
    jumpToSeq();
}

function jumpToOther(e, length, name, coord) {
    var top = parseFloat(e.pageY - jQuery('#' + name).offset().top);
    //if (top > parseFloat(getMapMarkerTop())) {
    top = top - parseFloat(getMapMarkerHeight()) / 2;
    //}
    var diff = parseInt(getEnd() - getBegin());
    var begin, end;
    if (diff) {
        begin = parseInt(top * length / parseFloat(jQuery("#" + name).css('height')));
        end = parseInt(begin) + diff;
        if (begin < 1) {
            end = end - begin;
            begin = 1;
        }
        if (end > length) {
            begin = begin - (end - length);
            end = length;
        }

        if (begin < 1) {
            begin = 1;
        }
        if (end > length) {
            end = length;
        }
    }
    else {
        begin = 1;
        end = length;
    }
    window.location.replace("index.jsp?query=" + name + "&coord=" + coord + "&from=" + begin + "&to=" + end);
}

function zoomOut(zoom_len) {
    var newbegin = parseInt(getBegin()) - parseInt(zoom_len);
    var newend = parseInt(getEnd()) + parseInt(zoom_len)

    if (parseInt(newend) >= sequencelength) {
        newend = sequencelength;
    }

    if (parseInt(getBegin()) <= 0) {
        newbegin = 1;
    }

    setBegin(newbegin);
    setEnd(newend);
    jumpToSeq();
}

function reset() {

    setBegin((sequencelength - minWidth) / 2);
    setEnd(parseInt(getBegin()) + minWidth)
    jumpToSeq();
}


function expand() {
    setBegin(1);
    setEnd(sequencelength)
    jumpToSeq();
}

function dragLeft() {

    var begin = getBegin();
    var end = getEnd();

    var newbegin = parseInt(begin - (end - begin) * 0.90);
    var newend = parseInt(end - (end - begin) * 0.90);

    if (newbegin < 0) {
        newend = newend - newbegin;
        newbegin = 0;
    }

    if (newend > sequencelength) {
        newbegin = newbegin - (newend - sequencelength);
        newend = sequencelength;
    }

    setBegin(newbegin);
    setEnd(newend);
    jumpToSeq();
}

function keyControl(e) {
    if (e.keyCode == 39) {
        dragRight();
    }
    else if (e.keyCode == 37) {
        dragLeft();
    }

}

function dragRight() {
    var begin = getBegin();
    var end = getEnd();

    var newbegin = parseInt(begin) + parseInt((end - begin) * 0.90);
    var newend = parseInt(end) + parseInt((end - begin) * 0.90);

    if (newbegin < 0) {
        newend = newend - newbegin;
        newbegin = 0;
    }

    if (newend > sequencelength) {
        newbegin = newbegin - (newend - sequencelength);
        newend = sequencelength;
    }

    setBegin(newbegin);
    setEnd(newend);

    jumpToSeq();
}

function dragtohere(e) {
    var left = parseFloat(e.pageX);// - jQuery('#canvas').offset().left);

    if (left > parseFloat(getDragableLeft())) {
        left = left - parseFloat(getDragableWidth());
    }

    setDragableLeft(left);
    setbglayerLeft(left, true);
}

function seqLeft() {


    var begin = getBegin();
    var end = getEnd();
    if (parseFloat(begin) > 1) {
        begin = parseFloat(getBegin()) - 1;
        end = parseFloat(getEnd()) - 1;
        setBegin(begin);
        setEnd(end);
        setNavPanel();
        jumpToSeq();
    }
}

function seqRight() {
    var begin = getBegin();
    var end = getEnd();
    if (parseFloat(end) < sequencelength) {
        begin = parseFloat(getBegin()) + 1;
        end = parseFloat(getEnd()) + 1;//seq.length;
        setBegin(begin);
        setEnd(end);
        setNavPanel();
        jumpToSeq();
    }
}

function auto_drag() {
    var drag = parseFloat(getDragableLeft());
    setbglayerLeft(drag, true);
    window.history.pushState('TGAC Browser', 'Title', "index.jsp?query=" + seqregname + "&&coord=" + coord + "&&from=" + getBegin() + '&&to=' + getEnd());

}


function setNavPanel() {
//    var left = 0;
//    var height = parseFloat(jQuery("#sequence").position().top) - (parseFloat(jQuery("#draggable").position().top) + parseFloat(jQuery("#draggable").css("height"))) + "px solid #cccccc";
//    console.log("setnavpanel h"+height)
//
//    var border_left = parseFloat(jQuery("#draggable").css("left")) - left + "px solid transparent";
//    console.log("setnavpanel bl"+border_left)
//
//    var width = jQuery("#draggable").css("width");
//    console.log("setnavpanel w"+width)
//
//    var diff = (parseFloat(maxLen) - (parseFloat(jQuery("#draggable").css("width")) + parseFloat(jQuery("#draggable").css("left"))))
//
//    console.log("setnavpanel d"+diff)
//
//    if (diff < 0) {
//        diff = 0;
//    }
//    var border_right = (diff) + "px solid transparent";
//    jQuery("#nav_panel").animate({left: left}, { duration: 300, queue: false});
//    jQuery("#nav_panel").animate({width: width}, { duration: 300, queue: false});
//    jQuery("#nav_panel").animate({borderBottom: height}, { duration: 300, queue: false});
//    jQuery("#nav_panel").animate({borderLeft: border_left}, { duration: 300, queue: false});
//    jQuery("#nav_panel").animate({borderRight: (border_right)}, { duration: 300, queue: false});
    var left = 0;
    var height = parseFloat(jQuery("#sequence").position().top) - (parseFloat(jQuery("#draggable").position().top) + parseFloat(jQuery("#draggable").css("height"))) + "px solid #cccccc";
    var border_left = parseFloat(jQuery("#draggable").css("left")) - left + "px solid transparent";
    var width = jQuery("#draggable").css("width");
    var diff = (parseFloat(maxLen) - (parseFloat(jQuery("#draggable").css("width")) + parseFloat(jQuery("#draggable").css("left"))))
    if (diff < 0) {
        diff = 0;
    }
    var border_right = (diff) + "px solid transparent";
    jQuery("#nav_panel").animate({left: left}, {duration: 300, queue: false});
    jQuery("#nav_panel").animate({width: width}, {duration: 300, queue: false});
    jQuery("#nav_panel").animate({borderBottom: height}, {duration: 300, queue: false});
    jQuery("#nav_panel").animate({borderLeft: border_left}, {duration: 300, queue: false});
    jQuery("#nav_panel").animate({borderRight: (border_right)}, {duration: 300, queue: false});

}


// Tracks can be drag
function trackDrag() {

    var temp = parseFloat(1) - parseFloat(jQuery('#wrapper').css("left"));
    if (temp > 10 || temp < -10) {


        var beginnew = parseFloat(getBegin()) + parseFloat((getEnd() - getBegin()) * temp / parseFloat(maxLen));
        var endnew = parseFloat(getEnd()) + parseFloat((getEnd() - getBegin()) * temp / parseFloat(maxLen));

        if (beginnew < 0) {
            endnew = endnew - beginnew;
            beginnew = 0;
        }

        if (endnew >= parseFloat(sequencelength)) {
            beginnew = beginnew - (endnew - sequencelength);
            endnew = sequencelength;
        }

        setBegin(beginnew);
        setEnd(endnew);
        jumpToSeq();
        jQuery("#wrapper").css({'left': '0px'});

    }
    else {
        jQuery("#wrapper").css({'left': '0px'});
    }
}
function updateJSON() {
    console.log("update json")
    var from, to;
    var partial = (getEnd() - getBegin()) / 2;
    from = Math.ceil(parseInt(getEnd()) - partial);
    to = Math.ceil(parseInt(getEnd()) + partial);

    if (lastStart >= 0 || lastEnd >= 0) {

        if (parseInt(lastStart) < parseInt(getBegin()) && parseInt(lastEnd) > parseInt(getEnd())) {
            // removeJSON(null, parseInt((parseInt(getEnd()) + parseInt(partial))));
            // removeJSON(parseInt(parseInt(getBegin() - partial)), null);

            from = Math.floor((getBegin() - partial));
            to = Math.ceil(parseInt(getEnd()) + parseInt(partial));

            removeJSON(from, to);

            lastEnd = getEnd();
            lastStart = getBegin();
        }
        else if (parseInt(lastStart) < parseInt(getBegin()) || parseInt(lastEnd) < parseInt(getEnd())) {
            from = Math.ceil(parseInt(getBegin()) - partial);
            to = Math.ceil(parseInt(getEnd()) + partial);

            addJSON(from, to);

            lastEnd = getEnd();
            lastStart = getBegin();
        }
        else if (parseInt(lastStart) > parseInt(getBegin()) || parseInt(lastEnd) > parseInt(getEnd())) {
            from = Math.floor((parseInt(getBegin()) - parseInt(partial)));
            to = Math.ceil(parseInt(getEnd()) + partial);

            addJSON(from, to);
            lastEnd = getEnd();
            lastStart = getBegin();
        }
        else if (parseInt(lastStart) > parseInt(getBegin()) || parseInt(lastEnd) < parseInt(getEnd())) {
            from = Math.floor((getBegin() - partial));
            to = Math.ceil(parseInt(getEnd()) + parseInt(partial));

            addJSON(from, to);

            lastEnd = getEnd();
            lastStart = getBegin();
        }
        if (lastStart < 0) {
            lastStart = 0;
        }
        if (lastEnd > sequencelength) {
            lastEnd = sequencelength;
        }
    }
    else {
        lastEnd = getEnd();
        lastStart = getBegin();
    }
}

function addJSON(from, to, trackName, trackId) {

    console.log("add json " + trackName)

    if (from < 0) {
        from = 0;
    }
    else if (from > sequencelength) {
        from = sequencelength;
    }
    if (to < 0) {
        to = 0;
    }
    else if (to > sequencelength) {
        to = sequencelength;
    }
    if ((to - from) > 0) {
        deltaWidth = parseInt(to - from) / parseInt(maxLen);

        if ((trackName && trackId && trackId.toString().indexOf('noid') < 0) || (jQuery("#track_files").val() != null && jQuery("#track_files").val().indexOf(trackName) >= 0)) {

            window[trackName] = "loading";
            window[trackName].graph = "false";
            trackToggle(trackName)


            Fluxion.doAjax(
                'dnaSequenceService',
                'loadTrack',
                {
                    'query': seqregname,
                    'coord': coord,
                    'name': trackName,
                    'trackid': trackId,
                    'start': from,
                    'end': to,
                    'delta': deltaWidth,
                    'url': ajaxurl
                },
                {
                    'doOnSuccess': function (json) {
                        var trackname = json.name;
                        window[trackname] = json[trackname];
                        if (json.type == "graph") {
                            window['track_list' + json.name].graph = "true";
                            window['track_list' + json.name].graphtype = json.graphtype;
                        }
                        else {
                            window['track_list' + json.name].graph = "false";
                            if (window[trackname + "_edited"]) {
                                jQuery.each(window[trackname], function (i, v) {
                                    jQuery.each(window[trackname + "_edited"], function (j, w) {
                                        if (w.id == v.id) {
                                            window[trackname].splice(i, 1, window[trackname + "_edited"][j])
                                            return;
                                        }
                                    });
                                    return;
                                });
                            }
                            if (window[trackname + "_removed"]) {
                                for (var i = 0; i < window[trackname].length; i++) {
                                    jQuery.each(window[trackname + "_removed"], function (j, w) {
                                        if (w.id == window[trackname][i].id) {
                                            window[trackname].splice(i - 1, 1)
                                            return;
                                        }
                                    });
                                }
                            }
                        }
                        trackToggle(json.name)
                    }
                });
        }
        else {
            var Tracklist = track_list;
            for (var i = 0; i < Tracklist.length; i++) {
                var temp_id = Tracklist[i].id
                if (Tracklist[i].name == "blasttrack") {
                    trackToggle(Tracklist[i].name);
                }
                else if (Tracklist[i].name.indexOf("upload") >= 0) {
                    updateUploadedTrack(Tracklist[i].name)
                    trackToggle(Tracklist[i].name);
                }
                else if ((jQuery("#" + Tracklist[i].name + "Checkbox").is(':checked') && Tracklist[i].id.toString().indexOf('noid') < 0) || (jQuery("#track_files").val() != null && jQuery("#track_files").val().indexOf(Tracklist[i].name) >= 0)) {
                    window[Tracklist[i].name] = "loading";
                    window['track_list' + Tracklist[i].name].graph = "false";
                    trackToggle(Tracklist[i].name)
                    var trackname = Tracklist[i].name;
                    var trackid = Tracklist[i].id;
                    // if (trackid && Tracklist[i].graph == "false") { //because graph == true is already loaded
                        Fluxion.doAjax(
                            'dnaSequenceService',
                            'loadTrack',
                            {
                                'query': seqregname,
                                'coord': coord,
                                'name': trackname,
                                'trackid': trackid,
                                'start': from,
                                'end': to,
                                'delta': deltaWidth,
                                'url': ajaxurl
                            },
                            {
                                'doOnSuccess': function (json) {
                                    var trackname = json.name;
                                    window[trackname] = json[trackname];

                                    if (json.type == "graph") {
                                        window['track_list' + json.name].graph = "true";
                                        window['track_list' + json.name].graphtype = json.graphtype;
                                    }
                                    else {
                                        window['track_list' + json.name].graph = "false";
                                        if (window[trackname + "_edited"]) {

                                            jQuery.each(window[trackname], function (i, v) {
                                                jQuery.each(window[trackname + "_edited"], function (j, w) {
                                                    if (w.id == v.id) {
                                                        window[trackname].splice(i, 1, window[trackname + "_edited"][j])
                                                        return;
                                                    }
                                                });
                                                return;
                                            });
                                        }
                                        if (window[trackname + "_removed"]) {
                                            for (var i = 0; i < window[trackname].length; i++) {
                                                jQuery.each(window[trackname + "_removed"], function (j, w) {
                                                    if (w.id == window[trackname][i].id) {
                                                        window[trackname].splice(i - 1, 1)
                                                        return;
                                                    }
                                                });
                                            }
                                        }
                                    }
                                    trackToggle(json.name)
                                }
                            });
                    // }
                }
            }
        }
    }

}

function updateUploadedTrack(trackName) {

    var temp_data = []
    var start = getBegin();
    var end = getEnd();

    var diff = (end - start) / 2

    start = start - diff
    end = parseInt(end) + parseInt(diff)


    var data;
    if (window['track_list' + trackName].data) {
        data = window['track_list' + trackName].data;
    } else {
        data = window[trackName];
    }

    window['track_list' + trackName].graph = "false";
    var temp_data = []

    if (trackName.indexOf("uploadWig") >= 0) {
        // var bin_array = data.split("\n")

        //     for (var i = 0; i < bin_array.length; i++) {

        //        // if(parseInt(bin_array[i].split("\t")[0])>parseInt(start) && parseInt(bin_array[i].split("\t")[0]) < parseInt(end)){
        //             temp_data.push(bin_array[i])
        //        // }
        //     }
        temp_data = data
    } else {
        data.forEach(function (value) {
            if (parseInt(value.start) > parseInt(start) && parseInt(value.start) < parseInt(end)) {
                temp_data.push(value)
            }
        })
    }


    window[trackName] = temp_data
    trackToggle(trackName)
}


function removeJSON(from, to) {
    console.log("remove json " + from + " " + to)


    if (from < 0) {
        from = 0;
    }
    else if (from > sequencelength) {
        from = sequencelength;
    }
    if (to < 0) {
        to = 0;
    }
    else if (to > sequencelength) {
        to = sequencelength;
    }

    var Tracklist = track_list;
    var query = jQuery('#search').val();
    deltaWidth = parseInt(to - from) / parseInt(maxLen);

    for (var i = 0; i < Tracklist.length; i++) {
        if (jQuery("#" + Tracklist[i].name + "Checkbox").is(':checked') || (jQuery("#track_files").val() != null && jQuery("#track_files").val().indexOf(Tracklist[i].name) >= 0)) {
            if (window['track_list' + Tracklist[i].name].graph == "true" && Tracklist[i].name.indexOf("upload") < 0) {
                window[Tracklist[i].name] = "loading";
                window['track_list' + Tracklist[i].name].graph = "false";
                trackToggle(Tracklist[i].name)

                Fluxion.doAjax(
                    'dnaSequenceService',
                    'loadTrack',
                    {
                        'query': seqregname,
                        'coord': coord,
                        'name': Tracklist[i].name,
                        'trackid': Tracklist[i].id,
                        'start': from,
                        'end': to,
                        'delta': deltaWidth,
                        'url': ajaxurl
                    },
                    {
                        'doOnSuccess': function (json) {
                            var trackname = json.name;
                            window[trackname] = json[trackname];

                            if (json.type == "graph") {
                                window['track_list' + json.name].graph = "true";
                                window['track_list' + json.name].graphtype = json.graphtype;
                            }
                            else {
                                window['track_list' + json.name].graph = "false";
                                if (window[trackname + "_edited"]) {

                                    jQuery.each(window[trackname], function (i, v) {
                                        jQuery.each(window[trackname + "_edited"], function (j, w) {
                                            if (w.id == v.id) {
                                                window[trackname].splice(i, 1, window[trackname + "_edited"][j])
                                                return;
                                            }
                                        });
                                        return;
                                    });
                                }
                                if (window[trackname + "_removed"]) {
                                    for (var i = 0; i < window[trackname].length; i++) {
                                        jQuery.each(window[trackname + "_removed"], function (j, w) {
                                            if (w.id == window[trackname][i].id) {
                                                window[trackname].splice(i - 1, 1)
                                                return;
                                            }
                                        });
                                    }
                                }
                            }
                            trackToggle(json.name)
                        }
                    })
            } else {


                var data;
                if (window['track_list' + Tracklist[i].name].data) {
                    data = window['track_list' + Tracklist[i].name].data;
                } else {
                    data = window[Tracklist[i].name];
                }

                window['track_list' + Tracklist[i].name].graph = "false";
                //console.log(data.length)
                var temp_data = []
                if (Tracklist[i].name.indexOf("uploadWig") >= 0) {
                    temp_data = data
                } else {
                    jQuery.each(data, function (index, value) {
                        if ((parseInt(value.start) >= parseInt(from) && parseInt(value.end) <= parseInt(to)) || (parseInt(value.start) <= parseInt(from) && parseInt(value.end) >= parseInt(to)) || (parseInt(value.end) >= parseInt(from) && parseInt(value.end) <= parseInt(to)) || (parseInt(value.start) >= parseInt(from) && parseInt(value.start) <= parseInt(to))) {
                            temp_data.push(value)
                        }

                    })
                }
                window[Tracklist[i].name] = temp_data
                trackToggle(Tracklist[i].name)

            }
        }
    }
}
