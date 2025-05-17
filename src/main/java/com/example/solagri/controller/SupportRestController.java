// REST API version of SupportController for Angular frontend
package com.example.solagri.controller;

import com.example.solagri.dto.SupportTicketDTO;
import com.example.solagri.model.SupportTicket;
import com.example.solagri.model.User;
import com.example.solagri.repository.SupportTicketRepository;
import com.example.solagri.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/support")
public class SupportRestController {

    @Autowired
    private SupportTicketRepository supportTicketRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getUserTickets(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(401).body("Not authenticated");

        List<SupportTicket> tickets = supportTicketRepository.findByUserId(user.getId());
        return ResponseEntity.ok(tickets);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTicket(@RequestBody SupportTicketDTO newTicket, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(401).body("Not authenticated");

        SupportTicket ticket = new SupportTicket(newTicket.getSubject(), newTicket.getDescription(), user);
        supportTicketRepository.save(ticket);
        return ResponseEntity.ok(ticket);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> viewTicket(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(401).body("Not authenticated");

        SupportTicket ticket = supportTicketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ticket ID"));

        if (!ticket.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body("Unauthorized access");
        }

        return ResponseEntity.ok(ticket);
    }

    @GetMapping("/admin")
    public ResponseEntity<?> viewAllTickets(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"admin".equals(user.getUsername())) {
            return ResponseEntity.status(403).body("Admin only access");
        }

        List<SupportTicket> tickets = supportTicketRepository.findAll();
        return ResponseEntity.ok(tickets);
    }

    @PostMapping("/admin/update-status")
    public ResponseEntity<?> updateTicketStatus(@RequestParam Long ticketId,
                                                @RequestParam String status,
                                                HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"admin".equals(user.getUsername())) {
            return ResponseEntity.status(403).body("Admin only access");
        }

        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ticket ID"));

        ticket.setStatus(status);
        ticket.setUpdatedAt(LocalDateTime.now());
        supportTicketRepository.save(ticket);

        return ResponseEntity.ok(ticket);
    }
}
