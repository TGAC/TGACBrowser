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
 * Time: 4:15 PM
 * To change this template use File | Settings | File Templates.
 */

function stringColour(temp) {
    var letters = temp.split('');
    var newSeq = "";
    for (var i = 0; i < letters.length; i++) {
        if (letters[i] == 'A') {
            newSeq += "<span class=\"span_str\" style=\"background:#ff8c00; LEFT: "+(i*10)+"px;\">" + letters[i] + "</span>" ;
        }
        else if (letters[i] == 'C') {
            newSeq += "<span class=\"span_str\" style=\"background:green; left: "+(i*10)+"px;\">" + letters[i] + "</span>";
        }
        else if (letters[i] == 'G') {
            newSeq += "<span class=\"span_str\" style=\"background:blue; left: "+(i*10)+"px;\">" + letters[i] + "</span>";
        }
        else if (letters[i] == 'T') {
            newSeq += "<span class=\"span_str\" style=\"background:red; left:  "+(i*10)+"px;\">" + letters[i] + "</span>";
        }
        else {
            newSeq += "<span class=\"span_str\" style=\"background:black; left:  "+(i*10)+"px;\">" + letters[i] + "</span>";
        }
    }
    return newSeq;
}

function visualLength(temp) {
    var ruler = $("ruler");
    var inLength = 0;
    var tempStr = "";
    ruler.innerHTML = "N";
    if (jQuery.browser.webkit) {
        inLength = (ruler.offsetWidth - 1) * temp;
    }
    else {
        inLength = (ruler.offsetWidth) * temp;
    }
    return inLength;
}

function findminwidth() {
    maxLen = jQuery(window).width();
//    var len = visualLength(sequencelength);
    var seqWidth = maxLen / 10; //parseFloat(maxLen) * sequencelength / parseFloat(len);
    deltaWidth = parseInt(sequencelength) * 2 / parseInt(maxLen);
    if (sequencelength < seqWidth) {
//       still need to recode
        seqWidth = sequencelength;
        maxLen = len;
        jQuery('#canvas').width(len)
        jQuery('#bar_image').width(len)
        var left = (parseInt(jQuery(window).width()) - len) / 2;
        setbglayerLeft(left, true)
        setbglayerWidth(len)
        setDragableLeft(left, true)
        setDragableWidth(len)
        jQuery('#bar_image').animate({"left": left}, { duration: 500, queue: false});
        jQuery('.browserimage').hide();
    }
    return parseInt(seqWidth);
}
function browser_coordinates() {

    var temp = "<FONT style=\"BACKGROUND-COLOR: #d3d3d3\">";
    jQuery("#vertical0").html(temp + Math.round(getBegin()));
    jQuery("#vertical1").html(temp + Math.round(parseFloat(getBegin()) + parseFloat((getEnd() - getBegin()) * 0.1)));
    jQuery("#vertical2").html(temp + Math.round(parseFloat(getBegin()) + parseFloat((getEnd() - getBegin()) * 0.2)));
    jQuery("#vertical3").html(temp + Math.round(parseFloat(getBegin()) + parseFloat((getEnd() - getBegin()) * 0.3)));
    jQuery("#vertical4").html(temp + Math.round(parseFloat(getBegin()) + parseFloat((getEnd() - getBegin()) * 0.4)));
    jQuery("#vertical5").html(temp + Math.round(parseFloat(getBegin()) + parseFloat((getEnd() - getBegin()) * 0.5)));
    jQuery("#vertical6").html(temp + Math.round(parseFloat(getBegin()) + parseFloat((getEnd() - getBegin()) * 0.6)));
    jQuery("#vertical7").html(temp + Math.round(parseFloat(getBegin()) + parseFloat((getEnd() - getBegin()) * 0.7)));
    jQuery("#vertical8").html(temp + Math.round(parseFloat(getBegin()) + parseFloat((getEnd() - getBegin()) * 0.8)));
    jQuery("#vertical9").html(temp + Math.round(parseFloat(getBegin()) + parseFloat((getEnd() - getBegin()) * 0.9)));
    jQuery("#vertical10").html(temp + Math.round(parseFloat(getBegin()) + parseFloat((getEnd() - getBegin()))));
}

