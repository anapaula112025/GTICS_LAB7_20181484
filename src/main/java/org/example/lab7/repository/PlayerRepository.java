package org.example.lab7.repository;

import org.example.lab7.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Integer> {

    @Query(value = "SELECT * FROM players WHERE region = ?1 and mmr > 6500;", nativeQuery = true)
    List<Player> listarplayerporregion(String region);

    @Query(value = "SELECT max(mmr) FROM players where region = ?1", nativeQuery = true)
    int maxmmr(String region);



}
