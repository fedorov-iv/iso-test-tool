package ru.somecompany.loadmodule.channels.forms;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class ChannelForm {
    private Long id;

    @NotNull
    private String name;

    private String description;

    @NotNull
    private String channelType;

    @NotNull
    private String ip;

    @NotNull
    private int port;

    @NotNull
    private String packager;

    private String startSymbol;

    private int headerLength;


    public String toString() {
        return "ChannelForm (Channel name: " + this.name + ", Description: "+ this.description +")";
    }
}