function trackToggle(trackname) {
    var index = 0;
    var graph = "false";
    var trackid = "";


//    for (var i = 0; i < track_list.length; i++) {
//        if (track_list[i].name == trackname) {
//            index = i;
//            trackid = track_list[i].id;
//        }
//        if (track_list[i].name == trackname && track_list[i].graph == "true") {
//            graph = "true";
//        }
//    }
    if (trackname == "all") {
        jQuery("#mergedtrack").html("<div id= \"mergelabel\" align='left' class='handle'></div>");
        for (var i = 0; i < track_list.length; i++) {
            var trackName = track_list[i].name;
            trackid = window['track_list' + trackName].id;
            graph = window['track_list' + trackName].graph;
            if (jQuery("#" + track_list[i].name + "Checkbox").is(':checked')) {
                if (graph == "true") {
                    dispGraph("#" + trackName + "_div", trackName, window['track_list' + trackName].display_label);
                }
                else if (trackName.toLowerCase().indexOf("blasttrack") >= 0) {
                    dispBLAST("#" + trackName + "_div", 'blasttrack');
                }
                else if (trackName.toLowerCase().indexOf("gene") >= 0) {
                    dispGenes("#" + trackName + "_div", trackName, window['track_list' + trackName].expand, window['track_list' + trackName].display_label);
                }
                else if (trackid.toString().toLowerCase().indexOf("wig") >= 0 || trackid.toString().toLowerCase().indexOf("bw") >= 0 || trackid.toString().toLowerCase().indexOf("bigwig") >= 0) {
                    dispGraphWig("#" + trackName + "_div", trackName, trackid, window['track_list' + trackName].display_label);
                }
                else if (trackName.toLowerCase().indexOf("bed") >= 0) {
                    dispGraphBed("#" + trackName + "_div", trackName, window['track_list' + trackName].display_label);
                }
                else {
                    dispTrack("#" + trackName + "_div", trackName, window['track_list' + trackName].display_label);
                }
            }
            else {
                jQuery("#" + track_list[i].name + "_wrapper").fadeOut();
            }
        }
    }
    else {
        layers = jQuery("#rowoftracks").val();
        trackid = window['track_list' + trackname].id;
        graph = window['track_list' + trackname].graph;
        if (jQuery('#' + trackname + 'Checkbox').is(':checked')) {
            if (graph == "true") {
                dispGraph("#" + trackname + "_div", trackname, window['track_list' + trackname].display_label);
            }
            else if (trackname.toLowerCase().indexOf("blasttrack") >= 0) {
                dispBLAST("#" + trackname + "_div", 'blasttrack');
            }
            else if (trackname.toLowerCase().indexOf("gene") >= 0) {
                dispGenes("#" + trackname + "_div", trackname, window['track_list' + trackname].expand, window['track_list' + trackname].display_label);
            }
            else if (trackid.toString().toLowerCase().indexOf("wig") >= 0 || trackid.toString().toLowerCase().indexOf("bw") >= 0 || trackid.toString().toLowerCase().indexOf("bigwig") >= 0) {
                dispGraphWig("#" + trackname + "_div", trackname, trackid, window['track_list' + trackname].display_label);
            }
            else if (trackname.toLowerCase().indexOf("bed") >= 0) {
                dispGraphBed("#" + trackname + "_div", trackname, window['track_list' + trackname].display_label);
            }
            else {
                dispTrack("#" + trackname + "_div", trackname, window['track_list' + trackname].display_label);
            }
        }
        else {
            jQuery("#" + trackname + "_wrapper").fadeOut();
        }

    }
    if (jQuery("#mergedtrack").text() == "") {
        jQuery("#mergedtrack").fadeOut();
        jQuery("#mergedtrack").html();
    }
}

