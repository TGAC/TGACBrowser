/**
 * Created by IntelliJ IDEA.
 * User: thankia
 * Date: 2/17/12
 * Time: 4:21 PM
 * To change this template use File | Settings | File Templates.
 */
function dispSeq() {
  var len = sequencelength;
  maxLen = jQuery("#canvas").css("width");
  var width = getbglayerWidth();
  var left = getbglayerLeft();


  var seqStart, seqEnd;
  seqStart = parseFloat(left) * len / parseFloat(maxLen);
  seqEnd = seqStart + parseFloat(width) * len / parseFloat(maxLen);
  seqEnd = Math.round(parseFloat(seqEnd));
  removeAllPopup();
//  removeDragPopup();
//  removeBlastPopup();
  seqBar(seqStart, seqEnd);
  dispCoord(seqStart, seqEnd);
  browser_coordinates();
  setNavPanel();
//  trackToggle("all");
  updateJSON();
}

function changeSeq(begin, end) {
  updateJSON();
  seqBar(begin, end);
  browser_coordinates();
//  trackToggle("all");
}

function seqBar(seqStart, seqEnd) {

  var temp = seqEnd - seqStart;
  seqLen = visualLength(temp);
  if (parseFloat(seqLen) <= (parseFloat(maxLen))) {
    selectionStart = seqStart;
    selectionEnd = seqEnd;
    var diff = (parseFloat(maxLen)) - parseFloat(seqLen);
    var newlength = diff * temp / seqLen;
    newStart = parseInt((parseFloat(seqStart) - parseFloat(newlength) / 2));
    newEnd = parseInt((parseFloat(seqEnd) + parseFloat(newlength) / 2));

    if (newStart < 0) {
      newEnd = newEnd + (-parseInt(newStart));
      newStart = 0;
    }
    if (newEnd > sequencelength) {
      newStart = newStart - (newEnd - sequencelength);
      newEnd = sequencelength;
    }

    var partial = (parseInt(getEnd()) - parseInt(getBegin())) / 2;
    var start = parseInt(getBegin()) - parseInt(partial)
    var end = parseInt(getEnd()) + parseInt(partial);

    Fluxion.doAjax(
            'dnaSequenceService',
            'loadSequence',
            {'query': seqregname, 'from': start, 'to': end, 'url': ajaxurl},
            {'doOnSuccess': function (json) {
              var seq = json.seq;
              if (seq.length > 1) {
                temp = seq;
                temp = "<font style='Courier New'>" + stringColour(temp);
                jQuery('#sequenceString').html(temp);
                translate(seq);
              }
              else {
                jQuery("#translation_div").hide();
                jQuery("#sequence").css('height', '20px');
                jQuery("#tracks").css('top', '20px');
                jQuery('#sequenceString').html("<hr id = \"seqbar\" style='background-color: silver; z-index: 999'>");
              }
            }
            });
  }
  else {

    selectionStart = seqStart;
    selectionEnd = seqEnd;
    seqLen = maxLen;
    newStart = seqStart;
    newEnd = seqEnd;

    jQuery("#translation_div").hide();
    jQuery("#sequence").css('height', '20px');
//    jQuery("#wrapper").css('top', '190px');
    jQuery("#tracks").css('top', '20px');


//    }
//    else {
//      jQuery("#translation_div").html("");
//      jQuery("#translation_div").hide();
//
//    }
    jQuery('#sequenceString').html("<hr id = \"seqbar\" style='background-color: silver; z-index: 999'>");
//    jQuery('#sequenceString').html("<hr id = \"seqbar\" style='background-color: silver; z-index: 999'>");

  }
}

function jumpToSeq() {
  var begin = getBegin();
  var end = getEnd();

  var len = sequencelength;
  if (parseInt(begin) < 0) {
    begin = 0;
    setBegin(0);
  }
  if (parseInt(end) > len) {
    end = len;
    setEnd(len);
  }
  if (parseInt(begin) >= parseInt(end)) {
    alert("Ending position need to be bigger than Starting position");
  }
  else {
    if ((parseInt(end) - parseInt(begin)) < minWidth) {
      var diff = minWidth - (parseInt(end) - parseInt(begin));

      var tempbegin = parseInt(getBegin() - (diff / 2))
      var tempend = parseInt(parseInt(getEnd()) + parseInt(diff / 2))
      if (parseInt(tempbegin) < 0) {
        tempend = tempend + (-(tempbegin));
        tempbegin = 0;
      }
      if (parseInt(tempend) > len) {
        tempbegin = tempbegin - (tempend - len);
        tempend = len;
      }

      setBegin(tempbegin);
      setEnd(tempend);
    }

    var begin = getBegin();
    var end = getEnd();
    var seqStart = parseInt(begin) * parseInt(maxLen) / len;
    var seqEnd = parseInt(end) * parseInt(maxLen) / len;
    var width = parseFloat(seqEnd) - parseFloat(seqStart);
    removeAllPopup();
//    removeBlastPopup();
//    removeDragPopup();
    setDragableLeft(seqStart);
    setDragableWidth(width);
    setbglayerLeft(seqStart, false);
    setbglayerWidth(width);

    changeSeq(begin, end);
  }

}

