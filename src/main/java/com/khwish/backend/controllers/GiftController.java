package com.khwish.backend.controllers;

import com.khwish.backend.responses.base.BaseResponse;
import com.khwish.backend.services.GiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/gift")
public class GiftController {

    private GiftService giftService;

    @Autowired
    public GiftController(GiftService giftService) {
        this.giftService = giftService;
    }

    @PostMapping(value = "/instamojo-webhook", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    private BaseResponse registerWebhook(@RequestParam("amount") Double amount,
                                         @RequestParam("buyer") String contributorEmail,
                                         @RequestParam("buyer_name") String contributorName,
                                         @RequestParam("buyer_phone") String contributorPhoneNumber,
                                         @RequestParam("currency") String currency,
                                         @RequestParam("fees") Double fees,
                                         @RequestParam("longurl") String longUrl,
                                         @RequestParam("mac") String mac,
                                         @RequestParam("payment_id") String paymentId,
                                         @RequestParam("payment_request_id") String paymentRequestId,
                                         @RequestParam("purpose") String purpose,
                                         @RequestParam("shorturl") String shortUrl,
                                         @RequestParam("status") String status,
                                         HttpServletResponse response) {
        BaseResponse result = giftService.registerWebHook(amount, contributorEmail, contributorName, contributorPhoneNumber,
                currency, fees, longUrl, mac, paymentId, paymentRequestId, purpose, shortUrl, status);
        response.setStatus(result.getStatusCode());
        return result;
    }
}