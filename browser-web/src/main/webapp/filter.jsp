<%@ include file="header.jsp" %>

<style type="text/css">
    .css_def {
        position: absolute;
    }

</style>

<center>

    <center>

        <div id="query_filter" style="top: 30px; position: relative; height: 1000px;">
            <div style="position: absolute; font-weight: bold; width: 120px;">
                SNPs
                <div style="left: 10px;  position: absolute; width: 100px;" id="origin" class="fbox ui-droppable">

                </div>
            </div>
            <div style="position: absolute; font-weight: bold; height: 500px; width: 100px; left: 130px; border: 1px solid black;">
                Group A
                <div id="drop1" class="fbox ui-droppable ui-sortable" style="height: 500px;">

                </div>
            </div>
            <div style="position: absolute; font-weight: bold; height: 500px; width: 100px; left: 240px; border: 1px solid black; font-weight: bold">
                Group B
                <div id="drop2" class="fbox ui-droppable ui-sortable" style="height:500px">

                </div>
            </div>

            <div style="position:absolute;left:350px">
                <button onclick="getSelectedSNPs()">Search</button>
            </div>
        </div>

        <div style="top: 50px; position: absolute; width: 1000px; left: 500px; background: white none repeat scroll 0% 0%; box-shadow: 0px 0px 15px 5px lightgray; padding: 20px;"
             id="query_result">

            <div id="venn">

            </div>

            <div id="selected_results">

            </div>


    </center>

    <script type="text/javascript">


        jQuery(document).ready(function () {
            var chr = metaData();
            listSNPs();
        });

        var common = [];


        function listSNPs() {
            Fluxion.doAjax(
                    'dnaSequenceService',
                    'listSNPs',
                    {'url': ajaxurl},
                    {
                        'doOnSuccess': function (json) {

                            var list = json.list;
                            var html_string_A = ""
                            var gallery = jQuery("#gallery"),
                                    trash = jQuery("#trash");


                            for (var i = 0; i < list.length; i++) {
                                html_string_A += " <li analysis_id=\"" + list[i].analysis_id + "\" class=\"draggable\">" + list[i].display_label + "  </li>"

                            }

                            jQuery("#origin").html(html_string_A)


                            jQuery(".draggable").draggable({cursor: "crosshair", revert: "invalid"});
                            jQuery("#drop1").droppable({
                                accept: ".draggable",
                                drop: function (event, ui) {
                                    jQuery(this).removeClass("border").removeClass("over");
                                    var dropped = ui.draggable;
                                    var droppedOn = $(this);
                                    jQuery(dropped).detach().css({top: 0, left: 0}).appendTo(droppedOn);


                                },
                                over: function (event, elem) {
                                    jQuery(this).addClass("over");
                                }
                                ,
                                out: function (event, elem) {
                                    jQuery(this).removeClass("over");
                                }
                            });
                            jQuery("#drop1").sortable();


                            jQuery("#drop2").droppable({
                                accept: ".draggable",
                                drop: function (event, ui) {
                                    jQuery(this).removeClass("border").removeClass("over");
                                    var dropped = ui.draggable;
                                    var droppedOn = $(this);
                                    jQuery(dropped).detach().css({top: 0, left: 0}).appendTo(droppedOn);


                                },
                                over: function (event, elem) {
                                    jQuery(this).addClass("over");
                                }
                                ,
                                out: function (event, elem) {
                                    jQuery(this).removeClass("over");
                                }
                            });
                            jQuery("#drop2").sortable();


                            jQuery("#origin").droppable({
                                accept: ".draggable", drop: function (event, ui) {
                                    jQuery(this).removeClass("border").removeClass("over");
                                    var dropped = ui.draggable;
                                    var droppedOn = $(this);
                                    jQuery(dropped).detach().css({top: 0, left: 0}).appendTo(droppedOn);


                                }
                            });

//                        jQuery("#group_B").html(html_string_B)
                        }
                    });
        }

        function changeFilter(group, id) {
            if (group.indexOf("A_") >= 0) {
                jQuery("#B_" + id).attr("disabled", true)
            } else {
                jQuery("#A_" + id).attr("disabled", true)
            }
        }

        function getSelectedSNPs() {
            var list_a = []
            var list_b = [];

            jQuery('#drop1 li').each(function () {
                list_a.push(jQuery(this).attr('analysis_id'));
            });

            jQuery('#drop2 li').each(function () {
                list_b.push(jQuery(this).attr('analysis_id'));
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

                            jQuery("#venn").html("")
                            jQuery("#selected_results").html("")

                            var sets = [{sets: ['GroupA-'+json.group_A.size()], size: json.group_A.size()},
                                {sets: ['GroupB-'+json.group_B.size()], size: json.group_B.size()},
                                {sets: ['GroupA-'+json.group_A.size(), 'GroupB-'+json.group_B.size()], size: findIntersect(json.group_A, json.group_B)}];


                            var chart = venn.VennDiagram();
                            var div = d3.select("#venn");
                            div.datum(sets).call(chart);

                            d3.selectAll("#venn .venn-circle")
                                    .on("mouseover", function (d, i) {
                                        // sort all the areas relative to the current item
                                        venn.sortAreas(div, d);

                                        // highlight the current path
                                        var selection = d3.select(this);
                                        selection.select("path")
                                                .style("stroke-width", 3)
                                                .style("fill-opacity", d.sets.length == 1 ? .4 : .1)
                                                .style("stroke-opacity", 1);
                                    })
                                    .on("mouseout", function (d, i) {
//                                        tooltip.transition().duration(400).style("opacity", 0);
                                        var selection = d3.select(this);
                                        selection.select("path")
                                                .style("stroke-width", 0)
                                                .style("fill-opacity", d.sets.length == 1 ? .25 : .0)
                                                .style("stroke-opacity", 0);
                                    })
                                    .on("click", function (d, i) {
                                        if (d.size == json.group_A.size()) {
                                            showResult(json.group_A)
                                        }
                                        else if (d.size == json.group_B.size()) {
                                            showResult(json.group_B)

                                        }
                                    });

                            d3.selectAll("#venn .venn-intersection")
                                    .on("mouseover", function (d, i) {
                                        // sort all the areas relative to the current item
                                        venn.sortAreas(div, d);

                                        // highlight the current path
                                        var selection = d3.select(this);
                                        selection.select("path")
                                                .style("stroke-width", 3)
                                                .style("fill-opacity", d.sets.length == 1 ? .4 : .1)
                                                .style("stroke-opacity", 1);
                                    })
                                    .on("mouseout", function (d, i) {
//                                        tooltip.transition().duration(400).style("opacity", 0);
                                        var selection = d3.select(this);
                                        selection.select("path")
                                                .style("stroke-width", 0)
                                                .style("fill-opacity", d.sets.length == 1 ? .25 : .0)
                                                .style("stroke-opacity", 0);
                                    })
                                    .on("click", function (d, i) {
                                        if(d.size == common.size()){
                                            showResult(common)
                                        }
                                    });

                        }
                    });
        }

        function findIntersect(userArray, origArray){
            function groupByTypeID(arr) {
                var groupBy = {};
                jQuery.each(arr, function () {
//                    var currentCount = groupBy[this.seq_region_id+"-"+this.seq_region_start] || 0;
                    groupBy[this.seq_region_id+"-"+this.seq_region_start] = this;
                });
                return groupBy;
            }


            var userArrayGroups = groupByTypeID(userArray);
            var origArrayGroups = groupByTypeID(origArray);


            var count = 0
            for (var prop in userArrayGroups) {
                if(origArrayGroups[prop]){
                    common.push(userArrayGroups[prop]);//+origArrayGroups[prop];
                    count++;
                }

            }

            return count
        }



        function showResult(json){
            var html = "<table id='search_result' class='display' border='1' width='300px'>" +
                    "<thead><tr>" +
                    "<th>ID" +
                    "<th>Ref CDS" +
                    "<th>Position" +
                    "<th>Type" +
                    "</thead><tbody>"
            for(var i=0; i<json.size(); i++){
                html+= "<tr><td>"+json[i].dna_align_feature_id+"<td>"+json[i].name+"<td>"+json[i].seq_region_start+"<td>"+json[i].display_label+"</tr>"
            }


            jQuery("#selected_results").html(html)

            jQuery(document).ready(function() {
                jQuery('#search_result').DataTable();
            } );
        }
    </script>


    <%@ include file="footer.jsp" %>

