package ru.backspark.testing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.backspark.testing.model.entity.SocksEntity;

import java.util.Optional;

@Repository
public interface SocksRepository extends JpaRepository<SocksEntity, Long> {

    Optional<SocksEntity> findByColorIgnoreCaseAndCottonPercentContent(String color, Integer cottonPercent);
}
