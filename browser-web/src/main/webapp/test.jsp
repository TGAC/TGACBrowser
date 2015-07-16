<%@ include file="header.jsp" %>

<style type="text/css">
    .css_def {
        position: absolute;
    }

</style>

<%--<div id="searchseqregioninput">--%>
<%--<input type="text" id="search" value="scaffold1.1-size1749886"/>--%>
<%--<button class="ui-state-default ui-corner-all" onclick="search(jQuery('#search').val());">Search Seq Region Name--%>
<%--</button>--%>
<%--</div>--%>


<center>

    <div id="query_filter" style="top : 30px ;position: relative">

        <button onclick="getSelectedSNPs()">Search</button>
    </div>

    <div id="query_result"
         style="padding: 20px; top: 50px; position: relative; box-shadow: 0px 0px 15px 5px gray; background: none repeat scroll 0% 0% lightgray; width: 90%;">
        <table width="100%">
            <tbody>
            <tr>
                <td width="10%">
                    <div id="group_A">
                    </div>
                </td>


                <td valign="top">
                    <center>
                        <div> Result
                            <div id="venn">

                            </div>
                            <div id="selected_results">

                            </div>
                            <%--<div style="background: none repeat scroll 0% 0% red; opacity: 0.5; height: 200px; width: 200px; border-radius: 100px; position: absolute; left: 40%;"> Group A</div>--%>
                            <%--<div style="background: none repeat scroll 0% 0% blue; opacity: 0.5; height: 200px; width: 200px; border-radius: 100px; position: absolute; left: 50%;"> Group A</div>--%>

                        </div>
                    </center>
                </td>
                <td width="10%">
                    <div id="group_B">
                    </div>
                </td>
            </tr>
            </tbody>
        </table>
    </div>

</center>

<script type="text/javascript">


    jQuery(document).ready(function () {
        var chr = metaData();
        listSNPs();
//        jQuery("#mainsearch").load("browser.jsp", function () {
//            onLoad();
//            getUrlVariables(chr);
//        });
    });

    //    function getUrlVars() {
    //        var vars = {};
    //        var parts = window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi, function (m, key, value) {
    //            vars[key] = value;
    //        });
    //        return vars;
    //    }
    //    function getUrlVariables(chr) {
    //
    //        jQuery.urlParam = function (name) {
    //            var results = new RegExp('[\\?&]' + name + '=([^&#]*)').exec(window.location.href);
    //            if (results == null) {
    //                return null;
    //            }
    //            else {
    //                return results[1] || 0;
    //            }
    //        }
    //
    //        processURL(jQuery.urlParam)
    //
    //    }

    function listSNPs() {
        Fluxion.doAjax(
                'dnaSequenceService',
                'listSNPs',
                {'url': ajaxurl},
                {
                    'doOnSuccess': function (json) {

                        var list = json.list;
                        var html_string_A = ""
                        var html_string_B = ""

                        for (var i = 0; i < list.length; i++) {
                            html_string_A += "<input type='checkbox' name='A_" + list[i].analysis_id + "' id='A_" + list[i].analysis_id + "' onClick=changeFilter('A_" + list[i].analysis_id + "','" + list[i].analysis_id + "')>" + list[i].display_label + "<br>"
                            html_string_B += "<input type='checkbox' name='B_" + list[i].analysis_id + "'id='B_" + list[i].analysis_id + "' onClick=changeFilter('B_" + list[i].analysis_id + "','" + list[i].analysis_id + "')>" + list[i].display_label + "<br>"
                        }

                        jQuery("#group_A").html(html_string_A)
                        jQuery("#group_B").html(html_string_B)
                    }
                });
    }

    function changeFilter(group, id) {
        console.log("change filter")
        if (group.indexOf("A_") >= 0) {
            console.log("if " + group + " " + id)
            jQuery("#B_" + id).attr("disabled", true)
        } else {
            console.log("else " + group + " " + id)
            jQuery("#A_" + id).attr("disabled", true)
        }
    }

    function getSelectedSNPs() {
        var list_a = []
        var list_b = [];

        jQuery('#group_A input:checked').each(function () {
            list_a.push(jQuery(this).attr('name').replace("A_", ""));
        });

        jQuery('#group_B input:checked').each(function () {
            list_b.push(jQuery(this).attr('name').replace("B_", ""));
        });

        list_a = list_a.join(",");
        list_b = list_b.join(",")

        Fluxion.doAjax(
                'dnaSequenceService',
                'getGroupedSNPs',
                {'group_a': list_a, 'group_b': list_b, 'url': ajaxurl},
                {
                    'doOnSuccess': function (json) {

                        var group_A = []



                        var sets = [{sets: ['A'], size: json.group_A},
                            {sets: ['B'], size: json.group_B},
                            {sets: ['A', 'B'], size: json.unique}];

                        var chart = venn.VennDiagram();
                        d3.select("#venn").datum(sets).call(chart);

                        d3.selectAll("#rings .venn-circle")
                                .on("mouseover", function(d, i) {
                                    var node = d3.select(this).transition();
//                                    node.select("path").style("fill-opacity", .2);
                                    node.select("text").style("font-weight", "100")
                                            .style("font-size", "36px");
                                })
                                .on("mouseout", function(d, i) {
                                    var node = d3.select(this).transition();
//                                    node.select("path").style("fill-opacity", 0);
                                    node.select("text").style("font-weight", "100")
                                            .style("font-size", "24px");
                                })
                                .on("click", function(d, i) {
                                    console.log(d)
                                    if(d.size == json.group_A.size()){
                                      jQuery("#selected_results").html("A")
                                    }
                                    else if(d.size == json.unique.size()){
                                        jQuery("#selected_results").html("unique")
                                    }
                                    else{
                                        jQuery("#selected_results").html("b")

                                    }
                                });

                    }
                });
    }

</script>


<%@ include file="footer.jsp" %>

