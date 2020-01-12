package ru.somecompany.loadmodule.channels.forms;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class CopyChannelForm {
    private Long channelId;

    @NotNull
    private String name;

    public String toString() {
        return "CopyChannelForm (Channel id: " + this.channelId + ", name: "+ this.name +")";
    }
}
