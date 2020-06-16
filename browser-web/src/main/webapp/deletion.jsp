<%@ include file="header.jsp" %>

<style type="text/css">
    .css_def {
        position: absolute;
    }

</style>

<center>

    <center>

        <div id="chr_map" style="top: 30px; position: relative; height: 1000px;">

        </div>

        <div id="lines_list" style="position:absolute; right:10px; top:50px; width:300px; height: 500px;">

        </div>


    </center>

    <script type="text/javascript">


        var loaded = false;
        var common = [];

        function listMiscFeatures() {
            Fluxion.doAjax(
                'dnaSequenceService',
                'initMiscFeature',
                {'url': ajaxurl},
                {
                    'doOnSuccess': function (json) {

                        //               loadDeletions("All");

                        var chr = json.chromosomes;
                        var referenceLength = json.chromosomes.length;
                        var maximumLengthname, maximumsequencelength;

                        var max = Math.max.apply(Math, json.chromosomes.map(function (o) {
                            return o.length;
                        }));
                        var maxWidth = 1000
                        var maxLen = jQuery(window).height();

                        var height = 20;
                        var distance = 10;//(parseInt(maxLen) - (height * referenceLength)) / (referenceLength + 1);
                        while (referenceLength--) {

                            if (json.chromosomes[referenceLength].length == max) {
                                maximumLengthname = json.chromosomes[referenceLength].name;
                                maximumsequencelength = json.chromosomes[referenceLength].length;
                            }

                            var top = parseInt(referenceLength * (height)) + parseInt(distance * referenceLength) + parseInt(distance);
                            var width = (json.chromosomes[referenceLength].length * maxWidth / max);
                            var length = json.chromosomes[referenceLength].length;
                            if (seqregname == json.chromosomes[referenceLength].name) {
                                refheight = height;
                            }
                            var left = "10%";
                            jQuery("#chr_map").append("<div length=" + length + " coord=" + json.chromosomes[referenceLength].coord + " name=" + json.chromosomes[referenceLength].name + " id='parent_" + json.chromosomes[referenceLength].seq_region_id + "' style='position: absolute; border: 1px solid grey; background-color: whitesmoke; cursor: default; border-radius:0;left:55px; top: " + top + "px; width:" + width + "px; height:" + height + "px;'></div>");
                            jQuery("#chr_map").append("<div style='position:absolute; color:blue; top: " + (top) + "px; left:0px; width:50px; text-align:right;'> " + json.chromosomes[referenceLength].name + "</div>");

                        }
                        //    new func


                        var lines = json.lines;

                        var track_html = "Select Lines: <br> <select class=\"js-example-basic-single\" id=\"lines_searchable_list\" name=\"lines_searchable_list\" style=\"width: 75%\"> " +
                            "<option value='all' selected>All</option>";

                        for (var i = 0; i < lines.length; i++) {
                            var selected = "";
                            track_html += "<option value='" + lines[i].id + "'" + selected + ">" + lines[i].name + "</option>"
                        }
                        track_html += "</select>"

                        jQuery("#lines_list").html(track_html)

                        jQuery('.js-example-basic-single').select2({
                            placeholder: "Search here.."
                        });

                        jQuery('.js-example-basic-single').on('select2:unselecting', function (e) {
                            var item = e["params"]["args"]["data"]["text"]
                            //hide deletions
                            jQuery(".markers_on_map").remove()

                            return true;
                        })
                        jQuery('.js-example-basic-single').on('select2:select', function (evt) {
                            var args = JSON.stringify(evt.params, function (key, value) {
                                var item = evt["params"]["data"]["text"]
                                var markers = jQuery(".markers_on_map")
                                if (markers.hasClass("all_markers")) {
                                    markers.hide();
                                } else {
                                    markers.remove();
                                }

                                if (item == "All") {
                                    if (jQuery(".all_markers").length > 0) {
                                        jQuery(".all_markers").show()
                                    } else {
                                        loadDeletions(item)
                                    }
                                } else {
                                    loadDeletions(item)
                                }
                            });
                        })

                        loaded = true;

                    }
                });

        }

        function jumpToRef(name, coord, begin, end) {
            window.location.replace("index.jsp?query=" + name + "&coord=" + coord + "&from=" + begin + "&to=" + end);
        }

        function loadDeletions(name) {
            var url = "/" + jQuery('#title').text() + "/deletion.jsp?query=" + name
            window.history.pushState(name, jQuery('#title').text() + "-" + name, url)
            Fluxion.doAjax(
                'dnaSequenceService',
                'getMiscFeature',
                {'url': ajaxurl, 'name': name},
                {
                    'doOnSuccess': function (json) {
                        var marker = json.features;
                        if (json.type == "graph") {
                            var height = 18;

                            for (var parent in json.features) {
                                var markers = json.features[parent]
                                var marker_length = markers.length;
                                var deletions = "";
                                while (marker_length--) {
                                    var marker = markers[marker_length]

                                    var length = jQuery("#parent_" + parent).attr("length")
                                    var left = ((marker.start) * parseFloat(jQuery("#parent_" + parent).css('width'))) / length;

                                    var width = ((marker.end - marker.start) * parseFloat(jQuery("#parent_" + parent).css('width'))) / length;

                                    if (width < 1) {
                                        width = 1;
                                    }

                                    deletions += "<div " +
                                        "title='" + marker.start + ":" + marker.end + "' " +
                                        "class='markers_on_map all_markers' " +
                                        "style='left:" + left + "px; top:0px;  width:" + width + "px; height:" + height + "px;' " +
                                        ">" +
                                        "</div>";
                                }
                                jQuery("#parent_" + parent).append(deletions)
                                deletions = "";

                            }

                        } else {
                            jQuery("#lines_searchable_list").val(json.features[0].line_id).trigger('change')
                            var marker_length = marker.length;
                            while (marker_length--) {
                                var marker = json.features[marker_length]
                                if (marker.parent) {
                                    var length = jQuery("#parent_" + marker.parent).attr("length")
                                    var left = ((marker.start) * parseFloat(jQuery("#parent_" + marker.parent).css('width'))) / length;
                                    var height = 18

                                    var width = ((marker.end - marker.start) * parseFloat(jQuery("#parent_" + marker.parent).css('width'))) / length;
                                    if (width < 1) {
                                        width = 1;
                                    }
                                    var coord = jQuery("#parent_" + marker.parent).attr("coord")
                                    var parent_name = jQuery("#parent_" + marker.parent).attr("name")
                                    jQuery("#parent_" + marker.parent).append("<div " +
                                        "title='" + marker.start + ":" + marker.end + "' " +
                                        "class='markers_on_map line" + marker.line_id + "' " +
                                        "style='opacity:0.3; left:" + left + "px; top:0px;  width:" + width + "px; height:" + height + "px;' " +
                                        " onclick='jumpToRef(\"" + parent_name + "\",\"" + coord + "\"," + marker.start + "," + marker.end + ");'>" +
                                        "</div>");

                                }
                            }
                        }
                    }
                });

        }


        jQuery(document).ready(function () {
            var chr = metaData();
            listMiscFeatures()
            jQuery.urlParam = function (name) {
                var results = new RegExp('[\\?&]' + name + '=([^&#]*)').exec(window.location.href);
                if (results == null) {
                    return null;
                } else {
                    return results[1] || 0;
                }
            }
            waitForIt();

            function waitForIt() {
                if (loaded == false) {
                    setTimeout(function () {
                        waitForIt()
                    }, 100);
                } else {
                    if (jQuery.urlParam("query") != null) {
                        loadDeletions(jQuery.urlParam("query"))
                    } else {
                        loadDeletions("All");
                    }
                }
            }

        });


    </script>


<%@ include file="footer.jsp" %>