/**
 * Created with IntelliJ IDEA.
 * User: thankia
 * Date: 4/29/13
 * Time: 3:51 PM
 * To change this template use File | Settings | File Templates.
 */


function blastSearch(query, blastdb, type) {
  jQuery('#blastresult').html("<span style=\"position:relative; left:50%;\"> Submitting &nbsp; <img alt=\"Loading\" src=\"./images/browser/loading_big.gif\" style=\"position: relative;\"> </span> </div>");
  submitBlastTask(query, blastdb, 6, type);
  jQuery('#main').animate({"height": "0px"}, { duration: 300, queue: false});
  jQuery('#main').fadeOut();
}


function blastTrackSearch(query, start, end, hit, blastdb, type) {
  if (!window['blasttrack']) {

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

  submitBlastTask(query, blastdb, 5, type, start, end, hit);
  start_global = start;
  end_global = end;
  hit_global = hit;
}

function submitBlastTask(query, db, format, type, start, end, hit) {
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
            type: type
          });

  ajaxurl = '/' + jQuery('#title').text() + '/' + jQuery('#title').text() + '/fluxion.ajax';

  Fluxion.doAjax(
          'blastservice',
          'submitBlastTask',
          {'url': ajaxurl, 'querystring': query, 'blastdb': db, 'location': link, 'BlastAccession': id, 'format': format, "type": type},
          {'doOnSuccess': processTaskSubmission,
            'doOnError': function (json) {
              alert(json.error);
            }
          });
  checkTask(id, db, format, start, end, hit, link);
}

var processTaskSubmission = function (json) {
  if (json.response) {
    console.log(json.response);
  }
};

function checkTask(task, db, format, start, end, hit, link) {
  jQuery("#notifier").html("<img src='images/browser/loading2.gif' height='10px'> BLAST running ");
  jQuery("#notifier").show();
  Fluxion.doAjax(
          'blastservice',
          'checkTask',
          {'url': ajaxurl, 'taskid': task},
          {'ajaxType': 'periodical', 'updateFrequency': 5, 'doOnSuccess': function (json) {
            if (json.result == 'FAILED') {
              alert('Blast search: ' + json.result);
              jQuery("#notifier").hide();
              jQuery("#notifier").html("");
            }
            else if (json.result == 'RUNNING') {
              jQuery('#blastresult').html("<span style=\"position:relative; left:50%;\"> BLASTing &nbsp; <img alt=\"Loading\" src=\"./images/browser/loading_big.gif\" style=\"position: relative;\"> </span> </div>");
            }
            else if (json.result == 'COMPLETED') {
              jQuery('#blastresult').html("<span style=\"position:relative; left:50%;\"> Completed Processing &nbsp; <img alt=\"Loading\" src=\"./images/browser/loading_big.gif\" style=\"position: relative;\"> </span> </div>");
              if (format == 6) {
                Fluxion.doAjax(
                        'blastservice',
                        'blastSearchSequence',
                        {'accession': task, 'db': db, 'location': link, 'url': ajaxurl},
                        {'doOnSuccess': function (json) {
                          jQuery('#blastresult').html(json.html);
                          jQuery("#blasttable").tablesorter();
                          jQuery("#notifier").hide()
                          jQuery("#notifier").html("");
                        }
                        });
              }
              else if (format == 5) {
                Fluxion.doAjax(
                        'blastservice',
                        'blastSearchTrack',
                        {'start': start, 'end': end, 'hit': hit, 'accession': task, 'location': link, 'db': db, 'url': ajaxurl},
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
