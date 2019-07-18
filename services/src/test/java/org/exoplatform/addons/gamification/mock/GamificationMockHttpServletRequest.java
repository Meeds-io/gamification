package org.exoplatform.addons.gamification.mock;

import org.exoplatform.addons.gamification.service.configuration.BadgeService;
import org.exoplatform.addons.gamification.service.dto.configuration.BadgeDTO;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.test.mock.MockHttpServletRequest;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
public class GamificationMockHttpServletRequest extends MockHttpServletRequest {

    private String remoteUser;

    /**
     * Instantiates a new mock http servlet request.
     *
     * @param url     the url
     * @param data    the data
     * @param length  the length
     * @param method  the method
     * @param headers the headers
     */
    public GamificationMockHttpServletRequest(String url, InputStream data, int length, String method, Map<String, List<String>> headers, String remoteUser) {
        super(url, data, length, method, headers);
        this.remoteUser = remoteUser;
    }


    /**
     * {@inheritDoc}
     */
    public String getServerName() {
        try {
            return super.getServerName();
        } catch (Exception e) {

        }
        return "localhost";
    }

    /**
     * {@inheritDoc}
     */
    public int getServerPort() {
        try {
            return super.getServerPort();
        } catch (Exception e) {

        }
        return 8080;
    }

    public String getRemoteUser() {
        return this.remoteUser != null ? this.remoteUser : super.getRemoteUser();
    }







}
