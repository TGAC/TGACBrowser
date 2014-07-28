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
 * Time: 4:21 PM
 * To change this template use File | Settings | File Templates.
 */
function dispSeq() {
    var len = sequencelength;
    maxLen = jQuery(window).width();
    var width = getbglayerWidth();
    var left = getbglayerLeft();


    var seqStart, seqEnd;
    seqStart = parseFloat(left) * len / parseFloat(maxLen);
    seqEnd = seqStart + parseFloat(width) * len / parseFloat(maxLen);
    seqEnd = Math.round(parseFloat(seqEnd));
    removeAllPopup();
    seqBar(seqStart, seqEnd);
    dispCoord(seqStart, seqEnd);
    browser_coordinates();
    setNavPanel();
    trackToggle("all");
    updateJSON();
}

function changeSeq(begin, end) {
    trackToggle("all");
    updateJSON();
    seqBar(begin, end);
    browser_coordinates();
}

function seqBar(seqStart, seqEnd) {

    var temp = seqEnd - seqStart;
    var seqLen = visualLength(temp);
    console.log(seqLen+" "+maxLen)
    if (parseFloat(seqLen) <= (parseFloat(maxLen)) && jQuery("#fasta").html().indexOf('true') >= 0) {
        selectionStart = seqStart;
        selectionEnd = seqEnd;
        var diff = (parseFloat(maxLen)) - parseFloat(seqLen);
        var newlength = diff * temp / seqLen;
        newStart = parseInt((parseFloat(seqStart) - parseFloat(newlength) / 2));
        newEnd = parseInt((parseFloat(seqEnd) + parseFloat(newlength) / 2));

        if (newStart < 0) {
            newEnd = newEnd + (-parseInt(newStart));
            newStart = 0;
        }
        if (newEnd > sequencelength) {
            newStart = newStart - (newEnd - sequencelength);
            newEnd = sequencelength;
        }

        var partial = (parseInt(getEnd()) - parseInt(getBegin())) / 2;
        var start = parseInt(getBegin());// - parseInt(partial)
        var end = parseInt(getEnd());// + parseInt(partial);
        Fluxion.doAjax(
            'dnaSequenceService',
            'loadSequence',
            {'query': seqregname, 'from': start, 'to': end, 'coord': coord, 'url': ajaxurl},
            {'doOnSuccess': function (json) {
                var seq = json.seq;
                if (seq.length > 1) {
                    temp = seq;

                    temp = "<font style='Courier New'>" + stringColour(temp);
                    if (start < 0) {
                        var left = (1 - start) * parseInt(maxLen) / end * 3 / 4;
                    }

                    jQuery('#sequenceString').html(temp);
                    translate(seq);
                }
                else {
                    jQuery("#translation_div").hide();
                    jQuery("#sequence").css('height', '20px');
                    jQuery("#tracks").css('top', '20px');
                    jQuery('#sequenceString').html("<hr id = \"seqbar\" style='background-color: silver; z-index: 999'>");
                }
            }
            });
    }
    else {

        selectionStart = seqStart;
        selectionEnd = seqEnd;
        seqLen = maxLen;
        newStart = seqStart;
        newEnd = seqEnd;

        jQuery("#translation_div").hide();
        jQuery("#sequence").css('height', '20px');
        jQuery("#tracks").css('top', '20px');
        jQuery('#sequenceString').html("<hr id = \"seqbar\" style='background-color: silver; z-index: 999'>");
    }
}


function jumpToSeqFromGo() {
    var begin = jQuery("#begin_scale").val()/scale;
    var end = jQuery("#end_scale").val()/scale;
    setBegin(begin)
    setEnd(end)

    jumpToSeq();
}


function jumpToSeq() {
    console.log("jumptoseq")
    var begin = getBegin();
    var end = getEnd();

    var len = sequencelength;
    if (parseInt(begin) < 0) {
        begin = 0;
        setBegin(0);
    }
    if (parseInt(end) > len) {
        end = len;
        setEnd(len);
    }
    if (parseInt(begin) >= parseInt(end)) {
        alert("Ending position need to be bigger than Starting position");
    }
    else {
        console.log("else")

        console.log(begin)
        console.log(end)

        console.log(minWidth)
//        if ((parseInt(end) - parseInt(begin)) < minWidth) {
//
//            console.log("if")
//            var diff = minWidth - (parseInt(end) - parseInt(begin));
//
//            var tempbegin = parseInt(getBegin() - (diff / 2))
//            var tempend = parseInt(parseInt(getEnd()) + parseInt(diff / 2))
//            if (parseInt(tempbegin) < 0) {
//                tempend = tempend + (-(tempbegin));
//                tempbegin = 0;
//            }
//            if (parseInt(tempend) > len) {
//                tempbegin = tempbegin - (tempend - len);
//                tempend = len;
//            }
//
//            setBegin(tempbegin);
//            setEnd(tempend);
//        }

        var begin = getBegin()-1;
        var end = getEnd();

        console.log(begin)
        console.log(len)
        console.log(maxLen)
        var seqStart = parseInt(begin) * parseInt(maxLen) / len;
        var seqEnd = parseInt(end) * parseInt(maxLen) / len;
        var width = parseFloat(seqEnd) - parseFloat(seqStart);
        console.log(seqStart)

        removeAllPopup();
        setDragableLeft(seqStart);
        setDragableWidth(width);
        setbglayerLeft(seqStart, false);
        setbglayerWidth(width);

        changeSeq(begin, end);
    }

}

