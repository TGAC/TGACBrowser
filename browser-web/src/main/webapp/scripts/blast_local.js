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
 * Created with IntelliJ IDEA.
 * User: thankia
 * Date: 4/29/13
 * Time: 3:48 PM
 * To change this template use File | Settings | File Templates.
 */
var blastbinary = jQuery("#blastLocation").html();

function blastSearch(query, db, type, params) {
    blastbinary = jQuery("#blastLocation").html();
    jQuery("#notifier").html("<img src='images/browser/loading2.gif' height='10px'> BLAST running ");
    jQuery("#notifier").show();
    var database = db.split(":");
    db = database[0];
    var id = randomString(8);
    var link = database[1];
    jQuery("#blast_list").append("<div id='" + id + "' class='blast_list_node'> <b>BLAST job " + id + " </b> <img style='position: relative;' src='./images/browser/loading_big.gif' height=15px alt='Loading'></div>")


    var location = jQuery('#title').text();

    ajaxurl = '/' + jQuery('#title').text() + '/' + jQuery('#title').text() + '/fluxion.ajax';
    Fluxion.doAjax(
        'blastservicelocal',
        'blastSearchSequence',
        {'query': query, 'blastdb': db, 'location': location, 'type': type, 'url': ajaxurl, 'BlastAccession': id, 'blastBinary': blastbinary, 'link': link, 'params': params, 'format': '6 qseqid sseqid qstart qend bitscore qseq sseq btop'},
        {'doOnSuccess': function (json) {
            jQuery('#main').animate({"height": "0px"}, { duration: 300, queue: false});
            jQuery('#main').fadeOut();


            if (json.html == "No hits found.") {
                jQuery("#" + json.id).html("<b>BLAST job " + json.id + "</b><br> No hits found. <span onclick=deleteTable('" + json.id + "') class=\"ui-button ui-icon ui-icon-trash\" > </span> ")
            }
            else if (json.html == "FAILED") {
                jQuery("#" + json.id).html("<b>BLAST job " + json.id + "</b><br> Failed. <span onclick=deleteTable('" + json.id + "') class=\"ui-button ui-icon ui-icon-trash\" > </span> ")
            }
            else if (json.error == "error") {
                jQuery("#" + json.id).html("<b>BLAST job " + json.id + "</b><br> Failed. <span onclick=deleteTable('" + json.id + "') class=\"ui-button ui-icon ui-icon-trash\" > </span> ")
            }
            else {
                jQuery("#" + json.id).html("BLAST job " + json.id + " <span title=\"Finished\" class=\"ui-button ui-icon ui-icon-check\"></span> <br>  <span onclick=toogleTable('" + json.id + "') class=\"ui-button ui-icon ui-icon-zoomin\" > </span> <span onclick=deleteTable('" + json.id + "') class=\"ui-button ui-icon ui-icon-trash\" > </span> ")
                jQuery('#main').animate({"height": "0px"}, { duration: 300, queue: false});
                jQuery('#main').fadeOut();
                parseBLAST(json);
            }
            jQuery("#notifier").hide()
            jQuery("#notifier").html("");
        }
        });
}


