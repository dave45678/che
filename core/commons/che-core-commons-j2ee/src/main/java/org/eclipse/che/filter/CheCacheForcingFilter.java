/*******************************************************************************
 * Copyright (c) 2012-2017 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.filter;

import com.xemantic.tadedon.servlet.CacheForcingFilter;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Forcing caching for the given URL resource patterns.
 *
 * @author Max Shaposhnik
 */
public class CheCacheForcingFilter extends CacheForcingFilter {

    private Set<Pattern> actionPatterns = new HashSet<>();

    @Override
    public void init(FilterConfig filterConfig) {
        Enumeration<String> names = filterConfig.getInitParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            if (name.startsWith("pattern")) {
                actionPatterns.add(Pattern.compile(filterConfig.getInitParameter(name)));
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        for (Pattern pattern : actionPatterns) {
            if (pattern.matcher(((HttpServletRequest)request).getRequestURI()).matches()) {
                super.doFilter(request, response, chain);
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
