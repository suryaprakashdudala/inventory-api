package com.inventory.service;

import org.springframework.stereotype.Service;

import com.inventory.entity.User;

@Service
public interface EmailService {

    public String generateAndSendOtp(String email);

    public boolean verifyOtp(String email, String otpCode);

    public void sendUserCreationMail(User user, String password);
    
    public void sendDocumentCompletionEmail(String email, String userName, String documentTitle, String completionLink);

    public void sendFinalDocumentEmail(String email, String userName, String documentTitle, String finalLink);

    public void sendLowStockAlert(String to, String productName, String sku, int currentQty, int reorderLevel, String userName);
    
}
