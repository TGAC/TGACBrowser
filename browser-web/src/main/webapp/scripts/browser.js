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

function jumpToOther(e, length, name) {
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
    window.location.replace("index.jsp?query=" + name + "&from=" + begin + "&to=" + end);
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
}


function setNavPanel() {
    var x1, y1, x2, y2;
    x1 = jQuery("#draggable").offset().left;
    x2 = parseInt(x1) + +parseFloat(jQuery("#draggable").css("width"));
    y1 = y2 = 0;

    var y3 = jQuery("#wrapper").position().top - jQuery("#nav_panel").position().top;
    var y4 = y3;
    var x3 = 0;
    var x4 = parseInt(jQuery("#nav_panel").width());

    jQuery('#nav_panel').svg();

    var svg = jQuery('#nav_panel').svg('get');
    jQuery('#nav_panel').svg('get').clear();


    svg.polygon([
        [x2, y1],
        [x1, y2],
        [x3, y3],
        [x4, y4]
    ],
        {fill: '#cccccc', stroke: 'blue', strokeWidth: 0});

}


// Tracks can be drag
function trackDrag() {
    var temp = parseFloat(1) - parseFloat(jQuery('#wrapper').css("left"));
    if (temp > 10 || temp < -10) {
        var beginnew = parseFloat(getBegin()) + parseFloat((newEnd - getBegin()) * temp / parseFloat(maxLen));
        var endnew = parseFloat(getEnd()) + parseFloat((newEnd - getBegin()) * temp / parseFloat(maxLen));

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
    var from, to;
    var partial = (getEnd() - getBegin()) / 2;
    from = Math.ceil(parseInt(getEnd()) - partial);
    to = Math.ceil(parseInt(getEnd()) + partial);

    for (var j = 0; j < track_list.length; j++) {

        if (window["track_list" + track_list[j].name].graph == "true") {
//      if (parseInt(lastStart) < parseInt(getBegin()) && parseInt(lastEnd) > parseInt(getEnd())) {
            from = Math.ceil(parseInt(getBegin()) - partial);
            to = Math.ceil(parseInt(getEnd()) + partial);
            addJSON(from, to, track_list[j].name, track_list[j].id);
//      }
        }
    }


    if (lastStart >= 0 || lastEnd >= 0) {

//      console.log("right");
        if (parseInt(lastStart) < parseInt(getBegin()) || parseInt(lastEnd) < parseInt(newEnd)) {
            from = Math.ceil(parseInt(getBegin()) - partial);
            to = Math.ceil(parseInt(getEnd()) + partial);

            addJSON(from, to);
//      removeJSON(parseInt((parseInt(getBegin()) - parseInt(partial)) - (getEnd() - getBegin())), "");

            lastEnd = getEnd();
            lastStart = getBegin();
        }
        //      console.log("left");
        else if (parseInt(lastStart) > parseInt(getBegin()) || parseInt(lastEnd) > parseInt(getEnd())) {
            from = Math.floor((parseInt(getBegin()) - parseInt(partial)));
            to = Math.ceil(parseInt(getEnd()) + partial);

            addJSON(from, to);
//      removeJSON("", parseInt((parseInt(getEnd()) + parseInt(partial) + parseInt((getEnd() - getBegin())))));
            lastEnd = getEnd();
            lastStart = getBegin();
        }
//       console.log("zoomout");
        else if (parseInt(lastStart) > parseInt(getBegin()) || parseInt(lastEnd) < parseInt(getEnd())) {
            from = Math.floor((getBegin() - partial));
            to = Math.ceil(parseInt(getEnd()) + parseInt(partial));

            addJSON(from, to);

            lastEnd = getEnd();
            lastStart = getBegin();
        }
//      console.log("zoomin");
        else if (parseInt(lastStart) < parseInt(getBegin()) && parseInt(lastEnd) > parseInt(getEnd())) {
            partial = (parseInt((getEnd() - getBegin() ) + parseInt(partial)));
            removeJSON(null, parseInt((parseInt(getEnd()) + parseInt(partial))));
            removeJSON(parseInt(parseInt(getBegin() - partial)), null);
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
        deltaWidth = parseInt(to - from) * 2 / parseInt(maxLen);

        if (trackName && trackId && trackId.toString().indexOf('noid') < 0) {
            Fluxion.doAjax(
                'dnaSequenceService',
                'loadTrack',
                {'query': seqregname, 'name': trackName, 'trackid': trackId, 'start': from, 'end': to, 'delta': deltaWidth, 'url': ajaxurl},
                {'doOnSuccess': function (json) {
                    var trackname = json.name;
                    window[trackname] = json[trackname];
                    if (json.type == "graph") {
                        window['track_list' + json.name].graph = "true";
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
                else if (jQuery("#" + Tracklist[i].name + "Checkbox").is(':checked') && Tracklist[i].id.toString().indexOf('noid') < 0) {
                    var trackname = Tracklist[i].name;
                    var trackid = Tracklist[i].id;
                    if (trackid && Tracklist[i].graph == "false") { //because graph == true is already loaded
                        Fluxion.doAjax(
                            'dnaSequenceService',
                            'loadTrack',
                            {'query': seqregname, 'name': trackname, 'trackid': trackid, 'start': from, 'end': to, 'delta': deltaWidth, 'url': ajaxurl},
                            {'doOnSuccess': function (json) {
                                var trackname = json.name;
                                window[trackname] = json[trackname];

                                if (json.type == "graph") {
                                    window['track_list' + json.name].graph = "true";
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
                }
            }
        }
    }

}

/*
 this can be modified to use for edited and removed tracks
 var obj1 = [
 {id:2, comment:"comment22",edited:"new"}
 ] ;

 var obj2 =  [
 {id:1, comment:"comment2"},
 {id:2, comment:"comment2"},
 {id:3, comment:"comment33"},
 {id:4, comment:"comment4"}
 ];

 function merge(one, two){

 var final = one;
 // merge
 for(var i = 0 ; i < two.length;i++){
 var item = two[i];
 insert(item, final);
 }
 return final;
 }


 function insert(item, obj){
 var data = obj;
 var insertIndex = data.length;
 for(var i = 0; i < data.length; i++){
 if(item.id == data[i].id){
 $('#o').append( item.id +"=="+ data[i].id +"\n");
 // ignore duplicates
 insertIndex = -1;
 break;
 } else if(item.id < data[i].id){
 $('#o').append( item.id +"<"+ data[i].id  +"\n");
 insertIndex = i;
 break;
 }
 }
 if(insertIndex == data.length){
 data.push(item);
 } else if(insertIndex != -1) {
 data.splice(insertIndex,0,item);
 }
 }

 var final = merge(obj1, obj2);

 $('#o').append("\n\n\nUsing merge()\n");
 $('#o').append( JSON.stringify(final) );
 */

function removeJSON(from, to) {
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
    if (from == null) {
        var count = 0;
        for (var i = 0; i < Tracklist.length; i++) {
            if (window[Tracklist[i].name]) {
                for (var j = 0; j < window[Tracklist[i].name].length;) {
                    if (window[Tracklist[i].name][j].start > to) {
                        count++;
                        delete window[Tracklist[i].name].splice(j, 1);
                    }
                    else {
                        j++;
                    }
                }
                if (jQuery("#" + Tracklist[i].name + "Checkbox").is(':checked')) {
                    trackToggle(Tracklist[i].name)
                }
            }
        }
    }
    else {
        var count = 0;
        for (var i = 0; i < Tracklist.length; i++) {
            if (window[Tracklist[i].name]) {
                for (var j = 0; j < window[Tracklist[i].name].length;) {
                    if (window[Tracklist[i].name][j].start < from) {
                        count++;
                        delete window[Tracklist[i].name].splice(j, 1);
                    }
                    else {
                        j++;
                    }
                }
                if (jQuery("#" + Tracklist[i].name + "Checkbox").is(':checked')) {
                    trackToggle(Tracklist[i].name)
                }
            }
        }
    }


}