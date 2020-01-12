package ru.somecompany.loadmodule.util.jpos;

import org.jpos.iso.ISOChannel;

@FunctionalInterface
public interface ChannelFactory {
    ISOChannel getChannel();
}
