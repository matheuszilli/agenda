package com.agenda.app.repository;

import com.agenda.app.model.AccountHold;
import com.agenda.app.model.AccountHoldStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountHoldRepository extends JpaRepository<AccountHold, UUID> {

}