package ru.somecompany.loadmodule.util.jpos;

import org.jpos.iso.ISOException;
import org.jpos.iso.packager.GenericPackager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OnlineConnectionParams {

    private static final Logger LOG = LoggerFactory.getLogger(OnlineConnectionParams.class);
    private static final String ERROR = "GenericPackager creation error";

    private String onlineIp;
    private String onlinePort;
    private String onlinePortEcom;
    private String onlineIpEcom;
    private GenericPackager genericPackager;
    private int timeOut;

    public String getOnlineIpEcom() {
        return onlineIpEcom;
    }

    public void setOnlineIpEcom(String onlineIpEcom) {
        this.onlineIpEcom = onlineIpEcom;
    }

    public String getOnlineIp() {
        return onlineIp;
    }

    public void setOnlineIp(String onlineIp) {
        this.onlineIp = onlineIp;
    }

    public String getOnlinePort() {
        return onlinePort;
    }

    public String getOnlinePortEcom() {
        return onlinePortEcom;
    }

    public void setOnlinePortEcom(String onlinePortEcom) {
        this.onlinePortEcom = onlinePortEcom;
    }

    public void setOnlinePort(String onlinePort) {
        this.onlinePort = onlinePort;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    public GenericPackager getGenericPackager() {
        return genericPackager;
    }

    public void setGenericPackager(String packagerConfig) {
        this.genericPackager = createGenericPackager(packagerConfig);
    }

    private GenericPackager createGenericPackager(String packagerConfig) {
        LOG.info("GENERIC PACKAGER CREATION");
        try {
            return new GenericPackager(packagerConfig);
        } catch (ISOException e) {
            LOG.error(ERROR, e.getNested());
            throw new RuntimeException(ERROR, e);//NOSONAR
        }
    }
}

