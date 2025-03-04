package com.citrusmall.citrusstock.repository;

import com.citrusmall.citrusstock.dto.ZoneStats;
import com.citrusmall.citrusstock.model.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, Long> {
    Optional<Zone> findByName(String name);

    @Query("select new com.citrusmall.citrusstock.dto.ZoneStats(z.id, z.name, z.color, count(pb)) " +
            "from Zone z left join ProductBatch pb on pb.zone = z " +
            "group by z.id, z.name, z.color")
    List<ZoneStats> findZoneStats();
}