<%@ include file="header.jsp" %>

<style type="text/css">
    .css_def {
        position: absolute;
    }

</style>

<%--<div id="searchseqregioninput">--%>
<%--<input type="text" id="search" value="scaffold1.1-size1749886"/>--%>
<%--<button class="ui-state-default ui-corner-all" onclick="search(jQuery('#search').val());">Search Seq Region Name--%>
<%--</button>--%>
<%--</div>--%>


<center>

    <div id="mainsearch" style="top : 10px ;"></div>

</center>

<script type="text/javascript">


    jQuery(document).ready(function() {
        metaData();
        jQuery("#mainsearch").load("browser.jsp", function() {
            onLoad();
            getUrlVariables();
//            var first = getUrlVars()["query"];
//var second = getUrlVars()["blast"];
            <%----%>
//alert(first);
//alert(second);
        });
    });

    function getUrlVars() {
        var vars = {};
        var parts = window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi, function(m, key, value) {
            vars[key] = value;
        });
        return vars;
    }
    function getUrlVariables() {
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
        else if (jQuery('#search').val().length > 1) {
            var now = new Date();


            search(jQuery('#search').val(), oldTracklist);
        <%--vars[1] for blast and so on --%>
        }

//        if(showBlast){
//            loadPreBlast(vars[3], vars[0]);
//        }
    }


</script>


<%@ include file="footer.jsp" %>