function mergeTrack(check) {
    jQuery(track_list).each(function (index) {
        //this is the object in the array, index is the index of the object in the array

        if (jQuery("#" + track_list[index].name + "mergedCheckbox").attr('checked')) {//
//            this.merge = 1;
            window['track_list' + track_list[index].name].merge = 1;
        }
        else {
//            this.merge = 0;
            window['track_list' + track_list[index].name].merge = 0;
        }
    });

    merged_track_list = "";
    jQuery("#mergedtrack").html("<div id= \"mergelabel\" align='left' class='handle'></div>");
    jQuery('#mergetracklist input:checked').each(function () {
        var track = jQuery(this).attr('name').replace("mergedCheckbox", "");
        merged_track_list += track + ", ";
        if (jQuery('#' + track + 'Checkbox').is(':checked')) {
            trackToggle(track);
        }
    });
    if (!jQuery('input[name=' + check + 'mergedCheckbox]').is(':checked')) {
        trackToggle(check);
    }
}

//remove tracklist when reload
function removeTrackslist(trackList) {

    for (var i = 0; i < trackList.length; i++) {
        delete window[trackList[i].name];
    }
    jQuery("#mergetracklist").html("");
}
//
//function controls() {
////  jQuery("#slider").fadeIn();
//}

function toggleLeftInfo(div, id) {
    if (jQuery(div).hasClass("toggleLeft")) {
        jQuery(div).removeClass("toggleLeft").addClass("toggleLeftDown");
    }
    else {
        jQuery(div).removeClass("toggleLeftDown").addClass("toggleLeft");
    }
    jQuery("#" + id).toggle("blind", {}, 500);
}

function groupTogether() {
    var trackid;
    jQuery(window[grouptrack]).each(function (index) {

        if (window[grouptrack][index].id == grouplastid) {
            trackid = index;
        }
    });

    var a = 0;
    for (var j = 0; j < window[grouptrack][trackid].transcript.length; j++) {
        if (jQuery.inArray(window[grouptrack][trackid].transcript[j].id, grouplist) > -1) {
            window[grouptrack][trackid].transcript[j].layer = jQuery.inArray(window[grouptrack][trackid].transcript[j].id, grouplist) + 1;
        }
    }

    for (var j = 0; j < window[grouptrack][trackid].transcript.length; j++) {
        if (jQuery.inArray(window[grouptrack][trackid].transcript[j].id, grouplist) < 0) {
            window[grouptrack][trackid].transcript[j].layer = grouplist.length + a + 1;
            a += 1;
        }
    }
    function SortByLayer(x, y) {
        return ((x.layer == y.layer) ? 0 : ((x.layer > y.layer) ? 1 : -1 ));
    }

    // Call Sort By Name
    window[grouptrack][trackid].transcript.sort(SortByLayer);
    trackToggle(grouptrack);
    jQuery("#makegroup").hide();
    backup_tracks(grouptrack, trackid)
    ctrldown = false;
    grouplist = [];
    grouplastid = null;
    grouptrack = null;

}

function groupCancel() {
    trackToggle(grouptrack);
    jQuery("#makegroup").hide();
    ctrldown = false;
    grouplist = [];
    grouplastid = null;
    grouptrack = null;

}

function stringTrim(string, width, newClass) {
    if(newClass){
        console.log(newClass)
        jQuery("#ruler").addClass(newClass.toString())
    }
    else{
        jQuery("#ruler").addClass("ruler")
    }
    var ruler = jQuery("#ruler");
    console.log("added"+jQuery("#ruler").attr('class'))
    var inLength = 0;
    var tempStr = "";

    jQuery("#ruler").html(string);
    inLength = jQuery("#ruler").width();

    if(newClass){
        jQuery("#ruler").removeClass(newClass.toString())
    }
    else{
        jQuery("#ruler").removeClass("ruler")
    }

    if (inLength < width) {
        return string;
    }
    else {
        width = parseInt(string.length * width / inLength);
        return "<span title=" + string + ">" + string.substring(0, width) + "... </span>";
    }

}

function findAndRemove(array, property, value) {
    jQuery.each(blastsdata, function (index, result) {
        if (result[property] == value) {
            blastsdata.splice(index, 1);
            return false;
        }
    });
}

function backup_tracks(track, i) {
    var add = window[track][i];
    var index = -1;
    if (!window[track + "_edited"]) {
        add.edited = 1;
        window[track + "_edited"] = [];
        window[track + "_edited"].push(add);
    }
    else {
        jQuery.each(window[track + "_edited"], function (b, w) {
            if (w.id == add.id) {
                index = b;
                add.edited = parseInt(add.edited) + 1;
                window[track + "_edited"].splice(b, 1, add);
                return;
            }
        });

        if (index == -1) {
            add.edited = 1;
            window[track + "_edited"].push(add);
        }
    }
}

