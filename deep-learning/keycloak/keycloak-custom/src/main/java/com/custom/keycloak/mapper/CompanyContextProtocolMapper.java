package com.custom.keycloak.mapper;

import org.keycloak.models.ClientSessionContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.ProtocolMapperModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.protocol.oidc.mappers.AbstractOIDCProtocolMapper;
import org.keycloak.protocol.oidc.mappers.OIDCAccessTokenMapper;
import org.keycloak.protocol.oidc.mappers.OIDCAttributeMapperHelper;
import org.keycloak.protocol.oidc.mappers.OIDCIDTokenMapper;
import org.keycloak.protocol.oidc.mappers.UserInfoTokenMapper;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.IDToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Custom Protocol Mapper: Company Context & Permissions
 * Injeta no token JWT claims calculadas com:
 *  - tenant (tenant_id, tier, features)
 *  - permissions (lista plana de permissões derivadas de roles)
 *  - is_internal_user (calculado a partir do domínio de e-mail)
 */
public class CompanyContextProtocolMapper extends AbstractOIDCProtocolMapper
        implements OIDCAccessTokenMapper, OIDCIDTokenMapper, UserInfoTokenMapper {

    public static final String PROVIDER_ID = "company-context-protocol-mapper";
    public static final String COMPANY_CLAIM_NAME = "company_context";

    public static final String CONF_DEFAULT_TIER = "default_tier";
    public static final String CONF_INTERNAL_EMAIL_DOMAIN = "internal_email_domain";

    private static final List<ProviderConfigProperty> CONFIG_PROPERTIES = new ArrayList<>();

    static {
        // Configuração de Tier Padrão (Ex: FREE, PRO, ENTERPRISE)
        ProviderConfigProperty tierProperty = new ProviderConfigProperty();
        tierProperty.setName(CONF_DEFAULT_TIER);
        tierProperty.setLabel("Default Tier");
        tierProperty.setType(ProviderConfigProperty.STRING_TYPE);
        tierProperty.setDefaultValue("FREE");
        tierProperty.setHelpText("Nível de assinatura padrão caso o usuário não possua o atributo 'tier'");
        CONFIG_PROPERTIES.add(tierProperty);

        // Configuração de Domínio Interno para is_internal_user
        ProviderConfigProperty internalDomainProperty = new ProviderConfigProperty();
        internalDomainProperty.setName(CONF_INTERNAL_EMAIL_DOMAIN);
        internalDomainProperty.setLabel("Internal Email Domain");
        internalDomainProperty.setType(ProviderConfigProperty.STRING_TYPE);
        internalDomainProperty.setDefaultValue("@company.com");
        internalDomainProperty.setHelpText("Sufixo de e-mail que identifica usuários corporativos internos");
        CONFIG_PROPERTIES.add(internalDomainProperty);

        // Helper para controles padrão de inclusão em Access Token, ID Token e UserInfo
        OIDCAttributeMapperHelper.addIncludeInTokensConfig(CONFIG_PROPERTIES, CompanyContextProtocolMapper.class);
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getDisplayCategory() {
        return TOKEN_MAPPER_CATEGORY;
    }

    @Override
    public String getDisplayType() {
        return "Company Context & Permissions Mapper";
    }

    @Override
    public String getHelpText() {
        return "Injeta metadados de tenant, permissões consolidadas e indicador de usuário interno no token JWT.";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return CONFIG_PROPERTIES;
    }

    @Override
    protected void setClaim(IDToken token, ProtocolMapperModel mappingModel,
                            UserSessionModel userSession, KeycloakSession keycloakSession,
                            ClientSessionContext clientSessionCtx) {

        UserModel user = userSession.getUser();

        // 1. Obter tenant e tier
        String tenantId = user.getFirstAttribute("tenant_id");
        if (tenantId == null || tenantId.isBlank()) {
            tenantId = "default-tenant";
        }

        String configuredDefaultTier = mappingModel.getConfig().getOrDefault(CONF_DEFAULT_TIER, "FREE");
        String tier = user.getFirstAttribute("tier");
        if (tier == null || tier.isBlank()) {
            tier = configuredDefaultTier;
        }

        // Features liberadas com base no tier
        List<String> features = switch (tier.toUpperCase()) {
            case "ENTERPRISE" -> List.of("basic_access", "analytics", "audit_logs", "sso_integration", "priority_support");
            case "PRO" -> List.of("basic_access", "analytics", "audit_logs");
            default -> List.of("basic_access");
        };

        Map<String, Object> tenantContext = new HashMap<>();
        tenantContext.put("tenant_id", tenantId);
        tenantContext.put("tier", tier.toUpperCase());
        tenantContext.put("features", features);

        // 2. Calcular lista plana de permissões a partir das roles do usuário
        Set<RoleModel> realmRoles = user.getRealmRoleMappingsStream().collect(Collectors.toSet());
        List<String> permissions = new ArrayList<>();

        for (RoleModel role : realmRoles) {
            String roleName = role.getName();
            // Mapeia roles para permissões de negócio limpas
            if ("admin".equalsIgnoreCase(roleName)) {
                permissions.addAll(List.of("users:read", "users:write", "settings:admin", "billing:admin"));
            } else if ("editor".equalsIgnoreCase(roleName)) {
                permissions.addAll(List.of("content:read", "content:write", "reports:read"));
            } else {
                permissions.add("role:" + roleName);
            }
        }

        // 3. Checar se é usuário interno
        String internalDomain = mappingModel.getConfig().getOrDefault(CONF_INTERNAL_EMAIL_DOMAIN, "@company.com");
        String email = user.getEmail();
        boolean isInternalUser = email != null && email.toLowerCase().endsWith(internalDomain.toLowerCase());

        // 4. Montar a claim consolidada 'company_context'
        Map<String, Object> companyClaim = new HashMap<>();
        companyClaim.put("tenant", tenantContext);
        companyClaim.put("permissions", permissions.stream().distinct().toList());
        companyClaim.put("is_internal_user", isInternalUser);

        // 5. Injeta no token
        token.getOtherClaims().put(COMPANY_CLAIM_NAME, companyClaim);
    }
}
