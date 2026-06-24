package com.gst.service;

import com.gst.entity.*;
import com.gst.repository.*;
import com.gst.requestDto.InvoiceItemRequest;
import com.gst.requestDto.InvoiceRequest;
import com.gst.responseDto.DashboardResponse;
import com.gst.responseDto.InvoiceItemResponse;
import com.gst.responseDto.InvoiceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final CustomerRepository customerRepository;
    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;


    @Override
    public InvoiceResponse createInvoice(InvoiceRequest request) {

        // Logged In User
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Business
        Business business = businessRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Business not found"));

        // Customer
        Customer customer = customerRepository
                .findByPhoneAndUserId(request.getCustomer().getPhone(), user.getId())
                .orElse(new Customer());

        customer.setName(request.getCustomer().getName());
        customer.setPhone(request.getCustomer().getPhone());
        customer.setEmail(request.getCustomer().getEmail());
        customer.setAddress(request.getCustomer().getAddress());
        customer.setUser(user);

        customer = customerRepository.save(customer);

        // Invoice
        Invoice invoice = new Invoice();

        invoice.setInvoiceNo("INV-" + System.currentTimeMillis());
        invoice.setInvoiceDate(LocalDate.now());

        invoice.setSubtotal(request.getSubtotal());
        invoice.setDiscountAmount(request.getDiscountAmount());
        invoice.setGstAmount(request.getGstAmount());
        invoice.setGrandTotal(request.getGrandTotal());

        invoice.setPaidAmount(request.getPaidAmount());
        invoice.setDueAmount(request.getDueAmount());

        invoice.setBusiness(business);
        invoice.setCustomer(customer);
        invoice.setUser(user);

        // Invoice Items
        List<InvoiceItem> items = new ArrayList<>();

        for (InvoiceItemRequest itemRequest : request.getProducts()) {

            InvoiceItem item = new InvoiceItem();

            item.setProduct(itemRequest.getProduct());
            item.setQty(itemRequest.getQty());
            item.setPrice(itemRequest.getPrice());
            item.setDiscount(itemRequest.getDiscount());
            item.setGst(itemRequest.getGst());

            double sub = itemRequest.getQty() * itemRequest.getPrice();

            double discount =
                    sub * (itemRequest.getDiscount() == null ? 0 : itemRequest.getDiscount()) / 100;

            double taxable = sub - discount;

            double gst =
                    taxable * (itemRequest.getGst() == null ? 0 : itemRequest.getGst()) / 100;

            item.setTotal(taxable + gst);

            item.setInvoice(invoice);

            items.add(item);
        }

        invoice.setItems(items);

        invoice = invoiceRepository.save(invoice);

        return mapToResponse(invoice);
    }


    private InvoiceResponse mapToResponse(Invoice invoice) {

        List<InvoiceItemResponse> itemResponses = invoice.getItems()
                .stream()
                .map(item -> InvoiceItemResponse.builder()
                        .id(item.getId())
                        .product(item.getProduct())
                        .qty(item.getQty())
                        .price(item.getPrice())
                        .discount(item.getDiscount())
                        .gst(item.getGst())
                        .total(item.getTotal())
                        .build())
                .toList();

        InvoiceResponse response = new InvoiceResponse();

        response.setId(invoice.getId());

        response.setInvoiceNo(invoice.getInvoiceNo());
        response.setInvoiceDate(invoice.getInvoiceDate());

        // Business
        response.setBusinessId(invoice.getBusiness().getId());
        response.setBusinessName(invoice.getBusiness().getName());
        response.setBusinessGst(invoice.getBusiness().getGst());

        response.setBusinessAddress(invoice.getBusiness().getAddress());
        response.setBusinessPhone(invoice.getBusiness().getPhone());
        response.setBusinessEmail(invoice.getBusiness().getEmail());




        // Customer
        response.setCustomerId(invoice.getCustomer().getId());
        response.setCustomerName(invoice.getCustomer().getName());
        response.setCustomerPhone(invoice.getCustomer().getPhone());
        response.setCustomerEmail(invoice.getCustomer().getEmail());
        response.setCustomerAddress(invoice.getCustomer().getAddress());

        // Products
        response.setItems(itemResponses);

        // Summary
        response.setSubtotal(invoice.getSubtotal());
        response.setDiscountAmount(invoice.getDiscountAmount());
        response.setGstAmount(invoice.getGstAmount());
        response.setGrandTotal(invoice.getGrandTotal());

        // Payment
        response.setPaidAmount(invoice.getPaidAmount());
        response.setDueAmount(invoice.getDueAmount());

        response.setLastPaymentDate(invoice.getLastPaymentDate());
        response.setLastDueUpdateDate(invoice.getLastDueUpdateDate());

        return response;
    }


    @Override
    public List<InvoiceResponse> getAllInvoices() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return invoiceRepository
                .findByUserIdOrderByIdDesc(user.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }



    @Override
    public InvoiceResponse getInvoiceById(Long id) {

        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        return mapToResponse(invoice);
    }

    @Override
    public void deleteInvoice(Long id) {

        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        invoiceRepository.delete(invoice);
    }


    //recent invoice


    @Override
    public List<InvoiceResponse> getInvoicesByDate(LocalDate date) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return invoiceRepository
                .findByUserIdAndInvoiceDateOrderByIdDesc(user.getId(), date)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    //dashboard

    @Override
    public DashboardResponse getDashboard() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Invoice> invoices = invoiceRepository.findByUserId(user.getId());

        long totalInvoices = invoices.size();
        long totalCustomers = customerRepository.countByUserId(user.getId());

        long totalProductsSold = 0;

        double totalRevenue = 0;
        double totalDiscount = 0;
        double totalGST = 0;
        double totalReceived = 0;
        double totalDue = 0;

        // Today
        // Today
        LocalDate today = LocalDate.now();

        double todaySale = 0;
        double todayReceived = 0;
        double todayDue = 0;
        long todayInvoices = 0;

        Set<Long> customerIds = new HashSet<>();

