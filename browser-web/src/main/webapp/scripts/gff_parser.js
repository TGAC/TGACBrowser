/**
 * Created by thankia on 15/07/2016.
 */

function readGFF(trackName, trackId, div){
    var gene = {}, transcript = {}, exon = {}, cds ={}
    var geneid = 1, transcriptid = 1, exonid = 1, cdsid = 1;
    var data = window[trackName].split(/\r\n|\n/)
    data.forEach(function (line) {
        if (line.indexOf('#') != 0) {
            var parts = line.split('\t');

            if (parts.length !== 9) {
                parts = line.trim().split(/\s+/);
            }

            if (parts.length == 9) {
                var attParts = parts[8].split(';');
                var arrayObject = {};
                var feature = {
                    seq_region_name: parts[0],
                    source: parts[1],
                    type: parts[2],
                    start: parseInt(parts[3]),
                    end: parseInt(parts[4]),
                    score: parts[5],
                    strand: parts[6] == "+" ? 1 : -1,
                    phase: parts[7]
                };

                for (var i = 0; i < attParts.length; ++i) {
                    var pair = attParts[i].split("=");
                    feature[pair[0].toLowerCase()] = pair[1];
                }

                if(parts[2].toLowerCase() == 'gene'){
                    if(feature['id'] == undefined){
                        feature["id"] = geneid++;
                    }
                    feature["Transcript"] = []
                    gene[feature['name']] = feature
                }else if(parts[2].toLowerCase() == 'transcript'){
                    if(feature['id'] == undefined){
                        feature["id"] = transcriptid++;
                    }
                    feature["Exon"] = []
                    feature["CDS"] = []
                    transcript[feature['name']] = feature
                }else if(parts[2].toLowerCase() == 'exon'){
                    if(feature['id'] == undefined){
                        feature["id"] = exonid++;
                    }
                    exon[feature['id']] = feature
                }else if(parts[2].toLowerCase() == 'cds'){
                    if(feature['id'] == undefined){
                        feature["id"] = cdsid++;
                    }
                    cds[feature['id']] = feature
                }
            } else {
                var err = new Error('9 parts of feature not found');
            }
        }
    });

    window[trackName] = joinGFF(gene, transcript, exon, cds)

    trackToggle(trackName)


}

function joinGFF(genes, transcripts, exons, CDSs){
    for(var id in exons){
        var parent = exons[id].parent
        if(transcripts.hasOwnProperty(parent)){
            transcripts[parent]["Exon"].push(exons[id])
        }
    }

    for(var id in CDSs){
        var parent = CDSs[id].parent
        if(transcripts.hasOwnProperty(parent)){
            transcripts[parent]["CDS"].push(CDSs[id])
        }
    }

    for(var id in transcripts){
        var parent = transcripts[id].parent
        if(genes.hasOwnProperty(parent)){
            genes[parent]["Transcript"].push(transcripts[id])
        }
    }
}
