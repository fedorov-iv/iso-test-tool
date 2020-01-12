var runProjectButton = document.getElementById("runProjectButton");
runProjectButton.addEventListener("click", runProject, false);

function runProject(){

    var projectData = {
        name: $("#name").val(),
        channelId : $("#channelId").val(),
        cardId : $("#cardId").val(),
        terminalId : $("#terminalId").val(),
        attributes: projectAttributes
    }

    $("#messages").html("");
    $("#runProjectButton").addClass("disabled");
    $("#messages").append("<samp style='color:green'>Running " + projectData.name + "</samp>")
    $("#messages").append("<samp style='color:green'>===================================</samp>")


     $.ajax({
       type: "POST",
       url: "/projects/run",
       contentType : 'application/json',
       data: JSON.stringify(projectData),
       success: function(data){


               for(key in data){
                    $("#messages").append("<samp><b>" + key + "</b>: " + data[key] + "</samp>");
               }

       },

       error: function(xhr, status, error){
            $("#messages").append("<samp style='color:red'>Error</samp>")
            $("#messages").append("<samp style='color:red'>===================================</samp>")
            $("#messages").append("<samp style='color:red'>"+xhr.responseText+"</samp>");

       },

       complete: function(data){
            $("#messages").append("<samp style='color:blue'>===================================</samp>")
            $("#messages").append("<samp style='color:blue'>Finished</samp>")
            $("#runProjectButton").removeClass("disabled");

       },

       dataType: "json"
     });



}