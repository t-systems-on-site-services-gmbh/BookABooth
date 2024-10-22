package de.tsystems.onsite.bookabooth.config;

import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tech.jhipster.config.JHipsterConstants;
import tech.jhipster.config.JHipsterProperties;

@Configuration
@Profile({ JHipsterConstants.SPRING_PROFILE_PRODUCTION, JHipsterConstants.SPRING_PROFILE_DEVELOPMENT })
public class StaticResourcesWebConfiguration implements WebMvcConfigurer {

    protected static final String[] RESOURCE_LOCATIONS = { "classpath:/static/", "classpath:/static/content/", "classpath:/static/i18n/" };
    protected static final String[] RESOURCE_PATHS = {
        "/*.js",
        "/*.css",
        "/*.svg",
        "/*.png",
        "*.ico",
        "/content/**",
        "/i18n/*",
        "/uploads/**",
    };

    private final JHipsterProperties jhipsterProperties;
    private final ApplicationProperties applicationProperties;

    public StaticResourcesWebConfiguration(JHipsterProperties jHipsterProperties, ApplicationProperties applicationProperties) {
        this.jhipsterProperties = jHipsterProperties;
        this.applicationProperties = applicationProperties;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        ResourceHandlerRegistration resourceHandlerRegistration = appendResourceHandler(registry);
        initializeResourceHandler(resourceHandlerRegistration);
    }

    protected ResourceHandlerRegistration appendResourceHandler(ResourceHandlerRegistry registry) {
        return registry.addResourceHandler(RESOURCE_PATHS);
    }

    protected void initializeResourceHandler(ResourceHandlerRegistration resourceHandlerRegistration) {
        String[] locations = applicationProperties.getUploadFolder() != null
            ? new String[] { "file:" + applicationProperties.getUploadFolder() }
            : RESOURCE_LOCATIONS;
        resourceHandlerRegistration.addResourceLocations(locations).setCacheControl(getCacheControl());
    }

    protected CacheControl getCacheControl() {
        return CacheControl.maxAge(getJHipsterHttpCacheProperty(), TimeUnit.DAYS).cachePublic();
    }

    private int getJHipsterHttpCacheProperty() {
        return jhipsterProperties.getHttp().getCache().getTimeToLiveInDays();
    }
}
