<%@ include file="header.jsp" %>

<style type="text/css">
    .css_def {
        position: absolute;
    }

</style>

<center>

    <div id="mainsearch" style="top : 10px ;"></div>

</center>

<script type="text/javascript">


    jQuery(document).ready(function() {
        var chr = metaData();

        jQuery("#mainsearch").load("browser.jsp", function() {
            onLoad();
            getUrlVariables(chr);
        });
    });

    function getUrlVars() {
        var vars = {};
        var parts = window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi, function(m, key, value) {
            vars[key] = value;
        });
        return vars;
    }
    function getUrlVariables(chr) {
        var showBlast = false;
        var vars = [];
        var parts = window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi, function(m, key, value) {
            vars.push(value);
            if (key == "blasttrack") {
                showBlast = true;
            }
        });

        if (vars.length > 0) {

            jQuery("#search").val(vars[0]);

            seqregionSearchPopup(jQuery("#search").val(), vars[1], vars[2], vars[3])

        }
        else if (jQuery('#search').val().length >= 1) {
            search(jQuery('#search').val(), oldTracklist);
        }
        else if(chr == true){
             getReferences(true)
        }
        else {
          console.log("else")
        }
    }


</script>


<%@ include file="footer.jsp" %>

