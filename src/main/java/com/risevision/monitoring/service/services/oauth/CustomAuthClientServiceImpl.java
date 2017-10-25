package com.risevision.monitoring.service.services.oauth;

import com.risevision.common.security.customauth.TokenManager;
import com.risevision.common.security.customauth.TokenManager.TokenClaims;
import com.risevision.common.security.customauth.TokenManager.TokenStatus;

public class CustomAuthClientServiceImpl implements CustomAuthClientService {

	@Override
	public boolean isCustomAuthToken(String token) {
		return TokenManager.getInstance().isValidJWTFormat(token);
	}

	@Override
	public TokenInfo lookupTokenInfo(String token) {
		TokenInfo tokenInfo = null;
		TokenClaims claims = TokenManager.getInstance().getTokenClaims(token);
		
		if (claims.getStatus() == TokenStatus.VALID) {
			tokenInfo = new TokenInfo("Rise Vision JWT", claims.getSubject());
		}

		return tokenInfo;
	}
}
