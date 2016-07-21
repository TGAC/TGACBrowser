/**
 * Created by thankia on 15/07/2016.
 */

function readGFF(trackName, trackId, div) {
    var gene = {}, transcript = {}, exon = {}, cds = {}, fiveUTR = {}, threeUTR = {};
    var geneid = 1, transcriptid = 1, exonid = 1, cdsid = 1, threeUTRid = 1, fiveUTRid = 1;
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
                    phase: parts[7],
                };

                for (var i = 0; i < attParts.length; ++i) {
                    var pair = attParts[i].split("=");
                    feature[pair[0].toLowerCase()] = pair[1];
                }

                if (parts[2].toLowerCase() == 'gene') {
                    feature["transcript"] = []
                    feature["layer"] = (geneid % 4) + 1

                    if (feature['id'] == undefined) {
                        feature["id"] = geneid++;
                        gene[feature['name']] = feature
                    } else {
                        gene[feature['id']] = feature
                        geneid++;
                    }
                } else if (parts[2].toLowerCase() == 'transcript') {
                    feature["Exons"] = []
                    feature["Translation"] = {}
                    feature["Translation"]["CDS"] = []

                    feature["layer"] = (transcriptid % 4) + 1
                    if (feature['id'] == undefined) {
                        feature["id"] = transcriptid++;
                        transcript[feature['name']] = feature
                    } else {
                        transcript[feature['id']] = feature
                        transcriptid++;
                    }

                } else if (parts[2].toLowerCase() == 'exon') {
                    if (feature['id'] == undefined) {
                        feature["id"] = exonid++;
                    }
                    if (!exon[feature['parent']]) {
                        exon[feature['parent']] = []
                    }
                    exon[feature['parent']].push(feature)

                } else if (parts[2].toLowerCase() == 'cds') {
                    if (feature['id'] == undefined) {
                        feature["id"] = cdsid++;
                    }

                    if (!cds[feature['parent']]) {
                        cds[feature['parent']] = []
                    }
                    cds[feature['parent']].push(feature)
                } else if (parts[2].toLowerCase() == 'five_prime_utr') {
                    if (feature['id'] == undefined) {
                        feature["id"] = fiveUTRid++;
                    } else {
                        transcript[feature['id']] = feature
                        transcriptid++;
                    }
                    if (!fiveUTR[feature['parent']]) {
                        fiveUTR[feature['parent']] = []
                    }
                    fiveUTR[feature['parent']].push(feature)
                } else if (parts[2].toLowerCase() == 'three_prime_utr') {
                    if (feature['id'] == undefined) {
                        feature["id"] = threeUTRid++;
                    }
                    if (!threeUTR[feature['parent']]) {
                        threeUTR[feature['parent']] = []
                    }
                    threeUTR[feature['id']].push(feature)
                }
            } else {
                var err = new Error('9 parts of feature not found');
            }
        }
    });
    window[trackName] = joinGFF(gene, transcript, exon, cds, threeUTR, fiveUTR)
    window['track_list' + trackName].data = window[trackName]
    trackToggle(trackName)


}

function joinGFF(genes, transcripts, exons, CDSs, threeUTR, fiveUTR) {
    var gene_array = []

    for (var parent in exons) {
        if (transcripts.hasOwnProperty(parent)) {
            exons[parent].sort(function (a, b) {
                return parseFloat(a.start) - parseFloat(b.start);
            });
            transcripts[parent]["Exons"] = exons[parent]
        }
    }

    for (var parent in CDSs) {
        if (transcripts.hasOwnProperty(parent)) {
            CDSs[parent].sort(function (a, b) {
                return parseFloat(a.start) - parseFloat(b.start);
            });
            transcripts[parent]["Translation"]["CDS"] = CDSs[parent]
            transcripts[parent]["Translation"]["id"] = CDSs[parent][0]["protein_id"]
            transcripts[parent]["Translation"]["start"] = CDSs[parent][0]["start"]
            transcripts[parent]["Translation"]["end"] = CDSs[parent][CDSs[parent].length - 1]["end"]
            transcripts[parent]["transcript_start"] = CDSs[parent][0]["start"]
            transcripts[parent]["transcript_end"] = CDSs[parent][CDSs[parent].length - 1]["end"]

        }
    }

    for (var id in transcripts) {
        var parent = transcripts[id].parent
        if (genes.hasOwnProperty(parent)) {
            genes[parent]["transcript"].push(transcripts[id])
        }
    }

    if (Object.keys(threeUTR).length > 0) {
        for (var parent in threeUTR) {
            if (transcripts.hasOwnProperty(parent)) {
                transcripts[parent]["transcript"]["transcript_start"].push(threeUTR[parent]["end"])
            }
        }
    }

    if (Object.keys(fiveUTR).length > 0) {
        for (var parent in fiveUTR) {
            if (transcripts.hasOwnProperty(parent)) {
                transcripts[parent]["transcript"]["transcript_end"].push(fiveUTR[parent]["start"])
            }
        }
    }

    for (var gene in genes) {
        gene_array.push(genes[gene])
    }

    gene_array.sort(function(a, b) {
        return parseFloat(a.start) - parseFloat(b.start);
    });

    return gene_array;
}
