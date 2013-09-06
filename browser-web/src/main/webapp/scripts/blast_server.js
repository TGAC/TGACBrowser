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
 * Created with IntelliJ IDEA.
 * User: thankia
 * Date: 4/29/13
 * Time: 3:51 PM
 * To change this template use File | Settings | File Templates.
 */


function blastSearch(query, blastdb, type, params) {
  if (jQuery('#blast_list img').length < 3) {
    submitBlastTask(query, blastdb, "6 qseqid sseqid sseq length mismatch gapopen qstart qend sstart send bitscore pident evalue", type, 0, 0, 0, params);
    jQuery('#main').animate({"height": "0px"}, { duration: 300, queue: false});
    jQuery('#main').fadeOut();
  }
  else {
    alert("You can submit 3 running jobs at a time.")
  }
}


function blastTrackSearch(query, start, end, hit, blastdb, type) {
  if (!window['blasttrack']) {

    window['track_listblasttrack'] = {
         name: "blasttrack",
         id: 0,
         display_label: "blasttrack",
         desc: "blast from browser",
         disp: 1,
         merge: 0,
         label: 0,
         graph: false
       }

    jQuery("#tracklist").append("<p title='blast' id=blastcheck><input type=\"checkbox\" checked id='blasttrackCheckbox' name='blasttrackCheckbox' onClick=loadTrackAjax(\"blasttrack\",\"blasttrack\");\>  Blasttrack\  </p>");

    jQuery("#mergetracklist").append("<span id=blasttrackspan> <input type=\"checkbox\" id='blasttrackmergedCheckbox' name='blasttrackmergedCheckbox' onClick=mergeTrack(\"blasttrack\"); value=blasttrack >Blast Track</span>");

    jQuery("#tracks").append("<div id='blasttrack_div' class='feature_tracks'> Blast Track </div>");

    jQuery("#blasttrack_div").html("<img style='position: relative; left: 50%; ' src='./images/browser/loading_big.gif' alt='Loading'>")
    jQuery("#blasttrack_div").fadeIn();

    track_list.push(
            {name: "blasttrack", display_label: "blasttrack", id: 0, desc: "blast from browser", disp: 1, merge: 0}
    );
    window['blasttrack'] = "running";
  }

  submitBlastTask(query, blastdb, 5, type, start, end, hit, " -num_threads  4 ");
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
  jQuery("#blast_list").append("<div id='" + id + "' class='blast_list_node'> <b>BLAST job " + id + " </b> <img style='position: relative;' src='./images/browser/loading_big.gif' height=15px alt='Loading'></div>")
  Fluxion.doAjax(
          'blastservice',
          'submitBlastTask',
          {'url': ajaxurl, 'querystring': query, 'blastdb': db, 'location': link, 'BlastAccession': id, 'format': format, "type": type, "params": params},
          {'doOnSuccess': function (json) {
            if (json.id) {
              console.log(id + ":" + json.id);
//              id = json.id;
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
              if (format == 6) {
                Fluxion.doAjax(
                        'blastservice',
                        'blastSearchSequence',
                        {'accession': task, 'db': db, 'location': link, 'url': ajaxurl, 'old_taskid': json.old_id, 'type': type},
                        {'doOnSuccess': function (json) {
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
                          //                          jQuery("#blasttable").tablesorter();
                          jQuery("#notifier").hide()
                          jQuery("#notifier").html("");
                        }
                        });
              }
              else if (format == 5) {
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
//                          window['blasttrack'].push(json.blast);
                            jQuery.merge(window['blasttrack'], json.blast);
                          }
                          jQuery('input[name=blasttrackCheckbox]').attr('checked', true);
//                          jQuery("#mergetracklist").append("<span id=blastcheckmerge> <input type=\"checkbox\" id='blasttrackmergedCheckbox' name='blasttrackmergedCheckbox' onClick=mergeTrack(\"blasttrack\"); value=blasttrack >Blast Track</span>");
                          trackToggle("blasttrack");
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
