package com.solar.api.saas.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.microsoft.azure.storage.StorageException;
import com.solar.api.saas.mapper.companyPreference.CompanyUploadDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URISyntaxException;
import java.util.List;

public interface StorageService {

    CompanyUploadDTO storeCompanyFAQ(MultipartFile file, Long companyKey);

    CompanyUploadDTO storeCompanyLogo(MultipartFile file, Long companyKey);

    CompanyUploadDTO storeCompanyBanner(List<MultipartFile> file, String bannerList, Long companyKey) throws JsonProcessingException, NoSuchFieldException;

    CompanyUploadDTO addOrUpdateBannerByIndex(MultipartFile file, String bannerList, Long companyKey) throws JsonProcessingException, NoSuchFieldException;

    String getBlobUrl(String container, String directoryReference, String fileName);

    String storeInContainer(MultipartFile file, String container, String directoryString, String fileName,
                            Long compKey, Boolean relativeUrl) throws URISyntaxException, StorageException, IOException;

    String uploadInputStream(InputStream inputStream, Long length, String container, String directoryReference,
                             String fileName, Long compKey, Boolean relativeUrl) throws URISyntaxException,
            StorageException, UnsupportedEncodingException;

    Boolean downloadToOutputStream(OutputStream outputStream, String container, String relativeFileUrl);

    String uploadByteArray(byte[] bytes, String container, String directoryReference, String fileName)
            throws URISyntaxException, StorageException;

    byte[] downloadToByteArray(String container, String url, String name);

    File getBlob(String container, String url, String name);

    void deleteBlob(String container, String fileName, Long compKey, String path);

    MultipartFile convertFileToMultipart(File file);

    List<String> getBlobUrl(String directoryReference, String storageContainer) throws URISyntaxException, StorageException;

    void deleteBlob(String container, String fileName, String path);

}
