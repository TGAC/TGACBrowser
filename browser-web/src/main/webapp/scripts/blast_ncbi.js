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
 * Date: 5/8/13
 * Time: 12:29 PM
 * To change this template use File | Settings | File Templates.
 */
function blastSearch(query, db, type) {
  ajaxurl = '/' + jQuery('#title').text() + '/' + jQuery('#title').text() + '/fluxion.ajax';
  var link = "";
  var id = randomString(8);
  var format = "format";
  Fluxion.doAjax(
          'blastServiceNCBI',
          'ncbiBlastSearchSequence',
          {'url': ajaxurl, 'querystring': query, 'blastdb': db, 'location': link, 'BlastAccession': id, 'format': format, 'type': type},
          {'doOnSuccess': function (json) {
            ncbiBLASTResult(json.html)
          },
            'doOnError': function (json) {
              alert(json.error);
            }
          });
}

function blastTrackSearch(query, start, end, hit, db, type) {
  jQuery("#notifier").html("<img src='images/browser/loading2.gif' height='10px'> BLAST running ");
  jQuery("#notifier").show();
  ajaxurl = '/' + jQuery('#title').text() + '/' + jQuery('#title').text() + '/fluxion.ajax';
  var link = path;
  var id = randomString(8);
  var format = "format";
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

  Fluxion.doAjax(
          'blastServiceNCBI',
          'ncbiBlastSearchTrack',
          {'url': ajaxurl, 'querystring': query, 'blastdb': db, 'location': link, 'BlastAccession': id, 'format': format, 'type': type},
          {'doOnSuccess': function (json) {
            blastsdata.push(
                    {
                      id: json.html,
                      start: start,
                      end: end,
                      db: db,
                      hit: hit,
                      link: link,
                      format: 5,
                      type: type
                    });

            checkTask(json.html, db, 5, start, end, hit, link)
          },
            'doOnError': function (json) {
              alert(json.error);
            }
          });
}
function checkTask(id, db, format, start, end, hit, link) {
  ajaxurl = '/' + jQuery('#title').text() + '/' + jQuery('#title').text() + '/fluxion.ajax';
  var link = path;

  var format = "format";
  Fluxion.doAjax(
          'blastServiceNCBI',
          'ncbiBlastGetResultTrack',
          {'start': start, 'end': end, 'hit': hit, 'url': ajaxurl, 'location': link, 'BlastAccession': id},
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
          },
            'doOnError': function (json) {
              alert(json.error);
            }
          });
}
function ncbiBLASTResult(id) {
  ajaxurl = '/' + jQuery('#title').text() + '/' + jQuery('#title').text() + '/fluxion.ajax';

  var format = "format";
  Fluxion.doAjax(
          'blastServiceNCBI',
          'ncbiBlastGetResult',
          {'url': ajaxurl,  'BlastAccession': id, 'format': format},
          {'doOnSuccess': function (json) {
            jQuery('#blastresult').html(json.html);
            jQuery("#blasttable").tablesorter();
            jQuery("#notifier").hide()
            jQuery("#notifier").html("");
          },
            'doOnError': function (json) {
              alert(json.error);
            }
          });
}