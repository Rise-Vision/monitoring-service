package com.risevision.monitoring.service.services.oauth;

/**
 * Created by rodrigopavezi on 1/13/15.
 */
public class TokenInfo {

    private String issued_to;
    private String audience;
    private String user_id;
    private String scope;
    private Integer expires_in;
    private String email;
    private Boolean verified_email;
    private String access_type;

    public TokenInfo() {

    }

    public TokenInfo(String issued_to, String email) {
      this.issued_to = issued_to;
      this.email = email;
    }

    public String getIssued_to() {
        return issued_to;
    }

    public void setIssued_to(String issued_to) {
        this.issued_to = issued_to;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Integer getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(Integer expires_in) {
        this.expires_in = expires_in;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getVerified_email() {
        return verified_email;
    }

    public void setVerified_email(Boolean verified_email) {
        this.verified_email = verified_email;
    }

    public String getAccess_type() {
        return access_type;
    }

    public void setAccess_type(String access_type) {
        this.access_type = access_type;
    }
}
