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


    // if (tests.formdata) {
    //     var xhr = new XMLHttpRequest();
    //     xhr.open('POST', '/devnull.php');
    //     xhr.onload = function () {
    //         progress.value = progress.innerHTML = 100;
    //     };

    //     if (tests.progress) {
    //         xhr.upload.onprogress = function (event) {
    //             if (event.lengthComputable) {
    //                 var complete = (event.loaded / event.total * 100 | 0);
    //                 progress.value = progress.innerHTML = complete;
    //             }
    //         }
    //     }

    //     xhr.send(formData);
    // }
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

    track_list.push({
        name: "upload"+count,
        display_label: "upload",
        id: "noid"+count,
        desc: "uploaded file:"+name,
        disp: 1,
        merge: 0
    });

    var label = "<div align='left' class='handle'>" +
        "<table>" +
        "<tbody>" +
        "<tr>" +
        "<td><b>"+name+"</b></td>" +
        "<td><div onclick='removeTrack(\"upload"+count+"\",\"upload"+count+"\");' class='closehandle ui-icon ui-icon-close'></div></td>" +
        "</tr>" +
        "</tbody>" +
        "</table>" +
        "</div>";


    jQuery("#tracks").append("<div id='upload"+count + "_wrapper' class='feature_tracks' style=\" max-height:150px; overflow-x: hidden;\">" +
        "</div>");

    jQuery("#upload"+count + "_wrapper").append(label +
        "<div id='upload"+count + "_div' class='feature_tracks' style=\"top:0px;\" > " + name + "</div>"
    );

    jQuery(function () {
        jQuery("#upload"+count + "_wrapper").resizable({
            handles: "s",
            minHeight: "50px",
            borderBottom: '1px solid black'
        });
    });

    jQuery("#tracklist").append("<p title='upload' id=upload"+count+"check><input type=\"checkbox\" checked id='upload"+count+"Checkbox' name='upload"+count+"Checkbox' onClick=loadTrackAjax(\"upload"+count+"\",\"upload"+count+"\");\>  Upload track\  </p>");

    jQuery("#mergetracklist").append("<span id=upload"+count+"trackspan> <input type=\"checkbox\" id='upload"+count+"mergedCheckbox' name='upload"+count+"mergedCheckbox' onClick=mergeTrack(\"upload"+count+"\"); value=upload >Upload Track</span>");

    window["upload"+count] = processData(window["gem"])

    window['track_listupload'+count] = {
        name: upload+count,
        id: "noid"+count,
        display_label: name,
        desc: "uploaded file:"+name,
        disp: 1,
        label_show: true
    }

    if(extension.indexOf("csv") >= 0){
        readGem("upload"+count, "noid"+count, "#upload"+count+"_div")
    }

    // If there's a file left to load
    if (i < files.length - 1) {
        setup_reader(files, i + 1);
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