function blastTrackSearch(query, start, end, hit, db, type) {
    blastbinary = jQuery("#blastLocation").html();
    jQuery("#notifier").html("<img src='images/browser/loading2.gif' height='10px'> BLAST running ");
    jQuery("#notifier").show();
    if (!window['blasttrack']) {

        jQuery("#tracklist").append("<p title='blast' id=blastcheck><input type=\"checkbox\" checked id='blasttrackCheckbox' name='blasttrackCheckbox' onClick=loadTrackAjax(\"blasttrack\",\"blasttrack\");\>  Blasttrack\  </p>");

        jQuery("#mergetracklist").append("<span id=blasttrackspan> <input type=\"checkbox\" id='blasttrackmergedCheckbox' name='blasttrackmergedCheckbox' onClick=mergeTrack(\"blasttrack\"); value=blasttrack >Blast Track</span>");

        jQuery("#tracks").append("<div id='blasttrack_div' class='feature_tracks'> Blast Track </div>");

        jQuery("#blasttrack_div").html("<img style='position: relative; left: 50%; ' src='./images/browser/loading_big.gif' alt='Loading'>")
        jQuery("#blasttrack_div").fadeIn();

        track_list.push(
            {name: "blasttrack", display_label: "blasttrack", id: "noid", desc: "blast from browser", disp: 1, merge: 0}
        );
        window['blasttrack'] = "running";

        window['track_listblasttrack'] = {
            name: "blasttrack",
            id: "noid",
            display_label: "blasttrack",
            desc: "blast from browser",
            disp: 1,
            merge: 0,
            label: 0,
            graph: false
        }
    }

    ajaxurl = '/' + jQuery('#title').text() + '/' + jQuery('#title').text() + '/fluxion.ajax';
    var location = jQuery('#title').text();
    var database = db.split(":");
    db = database[0];
    var id = randomString(8);
    var link = database[1];
    blastid = id;
    blastdb = db;
    blastsdata.push(
        {
            id: id,
            start: start,
            end: end,
            db: blastdb,
            hit: hit,
            link: link,
            format: '6 qseqid sseqid qstart qend bitscore qseq sseq btop',
            type: type
        });

    var format = '6 qseqid sseqid qstart qend bitscore qseq sseq btop';

    Fluxion.doAjax(
        'blastservicelocal',
        'blastSearchTrack',
        {'query': query, 'blastdb': db, 'location': location, 'url': ajaxurl, 'start': start, 'end': end, 'hit': hit, 'BlastAccession': id, 'type': type, 'format': format, 'blastBinary': blastbinary, 'params': ""},
        {'doOnSuccess': function (json) {
            findAndRemove(blastsdata, 'id', json.id);

            if (blastsdata.length == 0) {
                jQuery("#notifier").hide()
                jQuery("#notifier").html("");
            }
            if (!window['blasttrack']) {
                window['blasttrack'] = "running";
            }
            if (window['blasttrack'] == "running") {
                window['blasttrack'] = json.blast;
            }
            else {
                jQuery.merge(window['blasttrack'], json.blast);
            }
            jQuery('input[name=blasttrackCheckbox]').attr('checked', true);
            trackToggle("blasttrack");
        }
        });
}

function checkTask(task, db, format, start, end, hit, link) {
    jQuery("#notifier").html("<img src='images/browser/loading2.gif' height='10px'> BLAST running ");
    jQuery("#notifier").show();
    if (!window['blasttrack']) {

        jQuery("#tracklist").append("<p title='blast' id=blastcheck><input type=\"checkbox\" checked id='blasttrackCheckbox' name='blasttrackCheckbox' onClick=loadTrackAjax(\"blasttrack\",\"blasttrack\");\>  Blasttrack\  </p>");

        jQuery("#mergetracklist").append("<span id=blasttrackspan> <input type=\"checkbox\" id='blasttrackmergedCheckbox' name='blasttrackmergedCheckbox' onClick=mergeTrack(\"blasttrack\"); value=blasttrack >Blast Track</span>");

        jQuery("#tracks").append("<div id='blasttrack_div' class='feature_tracks'> Blast Track </div>");

        jQuery("#blasttrack_div").html("<img style='position: relative; left: 50%; ' src='./images/browser/loading_big.gif' alt='Loading'>")
        jQuery("#blasttrack_div").fadeIn();

        track_list.push(
            {name: "blasttrack", display_label: "blasttrack", id: "noid", desc: "blast from browser", disp: 1, merge: 0}
        );
        window['blasttrack'] = "running";
    }

    ajaxurl = '/' + jQuery('#title').text() + '/' + jQuery('#title').text() + '/fluxion.ajax';
    var location = jQuery('#title').text();

    Fluxion.doAjax(
        'blastservicelocal',
        'checkBlast',
        {'url': ajaxurl, 'start': start, 'end': end, 'hit': hit, 'BlastAccession': task, 'location': location, 'link': link},
        {'doOnSuccess': function (json) {

            findAndRemove(blastsdata, 'id', json.id);

            if (blastsdata.length == 0) {
                jQuery("#notifier").hide()
                jQuery("#notifier").html("");
            }
            if (!window['blasttrack']) {
                window['blasttrack'] = "running";
            }
            if (window['blasttrack'] == "running") {
                window['blasttrack'] = json.blast;//(decodeURIComponent(json.blast.replace(/\s+/g, ""))).replace(/>/g, "");
            }
            else {
                jQuery.merge(window['blasttrack'], json.blast);
            }
            jQuery('input[name=blasttrackCheckbox]').attr('checked', true);
            trackToggle("blasttrack");
        }
        });
}
