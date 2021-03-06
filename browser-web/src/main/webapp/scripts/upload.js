/**
 * Created by thankia on 30/03/2016.
 */
function uploadFile() {
    var file = ""
    readGem((file, trackName, trackId, div))
}

var holder, tests,
    support,
    acceptedTypes,
    progress,
    fileupload;
var tree, distance, csv_lables = null;


var allowedTypes = [{
    extension: ".newick"
}, {
    extension: ".nhx"
}, {
    extension: ".csv"
}, {
    extension: ".gff"
}, {
    extension: ".gff3"
}, {
    extension: ".vcf"
}, {
    extension: ".wig"
}]

function initUpload() {

    holder = document.getElementById('holder')
    tests = {
        filereader: typeof FileReader != 'undefined',
        dnd: 'draggable' in document.createElement('span'),
        formdata: !!window.FormData,
        progress: "upload" in new XMLHttpRequest
    },
        support = {
            filereader: document.getElementById('filereader'),
            formdata: document.getElementById('formdata'),
            progress: document.getElementById('progress')
        },
        acceptedTypes = {
            'text/txt': true,
            'text/csv': true,
            'text/gff': true
        },
        progress = document.getElementById('uploadprogress'),
        fileupload = document.getElementById('upload');

    if (tests.dnd) {
        holder.ondragover = function () {
            this.className = 'hover';
            return false;
        };
        holder.ondragend = function () {
            this.className = '';
            return false;
        };
        holder.ondrop = function (e) {
            this.className = '';
            e.preventDefault();
            readfiles(e.dataTransfer.files);
        }
    } else {
        fileupload.className = 'hidden';
        fileupload.querySelector('input').onchange = function () {
            readfiles(this.files);
        };
    }
}

function readfiles(files) {
    // debugger;
    var formData = tests.formdata ? new FormData() : null;
    var fileInput = document.getElementById('fileInput');
    var fileDisplayArea = document.getElementById('fileDisplayArea');
    var flag = false;
    var textType = /text.*/;
    setup_reader(files, 0);


}

