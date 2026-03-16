package com.algaworks.billing.presentention;

import com.algaworks.billing.application.invoice.input.GenerateInvoiceInput;
import com.algaworks.billing.application.invoice.output.InvoiceOutput;
import com.algaworks.billing.application.invoice.query.InvoiceQueryService;
import com.algaworks.billing.application.invoice.service.InvoiceManagementApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders/{orderId}/invoice")
@RequiredArgsConstructor
@Slf4j
public class InvoiceController {

    private final InvoiceQueryService  invoiceQueryService;
    private final InvoiceManagementApplicationService invoiceManagementApplicationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceOutput generate(@PathVariable String orderId, @RequestBody @Valid GenerateInvoiceInput input) {
        input.setOrderId(orderId);
        UUID invoice = invoiceManagementApplicationService.generate(input);
        try {
            invoiceManagementApplicationService.processPayment(invoice);
        } catch (Exception e) {
            log.error(String.format("Error processing invoice %s",  invoice), e);
        }
        return invoiceQueryService.findByOrderId(orderId);
    }

    @GetMapping
    public InvoiceOutput findOne(@PathVariable String orderId) {
        return invoiceQueryService.findByOrderId(orderId);
    }
}
