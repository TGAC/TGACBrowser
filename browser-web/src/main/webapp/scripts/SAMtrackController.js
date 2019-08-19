function SAMtrackController(div, track, expand, className)
{

    // var track_list_length = track_list.length


    // var partial = (parseInt(getEnd()) - parseInt(getBegin())) / 2;
    // var start = parseInt(getBegin()) - parseInt(partial)
    // var end = parseInt(getEnd()) + parseInt(partial);
    //
    //
    // track_list.push({
    //     name: "uploadWig"+count,
    //     display_label: "uploadWig"+count,
    //     id: "noidWig"+count,
    //     desc: "uploaded file:"+trackname,
    //     disp: 1,
    //     merge: 0,
    //     label: name+"Wig",
    //     graph: 0,
    //     graphtype: null,
    //     label_show: true,
    //     start_pos: start,
    //     end_pos: end,
    //     web:{colour:"red",source : "file", trackgroup: i}
    // });
    //
    // trackList(track_list, track_list_length)
    //
    // window['track_list' + "uploadWig"+count].start_pos = start
    // window['track_list' + "uploadWig"+count].end_pos = end
    //
    // jQuery("#uploadWig"+count+"_wrapper").css("max-height", "165px")
    // var wig = [];
    // var span = null;
    // var flag = false;
    // var min = 0
    // var max = 0
    //
    var bin_array = bin.split("\n")

    if (bin.indexOf("variableStep") >= 0 || bin.indexOf("fixedStep") >= 0) {
        for (var i = 0; i < bin_array.length; i++) {

            if (bin_array[i].indexOf("chrom") >= 0) {
                var chr = bin_array[i].split(/\s+/)[1].split("=")[1];

                flag = false;
                if (chr == seqregname) {
                    wig.push(bin_array[i]);
                    flag = true;
                }
            }
            else if (bin_array[i].indexOf("#") >= 0) {
                continue;
            } else if (flag && bin_array[i].length > 0) {
                wig.push(bin_array[i]);
            }
        }
    }
    else{
        alert("Unknown format detected")
    }

    window["uploadWig"+count] = wig.join("\n")

    window["uploadWig"+count+"biojs"] = new Biojs.wigExplorer({
        target: "uploadWig"+count+"_div",
        selectionBackgroundColor: 'red',
        dataSet: window["uploadWig"+count] //"data/wigExplorerDataSet.txt"
    });

    var instance = window["uploadWig"+count+"biojs"];

    instance._updateDraw(start,  end)


}