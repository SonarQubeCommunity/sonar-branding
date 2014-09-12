/*
 * Sonar Branding Plugin
 * Copyright (C) 2011 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.branding;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.config.Settings;
import org.sonar.api.web.Footer;

public class LogoFooter implements Footer {

  private static final Logger LOGGER = LoggerFactory.getLogger(LogoFooter.class);
  private final Settings settings;

  public LogoFooter(Settings settings) {
    this.settings = settings;
  }

  private String getImageUrl() {
    return settings.getString(BrandingPlugin.IMAGE_PROPERTY);
  }

  private Integer getImageWidth() {
    return settings.getInt(BrandingPlugin.IMAGE_WIDTH);
  }

  private Integer getImageHeight() {
    return settings.getInt(BrandingPlugin.IMAGE_HEIGHT);
  }

  private String getLinkUrl() {
    return settings.getString(BrandingPlugin.LINK_PROPERTY);
  }

  private LogoLocation getLogoLocation() {
    String locationStr = settings.getString(BrandingPlugin.LOGO_LOCATION_PROPERTY);
    LogoLocation location;
    try {
      location = LogoLocation.valueOf(locationStr);
    } catch (IllegalArgumentException e) { // NOSONAR
      LOGGER.warn("Invalid value for property " + BrandingPlugin.LOGO_LOCATION_PROPERTY + ". Using TOP as default.");
      location = LogoLocation.TOP;
    }
    return location;
  }

  public String getHtml() {
    String imageUrl = getImageUrl();
    Integer imageHeight = getImageHeight();
    Integer imageWidth = getImageWidth();

    if (StringUtils.isEmpty(imageUrl)) {
      return "";
    }

    StringBuilder sb = new StringBuilder();

    sb.append("<script>\n");
    sb.append("    $j(document).ready(function() {\n");

    sb.append("        var companyLogo = $j('<img>');\n");
    sb.append("        companyLogo.attr('src', '").append(imageUrl).append("');\n");
    if (imageHeight > 0) {
      sb.append("        companyLogo.attr('height', ").append(imageHeight).append(");\n");
    }
    if (imageWidth > 0) {
      sb.append("        companyLogo.attr('width', ").append(imageWidth).append(");\n");
    }
    sb.append("        companyLogo.attr('alt', '');\n");
    sb.append("        companyLogo.attr('title', '');\n");
    if (LogoLocation.MENU.equals(getLogoLocation())) {
      // Add a margin to separate the 2 logos - SONARPLUGINS-3022
      sb.append("        companyLogo.attr('style', 'margin-top: 10px');\n");
    }

    String linkUrl = getLinkUrl();
    if (!StringUtils.isEmpty(linkUrl)) {
      sb.append("        var companyUrl = $j('<a>');\n");
      sb.append("        companyUrl.attr('href', '").append(linkUrl).append("');\n");
      sb.append("        companyUrl.append(companyLogo);\n");
      sb.append("        companyLogo = companyUrl;\n");
    }

    switch (getLogoLocation()) {
      case TOP:
        sb.append("        var sonarContent = $j(\"#error\").first().parent();\n");
        sb.append("        sonarContent.prepend(companyLogo);\n");
        break;
      case MENU:
        sb.append("        var sonarLogo = $j(\"[title='Embrace Quality']\").first();\n");
        sb.append("        var center = sonarLogo.parent().parent();\n");
        sb.append("        center.append(companyLogo);\n");
        break;
      default:
        LOGGER.warn("Location no supported");
    }
    sb.append("    });\n");
    sb.append("</script>\n");
    return sb.toString();
  }

}
