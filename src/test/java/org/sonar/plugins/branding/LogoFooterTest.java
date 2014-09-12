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

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.config.PropertyDefinitions;
import org.sonar.api.config.Settings;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class LogoFooterTest {

  private Settings settings;
  private LogoFooter footer;

  @Before
  public void setUp() {
    settings = new Settings(new PropertyDefinitions(BrandingPlugin.class));
    footer = new LogoFooter(settings);
  }

  @Test
  public void shouldNotCreateFooterIfNoImage() {
    assertThat(footer.getHtml(), is(""));
  }

  @Test
  public void shouldCreateFooterDefaultLocation() {
    settings.setProperty(BrandingPlugin.IMAGE_PROPERTY, "http://example.org/logo.png");
    String html = footer.getHtml();
    assertThat(html, containsString("$j(\"#error\")"));
    assertThat(html, containsString("http://example.org/logo.png"));
  }

  @Test
  public void shouldCreateFooterTopLocation() {
    settings.setProperty(BrandingPlugin.IMAGE_PROPERTY, "http://example.org/logo.png");
    settings.setProperty(BrandingPlugin.LOGO_LOCATION_PROPERTY, "TOP");
    assertThat(footer.getHtml(), containsString("$j(\"#error\")"));
  }

  @Test
  public void shouldCreateFooterMenuLocation() {
    settings.setProperty(BrandingPlugin.IMAGE_PROPERTY, "http://example.org/logo.png");
    settings.setProperty(BrandingPlugin.LOGO_LOCATION_PROPERTY, "MENU");
    assertThat(footer.getHtml(), containsString("$j(\"[title='Embrace Quality']\")"));
  }

  @Test
  public void shouldCreateFooterInvalidLocation() {
    settings.setProperty(BrandingPlugin.IMAGE_PROPERTY, "http://example.org/logo.png");
    settings.setProperty(BrandingPlugin.LOGO_LOCATION_PROPERTY, "foo");
    System.out.println(footer.getHtml());
    assertThat(footer.getHtml(), containsString("$j(\"#error\")"));
  }

  @Test
  public void shouldCreateCompanyLogoWithLink() {
    settings.setProperty(BrandingPlugin.IMAGE_PROPERTY, "http://images.example.org/logo.png");
    settings.setProperty(BrandingPlugin.LINK_PROPERTY, "http://example.org/");
    String html = footer.getHtml();
    assertThat(html, containsString("companyUrl"));
    assertThat(html, containsString("http://example.org/"));
  }

  @Test
  public void shouldSetImageHeight() {
    settings.setProperty(BrandingPlugin.IMAGE_PROPERTY, "http://images.example.org/logo.png");
    settings.setProperty(BrandingPlugin.IMAGE_HEIGHT, 20);
    String html = footer.getHtml();
    assertThat(html, containsString("height"));
    assertThat(html, containsString("20"));
  }

  @Test
  public void shouldSetImageWieght() {
    settings.setProperty(BrandingPlugin.IMAGE_PROPERTY, "http://images.example.org/logo.png");
    settings.setProperty(BrandingPlugin.IMAGE_WIDTH, 80);
    String html = footer.getHtml();
    assertThat(html, containsString("width"));
    assertThat(html, containsString("80"));
  }

  @Test(expected = NumberFormatException.class)
  public void shouldThrowConversionExceptionIfWidthIsInvalid() {
    settings.setProperty(BrandingPlugin.IMAGE_PROPERTY, "http://images.example.org/logo.png");
    settings.setProperty(BrandingPlugin.IMAGE_WIDTH, "invalid");
    footer.getHtml();
  }

  @Test(expected = NumberFormatException.class)
  public void shouldThrowConversionExceptionIfHeightIsInvalid() {
    settings.setProperty(BrandingPlugin.IMAGE_PROPERTY, "http://images.example.org/logo.png");
    settings.setProperty(BrandingPlugin.IMAGE_HEIGHT, "invalid");
    footer.getHtml();
  }

}
