package ru.somecompany.loadmodule.channels.repository;


import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.somecompany.loadmodule.channels.models.Channel;

import java.util.List;

@Repository
public interface ChannelRepository extends CrudRepository<Channel, Long> {

    @Query(getAllChannelsByPage)
    List<Channel> getAllChannelsByPage(Pageable pageable);
    final String getAllChannelsByPage = "from Channel order by id";
}
