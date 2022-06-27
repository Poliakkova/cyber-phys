package com.cyberphysical.system.Repos;

import com.cyberphysical.system.Models.Record;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Repository
public interface RecordRepository extends CrudRepository<Record, Long> {
    @Query("select distinct r.region from Record r")
    List<String> findDistinctRegion();

    @Query("select r from Record r where r.region = ?1 and r.date between ?2 and ?3")
    List<Record> findByRegionAndDate(String region, Date date1, Date date2);

    int countRecordsByTempEqualsAndRegionIsAndDateAfterAndDateBefore(int temp, String region, Date startDate, Date endDate);

    int countRecordsByWindIsAndRegionIsAndDateAfterAndDateBefore(String wind, String region, Date startDate, Date endDate);

    @Query("select distinct r.speed from Record r where r.wind = ?1 and r.region = ?2 and r.date between ?3 and ?4")
    List<Integer> findDistinctSpeedByWind(String wind, String region, Date startDate, Date endDate);

    @Query("select distinct r.speed from Record r where r.region = ?1 and r.date between ?2 and ?3")
    List<Integer> findDistinctSpeed(String region, Date startDate, Date endDate);

    int countRecordsBySpeedIsAndWindIsAndRegionIsAndDateAfterAndDateBefore(int speed, String wind, String region, Date startDate, Date endDate);

    int countRecordsBySpeedIsAndRegionIsAndDateAfterAndDateBefore(int speed, String region, Date startDate, Date endDate);

}
