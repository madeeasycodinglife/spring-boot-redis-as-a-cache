package com.madeeasy.service.impl;

import com.madeeasy.entity.Invoice;
import com.madeeasy.error.InvoiceNotFoundException;
import com.madeeasy.repository.InvoiceRepository;
import com.madeeasy.service.InvoiceService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepo;

    @Override
    public Invoice saveInvoice(Invoice inv) {

        return invoiceRepo.save(inv);
    }

    @Override
    @CachePut(value = "Invoice", key = "#invId")
    public Invoice updateInvoice(Invoice inv, Integer invId) {
        Invoice invoice = invoiceRepo.findById(invId)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice Not Found"));
        invoice.setInvAmount(inv.getInvAmount());
        invoice.setInvName(inv.getInvName());
        return invoiceRepo.save(invoice);
    }

    @Override
    @CacheEvict(value = "Invoice", key = "#invId")
    // @CacheEvict(value="Invoice", allEntries=true) //in case there are multiple records to delete
    public void deleteInvoice(Integer invId) {
        Invoice invoice = invoiceRepo.findById(invId)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice Not Found"));
        invoiceRepo.delete(invoice);
    }

    @Override
    @Cacheable(value = "Invoice", key = "#invId")
    public Invoice getOneInvoice(Integer invId) {
        Invoice invoice = invoiceRepo.findById(invId)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice Not Found"));
        return invoice;
    }

    @Override
    @Cacheable(value = "Invoice")
    public List<Invoice> getAllInvoices() {
        return invoiceRepo.findAll();
    }
    /**
     * For knowledge purpose
     *
     * //----------------------When to Use @Caching Annotation?---------------------------
     *
     * Suppose we want to apply multiple caching annotations on a particular method, let’s try it with an example.
     *
     * @CacheEvict("Invoice")
     * @CacheEvict(value="Invoice", key="#invId")
     * public void deleteInvoice(Integer invId) {
     *     ....
     * }
     *
     * The above code would have a compilation error, since Java does not allow multiple annotations of the same type to be
     * declared for a given method. In this case we should use @Caching annotation. For example, below code demonstrates the concept.
     *
     * @Caching(
     *   evict = {@CacheEvict("Invoice"), @CacheEvict(value="Invoice", key="#invId")
     * })
     * public void deleteInvoice(Integer invId) {
     *      ....
     * }
     *
     *
     * Similarly, if we have annotations of different type, we can group them using @Caching annotation for better readability. However, below code will not have any compilation error. For example,
     *
     * @CacheEvict(value = "usersList", allEntries = true)
     * @CachePut(value = "user", key = "#user.getUserId()")
     * public User updateUser(User user) {
     *     ....
     * }
     *
     *
     * The above code can be converted as below:
     *
     * @Caching(
     *    evict = {@CacheEvict(value = "usersList", allEntries = true)},
     *    put   = {@CachePut(value = "user", key = "#user.getUserId()")}
     * )
     * public User updateUser(User user) {
     *    ....
     * }
     * //-----------------------------How to Implement Conditional Caching using Annotations?---------------------------------
     *
     * If we have some requirement when we need to cache data only on a particular condition, we can parameterize our annotation with two parameters: ‘condition’ and ‘unless’. They accept a SpEL expression and ensures that the results are cached based on evaluating that expression. This kind of conditional caching can be useful for managing large amount of results. For example, let’s observe one example from each parameter.
     *
     * Below Example demonstrates the concept of ‘condition’ parameter.
     *
     * @CachePut(value="invoices", condition="#invoice.amount>=2496")
     * public String getInvoice(Invoice invoice) {
     *     ...
     * }
     *
     * Now, let’s see the concept of ‘unless’ parameter from the example below:
     *
     * @CachePut(value="invoices", unless="#result.length()<24")
     * public String getInvoice(Invoice invoice) {
     *     ...
     * }
     *
     * The above annotation would cache addresses unless they were shorter than 24 characters.
     *
     * The ‘condition’ and ‘unless’ parameters can be used in conjunction with all the caching annotations.
     */
}
