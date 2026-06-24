package com.gst.service;

import com.gst.entity.Customer;
import com.gst.entity.Invoice;
import com.gst.entity.InvoiceItem;
import com.gst.entity.User;
import com.gst.repository.CustomerRepository;
import com.gst.repository.InvoiceRepository;
import com.gst.repository.UserRepository;
import com.gst.responseDto.CustomerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;

    @Override
    public List<CustomerResponse> getAllCustomers() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Customer> customers =
                customerRepository.findByUserIdOrderByIdDesc(user.getId());

        return customers.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public CustomerResponse getCustomerById(Long id) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Customer customer = customerRepository
                .findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return mapToResponse(customer);
    }

    private CustomerResponse mapToResponse(Customer customer) {

        List<Invoice> invoices =
                invoiceRepository.findByCustomerIdOrderByInvoiceDateDesc(customer.getId());

        long totalInvoices = invoices.size();

        long totalProducts = 0;
        double totalBusiness = 0;
        double totalPaid = 0;
        double totalDue = 0;

        for (Invoice invoice : invoices) {

            totalBusiness += invoice.getGrandTotal();
            totalPaid += invoice.getPaidAmount();
            totalDue += invoice.getDueAmount();

            if (invoice.getItems() != null) {
                for (InvoiceItem item : invoice.getItems()) {
                    totalProducts += item.getQty();
                }
            }
        }

        // Customer Since (Oldest Invoice)
        var customerSince = invoices.isEmpty()
                ? null
                : invoices.get(invoices.size() - 1).getInvoiceDate();

        // Last Purchase (Latest Invoice)
        var lastPurchaseDate = invoices.isEmpty()
                ? null
                : invoices.get(0).getInvoiceDate();

        // Last Payment Date
        var lastPaymentDate = invoices.stream()
                .filter(i -> i.getPaidAmount() > 0)
                .map(Invoice::getInvoiceDate)
                .findFirst()
                .orElse(null);

        // Last Payment Amount
        double lastPaymentAmount = invoices.stream()
                .filter(i -> i.getPaidAmount() > 0)
                .mapToDouble(Invoice::getPaidAmount)
                .findFirst()
                .orElse(0);

        // Last Due Date
        var lastDueDate = invoices.stream()
                .filter(i -> i.getDueAmount() > 0)
                .map(Invoice::getInvoiceDate)
                .findFirst()
                .orElse(null);

        // Same Day Purchases
        int todayPurchaseCount = lastPurchaseDate == null ? 0 :
                (int) invoices.stream()
                        .filter(i -> i.getInvoiceDate().equals(lastPurchaseDate))
                        .count();

        // Same Day Payments
        int todayPaymentCount = lastPaymentDate == null ? 0 :
                (int) invoices.stream()
                        .filter(i ->
                                i.getInvoiceDate().equals(lastPaymentDate)
                                        && i.getPaidAmount() > 0)
                        .count();

        // Same Day Due
        int todayDueCount = lastDueDate == null ? 0 :
                (int) invoices.stream()
                        .filter(i ->
                                i.getInvoiceDate().equals(lastDueDate)
                                        && i.getDueAmount() > 0)
                        .count();

        return CustomerResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .address(customer.getAddress())

                .totalInvoices(totalInvoices)
                .totalProducts(totalProducts)

                .totalBusiness(totalBusiness)
                .totalPaid(totalPaid)
                .totalDue(totalDue)

                .customerSince(customerSince)

                .lastPurchaseDate(lastPurchaseDate)
               
                .todayPurchaseCount(todayPurchaseCount)

                .lastPaymentDate(lastPaymentDate)
                .lastPaymentAmount(lastPaymentAmount)
                .todayPaymentCount(todayPaymentCount)

                .lastDueDate(lastDueDate)
                .todayDueCount(todayDueCount)

                .status(totalDue > 0 ? "Due" : "Paid")
                .build();
    }
}