function setup_reader(files, i) {

    var file = files[i];
    var name = file.name;

    var fileExtensionPatter = /\.([0-9a-z]+)(?=[?#])|(\.)(?:[\w]+)$/
    var extension = name.match(fileExtensionPatter)[0]
    for (var j = 0; j < allowedTypes.length; j++) {
        if (extension == allowedTypes[j].extension) {
            var reader = new FileReader();
            reader.onload = function (e) {

            };
            reader.onloadend = function (e) {
                readerLoaded(e, files, i, name);
            };
            reader.readAsText(file);
        }
    }
}

function readerLoaded(e, files, i, name) {

    // get file content
    var bin = e.target.result;
    // do sth with text
    var fileExtensionPatter = /\.([0-9a-z]+)(?=[?#])|(\.)(?:[\w]+)$/
    var extension = name.match(fileExtensionPatter)[0]


    window["gem"] = bin;

    var count = 0;

    track_list.forEach(function(d,i){
        if(d.name.indexOf("upload") >= 0){
            count++;
        }
    })

    var track_list_length = track_list.length

    if((extension.indexOf("csv") >= 0 && (name.toLowerCase().indexOf("gapit") >= 0 || name.toLowerCase().indexOf("gem") >= 0)) || (extension.indexOf("gapit") >= 0 || extension.indexOf("gem") >= 0)){
        var trackname=name.split(".")[0]
        track_list.push({
            name: "uploadManhattan"+count,
            display_label: trackname+"Manhattan",
            id: "noid"+count,
            desc: "uploaded file:"+trackname,
            disp: 1,
            merge: 0,
            label: name+"Manhattan",
            graph: 1,
            graphtype: "manhattan",
            label_show: true,
            web:{colour:"red",source : "file", trackgroup: i}
        });

        trackList(track_list, track_list_length)



        window["uploadManhattan"+count] = processData(window["gem"])

        readGem("uploadManhattan"+count, "noid"+count, "#uploadManhattan"+count+"_div")

        track_list.forEach(function(d,i){
            if(d.name.indexOf("upload") >= 0){
                count++;
            }
        })

        var track_list_length = track_list.length


        track_list.push({
            name: "uploadCDS"+count,
            display_label: trackname+"Gene",
            id: "noid"+count,
            desc: "uploaded file:"+trackname,
            disp: 1,
            merge: 0,
            label: name+"Gene",
            ensembl:"http://plants.ensembl.org/Multi/Search/Results?species=all;idx=;q=",
            graph: 0,
            graphtype: null,
            label_show: true,
            web:{colour:"red",source : "file", trackgroup: i}
        });

        trackList(track_list, track_list_length)

        window["uploadCDS"+count] = processData(window["gem"])

        readCDSfromGem("uploadCDS"+count, "noid"+count, "#uploadCDS"+count+"_div")

    }else if(extension.indexOf("gff") >= 0 ){
        var trackname=name.split(".")[0]

        track_list.forEach(function(d,i){
            if(d.name.indexOf("upload") >= 0){
                count++;
            }
        })

        var track_list_length = track_list.length


        track_list.push({
            name: "uploadGFF"+count,
            display_label: trackname+"Gene",
            id: "noid"+count,
            desc: "uploaded file:"+trackname,
            disp: 1,
            merge: 0,
            label: name+"Gene",
            ensembl:"http://plants.ensembl.org/Multi/Search/Results?species=all;idx=;q=",
            graph: 0,
            graphtype: null,
            label_show: true,
            web:{colour:"red",source : "file", trackgroup: i}
        });

        trackList(track_list, track_list_length)

        window["uploadGFF"+count] = window["gem"]

        readGFF("uploadGFF"+count, "noid"+count, "#uploadGFF"+count+"_div")

    }else if(extension.indexOf("vcf") >= 0 ){
        var trackname=name.split(".")[0]

        track_list.forEach(function(d,i){
            if(d.name.indexOf("upload") >= 0){
                count++;
            }
        })

        var track_list_length = track_list.length


        track_list.push({
            name: "uploadVCF"+count,
            display_label: trackname+"VCF",
            id: "noid"+count,
            desc: "uploaded file:"+trackname,
            disp: 1,
            merge: 0,
            label: name+"VCF",
            ensembl:"http://plants.ensembl.org/Multi/Search/Results?species=all;idx=;q=",
            graph: 0,
            graphtype: null,
            label_show: true,
            web:{colour:"red",source : "file", trackgroup: i}
        });

        trackList(track_list, track_list_length)

        window["uploadVCF"+count] = window["gem"]

        readVCF("uploadVCF"+count, "noid"+count, "#uploadVCF"+count+"_div")

    }
    else if(extension.indexOf("wig")){
        // track_list.forEach(function(d,i){
        //     if(d.name.indexOf("upload") >= 0){
        //         count++;
        //     }
        // })

        var track_list_length = track_list.length


        var partial = (parseInt(getEnd()) - parseInt(getBegin())) / 2;
        var start = parseInt(getBegin()) - parseInt(partial)
        var end = parseInt(getEnd()) + parseInt(partial);


        track_list.push({
            name: "uploadWig"+count,
            display_label: "uploadWig"+count,
            id: "noidWig"+count,
            desc: "uploaded file:"+trackname,
            disp: 1,
            merge: 0,
            label: name+"Wig",
            graph: 0,
            graphtype: null,
            label_show: true,
            start_pos: start,
            end_pos: end,
            web:{colour:"red",source : "file", trackgroup: i}
        });

        trackList(track_list, track_list_length)

        window['track_list' + "uploadWig"+count].start_pos = start
        window['track_list' + "uploadWig"+count].end_pos = end

        jQuery("#uploadWig"+count+"_wrapper").css("max-height", "165px")
        var wig = [];
        var span = null;
        var flag = false;
        var min = 0
        var max = 0

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

    // If there's a file left to load
    if (i < files.length - 1) {
        setup_reader(files, i + 1);
    }

}

function processFile(bin){
    var bin_array = bin.split("\n")

    for (var i = 0; i < bin_array.length; i++) {
        wig.push(bin_array[i]);
    }
}

function processData(allText) {


    var allTextLines = allText.split(/\r\n|\n/);
    var headers = allTextLines[0].split(',');
    var chr = headers.indexOf("Chr")
    var lines = [];
    var a = 0;

    for (var i = 1; i < allTextLines.length; i++) {
        var obj = {}
        var data = allTextLines[i].split(',');
        if (data.length == headers.length && data[chr] == seqregname) {
            lines[a] = {}
            for (var j = 0; j < headers.length; j++) {
                lines[a][headers[j]] = data[j];
            }
            a++;
        }
    }
    return lines;
}

function jsonArraytoObject(array) {
    var obj = {}
    for (var i = 0; i < array.length; i++) {
        obj[array[i].Protein_GI] = array[i]
    }
    return obj;
}
