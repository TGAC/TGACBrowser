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


    jQuery(document).ready(function () {
        var chr = metaData();

        jQuery("#mainsearch").load("browser.jsp", function () {
            onLoad();
            getUrlVariables(chr);
        });
    });

    function getUrlVars() {
        var vars = {};
        var parts = window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi, function (m, key, value) {
            vars[key] = value;
        });
        return vars;
    }
    function getUrlVariables(chr) {

        jQuery.urlParam = function (name) {
            var results = new RegExp('[\\?&]' + name + '=([^&#]*)').exec(window.location.href);
            if (results == null) {
                return null;
            }
            else {
                return results[1] || 0;
            }
        }

        processURL(jQuery.urlParam)

    }


</script>


<%@ include file="footer.jsp" %>

