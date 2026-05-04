package com.coopfinance.account_api.application.ports.out.generator;

import java.util.UUID;

public interface IdGenerator {
    UUID nextId();
}