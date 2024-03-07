package com.solar.api.tenant.mapper.tiles.entityDetail;


import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor

public class EntityDetailTile {

    private Long id;
    private Long entity;
    private String uri;
    private String fileName;
    private  Long acctId;

    public EntityDetailTile(Long id, Long entity, String uri, String fileName) {
        this.id = id;
        this.entity = entity;
        this.uri = uri;
        this.fileName = fileName;
    }

    public EntityDetailTile(Long id, Long entity, String uri, String fileName, Long acctId) {
        this.id = id;
        this.entity = entity;
        this.uri = uri;
        this.fileName = fileName;
        this.acctId = acctId;
    }

}
