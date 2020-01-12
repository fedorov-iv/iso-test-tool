package ru.somecompany.loadmodule.util.jpos;

import java.util.Map;

@FunctionalInterface
public interface Iso8583Service {

    Map<String, String> sendMessage(Map<String, String> fields) throws Exception;


}
