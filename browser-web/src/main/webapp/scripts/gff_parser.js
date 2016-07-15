/**
 * Created by thankia on 15/07/2016.
 */

function readGFF(trackName, trackId, div){
    var gene, transcript, exon, cds = []
    var data = window[trackName]
    data.forEach(function (line) {
        if (line.indexOf('#') != 0) {
            //its not a comment, ill process it

            var parts = line.split('\t');

            if (parts.length !== 9) {
                //the file might use spaces instead of tabs
                //ill try to split it by spaces
                parts = line.trim().split(/\s+/);
            }

            if (parts.length == 9) {
                var attParts = parts[8].split(';');
                var arrayObject = {};
                var feature = {
                    seqid: parts[0],
                    source: parts[1],
                    type: parts[2],
                    start: parts[3],
                    end: parts[4],
                    score: parts[5],
                    strand: parts[6],
                    phase: parts[7],
                    attributes: arrayObject
                };

                for (var i = 0; i < attParts.length; ++i) {
                    var pair = attParts[i].split("=");
                    feature[pair[0]] = pair[1];
                }

                if(parts[2].toLowerCase() == 'gene'){
                    gene.push(feature)
                }else if(parts[2].toLowerCase() == 'exon'){
                    exon.push(feature)
                }else if(parts[2].toLowerCase() == 'transcript'){
                    transcript.push(feature)
                }else if(parts[2].toLowerCase() == 'cds'){
                    cds.push(feature)
                }
            } else {
                var err = new Error('9 parts of feature not found');
            }
        }
    });

    console.log(gene)
    console.log(transcript)
    console.log(exon)
    console.log(cds)
}