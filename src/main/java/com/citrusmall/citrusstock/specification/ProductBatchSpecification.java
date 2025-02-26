package com.citrusmall.citrusstock.specification;

import com.citrusmall.citrusstock.dto.ProductBatchFilterCriteria;
import com.citrusmall.citrusstock.model.ProductBatch;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;


public class ProductBatchSpecification implements Specification<ProductBatch> {

    private final ProductBatchFilterCriteria criteria;

    public ProductBatchSpecification(ProductBatchFilterCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<ProductBatch> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Predicate predicate = cb.conjunction();

        if (criteria.getProductId() != null) {
            predicate = cb.and(predicate, cb.equal(root.get("product").get("id"), criteria.getProductId()));
        }
        if (criteria.getSupplierId() != null) {
            predicate = cb.and(predicate, cb.equal(root.get("supplier").get("id"), criteria.getSupplierId()));
        }
        if (criteria.getStatus() != null && !criteria.getStatus().isEmpty()) {
            predicate = cb.and(predicate, cb.equal(root.get("status"), criteria.getStatus()));
        }
        if (criteria.getZone() != null && !criteria.getZone().isEmpty()) {
            // Фильтруем по имени зоны (из сущности Zone)
            predicate = cb.and(predicate, cb.equal(root.get("zone").get("name"), criteria.getZone()));
        }
        if (criteria.getReceivedFrom() != null) {
            predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("receivedAt"), criteria.getReceivedFrom()));
        }
        if (criteria.getReceivedTo() != null) {
            predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("receivedAt"), criteria.getReceivedTo()));
        }
        return predicate;
    }
}
