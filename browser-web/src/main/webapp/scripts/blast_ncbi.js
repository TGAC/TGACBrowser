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
 * Date: 5/8/13
 * Time: 12:29 PM
 * To change this template use File | Settings | File Templates.
 */
function blastSearch(query, db, type) {
//        ncbiBLAST(query, db) {
  ajaxurl = '/' + jQuery('#title').text() + '/' + jQuery('#title').text() + '/fluxion.ajax';
  var link = "";
  var id = randomString(8);
  var format = "format";
    jQuery("#blast_list").append("<div id='" + id + "' class='blast_list_node'> <b>BLAST job " + id + " </b> <img style='position: relative;' src='./images/browser/loading_big.gif' height=15px alt='Loading'></div>")

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

   setBLASTTrack()

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
              window['blasttrack'] = json.blast;
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
            jQuery("#notifier").hide()
            jQuery("#notifier").html("");

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
          },
            'doOnError': function (json) {
              alert(json.error);
            }
          });
}