var seqregname = null;
var track_list, minWidth;
var start_global, end_global, hit_global, blastid = 0, blastdb = "", oldTracklist;

function blastSearch(query, blastdb) {
  var link = blastdb.split(":");
  jQuery('#blastresult').html("<span style=\"position:relative; left:50%;\"> Submitting &nbsp; <img alt=\"Loading\" src=\"./images/browser/loading_big.gif\" style=\"position: relative;\"> </span> </div>");

  submitBlastTask(query, blastdb, 6);
  jQuery('#main').animate({"height": "0px"}, { duration: 300, queue: false});
  jQuery('#main').fadeOut();
}


function blastTrackSearch(query, start, end, hit, blastdb) {
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


  submitBlastTask(query, blastdb, 5, start, end, hit);
  start_global = start;
  end_global = end;
  hit_global = hit;
}

function submitBlastTask(query, db, format, start, end, hit) {
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
            format: format
          });

  ajaxurl = '/' + jQuery('#title').text() + '/' + jQuery('#title').text() + '/fluxion.ajax';

  Fluxion.doAjax(
          'blastservice',
          'submitBlastTask',
          {'url': ajaxurl, 'querystring': query, 'blastdb': db, 'location': link, 'BlastAccession': id, 'format': format},
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
  jQuery("#alertDiv").html("<img src='images/browser/loading2.gif' height='10px'> BLAST running ");
  jQuery("#alertDiv").show();
  Fluxion.doAjax(
          'blastservice',
          'checkTask',
          {'url': ajaxurl, 'taskid': task},
          {'ajaxType': 'periodical', 'updateFrequency': 5, 'doOnSuccess': function (json) {
            if (json.result == 'FAILED') {
              alert('Blast search: ' + json.result);
              jQuery("#alertDiv").hide();
              jQuery("#alertDiv").html("");

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
                          jQuery("#alertDiv").hide()
                          jQuery("#alertDiv").html("");
                        }
                        });
              }
              else if (format == 5) {
                Fluxion.doAjax(
                        'blastservice',
                        'blastSearchTrack',
                        {'start': start, 'end': end, 'hit': hit, 'accession': task, 'location': link, 'db': db, 'url': ajaxurl},
                        {'doOnSuccess': function (json) {
                          jQuery("#alertDiv").hide()
                          jQuery("#alertDiv").html("");
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

function seqregionSearch(query) {
  jQuery(window.location).attr('href', "./index.jsp?query=" + query + "&&blast=").attr("target", "_new");
}

function seqregionSearchPopup(query, from, to, blast) {

  jQuery("#searchresult").fadeOut();
  jQuery("#searchresultMap").fadeOut();
  jQuery('#sessioninput').fadeOut();
  jQuery("#sessionid").html("");
  minWidth = null;
  removeAllPopup();
  Fluxion.doAjax(
          'dnaSequenceService',
          'seqregionSearchSequence',
          {'query': query, 'url': ajaxurl},
          {'doOnSuccess': function (json) {
            if (json.html == "seqregion") {
              jQuery('#canvas').hide();
              jQuery('#currentposition').hide();
              jQuery("#searchresult").fadeIn();
              var content = "<h1>Search Results: </h1><br>";
              for (var i = 0; i < json.seqregion.length; i++) {
                if (json.seqregion[i].parent_id) {
                  content += "Seq Regions: " + json.seqregion[i].seq_region_id + ": <a target='_blank' href=\"index.jsp?query=" + json.seqregion[i].parent_name + "&&from=" + json.seqregion[i].start + "&&to=" + json.seqregion[i].end + "\" > " + json.seqregion[i].name + "</a> <br>";
                }
                else {
                  content += "Seq Regions: " + json.seqregion[i].seq_region_id + ": <a target='_blank' href=\"index.jsp??query=" + json.seqregion[i].name + "&&blast=\" > " + json.seqregion[i].name + "</a> <br>";
                }
              }
              jQuery("#searchresult").html(content)
            }
            else {
              seq = json.html;
              sequencelength = json.seqlength;
              if (!track_list) {
                track_list = json.tracklists;
              }
              jQuery('#seqnameh1').html(json.seqregname);
              jQuery('#seqname').html("<br/>");
              jQuery('#searchseqregioninput').fadeOut();
              jQuery('#canvas').show();
              jQuery('#currentposition').show();
              jQuery('#openCloseWrap').show();
              jQuery('#displayoptions').show();
              seqregname = json.seqregname;
              tracks = jQuery("#filetrack").html().split(',');
              if (tracks[0].length) {
                for (var i = 0; i < tracks.length; i++) {
                  var filename = tracks[i].substring(tracks[i].lastIndexOf("/") + 1, tracks[i].lastIndexOf("."));
                  var type = tracks[i].substring(tracks[i].lastIndexOf(".") + 1, tracks[i].length);
                  track_list.push(
                          {name: filename + "_" + type, id: tracks[i], display_label: filename, desc: "loaded Sam", disp: 1, merge: 0, graph: "false", display_lable: tracks[i], label: 0}
                  );
                }
              }
              trackList(track_list);

              minWidth = findminwidth();
              if (from && to) {
                if (parseInt(from) < parseInt(to)) {
                  setBegin(from);
                  setEnd(parseInt(to));
                }
                else {
                  setBegin(to);
                  setEnd(parseInt(from));
                }
              }
              else {
                setBegin((sequencelength - minWidth) / 2);
                setEnd(parseInt(getBegin()) + minWidth);
              }
              if (blast) {
                loadPreBlast(blast, query);
              }
              jumpToSeq();
              dispSeqCoord();
              displayCursorPosition();
              setNavPanel();
              getReferences();
              loadDefaultTrack(track_list);
              jQuery("#controlsbutton").colorbox({width: "90%", inline: true, href: "#controlpanel"});


            }
          }
          });

}

function search(query, from, to, jsonid, oldtracks) {
//  if (track_list) {
//    jQuery.cookie('trackslist', track_list.toJSON(), {  expires: 10});
//    removeTrackslist(track_list);
//  }

  jQuery('#sessioninput').fadeOut();
  jQuery("#sessionid").html("");
  minWidth = null;
  removeAllPopup();
  jQuery('#canvas').hide();
  jQuery('#tabGenes').html('');
  jQuery('#tabGO').html('');
  jQuery('#tabTranscripts').html('');
  jQuery("#searchresult").fadeIn();
  jQuery("#searchresultHead").html("<img style='position: relative; left: 50%; ' src='./images/browser/loading_big.gif' alt='Loading'>");

  Fluxion.doAjax(
          'dnaSequenceService',
          'searchSequence',
          {'query': query, 'url': ajaxurl},
          {'doOnSuccess': function (json) {
            if (json.html == "seqregion") {
              jQuery('#canvas').hide();
              jQuery('#currentposition').hide();
              jQuery("#searchresult").fadeIn();
              var seqregioncontent = "<h1>Search Results: </h1><br>";
              var content = "<h1>Search Results: </h1><br>";
              for (var i = 0; i < json.seqregion.length; i++) {
                if (i == 0) {
                  seqregioncontent += "<table class='list' id='search_hit' ><thead><tr><th>SeqRegion</th><th>SeqRegion Id</th><th>Reference Name</th><th>Link</th></tr></thead>";
                }

                seqregioncontent += "<tr><td>" + json.seqregion[i].Type + "<td> " + json.seqregion[i].seq_region_id + "<td>" + json.seqregion[i].name + " <td><a target='_blank' href='index.jsp?query=" + json.seqregion[i].name + "' > <span title=\"Link\" class=\"ui-button ui-icon ui-icon-link\" </span><a/></td>";
                if (i == json.seqregion.length - 1) {
                  seqregioncontent += "</table>";
                  jQuery("#searchresult").html(seqregioncontent);
                }

                jQuery("#search_hit").tablesorter();
              }


            }
            else if (json.html == "gene" || json.html == "GO" || json.html == "transcript") {

              jQuery('#canvas').hide();
              jQuery('#currentposition').hide();
              jQuery("#searchresult").html(" <div id=\"searchresultHead\"></div><div id=\"searchnavtabs\"><ul> <li><a href=\"#tabGenes\"><span>Genes</span></a></li>  <li><a href=\"#tabTranscripts\"><span>Transcripts</span></a></li><li><a href=\"#tabGO\"><span>GO</span></a></li> </ul> <div id=\"tabGenes\"></div> <div id=\"tabGO\"> </div>      <div id=\"tabTranscripts\"></div> </div>");
              jQuery("#searchresult").fadeIn();
              var genecontent = "";
              var content = "<h1>Search Results: </h1><br>";
              for (var i = 0; i < json.gene.length; i++) {
                if (i == 0) {
                  genecontent += "<table class='list' id='gene_hit'><thead><tr><th>Track</th><th>Gene</th><th>Reference Name</th><th>Position</th><th>Link</th></tr></thead>";
                }
                genecontent += "<tr><td>" + json.gene[i].Type + "<td> " + json.gene[i].name + "<td>" + json.gene[i].parent + "<td>" + json.gene[i].start + "-" + json.gene[i].end + "<td> <a target='_blank' href='index.jsp?query=" + json.gene[i].parent + "&&from=" + json.gene[i].start + "&&to=" + json.gene[i].end + "' > <span title=\"Link\" class=\"ui-button ui-icon ui-icon-link\" </span> </a></td>";
                if (i == json.gene.length - 1) {
                  genecontent += "</table>";
                  jQuery('#tabGenes').append(genecontent);
                }

                jQuery("#gene_hit").tablesorter();

              }
              content += "<hr>";
              var gocontent = "";
              for (var i = 0; i < json.GO.length; i++) {

                if (i == 0) {
                  gocontent += "<table class='list' id='go_hit'><thead><tr><th>Attribute Type</th><th>Gene/Transcript Name</th><th>Reference Name</th><th>Position</th><th>Link</th></tr></thead>";
                }

                gocontent += "<tr><td>" + json.GO[i].Type + "<td>" + json.GO[i].name + "<td>" + json.GO[i].parent + "<td>" + json.GO[i].start + "-" + json.GO[i].end + "</td><td> <a target='_blank' href='index.jsp?query=" + json.GO[i].parent + "&&from=" + json.GO[i].start + "&&to=" + json.GO[i].end + "' ><span title=\"Link\" class=\"ui-button ui-icon ui-icon-link\" </span> </a></tr>";

                if (i == json.GO.length - 1) {
                  gocontent += "</table>";
                  jQuery('#tabGO').append(gocontent);
                }

                jQuery("#go_hit").tablesorter();

              }


              content += "<hr>";
              var transcriptcontent = "";
              for (var i = 0; i < json.transcript.length; i++) {
                if (i == 0) {
                  transcriptcontent += "<table class='list' id='transcript_hit'><thead><tr><th>Attribute Type</th><th>Transcript Name</th><th>Reference Name</th><th>Position</th><th>Link</th></tr></thead>";
                }
                transcriptcontent += "<tr><td> " + json.transcript[i].Type + "<td>" + json.transcript[i].name + "<td> " + json.transcript[i].parent + "<td>" + json.transcript[i].start + "-" + json.transcript[i].end + "<td><a target='_blank' href='index.jsp?query=" + json.transcript[i].parent + "&&from=" + json.transcript[i].start + "&&to=" + json.transcript[i].end + "' ><span title=\"Link\" class=\"ui-button ui-icon ui-icon-link\" </span> </a></td></tr>";

                if (i == json.transcript.length - 1) {
                  transcriptcontent += "</table>";
                  jQuery('#tabTranscripts').append(transcriptcontent);
                }
              }

              jQuery("#transcript_hit").tablesorter();
              jQuery("#searchnavtabs").tabs();
              jQuery("#searchresultHead").html("<h2>Search Result</h2>");
            }
            else {
              window.location.replace("index.jsp?query=" + json.seqregname);
            }
          }
          });
}

function generateFileLink(data) {
  var filelink;
  Fluxion.doAjax(
          'fileService',
          'exportFile',
          {'filecontent': data, 'url': ajaxurl, 'location': path},
          {'doOnSuccess': function (json) {
            filelink = json.link;
            jQuery(window.location).attr('href', filelink).attr("target", "_blank");
          }
          });
}

function loadTrackAjax(trackId, trackname) {
//  jQuery.cookie('trackslist', track_list.toJSON());
  mergeTrackList(trackname);
  var query = jQuery('#search').val();
  jQuery(track_list).each(function (index) {
    //this is the object in the array, index is the index of the object in the array
    if (jQuery("#" + track_list[index].name + "Checkbox").attr('checked')) {//
      this.disp = 1;
      jQuery("#unSelectAllCheckbox").attr('checked', false)
    }
    else {
      this.disp = 0;
      jQuery("#selectAllCheckbox").attr('checked', false)
    }
  });

  if (window[trackname] || window[trackname] == "running" || window[trackname] == "loading") {
    trackToggle(trackname);
//    need to think abt it
  }

  if (jQuery("#" + trackname + "Checkbox").attr('checked')) {
    var partial = (getEnd() - getBegin()) + ((getEnd() - getBegin()) / 2);
    var start = (getBegin() - partial);
    var end = parseInt(getEnd()) + parseFloat(partial);
    if (start < 0) {
      start = 0;
    }
    if (end > sequencelength) {
      end = sequencelength;
    }
    deltaWidth = parseInt(end - start) * 2 / parseInt(maxLen);
    window[trackname] == "loading";
    trackToggle(trackname);
    Fluxion.doAjax(
            'dnaSequenceService',
            'loadTrack',
            {'query': seqregname, 'name': trackname, 'trackid': trackId, 'start': start, 'end': end, 'delta': deltaWidth, 'url': ajaxurl},
            {'doOnSuccess': function (json) {
              var trackname = json.name;

              if (json.type == "graph") {
                for (var j = 0; j < track_list.length; j++) {
                  if (track_list[j].name == trackname) {
                    track_list[j].graph = "true";
                  }
                }
              }
              else {
                for (var j = 0; j < track_list.length; j++) {
                  if (track_list[j].name == trackname) {
                    track_list[j].graph = "false";
                  }
                }
              }
              window[trackname] = json[trackname];
              trackToggle(trackname);
            }
            });
  }
}

function metaData() {
  ajaxurl = '/' + jQuery('#title').text() + '/' + jQuery('#title').text() + '/fluxion.ajax';
//  ajaxurl = "/" +  jQuery('#title').text()    + '/fluxion.ajax';
  Fluxion.doAjax(
          'dnaSequenceService',
          'metaInfo',
          {'url': ajaxurl},
          {'doOnSuccess': function (json) {
            jQuery("#dbinfo").html("Species Name: <i>" + json.metainfo[0].name + "</i> Database Version: " + json.metainfo[0].version);
          }
          });
}

function saveSession() {
  var tracks = getTracks();
  var blast = jQuery("#alertDiv").text().contains("BLAST");
  Fluxion.doAjax(
          'fileService',
          'saveFile',
          {'location': path, 'reference': seqregname, 'session': randomnumber, 'from': getBegin(), 'to': getEnd(), 'seq': seq, 'seqlen': sequencelength, 'track': track_list, 'tracks': tracks, 'filename': (randomnumber), 'blast': blast, 'url': ajaxurl},
          {'doOnSuccess': function (json) {
            jQuery("#export").html("<a href=" + json.link + " target = '_blank'>Export</a>");
            jQuery("#export").css('background', "");
          }
          });
}

function loadSession(query) {
  Fluxion.doAjax(
          'fileService',
          'loadSession',
          {'location': path, 'query': query, 'url': ajaxurl},
          {'doOnSuccess': function (json) {
            var now = new Date();
            seq = json.seq;
            sequencelength = json.seqlen;
            track_list = json.tracklist;
            randomnumber = json.session;
            jQuery("#sessionid").html("<b>Session Id: </b><a  href='./session.jsp?query=" + randomnumber + "' target='_blank'>" + randomnumber + "</a> Saved at " + now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds());
            jQuery('#seqnameh1').html('<a href="/"+path+"/">' + json.reference + '</a>');
            jQuery('#seqname').html("<br/>");
            jQuery('#canvas').show();
            jQuery('#displayoptions').show();
            jQuery('#sessioninput').fadeOut();
            seqregname = json.reference;
            trackList(track_list);
            minWidth = findminwidth();
            setBegin(json.from);
            setEnd(json.to)
            jumpToSeq();
            dispSeqCoord();
            displayCursorPosition();
            setNavPanel();
            checkSession();
            getReferences();
            reloadTracks(json.tracks, track_list, json.blast);
            jQuery("#controlsbutton").colorbox({width: "90%", inline: true, href: "#controlpanel"});
          }
          });
}

function loadSeq(query, from, to) {
  Fluxion.doAjax(
          'dnaSequenceService',
          'loadSequence',
          {'query': query, 'from': getBegin(), 'to': getEnd(), 'url': ajaxurl},
          {'doOnSuccess': function (json) {
            seq = json.seq;
            return json.seq;
          }
          });
}

function reloadTracks(tracks, tracklist, blast) {
  for (var i = 0; i < tracklist.length; i++) {
    if (tracklist[i].disp == "1") {
      for (var j = 0; j < tracks.length; j++) {
        if (tracklist[i].id == tracks[j].trackId) {
          window[tracklist[i].name] = tracks[j].child;
          jQuery('#' + tracklist[i].name + 'Checkbox').attr('checked', true);
          mergeTrackList(tracklist[i].name);
          if (tracklist[i].merge == "1") {
            jQuery('input[name=' + tracklist[i].name + 'mergedCheckbox]').attr('checked', true);
          }
          trackToggle(tracklist[i].name);
        }

      }
    }
  }
  if (blast == "true") {
    for (var j = 0; j < tracks.length; j++) {
      if (tracks[j].trackId == "running") {
        if (!window['blasttrack']) {
          window['blasttrack'] = "running";
          jQuery("#blasttrack_div").html("<img style='position: relative; left: 50%; ' src='./images/browser/loading_big.gif' alt='Loading'>")
          jQuery("#blasttrack_wrapper").fadeIn();
          jQuery('input[name=blasttrack-0]').attr('checked', true);
        }
        var blasts = tracks[j].child;
        jQuery.each(blasts, function (index) {
          checkTask(blasts[index].id, blasts[index].db, blasts[index].format, blasts[index].start, blasts[index].end, blasts[index].hit, blasts[index].link);
        });
        jQuery('input[name=blasttrackCheckbox]').attr('checked', true);
        trackToggle('blasttrack');
      }
    }
  }
}


function randomString(length) {
  var chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz'.split('');

  if (!length) {
    length = Math.floor(Math.random() * chars.length);
  }

  var str = '';
  for (var i = 0; i < length; i++) {
    str += chars[Math.floor(Math.random() * chars.length)];
  }
  return str;
}

function fastaFile(seq, start, end) {
  var fastaseq = seq;
  Fluxion.doAjax(
          'fileService',
          'saveFasta',
          {'sequence': fastaseq, 'reference': seqregname, 'from': start, 'to': end, 'url': ajaxurl, 'location': path},
          {'doOnSuccess': function (json) {
            jQuery("#fastadownload").html("<a href=" + json.link + " target = '_blank'>Download</a>");
          }
          });

}

function loadPreBlast(jsonid, refid) {

  var refseq = refid;
  if (window['blasttrack']) {
    delete window['blasttrack'];
    delete track_list.splice(track_list.length, 1);
    jQuery("#blasttrack_div").remove();
    jQuery("#blastcheck").remove();
    jQuery("#blastcheckmerge").remove();
  }
  jQuery("#tracklist").append("<p title='blast' id=blastcheck><input type=\"checkbox\" checked id='blasttrackCheckbox' name='blasttrackCheckbox' onClick=loadTrackAjax(\"blasttrack\",\"blasttrack\");\> Blasttrack\ </p>");
  jQuery("#tracks").append("<div id='blasttrack_div' class='feature_tracks'> Blast Track </div>");
  jQuery("#blasttrack_div").html(" <img style='position: relative; left: 50%; ' src='./images/browser/loading_big.gif' alt='Loading'>")
  jQuery("#blasttrack_div").fadeIn();
  Fluxion.doAjax(
          'blastservice',
          'blastEntry',
          {'accession': jsonid, 'seqregion': refid, 'url': ajaxurl},
          {'doOnSuccess': function (json) {
            track_list.push(
                    {name: "blasttrack", id: 0, desc: "blast from browser", disp: 1, merge: 0}
            );

            window['blasttrack'] = json.entries;//(decodeURIComponent(json.blast.replace(/\s+/g, ""))).replace(/>/g, "");
            jQuery('input[name=blasttrackCheckbox]').attr('checked', true);
            jQuery("#mergetracklist").append("<span id=blastcheckmerge> <input type=\"checkbox\" id='blasttrackmergedCheckbox' name='blasttrackmergedCheckbox' onClick=mergeTrack(\"blasttrack\"); value=blasttrack >Blast Track</span>");
            trackToggle("blasttrack");

          }
          });
}

function fileUploadProgress(formname, divname, successfunc) {
  var self = this;
  Fluxion.doAjaxUpload(
          formname,
          'fileUploadProgressBean',
          'checkUploadStatus',
          {'url': ajaxurl},
          {'statusElement': divname, 'progressElement': 'trash', 'doOnSuccess': successfunc},
          {'validationBeanId': 'fileValidationBean'}
  );
}

function processingOverlay() {
  jQuery.colorbox({width: "30%", html: "Processing..."});
}

function fileUploadSuccess() {
  alert("upload done");
}

function getReferences(show) {
  Fluxion.doAjax(
          'dnaSequenceService',
          'searchSeqRegionforMap',
          {'url': ajaxurl},
          {'doOnSuccess': function (json) {
            var max = Math.max.apply(Math, json.seqregion.map(function (o) {
              return o.length;
            }));

            var referenceLength = json.seqregion.length;
            if (!maxLen) {
              maxLen = jQuery(window).width();
            }
            var width = 25;
            var distance = (parseInt(maxLen) - (width * referenceLength)) / (referenceLength + 1);
            jQuery("#mapmarker").animate({"width": width}, 100);
            while (referenceLength--) {
              var left = parseInt(referenceLength * (width)) + parseInt(distance * referenceLength) + parseInt(distance);
              var height = (json.seqregion[referenceLength].length * 150 / max);
              var length = json.seqregion[referenceLength].length;
              if (seqregname == json.seqregion[referenceLength].name) {
                refheight = height;
              }
              var top = parseInt(jQuery("#map").css('height')) - height - 25;
              if (seqregname == json.seqregion[referenceLength].name) {
                jQuery("#refmap").append("<div onclick='jumpToHere(event);' class='refmap' id='" + json.seqregion[referenceLength].name + "' style='top:" + top + "px; left: " + left + "px; width:" + width + "px; height:" + height + "px;'></div>");
              }
              else {
                jQuery("#refmap").append("<div onclick='jumpToOther(event, " + length + ",\"" + json.seqregion[referenceLength].name + "\");' class='refmap' id='" + json.seqregion[referenceLength].name + "' style='top:" + top + "px; left: " + left + "px; width:" + width + "px; height:" + height + "px;'></div>");
              }
              jQuery("#refmap").append("<div style='position:absolute; bottom: 0px; left: " + (left - 5) + "px; '>" + stringTrim(json.seqregion[referenceLength].name, width) + "</div>");
            }
            if (show) {

              jQuery("#searchresultMap").html("<center><h1>References</h1><br>Click to jump to reference</center>" + jQuery("#refmap").html());
            }
            setMapMarkerLeft();
            setMapMarkerTop(getBegin());
            setMapMarkerHeight(getEnd() - getBegin())

          }
          });
}
