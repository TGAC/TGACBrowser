/**
 * Created with IntelliJ IDEA.
 * User: thankia
 * Date: 18/11/2013
 * Time: 16:00
 * To change this template use File | Settings | File Templates.
 */

function dispGraphWig(div, trackName, trackId, className) {

    var track_html = "";
    jQuery(div).html("");
    jQuery(div).fadeIn();
    jQuery(trackName + "_wrapper").fadeIn();

    var track = window[trackName];

    if (track) {
        if (track[0] == null) {
            track_html = [];
            track_html.push("<font size=4><center>No data available for selected region</center></font>");
        } else {
            if (track[0][1] > 0) {
                posGraphWig(div, trackName, trackId, className)
            } else {
                negGraphWig(div, trackName, trackId, className)
            }
        }

    } else {
        jQuery(div).html("<img style='position: relative; left: 50%; ' src='./images/browser/loading_big.gif' alt='Loading'>")
    }
    jQuery(div).css('height', '70px');
    jQuery(div).fadeIn();
    jQuery("#" + trackName + "_wrapper").fadeIn();
    jQuery("#" + trackName + "_wrapper").css("max-height", '70px');

}

function posGraphWig(div, trackName, trackId, className) {

    jQuery(div).html("");
    jQuery(div).fadeIn();
    jQuery(trackName + "_wrapper").fadeIn();

    var track = window[trackName];
    var partial = (parseInt(getEnd()) - parseInt(getBegin())) / 2;
    var start = parseInt(getBegin()) - parseInt(partial)
    var end = parseInt(getEnd()) + parseInt(partial);


    var total = 0;
    var max = Math.max.apply(Math, track.map(function (o) {
        return o[1];
    }));

    var width = jQuery("#wrapper").width(),
        height = 80;


    var left = 0;
    if (start < 0) {
        left = (1 - start) * parseInt(width) / end * 3 / 4;
    }

    var top = 20;

    var svg = d3.select(div).append("svg")
        .attr("width", width)
        .attr("height", height + 20)
        .append("g")
        .attr("transform", "translate(" + left + "," + top + ")");

    var d3line2 = d3.svg.line()
        .x(function (d) {
            return d.x;
        })
        .y(function (d) {
            return d.y;
        })
        .interpolate("linear");

    d3.json(track, function () {
        data = track;
        var length = data.length - 1;
        var end_val = parseInt(data[length][0]) + parseInt(data[1][0] - data[0][0]);
        var start_val = parseInt(data[0][0]) - (parseInt(data[1][0] - data[0][0]));

        data.splice(0, 0, [start_val, '0']);

        data.splice(data.length, 0, [ end_val, '0']);
        var space = parseInt(width) / (end - start);

        var pathinfo = [];

        var last_start = 0;

        var diff = parseInt(data[1][0] - data[0][0]);

        if (diff > parseInt(data[2][0] - data[1][0]) || diff > parseInt(data[3][0] - data[2][0])) {
            if (diff > parseInt(data[2][0] - data[1][0])) {
                diff = parseInt(data[2][0] - data[1][0])
            }
            else {
                diff = parseInt(data[3][0] - data[2][0])
            }
        }


        var data_len = data.length;
        for (var i = 0; i < data_len - 1;) {
            var tempx;
            if (start > 0) {
                tempx = (data[i][0] - start) * space;
            }
            else {
                tempx = (data[i][0]) * space;
            }

            var tempy = height - (data[i][1] * height / max);
            pathinfo.push({ x: tempx, y: tempy});


            i++;

            if (last_start < data[i][0] - diff) {
                if (start > 0) {
                    tempx = ((parseInt(last_start) + parseInt(diff)) - start) * space;
                }
                else {
                    tempx = ((parseInt(last_start) + parseInt(diff))) * space;
                }
                var tempy = height; //(parseInt(max)*(41-parseInt(patharray[i]))/41);
                pathinfo.push({ x: tempx, y: tempy});

                if (start > 0) {
                    tempx = ((parseInt(data[i][0]) - parseInt(diff)) - start) * space;
                }
                else {
                    tempx = ((parseInt(data[i][0]) - parseInt(diff))) * space;
                }
                var tempy = height;
                pathinfo.push({ x: tempx, y: tempy});

            }

            last_start = data[i][0];
        }


        if (start > 0) {
            tempx = (data[data.length - 1][0] - start) * space;
        }
        else {
            tempx = (data[data.length - 1][0]) * space;
        }
        var tempy = height - (data[data.length - 1][1] * height / max);
        pathinfo.push({ x: tempx, y: tempy});
        var path = svg.selectAll("path")
            .data([1]);

        path.enter().append("svg:path")
            .attr("width", 200)
            .attr("height", 200)
            .attr("class", "path " + className)
            .attr("d", d3line2(pathinfo));
    });


    jQuery(div).css('height', '70px');
    jQuery(div).fadeIn();
    jQuery("#" + trackName + "_wrapper").fadeIn();
}

