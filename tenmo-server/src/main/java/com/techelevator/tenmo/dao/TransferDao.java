package com.techelevator.tenmo.dao;


import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {
    List<Transfer> listAllTransfers();

    Transfer getTransferByTransferId(int id);
    List<Transfer> getTransfersByUserId(int userId);

    void createTransfer(Transfer transfer);

    void updateTransfer(Transfer transfer);

    List<Transfer> getPendingTransfers(int id);
}