function backup_tracks_minus(track, i) {
    var add = window[track][i];
    jQuery.each(window[track + "_edited"], function (b, w) {
        if (w.id == window[track][i].id) {
            window[track + "_edited"][b].edited = window[track + "_edited"][b].edited - 1;
            if (window[track + "_edited"][b].edited == 0) {
                window[track + "_edited"].splice(b, 1);
            }
        }
    });
}

function backup_tracks_removed(track, i) {
    var add = window[track][i];
    if (!window[track + "_removed"]) {
        window[track + "_removed"] = [];
    }
    window[track + "_removed"].push(add);

//
//  can be used if removed tracks need to be add again new feature
//
// else {
//    jQuery.each(window[track + "_removed"], function (b, w) {
//      if (w.id == add.id) {
//        index = b;
//        add.edited = parseInt(add.edited) + 1;
//        console.log("if");
//        window[track + "_removed"].splice(b, 1, add)
//        return;
//      }
//    });
//
//    if (index == -1) {
//      add.edited = 1;
//      console.log("else");
//      window[track + "_removed"].push(add);
//    }
//  }
}

function parseBLAST(json){
    jQuery('#blastresult').fadeIn();
    jQuery('#blastresult').append("<table style=\"display: none;\" class='list' id='blasttable"+json.id+"'> <thead> " +
        "<tr><th> Query id </th> <th> Subject id </th>  <th> % identity </th>   <th> alignment length </th>  <th> mismatches </th>  <th> gap openings </th>  <th> q.start </th>  <th> q.end </th>  <th> s.start </th>  <th> s.end </th> <th> e-value </th> <th> bit score </th> <th> Subject db </th><th> Download Sequence </th>        </tr>        </thead>        <tbody>        </tbody>    </table>")

    for(var i=0; i<json.html.length; i++){
        jQuery("#blasttable"+json.id+" tbody").append("<tr><td>"+json.html[i].q_id+"</td><td>"+json.html[i].s_id+"</td><td>"+json.html[i].identity+"</td><td>"+json.html[i].aln_length+"</td><td>"+json.html[i].mismatch+"</td><td>"+json.html[i].gap_open+"</td><td>"+json.html[i].q_start+"</td><td>"+json.html[i].q_end+"</td><td>"+json.html[i].s_start+"</td><td>"+json.html[i].s_end+"</td><td>"+json.html[i].e_value+"</td><td>"+json.html[i].bit_score+"</td><td>"+json.html[i].s_db+"</td><td><div class=\"ui-widget ui-state-default ui-corner-all ui-button ui-icon ui-icon-arrow-1-s\" id=\"openmenu\" onclick=\"sub_seq('"+json.html[i].sequence+"')\" title=\"More Option\"> </div></td></tr>");
    }
    jQuery("#blasttable"+json.id).tablesorter();
    jQuery("'#blasttable"+json.id+"'").trigger("update");
}

function sub_seq(seq){
    var id = "";//seq.split("<br>")[0];
  //  seq =  seq.split("<br>")[1];
    jQuery.colorbox({
        width: "90%",
        height: "100%",
        html: "<table><tr><td><button class='ui-state-default ui-corner-all' " +
            "onclick=\"selectText('sub_output');\"')\">Select Sequence</button><br/>" +
            "<td><div id=fastadownload></div></td></td></tr></table><br/>" +
            "<br/><b>Subject Sequence:</b> <br/>" +
            "<div id=\"sub_output\" style=' font-family: Courier, \"Courier New\", monospace'> "+id+"<br>"+convertFasta(seq)+"</div>"});
}

function resetBLAST(){
    jQuery('#blasttable tbody').html("");
    jQuery('#blastresult').fadeOut();
}

function toogleTable(id){
    jQuery("th.header").closest("table").hide();
    jQuery("#blasttable"+id).show()
}

function deleteTable(id){

    jQuery("#blasttable"+id).remove()
    jQuery("#"+id).remove()
}