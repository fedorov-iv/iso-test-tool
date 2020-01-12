var webSocket = new WebSocket('ws://s-msk-t-cp-aft:8080/projects/websocketrun');

webSocket.onopen = function(event) {
    console.log("Websocket connection established");
};



var scenarioResultsComponent = new Vue({
		el: '#scenarioResults',
		data: {
			 resultSteps: [],
			 errors:[]


		},
		methods:{

		    run: function(scenario_id){

		        var self = this;
		        var scenarioId = $("#scenario_id").val();
		        self.resultSteps = [];
		        self.errors = [];

		        var scenarioData = {scenario_id: scenarioId};

		        webSocket.onmessage = function(event) {
                    self.resultSteps.push(JSON.parse(event.data));
                };

                webSocket.onerror = function(event) {
                    self.errors.push(event.data);
                };

		        webSocket.send(JSON.stringify(scenarioData));


		    }

		}

});