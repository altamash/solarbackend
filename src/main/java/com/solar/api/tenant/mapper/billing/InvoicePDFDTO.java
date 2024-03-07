package com.solar.api.tenant.mapper.billing;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class InvoicePDFDTO {

    private String status;
    private String download_url;
    private String template_id;
    private String transaction_ref;

}
