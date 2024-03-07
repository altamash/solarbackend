package com.solar.api.saas.mapper.companyPreference;

import com.solar.api.tenant.model.companyPreference.Banner;

import java.util.List;
import java.util.stream.Collectors;

public class BannerMapper {

    public static Banner toBanner(BannerDTO bannerDTO) {
        return Banner.builder()
                .id(bannerDTO.getId())
                .image(bannerDTO.getImage())
                .pictureSequence(bannerDTO.getPictureSequence())
                .url(bannerDTO.getUrl())
                .filename(bannerDTO.getFilename())
                .idx(bannerDTO.getIdx())
                .redirectUrl(bannerDTO.getRedirectUrl())
                .imageUrl(bannerDTO.getImageUrl())
                .build();
    }

    public static BannerDTO toBannerDTO(Banner banner) {
        if (banner == null) {
            return null;
        }
        return BannerDTO.builder()
                .id(banner.getId())
                .image(banner.getImage())
                .pictureSequence(banner.getPictureSequence())
                .url(banner.getUrl())
                .filename(banner.getFilename())
                .idx(banner.getIdx())
                .redirectUrl(banner.getRedirectUrl())
                .imageUrl(banner.getImageUrl())
                .build();
    }

    public static Banner toUpdatedBanner(Banner banner, Banner bannerUpdate) {
        banner.setImage(bannerUpdate.getImage() == null ? banner.getImage() : bannerUpdate.getImage());
        banner.setPictureSequence(bannerUpdate.getPictureSequence() == null ? banner.getPictureSequence() :
                bannerUpdate.getPictureSequence());
        banner.setUrl(bannerUpdate.getUrl() == null ? banner.getUrl() : bannerUpdate.getUrl());
        banner.setFilename(bannerUpdate.getFilename() == null ? banner.getFilename() : bannerUpdate.getFilename());
        banner.setIdx(bannerUpdate.getIdx() == null ? banner.getIdx() : bannerUpdate.getIdx());
        banner.setRedirectUrl(bannerUpdate.getRedirectUrl() == null ? banner.getRedirectUrl() :
                bannerUpdate.getRedirectUrl());
        banner.setImageUrl(bannerUpdate.getImageUrl() == null ? banner.getImageUrl() : bannerUpdate.getImageUrl());

        return banner;
    }

    public static List<Banner> toBanners(List<BannerDTO> bannerDTOs) {
        return bannerDTOs.stream().map(cc -> toBanner(cc)).collect(Collectors.toList());
    }

    public static List<BannerDTO> toBannerDTOs(List<Banner> banners) {
        return banners.stream().map(cc -> toBannerDTO(cc)).collect(Collectors.toList());
    }
}
