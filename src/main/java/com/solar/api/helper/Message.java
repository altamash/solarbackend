package com.solar.api.helper;

import java.util.Arrays;

public enum Message {
    ORG_DETAIL_GET_LINKED_CONTRACTS("linked contracts list returned successfully"),
    SUB_DETAIL_GET_VARIANT_SUBSCRIPTIONS("Subscription list returned successfully"),
    WO_GET_List("Work order list returned successfully"),
    WO_GET_Details("Work order details returned successfully"),
    ORG_DETAIL_REMOVE_LINKED_CONTRACTS_SUCCESSFULLY("linked contract removed successfully"),
    ERROR_ORG_DETAIL_REMOVE_LINKED_CONTRACTS("can n't delete linked contract"),
    ERROR_ORG_DETAIL_GET_LINKED_CONTRACTS("linked contracts not found"),
    ERROR_SAVE_RESOURCES_FAILED("Can n't save resources"),
    MSG_SAVE_RESOURCES_SUCCESSFULLY("Resources saved Successfully"),
    E_582("Actual bill credit and MPA not found for bill"),
    E_583("Actual bill credit not found for bill"),
    E_584("MPA not found for bill"),
    E_585("Power generation record not found for bill"),
    E_586("Invalid subscription information"),
    E_587("SRTE not found for bill");

    String message;

    Message(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public static Message get(String message) {
        return Arrays.stream(values()).filter(value -> message.equalsIgnoreCase(value.message)).findFirst().orElse(null);
    }
}
