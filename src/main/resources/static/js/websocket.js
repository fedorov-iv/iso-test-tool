
 $(function () {
                  $('[data-toggle="tooltip"]').tooltip();
               });

document.getElementById("runProjectWebsocketButton").addEventListener("click", start, false);

var webSocket = new WebSocket('ws://s-msk-t-cp-aft:8080/projects/websocketrun');

webSocket.onerror = function(event) {
    $("#messages").append("<samp style='color:red'>Error</samp>");
    $("#messages").append("<samp style='color:red'>===================================</samp>");
    $("#messages").append("<samp style='color:red'>"+event.data+"</samp>");
};
webSocket.onopen = function(event) {
    console.log("Websocket connection established");
};
webSocket.onmessage = function(event) {

        //$("#messages").append('<samp>' +htmlEntities(event.data)+ '</samp>');

        response = JSON.parse(event.data);

      for(key in response){

            if(key == "message" || key == "fields"){
                $("#messages").append("<samp style='color:green'>" + response[key]+ "</samp>");
                $("#messages").append("<samp style='color:green'>===================================</samp>");
                continue;
            }

            if(key == "finished"){
                 $("#messages").append("<samp style='color:blue'>===================================</samp>");
                 $("#messages").append("<samp style='color:blue'>Finished</samp>");
                 continue;
            }

            if(key == "error"){
                $("#messages").append("<samp style='color:red'>Error</samp>");
                $("#messages").append("<samp style='color:red'>===================================</samp>");
                $("#messages").append("<samp style='color:red'>"+response[key]+"</samp>");
                continue;
            }

            $("#messages").append('<samp><b>' + key + '</b>: ' + response[key]+ '</samp>');
      }
};


function htmlEntities(input){
     return input.replace(/[\u00A0-\u9999<>\&]/gim, function(i) {
       return '&#'+i.charCodeAt(0)+';';
    });
}


function start() {
    $("#messages").html("");


     var projectData = {
            name: $("#name").val(),
            channelId : $("#channelId").val(),
            cardId : $("#cardId").val(),
            terminalId : $("#terminalId").val(),
            attributes: projectAttributes
        }

       $("#messages").append("<samp style='color:green'>Running " + projectData.name + "</samp>");
       $("#messages").append("<samp style='color:green'>===================================</samp>");

     webSocket.send(JSON.stringify(projectData));
     return false;
 }