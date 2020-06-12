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
 * Time: 4:05 PM
 * To change this template use File | Settings | File Templates.
 */

// API type functions to make generous coding
function getDragableLeft() {
    return jQuery("#draggable").position().left;
}

function setDragableLeft(left) {
    jQuery("#draggable").animate({"left": left}, 100, function () {
        setNavPanel()
        if(jQuery('#map').is(':visible') ) {
            setMapMarkerTop(getBegin());
        }
    });
}

function getDragableWidth() {
    return jQuery("#draggable").css("width");
}

function setDragableWidth(width) {
    jQuery("#draggable").animate({"width": width}, 100, function () {
        setNavPanel()
    });
    var diff = parseInt((getEnd() - getBegin()) * 0.90);
    var bp = "bp";
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

    jQuery("#leftbig").attr('title', "Move Left(" + diff + "" + bp + ")");
    jQuery("#rightbig").attr('title', "Move Right(" + diff + "" + bp + ")");
    if(jQuery('#map').is(':visible') ) {
        setMapMarkerHeight(getEnd() - getBegin())
    }

}

function getbglayerWidth() {
    return jQuery("#bg_layer").css("width");
}

function setbglayerWidth(width, seq) {
//  to call disp seq function if left is 0 then it doesn't call so
    if (seq == true) {
        jQuery("#bg_layer").animate({"width": width}, 100, function () {
            dispSeq();
        });
    }
    else {
        jQuery("#bg_layer").animate({"width": width}, { duration: 100, queue: false});
    }
}

function getbglayerLeft() {
    return jQuery("#bg_layer").position().left;
}

function setbglayerLeft(left, seq) {
    var diff = parseFloat(left) - parseFloat(getbglayerLeft());
    if (parseInt(diff) >= 1 || parseInt(diff) <= -1) {
        if (seq == true) {
            jQuery("#bg_layer").animate({"left": left}, 100, function () {
                dispSeq();
            });
        }
        else {
            jQuery("#bg_layer").animate({"left": left}, { duration: 100, queue: false});
        }
    }
    else {
        setDragableLeft(parseFloat(getbglayerLeft()));

    }

}

function getBegin() {
    if (parseInt(jQuery("#begin").val()) > 0) {
        return jQuery("#begin").val();
    }
    else {
        return 1;
    }
}

function setBegin(begin) {
    if (parseInt(begin) > 0) {
        var begin_scale = (begin)*scale;
        if(begin_scale % 1 != 0){
             begin_scale = (begin_scale).toFixed(2)
        }
        jQuery("#begin").val(parseInt(begin));
        jQuery("#begin_scale").val(parseInt(begin_scale).toLocaleString());

    }
    else {
        jQuery("#begin").val(parseInt(1));
        jQuery("#begin_scale").val(parseInt(1));

    }

    jQuery("#ruler").html((begin*scale).toLocaleString());

    var inLength = jQuery("#ruler").width();

    jQuery("#begin_scale").width(inLength);


}

function getEnd() {
    if (parseInt(jQuery("#end").val()) <= sequencelength) {
        return jQuery("#end").val();
    }
    else {
        return sequencelength;
    }
}

function setEnd(end) {
    if (parseInt(end) <= sequencelength) {
        var end_scale = (end)*scale;

        if(end_scale % 1 != 0){
            end_scale = (end_scale).toFixed(2)
        }

        jQuery("#end").val(parseInt(end));
        jQuery("#end_scale").val(parseInt(end_scale).toLocaleString());
    }
    else {
        jQuery("#end").val(sequencelength);
        jQuery("#end_scale").val((sequencelength*scale).toFixed(2));
    }
    jQuery("#ruler").html((end*scale).toLocaleString());

    var inLength = jQuery("#ruler").width();

    jQuery("#end_scale").width(inLength);


}

function setMapMarkerTop(top) {
    var ref = jQuery("#" + seqregname)
    var height = ref.css('height');
    var maptop = parseFloat(jQuery("#" + seqregname).position().top) + top * parseFloat(height) / sequencelength;
    jQuery("#mapmarker").animate({"top": maptop}, 100);
}

function setMapMarkerHeight(height) {
    var ref = jQuery("#" + seqregname)
    var mapheight = height * parseFloat(ref.css('height')) / sequencelength;
    jQuery("#mapmarker").animate({"height": mapheight}, 100);
}

function setMapMarkerLeft() {
    var left = parseInt(jQuery("#" + seqregname).css('left'));
    jQuery("#mapmarker").animate({"left": left}, 100);
}

function getMapMarkerTop() {
    return jQuery("#mapmarker").position().top;
}

function getMapMarkerHeight() {
    return jQuery("#mapmarker").css('height');
}

function getMapMarkerLeft() {
    return jQuery("#mapmarker").position().left;
}