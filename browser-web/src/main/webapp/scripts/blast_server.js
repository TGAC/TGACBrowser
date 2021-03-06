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
 * Time: 3:51 PM
 * To change this template use File | Settings | File Templates.
 */


function blastSearch(query, blastdb, type, params) {
  if (jQuery('#blast_list img').length < 3) {
    submitBlastTask(query, blastdb, "6 qseqid sseqid pident length mismatch gapopen qstart qend sstart send evalue bitscore sseq qseq", type, 0, 0, 0, params);
    jQuery('#main').animate({"height": "0px"}, { duration: 300, queue: false});
    jQuery('#main').fadeOut();
  }
  else {
    alert("You can submit 3 running jobs at a time.")
  }
}


function blastTrackSearch(query, start, end, hit, blastdb, type) {
  if (!window['blasttrack']) {
    setBLASTTrack()
  }

  submitBlastTask(query, blastdb, "6 qseqid sseqid qstart qend bitscore qseq sseq btop", type, start, end, hit, " -num_threads  4 ");
  start_global = start;
  end_global = end;
  hit_global = hit;
}

function submitBlastTask(query, db, format, type, start, end, hit, params) {
  // format 5 for plain text, 6 for xml
  var id = randomString(8);
  var database = db.split(":");
  db = database[0];
  var link = database[1];
  if (format == 5) {

    blastid = id;
    blastdb = db;

  }

  blastsdata.push(
          {
            id: id,
            start: start,
            end: end,
            db: blastdb,
            hit: hit,
            link: link,
            format: format,
            type: type,
            params: params
          });
  ajaxurl = '/' + jQuery('#title').text() + '/' + jQuery('#title').text() + '/fluxion.ajax';
  jQuery("#blast_list").append("<div style=\"height:50px;\" id='" + id + "' class='blast_list_node list-group-item list-group-item-info'> <b>BLAST job " + id + " </b> <img style='position: relative;' src='./images/browser/loading_big.gif' height=15px alt='Loading'></div>")
  jQuery("#blast_no").html(jQuery("#blast_list").children().size())

    Fluxion.doAjax(
          'blastservice',
          'submitBlastTask',
          {'url': ajaxurl, 'querystring': query, 'blastdb': db, 'location': link, 'BlastAccession': id, 'format': format, "type": type, "params": params},
          {'doOnSuccess': function (json) {
            if (json.id) {
              checkTask(id, db, format, start, end, hit, link, json.id, type);
            }
            else {
              checkTask(id, db, format, start, end, hit, link, id, type);
            }
          },
            'doOnError': function (json) {
              alert(json.error);
            }
          });

}

var processTaskSubmission = function (json) {
  if (json.response) {
    console.log(json.response);
  }
};

function checkTask(task, db, format, start, end, hit, link, old_id, type) {
  jQuery("#notifier").html("<img src='images/browser/loading2.gif' height='10px'> BLAST running ");
  jQuery("#notifier").show();
  Fluxion.doAjax(
          'blastservice',
          'checkTask',
          {'url': ajaxurl, 'taskid': task, 'old_taskid': old_id},
          {'ajaxType': 'periodical', 'updateFrequency': 5, 'doOnSuccess': function (json) {
            if (json.result == 'FAILED') {
              alert('Blast search: ' + json.result);
              jQuery("#notifier").hide();
              jQuery("#notifier").html("");
            }
            else if (json.result == 'RUNNING') {
//              jQuery('#blastresult').html("<span style=\"position:relative; left:50%;\"> BLASTing &nbsp; <img alt=\"Loading\" src=\"./images/browser/loading_big.gif\" style=\"position: relative;\"> </span> </div>");
            }
            else if (json.result == 'COMPLETED') {
//              jQuery('#blastresult').html("<span style=\"position:relative; left:50%;\"> Completed Processing &nbsp; <img alt=\"Loading\" src=\"./images/browser/loading_big.gif\" style=\"position: relative;\"> </span> </div>");
              if (format == "6 qseqid sseqid qstart qend bitscore qseq sseq btop") {
                Fluxion.doAjax(
                        'blastservice',
                        'blastSearchTrack',
                        {'start': start, 'end': end, 'hit': hit, 'accession': task, 'location': link, 'db': db, 'url': ajaxurl, 'old_taskid': json.old_id},
                        {'doOnSuccess': function (json) {
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
              }    else{

                      Fluxion.doAjax(
                          'blastservice',
                          'blastSearchSequence',
                          {'accession': task, 'db': db, 'location': link, 'url': ajaxurl, 'old_taskid': json.old_id, 'type': type},
                          {'doOnSuccess': function (json) {
                              if (json.html == "No hits found.") {
                                  jQuery("#"+json.id).removeClass("list-group-item-info")
                                  jQuery("#"+json.id).addClass("list-group-item-danger")
                                  jQuery("#" + json.id).html("<b>BLAST job " + json.id + "</b><br> No hits found. <span style=\"float: right; position: relative;\" onclick=deleteTable('" + json.id + "') class=\"ui-button ui-icon ui-icon-trash\" > </span> ")
                              }
                              else if (json.html == "FAILED") {
                                  jQuery("#"+json.id).removeClass("list-group-item-info")
                                  jQuery("#"+json.id).addClass("list-group-item-danger")
                                  jQuery("#" + json.id).html("<b>BLAST job " + json.id + "</b><br> Failed. <span style=\"float: right; position: relative;\" onclick=deleteTable('" + json.id + "') class=\"ui-button ui-icon ui-icon-trash\" > </span> ")
                              }
                              else if (json.error == "error") {
                                  jQuery("#"+json.id).removeClass("list-group-item-info")
                                  jQuery("#"+json.id).addClass("list-group-item-danger")
                                  jQuery("#" + json.id).html("<b>BLAST job " + json.id + "</b><br> Failed. <span style=\"float: right; position: relative;\" onclick=deleteTable('" + json.id + "') class=\"ui-button ui-icon ui-icon-trash\" > </span> ")
                              }
                              else {
                                  jQuery("#"+json.id).removeClass("list-group-item-info")
                                  jQuery("#"+json.id).addClass("list-group-item-success")
                                  jQuery("#" + json.id).html("BLAST job " + json.id + " <br>  <span onclick=toogleTable('" + json.id + "') class=\"ui-button ui-icon ui-icon-zoomin\" style=\"float: right; position: relative;\"> </span> <span style=\"float: right; position: relative;\" onclick=deleteTable('" + json.id + "') class=\"ui-button ui-icon ui-icon-trash\" > </span> ")
                                  jQuery('#main').animate({"height": "0px"}, { duration: 300, queue: false});
                                  jQuery('#main').fadeOut();
                                  parseBLAST(json);
                              }
                              jQuery("#notifier").hide()
                              jQuery("#notifier").html("");
                          }
                          });
                  }

            }
          },
            'doOnError': function (json) {
              alert(json.error);
            }
          });
}
