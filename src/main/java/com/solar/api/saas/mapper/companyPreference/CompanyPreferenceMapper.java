package com.solar.api.saas.mapper.companyPreference;

import com.solar.api.tenant.model.companyPreference.CompanyPreference;

import java.util.List;
import java.util.stream.Collectors;

public class CompanyPreferenceMapper {

    public static CompanyPreference toCompanyPreference(CompanyPreferenceDTO companyPreferenceDTO) {
        return CompanyPreference.builder()
                .id(companyPreferenceDTO.getId())
                .companyCode(companyPreferenceDTO.getCompanyCode())
                .companyKey(companyPreferenceDTO.getCompanyKey())
                .companyName(companyPreferenceDTO.getCompanyName())
                .country(companyPreferenceDTO.getCountry())
                .locId(companyPreferenceDTO.getLocId())
                .countryCode(companyPreferenceDTO.getCountryCode())
                .helpline247(companyPreferenceDTO.getHelpline247())
                .defaultCurrency(companyPreferenceDTO.getDefaultCurrency())
                .pictureRollingDelay(companyPreferenceDTO.getPictureRollingDelay())
                .emergencySupportNumber(companyPreferenceDTO.getEmergencySupportNumber())
                .facebookURL(companyPreferenceDTO.getFacebookURL())
                .twitterURL(companyPreferenceDTO.getTwitterURL())
                .youtubeURL(companyPreferenceDTO.getYoutubeURL())
                .linkedInURL(companyPreferenceDTO.getLinkedInURL())
                .faqURL(companyPreferenceDTO.getFaqURL())
                .logo(companyPreferenceDTO.getLogo())
                .banners(companyPreferenceDTO.getBanner() != null ? BannerMapper.toBanners(companyPreferenceDTO.getBanner()) : null)
                .websiteURL(companyPreferenceDTO.getWebsiteURL())
                .companyPolicy(companyPreferenceDTO.getCompanyPolicy())
                .companyTerms(companyPreferenceDTO.getCompanyTerms())
                .cpConfigure(companyPreferenceDTO.getCpConfigure())
                .landingText(companyPreferenceDTO.getLandingText())
                .landingDescription(companyPreferenceDTO.getLandingDescription())
                .build();
    }

    public static CompanyPreferenceDTO toCompanyPreferenceDTO(CompanyPreference companyPreference) {
        if (companyPreference == null) {
            return null;
        }

        return CompanyPreferenceDTO.builder()
                .id(companyPreference.getId())
                .companyCode(companyPreference.getCompanyCode())
                .companyKey(companyPreference.getCompanyKey())
                .companyName(companyPreference.getCompanyName())
                .locId(companyPreference.getLocId())
                .country(companyPreference.getCountry())
                .countryCode(companyPreference.getCountryCode())
                .helpline247(companyPreference.getHelpline247())
                .defaultCurrency(companyPreference.getDefaultCurrency())
                .pictureRollingDelay(companyPreference.getPictureRollingDelay())
                .emergencySupportNumber(companyPreference.getEmergencySupportNumber())
                .facebookURL(companyPreference.getFacebookURL())
                .twitterURL(companyPreference.getTwitterURL())
                .youtubeURL(companyPreference.getYoutubeURL())
                .linkedInURL(companyPreference.getLinkedInURL())
                .faqURL(companyPreference.getFaqURL())
                .logo(companyPreference.getLogo())
                .banner(BannerMapper.toBannerDTOs(companyPreference.getBanners()))
                .websiteURL(companyPreference.getWebsiteURL())
                .companyPolicy(companyPreference.getCompanyPolicy())
                .cpConfigure(companyPreference.getCpConfigure())
                .landingText(companyPreference.getLandingText())
                .landingDescription(companyPreference.getLandingDescription())
                .build();
    }