function translate(sequence) {

  var space;
  var ptn_seq = "";

  for (var j = 0; j < 3; j++) {

    var seq = sequence.substring(j, sequence.length);
    space = (parseFloat(maxLen) - (visualLength(seq.length) / 3)) / parseFloat(seq.length / 3);


    if (j == 1) {
      ptn_seq += "&nbsp;";
    }
    else if (j == 2) {
      ptn_seq += "&nbsp;&nbsp;";
    }
    var i = 0;
    for (i; i <= seq.length - 3; i = i + 3) {
      var chunk = seq.substring(i, i + 3);
//        console.log(i+":"+j+":"+chunk);
      ptn_seq += "&nbsp;";
      if (chunk.indexOf("N") > -1) {
        ptn_seq += "X";
      }
      else if (chunk == "GCT" || chunk == "GCC" || chunk == "GCA" || chunk == "GCG") {
        ptn_seq += "A";
      }
//    CGU, CGC, CGA, CGG, AGA, AGG
      else if (chunk == "CGT" || chunk == "CGC" || chunk == "CGA" || chunk == "CGG" || chunk == "AGA" || chunk == "AGG") {
        ptn_seq += "R";
      }
//    AAU, AAC
      else if (chunk == "AAT" || chunk == "AAC") {
        ptn_seq += "N";
      }
//    GAU, GAC
      else if (chunk == "GAT" || chunk == "GAC") {
        ptn_seq += "D";
      }
//    UGU, UGC
      else if (chunk == "TGT" || chunk == "TGC") {
        ptn_seq += "C";
      }
//    CAA, CAG
      else if (chunk == "CAA" || chunk == "CAG") {
        ptn_seq += "Q";
      }
//    GAA, GAG
      else if (chunk == "GAA" || chunk == "GAG") {
        ptn_seq += "E";
      }
//      GGU, GGC, GGA, GGG
      else if (chunk == "GGT" || chunk == "GGC" || chunk == "GGA" || chunk == "GGG") {
        ptn_seq += "G";
      }
//    CAU, CAC
      else if (chunk == "CAT" || chunk == "CAC") {
        ptn_seq += "H";
      }
//      AUU, AUC, AUA
      else if (chunk == "ATT" || chunk == "ATC" || chunk == "ATA") {
        ptn_seq += "I";
      }
//     AUG
      else if (chunk == "ATG") {
        ptn_seq += "M";
      }
//     UUA, UUG, CUU, CUC, CUA, CUG
      else if (chunk == "TTA" || chunk == "TTG" || chunk == "CTT" || chunk == "CTC" || chunk == "CTA" || chunk == "CTG") {
        ptn_seq += "L";
      }
//         AAA, AAG
      else if (chunk == "AAA" || chunk == "AAG") {
        ptn_seq += "K";
      }
//    UUU, UUC
      else if (chunk == "TTT" || chunk == "TTC") {
        ptn_seq += "F";
      }
      //    CCU, CCC, CCA, CCG
      else if (chunk == "CCT" || chunk == "CCC" || chunk == "CCA" || chunk == "CCG") {
        ptn_seq += "P";
      }
      //  UCU, UCC, UCA, UCG, AGU, AGC
      else if (chunk == "TCT" || chunk == "TCC" || chunk == "TCA" || chunk == "TCG" || chunk == "AGT" || chunk == "AGC") {
        ptn_seq += "S";
      }
      //      ACU, ACC, ACA, ACG
      else if (chunk == "ACT" || chunk == "ACC" || chunk == "ACA" || chunk == "ACG") {
        ptn_seq += "T";
      }
      //      UGG
      else if (chunk == "TGG") {
        ptn_seq += "W";
      }
//    UAU, UAC
      else if (chunk == "TAT" || chunk == "TAC") {
        ptn_seq += "Y";
      }
      //   GUU, GUC, GUA, GUG
      else if (chunk == "GTT" || chunk == "GTC" || chunk == "GTA" || chunk == "GTG") {
        ptn_seq += "V";
      }
      //  	UAA, UGA, UAG
      else if (chunk == "TAA" || chunk == "TGA" || chunk == "TAG") {
        ptn_seq += "*";
      }

      else {
        ptn_seq += "-";
      }
      ptn_seq += "&nbsp;";
    }

    ptn_seq += "<br>";
  }
  maxLen - visualLength(ptn_seq.length)


  if (space >= 0) {
    jQuery("#translation_div").show();
//        jQuery("#wrapper").css('top', '230px');
    jQuery("#tracks").css('top', '70px');
    jQuery("#sequence").css('height', '70px');
//      jQuery("#translation_div").css('letter-spacing', space);
    jQuery("#translation_div").html("<br>" + ptn_seq);
  }
  else {
    jQuery("#translation_div").html("");
    jQuery("#translation_div").hide;
    jQuery("#sequence").css('height', '20px');
    jQuery("#tracks").css('top', '20px');
//        jQuery("#wrapper").css('top', '190px');
  }


}

