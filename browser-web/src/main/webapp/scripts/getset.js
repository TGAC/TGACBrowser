/**
 * Created by IntelliJ IDEA.
 * User: thankia
 * Date: 2/17/12
 * Time: 4:05 PM
 * To change this template use File | Settings | File Templates.
 */

// API type functions to make generous coding
function getDragableLeft() {
  return jQuery("#draggable").position().left;
}

function setDragableLeft(left) {
  jQuery("#draggable").animate({"left": left}, 100, function () {
    setNavPanel()
  });
}

function getDragableWidth() {
  return jQuery("#draggable").css("width");
}

function setDragableWidth(width) {
  jQuery("#draggable").animate({"width": width}, 100, function () {
    setNavPanel()
  });
  var diff = parseInt((getEnd() - getBegin()) * 0.90);
  var bp = "bp";
  if (diff > 100000000) {
    diff = (diff / 1000000);
    bp = "Gbp";
  }
  else if (diff > 1000000) {
    diff = (diff / 1000000);
    bp = "Mbp";
  }
  else if (diff > 1000) {
    diff = diff / 1000;
    bp = "Kbp";
  }

  jQuery("#leftbig").attr('title', "Move Left(" + diff + "" + bp + ")");
  jQuery("#rightbig").attr('title', "Move Right(" + diff + "" + bp + ")");
}

function getbglayerWidth() {
  return jQuery("#bg_layer").css("width");
}

function setbglayerWidth(width, seq) {
//  to call disp seq function if left is 0 then it doesn't call so
  if (seq == true) {
    jQuery("#bg_layer").animate({"width": width}, 100, function () {
      dispSeq();
    });
  }
  else {
    jQuery("#bg_layer").animate({"width": width}, { duration: 100, queue: false});
  }
}

function getbglayerLeft() {
  return jQuery("#bg_layer").position().left;
}

function setbglayerLeft(left, seq) {
  var diff = parseFloat(left) - parseFloat(getbglayerLeft());
  if (parseInt(diff) >= 1 || parseInt(diff) <= -1) {
    if (seq == true) {
      jQuery("#bg_layer").animate({"left": left}, 100, function () {
        dispSeq();
      });
    }
    else {
      jQuery("#bg_layer").animate({"left": left}, { duration: 100, queue: false});
    }
  }
  else {
    setDragableLeft(parseFloat(getbglayerLeft()));

  }

}

function getBegin() {
  if (parseInt(jQuery("#begin").val()) > 0) {
    return jQuery("#begin").val();
  }
  else {
    return 1;
  }
}

function setBegin(begin) {
  if (parseInt(begin) > 0) {
    jQuery("#begin").val(parseInt(begin));
  }
  else {
    jQuery("#begin").val(parseInt(1));
  }
  jQuery("#begin").size(begin.length);
}

function getEnd() {
  if (parseInt(jQuery("#end").val()) <= sequencelength) {
    return jQuery("#end").val();
  }
  else {
    return sequencelength;
  }
}

function setEnd(end) {
  if (parseInt(end) <= sequencelength) {
    jQuery("#end").val(parseInt(end));
  }
  else {
    jQuery("#end").val(sequencelength);
  }
  jQuery("#end").size(end.length);

}
