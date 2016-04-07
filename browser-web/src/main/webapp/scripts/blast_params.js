/**
 * Created with IntelliJ IDEA.
 * User: thankia
 * Date: 29/07/2014
 * Time: 11:07
 * To change this template use File | Settings | File Templates.
 */
function setBLASTParams(){
    if (jQuery('#blast_type').val().indexOf('blastn') == 0) {
        jQuery("#blastn_para").show()
        jQuery("#blastp_para").hide()
    }
    else {
        jQuery("#blastn_para").hide()
        jQuery("#blastp_para").show()
    }

    setBLASTPenalty();
    setWordSize()
}


function setBLASTPenalty(){
    var pam30 = [[7,2],[6,2],[5,2],[10,1],[9,1],[8,1],[13,3],[15,3],[14,1],[14,2]];
    var pam70 = [[8,2],[7,2],[6,2],[11,1],[10,1],[9,1],[12,3],[11,2]];
    var pam250 = [[15,3],[14,3],[13,3],[12,3],[11,3],[17,2],[16,2],[15,2],[14,2],[13,2],[21,1],[20,1],[19,1],[18,1],[17,1]];
    var blosum80 = [[8,2],[7,2],[6,2],[11,1],[10,1],[9,1]];
    var blosum45 = [[13,3],[12,3],[11,3],[10,3],[15,2],[14,2],[13,2],[12,2],[19,1],[18,1],[17,1],[16,1]];
    var blosum62 = [[11,2],[10,2],[9,2],[8,2],[7,2],[6,2],[13,1],[12,1],[11,1],[10,1],[9,1]];
    var blosum50 = [[13,3],[12,3],[11,3],[10,3],[9,3],[16,2],[15,2],[14,2],[13,2],[12,2],[19,1],[18,1],[17,1],[16,1],[15,1]];
    var blosum90 = [[9,2],[8,2],[7,2],[6,2],[11,1],[10,1],[9,1]];

    if (jQuery('#matrix').val() == "PAM30") {
        gen_penalties(pam30)
    }
    else if (jQuery('#matrix').val() == "PAM70") {
        gen_penalties(pam70)
    }   else if (jQuery('#matrix').val() == "PAM250") {
        gen_penalties(pam250)
    }else if (jQuery('#matrix').val() == "BLOSUM80") {
        gen_penalties(blosum80)
    }else if (jQuery('#matrix').val() == "bLOSUM45") {
        gen_penalties(blosum45)
    }else if (jQuery('#matrix').val() == "BLOSUM62") {
        gen_penalties(blosum62)
    }else if (jQuery('#matrix').val() == "BLOSUM50") {
        gen_penalties(blosum50)
    }else if (jQuery('#matrix').val() == "BLOSUM90") {
        gen_penalties(blosum90)
    }
    else  {
        alert("else")
    }
}

function gen_penalties(penalties){
    var penalty_string = "<select name='penalty' id='penalty'>"
    for(var i=0; i<penalties.length; i++){
        penalty_string += "<option value='"+penalties[i]+"'> Existence: "+penalties[i][0]+" Extension: "+penalties[i][1]+"</option>";
    }
    penalty_string += "</select>";
        jQuery("#penalty_div").html(penalty_string)
}

function setWordSize(){
    var blastn = [16,20,24,28,32,48,64,128,256];
    var blastp = [3,2];

    if (jQuery('#blast_type').val().indexOf('blastn') == 0) {
        gen_wordsize(blastn)
    }
    else {
        gen_wordsize(blastp)
    }
}

function gen_wordsize(word_size){
    var word_string = "<select name='word_size' id='word_size'>"
    for(var i=0; i<word_size.length; i++){
        word_string += "<option value='"+word_size[i]+"'> "+word_size[i]+"</option>";
    }
    word_string += "</select>";

    jQuery("#word_size_div").html(word_string)

}

function toogleParams(){
    if(jQuery("#short_seq").attr('checked')){
        jQuery("#word_size").attr("disabled", true)
        jQuery("#gap_cost").attr("disabled", true)
        jQuery("#match-mismatch").attr("disabled", true)
        jQuery("#penalty").attr("disabled", true)
        jQuery("#matrix").attr("disabled", true)
        jQuery("#filter").attr("disabled", true)

    }else{
        jQuery("#word_size").attr("disabled", false)
        jQuery("#gap_cost").attr("disabled", false)
        jQuery("#match-mismatch").attr("disabled", false)
        jQuery("#penalty").attr("disabled", false)
        jQuery("#matrix").attr("disabled", false)
        jQuery("#filter").attr("disabled", false)

    }
}