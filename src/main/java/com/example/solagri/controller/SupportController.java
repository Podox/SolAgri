package com.example.solagri.controller;

import com.example.solagri.dto.SupportTicketDTO;
import com.example.solagri.model.SupportTicket;
import com.example.solagri.model.User;
import com.example.solagri.repository.SupportTicketRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/support")
public class SupportController {

    @Autowired
    private SupportTicketRepository supportTicketRepository;

    // FAQ page (Optional)
    @GetMapping("/faqs")
    public String viewFaqs(Model model) {
        // Add FAQs if applicable
        return "faqs";
    }

    // Display support ticket form and list user's tickets
    @GetMapping
    public String supportPage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        List<SupportTicket> tickets = supportTicketRepository.findByUserId(user.getId());
        model.addAttribute("tickets", tickets);
        model.addAttribute("newTicket", new SupportTicketDTO());
        return "support";
    }

    // Handle ticket creation
    @PostMapping("/create")
    public String createTicket(@ModelAttribute SupportTicketDTO newTicket, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        SupportTicket ticket = new SupportTicket(newTicket.getSubject(), newTicket.getDescription(), user);
        supportTicketRepository.save(ticket);

        return "redirect:/support";
    }

    // View a specific ticket (Optional)
    @GetMapping("/{id}")
    public String viewTicket(@PathVariable Long id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        SupportTicket ticket = supportTicketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ticket ID"));

        if (!ticket.getUser().getId().equals(user.getId())) {
            model.addAttribute("error", "You are not authorized to view this ticket.");
            return "support";
        }

        model.addAttribute("ticket", ticket);
        return "view_ticket"; // Create a separate template for individual ticket if needed
    }
    // ADMIN: View all tickets
    @GetMapping("/admin")
    public String viewAllTickets(Model model, HttpSession session) {
        // Optional: Implement admin-only access here
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getUsername().equals("admin")) { // Example: Admin username
            return "redirect:/login";
        }

        List<SupportTicket> tickets = supportTicketRepository.findAll();
        model.addAttribute("tickets", tickets);
        return "all_tickets";
    }

    // ADMIN: Update ticket status
    @PostMapping("/admin/update-status")
    public String updateTicketStatus(@RequestParam Long ticketId, @RequestParam String status, HttpSession session) {
        // Optional: Implement admin-only access validation
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getUsername().equals("admin")) {
            return "redirect:/login";
        }

        // Fetch the ticket
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Ticket ID"));

        // Update status
        ticket.setStatus(status);
        ticket.setUpdatedAt(LocalDateTime.now());
        supportTicketRepository.save(ticket);

        // Redirect back to admin tickets listing
        return "redirect:/support/admin";
    }
}