function translate(sequence) {

    var space;
    var ptn_seq = "";

    for (var j = 0; j < 3; j++) {

        var seq = sequence.substring(j, sequence.length);
        space = (parseFloat(maxLen) - (visualLength(seq.length) / 3)) / parseFloat(seq.length / 3);
        var width = maxLen / (getEnd()-getBegin() + 1)
        var i = 0;
        for (i; i <= seq.length - 3; i = i + 3) {
            var chunk = seq.substring(i, i + 3);
            var left = (parseInt(i*width)+parseInt(j*width));
            ptn_seq += "<span class=\"span_str aminoacid-str\" style=\"border: 1px solid gray; width:"+(width*3)+"px; LEFT: "+left+"px; \">&nbsp;";
            if (chunk.indexOf("N") > -1) {
                ptn_seq += "X";
            }
            else if (chunk == "GCT" || chunk == "GCC" || chunk == "GCA" || chunk == "GCG") {
                ptn_seq += "A";
            }
            else if (chunk == "CGT" || chunk == "CGC" || chunk == "CGA" || chunk == "CGG" || chunk == "AGA" || chunk == "AGG") {
                ptn_seq += "R";
            }
            else if (chunk == "AAT" || chunk == "AAC") {
                ptn_seq += "N";
            }
            else if (chunk == "GAT" || chunk == "GAC") {
                ptn_seq += "D";
            }
            else if (chunk == "TGT" || chunk == "TGC") {
                ptn_seq += "C";
            }
            else if (chunk == "CAA" || chunk == "CAG") {
                ptn_seq += "Q";
            }
            else if (chunk == "GAA" || chunk == "GAG") {
                ptn_seq += "E";
            }
            else if (chunk == "GGT" || chunk == "GGC" || chunk == "GGA" || chunk == "GGG") {
                ptn_seq += "G";
            }
            else if (chunk == "CAT" || chunk == "CAC") {
                ptn_seq += "H";
            }
            else if (chunk == "ATT" || chunk == "ATC" || chunk == "ATA") {
                ptn_seq += "I";
            }
            else if (chunk == "ATG") {
                ptn_seq += "M";
            }
            else if (chunk == "TTA" || chunk == "TTG" || chunk == "CTT" || chunk == "CTC" || chunk == "CTA" || chunk == "CTG") {
                ptn_seq += "L";
            }
            else if (chunk == "AAA" || chunk == "AAG") {
                ptn_seq += "K";
            }
            else if (chunk == "TTT" || chunk == "TTC") {
                ptn_seq += "F";
            }
            else if (chunk == "CCT" || chunk == "CCC" || chunk == "CCA" || chunk == "CCG") {
                ptn_seq += "P";
            }
            else if (chunk == "TCT" || chunk == "TCC" || chunk == "TCA" || chunk == "TCG" || chunk == "AGT" || chunk == "AGC") {
                ptn_seq += "S";
            }
            else if (chunk == "ACT" || chunk == "ACC" || chunk == "ACA" || chunk == "ACG") {
                ptn_seq += "T";
            }
            else if (chunk == "TGG") {
                ptn_seq += "W";
            }
            else if (chunk == "TAT" || chunk == "TAC") {
                ptn_seq += "Y";
            }
            else if (chunk == "GTT" || chunk == "GTC" || chunk == "GTA" || chunk == "GTG") {
                ptn_seq += "V";
            }
            else if (chunk == "TAA" || chunk == "TGA" || chunk == "TAG") {
                ptn_seq += "*";
            }
            else {
                ptn_seq += "-";
            }
            ptn_seq += "&nbsp;</span>";
        }

        ptn_seq += "</span><br>";
    }

    if (space >= 0) {
        jQuery("#translation_div").show();
        jQuery("#tracks").css('top', '60px');
        jQuery("#sequence").css('height', '60px');
        jQuery("#translation_div").html("<br>" + ptn_seq);
    }
    else {
        jQuery("#translation_div").html("");
        jQuery("#translation_div").hide;
        jQuery("#sequence").css('height', '20px');
        jQuery("#tracks").css('top', '20px');
    }
}
