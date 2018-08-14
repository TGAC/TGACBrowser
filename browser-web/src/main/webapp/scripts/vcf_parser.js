/**
 * Created by thankia on 15/07/2016.
 */

function readVCF(trackName, trackId, div) {
    var vcf = [];
    var vcfid = 1;
    var data = window[trackName].split(/\r\n|\n/)

    var counter = 0;
    data.forEach(function (line) {
        if (line.indexOf('#') != 0) {
            var parts = line.split('\t');
            if (parts.length >= 8  && parts[0] == seqregname && parseInt(parts[1]) < sequencelength) {
                var attParts = parts[7].split(';');
                var arrayObject = {};

                var feature = {
                    ref: parts[0],
                    source: parts[1],
                    id: parts[2],
                    start: parseInt(parts[1]),
                    end: parseInt(parts[1])+parseInt(parts[3].length)-1, // because when drawing, end base is counted for track length as well
                    source: parts[3],
                    alt: parts[4],
                    qual: parts[5],
                    filter: parts[6],
                    desc:parts[0]+":"+parts[3]+":"+parts[4]
                };

                //eachEntry.put("genotype", entry.getGenotypes());

                for (var i = 0; i < attParts.length; ++i) {
                    var pair = attParts[i].split("=");
                    arrayObject[pair[0].toLowerCase()] = pair[1];
                }

                feature["info"] = arrayObject;
                vcf.push(feature)
            }
            else{
                counter++;
            }
        } else {
            var err = new Error('9 parts of feature not found');
        }
    });
    window['track_list' + trackName].data = vcf
    window[trackName] = filterData(trackName);
    trackToggle(trackName)
}

function filterData(trackName){
    var data;
    if(window['track_list' + trackName].data){
        data = window['track_list' + trackName].data;
    }else{
        data = window[trackName];
    }

    var temp_data = []
    var start = getBegin();
    var end = getEnd();

    var diff = (end-start)/2

    start = start - diff
    end = parseInt(end) + parseInt(diff)

    jQuery.each(data, function (index, value) {
        if(parseInt(value.start) > parseInt(start) && parseInt(value.start) < parseInt(end)){
            temp_data.push(value)
        }

    })
    return temp_data;
}

