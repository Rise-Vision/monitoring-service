package com.risevision.monitoring.service.services.oauth;

public interface CustomAuthClientService {
	public boolean isCustomAuthToken(String token);
  public TokenInfo lookupTokenInfo(String token);
}
