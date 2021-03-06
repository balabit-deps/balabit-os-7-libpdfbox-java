/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.pdfbox.pdmodel.font;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.PDMatrix;

import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;

/**
 * This is implementation of the Type3 Font.
 *
 * @author Ben Litchfield
 * 
 */
public class PDType3Font extends PDSimpleFont
{
    /**
     * Log instance.
     */
    private static final Log LOG = LogFactory.getLog(PDType3Font.class);
    
    /**
     * cached height.
     */
    private float type3Height = 0;

    /**
     * Constructor.
     */
    public PDType3Font()
    {
        super();
        font.setItem( COSName.SUBTYPE, COSName.TYPE3 );
    }

    /**
     * Constructor.
     *
     * @param fontDictionary The font dictionary according to the PDF specification.
     */
    public PDType3Font( COSDictionary fontDictionary )
    {
        super( fontDictionary );
    }

    /**
     * {@inheritDoc}
     */
    public void drawString( String string, int[] codePoints, Graphics g, float fontSize, AffineTransform at, float x, float y ) 
        throws IOException
    {
        LOG.info("Rendering of type3 fonts isn't supported in PDFBox 1.8.x. It will be available in the 2.0 version!");
    }

    /**
     * Set the font matrix for this type3 font.
     *
     * @param matrix The font matrix for this type3 font.
     */
    public void setFontMatrix( PDMatrix matrix )
    {
        font.setItem( COSName.FONT_MATRIX, matrix );
    }
    
    @Override
    public float getFontHeight(byte[] c, int offset, int length) throws IOException
    {
        // first try old behavior until & including 1.8.10
        float height = super.getFontHeight(c, offset, length);
        
        // if that didn't work, use 1/2 of font bounding box
        if (height == 0)
        {
            if (type3Height != 0)
            {
                return type3Height;
            }

            COSBase bboxBase = font.getDictionaryObject(COSName.FONT_BBOX);
            if (bboxBase instanceof COSArray)
            {
                COSArray bboxArray = (COSArray) bboxBase;
                COSBase o1 = bboxArray.getObject(1);
                COSBase o3 = bboxArray.getObject(3);
                if (bboxArray.size() == 4 && o1 instanceof COSNumber && o3 instanceof COSNumber)
                {
                    height = ((COSNumber) o3).floatValue() - ((COSNumber) o1).floatValue();
                    type3Height = height / 2;
                    return type3Height;
                }
            }
        }

        return height;
    }

}
