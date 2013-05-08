/**
 * Created with IntelliJ IDEA.
 * User: thankia
 * Date: 4/29/13
 * Time: 3:48 PM
 * To change this template use File | Settings | File Templates.
 */
var blastbinary = jQuery("#blastLocation").html();
//var type = 'blastn';
//var location = "";
function blastSearch(query, db, type) {
  blastbinary = jQuery("#blastLocation").html();
  jQuery("#notifier").html("<img src='images/browser/loading2.gif' height='10px'> BLAST running ");
  jQuery("#notifier").show();
  var database = db.split(":");
  db = database[0];
  var id = randomString(8);
  var link = database[1];


  var location = "";

  ajaxurl = '/' + jQuery('#title').text() + '/' + jQuery('#title').text() + '/fluxion.ajax';
  Fluxion.doAjax(
          'blastservicelocal',
          'blastSearchSequence',
          {'query': query, 'blastdb': db, 'location': location, 'type': type, 'url': ajaxurl, 'BlastAccession': id, 'blastBinary': blastbinary},
          {'doOnSuccess': function (json) {
            jQuery('#main').animate({"height": "0px"}, { duration: 300, queue: false});
            jQuery('#main').fadeOut();
            jQuery('#blastresult').html(json.html);
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
            {name: "blasttrack", display_label: "blasttrack", id: 0, desc: "blast from browser", disp: 1, merge: 0}
    );
    window['blasttrack'] = "running";
    //delete window['blasttrack'];
    //delete track_list.splice(track_list.length, 1);
    //jQuery("#blasttrack_div").remove();
    //jQuery("#blastcheck").remove();
    //jQuery("#blastcheckmerge").remove();
  }

  var location = "";
  ajaxurl = '/' + jQuery('#title').text() + '/' + jQuery('#title').text() + '/fluxion.ajax';
  var location = "";
  var preset = false;
  var id = randomString(8);
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
            format: 5,
            type: type
          });

//  jQuery("#blasttrack_div").html("<div align='left' class='handle'><b> Blasttrack </b> </div> <img style='position: fixed; left: 50%; ' src='./images/browser/loading.gif' alt='Loading'>")
//  jQuery("#blasttrack_div").fadeIn();
  Fluxion.doAjax(
          'blastservicelocal',
          'blastSearchTrack',
          {'query': query, 'blastdb': db, 'location': location, 'url': ajaxurl, 'start': start, 'end': end, 'hit': hit, 'BlastAccession': id, 'type': type, 'blastBinary': blastbinary},
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
//                          window['blasttrack'].push(json.blast);
              jQuery.merge(window['blasttrack'], json.blast);
            }
            jQuery('input[name=blasttrackCheckbox]').attr('checked', true);
//                          jQuery("#mergetracklist").append("<span id=blastcheckmerge> <input type=\"checkbox\" id='blasttrackmergedCheckbox' name='blasttrackmergedCheckbox' onClick=mergeTrack(\"blasttrack\"); value=blasttrack >Blast Track</span>");
            trackToggle("blasttrack");


          }
          });
}