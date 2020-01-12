var attachStepsComponent = new Vue({
		el: '#steps_div',
		data: {
			allSteps: [],
			attachedSteps: [],
			stepsToAttach: [],
			stepProperties: {},
			stepFields: []
		},
		created: function () {

			var self = this;
			var scenarioId = $("#scenario_id").val();

			/*$.ajax({
			url: '/scenarios/steps/all',
			method: 'GET',
			success: function (response) {
			self.allSteps = response;
			},
			error: function (error) {
			console.log(error);
			}
			});*/

			if (scenarioId) {

				$.ajax({
					url: '/scenarios/' + scenarioId + '/steps/',
					method: 'GET',
					success: function (response) {
						self.attachedSteps = response;
					},
					error: function (error) {
						console.log(error);
					}
				});

			}

		},
		methods: {

			addField: function () {
				var self = this;
				var scenarioId = $("#scenario_id").val();
				//stepFields.push({id: null, name: '', value:''});

				var fieldForm = {
					name: $('#addFieldName').val(),
					value: $('#addFieldValue').val()
				};

				$.ajax({
					url: '/scenarios/' + scenarioId + '/steps/' + self.stepProperties.id + '/addfield',
					method: 'POST',
					data: JSON.stringify(fieldForm),
					contentType: "application/json",
					success: function (response) {

						$.ajax({
							url: '/scenarios/' + scenarioId + '/steps/' + self.stepProperties.id + '/fields',
							method: 'GET',
							success: function (response) {
								self.stepFields = response;
							},
							error: function (error) {
								console.log(error);
							}
						});

						$('#addFieldName').val('');
						$('#addFieldValue').val('');

					},
					error: function (error) {
						console.log(error);
					}
				});
			},

			deleteField: function (id) {
				var self = this;
				var scenarioId = $("#scenario_id").val();
				$.ajax({
					url: '/scenarios/' + scenarioId + '/steps/' + self.stepProperties.id + '/field/' + id,
					method: 'DELETE',

					success: function (response) {

						$.ajax({
							url: '/scenarios/' + scenarioId + '/steps/' + self.stepProperties.id + '/fields',
							method: 'GET',
							success: function (response) {
								self.stepFields = response;
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

			saveField: function (field) {
				var self = this;
				var scenarioId = $("#scenario_id").val();

				$.ajax({
					url: '/scenarios/' + scenarioId + '/steps/' + self.stepProperties.id + '/field',
					method: 'POST',
					data: JSON.stringify(field),
					contentType: "application/json",
					success: function (response) {

						$.ajax({
							url: '/scenarios/' + scenarioId + '/steps/' + self.stepProperties.id + '/fields',
							method: 'GET',
							success: function (response) {
								self.stepFields = response;
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

			saveStep: function () {
				var self = this;
				var scenarioId = $("#scenario_id").val();
				$.ajax({
					url: '/scenarios/' + scenarioId + '/steps/' + self.stepProperties.id + '/edit',
					method: 'POST',
					data: JSON.stringify(self.stepProperties),
					contentType: "application/json",
					success: function (response) {

						$.ajax({
							url: '/scenarios/' + scenarioId + '/steps/',
							method: 'GET',
							success: function (response) {
								self.attachedSteps = response;
							},

							error: function (error) {
								console.log(error);
							}
						});

						//$('#editStepModal').modal('hide');

					},
					error: function (error) {
						console.log(error);
					}
				});

			},
			editStep: function (stepId) {
				var self = this;
				var scenarioId = $("#scenario_id").val();
				$('#editStepModal').modal('show');

				$.ajax({
					url: '/scenarios/' + scenarioId + '/steps/' + stepId + '/edit',
					method: 'GET',
					success: function (response) {

						self.stepProperties = response;

						$.ajax({
							url: '/scenarios/' + scenarioId + '/steps/' + self.stepProperties.id + '/fields',
							method: 'GET',
							success: function (response) {
								self.stepFields = response;
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

			createAndAttachStep: function () {
				console.log("Attaching step...");
				var self = this;
				var scenarioId = $("#scenario_id").val();

				// step form data
				var stepFormData = {
					name: $("#stepName").val(),
					type: $("#stepType").val(),
					description: $("#stepDescription").val()
				};

				$.ajax({
					url: '/scenarios/' + scenarioId + '/attachstep',
					method: 'POST',
					data: JSON.stringify(stepFormData),
					contentType: "application/json",
					success: function (response) {

						$.ajax({
							url: '/scenarios/' + scenarioId + '/steps/',
							method: 'GET',
							success: function (response) {
								self.attachedSteps = response;
							},
							error: function (error) {
								console.log(error);
							}
						});

						$('#attachStepModal').modal('hide');
						$("#stepName").val('');
						$("#stepDescription").val('')

					},
					error: function (error) {
						console.log(error);
					}
				});

			},

			moveUp: function (id) {
				var self = this;
				var scenarioId = $("#scenario_id").val();
				//console.log("Move up: " + id);
				$.ajax({
					url: '/scenarios/' + scenarioId + '/steps/' + id + '/moveup',
					method: 'POST',
					contentType: "application/json",
					success: function (response) {

						$.ajax({
							url: '/scenarios/' + scenarioId + '/steps/',
							method: 'GET',
							success: function (response) {
								self.attachedSteps = response;
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

			moveDown: function (id) {

				var self = this;
				var scenarioId = $("#scenario_id").val();
				//console.log("Move down: " + id);
				$.ajax({
					url: '/scenarios/' + scenarioId + '/steps/' + id + '/movedown',
					method: 'POST',
					contentType: "application/json",
					success: function (response) {

						$.ajax({
							url: '/scenarios/' + scenarioId + '/steps/',
							method: 'GET',
							success: function (response) {
								self.attachedSteps = response;
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

			removeStep: function (id) {

				var self = this;
				var scenarioId = $("#scenario_id").val();
				$.ajax({
					url: '/scenarios/' + scenarioId + '/steps/' + id + '/remove',
					method: 'DELETE',
					success: function (response) {

						$.ajax({
							url: '/scenarios/' + scenarioId + '/steps/',
							method: 'GET',
							success: function (response) {
								self.attachedSteps = response;
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

			attachSteps: function (e) {

				var self = this;
				var scenarioId = $("#scenario_id").val();
				$.ajax({
					url: '/scenarios/' + scenarioId + '/steps',
					method: 'POST',
					data: JSON.stringify(self.stepsToAttach),
					contentType: "application/json",
					success: function (response) {

						$.ajax({
							url: '/scenarios/' + scenarioId + '/steps/',
							method: 'GET',
							success: function (response) {
								self.attachedSteps = response;
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
				console.log("Attach steps: " + this.stepsToAttach);
				//console.log("All steps: " + this.allSteps);
				//console.log("Attached steps: " + this.attachedSteps);


			}
		}
	})
