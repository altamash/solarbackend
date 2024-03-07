package com.solar.api.saas.service;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.storage.StorageCredentials;
import com.microsoft.azure.storage.StorageCredentialsSharedAccessSignature;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.*;
import com.solar.api.exception.NotFoundException;
import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.mapper.companyPreference.BannerMapper;
import com.solar.api.saas.mapper.companyPreference.CompanyPreferenceMapper;
import com.solar.api.saas.mapper.companyPreference.CompanyUploadDTO;
import com.solar.api.saas.model.tenant.MasterTenant;
import com.solar.api.tenant.model.companyPreference.Banner;
import com.solar.api.tenant.model.companyPreference.CompanyPreference;
import com.solar.api.tenant.repository.BannerRepository;
import com.solar.api.tenant.repository.CompanyPreferenceRepository;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class StorageServiceImpl implements StorageService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Value("${app.storage.azureBlobSasToken}")
    private String sasToken;
    @Value("${app.storage.blobService}")
    private String blogService;
    @Value("${app.storage.container}")
    private String storageContainer;

    @Value("${app.storage.saasDirectory}")
    private String saas;
    @Value("${app.storage.tenantDirectory}")
    private String tenant;
    @Value("${app.storage.publicContainer}")
    private String publicUrl;
    private CloudBlobDirectory saasDirectory;
    private CloudBlobDirectory tenantDirectory;
    private BlobContainerClient publicContainer;

    @Autowired
    private MasterTenantService masterTenantService;

    //    private final String SAAS_CONTAINER = appProfile + "saas";
    private static final String FREEQUENTLY_ASKED_QUESTIONS_SUFFIX = "-FreequentlyAskedQuestions.pdf";
    private static final String LOGO_SUFFIX = "-company-logo.png";
    private static final String BANNER_SUFFIX = "-company-banner.png";
    private static final String TEMPLATE = "template";

    //Azure BillingCredits File
    public static final String PREFIX = "tempFile";
    public static final String SUFFIX = ".csv";
    int fileSequence = 0;

    @Autowired
    private CompanyPreferenceRepository companyPreferenceRepository;

    @Autowired
    BannerRepository bannerRepository;

    @PostConstruct
    public void init() throws URISyntaxException, StorageException {
        saasDirectory = createDirectory(saas);
        tenantDirectory = createDirectory(tenant);
        publicContainer = getBlobContainer(publicUrl);
        createDirectory(publicUrl, tenant);
    }

    /**
     * FAQs
     *
     * @param file
     * @param compKey
     * @return
     */
    @Override
    public CompanyUploadDTO storeCompanyFAQ(MultipartFile file, Long compKey) {
        /* Create a new BlobServiceClient with a SAS Token */
        BlobServiceClient blobServiceClient = getBlobServiceClient();

        String container = publicUrl;
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(container);
        if (!containerClient.exists()) {
            containerClient = blobServiceClient.createBlobContainer(container);
        }

        /* Upload the file to the container */
        String blobUrl = null;
        try {
            Long companyKey = getCompKey();
            BlobClient blobClient =
                    containerClient.getBlobClient(tenant + "/" + companyKey + "/" + companyKey + FREEQUENTLY_ASKED_QUESTIONS_SUFFIX);
            try (ByteArrayInputStream dataStream = new ByteArrayInputStream(file.getBytes())) {
                blobClient.upload(file.getInputStream(), file.getSize(), true);
            }
            blobUrl = blobClient.getBlobUrl();

            CompanyPreference companyPreference = getCompanyPreference();
            companyPreference.setFaqURL(blobUrl);
            addOrUpdateCompanyPreference(companyPreference);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return CompanyUploadDTO.builder().freequentlyAskedQuestionsUrl(blobUrl).build();
    }

    private Long getCompKey() {
        return getCompanyPreference().getCompanyKey();
    }

    private CompanyPreference getCompanyPreference() {
        MasterTenant tenant = masterTenantService.findByDbName(DBContextHolder.getTenantName());
        return companyPreferenceRepository.findByCompanyKeyFetchBanners(tenant.getCompanyKey());
    }

    private CompanyPreference addOrUpdateCompanyPreference(CompanyPreference companyPreference) {
        if (companyPreference.getId() != null) {
            CompanyPreference companyPreferenceData = companyPreferenceRepository.findById(companyPreference.getId())
                    .orElseThrow(() -> new NotFoundException(CompanyPreference.class, companyPreference.getId()));
            if (companyPreferenceData == null) {
                throw new NotFoundException(CompanyPreference.class, companyPreference.getId());
            }
            companyPreferenceData = CompanyPreferenceMapper.toUpdateCompanyPreference(companyPreferenceData,
                    companyPreference);
            return companyPreferenceRepository.save(companyPreferenceData);
        }
        return companyPreferenceRepository.save(companyPreference);
    }

    /**
     * Logo
     *
     * @param file
     * @param compKey
     * @return
     */
    @Override
    public CompanyUploadDTO storeCompanyLogo(MultipartFile file, Long compKey) {
        BlobServiceClient blobServiceClient = getBlobServiceClient();
        String container = publicUrl;
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(container);
        if (!containerClient.exists()) {
            containerClient = blobServiceClient.createBlobContainer(container);
        }
        String blobUrl = null;
        Long companyKey = null;
        try {
            companyKey = getCompKey();
            BlobClient blobClient =
                    containerClient.getBlobClient(tenant + "/" + companyKey + "/" + companyKey + LOGO_SUFFIX);
            try (ByteArrayInputStream dataStream = new ByteArrayInputStream(file.getBytes())) {
                blobClient.upload(file.getInputStream(), file.getSize(), true);
            }
            blobUrl = blobClient.getBlobUrl();
            CompanyPreference companyPreference = getCompanyPreference();
            companyPreference.setLogo(blobUrl);
            addOrUpdateCompanyPreference(companyPreference);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        CompanyUploadDTO companyUploadDTO = CompanyUploadDTO.builder().logoUrl(blobUrl).build();
        MasterTenant company = masterTenantService.findByCompanyKey(companyKey);
        company.setCompanyLogo(companyUploadDTO.getLogoUrl());
        masterTenantService.save(company);
        return companyUploadDTO;
    }

    /**
     * Banner
     *
     * @param files
     * @param compKey
     * @return
     */
    @Override
    public CompanyUploadDTO storeCompanyBanner(List<MultipartFile> files, String bannerList, Long compKey) throws JsonProcessingException, NoSuchFieldException {

        bannerRepository.getLastIndex();
        ObjectMapper mapper = new ObjectMapper();
        Banner[] bannersObject = mapper.readValue(bannerList, Banner[].class);
        BlobServiceClient blobServiceClient = getBlobServiceClient();
        String container = publicUrl;
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(container);
        if (!containerClient.exists()) {
            containerClient = blobServiceClient.createBlobContainer(container);
        }
        List<String> blobUrl = new ArrayList<>();
        List<Banner> banners = new ArrayList<>();
        CompanyPreference companyPreference = getCompanyPreference();
        if (companyPreference == null) {
            throw new NotFoundException(CompanyPreference.class, companyPreference.getCompanyKey());
        }
        List<Banner> bannerListExists = bannerRepository.findByCompanyPreference(companyPreference);

        Long companyKey = getCompKey();
        if (bannerListExists.size() != 0) {
            BlobContainerClient finalContainerClient = containerClient;
            bannerRepository.deleteByCompanyPreferenceId(companyPreference.getId());
            for (int i = 0; i < bannerListExists.size(); i++) {
                BlobClient blobClient =
                        finalContainerClient.getBlobClient(tenant + "/" + companyKey + "/" + companyKey + "-" + i + BANNER_SUFFIX);
                blobClient.delete();
            }

        }
        try {
            BlobContainerClient finalContainerClient = containerClient;
            fileSequence = 0;
            files.forEach(file -> {
                BlobClient blobClient =
                        finalContainerClient.getBlobClient(tenant + "/" + companyKey + "/" + companyKey + "-" + fileSequence + BANNER_SUFFIX);
                try (ByteArrayInputStream dataStream = new ByteArrayInputStream(file.getBytes())) {
                    blobClient.upload(file.getInputStream(), file.getSize(), true);
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
                blobUrl.add(blobClient.getBlobUrl());
                banners.add(Banner.builder()
                        .companyPreference(companyPreference)
                        .companyPreferenceId(companyPreference.getId())
                        .image(blobClient.getBlobName())
                        .pictureSequence(fileSequence)
                        .url(blobClient.getBlobUrl())
                        .idx(bannersObject[fileSequence].getIdx())
                        .filename(bannersObject[fileSequence].getFilename())
                        .imageUrl(bannersObject[fileSequence].getImageUrl())
                        .redirectUrl(bannersObject[fileSequence].getRedirectUrl())
                        .build());
                fileSequence++;
            });
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        companyPreference.setBanners(banners);
        addOrUpdateCompanyPreference(companyPreference);
        return CompanyUploadDTO.builder().bannerURLs(blobUrl).build();
    }

    /**
     * AddOrUpdateBanner
     *
     * @param file
     * @param bannerList
     * @param compKey
     * @return
     * @throws JsonProcessingException
     * @throws NoSuchFieldException
     */
    @Override
    public CompanyUploadDTO addOrUpdateBannerByIndex(MultipartFile file, String bannerList, Long compKey) throws JsonProcessingException, NoSuchFieldException {
        ObjectMapper mapper = new ObjectMapper();
        Banner bannersObject = mapper.readValue(bannerList, Banner.class);
        List<String> blobUrl = new ArrayList<>();
        CompanyPreference companyPreference = getCompanyPreference();
        Long companyKey = getCompKey();
        if (companyPreference == null) {
            throw new NotFoundException(CompanyPreference.class, companyKey);
        }
        if (companyPreference.getBanners().isEmpty()) {
            throw new NotFoundException(Banner.class, companyKey);
        }

        Banner bannerData = bannerRepository.findByIdxAndCompanyPreference(bannersObject.getIdx(), companyPreference);
        String container = publicUrl;
        //Update
        if (bannerData != null) {
            BlobServiceClient blobServiceClient = getBlobServiceClient();
            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(container);
            if (!containerClient.exists()) {
                containerClient = blobServiceClient.createBlobContainer(container);
            }
            try {
                int picSequence = bannersObject.getIdx() - 1;
                BlobClient blobClient = containerClient.getBlobClient(tenant + "/" + companyKey + "/" + companyKey +
                        "-" + picSequence + BANNER_SUFFIX);
                try (ByteArrayInputStream dataStream = new ByteArrayInputStream(file.getBytes())) {
                    blobClient.upload(file.getInputStream(), file.getSize(), true);
                }
                blobUrl.add(blobClient.getBlobUrl());
                bannersObject.setImage(blobClient.getBlobName());
                bannersObject.setPictureSequence(picSequence);
                bannersObject.setUrl(blobClient.getBlobUrl());
                bannersObject.setIdx(bannersObject.getIdx());
                bannersObject.setFilename(bannersObject.getFilename());
                bannersObject.setImageUrl(bannersObject.getImageUrl());
                bannersObject.setRedirectUrl(bannersObject.getRedirectUrl());
                bannerData = BannerMapper.toUpdatedBanner(bannerData, bannersObject);
                bannerRepository.save(bannerData);
                return CompanyUploadDTO.builder().bannerURLs(blobUrl).build();
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        //Add New
        BlobServiceClient blobServiceClient = getBlobServiceClient();
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(container);
        if (!containerClient.exists()) {
            containerClient = blobServiceClient.createBlobContainer(container);
        }
        try {
            int picSequence = bannersObject.getIdx() - 1;
            BlobClient blobClient =
                    containerClient.getBlobClient(tenant + "/" + companyKey + "/" + companyKey + "-" + picSequence + BANNER_SUFFIX);
            try (ByteArrayInputStream dataStream = new ByteArrayInputStream(file.getBytes())) {
                blobClient.upload(file.getInputStream(), file.getSize(), true);
            }
            blobUrl.add(blobClient.getBlobUrl());
            bannersObject.setCompanyPreference(companyPreference);
            bannersObject.setCompanyPreferenceId(companyPreference.getId());
            bannersObject.setImage(blobClient.getBlobName());
            bannersObject.setPictureSequence(picSequence);
            bannersObject.setUrl(blobClient.getBlobUrl());
            bannersObject.setIdx(bannersObject.getIdx());
            bannersObject.setFilename(bannersObject.getFilename());
            bannersObject.setImageUrl(bannersObject.getImageUrl());
            bannersObject.setRedirectUrl(bannersObject.getRedirectUrl());
            bannerRepository.save(bannersObject);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return CompanyUploadDTO.builder().bannerURLs(blobUrl).build();
    }

    /**
     * @param container
     * @param directoryReference
     * @param fileName
     * @return
     */
    // TODO: Check usage
    @Override
    public String getBlobUrl(String container, String directoryReference, String fileName) {
        BlobContainerClient containerClient = getBlobContainer(container);
        String blobUrl = null;
        try {
            BlobClient blobClient =
                    containerClient.getBlobClient(directoryReference + "/" + fileName);
            blobUrl = blobClient.getBlobUrl();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return blobUrl;
    }

    @Override
    public String storeInContainer(MultipartFile file, String container, String directoryReference, String fileName,
                                   Long compKey, Boolean relativeUrl) throws URISyntaxException, StorageException,
            IOException {
        try (ByteArrayInputStream dataStream = new ByteArrayInputStream(file.getBytes())) {
            return uploadInputStream(dataStream, file.getSize(), container, directoryReference, fileName, compKey,
                    relativeUrl);
        }
    }

    // Generic function to uload file in a container and directory
    @Override
    public String uploadInputStream(InputStream inputStream, Long length, String container, String directoryReference
            , String fileName, Long compKey, Boolean relativeUrl) throws URISyntaxException, StorageException,
            UnsupportedEncodingException {
        BlobContainerClient containerClient = getBlobContainer(container);
        if (directoryReference != null && !directoryReference.isEmpty()) {
            createDirectory(directoryReference);
        }
        /* Upload the file to the container */
        String blobUrl = null;
        try {
            BlobClient blobClient =
                    containerClient.getBlobClient(directoryReference + "/" + fileName);
            blobClient.upload(inputStream, length, true);
            blobUrl = blobClient.getBlobUrl();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        if (relativeUrl) {
            blobUrl = URLEncoder.encode(URLDecoder.decode(blobUrl, StandardCharsets.UTF_8.name())
                    .substring(blogService.length() + container.length() + tenant.length() + String.valueOf(compKey).length() + 3), StandardCharsets.UTF_8.name());
            blobUrl = blobUrl.replaceAll("\\+", " ");
        }
        return blobUrl;
    }

    @Override
    public Boolean downloadToOutputStream(OutputStream outputStream, String container, String relativeFileUrl) {
        BlobContainerClient containerClient = getBlobContainer(container);

        /* Upload the file to the container */
        try {
            BlobClient blobClient =
                    !containerClient.getBlobClient(relativeFileUrl).exists() ? null :
                            containerClient.getBlobClient(relativeFileUrl);
            if (blobClient == null) {
                return false;
            }
            blobClient.download(outputStream);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
//            throw e;
        }
        return true;
    }

    @Override
    public String uploadByteArray(byte[] bytes, String container, String directoryReference, String fileName)
            throws URISyntaxException, StorageException {
        BlobContainerClient containerClient = getBlobContainer(container);
        if (directoryReference != null && !directoryReference.isEmpty()) {
            createDirectory(directoryReference);
        }
        String blobUrl = null;
        try {
            BlobClient blobClient =
                    containerClient.getBlobClient(directoryReference + "/" + fileName);
            blobClient.upload(BinaryData.fromBytes(bytes), true);
            blobUrl = blobClient.getBlobUrl();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return blobUrl;
    }

    @Override
    public byte[] downloadToByteArray(String container, String url, String name) {
        byte[] bytes = new byte[0];
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            downloadToOutputStream(os, container, url + "/" + name);
            try (InputStream is = new ByteArrayInputStream(os.toByteArray())) {
                bytes = IOUtils.toByteArray(is);
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return bytes;
    }

    private BlobContainerClient getBlobContainer(String container) {
        /* Create a new BlobServiceClient with a SAS Token */
        BlobServiceClient blobServiceClient = getBlobServiceClient();
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(container);
        if (!containerClient.exists()) {
            containerClient = blobServiceClient.createBlobContainer(container);
        }
        return containerClient;
    }

    private CloudBlobDirectory createDirectory(String directoryReference) throws URISyntaxException, StorageException {
        StorageCredentials creds = new StorageCredentialsSharedAccessSignature(sasToken);
        CloudBlobClient cloudBlobClient = new CloudBlobClient(new URI(blogService), creds);
        CloudBlobContainer blobContainer = cloudBlobClient.getContainerReference(storageContainer);
        if (blobContainer.exists()) {
            return blobContainer.getDirectoryReference(directoryReference);
        }
        return null;
    }

    private CloudBlobDirectory createDirectory(String container, String directoryReference) throws URISyntaxException
            , StorageException {
        StorageCredentials creds = new StorageCredentialsSharedAccessSignature(sasToken);
        CloudBlobClient cloudBlobClient = new CloudBlobClient(new URI(blogService), creds);
        CloudBlobContainer blobContainer = cloudBlobClient.getContainerReference(container);
        if (blobContainer.exists()) {
            return blobContainer.getDirectoryReference(directoryReference);
        }
        return null;
    }

    @Override
    public File getBlob(String container, String url, String name) {

        File tempFile = null;
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            Boolean blobClient = downloadToOutputStream(os, container, url + "/" + name);
            if (!blobClient) {
                return null;
            }
            try (InputStream is = new ByteArrayInputStream(os.toByteArray())) {
                tempFile = File.createTempFile(PREFIX, SUFFIX);
                tempFile.deleteOnExit();
                try (FileOutputStream out = new FileOutputStream(tempFile)) {
                    FileUtils.copyInputStreamToFile(is, tempFile);
                }
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return tempFile;
    }

    @Override
    public void deleteBlob(String container, String fileName, Long compKey, String path) {
        BlobServiceClient blobServiceClient = getBlobServiceClient();
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(container);
        BlobClient blobClient =
                containerClient.getBlobClient("tenant/" + compKey + path + "/" + fileName);
        blobClient.delete();
    }

    @Override
    public MultipartFile convertFileToMultipart(File file) {
        MultipartFile multipartFile = null;
        try {
            FileInputStream input = new FileInputStream(file);
            multipartFile = new MockMultipartFile("file",
                    file.getName(), "text/plain", IOUtils.toByteArray(input));
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return multipartFile;
    }

    @Override
    public List<String> getBlobUrl(String directoryReference, String storageContainer) throws URISyntaxException, StorageException {
        StorageCredentials creds = new StorageCredentialsSharedAccessSignature(sasToken);
        CloudBlobClient cloudBlobClient = new CloudBlobClient(new URI(blogService), creds);
        // Retrieve reference to a previously created container.
        CloudBlobContainer container = cloudBlobClient.getContainerReference(storageContainer);
        CloudBlobDirectory directory  = container.getDirectoryReference(directoryReference);
        Iterable<ListBlobItem> fileBlobs = null;
        List<String> urls = new ArrayList<>();
        for (ListBlobItem blobItem : directory.listBlobs()) {
            if (blobItem instanceof CloudBlob) {
                CloudBlob cloudBlob = (CloudBlob) blobItem;
                urls.add(cloudBlob.getStorageUri().getPrimaryUri().toString());
            }
        }
        return urls;
    }

    private BlobServiceClient getBlobServiceClient() {
        return new BlobServiceClientBuilder()
                .endpoint(blogService)
                .sasToken(sasToken)
                .buildClient();
    }
    @Override
    public void deleteBlob(String container, String fileName , String path) {
        BlobServiceClient blobServiceClient = getBlobServiceClient();
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(container);
        BlobClient blobClient =
                containerClient.getBlobClient( path + "/" + fileName);
        blobClient.delete();
    }
}

