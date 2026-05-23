package com.tictactoe.session.service;

import com.tictactoe.common.dto.MoveDTO;
import com.tictactoe.common.dto.SessionDTO;
import com.tictactoe.common.enums.SessionStatus;
import com.tictactoe.common.exception.SessionNotFoundException;
import com.tictactoe.session.entity.Move;
import com.tictactoe.session.entity.Session;
import com.tictactoe.session.repository.SessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Transactional
    public SessionDTO createSession() {
        String sessionId = UUID.randomUUID().toString();
        Session session = new Session(sessionId);
        Session savedSession = sessionRepository.save(session);
        return toDTO(savedSession);
    }

    public SessionDTO getSession(String sessionId) {
        Session session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new SessionNotFoundException(sessionId));
        return toDTO(session);
    }

    @Transactional
    public void updateSessionStatus(String sessionId, SessionStatus status) {
        Session session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new SessionNotFoundException(sessionId));
        session.setStatus(status);
        sessionRepository.save(session);
    }

    private SessionDTO toDTO(Session session) {
        List<MoveDTO> moveDTOs = session.getMoveHistory().stream()
            .map(move -> new MoveDTO(move.getPlayer(), move.getPosition(), move.getTimestamp()))
            .collect(Collectors.toList());

        return new SessionDTO(
            session.getSessionId(),
            session.getStatus(),
            session.getWinner(),
            moveDTOs
        );
    }
}
