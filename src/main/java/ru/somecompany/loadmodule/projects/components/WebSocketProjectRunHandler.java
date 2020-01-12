package ru.somecompany.loadmodule.projects.components;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ru.somecompany.loadmodule.cards.models.Card;
import ru.somecompany.loadmodule.cards.service.CardsService;
import ru.somecompany.loadmodule.channels.models.Channel;
import ru.somecompany.loadmodule.channels.service.ChannelService;
import ru.somecompany.loadmodule.scenarios.models.Scenario;
import ru.somecompany.loadmodule.scenarios.service.ScenarioService;
import ru.somecompany.loadmodule.steps.models.ScenarioStep;
import ru.somecompany.loadmodule.steps.models.StepField;
import ru.somecompany.loadmodule.steps.service.StepFieldService;
import ru.somecompany.loadmodule.steps.service.StepsService;
import ru.somecompany.loadmodule.terminals.models.Terminal;
import ru.somecompany.loadmodule.terminals.service.TerminalsService;
import ru.somecompany.loadmodule.util.jpos.ChannelKeeper;
import ru.somecompany.loadmodule.util.jpos.SimplePosSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class WebSocketProjectRunHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(WebSocketProjectRunHandler.class.getName());
    private org.jpos.util.Logger channelLogger = org.jpos.util.Logger.getLogger(ChannelKeeper.class.toString());

    @Autowired
    private ChannelService channelService;

    @Autowired
    private CardsService cardsService;

    @Autowired
    private TerminalsService terminalsService;

    @Autowired
    private ScenarioService scenarioService;

    @Autowired
    private StepsService stepsService;

    @Autowired
    private StepFieldService stepFieldService;

   /* @Autowired
    private*/

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        Map<String, String> context = new HashMap<>();

        Map requestBody = new Gson().fromJson(message.getPayload(), Map.class);
        //session.sendMessage(new TextMessage("Hello " + value.get("name") + " !"));

        Scenario scenario = scenarioService.getById(Long.parseLong((String) requestBody.get("scenario_id")));
        Card card = cardsService.getById(scenario.getCardId());
        Terminal terminal = terminalsService.getById(scenario.getTerminalId());
        Channel channel = channelService.getById(scenario.getChannelId());
        List<ScenarioStep> scenarioSteps = scenarioService.getScenarioSteps(scenario);

        int cnt = 1;
        for(ScenarioStep ss: scenarioSteps){

            try {
                String result = "Pass";

                switch (ss.getStep().getType()) {
                    case "ISO":
                        log.info("Trying to connect to ONLINE ip: {} port: {}", channel.getIp(), channel.getPort());
                        session.sendMessage(new TextMessage(String.format("{\"message\": \"Trying to connect to ONLINE ip: %s port: %s\"}", channel.getIp(), channel.getPort())));

                        Map<String, String> isoData = new HashMap<>();
                        List<StepField> stepFields = stepFieldService.getFieldsByStep(ss.getStep());
                        stepFields.forEach(sf -> isoData.put(sf.getName(), sf.getValue()));

                        try {
                            SimplePosSender simplePosSender = new SimplePosSender(new ChannelKeeper(channel, 60000,
                                    channelLogger), channel);
                            context = simplePosSender.sendMessage(session, isoData, card, terminal);

                        } catch (Exception e) {
                            session.sendMessage(new TextMessage(String.format("{\"error\": \"%s\"}", e.toString())));
                            result = "Fail";
                        }

                        break;

                    case "VALIDATE":
                        break;


                }


                session.sendMessage(new TextMessage("{\"type\": \"step\", \"index\": "+ cnt + ", \"name\": \"" + ss.getStep().getName() + "\", \"status\": \"" + result + "\"}"));
                //Thread.sleep(1000);

            } catch (Exception e) {
                log.error(e.getMessage());
            }

            cnt++;

        }

        session.sendMessage(new TextMessage("{\"finished\": \"Finished\"}"));





       /*

        Channel channel = channelService.getById(Long.valueOf((String) requestBody.get("channelId")));
        Card card = cardsService.getById(Long.valueOf((String) requestBody.get("cardId")));
        Terminal terminal = terminalsService.getById(Long.valueOf((String) requestBody.get("terminalId")));

       */


    }


}
