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


function getParams(type) {
    ajaxurl = '/' + jQuery('#title').text() + '/' + jQuery('#title').text() + '/fluxion.ajax';
    Fluxion.doAjax(
        'blastGrassroot',
        'getParams',
        {
            'url': ajaxurl,
            'type': type
        },
        {
            'doOnSuccess': function (json) {
                console.log(json)
            },
            'doOnError': function (json) {
                alert(json.error);
            }
        });
}


function blastSearch(query, db, type, params) {
    ajaxurl = '/' + jQuery('#title').text() + '/' + jQuery('#title').text() + '/fluxion.ajax';
    var link = "";
    var id = randomString(8);
    var format = 7;// qseqid sseqid pident length mismatch gapopen qstart qend sstart send evalue bitscore sseq qseq";//"format";
    jQuery("#blast_list").append("<div id='" + id + "' class='blast_list_node'> <b>BLAST job " + id + " </b> <img style='position: relative;' src='./images/browser/loading_big.gif' height=15px alt='Loading'></div>")

    Fluxion.doAjax(
        'blastGrassroot',
        'BlastSearchSequence',
        {
            'url': ajaxurl,
            'querystring': query,
            'blastdb': db,
            'location': link,
            'BlastAccession': id,
            'format': format,
            'type': type,
            'params': params
        },
        {
            'doOnSuccess': function (json) {
                grassrootBLASTResult(json.BlastAccession, json.id)
            },
            'doOnError': function (json) {
                alert(json.error);
                jQuery("#" + id).html("<b>BLAST job " + id + "</b><br> Failed.> </span> ")
            }
        });
}


function grassrootBLASTResult(BlastAccession, id) {
    ajaxurl = '/' + jQuery('#title').text() + '/' + jQuery('#title').text() + '/fluxion.ajax';

    var format = "format";
    Fluxion.doAjax(
        'blastGrassroot',
        'BlastGetResult',
        {'url': ajaxurl, 'BlastAccession': BlastAccession, 'id': id, 'format': format},
        {
            'doOnSuccess': function (json) {
                jQuery("#notifier").hide()
                jQuery("#notifier").html("");

                if (json.response[0]["status_text"] == "Succeeded") {
                    jQuery("#" + json.BlastAccession).html("BLAST job " + json.BlastAccession + " <span title=\"Finished\" class=\"ui-button ui-icon ui-icon-check\"></span> <br>  <span onclick=toogleTable('" + json.BlastAccession + "') class=\"ui-button ui-icon ui-icon-zoomin\" > </span> <span onclick=deleteTable('" + json.BlastAccession + "') class=\"ui-button ui-icon ui-icon-trash\" > </span> ")
                    jQuery('#main').animate({"height": "0px"}, {duration: 300, queue: false});
                    jQuery('#main').fadeOut();
                    if(json.response[0]["results"][0]["data"].indexOf("0 hits found") > 0){
                        jQuery("#" + json.BlastAccession).html("<b>BLAST job " + json.BlastAccession + "</b><br> No hits found. <span style=\"float: right; position: relative;\" onclick=deleteTable('" + json.id + "') class=\"ui-button ui-icon ui-icon-trash\" > </span> ")
                    }else{
                        parseGrassRootBLAST(json.response[0]["results"][0]["data"], json.BlastAccession);
                    }
                } else if (json.response[0]["status_text"] == "Started" || json.response[0]["status_text"] == "Pending") {

                    setTimeout(function () {
                        grassrootBLASTResult(BlastAccession, id)
                    }, 5000);
                } else {
                    var msg = json.response[0]["errors"]["runtime_errors"]["errors"][0];

                    jQuery("#" + json.BlastAccession).html("<b>BLAST job " + json.BlastAccession + "</b><br> Failed")

                    var errmsg = document.createElement('span');
                    errmsg.className = "ui-button ui-icon ui-icon-info";
                    errmsg.onclick = (function () {
                        return function () {
                            alert(msg);
                        }
                    })();
                    jQuery("#" + json.BlastAccession).append(errmsg)

                    var delEntry = document.createElement('span');
                    delEntry.className = "ui-button ui-icon ui-icon-trash";
                    delEntry.onclick = (function () {
                        return function () {
                            deleteTable(json.BlastAccession)
                        }
                    })();
                    jQuery("#" + json.BlastAccession).append(delEntry)
                }
            },
            'doOnError': function (json) {
                alert(json.error);
            }
        });
}

