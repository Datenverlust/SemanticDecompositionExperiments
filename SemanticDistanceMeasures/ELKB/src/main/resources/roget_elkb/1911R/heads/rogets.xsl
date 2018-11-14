<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (C) Johannes Fähndrich - All Rights Reserved.
  ~ Unauthorized copying of this file, via any medium is strictly
  ~ prohibited Proprietary and confidential.
  ~ Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
  ~
  -->

<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://www.w3.org/1999/xhtml">
 
  <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
 
  <xsl:template match="/head">
    <html>
      <head> <title>Head <xsl:value-of select="@number"/>: <xsl:value-of select="@name"/></title> </head>
      <body>
        <h3>Head <xsl:value-of select="@number"/>: <xsl:value-of select="@name"/></h3>
          <xsl:apply-templates select="pos">
          </xsl:apply-templates>
      </body>
    </html>
  </xsl:template>
 
  <xsl:template match="pos">
    <h4><xsl:value-of select="@type"/></h4>
    <xsl:apply-templates select="paragraph">
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="paragraph">
    <p>
      <xsl:apply-templates select="sg"> 
      </xsl:apply-templates>
    <xsl:text>.</xsl:text>
    </p>
  </xsl:template>

  <xsl:template match="sg">
    <xsl:apply-templates select="word"> 
    </xsl:apply-templates>
    <xsl:if test="not(position()=last())">;<br/> </xsl:if>
  </xsl:template>


  <xsl:template match="word">
    <xsl:choose>
      <xsl:when test="@new > 0">
        <font color="red"><xsl:value-of select="."/></font>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="."/>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:if test="not(position()=last())">, </xsl:if> 
  </xsl:template>
 
</xsl:stylesheet>
