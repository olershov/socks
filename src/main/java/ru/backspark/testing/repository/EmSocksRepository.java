package ru.backspark.testing.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;
import ru.backspark.testing.model.dto.SocksFilterParams;
import ru.backspark.testing.model.entity.SocksEntity;

import java.util.ArrayList;
import java.util.List;

@Repository
public class EmSocksRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<SocksEntity> getFilteredSocks(SocksFilterParams params) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<SocksEntity> cr = cb.createQuery(SocksEntity.class);
        Root<SocksEntity> root = cr.from(SocksEntity.class);

        List<Predicate> predicates = new ArrayList<>();
        if (params.getColor() != null) {
            predicates.add(cb.equal(root.get("color"), params.getColor()));
        }

        var cottonPercentMin = params.getCottonPercentMin();
        var cottonPercentMax = params.getCottonPercentMax();
        if (cottonPercentMin != null && cottonPercentMax != null) {
            predicates.add(cb.between(root.get("cottonPercentContent"), cottonPercentMin, cottonPercentMax));
        } else {
            if (cottonPercentMin != null) {
                predicates.add(cb.greaterThan(root.get("cottonPercentContent"), cottonPercentMin));
            }
            if (cottonPercentMax != null) {
                predicates.add(cb.lessThan(root.get("cottonPercentContent"), cottonPercentMax));
            }
        }

        var orderBy = params.getOrderBy();
        var ascending = params.getAscending();
        if (orderBy.equals("cottonPercentContent")) {
            if (ascending) {
                cr.orderBy(cb.asc(root.get("cottonPercentContent")));
            } else {
                cr.orderBy(cb.desc(root.get("cottonPercentContent")));
            }
        } else {
            if (ascending) {
                cr.orderBy(cb.asc(root.get("color")));
            } else {
                cr.orderBy(cb.desc(root.get("color")));
            }
        }

        cr.select(root).where(predicates.toArray(new Predicate[predicates.size()]));
        return entityManager.createQuery(cr).getResultList();
    }
}
