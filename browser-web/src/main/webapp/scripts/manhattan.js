/**
 * Created by thankia on 30/03/2016.
 */

var duration = 10;
function readGem(trackName, trackId, div) {
    var data = window[trackName]
    var keys = d3.keys(window[trackName][0])
    window[trackName] = []
    data.forEach(function (d, i) {
        if (d.Chr == seqregname) {
            var start = d.Loc.split("_")[1]
            var end= d.Loc.split("_")[2]
            if(end > start){
                start = start
                end = end
            }else{
                start = end;
                end = start;
            }

            d.ref = d.Loc.split("_")[0]; //d[keys[0]].split(":")[0];
            // d.position = parseInt(start) + parseInt(d[keys[0]].split(":")[1]);
            var addition = (d[keys[0]].split(":")[1] ? parseInt(d[keys[0]].split(":")[1]) : 0);
            d.position = parseInt(start) + addition;

            d.refbase = d[keys[0]].split(":")[2];
            d.cds = d[keys[0]].split(":")[0];
            // console.log(d)

            window[trackName].push({
                "ref": d.ref,
                "position": d.position,
                "refbase": d.refbase,
                "log10P": noExponents(d.log10P),
                "cds": d.cds,
                "cds_pos": d.cds_pos
            })
        }

    });

    var margin = {top: 10, right: 0, bottom: 10, left: 0};
    var width = jQuery("#wrapper").width(),
        height = 90;


    var y = d3.scale.linear().range([height, 0]);


    window[trackName+"svg"] = d3.select(div)
        .append("svg")
        .attr("width", width )
        .attr("height", height+margin.top+margin.bottom )
        .append("g")
        .attr("transform",
            "translate(" + margin.left + "," + margin.top + ")");
    var gem = window[trackName];

    var yScale = d3.scale.linear()
        .domain(d3.extent(gem, function (d) {
            return d.log10P;
        }))
        .range([height, 0]);
    var yAxis = d3.svg.axis().scale(yScale).orient("left");
    var svg = window[trackName+"svg"]


    svg.append("g")
        .attr("class", "y axis")
        .attr("transform", "translate("+width/2+",0)")
        .call(yAxis)
        .append("text")
        .attr("class", "label")
        .attr("transform", "rotate(-90)")
        .attr("y", 6)
        .attr("dy", "10px")
        .style("fill", "none")
        .style("text-anchor", "end")
        .text("log10P");


    dispGraphManhattan(div, trackName, trackId)
    // })
}

function readCDSfromGem(trackName, trackId, div) {
    console.log("readCDSfromGem")
    var data = window[trackName]
    var temp_data = []
    var outputList = [];

    window[trackName] = []
    data.forEach(function (d, i) {
        if (d.Chr == seqregname) {
            var start = d.Loc.split("_")[1]
            var end = d.Loc.split("_")[2]

            if(end > start){
                d.start = start
                d.end = end
                d.strand = 1
            }else{
                d.start = end;
                d.end = start;
                d.strand = -1;
            }
            d.ref = d.Loc.split("_")[0]; //d[keys[0]].split(":")[0];
            d.desc = d.TAIR_id;

            if(outputList.indexOf(d.TAIR_id) >=0 ){
                //console.log("exist "+d.TAIR_id)
            }else{
                outputList.push(d.TAIR_id)

                temp_data.push({
                    "ref": d.ref,
                    "start": d.start,
                    "end": d.end,
                    "desc": d.desc,
                })
            }




        }



        window[trackName] = temp_data

    });

    console.log(window[trackName])
    trackToggle(trackName)
    // })
}


function dispGraphManhattan(div, trackName, trackId) {
    console.log("dispGraphManhattan")

    var data = window[trackName];
    var gem = []

    var newStart_temp = getBegin();
    var newEnd_temp = getEnd();
    var partial = (newEnd_temp - newStart_temp) / 2;


    data.forEach(function (d, i) {
        if(d.position >= newStart_temp-partial && d.position <= parseInt(newEnd_temp) + parseInt(partial)){
            gem.push(d)
        }
    });

    var width = jQuery("#wrapper").width(),
        height = 90;
    var margin = {top: 10, right: 0, bottom: 10, left: 0};


    var radious = (width / (newEnd_temp - newStart_temp) )/ 4;
    if(radious < 1)
    {
        radious = 1
    }


    var svg = window[trackName+"svg"]

    var xScale = d3.scale.linear()
        .domain([newStart_temp - partial, parseInt(newEnd_temp) + parseInt(partial)])
        .range([0, width]), // value -> display
        xAxis = d3.svg.axis().scale(xScale).orient("bottom");

    var yScale = d3.scale.linear()
        .domain(d3.extent(gem, function (d) {
            return d.log10P;
        }))
        .range([height, 0]);

    svg.selectAll(".dot")
        .remove();

    var dot = svg.selectAll(".dot")
        .data(gem);

    console.log(gem.length)
    console.log(gem.size())

    var dotEnter = dot.enter().append("circle")
        .attr("class", "dot")
        .attr("r", function(){
            return radious
        })
        .attr("cx", function (d) {
            return xScale(d.position);
        })
        .attr("cy", function (d) {
            return yScale(d.log10P);
        })
        .style("fill", "black")
        .append("svg:title")
        .text(function (d) {
            return d.ref+":"+d.position
        })
        .attr("transform",
            "translate(" + margin.left + "," + margin.top + ")");

    var dotUpdate = dot.transition()
        .duration(duration)
        .attr("cx", function (d) {
            return xScale(d.position);
        })
        .attr("cy", function (d) {
            return yScale(d.log10P);
        })
        .attr("transform",
            "translate(" + margin.left + "," + margin.top + ")");

    var dotExit = dot.exit().transition()
        .duration(duration)
        .attr("cx", function (d) {
            return xScale(d.position);
        })
        .attr("cy", function (d) {
            return yScale(d.log10P);
        })
        .remove();
}

function noExponents(n) {
    var data= String(n).split(/[eE]/);
    if(data.length== 1) return data[0];

    var  z= '', sign= this<0? '-':'',
        str= data[0].replace('.', ''),
        mag= Number(data[1])+ 1;

    if(mag<0){
        z= sign + '0.';
        while(mag++) z += '0';
        return z + str.replace(/^\-/,'');
    }
    mag -= str.length;
    while(mag--) z += '0';
    return str + z;
}