    public static CompanyPreference toUpdateCompanyPreference(CompanyPreference companyPreference,
                                                              CompanyPreference companyPreferenceUpdate) {
        companyPreference.setCompanyCode(companyPreferenceUpdate.getCompanyCode() == null ?
                companyPreference.getCompanyCode() : companyPreferenceUpdate.getCompanyCode());
        companyPreference.setCompanyKey(companyPreferenceUpdate.getCompanyKey() == null ?
                companyPreference.getCompanyKey() : companyPreferenceUpdate.getCompanyKey());
        companyPreference.setCompanyName(companyPreferenceUpdate.getCompanyName() == null ?
                companyPreference.getCompanyName() : companyPreferenceUpdate.getCompanyName());
        companyPreference.setCountry(companyPreferenceUpdate.getCountry() == null ? companyPreference.getCountry() :
                companyPreferenceUpdate.getCountry());
        companyPreference.setCountryCode(companyPreferenceUpdate.getCountryCode() == null ?
                companyPreference.getCountryCode() : companyPreferenceUpdate.getCountryCode());
        companyPreference.setHelpline247(companyPreferenceUpdate.getHelpline247() == null ?
                companyPreference.getHelpline247() : companyPreferenceUpdate.getHelpline247());
        companyPreference.setDefaultCurrency(companyPreferenceUpdate.getDefaultCurrency() == null ?
                companyPreference.getDefaultCurrency() : companyPreferenceUpdate.getDefaultCurrency());
        companyPreference.setPictureRollingDelay(companyPreferenceUpdate.getPictureRollingDelay() == null ?
                companyPreference.getPictureRollingDelay() : companyPreferenceUpdate.getPictureRollingDelay());
        companyPreference.setEmergencySupportNumber(companyPreferenceUpdate.getEmergencySupportNumber() == null ?
                companyPreference.getEmergencySupportNumber() : companyPreferenceUpdate.getEmergencySupportNumber());
        companyPreference.setFacebookURL(companyPreferenceUpdate.getFacebookURL() == null ?
                companyPreference.getFacebookURL() : companyPreferenceUpdate.getFacebookURL());
        companyPreference.setTwitterURL(companyPreferenceUpdate.getTwitterURL() == null ?
                companyPreference.getTwitterURL() : companyPreferenceUpdate.getTwitterURL());
        companyPreference.setYoutubeURL(companyPreferenceUpdate.getYoutubeURL() == null ?
                companyPreference.getYoutubeURL() : companyPreferenceUpdate.getYoutubeURL());
        companyPreference.setLinkedInURL(companyPreferenceUpdate.getLinkedInURL() == null ?
                companyPreference.getLinkedInURL() : companyPreferenceUpdate.getLinkedInURL());
        companyPreference.setFaqURL(companyPreferenceUpdate.getFaqURL() == null ? companyPreference.getFaqURL() :
                companyPreferenceUpdate.getFaqURL());
        companyPreference.setLogo(companyPreferenceUpdate.getLogo() == null ? companyPreference.getLogo() :
                companyPreferenceUpdate.getLogo());
        companyPreference.setWebsiteURL(companyPreferenceUpdate.getWebsiteURL() == null ? companyPreference.getWebsiteURL() :
                companyPreferenceUpdate.getWebsiteURL());
        companyPreference.setCompanyPolicy(companyPreferenceUpdate.getCompanyPolicy() == null ? companyPreference.getCompanyPolicy() :
                companyPreferenceUpdate.getCompanyPolicy());
        companyPreference.setCompanyTerms(companyPreferenceUpdate.getCompanyTerms() == null ? companyPreference.getCompanyTerms() :
                companyPreferenceUpdate.getCompanyTerms());
        companyPreference.setCpConfigure(companyPreferenceUpdate.getCpConfigure() == null ? companyPreference.getCpConfigure() :
                companyPreferenceUpdate.getCpConfigure());
        companyPreference.setLandingText(companyPreferenceUpdate.getLandingText() == null ? companyPreference.getLandingText() :
                companyPreferenceUpdate.getLandingText());
        companyPreference.setLandingDescription(companyPreferenceUpdate.getLandingDescription() == null ? companyPreference.getLandingDescription() :
                companyPreferenceUpdate.getLandingDescription());
        return companyPreference;
    }

    /**
     * @param companyPreferenceDTOs
     * @return
     */
    public static List<CompanyPreference> toCompanyPreferences(List<CompanyPreferenceDTO> companyPreferenceDTOs) {
        return companyPreferenceDTOs.stream().map(p -> toCompanyPreference(p)).collect(Collectors.toList());
    }

    /**
     * @param companyPreferences
     * @return
     */
    public static List<CompanyPreferenceDTO> toCompanyPreferenceDTOs(List<CompanyPreference> companyPreferences) {
        return companyPreferences.stream().map(c -> toCompanyPreferenceDTO(c)).collect(Collectors.toList());
    }
}
