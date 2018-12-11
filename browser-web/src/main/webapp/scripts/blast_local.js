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
    jQuery("#blast_list").append("<div style=\"height:50px;\" id='" + id + "' class='blast_list_node'> <b>BLAST job " + id + " </b> <img style='position: relative;' src='./images/browser/loading_big.gif' height=15px alt='Loading'></div>")
    var format = "6 qseqid sseqid pident length mismatch gapopen qstart qend sstart send evalue bitscore sseq qseq"

    var location = jQuery('#title').text();

    ajaxurl = '/' + jQuery('#title').text() + '/' + jQuery('#title').text() + '/fluxion.ajax';
    Fluxion.doAjax(
        'blastservicelocal',
        'submitBlastTask',
        {
            'url': ajaxurl,
            'querystring': query,
            'blastdb': db,
            'location': link,
            'BlastAccession': id,
            'format': format,
            "type": type,
            "params": params
        },
        {
            'doOnSuccess': function (json) {

                    checkTask(id, db, format, link, id, type);
            },
            'doOnError': function (json) {
                alert(json.error);
            }
        });
}



function blastTrackSearch(query, start, end, hit, db, type) {
    blastbinary = jQuery("#blastLocation").html();
    jQuery("#notifier").html("<img src='images/browser/loading2.gif' height='10px'> BLAST running ");
    jQuery("#notifier").show();
    if (!window['blasttrack']) {

setBLASTTrack()


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

function checkTask(task, db, format, link, old_id, type) {
    console.log("check task "+ task +","+ db +","+ format +","+ ","+ link +","+ old_id +","+ type)
    jQuery("#notifier").html("<img src='images/browser/loading2.gif' height='10px'> BLAST running ");
    jQuery("#notifier").show();
    Fluxion.doAjax(
        'blastservicelocal',
        'checkTask',
        {'url': ajaxurl, 'taskid': task, 'old_taskid': old_id},
        {
            'doOnSuccess': function (json) {
                if (json.result == "PENDING" || json.result == "RUNNING" || json.result == "COMPLETING") {
                    setTimeout(function () {
                        checkTask(task, db, format, link, old_id, type)
                    }, 1000);
                } else if (json.result == 'FAILED' || json.result == "SUSPENDED" || json.result == "CANCELLED" || json.result == "TIMEOUT") {
                    alert('Blast search: ' + json.result);
                    jQuery("#notifier").hide();
                    jQuery("#notifier").html("");
                }
                else if (json.result == 'COMPLETED') {
                    if (format == "6 qseqid sseqid qstart qend bitscore qseq sseq btop") {
                        Fluxion.doAjax(
                            'blastservicelocal',
                            'blastSearchTrack',
                            {
                                'start': start,
                                'end': end,
                                'hit': hit,
                                'accession': task,
                                'location': link,
                                'db': db,
                                'url': ajaxurl,
                                'old_taskid': json.old_id,
                                'slurm_id': slurm_id
                            },
                            {
                                'doOnSuccess': function (json) {
                                    jQuery("#notifier").hide()
                                    jQuery("#notifier").html("");
                                    findAndRemove(blastsdata, 'id', task);
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
                    } else {

                        Fluxion.doAjax(
                            'blastservicelocal',
                            'blastSearchSequence',
                            {
                                'accession': task,
                                'db': db,
                                'location': link,
                                'url': ajaxurl,
                                'old_taskid': json.old_id,
                                'type': type
                            },
                            {
                                'doOnSuccess': function (json) {
                                    if (json.html == "error") {
                                        jQuery("#" + json.id).removeClass("list-group-item-info")
                                        jQuery("#" + json.id).addClass("list-group-item-danger")
                                        jQuery("#" + json.id).html("<b>BLAST job " + json.id + "</b><br> "+stringTrim(json.error, 250)+" <span style=\"float: right; position: relative;\" onclick=deleteTable('" + json.id + "') class=\"ui-button ui-icon ui-icon-trash\" > </span> ")
                                    }else if (json.html == "No hits found.") {
                                        jQuery("#" + json.id).removeClass("list-group-item-info")
                                        jQuery("#" + json.id).addClass("list-group-item-danger")
                                        jQuery("#" + json.id).html("<b>BLAST job " + json.id + "</b><br> No hits found. <span style=\"float: right; position: relative;\" onclick=deleteTable('" + json.id + "') class=\"ui-button ui-icon ui-icon-trash\" > </span> ")
                                    }
                                    else if (json.html == "FAILED") {
                                        jQuery("#" + json.id).removeClass("list-group-item-info")
                                        jQuery("#" + json.id).addClass("list-group-item-danger")
                                        jQuery("#" + json.id).html("<b>BLAST job " + json.id + "</b><br> Failed. <span style=\"float: right; position: relative;\" onclick=deleteTable('" + json.id + "') class=\"ui-button ui-icon ui-icon-trash\" > </span> ")
                                    }
                                    else if (json.error == "error") {
                                        jQuery("#" + json.id).removeClass("list-group-item-info")
                                        jQuery("#" + json.id).addClass("list-group-item-danger")
                                        jQuery("#" + json.id).html("<b>BLAST job " + json.id + "</b><br> Failed. <span style=\"float: right; position: relative;\" onclick=deleteTable('" + json.id + "') class=\"ui-button ui-icon ui-icon-trash\" > </span> ")
                                    }
                                    else {
                                        jQuery("#" + json.id).removeClass("list-group-item-info")
                                        jQuery("#" + json.id).addClass("list-group-item-success")
                                        jQuery("#" + json.id).html("BLAST job " + json.id + " <br>  <span onclick=toogleTable('" + json.id + "') class=\"ui-button ui-icon ui-icon-zoomin\" style=\"float: right; position: relative;\"> </span> <span style=\"float: right; position: relative;\" onclick=deleteTable('" + json.id + "') class=\"ui-button ui-icon ui-icon-trash\" > </span> ")
                                        jQuery('#main').animate({"height": "0px"}, {duration: 300, queue: false});
                                        jQuery('#main').fadeOut();
                                        parseBLAST(json);
                                    }
                                    jQuery("#notifier").hide()
                                    jQuery("#notifier").html("");
                                },
                                'doOnError': function (json) {
                                    if (json.html.toLowerCase() == "error") {
                                        jQuery("#" + json.id).removeClass("list-group-item-info")
                                        jQuery("#" + json.id).addClass("list-group-item-danger")
                                        jQuery("#" + json.id).html("<b>BLAST job " + json.id + "</b><br> "+stringTrim(json.error, 200)+" <span style=\"float: right; position: relative;\" onclick=deleteTable('" + json.id + "') class=\"ui-button ui-icon ui-icon-trash\" > </span> ")
                                    }
                                }
                            });
                    }

                }
            }
        })
}

