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
    ajaxurl = '/' + jQuery('#title').text() + '/' + jQuery('#title').text() + '/fluxion.ajax';
    var link = "";
    var id = randomString(8);
    var format = 19;// qseqid sseqid pident length mismatch gapopen qstart qend sstart send evalue bitscore sseq qseq";//"format";
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
            'type': type
        },
        {
            'doOnSuccess': function (json) {
                grassrootBLASTResult(json.BlastAccession, json.id)
            },
            'doOnError': function (json) {
                alert(json.error);
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
                    parseGrassRootBLAST(json.response[0]["results"][0]["data"]["blast_search_results"]["reports"][0], json.BlastAccession);
                } else if (json.response[0]["status_text"] == "Started" || json.response[0]["status_text"] == "Pending") {

                    setTimeout(function () {
                        grassrootBLASTResult(BlastAccession, id)
                    }, 5000);
                } else {
                    alert("else")
                    jQuery("#" + json.id).html("<b>BLAST job " + json.BlastAccession + "</b><br> Failed. <span onclick=deleteTable('" + json.BlastAccession + "') class=\"ui-button ui-icon ui-icon-trash\" > </span> ")
                }
            },
            'doOnError': function (json) {
                alert(json.error);
            }
        });
}

function parseGrassRootBLAST(result, id) {
    console.log(result)
    var hits = result["hits"]

    jQuery('#blastresult').fadeIn();
    jQuery('#blastresult').append("<table style=\"display: none;\" class='list' id='blasttable" + id + "'> <thead> " +
        "<tr><th> Query id </th> <th> Subject id </th>  <th> alignment length </th>  <th> mismatches </th>  <th> gap openings </th>  <th> q.start </th>  <th> q.end </th>  <th> s.start </th>  <th> s.end </th> <th> e-value </th> <th> bit score </th> <th> Subject db </th><th> Download Sequence </th>        </tr>        </thead>        <tbody>        </tbody>    </table>")

    for (var i = 0; i < hits.length; i++) {
        var hit_seq = hits[i]["hsps"][0]["hit_sequence"]
        var q_seq = hits[i]["hsps"][0]["query_sequence"]
        jQuery("#blasttable" + id + " tbody").append("<tr>" +
            "<td>" + result["query_id"] + "</td>" +
            "<td>" + hits[i]["scaffolds"][0]["scaffold"] + "</td><td>-</td>" +
            "<td>" + hits[i]["hsps"][0]["hit_sequence"].length + "</td>" +
            "<td>" + hits[i]["hsps"][0]["polymorphisms"].length + "</td>" +
            "<td>" + hits[i]["hsps"][0]["query_location"]["faldo:begin"]["faldo:position"] + "</td>" +
            "<td>" + hits[i]["hsps"][0]["query_location"]["faldo:end"]["faldo:position"] + "</td>" +
            "<td>" + hits[i]["hsps"][0]["hit_location"]["faldo:begin"]["faldo:position"] + "</td>" +
            "<td>" + hits[i]["hsps"][0]["hit_location"]["faldo:end"]["faldo:position"] + "</td>" +
            "<td>" + hits[i]["hsps"][0].evalue + "</td><td>" + hits[i]["hsps"][0].bit_score + "</td>" +
            "<td>" + result["database"]["database_name"] + "</td>" +
            "<td><div class=\"ui-widget ui-state-default ui-corner-all ui-button ui-icon ui-icon-arrow-1-s\" id=\"openmenu\" data-toggle=\"modal\" data-target=\"#controlModal\" onclick=\"sub_seq('" + hit_seq + "','" + q_seq + "')\" title=\"More Option\"> </div></td>" +
            "</tr>");
    }
    jQuery("#blasttable" + json.id).tablesorter();
    jQuery("'#blasttable" + json.id + "'").trigger("update");
}