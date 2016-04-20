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

    var track_list_length = track_list.length

    console.log("track_list_length "+track_list_length)

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


    //var label = "<div align='left' class='handle'>" +
    //    "<table>" +
    //    "<tbody>" +
    //    "<tr>" +
    //    "<td><b>"+name+"</b></td>" +
    //    "<td><div onclick='removeTrack(\"uploadManhattan"+count+"\",\"uploadManhattan"+count+"\");' class='closehandle ui-icon ui-icon-close'></div></td>" +
    //    "</tr>" +
    //    "</tbody>" +
    //    "</table>" +
    //    "</div>";
    //
    //
    //jQuery("#tracks").append("<div id='uploadManhattan"+count + "_wrapper' class='feature_tracks' style=\" max-height:150px; overflow-x: hidden;\">" +
    //    "</div>");
    //
    //jQuery("#uploadManhattan"+count + "_wrapper").append(label +
    //    "<div id='uploadManhattan"+count + "_div' class='feature_tracks' style=\"top:0px;\" > " + name + "</div>"
    //);
    //
    //jQuery(function () {
    //    jQuery("#uploadManhattan"+count + "_wrapper").resizable({
    //        handles: "s",
    //        minHeight: "50px",
    //        borderBottom: '1px solid black'
    //    });
    //});
    //
    //jQuery("#tracklist").append("<p title='uploadManhattan' id=uploadManhattan"+count+"check><input type=\"checkbox\" checked id='uploadManhattan"+count+"Checkbox' name='uploadManhattan"+count+"Checkbox' onClick=loadTrackAjax(\"uploadManhattan"+count+"\",\"uploadManhattan"+count+"\");\>  Upload track\  </p>");
    //
    //jQuery("#mergetracklist").append("<span id=uploadManhattan"+count+"trackspan> <input type=\"checkbox\" id='uploadManhattan"+count+"mergedCheckbox' name='uploadManhattan"+count+"mergedCheckbox' onClick=mergeTrack(\"uploadManhattan"+count+"\"); value=uploadManhattan >Upload Track</span>");

    window["uploadManhattan"+count] = processData(window["gem"])

    //window['track_listuploadManhattan'+count] = {
    //    name: "uploadManhattan"+count,
    //    id: "noid"+count,
    //    display_label: name,
    //    desc: "uploaded file:"+name,
    //    disp: 1,
    //    label_show: true
    //}

    if(extension.indexOf("csv") >= 0 && name.toLowerCase().indexOf("gapit") >= 0){
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
            graph: 0,
            graphtype: null,
            label_show: true,
            web:{colour:"red",source : "file", trackgroup: i}
        });

        trackList(track_list, track_list_length)

        // var label = "<div align='left' class='handle'>" +
        //     "<table>" +
        //     "<tbody>" +
        //     "<tr>" +
        //     "<td><b>"+name+"genes</b></td>" +
        //     "<td><div onclick='removeTrack(\"uploadCDS"+count+"\",\"uploadCDS"+count+"\");' class='closehandle ui-icon ui-icon-close'></div></td>" +
        //     "</tr>" +
        //     "</tbody>" +
        //     "</table>" +
        //     "</div>";


        // jQuery("#tracks").append("<div id='uploadCDS"+count + "_wrapper' class='feature_tracks' style=\" max-height:150px; overflow-x: hidden;\">" +
        //     "</div>");

        // jQuery("#uploadCDS"+count + "_wrapper").append(label +
        //     "<div id='uploadCDS"+count + "_div' class='feature_tracks' style=\"top:0px;\" > " + name + "</div>"
        // );

        // jQuery(function () {
        //     jQuery("#uploadCDS"+count + "_wrapper").resizable({
        //         handles: "s",
        //         minHeight: "50px",
        //         borderBottom: '1px solid black'
        //     });
        // });

        // jQuery("#tracklist").append("<p title='uploadCDS' id=uploadCDS"+count+"check><input type=\"checkbox\" checked id='uploadCDS"+count+"Checkbox' name='uploadCDS"+count+"Checkbox' onClick=loadTrackAjax(\"uploadCDS"+count+"\",\"uploadCDS"+count+"\");\>  Upload track\  </p>");

        // jQuery("#mergetracklist").append("<span id=uploadCDS"+count+"trackspan> <input type=\"checkbox\" id='uploadCDS"+count+"mergedCheckbox' name='uploadCDS"+count+"mergedCheckbox' onClick=mergeTrack(\"uploadCDS"+count+"\"); value=uploadCDS >Upload Track</span>");

        window["uploadCDS"+count] = processData(window["gem"])

        // window['track_listuploadCDS'+count] = {
        //     name: "uploadCDS"+count,
        //     id: "noid"+count,
        //     display_label: name,
        //     desc: "uploaded file:"+name,
        //     disp: 1,
        //     label_show: true
        // }

        // window["uploadCDS"+count] = processData(window["gem"])

        readCDSfromGem("uploadCDS"+count, "noid"+count, "#uploadCDS"+count+"_div")


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