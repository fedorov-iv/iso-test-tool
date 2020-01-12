package ru.somecompany.loadmodule.channels.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.somecompany.loadmodule.channels.models.Channel;
import ru.somecompany.loadmodule.channels.repository.ChannelRepository;

import java.util.List;

@Service("channelService")
public class ChannelService {

    private ChannelRepository channelRepository;

    @Autowired
    public ChannelService(ChannelRepository channelRepository){
        this.channelRepository = channelRepository;
    }

    public Iterable<Channel> getAllChannels(){
        return channelRepository.findAll();

    }

    public List<Channel> getAllChannelsByPage(int page, int size){
        return channelRepository.getAllChannelsByPage(PageRequest.of(page, size));
    }

    public Channel getById(Long id){
        return channelRepository.findById(id).get();

    }

    public Long getChannelsCount(){
        return channelRepository.count();
    }

    public void save(Channel channel){
        channelRepository.save(channel);
    }

    public void delete(Channel channel){
        channelRepository.delete(channel);
    }
}
