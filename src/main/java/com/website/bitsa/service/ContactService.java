package com.website.bitsa.service;


import com.website.bitsa.dto.request.ContactRequest;
import com.website.bitsa.exception.ResourceNotFoundException;
import com.website.bitsa.model.ContactMessage;
import com.website.bitsa.repository.ContactMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactMessageRepository contactMessageRepository;

    @Transactional
    public ContactMessage submitContactMessage(ContactRequest request) {
        ContactMessage message = ContactMessage.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .subject(request.getSubject())
                .message(request.getMessage())
                .category(request.getCategory())
                .priority("NORMAL")
                .isRead(false)
                .isReplied(false)
                .isSpam(false)
                .build();

        return contactMessageRepository.save(message);
    }

    public List<ContactMessage> getAllMessages() {
        return contactMessageRepository.findByIsSpamFalseOrderByCreatedAtDesc();
    }

    public List<ContactMessage> getUnreadMessages() {
        return contactMessageRepository.findByIsReadFalseOrderByCreatedAtDesc();
    }

    public List<ContactMessage> getPendingMessages() {
        return contactMessageRepository.findByIsReadFalseAndIsRepliedFalseOrderByCreatedAtDesc();
    }

    public List<ContactMessage> getMessagesByCategory(String category) {
        return contactMessageRepository.findByCategoryOrderByCreatedAtDesc(category);
    }

    public ContactMessage getMessageById(Long id) {
        return contactMessageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));
    }

    @Transactional
    public ContactMessage markAsRead(Long id, Long adminId) {
        ContactMessage message = getMessageById(id);
        message.markAsRead(adminId);
        return contactMessageRepository.save(message);
    }

    @Transactional
    public ContactMessage replyToMessage(Long id, String replyText, Long adminId) {
        ContactMessage message = getMessageById(id);
        message.reply(replyText, adminId);
        return contactMessageRepository.save(message);
    }

    @Transactional
    public ContactMessage markAsSpam(Long id) {
        ContactMessage message = getMessageById(id);
        message.markAsSpam();
        return contactMessageRepository.save(message);
    }

    public void deleteMessage(Long id) {
        ContactMessage message = getMessageById(id);
        contactMessageRepository.delete(message);
    }

    public long countUnreadMessages() {
        return contactMessageRepository.countByIsReadFalse();
    }

    public long countPendingMessages() {
        return contactMessageRepository.countPendingMessages();
    }

    public List<ContactMessage> getRecentMessages() {
        return contactMessageRepository.findRecentMessages(LocalDateTime.now().minusHours(24));
    }

    public List<ContactMessage> searchMessages(String keyword) {
        return contactMessageRepository.searchAllFields(keyword);
    }
}