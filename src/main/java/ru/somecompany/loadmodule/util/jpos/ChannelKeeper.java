package ru.somecompany.loadmodule.util.jpos;

import org.jpos.iso.BaseChannel;
import org.jpos.iso.ISOChannel;
import org.jpos.iso.channel.NACChannel;
import org.jpos.iso.packager.GenericPackager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.somecompany.bankcards.loadscripts.channels.CustomASCIIChannel;
import ru.somecompany.bankcards.security.Encripter;
import ru.somecompany.loadmodule.channels.models.Channel;

import java.net.SocketException;
import java.util.Date;

public class ChannelKeeper implements ChannelFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ChannelKeeper.class);

    private BaseChannel channel;

    private int channelBorrowedCount;
    private Date createDate;
    private Date lastBorrowed;
    private String id;

    private String onlineIp;
    private int onlinePort;
    private String packagerConfig;
    private org.jpos.util.Logger channelLogger;

    public ChannelKeeper() {
    }

    public ChannelKeeper(Channel channelModel, int timeOut, org.jpos.util.Logger channelLogger) throws Exception{

        this.onlineIp = channelModel.getIp();
        this.onlinePort = channelModel.getPort();
        this.channelLogger = channelLogger;

        GenericPackager packager = new GenericPackager("jar:"+channelModel.getPackager());

        byte[] encripter;

        if(channelModel.getChannelType().equals("POS"))
            encripter = Encripter.hexToBytes("6000010000");
        else
            encripter = Encripter.hexToBytes("");


        if(channelModel.getChannelType().equals("ATM")){

            channel = new CustomASCIIChannel(channelModel.getIp(),
                    channelModel.getPort(),
                    packager, channelModel.getHeaderLength(), channelModel.getStartSymbol());

        }else{
            channel = new NACChannel(onlineIp, onlinePort,
                    packager,
                    encripter);
            channel.setLogger(channelLogger, "vmt-channel");
        }



        channel.connect();
        LOG.info("ChannelKeeper TimeOut: {}", timeOut);
        channel.setTimeout(timeOut);


        this.channel = channel;
        channelBorrowedCount = 0;
        createDate = new Date();
        lastBorrowed = new Date();



    }


    public int getTimeout() {
        return this.channel.getTimeout();
    }

    public void setTimeout(int timeout) throws SocketException {
        this.channel.setTimeout(timeout);
    }

    @Override
    public ISOChannel getChannel() {
        LOG.info("ChannelKeeper::getChannel");
        this.channelBorrowedCount++;
        this.lastBorrowed = new Date();
        return this.channel;
    }
}
