package ru.somecompany.loadmodule.util.jpos;

import com.google.gson.GsonBuilder;
import org.jpos.iso.ISOChannel;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import ru.somecompany.bankcards.security.Encripter;
import ru.somecompany.loadmodule.cards.models.Card;
import ru.somecompany.loadmodule.channels.models.Channel;
import ru.somecompany.loadmodule.terminals.models.Terminal;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.google.common.base.Strings.isNullOrEmpty;


public class SimplePosSender{

    private ChannelFactory channelFactory;
    private static final String MASK_PAN = "******";
    private static final String MASK_CVV = "***";
    private static final Logger LOGGER = LoggerFactory.getLogger(SimplePosSender.class.getName());
    private Channel channelModel;

    public SimplePosSender() {
    }

    public SimplePosSender(ChannelFactory channelFactory) {
        this.channelFactory = channelFactory;
    }

    public SimplePosSender(ChannelFactory channelFactory, Channel channelModel) {
        this.channelFactory = channelFactory;
        this.channelModel = channelModel;
    }

    public HashMap<String, String> sendMessage(WebSocketSession session, Map<String, String> fields, Card card, Terminal terminal)
            throws Exception {
        LOGGER.info("SimplePosSender::sendMessage");
        ISOChannel channel = channelFactory.getChannel();
        LOGGER.info("ISO channel factory is {}", channel);

        ISOMsg m = new ISOMsg();

        SecureRandom rand = new SecureRandom();
        String STAN = String.format("%06d", rand.nextInt(999999));
        //String STAN = Environment.getSTAN();


        if(card != null){
            m.set(2, card.getPan());
            m.set(14, card.getExpiryDate());
            /*if(!card.getCvv().isEmpty())
                m.set(10, card.getCvv());*/
            if(!card.getTrack2().isEmpty())
                m.set(35, card.getTrack2());
        }

        if(terminal != null){
            m.set(41, String.valueOf(terminal.getTerminalId()));
            m.set(42, String.valueOf(terminal.getMerchantId()));
            m.set(18, terminal.getMcc());
        }

        for (String field : fields.keySet()) {

            if (field.equals("MTI")) {
                m.setMTI(fields.get(field));


            /*}else if(field.equals("4")){
                m.set(4, String.format("%012d", Integer.parseInt(fields.get(field))));*/

            } else {
                try {
                    m.set(field, fields.get(field));
                } catch (NumberFormatException eNumber) {
                    LOGGER.error("PARSE EXC", eNumber);
                }
            }
        }

        DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("YY");
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("DDD");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH");

        LocalDateTime date = LocalDateTime.now();


        if (fields.get("11") == null)
            m.set(11, STAN);
        if (fields.get("37") == null)
            m.set(37, yearFormatter.format(date).substring(1) + dayFormatter.format(date) + timeFormatter.format(date) + STAN);

        if(terminal != null && (terminal.getTerminalType().equals("ATM") || terminal.getTerminalType().equals("POS"))){
            byte[] encpin = Encripter.encriptTDES(Encripter.formTDESKey(terminal.getTpk()), Encripter.formPINBlock(card.getPan(), card.getPin()));
            m.set(52, Arrays.copyOfRange(encpin, 0, 8));
        }

        m.setDirection(ISOMsg.OUTGOING);
        m.setPackager(channel.getPackager());

        LOGGER.info("Message HEX is {}", ISOUtil.hexdump(m.pack()));

        Map<String, String> requestHex = new HashMap<>();
        requestHex.put("requestHEX", ISOUtil.hexdump(m.pack()));
        session.sendMessage(new TextMessage(new GsonBuilder().create().toJson(requestHex)));

        LOGGER.info("Message XML is {}", getMessageXml(m));

      /*  Map<String, String> requestXML = new HashMap<>();
        requestXML.put("requestXML",  getMessageXml(m));
        session.sendMessage(new TextMessage(new GsonBuilder().create().toJson(requestXML)));*/

        channel.send(m);

        LOGGER.info("Message is sent into ONLINE");

        Map<String, String> message = new HashMap<>();
        message.put("message", "Message is sent into ONLINE");
        session.sendMessage(new TextMessage(new GsonBuilder().create().toJson(message)));

        ISOMsg rc = channel.receive();

        LOGGER.info("Response Message XML is {}", getMessageXml(rc));

       /* Map<String, String> responseXML = new HashMap<>();
        responseXML.put("responseXML",  getMessageXml(rc));
        session.sendMessage(new TextMessage(new GsonBuilder().create().toJson(responseXML)));*/


        LOGGER.info("Response Hexdump is {}", ISOUtil.hexdump(rc.pack()));

        Map<String, String> responseHex = new HashMap<>();
        responseHex.put("responseHEX",   ISOUtil.hexdump(rc.pack()));
        session.sendMessage(new TextMessage(new GsonBuilder().create().toJson(responseHex)));

        channel.disconnect();


        HashMap<String, String> fieldsToReturn = new HashMap<>();
        for (Object fObj : rc.getChildren().keySet()) {
            int fNum = (Integer) fObj;
            fieldsToReturn.put(String.valueOf(fNum), rc.getString(fNum));
        }


        session.sendMessage(new TextMessage("{\"fields\": " + new GsonBuilder().create().toJson(fieldsToReturn) + "}"));



        //session.sendMessage(new TextMessage(new GsonBuilder().create().toJson(fieldsToReturn)));

        return fieldsToReturn;
    }

    public String getMessageXml(ISOMsg m) {
        String result = "";
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(ba);
        m.dump(ps, "");
        try {
            result = ba.toString("UTF8");
        } catch (UnsupportedEncodingException e1) {
            LOGGER.error("error", e1);
        }
        return maskIsoMessage(result);
    }

    private String maskIsoMessage(String isoMessage) {
        if (!isNullOrEmpty(isoMessage)) {
            StringBuilder sb = new StringBuilder(isoMessage);

            int panIndexStart = sb.indexOf("id=\"2\"");
            if (panIndexStart != -1) {
                int panIndexEnd = sb.indexOf("\"", panIndexStart + 20);
                sb.replace(panIndexStart + 20, panIndexEnd - 4, MASK_PAN);
            }

            int cvvIndex = sb.lastIndexOf("value=\"10 ");
            if (cvvIndex != -1) {
                sb.replace(cvvIndex + 10, cvvIndex + 13, MASK_CVV);
            }


            int f2IndexStart = sb.lastIndexOf("F2");
            if (f2IndexStart != -1) {

                int f2IndexEnd = sb.indexOf("\"", f2IndexStart);
                if ((f2IndexEnd - f2IndexStart) >= 18)
                    sb.replace(f2IndexStart + 10, f2IndexEnd - 4, MASK_PAN);
            }

            return sb.toString();
        } else return isoMessage;
    }

    private static void processVariable(ISOMsg m, String value){

    }

    private static void processPinBlock(ISOMsg m, String value){

        //value.split("#");

        //m.set(52);

    }


}
