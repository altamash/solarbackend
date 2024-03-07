package com.solar.api.tenant.service.contract.lookup;

import com.microsoft.azure.storage.StorageException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface CodeTypeRefMap {
    Object doPostAttachment(String refCode, List<MultipartFile> multipartFiles, String directoryString, String filePath,
                            String codeRefType, String codeRefId, Object o) throws URISyntaxException, IOException, StorageException;
}
