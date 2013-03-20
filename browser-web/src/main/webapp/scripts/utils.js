/**
 * Created by IntelliJ IDEA.
 * User: thankia
 * Date: 2/17/12
 * Time: 4:15 PM
 * To change this template use File | Settings | File Templates.
 */

function stringColour(temp) {
  var letters = temp.split('');
  var newSeq = "";
  for (var i = 0; i < letters.length; i++) {
    if (letters[i] == 'A') {
      newSeq += '<span style="color:#ff8c00;">' + letters[i] + '</span>';
    }
    else if (letters[i] == 'C') {
      newSeq += '<span style="color:green; ">' + letters[i] + '</span>';
    }
    else if (letters[i] == 'G') {
      newSeq += '<span style="color:blue;">' + letters[i] + '</span>';
    }
    else if (letters[i] == 'T') {
      newSeq += '<span style="color:red; ;">' + letters[i] + '</span>';
    }
    else {
      newSeq += '<span style="color:black; ">' + letters[i] + '</span>';
    }
  }
  return newSeq;
}

function visualLength(temp) {
  var ruler = $("ruler");
  var inLength = 0;
  var tempStr = "";
  ruler.innerHTML = "N";
  inLength = ruler.offsetWidth * temp;
  return inLength;
  /*
   if ((temp / 10000) >= 1) {
   for (var i = 0; i < 10000; i++) {
   tempStr += "N";
   }
   ruler.innerHTML = tempStr;
   inLength += ruler.offsetWidth;
   inLength = inLength * (temp / 10000)
   }

   ruler.innerHTML = "";
   tempStr = "";

   for (var i = 0; i < temp % 10000; i++) {
   tempStr += "N";
   }

   ruler.innerHTML = tempStr;
   inLength += ruler.offsetWidth;
   ruler.innerHTML = "";
   return inLength;*/
}

function findminwidth() {
  maxLen = jQuery("#canvas").css("width");
  var len = visualLength(sequencelength);
  var seqWidth = parseFloat(maxLen) * sequencelength / parseFloat(len);
  deltaWidth = parseInt(sequencelength) * 2 / parseInt(maxLen);
  return parseInt(seqWidth);
}
function browser_coordinates() {

  var temp = "<FONT style=\"BACKGROUND-COLOR: #d3d3d3\">";
  jQuery("#vertical0").html(temp + Math.round(getBegin()));
  jQuery("#vertical1").html(temp + Math.round(parseFloat(getBegin()) + parseFloat((getEnd() - getBegin()) * 0.1)));
  jQuery("#vertical2").html(temp + Math.round(parseFloat(getBegin()) + parseFloat((getEnd() - getBegin()) * 0.2)));
  jQuery("#vertical3").html(temp + Math.round(parseFloat(getBegin()) + parseFloat((getEnd() - getBegin()) * 0.3)));
  jQuery("#vertical4").html(temp + Math.round(parseFloat(getBegin()) + parseFloat((getEnd() - getBegin()) * 0.4)));
  jQuery("#vertical5").html(temp + Math.round(parseFloat(getBegin()) + parseFloat((getEnd() - getBegin()) * 0.5)));
  jQuery("#vertical6").html(temp + Math.round(parseFloat(getBegin()) + parseFloat((getEnd() - getBegin()) * 0.6)));
  jQuery("#vertical7").html(temp + Math.round(parseFloat(getBegin()) + parseFloat((getEnd() - getBegin()) * 0.7)));
  jQuery("#vertical8").html(temp + Math.round(parseFloat(getBegin()) + parseFloat((getEnd() - getBegin()) * 0.8)));
  jQuery("#vertical9").html(temp + Math.round(parseFloat(getBegin()) + parseFloat((getEnd() - getBegin()) * 0.9)));
  jQuery("#vertical10").html(temp + Math.round(parseFloat(getBegin()) + parseFloat((getEnd() - getBegin()))));
}