function negGraphWig(div, trackName, trackId, className) {

    jQuery(div).html("");
    jQuery(div).fadeIn();
    jQuery(trackName + "_wrapper").fadeIn();

    var track = window[trackName];
    var partial = (parseInt(getEnd()) - parseInt(getBegin())) / 2;
    var start = parseInt(getBegin()) - parseInt(partial)
    var end = parseInt(getEnd()) + parseInt(partial);


    var min = Math.min.apply(Math, track.map(function (o) {
        return o[1];
    }));

    var width = jQuery("#wrapper").width(),
        height = 80;


    var left = 0;
    if (start < 0) {
        left = (1 - start) * parseInt(width) / end * 3 / 4;
    }

    var top = 0;

    var svg = d3.select(div).append("svg")
        .attr("width", width)
        .attr("height", height + 20)
        .append("g")
        .attr("transform", "translate(" + left + "," + top + ")");

    var d3line2 = d3.svg.line()
        .x(function (d) {
            return d.x;
        })
        .y(function (d) {
            return d.y;
        })
        .interpolate("linear");

    d3.json(track, function () {
        data = track;
        var length = data.length - 1;
        var end_val = parseInt(data[length][0]) + parseInt(data[1][0] - data[0][0]);
        var start_val = parseInt(data[0][0]) - (parseInt(data[1][0] - data[0][0]));

        data.splice(0, 0, [start_val, 0]);

        data.splice(data.length, 0, [ end_val, 0]);
        var space = parseInt(width) / (end - start);

        var pathinfo = [];

        var last_start = 0;

        var diff = parseInt(data[1][0] - data[0][0]);

        if (diff > parseInt(data[2][0] - data[1][0]) || diff > parseInt(data[3][0] - data[2][0])) {
            if (diff > parseInt(data[2][0] - data[1][0])) {
                diff = parseInt(data[2][0] - data[1][0])
            }
            else {
                diff = parseInt(data[3][0] - data[2][0])
            }
        }


        var data_len = data.length;
        for (var i = 0; i < data_len - 1;) {
            var tempx;
            if (start > 0) {
                tempx = (data[i][0] - start) * space;
            }
            else {
                tempx = (data[i][0]) * space;
            }

            var tempy = (data[i][1] * height / min);
            pathinfo.push({ x: tempx, y: tempy});


            i++;

            if (last_start < data[i][0] - diff) {
                if (start > 0) {
                    tempx = ((parseInt(last_start) + parseInt(diff)) - start) * space;
                }
                else {
                    tempx = ((parseInt(last_start) + parseInt(diff))) * space;
                }
                var tempy = 0;
                pathinfo.push({ x: tempx, y: tempy});

                if (start > 0) {
                    tempx = ((parseInt(data[i][0]) - parseInt(diff)) - start) * space;
                }
                else {
                    tempx = ((parseInt(data[i][0]) - parseInt(diff))) * space;
                }
                var tempy = 0;
                pathinfo.push({ x: tempx, y: tempy});

            }

            last_start = data[i][0];
        }


        if (start > 0) {
            tempx = (data[data.length - 1][0] - start) * space;
        }
        else {
            tempx = (data[data.length - 1][0]) * space;
        }
        var tempy = 0;
        pathinfo.push({ x: tempx, y: tempy});
        var path = svg.selectAll("path")
            .data([1]);

        path.enter().append("svg:path")
            .attr("width", 200)
            .attr("height", 200)
            .attr("class", "path " + className)
            .attr("d", d3line2(pathinfo));
    });

    jQuery(div).css('height', '70px');
    jQuery(div).fadeIn();
    jQuery("#" + trackName + "_wrapper").fadeIn();
}

function dispGraphBed(div, trackName, trackId, className) {
    var track_html = "";

    if (!window[trackName] || window[trackName] == "loading") {
        jQuery(div).html("<img style='position: relative; left: 50%; ' src='./images/browser/loading_big.gif' alt='Loading'>")
        jQuery(div).fadeIn();
        jQuery("#" + trackName + "_wrapper").fadeIn();

    }
    else {
        var track = window[trackName];
        var partial = (parseInt(getEnd()) - parseInt(getBegin())) / 2;
        var start = parseInt(getBegin()) - parseInt(partial)
        var end = parseInt(getEnd()) + parseInt(partial);
        var maxLen_temp = jQuery("#canvas").css("width");

        var newStart_temp = getBegin();
        var newEnd_temp = getEnd();

        if (track[0]) {
            track = jQuery.grep(track, function (element, index) {
                return element.start >= start && element.start <= end; // retain appropriate elements
            });
        }


        var total = 0;
        var max = Math.max.apply(Math, track.map(function (o) {
            return o.value;
        }));

        var track_len = track.length;

        while (track_len--) {
            var track_start = track[track_len].start;
            var track_stop = track[track_len].end;

            var startposition = (track_start - newStart_temp) * parseFloat(maxLen_temp) / (newEnd_temp - newStart_temp) + parseFloat(maxLen_temp) / 2;
            var stopposition = (track_stop - track_start ) * parseFloat(maxLen_temp) / (newEnd_temp - newStart_temp);

            track_html += "<div class= \"graph " + className + "graph\" onclick=\"setBegin(" + track[track_len].start + ");setEnd(" + track[track_len].end + ");jumpToSeq();\"STYLE=\"bottom:0px; height: " + (track[track_len].value * 45 / max) + "px;" +
                "LEFT:" + startposition + "px;" +
                "width:" + (stopposition - 1) + "px \" title=\"" + track_start + ":" + track_stop + "->" + track[track_len].value + "\" ></div>";

        }
        jQuery(div).fadeIn();
        jQuery("#" + trackName + "_wrapper").fadeIn();
        jQuery(div).html(track_html);
    }
}



function sortResults(prop, asc, array) {
    array = array.sort(function (a, b) {
        if (asc) return (a[prop] > b[prop]);
        else return (b[prop] > a[prop]);
    });
    return array;
}