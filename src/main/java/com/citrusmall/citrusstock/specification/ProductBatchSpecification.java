package com.citrusmall.citrusstock.specification;

import com.citrusmall.citrusstock.dto.ProductBatchFilterCriteria;
import com.citrusmall.citrusstock.model.ProductBatch;
import com.citrusmall.citrusstock.model.enums.GoodsStatus;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Спецификация для динамической фильтрации партий товаров.
 * Позволяет фильтровать по спискам productIds, supplierIds, statuses (с конвертацией в GoodsStatus)
 * и по списку zoneIds, а также по диапазону дат receivedAt.
 */
public class ProductBatchSpecification implements Specification<ProductBatch> {
    private static final Logger logger = LoggerFactory.getLogger(ProductBatchSpecification.class);
    private final ProductBatchFilterCriteria criteria;

    public ProductBatchSpecification(ProductBatchFilterCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<ProductBatch> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        try {
            List<Predicate> predicates = new ArrayList<>();

            // Фильтр по productIds
            if (criteria.getProductIds() != null && !criteria.getProductIds().isEmpty()) {
                predicates.add(root.get("product").get("id").in(criteria.getProductIds()));
            }

            // Фильтр по supplierIds
            if (criteria.getSupplierIds() != null && !criteria.getSupplierIds().isEmpty()) {
                predicates.add(root.get("supplier").get("id").in(criteria.getSupplierIds()));
            }

            // Фильтр по статусам
            if (criteria.getStatusList() != null && !criteria.getStatusList().isEmpty()) {
                List<GoodsStatus> statuses = new ArrayList<>();
                for (String status : criteria.getStatusList()) {
                    try {
                        statuses.add(GoodsStatus.valueOf(status));
                    } catch (IllegalArgumentException e) {
                        logger.warn("Invalid status value: {}", status);
                    }
                }
                if (!statuses.isEmpty()) {
                    predicates.add(root.get("status").in(statuses));
                }
            }

            // Фильтр по zoneIds
            if (criteria.getZoneIds() != null && !criteria.getZoneIds().isEmpty()) {
                predicates.add(root.get("zone").get("id").in(criteria.getZoneIds()));
            }

            // Фильтр по дате получения (receivedAt)
            if (criteria.getReceivedFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("receivedAt"), criteria.getReceivedFrom()));
            }
            if (criteria.getReceivedTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("receivedAt"), criteria.getReceivedTo()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        } catch (Exception e) {
            logger.error("Error building predicate: {}", e.getMessage(), e);
            throw e;
        }
    }
}