function trackToggle(trackname) {
console.log(trackname)
  var index = 0;
  var graph = "false";
var trackid = "";
  layers = jQuery("#rowoftracks").val();
  for (var i = 0; i < track_list.length; i++) {
    if (track_list[i].name == trackname) {
      index = i;
      trackid = track_list[i].id;
    }
    if (track_list[i].name == trackname && track_list[i].graph == "true") {
      graph = "true";
    }
  }
  if (trackname == "all") {
    jQuery("#mergedtrack").html("<div id= \"mergelabel\" align='left' class='handle'></div>");
    for (var i = 0; i < track_list.length; i++) {
      if (jQuery("#" + track_list[i].name + "Checkbox").is(':checked')) {
        if (track_list[i].graph == "true") {
          dispGraph("#" + track_list[i].name + "_div", track_list[i].name)
        }
        else if (track_list[i].name.toLowerCase().indexOf("blasttrack") >= 0) {
          dispBLAST("#" + track_list[i].name + "_div", 'blasttrack');
        }
        else if (track_list[i].name.toLowerCase().indexOf("gene") >= 0) {
          dispGenes("#" + track_list[i].name + "_div", track_list[i].name, track_list[i].expand);
        }
        else if (track_list[i].name.toLowerCase().indexOf("wig") >= 0) {
          dispGraphWig("#" + track_list[i].name + "_div", track_list[i].name, trackid);
        }
        else {
          dispTrack("#" + track_list[i].name + "_div", track_list[i].name);
        }
      }
    }
  }
  else {
    if (jQuery('#' + trackname + 'Checkbox').is(':checked')) {

      if (graph == "true") {
        dispGraph("#" + trackname + "_div", trackname);
      }
      else if (trackname.toLowerCase().indexOf("blasttrack") >= 0) {
        dispBLAST("#" + trackname + "_div", 'blasttrack');
      }
      else if (trackname.toLowerCase().indexOf("gene") >= 0) {
        dispGenes("#" + trackname + "_div", trackname, track_list[index].expand);
      }
      else if (trackname.toLowerCase().indexOf("wig") >= 0) { 
        dispGraphWig("#" + trackname + "_div", trackname, trackid);
      }
      else {
        dispTrack("#" + trackname + "_div", trackname);
      }
    }
    else {
      jQuery("#" + trackname + "_wrapper").fadeOut();
    }

  }
  if (jQuery("#mergedtrack").text() == "") {
    jQuery("#mergedtrack").fadeOut();
    jQuery("#mergedtrack").html();
  }
}

function mergeTrack(check) {
  jQuery(track_list).each(function (index) {
    //this is the object in the array, index is the index of the object in the array

    if (jQuery("#" + track_list[index].name + "mergedCheckbox").attr('checked')) {//
      this.merge = 1;
    }
    else {
      this.merge = 0;
    }
  });

  merged_track_list = "";
  jQuery("#mergedtrack").html("<div id= \"mergelabel\" align='left' class='handle'></div>");
  jQuery('#mergetracklist input:checked').each(function () {
    var track = jQuery(this).attr('name').replace("mergedCheckbox", "");
    merged_track_list += track + ", ";
    if (jQuery('#' + track + 'Checkbox').is(':checked')) {
      trackToggle(track);
    }
  });
  if (!jQuery('input[name=' + check + 'mergedCheckbox]').is(':checked')) {
    trackToggle(check);
  }
}

//remove tracklist when reload
function removeTrackslist(trackList) {

  for (var i = 0; i < trackList.length; i++) {
    delete window[trackList[i].name];
  }
  jQuery("#mergetracklist").html("");
}
//
//function controls() {
////  jQuery("#slider").fadeIn();
//}

function toggleLeftInfo(div, id) {
  if (jQuery(div).hasClass("toggleLeft")) {
    jQuery(div).removeClass("toggleLeft").addClass("toggleLeftDown");
  }
  else {
    jQuery(div).removeClass("toggleLeftDown").addClass("toggleLeft");
  }
  jQuery("#" + id).toggle("blind", {}, 500);
}

function groupTogether() {
  var trackid;
  jQuery(window[grouptrack]).each(function (index) {

    if (window[grouptrack][index].id == grouplastid) {
      trackid = index;
    }
  });

  var a = 0;
  for (var j = 0; j < window[grouptrack][trackid].transcript.length; j++) {
    if (jQuery.inArray(window[grouptrack][trackid].transcript[j].id, grouplist) > -1) {
      window[grouptrack][trackid].transcript[j].layer = jQuery.inArray(window[grouptrack][trackid].transcript[j].id, grouplist) + 1;
    }
  }

  for (var j = 0; j < window[grouptrack][trackid].transcript.length; j++) {
    if (jQuery.inArray(window[grouptrack][trackid].transcript[j].id, grouplist) < 0) {
      window[grouptrack][trackid].transcript[j].layer = grouplist.length + a + 1;
      a += 1;
    }
  }
  function SortByLayer(x, y) {
    return ((x.layer == y.layer) ? 0 : ((x.layer > y.layer) ? 1 : -1 ));
  }

  // Call Sort By Name
  window[grouptrack][trackid].transcript.sort(SortByLayer);
  trackToggle(grouptrack);
  jQuery("#makegroup").hide();
  ctrldown = false;
  grouplist = [];
  grouplastid = null;
  grouptrack = null;


}

function groupCancel() {
  trackToggle(grouptrack);
  jQuery("#makegroup").hide();
  ctrldown = false;
  grouplist = [];
  grouplastid = null;
  grouptrack = null;

}

function stringTrim(string, width) {
  var ruler = jQuery("#ruler");
  var inLength = 0;
  var tempStr = "";

  jQuery("#ruler").html(string);
  inLength = jQuery("#ruler").width();


  if (inLength < width) {
    return string;
  }
  else {
    return "<span title=" + string + ">" + string.substring(0, width - 6) + "... </span>";
  }
}

function findAndRemove(array, property, value) {
  jQuery.each(array, function (index, result) {
    if (result[property] == value) {
      //Remove from array
      array.splice(index, 1);
    }
  });
}