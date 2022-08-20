package com.khwish.backend.services;

import com.khwish.backend.models.Wallet;
import com.khwish.backend.repositories.WalletRepository;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class WalletService {

    private WalletRepository walletRepo;
    private final String classTag = this.getClass().getSimpleName();
    private Logger LOGGER = LogManager.getLogger(this.getClass());

    @Autowired
    public WalletService(WalletRepository walletRepo) {
        this.walletRepo = walletRepo;
    }

    public double getWalletAmount(UUID userId) {
        if (userId == null) {
            LOGGER.log(Level.ERROR, "[" + classTag + "][getWalletAmount] wallet amount requested for userId = null");
            return 0;
        }
        Wallet userWallet = walletRepo.findByUserId(userId);
        if (userWallet == null) {
            LOGGER.log(Level.ERROR, "[" + classTag + "][getWalletAmount] wallet not found for userId = " + userId);
            return 0;
        } else {
            return userWallet.getAmount();
        }
    }

    public void addToWallet(UUID userId, double updateAmount) {
        Wallet userWallet = walletRepo.findByUserId(userId);
        if (userWallet != null) {
            userWallet.addAmount(updateAmount);
            walletRepo.save(userWallet);
        }
    }

    public boolean deductFromWallet(UUID userId, double updateAmount) {
        Wallet userWallet = walletRepo.findByUserId(userId);
        if (userWallet != null) {
            boolean success = userWallet.deductAmount(updateAmount);
            if (success) {
                walletRepo.save(userWallet);
                return true;
            } else
                return false;
        } else {
            return false;
        }
    }

    public void createWallet(UUID userId) {
        Wallet foundWallet = walletRepo.findByUserId(userId);
        if (foundWallet == null) {
            Wallet walletObj = new Wallet(userId, 0);
            walletRepo.save(walletObj);
        }
    }
}
