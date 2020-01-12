package ru.somecompany.loadmodule.channels.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.somecompany.loadmodule.channels.forms.ChannelForm;
import ru.somecompany.loadmodule.channels.forms.CopyChannelForm;
import ru.somecompany.loadmodule.channels.repository.ChannelSearchRepository;
import ru.somecompany.loadmodule.channels.service.ChannelService;
import ru.somecompany.loadmodule.channels.models.Channel;

import java.util.List;


@Controller
public class ChannelsController {

    private int pageSize = 10;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private ChannelSearchRepository channelSearch;

    @RequestMapping(value = "/channels", method = RequestMethod.GET)
    public ModelAndView channels(@RequestParam(name="page", required=false, defaultValue="0") String page,  
                                 @RequestParam(name="size", required=false, defaultValue="10") String size,
                                 @RequestParam(name="search", required=false, defaultValue="") String search) {

        ModelAndView modelAndView = new ModelAndView();

        int p = (!page.equals("0")) ? Integer.valueOf(page) : 1;
        int s = Integer.valueOf(size); //items on page

        Long channelsCount = 0L;

        if(!search.isEmpty()){
            int startAt = s * (p  - 1);
            List<Channel> channels = channelSearch.search(search, startAt, s);
            channelsCount = (long) channelSearch.getResultSize(search);
            modelAndView.addObject("channels", channels);

        }else{
            List<Channel> channels = channelService.getAllChannelsByPage(p - 1, s);
            channelsCount = channelService.getChannelsCount();
            modelAndView.addObject("channels", channels);
        }

        int pagesCount = (int) Math.ceil((double) channelsCount / (double) s);
        modelAndView.addObject("pages", pagesCount == 0 ? 1 : pagesCount);
        modelAndView.addObject("current_page", p);
        modelAndView.addObject("current_size", s);
        modelAndView.addObject("current_search", search);
        modelAndView.setViewName("channels");
        return modelAndView;
    }

    @RequestMapping(value = "/channels/add", method = RequestMethod.GET)
    public ModelAndView addChannelForm() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("channel", new Channel());
        modelAndView.setViewName("edit_channel");
        return modelAndView;

    }

    @RequestMapping(value = "/channels/add", method = RequestMethod.POST)
    public String addChannel(ChannelForm form) {

        Channel channel = new Channel();
        channel.setName(form.getName());
        channel.setDescription(form.getDescription());
        channel.setChannelType(form.getChannelType());
        channel.setIp(form.getIp());
        channel.setPort(form.getPort());
        channel.setPackager(form.getPackager());
        channel.setStartSymbol(form.getStartSymbol());
        channel.setHeaderLength(form.getHeaderLength());
        channelService.save(channel);

        return "redirect:/channels";

    }

    @RequestMapping(value = "/channels/edit", method = RequestMethod.GET)
    public ModelAndView editChannel(@RequestParam(name="id") String id) {

        Long channelId = Long.valueOf(id);
        Channel channel = channelService.getById(channelId);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("channel", channel);
        modelAndView.setViewName("edit_channel");
        return modelAndView;

    }

    @RequestMapping(value = "/channels/edit", method = RequestMethod.POST)
    public String editChannel(ChannelForm form) {

        Channel channel = channelService.getById(form.getId());
        channel.setName(form.getName());
        channel.setDescription(form.getDescription());
        channel.setChannelType(form.getChannelType());
        channel.setIp(form.getIp());
        channel.setPort(form.getPort());
        channel.setPackager(form.getPackager());
        channel.setStartSymbol(form.getStartSymbol());
        channel.setHeaderLength(form.getHeaderLength());
        channelService.save(channel);

        return "redirect:/channels/edit/?id=" + channel.getId();

    }

    @RequestMapping(value = "/channels/delete", method = RequestMethod.GET)
    public String deleteChannel(@RequestParam(name = "id") String id) {
        Long channelId = Long.valueOf(id);

        Channel channel = channelService.getById(channelId);
        channelService.delete(channel);

        return "redirect:/channels";

    }

    @RequestMapping(path="/channels/copy", method = RequestMethod.POST)
    public String copyChannel(CopyChannelForm form){

        Channel channel = channelService.getById(form.getChannelId());
        Channel newChannel = new Channel();
        newChannel.setName(form.getName());
        newChannel.setDescription("");
        newChannel.setChannelType(channel.getChannelType());
        newChannel.setIp(channel.getIp());
        newChannel.setPort(channel.getPort());
        newChannel.setPackager(channel.getPackager());
        newChannel.setStartSymbol(channel.getStartSymbol());
        newChannel.setHeaderLength(channel.getHeaderLength());
        channelService.save(newChannel);
        return "redirect:/channels";

    }
}
