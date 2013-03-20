<%@ include file="header.jsp" %>

<style type="text/css">
    .css_def {
        position: absolute;
    }

</style>


<%--<h1 id="seqnameh1">TGAC Browser</h1>--%>


<div id="sessioninput">
    <input type="text" id="session" value=""/>
    <button class="ui-state-default ui-corner-all" onclick="loadSession(jQuery('#session').val());">Session ID
    </button>  <p>
    <input type="file"  name="sessionfile"  id="sessionfile" size="10">
    <input type='button' value='upload' onclick="fileupload();">
    </p>
</div>


<center>

    <div id="mainsearch" style="top : 10px ; height: 800px; "></div>

</center>


<script type="text/javascript">


    jQuery(document).ready(function() {
        metaData();
        jQuery("#mainsearch").load("browser.jsp", function() {
            onLoad();
            getUrlVariables();
        });
    });


    function getUrlVariables() {
        var vars = [];
        var parts = window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi, function(m, key, value) {
            vars.push(value);

        });
        if (vars.length > 0) {
            jQuery("#session").val(vars[0]);
            loadSession(jQuery('#session').val());
        <%--vars[1] for blast and so on --%>
        }
    }


</script>


<%@ include file="footer.jsp" %>

