package ru.somecompany.loadmodule.channels.models;

import lombok.Data;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Data
@Entity
@Indexed
@Table(name = "channels")
public class Channel {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    private Long id;

    @Field(store = Store.NO)
    @NotEmpty(message = "*Please provide channel name")
    private String name;

    @Field(store = Store.NO)
    private String description;

    @Column(name = "channel_type")
    private String channelType;

    private String ip;

    private int port;

    private String packager;

    @Column(name = "start_symbol")
    private String startSymbol;

    @Column(name = "header_length")
    private int headerLength;

    @Override
    public String toString() {
        return String.format("Channel [id=%s, name='%s', ip='%s', port=%d, packager='%s'start_symbol='%s', header_length=%d]", id, name, ip, port, packager, startSymbol, headerLength);
    }
}