function parseGrassRootBLAST(result, id) {
    var hits = result.split("\n")

    if(hits.length() > 1){
        jQuery('#blastresult').fadeIn();
        jQuery('#blastresult').append("<table style=\"display: none;\" class='list' id='blasttable" + id + "'> <thead></thead><tbody></tbody></table>")
        //      "<tr><th> Query id </th> <th> Subject id </th>  <th> alignment length </th>  <th> mismatches </th>  <th> gap openings </th>  <th> q.start </th>  <th> q.end </th>  <th> s.start </th>  <th> s.end </th> <th> e-value </th> <th> bit score </th> <th> Subject db </th><th> Download Sequence </th>        </tr>        </thead>        <tbody>        </tbody>    </table>")

        for (var i = 0; i < hits.length; i++) {
            if (hits[i].indexOf("#") == 0) {
                if (hits[i].indexOf("Fields") > 0) {
                    var row = hits[i].replace("# Fields: ", "");
                    jQuery("#blasttable" + id + " thead").append("<tr><th>" + row.replaceAll(",", "<th>"))
                }
            } else {
                var cols = hits[i].split("\t");
                var col_html = "<tr>"
                for (var col = 0; col < cols.length; col++) {
                    if (col == 1) {
                        col_html += "<td><a href='../tgac_browser/index.jsp?query=" + cols[col] + "&&from=" + cols[8] + "&&to=" + cols[9] + "'>" + cols[col] + "</a></td>";
                    } else {
                        col_html += "<td>" + cols[col] + "</td>";
                    }
                }
                jQuery("#blasttable" + id + " tbody").append(col_html)
            }
        }
    } else {
        jQuery("#" + id).html("<b>BLAST job " + json.id + "</b><br> No hits found. <span style=\"float: right; position: relative;\" onclick=deleteTable('" + json.id + "') class=\"ui-button ui-icon ui-icon-trash\" > </span> ")
    }

    jQuery("#blasttable" + json.id).tablesorter();
    jQuery("'#blasttable" + json.id + "'").trigger("update");
}


function blastTrackSearch(query, start, end) {
    jQuery("#notifier").html("<img src='images/browser/loading2.gif' height='10px'> BLAST running ");
    jQuery("#notifier").show();
    ajaxurl = '/' + jQuery('#title').text() + '/' + jQuery('#title').text() + '/fluxion.ajax';
    var link = path;
    var id = randomString(8);
    var format = 7;//"format";
    if (!window['blasttrack']) {

        setBLASTTrack()

    }

    Fluxion.doAjax(
        'blastGrassroot',
        'BlastSearchSequence',
        {
            'url': ajaxurl,
            'querystring': query,
            'blastdb': 'db',
            'type': 'blastn',
            'location': link,
            'BlastAccession': id,
            'format': format
        },
        {
            'doOnSuccess': function (json) {
                // blastsdata.push(
                //     {
                //         id: json.html,
                //         start: start,
                //         end: end,
                //         db: db,
                //         hit: hit,
                //         link: link,
                //         format: 5,
                //         type: type
                //     });
                console.log("hererere")
                grassrootBLASTResultTrack(json.BlastAccession, json.id, start)

            },
            'doOnError': function (json) {
                alert(json.error);
            }
        });
}

function grassrootBLASTResultTrack(BlastAccession, id, start) {
    console.log("grassrootBLASTResultTrack")
    ajaxurl = '/' + jQuery('#title').text() + '/' + jQuery('#title').text() + '/fluxion.ajax';

    var format = "format";
    Fluxion.doAjax(
        'blastGrassroot',
        'BlastGetResult',
        {'url': ajaxurl, 'BlastAccession': BlastAccession, 'id': id, 'format': format},
        {
            'doOnSuccess': function (json) {
                jQuery("#notifier").hide()
                jQuery("#notifier").html("");

                if (json.response[0]["status_text"] == "Succeeded") {
                    parseGrassRootBLASTTrack(json.response[0]["results"][0]["data"], json.BlastAccession, start);
                } else if (json.response[0]["status_text"] == "Started" || json.response[0]["status_text"] == "Pending") {

                    setTimeout(function () {
                        grassrootBLASTResultTrack(BlastAccession, id, start)
                    }, 5000);
                } else {
                    alert("else")
                }
            },
            'doOnError': function (json) {
                alert(json.error);
            }
        });
}

function parseGrassRootBLASTTrack(result, id, start) {
    var hits = result.split("\n")
    var blast = [];
    for (var i = 0; i < hits.length; i++) {
        console.log(hits[i])
        if (hits[i].indexOf("#") == 0) {
            if (hits[i].indexOf("Fields") > 0) {
                var row = hits[i].replace("# Fields: ", "");
            }
        } else {
            var row = hits[i].split("\t");
            var each_track = {};
            each_track['start'] = parseInt(start) + parseInt(row[6]);
            each_track['end'] = parseInt(start) + parseInt(row[7]);
            each_track['desc'] = row[1] + ":" + row[8] + "-" + row[9];
            each_track['score'] = row[11];
            blast.push(each_track);
        }
        if (blast.length > 20) {
            break;
        }
    }

    if (!window['blasttrack']) {
        window['blasttrack'] = "running";
    }
    if (window['blasttrack'] == "running") {
        window['blasttrack'] = blast;
    } else {
        jQuery.merge(window['blasttrack'], blast);
    }
    jQuery('input[name=blasttrackCheckbox]').attr('checked', true);
    trackToggle("blasttrack");


    jQuery("#blasttable" + json.id).tablesorter();
    jQuery("'#blasttable" + json.id + "'").trigger("update");
}
