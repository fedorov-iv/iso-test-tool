var attachParameterComponent = new Vue({
		el: '#parameters_div',
		data: {
			allParams: [],
			attachedParams: [],
			paramsToAttach: []
		},
		created: function () {

			var self = this;
			var scenarioId = $("#scenario_id").val();
			$.ajax({
				url: '/scenarios/parameters/all',
				method: 'GET',
				success: function (response) {
					self.allParams = response;
				},
				error: function (error) {
					console.log(error);
				}
			});

			if (scenarioId) {

				$.ajax({
					url: '/scenarios/' + scenarioId + '/parameters/',
					method: 'GET',
					success: function (response) {
						self.attachedParams = response;
					},
					error: function (error) {
						console.log(error);
					}
				});

			}

		},
		methods: {

			removeParameter: function (id) {

				var self = this;
				var scenarioId = $("#scenario_id").val();
				$.ajax({
					url: '/scenarios/' + scenarioId + '/parameters/' + id + '/remove',
					method: 'DELETE',
					success: function (response) {

						$.ajax({
							url: '/scenarios/' + scenarioId + '/parameters/',
							method: 'GET',
							success: function (response) {
								self.attachedParams = response;
							},
							error: function (error) {
								console.log(error);
							}
						});

					},
					error: function (error) {
						console.log(error);
					}
				});

			},

			attachParameters: function (e) {

				var self = this;
				var scenarioId = $("#scenario_id").val();
				$.ajax({
					url: '/scenarios/' + scenarioId + '/parameters',
					method: 'POST',
					data: JSON.stringify(self.paramsToAttach),
					contentType: "application/json",
					success: function (response) {

						$.ajax({
							url: '/scenarios/' + scenarioId + '/parameters/',
							method: 'GET',
							success: function (response) {
								self.attachedParams = response;
							},
							error: function (error) {
								console.log(error);
							}
						});

					},
					error: function (error) {
						console.log(error);
					}
				});

				console.log("Scenario Id:" + scenarioId);
				console.log("Attach params: " + this.paramsToAttach);
				//console.log("All params: " + this.allParams);
				//console.log("Attached params: " + this.attachedParams);


			}
		}
	})