// Week
        LocalDate weekStart = today.with(java.time.DayOfWeek.MONDAY);

        double weekSale = 0;
        double weekReceived = 0;
        double weekDue = 0;
        long weekInvoices = 0;

        Set<Long> weekCustomerIds = new HashSet<>();

// Month
        LocalDate monthStart = today.withDayOfMonth(1);

        double monthSale = 0;
        double monthReceived = 0;
        double monthDue = 0;
        long monthInvoices = 0;

        Set<Long> monthCustomerIds = new HashSet<>();

// Year
        LocalDate yearStart = today.withDayOfYear(1);

        double yearSale = 0;
        double yearReceived = 0;
        double yearDue = 0;
        long yearInvoices = 0;

        Set<Long> yearCustomerIds = new HashSet<>();

        for (Invoice invoice : invoices) {

            totalRevenue += invoice.getGrandTotal();
            totalDiscount += invoice.getDiscountAmount();
            totalGST += invoice.getGstAmount();
            totalReceived += invoice.getPaidAmount();
            totalDue += invoice.getDueAmount();

            LocalDate invoiceDate = invoice.getInvoiceDate();

            // Products
            if (invoice.getItems() != null) {
                for (InvoiceItem item : invoice.getItems()) {
                    totalProductsSold += item.getQty();
                }
            }

            // ================= TODAY =================

            if (invoiceDate.equals(today)) {

                todaySale += invoice.getGrandTotal();
                todayReceived += invoice.getPaidAmount();
                todayDue += invoice.getDueAmount();
                todayInvoices++;

                if (invoice.getCustomer() != null) {
                    customerIds.add(invoice.getCustomer().getId());
                }
            }

            // ================= WEEK =================

            if (!invoiceDate.isBefore(weekStart)) {

                weekSale += invoice.getGrandTotal();
                weekReceived += invoice.getPaidAmount();
                weekDue += invoice.getDueAmount();
                weekInvoices++;

                if (invoice.getCustomer() != null) {
                    weekCustomerIds.add(invoice.getCustomer().getId());
                }
            }

            // ================= MONTH =================

            if (!invoiceDate.isBefore(monthStart)) {

                monthSale += invoice.getGrandTotal();
                monthReceived += invoice.getPaidAmount();
                monthDue += invoice.getDueAmount();
                monthInvoices++;

                if (invoice.getCustomer() != null) {
                    monthCustomerIds.add(invoice.getCustomer().getId());
                }
            }

            // ================= YEAR =================

            if (!invoiceDate.isBefore(yearStart)) {

                yearSale += invoice.getGrandTotal();
                yearReceived += invoice.getPaidAmount();
                yearDue += invoice.getDueAmount();
                yearInvoices++;

                if (invoice.getCustomer() != null) {
                    yearCustomerIds.add(invoice.getCustomer().getId());
                }
            }
        }

        long todayCustomers = customerIds.size();
        long weekCustomers = weekCustomerIds.size();
        long monthCustomers = monthCustomerIds.size();
        long yearCustomers = yearCustomerIds.size();

        return DashboardResponse.builder()

                // Overall
                .totalInvoices(totalInvoices)
                .totalCustomers(totalCustomers)
                .totalProductsSold(totalProductsSold)

                .totalRevenue(totalRevenue)
                .totalDiscount(totalDiscount)
                .totalGST(totalGST)
                .totalReceived(totalReceived)
                .totalDue(totalDue)

                // Today
                .todaySale(todaySale)
                .todayReceived(todayReceived)
                .todayDue(todayDue)
                .todayCustomers(todayCustomers)
                .todayInvoices(todayInvoices)

                // Week
                .weekSale(weekSale)
                .weekReceived(weekReceived)
                .weekDue(weekDue)
                .weekCustomers(weekCustomers)
                .weekInvoices(weekInvoices)

                // Month
                .monthSale(monthSale)
                .monthReceived(monthReceived)
                .monthDue(monthDue)
                .monthCustomers(monthCustomers)
                .monthInvoices(monthInvoices)

                // Year
                .yearSale(yearSale)
                .yearReceived(yearReceived)
                .yearDue(yearDue)
                .yearCustomers(yearCustomers)
                .yearInvoices(yearInvoices)

                .build();
    }


    @Override
    public InvoiceResponse updatePayment(Long id, InvoiceRequest request) {

        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        // Input value = Deposit Amount
        Double depositAmount = request.getDueAmount();

        if (depositAmount == null || depositAmount <= 0) {
            throw new RuntimeException("Enter valid amount");
        }

        double currentPaid = invoice.getPaidAmount();
        double currentDue = invoice.getDueAmount();

        if (depositAmount > currentDue) {
            throw new RuntimeException("Deposit amount cannot be greater than Due Amount");
        }

        invoice.setPaidAmount(currentPaid + depositAmount);

        invoice.setDueAmount(currentDue - depositAmount);

        invoice.setLastPaymentDate(LocalDateTime.now());
        invoice.setLastDueUpdateDate(LocalDateTime.now());

        invoice = invoiceRepository.save(invoice);

        return mapToResponse(invoice);
    }


    @Override
    @Transactional
    public InvoiceResponse updateInvoice(Long id, InvoiceRequest request) {

        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        // Update Customer
        Customer customer = invoice.getCustomer();

        customer.setName(request.getCustomer().getName());
        customer.setPhone(request.getCustomer().getPhone());
        customer.setEmail(request.getCustomer().getEmail());
        customer.setAddress(request.getCustomer().getAddress());

        customerRepository.save(customer);

        // Update Invoice Summary
        invoice.setSubtotal(request.getSubtotal());
        invoice.setDiscountAmount(request.getDiscountAmount());
        invoice.setGstAmount(request.getGstAmount());
        invoice.setGrandTotal(request.getGrandTotal());

        invoice.setPaidAmount(request.getPaidAmount());
        invoice.setDueAmount(request.getDueAmount());

        // Remove old items
        invoice.getItems().clear();

        // Add new items
        for (InvoiceItemRequest itemRequest : request.getProducts()) {

            InvoiceItem item = new InvoiceItem();

            item.setProduct(itemRequest.getProduct());
            item.setQty(itemRequest.getQty());
            item.setPrice(itemRequest.getPrice());
            item.setDiscount(itemRequest.getDiscount());
            item.setGst(itemRequest.getGst());

            double sub = itemRequest.getQty() * itemRequest.getPrice();

            double discount =
                    sub * (itemRequest.getDiscount() == null ? 0 : itemRequest.getDiscount()) / 100;

            double taxable = sub - discount;

            double gst =
                    taxable * (itemRequest.getGst() == null ? 0 : itemRequest.getGst()) / 100;

            item.setTotal(taxable + gst);

            item.setInvoice(invoice);

            invoice.getItems().add(item);
        }

        Invoice updatedInvoice = invoiceRepository.save(invoice);

        return mapToResponse(updatedInvoice);
    }



    @Override
    @Transactional
    public void updatePdfUrl(Long invoiceId, String pdfUrl) {

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        invoice.setPdfUrl(pdfUrl);

        invoiceRepository.save(invoice);
    }

}