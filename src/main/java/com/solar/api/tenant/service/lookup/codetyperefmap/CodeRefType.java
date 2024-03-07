package com.solar.api.tenant.service.lookup.codetyperefmap;

import com.microsoft.azure.storage.StorageException;

import java.io.IOException;
import java.net.URISyntaxException;

public interface CodeRefType {
    Object doPostConversationHead(Object... params) throws URISyntaxException, IOException, StorageException;

    Object doPostConversationHistory(Object... params) throws URISyntaxException, IOException, StorageException;

    Object doPostConversationHistoryReply(Object... params) throws URISyntaxException, IOException, StorageException;
}
