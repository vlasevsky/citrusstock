package com.citrusmall.citrusstock.service;

import com.citrusmall.citrusstock.model.Box;
import com.citrusmall.citrusstock.model.ProductBatch;
import com.citrusmall.citrusstock.model.ScanEvent;
import com.citrusmall.citrusstock.model.User;
import com.citrusmall.citrusstock.model.enums.BoxStatus;
import com.citrusmall.citrusstock.model.enums.ProductBatchStatus;
import com.citrusmall.citrusstock.model.enums.ScanMode;
import com.citrusmall.citrusstock.model.enums.Zone;
import com.citrusmall.citrusstock.repository.BoxRepository;
import com.citrusmall.citrusstock.repository.ScanEventRepository;
import com.citrusmall.citrusstock.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScanService {

    @Autowired
    private BoxRepository boxRepository;

    @Autowired
    private ScanEventRepository scanEventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductBatchService productBatchService;

    /**
     * Processes scanning for a box.
     * Updates the box status to newBoxStatus, records the scan event,
     * and if all boxes in the batch have the required status, updates the ProductBatch.
     *
     * @param boxId           the ID of the box being scanned
     * @param userId          the ID of the operator performing the scan
     * @param targetZone      the zone to set for the batch (STORAGE for new goods, SHIPMENT for shipment)
     * @param newBatchStatus  the new status for the batch (CONFIRMED or SHIPPED)
     * @param newBoxStatus    the new status for the box (SCANNED for new goods, SHIPPED for shipment)
     */
    public void scanBox(Long boxId, Long userId, Zone targetZone, ProductBatchStatus newBatchStatus, BoxStatus newBoxStatus) {
        // Retrieve the box
        Box box = boxRepository.findById(boxId)
                .orElseThrow(() -> new IllegalArgumentException("Box with id " + boxId + " not found"));

        // Update the box status and set scanned time
        box.setStatus(newBoxStatus);
        box.setScannedAt(LocalDateTime.now());
        // Retrieve and set the user entity
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id " + userId));
        box.setScannedBy(user);
        boxRepository.save(box);

        // Determine scan mode based on target zone
        ScanMode mode = (targetZone == Zone.SHIPMENT) ? ScanMode.SHIPMENT : ScanMode.ON_WAREHOUSE;

        // Create and save a scan event
        ScanEvent event = new ScanEvent();
        event.setBox(box);
        event.setUser(user);
        event.setScanMode(mode);
        event.setScanTime(LocalDateTime.now());
        scanEventRepository.save(event);

        // Check if all boxes in the associated ProductBatch have the required newBoxStatus
        ProductBatch batch = box.getProductBatch();
        List<Box> boxes = boxRepository.findByProductBatch_Id(batch.getId());
        boolean allMatch = boxes.stream().allMatch(b -> b.getStatus() == newBoxStatus);
        if (allMatch) {
            productBatchService.checkAndUpdateBatchStatus(batch.getId(), newBoxStatus, newBatchStatus, targetZone);
        }
    }

    /**
     * Processes scanning for new goods.
     * For new goods, sets the box status to SCANNED, batch status to CONFIRMED, and batch zone to STORAGE.
     *
     * @param boxId  the ID of the box being scanned
     * @param userId the ID of the operator
     */
    public void scanNewBox(Long boxId, Long userId) {
        scanBox(boxId, userId, Zone.STORAGE, ProductBatchStatus.CONFIRMED, BoxStatus.SCANNED);
    }

    /**
     * Processes scanning for shipment.
     * For shipment scanning, sets the box status to SHIPPED, batch status to SHIPPED, and batch zone to SHIPMENT.
     *
     * @param boxId  the ID of the box being scanned for shipment
     * @param userId the ID of the operator
     */
    public void scanBoxForShipment(Long boxId, Long userId) {
        scanBox(boxId, userId, Zone.SHIPMENT, ProductBatchStatus.SHIPPED, BoxStatus.SHIPPED);
    }
}