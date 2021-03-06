package com.smartling.connector.hubspot.sdk.rest.api;

import com.smartling.connector.hubspot.sdk.common.ListWrapper;
import com.smartling.connector.hubspot.sdk.email.CloneEmailRequest;
import com.smartling.connector.hubspot.sdk.email.CreateVariationRequest;
import com.smartling.connector.hubspot.sdk.email.EmailDetail;
import feign.Headers;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;

import java.util.Map;

public interface EmailsEntityApi
{
    @RequestLine("GET /marketing-emails/v1/emails?offset={offset}&limit={limit}&order={order}&property=" + EmailDetail.FIELDS)
    ListWrapper<EmailDetail> list(@Param("offset") int offset, @Param("limit") int limit,
                                    @Param("order") String orderBy, @QueryMap Map<String, Object> queryMap);

    @RequestLine("GET /marketing-emails/v1/emails/{email_id}?property=" + EmailDetail.FIELDS)
    EmailDetail getDetail(@Param("email_id") String emailId);

    @RequestLine("GET /marketing-emails/v1/emails/{email_id}/buffer?property=" + EmailDetail.FIELDS)
    EmailDetail getBufferedDetail(@Param("email_id") String emailId);

    @RequestLine("POST /marketing-emails/v1/emails/{email_id}/clone")
    @Headers("Content-Type: application/json")
    EmailDetail clone(@Param("email_id") String email_id, CloneEmailRequest cloneFormRequest);

    @RequestLine("POST /marketing-emails/v1/emails/{email_id}/create-variation")
    @Headers("Content-Type: application/json")
    EmailDetail createVariation(@Param("email_id") String emailId, CreateVariationRequest createVariationRequest);
}
