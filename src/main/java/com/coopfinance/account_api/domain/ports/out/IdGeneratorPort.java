package com.coopfinance.account_api.domain.ports.out;

import java.util.UUID;

public interface IdGeneratorPort {
    UUID nextId();
}