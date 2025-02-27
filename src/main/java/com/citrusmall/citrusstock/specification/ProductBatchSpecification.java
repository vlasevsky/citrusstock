package com.citrusmall.citrusstock.specification;

import com.citrusmall.citrusstock.dto.ProductBatchFilterCriteria;
import com.citrusmall.citrusstock.model.ProductBatch;
import com.citrusmall.citrusstock.model.enums.GoodsStatus;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.stream.Collectors;


/**
 * Спецификация для динамической фильтрации партий товаров.
 * Позволяет фильтровать по спискам productIds, supplierIds, statuses (с конвертацией в GoodsStatus)
 * и по списку zoneIds, а также по диапазону дат receivedAt.
 */
public class ProductBatchSpecification implements Specification<ProductBatch> {

    private final ProductBatchFilterCriteria criteria;

    public ProductBatchSpecification(ProductBatchFilterCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<ProductBatch> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        // Начинаем с пустой конъюнкции (условие "true")
        Predicate predicate = cb.conjunction();

        if (criteria.getProductIds() != null && !criteria.getProductIds().isEmpty()) {
            predicate = cb.and(predicate, root.get("product").get("id").in(criteria.getProductIds()));
        }
        if (criteria.getSupplierIds() != null && !criteria.getSupplierIds().isEmpty()) {
            predicate = cb.and(predicate, root.get("supplier").get("id").in(criteria.getSupplierIds()));
        }
        if (criteria.getStatusList() != null && !criteria.getStatusList().isEmpty()) {
            // Преобразуем строки в enum GoodsStatus
            List<GoodsStatus> statusEnums = criteria.getStatusList().stream()
                    .map(GoodsStatus::valueOf)
                    .collect(Collectors.toList());
            predicate = cb.and(predicate, root.get("status").in(statusEnums));
        }
        if (criteria.getZoneIds() != null && !criteria.getZoneIds().isEmpty()) {
            predicate = cb.and(predicate, root.get("zone").get("id").in(criteria.getZoneIds()));
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