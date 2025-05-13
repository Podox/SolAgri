package com.example.solagri.repository;

import com.example.solagri.model.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {

    // Custom method to get tickets by user
    List<SupportTicket> findByUserId(Long userId);
}